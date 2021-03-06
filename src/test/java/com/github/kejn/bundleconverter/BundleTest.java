package com.github.kejn.bundleconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.github.kejn.bundleconverter.Bundle;
import com.github.kejn.bundleconverter.Bundles;
import com.github.kejn.bundleconverter.shared.Path;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link Bundle} class.
 * 
 * @author kejn
 */
public class BundleTest {

    private Bundle bundle;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        bundle = null;
    }

    private void bundleIsOK() {
        bundleIsOK(bundle);
    }

    private void bundleIsOK(Bundle bundle) {
        assertNotNull(bundle);
        assertNotNull(bundle.getProperties());
    }

    /**
     * All loaded files should have the ".properties" extension.
     */
    @Test
    public void shouldAcceptOnlyPropertiesFilesValidFilePath() {
        // when
        bundle = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);

        // then
        bundleIsOK();
    }

    /**
     * Files with extensions other than '.properties' should not be accepted.
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldAcceptOnlyPropertiesFilesInvalidFileName() {
        // given
        final File invalidFile = new File("bundle.propertY");

        // when
        bundle = new Bundle(invalidFile);
    }

    /**
     * File extension check should ignore case. Both lowercase and uppercase should
     * be accepted. It means that the '.PROPERTIES' extension should be accepted as
     * well as '.properties'.
     */
    @Test
    public void fileExtensionCheckShouldBeCaseInsensitive() {
        // given
        final File validFile = new File(Path.UPPERCASE_BUNDLE);

        // when
        bundle = new Bundle(validFile);

        // then
        bundleIsOK();
    }

    /**
     * Test of overridden {@link Bundle#hashCode()} and
     * {@link Bundle#equals(Object)} methods. The same file with the same filename
     * should mean equality.
     * <p>
     * This is an extensive test checking if the {@link Object#equals(Object)}
     * paradigms are met.
     * <p>
     * 
     * @see Object#equals(Object)
     */
    @Test
    public void bundlesAreDistinguishedByFilenameSameFilesHashCodeAndEqualsAndComparableTest() {
        // given
        bundle = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);
        Bundle bundle2 = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);
        Bundle bundle3 = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);

        // then
        bundleIsOK();
        bundleIsOK(bundle2);
        bundleIsOK(bundle3);

        assertEquals("Hash codes of both bundles should be equal", bundle.hashCode(), bundle2
                .hashCode());
        assertEquals("Hash codes of both bundles should be equal", bundle.hashCode(), bundle3
                .hashCode());

        assertTrue("Equals method is not reflexive", bundle.equals(bundle));
        assertTrue("Equals method is not symmetric", bundle.equals(bundle2) && bundle2.equals(
                bundle));
        assertTrue("Equals method is not transitive", bundle.equals(bundle3) && bundle2.equals(
                bundle3) && bundle.equals(bundle3));
        assertFalse("Comparision with null should return false", bundle.equals(null));

        assertEquals("CompareTo method is not reflexive", 0, bundle.compareTo(bundle));
        assertTrue("CompareTo method is not symmetric", bundle.compareTo(bundle2) == 0 && bundle2
                .compareTo(bundle) == 0);
        assertTrue("CompareTo method is not transitive", bundle.compareTo(bundle3) == 0 && bundle2
                .compareTo(bundle3) == 0 && bundle.compareTo(bundle3) == 0);
        assertEquals("Comparision with null should return -1", -1, bundle.compareTo(null));

    }

    /**
     * Test of overridden {@link Bundle#hashCode()} and
     * {@link Bundle#equals(Object)} methods. Different files with the same filename
     * should mean equality.
     */
    @Test
    public void bundlesAreDistinguishedByFilenameDifferentFilesSameFilename() {
        // given
        bundle = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);
        Bundle bundle2 = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE_OTHER_LOCATION);

        // then
        bundleIsOK();
        bundleIsOK(bundle2);
        assertEquals(bundle.hashCode(), bundle2.hashCode());
        assertEquals(bundle, bundle2);
    }

    /**
     * Test of overridden {@link Bundle#hashCode()} and
     * {@link Bundle#equals(Object)} methods. Different files with different
     * filenames should not mean equality.
     */
    @Test
    public void bundlesAreDistinguishedByFilenameDifferentFilesDifferentFilename() {
        // given
        bundle = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);
        Bundle bundle2 = Bundles.newExistingBundle(Path.POLISH_BUNDLE);

        // then
        bundleIsOK();
        bundleIsOK(bundle2);
        assertNotEquals(bundle.hashCode(), bundle2.hashCode());
        assertNotEquals(bundle, bundle2);
    }

    @Test
    public void differentBundleLanguageVariantsHaveTheSameName() {
        // given
        bundle = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);
        Bundle bundle2 = Bundles.newExistingBundle(Path.POLISH_BUNDLE);

        // then
        bundleIsOK();
        bundleIsOK(bundle2);
        assertEquals(bundle.getName(), bundle2.getName());
    }

    @Test
    public void bundleWithSameNameAndNameWithLanguageVariantIsDefaultBundle() {
        // given
        bundle = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);

        // then
        bundleIsOK();
        assertEquals(bundle.getName(), bundle.getNameWithLanguageVariant());
        assertTrue(bundle.isDefaultBundle());
    }

    @Test
    public void canSavePropertiesToFile() throws IOException {
        // given
        final String key = "key";
        final String value = "value";
        final File file = folder.newFile("tempBundle.properties");
        final Properties properties = new Properties();
        properties.setProperty(key, value);
        bundle = Bundles.newNotExistingBundle(file.getPath(), properties);

        // when
        bundle.saveToFile();
        final Properties actualProperties = new Properties();
        actualProperties.load(new FileInputStream(file));

        // then
        final String actualValue = actualProperties.getProperty(key);
        assertNotNull(value, actualValue);
        assertEquals(value, actualValue);
    }

    @Test
    public void canSavePropertiesToFileUsingOtherFileAsTemplate() throws IOException {
        // given
        final File templateFile = new File(Path.DEFAULT_BUNDLE_OTHER_LOCATION);
        final Bundle templateBundle = new Bundle(templateFile);
        final BufferedReader templateReader = new BufferedReader(new FileReader(templateFile));

        final File targetFile = folder.newFile("tempBundle.properties");
        final Properties properties = templateBundle.getProperties();
        bundle = Bundles.newNotExistingBundle(targetFile.getPath(), properties);
        final BufferedReader targetReader = new BufferedReader(new FileReader(targetFile));

        // when
        bundle.saveToFile(templateFile);

        // then
        String templateLine = null;
        String targetLine = null;
        boolean testFailed = false;
        while ((templateLine = templateReader.readLine()) != null) {
            targetLine = targetReader.readLine();
            if (targetLine == null || !templateLine.equals(targetLine)) {
                testFailed = true;
                break;
            }
        }
        templateReader.close();
        targetReader.close();
        assertFalse(String.format("Assertion failed. templateLine(%s) != targetLine(%s)",
                templateLine, targetLine), testFailed);
    }
}
