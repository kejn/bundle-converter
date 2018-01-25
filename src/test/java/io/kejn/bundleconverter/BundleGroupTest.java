package io.kejn.bundleconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.kejn.bundleconverter.shared.Path;

/**
 * Tests for {@link BundleGroup} class.
 * 
 * @author kejn
 */
public class BundleGroupTest {

    private static final Bundle BUNDLE_DEFAULT = Bundles.newExistingBundle(Path.DEFAULT_BUNDLE);
    private static final Bundle BUNDLE_POLISH = Bundles.newExistingBundle(Path.POLISH_BUNDLE);
    private static final Bundle VALUES_DEFAULT = Bundles.newExistingBundle(Path.DEFAULT_VALUES);

    private BundleGroup group;

    @Before
    public void setUp() {
        group = new BundleGroup(BUNDLE_DEFAULT);
    }

    /**
     * The default {@link Bundle}, which is required in order to initialize a group,
     * cannot be some kind of language variant. It should be a bundle with the
     * default values only, not translated values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void defaultBundleCannotBeABundleVariant() {
        new BundleGroup(BUNDLE_POLISH);
    }

    /**
     * If some {@link Bundle} was already added to the group, adding it again will
     * not overwrite the old one and the group will not change.
     */
    @Test
    public void addingExistingBundleDoesNotOverwriteAnOldOne() {
        // when
        final boolean addedToGroup = group.put(BUNDLE_DEFAULT);

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
        final boolean addedToGroup = group.put(BUNDLE_POLISH);

        // then
        assertTrue(addedToGroup);
        assertEquals(2, group.size());
    }

    /**
     * The group should reject to be created if any of the listed {@link Bundle}s is
     * not a different language variant of the same ".properties" file, but a whole
     * different bundle.
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
     * language variant of the same ".properties" file, but a whole different
     * bundle.
     * <p>
     * For example it is not possible to keep in one {@link BundleGroup} a
     * {@link Bundle} with name "bundle.properties" and a {@link Bundle} with name
     * "values.properties".
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectToAddDifferentBundles() {
        // when
        group.put(VALUES_DEFAULT);
    }

    /**
     * The {@link BundleGroup} shouldn't exist without a default bundle.
     */
    @Test
    public void canGetDefaultBundle() {
        // when
        final Bundle bundle = group.getDefaultBundle();

        // then
        assertEquals(BUNDLE_DEFAULT, bundle);
    }

    /**
     * It should be possible to get bundle by {@link Language}
     */
    @Test
    public void canGetBundleByLocale() {
        // given
        final Language polish = Language.POLISH;
        group.put(BUNDLE_POLISH);

        // when
        final Bundle bundle = group.getBundle(polish);

        // then
        assertEquals(BUNDLE_POLISH, bundle);
        assertEquals(2, group.size());
    }

    @Test
    public void shouldHaveTheSameNameAsTheDefaultBundle() {
        // when
        final Bundle bundle = group.getDefaultBundle();

        // then
        assertEquals(bundle.getName(), group.getName());
    }

    @Test
    public void shouldCreateAListOfAllGroupsInDirectory() {
        // given
        File directory = new File(Path.DIR_PATH);
        assertTrue(directory.exists());
        assertTrue(directory.isDirectory());

        // when
        List<BundleGroup> groups = Bundles.groupsInDirectory(directory);

        // then
        assertNotNull(groups);
        assertEquals(3, groups.size());
    }

    @Test
    public void shoudldSaveAllBundlesInGroupToPropertiesFiles() throws IOException {
        // given
        Bundle defaultSpy = spy(BUNDLE_DEFAULT);
        Bundle polishSpy = spy(BUNDLE_POLISH);
        BundleGroup group = new BundleGroup(defaultSpy, polishSpy);

        // when
        doNothing().when(defaultSpy).saveToFile(isNull());
        doNothing().when(polishSpy).saveToFile(isNull());

        group.saveGroupAsPropertiesFiles();

        verify(defaultSpy).saveToFile(isNull());
        verify(polishSpy).saveToFile(isNull());
    }

    @Test
    public void shoudldSaveAllBundlesInGroupToPropertiesFilesUsingTemplate() throws IOException {
        // given
        Bundle defaultSpy = spy(BUNDLE_DEFAULT);
        Bundle polishSpy = spy(BUNDLE_POLISH);
        BundleGroup group = new BundleGroup(defaultSpy, polishSpy);
        File templateFile = new File("bundle_es.properties");

        // when
        doNothing().when(defaultSpy).saveToFile(any(File.class));
        doNothing().when(polishSpy).saveToFile(any(File.class));

        group.saveGroupAsPropertiesFiles(templateFile);

        verify(defaultSpy).saveToFile(any(File.class));
        verify(polishSpy).saveToFile(any(File.class));
    }
}
