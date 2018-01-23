package io.kejn.bundleconverter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * A group of {@link Bundle} objects that refer to the same properties, but in
 * different language variants. It must contain at least the
 * {@link Language#DEFAULT} translation. Other translations can be provided
 * later using the {@link #put(Bundle)} or {@link #putAll(Collection)} methods.
 * <p>
 * They are stored as a map where the keys are {@link Language} enums and the
 * values are the corresponding {@link Bundle} objects.
 * 
 * @author kejn
 * 
 * @see Language
 * @see Bundle
 * @see Map
 */
public class BundleGroup {

    private final Bundle defaultBundle;

    private Map<Language, Bundle> bundles = new HashMap<>();

    /*
     * API.
     */

    /**
     * Creates a group of {@link Bundle}s and initializes it with a {@link Bundle}
     * containing the default values. If <b>otherBundleVariants</b> are specified
     * and they are simply other language variants of the <b>defaultBundle</b> they
     * are also added to the {@link #bundles} map.
     * 
     * @param defaultBundle the bundle containing default values
     * @param otherBundleVariants (optional) other bundles to be added to this group
     * 
     * @throws NullPointerException if <b>defaultBundle</b> is null
     * @throws IllegalArgumentException if <b>defaultBundle</b> is not a default
     *             bundle (is a language variant of some bundle), or if
     *             <b>otherBundleVariants</b> array contain at least one
     *             {@link Bundle} which is not a language variant of
     *             <b>defaultBundle</b>, but a completely different bundle than
     *             <b>defaultBundle</b>
     */
    public BundleGroup(Bundle defaultBundle, Bundle... otherBundleVariants) {
	this(defaultBundle, Arrays.asList(otherBundleVariants));
    }

    /**
     * Creates a group of {@link Bundle}s and initializes it with a {@link Bundle}
     * containing the default values. If <b>otherBundleVariants</b> collection is
     * not empty, then it contains simply other language variants of the
     * <b>defaultBundle</b> and they are also added to the {@link #bundles} map.
     * 
     * @param defaultBundle the bundle containing default values
     * @param otherBundleVariants collection of other bundles to be added to this
     *            group
     * 
     * @throws NullPointerException if <b>defaultBundle</b> is null
     * @throws IllegalArgumentException if <b>defaultBundle</b> is not a default
     *             bundle (is a language variant of some bundle), or if
     *             <b>otherBundleVariants</b> collection contain at least one
     *             {@link Bundle} which is not a language variant of
     *             <b>defaultBundle</b>, but a completely different bundle than
     *             <b>defaultBundle</b>
     */
    public BundleGroup(Bundle defaultBundle, Collection<Bundle> otherBundleVariants) {
	Objects.requireNonNull(defaultBundle, "The default Bundle cannot be null");

	if (!defaultBundle.isDefaultBundle()) {
	    throw new IllegalArgumentException("The defaultBundle cannot be a language variant. "
		    + "It must be the bundle with the default values, not the translated values");
	}

	this.defaultBundle = defaultBundle;
        put(defaultBundle);
	putAll(otherBundleVariants);
    }

    /**
     * Puts a bundle to this bundle group (if it is not already present).
     * 
     * @param bundle bundle to be added to this group
     * @return <tt>true</tt> if the bundle was added, <tt>false</tt> otherwise.
     * @see Map#put(Object, Object)
     * 
     * @throws IllegalArgumentException if <b>bundle</b> is not a language variant
     *             of <b>defaultBundle</b>, but a completely different bundle than
     *             <b>defaultBundle</b>
     */
    public boolean put(Bundle bundle) {
	checkBundle(bundle);
	return bundles.put(bundle.getLanguage(), bundle) == null;
    }

    /**
     * Puts all bundles in the provided collection to this bundle group (if they are
     * not already present).
     * 
     * @param bundles bundles to be added to this group
     * @return <tt>true</tt> if any bundle from the provided collection was added to
     *         this group, <tt>false</tt> otherwise.
     * @see Map#putAll(Map)
     * 
     * @throws IllegalArgumentException if <b>otherBundleVariants</b> collection
     *             contain at least one {@link Bundle} which is not a language
     *             variant of <b>defaultBundle</b>, but a completely different
     *             bundle than <b>defaultBundle</b>
     */
    public boolean putAll(Collection<Bundle> bundles) {
	Objects.requireNonNull(bundles, "Bundles cannot be null");

	boolean addedSomething = false;
	for (Bundle b : bundles) {
            if (put(b)) {
                addedSomething = true;
            }
	}
	return addedSomething;

    }

    /**
     * @return the {@link #defaultBundle}
     */
    public Bundle getDefaultBundle() {
	return defaultBundle;
    }

    /**
     * @return number of bundles in this group
     */
    public int size() {
	return bundles.size();
    }

    /**
     * Checks if this group already contains given <b>bundle</b>.
     * 
     * @param bundle the bundle to find
     * @return <tt>true</tt> if this group already contains given <b>bundle</b>,
     *         <tt>false</tt> otherwise
     */
    public boolean contains(Bundle bundle) {
	return getName().equals(bundle.getName()) && getBundle(bundle.getLanguage()) != null;
    }

    /**
     * Returns a bundle translation from this group for given <b>language</b>.
     * 
     * @param language the language
     * @return <tt>null</tt> if this group does not contain a translation for given
     *         <b>language</b>
     */
    public Bundle getBundle(Language language) {
	return bundles.get(language);
    }

    /**
     * The name of this group. It is equal to the {@link #defaultBundle} name.
     * 
     * @see Bundle#getName()
     * @return the name of this group
     */
    public String getName() {
	return defaultBundle.getName();
    }

    /**
     * @return set of {@link Language}s of all bundles in this group
     */
    public Set<Language> supportedLanguages() {
	return bundles.keySet();
    }

    /**
     * @return set of property keys that the {@link #defaultBundle} contains
     */
    public Set<String> stringPropertyNames() {
	Properties properties = defaultBundle.getProperties();
	if (properties == null) {
	    return Collections.emptySet();
	}
	return properties.stringPropertyNames();
    }

    /**
     * Returns the property value from given <b>key</b> from the bundle matching
     * given <b>language</b>.
     * <p>
     * <tt>null</tt> will be returned if:
     * <ul>
     * <li>this group does not contain a bundle for given <b>language</b>
     * <li>it was not possible to get the properties from the bundle mapped by given
     * <b>language</b>
     * <li>the bundle mapped by given <b>language</b> does not contain the mapping
     * for given <b>key</b>
     * 
     * @param key the property key
     * @param language the language
     * @return the property value from given <b>key</b> from the bundle matching
     */
    public String getProperty(String key, Language language) {
	Bundle bundle = getBundle(language);
	if (bundle == null) {
	    return null;
	}
        Properties prop = bundle.getProperties();
        if (prop == null) {
            return null;
        }
        return prop.getProperty(key);
    }

    /**
     * Saves all the bundles in group as '.properties' files.
     * 
     * @throws IOException if the any of the {@link Bundle#saveToFile()} call throws
     *             IOException
     * 
     * @see Bundle#saveToFile()
     */
    public void saveGroupAsPropertiesFiles() throws IOException {
        saveGroupAsPropertiesFiles(null);
    }

    /**
     * Saves all the bundles in group as '.properties' files matching property keys
     * specified in <b>templateFile</b> and using its file structure. It preserves
     * the comments and keys order.<br>
     * 
     * @throws IllegalArgumentException if any of the
     *             {@link Bundle#saveToFile(File)} call throws
     *             IllegalArgumentException
     * @throws IOException if the any of the {@link Bundle#saveToFile(File)} call
     *             throws IOException
     * 
     * @see Bundle#saveToFile(File)
     */
    public void saveGroupAsPropertiesFiles(File templateFile) throws IOException {
        for (Language language : supportedLanguages()) {
            bundles.get(language).saveToFile(templateFile);
        }
    }

    /*
     * Private methods.
     */

    private void checkBundle(Bundle bundle) {
        Objects.requireNonNull(bundle, "The provided Bundles cannot be null");
        if (!defaultBundle.getName().equals(bundle.getName())) {
            throw new IllegalArgumentException(bundle + " is not a variant of " + defaultBundle);
        }
    }

    /*
     * Methods overridden from Object.
     */

    @Override
    public String toString() {
        return "BundleGroup [name=" + getName() + ", bundles=" + bundles + "]";
    }

}
