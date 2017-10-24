package io.kejn.bundleconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author kejn
 */
public class BundleGroupTest {

    private final Bundle validBundleDefault = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);
    private final Bundle validBundlePolish = new Bundle(Const.VALID_FILE_PATH_POLISH_BUNDLE);

    @Test
    public void canAddPropertiesFileAfterGroupWasCreated() {
	// given
	BundleGroup group = new BundleGroup();

	// when
	boolean addedToGroup = group.add(validBundleDefault);

	// then
	assertTrue(addedToGroup);
	assertNotNull(group.getBundle());
	assertEquals(1, group.size());
    }

    @Test
    public void canAddMultipleBundlesToOneGroup() {
	// given
	BundleGroup group = new BundleGroup();

	// when
	boolean addedToGroupFirst = group.add(validBundleDefault);
	boolean addedToGroupSecond = group.add(validBundlePolish);

	// then
	assertTrue(addedToGroupFirst);
	assertTrue(addedToGroupSecond);
	assertEquals(2, group.size());
    }

    @Test
    @Ignore
    public void shouldAcceptDifferentLanguageVariantsOfTheSamePropertiesFiles() {
	// given
	// PropertiesGroup group
	fail("Not yet implemented");
    }

}
