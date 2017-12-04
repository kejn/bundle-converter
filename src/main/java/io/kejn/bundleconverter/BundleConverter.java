package io.kejn.bundleconverter;

import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BundleConverter {

    private static final short FONT_SIZE = 10;
    private static final String FONT_NAME = "Arial";
    
    public static final int HEADER_ROW = 0;
    private static final int CONTENT_FIRST_ROW = 1;

    public static final int KEY_COLUMN = 0;
    public static final int DEFAULT_COLUMN = 1;

    public static final String KEY_LABEL = "Key";

    public Workbook toXlsx(BundleGroup group) {
	Workbook workbook = new XSSFWorkbook();
	Sheet sheet = workbook.createSheet(group.getName());

	createHeader(sheet, group);
	createTranslations(sheet, group);

	return workbook;
    }

    private void createHeader(Sheet sheet, BundleGroup group) {
	CellStyle cellStyle = getHeaderCellStyle(sheet.getWorkbook());
	String defaultValue = Language.DEFAULT.getDisplayLanguage();

	createRow(group, sheet, HEADER_ROW, cellStyle, KEY_LABEL, defaultValue, language -> {
	    return language.getDisplayLanguage();
	});
    }

    private void createTranslations(Sheet sheet, BundleGroup group) {
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
	cell.getStringCellValue();
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

}
