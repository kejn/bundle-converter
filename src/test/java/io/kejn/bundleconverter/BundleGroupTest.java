package io.kejn.bundleconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author kejn
 */
public class BundleGroupTest {

    private static final Bundle BUNDLE_DEFAULT = new Bundle(Const.VALID_FILE_PATH_DEFAULT_BUNDLE);
    private static final Bundle BUNDLE_POLISH = new Bundle(Const.VALID_FILE_PATH_POLISH_BUNDLE);
    private static final Bundle VALUES_DEFAULT = new Bundle(Const.VALID_FILE_PATH_DEFAULT_VALUES);

    private BundleGroup group;
    
    @Before
    public void setUp() {
	group = new BundleGroup(BUNDLE_DEFAULT);
    }
    
    /**
     * If some {@link Bundle} was already added to the group, adding it again will
     * not overwrite the old one and the group will not change.
     */
    @Test
    public void addingExistingBundleDoesNotOverwriteAnOldOne() {
	// when
	boolean addedToGroup = group.add(BUNDLE_DEFAULT);

	// then
	assertFalse(addedToGroup);
	assertNotNull(group.getDefaultBundle());
	assertEquals(1, group.size());
    }

    /**
     * A {@link BundleGroup} shold be able to hold more than one {@link Bundle}.
     */
    @Test
    public void canAddMultipleBundlesToOneGroup() {
	// when
	boolean addedToGroup = group.add(BUNDLE_POLISH);

	// then
	assertTrue(addedToGroup);
	assertEquals(2, group.size());
    }

    /**
     * The group should reject to be created if any of the listed {@link Bundle}s is
     * not a different language variant of the same ".properties" file, but
     * a whole different bundle.
     * <p>
     * For example it is not possible to keep in one {@link BundleGroup} a
     * {@link Bundle} with name "bundle.properties" and a {@link Bundle} with name
     * "values.properties".
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectToCreateWithDifferentBundles() {
	new BundleGroup(BUNDLE_DEFAULT, VALUES_DEFAULT);
    }

    /**
     * The group should reject to accept a {@link Bundle}, which is not a different
     * language variant of the same ".properties" file, but a whole
     * different bundle.
     * <p>
     * For example it is not possible to keep in one {@link BundleGroup} a
     * {@link Bundle} with name "bundle.properties" and a {@link Bundle} with name
     * "values.properties".
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectToAddDifferentBundles() {
	// when
	group.add(VALUES_DEFAULT);
    }

    @Test
    public void canGetDefaultBundle() {
	// when
	Bundle bundle = group.getDefaultBundle();
	
	// then
	assertEquals(BUNDLE_DEFAULT, bundle);
    }


    @Test
    public void canGetBundleByLocale() {
	// given
	group.add(BUNDLE_POLISH);
	
	// when
	Bundle bundle = group.getBundle(BUNDLE_POLISH.getLocale());
	
	// then
	assertEquals(BUNDLE_POLISH, bundle);
	assertEquals(2, group.size());
    }
}
