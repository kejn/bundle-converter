package io.kejn.bundleconverter;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

public class PropertiesToXlsxConverterTest {

    private final BundleConverter converter = new BundleConverter();
    private final Bundle defaultBundle = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);
    private final Bundle polishBundle = new Bundle(Const.VALID_FILE_PATH_POLISH_BUNDLE);

    /**
     * There should be created a sheet with name equal to {@link BundleGroup} name.
     */
    @Test
    public void shouldCreateSheetWithTitleEqualToBundleGroupName() {
	// given
	BundleGroup group = new BundleGroup(defaultBundle);

	// when
	Workbook workbook = converter.toXlsx(group);

	// then
	assertNotNull(workbook);
	assertEquals(1, workbook.getNumberOfSheets());
	assertNotNull(workbook.getSheet(group.getName()));
    }

    /**
     * There should be a header in first row. First cell should contain 'Key' value.
     * Second cell the 'Default' value. All other should be {@link Language} display
     * names corresponding to the translation.
     */
    @Test
    public void sheetShouldContainHeaderInFirstRow() {
	// given
	BundleGroup group = new BundleGroup(defaultBundle, polishBundle);
	String[] expectedHeaderContents = { //
		BundleConverter.KEY_LABEL, //
		Language.DEFAULT.getDisplayLanguage(), //
		Language.POLISH.getDisplayLanguage() //
	};

	// when
	Workbook workbook = converter.toXlsx(group);

	// then
	assertNotNull(workbook);
	assertEquals(1, workbook.getNumberOfSheets());

	Sheet sheet = workbook.getSheet(group.getName());
	assertRowContainsValuesInOrder(sheet, BundleConverter.HEADER_ROW, expectedHeaderContents);
    }

    private void assertRowContainsValuesInOrder(Sheet sheet, int rowNum, String... expectedValues) {
	assertNotNull(sheet);
	assertNotNull(expectedValues);

	Row row = sheet.getRow(BundleConverter.HEADER_ROW);
	assertNotNull(row);

	Iterator<Cell> iterator = row.cellIterator();
	assertNotNull(iterator);

	int index = 0;
	while (iterator.hasNext()) {
	    Cell cell = iterator.next();
	    assertNotNull(cell);
	    assertEquals(CellType.STRING, cell.getCellTypeEnum());

	    String cellValue = cell.getStringCellValue();
	    assertTrue(
		    "Index " + index + " is too big. Maximum should be " + (expectedValues.length - 1)
			    + ". Current cell value: " + cellValue, //
		    index < expectedValues.length);
	    assertEquals(expectedValues[index], cell.getStringCellValue());

	    ++index;
	}
	assertEquals(expectedValues.length, index);
    }

    @Test
    public void sheetShouldContainTranslationsInProperColumns() {
	// given
	BundleGroup group = new BundleGroup(defaultBundle, polishBundle);
	String key1 = "key1";
	String value1 = defaultBundle.getProperties().getProperty(key1);
	String value1_pl = polishBundle.getProperties().getProperty(key1);
	String key2 = "key2";
	String value2 = defaultBundle.getProperties().getProperty(key2);
	String value2_pl = polishBundle.getProperties().getProperty(key2);

	// when
	Workbook workbook = converter.toXlsx(group);

	// then
	assertNotNull(workbook);
	assertEquals(1, workbook.getNumberOfSheets());

	Sheet sheet = workbook.getSheet(group.getName());
	assertNotNull(sheet);

	assertSheetContainsTranslationInColumn(sheet, key1, value1, BundleConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(sheet, key1, value1_pl, BundleConverter.DEFAULT_COLUMN + 1);
	assertSheetContainsTranslationInColumn(sheet, key2, value2, BundleConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(sheet, key2, value2_pl, BundleConverter.DEFAULT_COLUMN + 1);
    }

    private void assertSheetContainsTranslationInColumn(Sheet sheet, String key, String translation, int column) {
	assertNotNull(sheet);
	assertNotNull(key);
	assertNotNull(translation);
	assertTrue(column >= 0);

	Iterator<Row> rowIterator = sheet.rowIterator();
	assertNotNull(rowIterator);

	Row row = null;
	while (rowIterator.hasNext()) {
	    row = rowIterator.next();

	    Cell cell = row.getCell(BundleConverter.KEY_COLUMN);
	    if (cell != null && key.equals(cell.getStringCellValue())) {
		break;
	    }
	    row = null;
	}
	assertNotNull(row);

	Iterator<Cell> iterator = row.cellIterator();
	assertNotNull(iterator);

	boolean found = false;
	while (iterator.hasNext() && !found) {
	    Cell cell = iterator.next();
	    assertNotNull(cell);
	    assertEquals(CellType.STRING, cell.getCellTypeEnum());

	    String cellValue = cell.getStringCellValue();
	    if (key.equals(cellValue)) {
		continue;
	    }
	    found = translation.equals(cellValue);
	}
	assertTrue(found);
    }

}
