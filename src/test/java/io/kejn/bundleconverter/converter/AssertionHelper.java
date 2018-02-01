package io.kejn.bundleconverter.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.kejn.bundleconverter.Bundle;
import io.kejn.bundleconverter.BundleGroup;

import java.util.Iterator;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


class AssertionHelper {
    public static void assertSheetContainsTranslationInColumn(Sheet sheet, String key,
            String translation, int column) {
        assertNotNull(sheet);
        assertNotNull(key);
        assertNotNull(translation);
        assertTrue(column >= 0);

        Iterator<Row> rowIterator = sheet.rowIterator();
        assertNotNull(rowIterator);

        Row row = null;
        while (rowIterator.hasNext()) {
            row = rowIterator.next();

            Cell cell = row.getCell(XlsxConverter.KEY_COLUMN);
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

    public static void verifyBundle(Bundle bundle, Bundle resultDefaultBundle) {
        assertNotNull(resultDefaultBundle);
        Set<String> keys = resultDefaultBundle.getProperties().stringPropertyNames();
        for (String key : keys) {
            String expectedValue = bundle.getProperties().getProperty(key);
            String resultValue = resultDefaultBundle.getProperties().getProperty(key);
            assertEquals(expectedValue, resultValue);
        }
    }

    public static void verifyGroup(BundleGroup group, BundleGroup resultGroup) {
        assertNotNull(resultGroup);
        assertEquals(2, resultGroup.size());
        assertTrue(resultGroup.supportedLanguages().containsAll(group.supportedLanguages()));
        assertEquals(group.stringPropertyNames().size(), resultGroup.stringPropertyNames().size());
        assertTrue(resultGroup.stringPropertyNames().containsAll(group.stringPropertyNames()));
    }
}