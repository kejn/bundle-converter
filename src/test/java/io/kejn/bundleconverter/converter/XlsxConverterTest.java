package io.kejn.bundleconverter.converter;

import static io.kejn.bundleconverter.converter.AssertionHelper.assertSheetContainsTranslationInColumn;
import static io.kejn.bundleconverter.converter.AssertionHelper.verifyBundle;
import static io.kejn.bundleconverter.converter.AssertionHelper.verifyGroup;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import io.kejn.bundleconverter.Bundle;
import io.kejn.bundleconverter.BundleGroup;
import io.kejn.bundleconverter.Language;
import io.kejn.bundleconverter.shared.Path;

public class XlsxConverterTest {

    private final XlsxConverter converter = new XlsxConverter();

    private final Bundle defaultBundle = Bundle.newExistingBundle(Path.DEFAULT_BUNDLE);
    private final Bundle polishBundle = Bundle.newExistingBundle(Path.POLISH_BUNDLE);

    private final Bundle defaultValues = Bundle.newExistingBundle(Path.DEFAULT_VALUES);
    private final Bundle germanValues = Bundle.newExistingBundle(Path.GERMAN_VALUES);

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
		XlsxConverter.KEY_LABEL, //
		Language.DEFAULT.getDisplayLanguage(), //
		Language.POLISH.getDisplayLanguage() //
	};

	// when
	Workbook workbook = converter.toXlsx(group);

	// then
	assertNotNull(workbook);
	assertEquals(1, workbook.getNumberOfSheets());

	Sheet sheet = workbook.getSheet(group.getName());
	assertRowContainsValuesInOrder(sheet, XlsxConverter.HEADER_ROW, expectedHeaderContents);
    }

    private void assertRowContainsValuesInOrder(Sheet sheet, int rowNum, String... expectedValues) {
	assertNotNull(sheet);
	assertNotNull(expectedValues);

	Row row = sheet.getRow(XlsxConverter.HEADER_ROW);
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

	assertSheetContainsTranslationInColumn(sheet, key1, value1, XlsxConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(sheet, key1, value1_pl, XlsxConverter.DEFAULT_COLUMN + 1);
	assertSheetContainsTranslationInColumn(sheet, key2, value2, XlsxConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(sheet, key2, value2_pl, XlsxConverter.DEFAULT_COLUMN + 1);
    }

    @Test
    public void eachBundleGroupIsCreatedInSeparateSheet() {
	// given
	BundleGroup bundlesGroup = new BundleGroup(defaultBundle, polishBundle);
	String bundlesGroupKey1 = "key1";
	String bundlesGroupValue1 = defaultBundle.getProperties().getProperty(bundlesGroupKey1);
	String bundlesGroupValue1_pl = polishBundle.getProperties().getProperty(bundlesGroupKey1);
	String bundlesGroupKey2 = "key2";
	String bundlesGroupValue2 = defaultBundle.getProperties().getProperty(bundlesGroupKey2);
	String bundlesGroupValue2_pl = polishBundle.getProperties().getProperty(bundlesGroupKey2);

	BundleGroup valuesGroup = new BundleGroup(defaultValues, germanValues);
	String valuesGroupKey1 = "values.key1";
	String valuesGroupValue1 = defaultValues.getProperties().getProperty(valuesGroupKey1);
	String valuesGroupValue1_de = germanValues.getProperties().getProperty(valuesGroupKey1);
	String valuesGroupKey2 = "values.key2";
	String valuesGroupValue2 = defaultValues.getProperties().getProperty(valuesGroupKey2);
	String valuesGroupValue2_de = germanValues.getProperties().getProperty(valuesGroupKey2);

	// when
	Workbook workbook = converter.toXlsx(bundlesGroup, valuesGroup);

	// then
	assertNotNull(workbook);
	assertEquals(2, workbook.getNumberOfSheets());

	Sheet bundlesSheet = workbook.getSheet(bundlesGroup.getName());
	assertNotNull(bundlesSheet);

	assertSheetContainsTranslationInColumn(bundlesSheet, bundlesGroupKey1, bundlesGroupValue1,
		XlsxConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(bundlesSheet, bundlesGroupKey1, bundlesGroupValue1_pl,
		XlsxConverter.DEFAULT_COLUMN + 1);
	assertSheetContainsTranslationInColumn(bundlesSheet, bundlesGroupKey2, bundlesGroupValue2,
		XlsxConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(bundlesSheet, bundlesGroupKey2, bundlesGroupValue2_pl,
		XlsxConverter.DEFAULT_COLUMN + 1);

	Sheet valuesSheet = workbook.getSheet(valuesGroup.getName());
	assertNotNull(valuesSheet);

	assertSheetContainsTranslationInColumn(valuesSheet, valuesGroupKey1, valuesGroupValue1,
		XlsxConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(valuesSheet, valuesGroupKey1, valuesGroupValue1_de,
		XlsxConverter.DEFAULT_COLUMN + 1);
	assertSheetContainsTranslationInColumn(valuesSheet, valuesGroupKey2, valuesGroupValue2,
		XlsxConverter.DEFAULT_COLUMN);
	assertSheetContainsTranslationInColumn(valuesSheet, valuesGroupKey2, valuesGroupValue2_de,
		XlsxConverter.DEFAULT_COLUMN + 1);
    }

    @Test
    public void shouldCreateBundleForEachLanguage() throws IOException {
	// given
	File directory = new File(Path.DIR_PATH);
	BundleGroup group = new BundleGroup(defaultBundle, polishBundle);
	Workbook workbook = dummyWorkbook(Arrays.asList(group));

	// when
	List<BundleGroup> result = converter.toBundleGroupList(workbook, directory);

	// then
	assertNotNull(result);
	assertEquals(1, result.size());

	BundleGroup resultGroup = result.get(0);
	verifyGroup(group, resultGroup);

	verifyBundle(defaultBundle, resultGroup.getBundle(Language.DEFAULT));
	verifyBundle(polishBundle, resultGroup.getBundle(Language.POLISH));
    }



    private Workbook dummyWorkbook(List<BundleGroup> groups) {
	Workbook workbook = new XSSFWorkbook();
	for (BundleGroup group : groups) {
	    Sheet sheet = workbook.createSheet(group.getName());
	    converter.createHeader(sheet, group);
	    converter.createTranslations(sheet, group);
	}
	return workbook;
    }

    @Test
    public void shouldCreateBundleGroupForEachSheet() {
	// given
	File directory = new File(Path.DIR_PATH);
	BundleGroup groupBundle = new BundleGroup(defaultBundle, polishBundle);
	BundleGroup groupValues = new BundleGroup(defaultValues, germanValues);
	Workbook workbook = dummyWorkbook(Arrays.asList(groupBundle, groupValues));

	// when
	List<BundleGroup> result = converter.toBundleGroupList(workbook, directory);

	// then
	assertNotNull(result);
	assertEquals(2, result.size());

	BundleGroup resultGroupBundle = result.get(0);
	verifyGroup(groupBundle, resultGroupBundle);

	verifyBundle(defaultBundle, resultGroupBundle.getBundle(Language.DEFAULT));
	verifyBundle(polishBundle, resultGroupBundle.getBundle(Language.POLISH));

	BundleGroup resultGroupValues = result.get(1);
	verifyGroup(groupValues, resultGroupValues);

	verifyBundle(defaultValues, resultGroupValues.getBundle(Language.DEFAULT));
	verifyBundle(germanValues, resultGroupValues.getBundle(Language.GERMAN));
    }
}
