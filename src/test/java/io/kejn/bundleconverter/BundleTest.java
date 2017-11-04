package io.kejn.bundleconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link Bundle} class.
 * 
 * @author kejn
 */
public class BundleTest {

    private Bundle bundle;

    @Before
    public void setUp() {
	bundle = null;
    }

    private void bundleIsOK() {
	assertNotNull(bundle);
	assertNotNull(bundle.getFile());
    }

    /**
     * All loaded files should have the ".properties" extension.
     */
    @Test
    public void shouldAcceptOnlyPropertiesFilesValidFilePath() {
	// when
	bundle = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);

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
	final File validFile = new File(Const.VALID_FILE_PATH_DEFAULT_BUNDLE.toUpperCase());

	// when
	bundle = new Bundle(validFile);

	// then
	bundleIsOK();
    }

    /**
     * The {@link File} passed as an argument to the {@link Bundle} constructor must
     * exist.
     */
    @Test
    public void filePassedAsConstructorArgumentMustExist() {
	// when
	bundle = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);

	// then
	bundleIsOK();
	assertTrue(bundle.getFile().exists());
    }

    /**
     * Test of overridden {@link Bundle#hashCode()} and
     * {@link Bundle#equals(Object)} methods. The same file with the same filename
     * should mean equality.
     * <p>
     * This is an extensive test checking if the {@link Object#equals(Object)}
     * paradigms are met.
     * <p>
     * @see Object#equals(Object)
     */
    @Test
    public void bundlesAreDistinguishedByFilenameSameFilesExtensiveHashCodeAndEqualsTest() {
	// given
	bundle = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);
	Bundle bundle2 = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);
	Bundle bundle3 = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);

	// then
	assertEquals("Hash codes of both bundles should be equal", bundle.hashCode(), bundle2.hashCode());
	assertEquals("Hash codes of both bundles should be equal", bundle.hashCode(), bundle3.hashCode());

	assertTrue("Equals method is not reflexive", bundle.equals(bundle));
	assertTrue("Equals method is not symmetric", bundle.equals(bundle2) && bundle2.equals(bundle));
	assertTrue("Equals method is not transitive",
		bundle.equals(bundle3) && bundle2.equals(bundle3) && bundle.equals(bundle3));
	assertFalse("Comparision with null should return false", bundle.equals(null));
    }

    /**
     * Test of overridden {@link Bundle#hashCode()} and
     * {@link Bundle#equals(Object)} methods. Different files with the same filename
     * should mean equality.
     */
    @Test
    public void bundlesAreDistinguishedByFilenameDifferentFilesSameFilename() {
	// given
	bundle = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);
	Bundle bundle2 = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE_OTHER_LOCATION);

	// then
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
	bundle = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);
	Bundle bundle2 = new Bundle(Const.VALID_FILE_PATH_POLISH_BUNDLE);

	// then
	assertNotEquals(bundle.hashCode(), bundle2.hashCode());
	assertNotEquals(bundle, bundle2);
    }
}
