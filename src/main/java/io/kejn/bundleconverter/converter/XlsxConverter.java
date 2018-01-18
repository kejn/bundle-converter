package io.kejn.bundleconverter.converter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.kejn.bundleconverter.Bundle;
import io.kejn.bundleconverter.BundleGroup;
import io.kejn.bundleconverter.Bundles;
import io.kejn.bundleconverter.Language;

public class XlsxConverter {

    private static final int COLUMN_WIDTH = 11000;
    private static final short FONT_SIZE = 10;
    private static final String FONT_NAME = "Arial";

    public static final int HEADER_ROW = 0;
    public static final int CONTENT_FIRST_ROW = 1;

    public static final int KEY_COLUMN = 0;
    public static final int DEFAULT_COLUMN = 1;

    public static final String KEY_LABEL = "Key";

    /*
     * API.
     */

    public Workbook toXlsx(BundleGroup... bundleGroups) {
        Objects.requireNonNull(bundleGroups);
	return toXlsx(Arrays.asList(bundleGroups));
    }

    public Workbook toXlsx(List<BundleGroup> bundleGroups) {
        Objects.requireNonNull(bundleGroups);

	Workbook workbook = new XSSFWorkbook();
	for (BundleGroup group : bundleGroups) {
	    Objects.requireNonNull(group, "The BundleGroup list contain null value");
	    createSheet(workbook, group);
	}
	return workbook;
    }

    public void createSheet(Workbook workbook, BundleGroup group) {
        Objects.requireNonNull(workbook);
        Objects.requireNonNull(group);

        Sheet sheet = workbook.createSheet(group.getName());

	createHeader(sheet, group);
	createTranslations(sheet, group);

	setAutoFilter(sheet, group);
	sheet.createFreezePane(KEY_COLUMN, DEFAULT_COLUMN);
	setWidthForAllColumns(sheet, group);
    }

    public void createHeader(Sheet sheet, BundleGroup group) {
        Objects.requireNonNull(sheet);
        Objects.requireNonNull(group);

        CellStyle cellStyle = getHeaderCellStyle(sheet.getWorkbook());
	String defaultValue = Language.DEFAULT.getDisplayLanguage();

	createRow(group, sheet, HEADER_ROW, cellStyle, KEY_LABEL, defaultValue, language -> {
	    return language.getDisplayLanguage();
	});
    }

    public void createTranslations(Sheet sheet, BundleGroup group) {
        Objects.requireNonNull(sheet);
        Objects.requireNonNull(group);

	CellStyle cellStyle = getContentCellStyle(sheet.getWorkbook());

	int rowIndex = CONTENT_FIRST_ROW;
	for (String key : group.stringPropertyNames()) {
	    String defaultValue = group.getProperty(key, Language.DEFAULT);
	    createRow(group, sheet, rowIndex, cellStyle, key, defaultValue, language -> {
		return group.getProperty(key, language);
	    });
	    ++rowIndex;
	}
    }

    public List<BundleGroup> toBundleGroupList(Workbook workbook, File outputDirectory) {
        Objects.requireNonNull(workbook);
        Objects.requireNonNull(outputDirectory);

	List<BundleGroup> groups = new ArrayList<>();

	Iterator<Sheet> iterator = workbook.sheetIterator();
	while (iterator.hasNext()) {
	    groups.add(toBundleGroup(iterator.next(), outputDirectory));
	}
	return groups;
    }

