package io.kejn.bundleconverter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BundleConverter {

    public static final String KEY_LABEL = "Key";

    public Workbook toXlsx(BundleGroup group) {
	Workbook workbook = new XSSFWorkbook();
	Sheet sheet = workbook.createSheet(group.getName());
	
	Row row = sheet.createRow(0);
	
	Cell cell = row.createCell(0);
	cell.setCellValue(KEY_LABEL);

	cell = row.createCell(1);
	cell.setCellValue(Language.DEFAULT.getDisplayLanguage());
	
	int colIndex = 2;
	for(Language language: group.supportedLanguages()) {
	    if(Language.DEFAULT.equals(language)) {
		continue;
	    }
	    cell = row.createCell(colIndex);
	    cell.setCellValue(language.getDisplayLanguage());
	    
	    ++colIndex;
	}

	return workbook;
    }

}
