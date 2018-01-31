package io.kejn.bundleconverter.converter;

import io.kejn.bundleconverter.Bundle;
import io.kejn.bundleconverter.BundleGroup;
import io.kejn.bundleconverter.Bundles;
import io.kejn.bundleconverter.Language;

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

/**
 * Supports the following conversions:
 * <ul>
 * <li>{@link BundleGroup} (single/vararg/array object or {@link List}) --&gt;
 * {@link Workbook}
 * <li>{@link BundleGroup} --&gt; {@link Sheet}
 * <li>{@link Workbook} --&gt; {@link List} of {@link BundleGroup}
 * <li>{@link Sheet} --&gt; {@link BundleGroup}
 * </ul>
 * and some more handy methods that can be used when converting between
 * '.properties' and '.xlsx' file formats.
 * 
 * @author kejn
 */
public class XlsxConverter {

    private static final int COLUMN_WIDTH = 11000;
    private static final short FONT_SIZE = 10;
    private static final String FONT_NAME = "Arial";

    /**
     * Row index in a {@link Sheet} for table header.
     */
    public static final int HEADER_ROW = 0;

    /**
     * First row index in a {@link Sheet} that contains the actual Properties
     * content (key/values).
     */
    public static final int CONTENT_FIRST_ROW = 1;

    /**
     * Column index holding Properties keys.
     */
    public static final int KEY_COLUMN = 0;

    /**
     * Column index holding the default values.
     */
    public static final int DEFAULT_COLUMN = 1;

    /**
     * The label for keys' column.
     */
    public static final String KEY_LABEL = "Key";

    /*
     * API.
     */

    /**
     * Converts one or more <b>bundleGroups</b> to {@link Workbook}. For each group
     * there is created a separate {@link Sheet} with name of the corresponding
     * group. Each sheet contain the following columns:
     * <ul>
     * <li>Key (property keys)
     * <li>Default (property values for {@link Language#DEFAULT})
     * <li>other translations (if present in the group)
     * </ul>
     * On each sheet there is applied an auto-filter and the header row is frozen,
     * not to loose context while browsing the result document.
     * 
     * @param bundleGroups one or more {@link BundleGroup}s
     * @return the {@link Workbook} containing a {@link Sheet} for each of
     *         <b>bundleGroups</b>
     */
    public Workbook toXlsx(BundleGroup... bundleGroups) {
        Objects.requireNonNull(bundleGroups);
        return toXlsx(Arrays.asList(bundleGroups));
    }

    /**
     * Converts list of <b>bundleGroups</b> to {@link Workbook}. For each group
     * there is created a separate {@link Sheet} with name of the corresponding
     * group. Each sheet contain the following columns:
     * <ul>
     * <li>Key (property keys)
     * <li>Default (property values for {@link Language#DEFAULT})
     * <li>other translations (if present in the group)
     * </ul>
     * On each sheet there is applied an auto-filter and the header row is frozen,
     * not to loose context while browsing the result document.
     * 
     * @param bundleGroups list of {@link BundleGroup}s
     * @return the {@link Workbook} containing a {@link Sheet} for each of
     *         <b>bundleGroups</b>
     */
    public Workbook toXlsx(List<BundleGroup> bundleGroups) {
        Objects.requireNonNull(bundleGroups);

        Workbook workbook = new XSSFWorkbook();
        for (BundleGroup group : bundleGroups) {
            Objects.requireNonNull(group, "The BundleGroup list contain null value");
            createSheet(workbook, group);
        }
        return workbook;
    }

    /**
     * Creates a {@link Sheet} in given <b>workbook</b> using given <b>group</b>.
     * The result sheet contains the following columns:
     * <ul>
     * <li>Key (property keys)
     * <li>Default (property values for {@link Language#DEFAULT})
     * <li>other translations (if present in the group)
     * </ul>
     * There is also applied an auto-filter and the header row is frozen, not to
     * loose context while browsing the result document.
     * 
     * @param workbook the target workbook, where the sheet will be created
     * @param group the {@link BundleGroup} used to create the sheet
     */
    public void createSheet(Workbook workbook, BundleGroup group) {
        Objects.requireNonNull(workbook);
        Objects.requireNonNull(group);

        Sheet sheet = workbook.createSheet(group.getName());

        createHeader(sheet, group);
        createTranslations(sheet, group);

        setAutoFilter(sheet, group);
        sheet.createFreezePane(KEY_COLUMN, CONTENT_FIRST_ROW);
        setWidthForAllColumns(sheet, group);
    }