    public BundleGroup toBundleGroup(Sheet sheet, File outputDirectory) {
        Objects.requireNonNull(sheet);
        Objects.requireNonNull(outputDirectory);

        Row firstRow = sheet.rowIterator().next();
	Iterator<Cell> cellIterator = firstRow.cellIterator();
	// skip the column with keys
	cellIterator.next();

	Bundle defaultBundle = null;
	List<Bundle> bundleVariants = new ArrayList<>();

	for (int index = DEFAULT_COLUMN; cellIterator.hasNext(); ++index) {
            String stringCellValue = cellIterator.next().getStringCellValue();
            Language language = Language.forDisplayLanguage(stringCellValue);
            if (language == null) {
                throw new IllegalStateException("Sheet contains unknown language: [" + stringCellValue
                        + "]. Languages supported by API: " + Language.listSupportedLanguages());
            }

	    Properties properties = sheetColumnToProperties(sheet, index);
	    String bundlePath = Bundles.createFileName(outputDirectory, sheet.getSheetName(), language);
            Bundle bundle = Bundle.newNotExistingBundle(bundlePath, properties);
	    if (bundle.isDefaultBundle()) {
		defaultBundle = bundle;
	    } else {
		bundleVariants.add(bundle);
	    }
	}
	if (defaultBundle == null) {
	    throw new IllegalStateException("Sheet is missing the Default translation.");
	}
	return new BundleGroup(defaultBundle, bundleVariants);
    }

    public Properties sheetColumnToProperties(Sheet sheet, int indexOfColumnwithTranslation) {
        Objects.requireNonNull(sheet);

	Iterator<Row> rowIterator = sheet.rowIterator();
	// skip the table header
	rowIterator.next();

	Properties propertiesToGenerate = new Properties();

	while (rowIterator.hasNext()) {
	    Row row = (Row) rowIterator.next();
	    propertiesToGenerate.setProperty(row.getCell(KEY_COLUMN).getStringCellValue(),
		    row.getCell(indexOfColumnwithTranslation).getStringCellValue());
	}
	return propertiesToGenerate;
    }

    /*
     * Private methods.
     */

    private void createRow(BundleGroup group, Sheet sheet, int rowIndex, CellStyle cellStyle, String key,
	    String defaultValue, Function<Language, String> valueConverter) {
	Row row = sheet.createRow(rowIndex);

	createCellWithStyle(row, KEY_COLUMN, cellStyle, key);
	createCellWithStyle(row, DEFAULT_COLUMN, cellStyle, defaultValue);

	int colIndex = DEFAULT_COLUMN + 1;
	for (Language language : group.supportedLanguages()) {
	    if (Language.DEFAULT.equals(language)) {
		continue;
	    }
	    createCellWithStyle(row, colIndex, cellStyle, valueConverter.apply(language));
	    ++colIndex;
	}
    }

    private void createCellWithStyle(Row row, int cellIndex, CellStyle cellStyle, String value) {
	Cell cell = row.createCell(cellIndex);
	cell.setCellStyle(cellStyle);
	cell.setCellValue(value);
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
	Font font = createFont(workbook);
	font.setBold(true);

	CellStyle cellStyle = getContentCellStyle(workbook);
	cellStyle.setFont(font);
	return cellStyle;
    }

    private CellStyle getContentCellStyle(Workbook workbook) {
	CellStyle cellStyle = workbook.createCellStyle();
	cellStyle.setFont(createFont(workbook));
	cellStyle.setWrapText(true);
	cellStyle.setAlignment(HorizontalAlignment.LEFT);
	cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
	return cellStyle;
    }

    private Font createFont(Workbook workbook) {
	Font font = workbook.createFont();
	font.setFontName(FONT_NAME);
	font.setFontHeightInPoints(FONT_SIZE);
	return font;
    }

    private void setAutoFilter(Sheet sheet, BundleGroup group) {
	int maxRows = group.getDefaultBundle().getProperties().size();
	int maxCols = group.supportedLanguages().size();
	sheet.setAutoFilter(new CellRangeAddress(HEADER_ROW, maxRows, KEY_COLUMN, maxCols));
    }

    private void setWidthForAllColumns(Sheet sheet, BundleGroup group) {
	for (int colIndex = 0; colIndex <= group.size(); ++colIndex) {
	    sheet.setColumnWidth(colIndex, COLUMN_WIDTH);
	}
    }

}
