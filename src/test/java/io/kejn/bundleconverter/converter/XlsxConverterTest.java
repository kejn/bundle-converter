package io.kejn.bundleconverter.converter;

import static io.kejn.bundleconverter.converter.AssertionHelper.assertSheetContainsTranslationInColumn;
import static io.kejn.bundleconverter.converter.AssertionHelper.verifyBundle;
import static io.kejn.bundleconverter.converter.AssertionHelper.verifyGroup;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.kejn.bundleconverter.Bundle;
import io.kejn.bundleconverter.BundleGroup;
import io.kejn.bundleconverter.Bundles;
import io.kejn.bundleconverter.Language;
import io.kejn.bundleconverter.shared.Path;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

/**
 * Test for {@link XlsxConverter} class.
 * 
 * @author kejn
 */
public class XlsxConverterTest {

    private final XlsxConverter converter = new XlsxConverter();

    private final Bundle defaultBundle = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);
    private final Bundle polishBundle = Bundles.newExistingBundle(Path.POLISH_BUNDLE);

    private final Bundle defaultValues = Bundles.newExistingBundle(Path.DEFAULT_VALUES);
    private final Bundle germanValues = Bundles.newExistingBundle(Path.GERMAN_VALUES);

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
            assertTrue("Index " + index + " is too big. Maximum should be " + (expectedValues.length
                    - 1) + ". Current cell value: " + cellValue, //
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
        assertSheetContainsTranslationInColumn(sheet, key1, value1_pl, XlsxConverter.DEFAULT_COLUMN
                + 1);
        assertSheetContainsTranslationInColumn(sheet, key2, value2, XlsxConverter.DEFAULT_COLUMN);
        assertSheetContainsTranslationInColumn(sheet, key2, value2_pl, XlsxConverter.DEFAULT_COLUMN
                + 1);
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
        assertSheetContainsTranslationInColumn(bundlesSheet, bundlesGroupKey1,
                bundlesGroupValue1_pl, XlsxConverter.DEFAULT_COLUMN + 1);
        assertSheetContainsTranslationInColumn(bundlesSheet, bundlesGroupKey2, bundlesGroupValue2,
                XlsxConverter.DEFAULT_COLUMN);
        assertSheetContainsTranslationInColumn(bundlesSheet, bundlesGroupKey2,
                bundlesGroupValue2_pl, XlsxConverter.DEFAULT_COLUMN + 1);

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

    /**
     * 
     * @throws IOException should not be thrown
     */
    @Test
    public void shouldgetAllPropertiesFromColumn() throws IOException {
        // given
        final String key1 = "key1";
        final String key2 = "key2";
        final String value1 = "value1";
        final String value2 = "value2";

        Properties expectedProperties = new Properties();
        expectedProperties.setProperty(key1, value1);
        expectedProperties.setProperty(key2, value2);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            createRow(sheet, 0, "header keys", "header values");
            createRow(sheet, 1, key1, value1);
            createRow(sheet, 2, key2, value2);

            final int column = 1;

            // when
            Properties properties = converter.sheetColumnToProperties(sheet, column);

            // then
            assertNotNull(properties.getProperty(key1));
            assertEquals(value1, properties.getProperty(key1));

            assertNotNull(properties.getProperty(key2));
            assertEquals(value2, properties.getProperty(key2));
        }
    }

    private void createRow(Sheet sheet, int rowIndex, final String... cellValues) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < cellValues.length; ++i) {
            Cell keyCell1 = row.createCell(i);
            keyCell1.setCellValue(cellValues[i]);
        }
    }

    @Test
    public void shouldCreateGroupWithNameEqualToSheetTitle() throws IOException {
        // given
        final String sheetname = "name";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetname);
            createRow(sheet, 0, XlsxConverter.KEY_LABEL, Language.DEFAULT.getDisplayLanguage(),
                    Language.ABKHAZIAN.getDisplayLanguage(), Language.AFAR.getDisplayLanguage(),
                    Language.AFRIKAANS.getDisplayLanguage());
            createRow(sheet, 1);

            final File directory = spyDirectory();
            XlsxConverter spyConverter = spy(converter);
            
            // when
            doReturn(new Properties()).when(spyConverter).sheetColumnToProperties(any(Sheet.class),
                    anyInt());
            BundleGroup group = spyConverter.toBundleGroup(sheet, directory);

            // then
            verify(spyConverter, times(4)).sheetColumnToProperties(any(Sheet.class), anyInt());

            assertNotNull(group);
            assertEquals(sheetname, group.getName());
        }
    }

    private File spyDirectory() {
        final File directory = mock(File.class);

        when(directory.exists()).thenReturn(true);
        when(directory.isDirectory()).thenReturn(true);
        return directory;
    }

    @Test
    public void shouldCreateBundleGroupForEachSheetInWorkbook() throws IOException {
        // given
        final String[] sheetnames = { "sheet1", "sheet2", "sheet3" };
        try (Workbook workbook = new XSSFWorkbook()) {
            for (String sheetname : sheetnames) {
                Sheet sheet = workbook.createSheet(sheetname);
                createRow(sheet, 0, XlsxConverter.KEY_LABEL, Language.DEFAULT.getDisplayLanguage());
                createRow(sheet, 1);
            }
            File directory = mock(File.class);
            XlsxConverter spyConverter = spy(converter);

            Bundle bundle = new Bundle(new File("bundle.properties"));
            BundleGroup group = new BundleGroup(bundle);

            // when
            doReturn(group).when(spyConverter).toBundleGroup(any(Sheet.class), any(File.class));
            spyConverter.toBundleGroupList(workbook, directory);

            // then
            verify(spyConverter, times(3)).toBundleGroup(any(Sheet.class), any(File.class));
        }

    }
}
