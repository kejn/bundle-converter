package io.kejn.bundleconverter;

import java.util.Properties;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BundleConverter {


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

    private void createTranslations(Sheet sheet, BundleGroup group) {
	Set<String> keys = group.stringPropertyNames();

	int rowIndex = CONTENT_FIRST_ROW;
	for (String key : keys) {
	    Row row = sheet.createRow(rowIndex);

	    Cell cell = row.createCell(KEY_COLUMN);
	    cell.setCellValue(key);

	    cell = row.createCell(DEFAULT_COLUMN);
	    cell.setCellValue(group.getProperty(key, Language.DEFAULT));

	    int colIndex = DEFAULT_COLUMN + 1;
	    for (Language language : group.supportedLanguages()) {
		if (Language.DEFAULT.equals(language)) {
		    continue;
		}
		cell = row.createCell(colIndex);
		cell.setCellValue(group.getProperty(key, language));

		++colIndex;
	    }
	    ++rowIndex;
	}
    }

    private void createHeader(Sheet sheet, BundleGroup group) {
	Row row = sheet.createRow(HEADER_ROW);

	Cell cell = row.createCell(KEY_COLUMN);
	cell.setCellValue(KEY_LABEL);

	cell = row.createCell(DEFAULT_COLUMN);
	cell.setCellValue(Language.DEFAULT.getDisplayLanguage());

	int colIndex = DEFAULT_COLUMN + 1;
	for (Language language : group.supportedLanguages()) {
	    if (Language.DEFAULT.equals(language)) {
		continue;
	    }
	    cell = row.createCell(colIndex);
	    cell.setCellValue(language.getDisplayLanguage());

	    ++colIndex;
	}
    }

}