    /**
     * Creates a header row in given <b>sheet</b> using the languages that are
     * supported by given <b>group</b>.
     * 
     * @param sheet the target sheet to create the header
     * @param group the group used to get the supported languages
     */
    public void createHeader(Sheet sheet, BundleGroup group) {
        Objects.requireNonNull(sheet);
        Objects.requireNonNull(group);

        CellStyle cellStyle = getHeaderCellStyle(sheet.getWorkbook());

        Row row = sheet.createRow(HEADER_ROW);
        createRow(group, row, cellStyle, KEY_LABEL, language -> {
            return language.getDisplayLanguage();
        });
    }

    /**
     * Creates the rows containing the keys and values in given <b>sheet</b> using
     * the languages that are supported by given <b>group</b>.
     * 
     * @param sheet the target sheet to create the translations
     * @param group the group used to get the supported languages
     */
    public void createTranslations(Sheet sheet, BundleGroup group) {
        Objects.requireNonNull(sheet);
        Objects.requireNonNull(group);

        CellStyle cellStyle = getContentCellStyle(sheet.getWorkbook());

        int rowIndex = CONTENT_FIRST_ROW;
        for (String key : group.stringPropertyNames()) {
            Row row = sheet.createRow(rowIndex);
            createRow(group, row, cellStyle, key, language -> {
                return group.getProperty(key, language);
            });
            ++rowIndex;
        }
    }

    /**
     * Converts given <b>workbook</b> to the list of {@link BundleGroup}s. The
     * <b>outputDirectory</b> parameter is used as the path for the {@link Bundle}s
     * in the effective {@link BundleGroup}s.
     * 
     * @param workbook the source workbook
     * @param outputDirectory the path for the {@link Bundle}s in the effective
     *            {@link BundleGroup}s.
     * @return the list of bundle groups created using the data from the source
     *         workbook
     * 
     * @throws IllegalStateException if the source sheet contains translation column
     *             not supported by the API
     */
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

    /**
     * Converts the <b>sheet</b> into a {@link BundleGroup}. The
     * <b>outputDirectory</b> parameter is used as the path for the {@link Bundle}s
     * in the effective {@link BundleGroup}.
     * 
     * @param sheet the source sheet
     * @param outputDirectory the path for the {@link Bundle}s in the effective
     *            {@link BundleGroup}.
     * @return the bundle group created using the data from the source sheet
     * 
     * @throws IllegalStateException if the source sheet contains translation column
     *             not supported by the API
     */
    public BundleGroup toBundleGroup(Sheet sheet, File outputDirectory) {
        Objects.requireNonNull(sheet);
        Objects.requireNonNull(outputDirectory);

        Row firstRow = sheet.rowIterator().next();
        Iterator<Cell> cellIterator = firstRow.cellIterator();
        // skip the column with keys
        cellIterator.next();

        List<Bundle> bundleList = new ArrayList<>();

        for (int index = DEFAULT_COLUMN; cellIterator.hasNext(); ++index) {
            String stringCellValue = cellIterator.next().getStringCellValue();
            Language language = Language.forDisplayLanguage(stringCellValue);
            if (language == null) {
                throw new IllegalStateException("Sheet contains unknown language: ["
                        + stringCellValue + "]. Languages supported by API: " + Language
                                .supportedDisplayLanguages());
            }

            Properties properties = sheetColumnToProperties(sheet, index);
            String bundlePath = Bundles.createFileName(outputDirectory, sheet.getSheetName(),
                    language);
            Bundle bundle = Bundles.newNotExistingBundle(bundlePath, properties);
            bundleList.add(bundle);
        }
        return Bundles.newBundleGroup(bundleList);
    }

    /**
     * Converts a specific column in a <b>sheet</b> to {@link Properties}.
     * 
     * @param sheet the source sheet
     * @param indexOfColumnwithTranslation index of column in given sheet that will
     *            be used to create the Properties
     * @return the Properties containing all properties specified in given sheet
     *         column
     */
    public Properties sheetColumnToProperties(Sheet sheet, int indexOfColumnwithTranslation) {
        Objects.requireNonNull(sheet);

        Iterator<Row> rowIterator = sheet.rowIterator();
        // skip the table header
        rowIterator.next();

        Properties propertiesToGenerate = new Properties();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell keyCell = row.getCell(KEY_COLUMN);
            Cell valueCell = row.getCell(indexOfColumnwithTranslation);
            if (keyCell != null && valueCell != null) {
                propertiesToGenerate.setProperty(keyCell.getStringCellValue(), valueCell
                        .getStringCellValue());
            }
        }
        return propertiesToGenerate;
    }

    /*
     * Private methods.
     */

    private void createRow(BundleGroup group, Row row, CellStyle cellStyle, String key,
            Function<Language, String> valueConverter) {
        createCellWithStyle(row, KEY_COLUMN, cellStyle, key);
        createCellWithStyle(row, DEFAULT_COLUMN, cellStyle, valueConverter.apply(Language.DEFAULT));

        int colIndex = DEFAULT_COLUMN + 1;
        for (Language language : group.supportedLanguagesWithoutDefault()) {
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
