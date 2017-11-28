package io.kejn.bundleconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	assertNotNull(sheet);

	Row row = sheet.getRow(0);
	assertNotNull(row);

	assertRowContainsValuesInOrder(row, expectedHeaderContents);
    }

    private void assertRowContainsValuesInOrder(Row row, String... expectedValues) {
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

}
