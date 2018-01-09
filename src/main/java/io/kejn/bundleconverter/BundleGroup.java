package io.kejn.bundleconverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * A group of {@link Bundle} objects that refer to the same properties, but in
 * different language variants.
 * 
 * @author kejn
 */
public class BundleGroup {

    private final Bundle defaultBundle;

    private Map<Language, Bundle> bundles = new HashMap<>();

    /**
     * Creates a group of {@link Bundle}s and initializes it with a {@link Bundle}
     * containing the default values. If <b>otherBundleVariants</b> are specified
     * and they are simply other language variants of the <b>defaultBundle</b> they
     * are also added to the {@link #bundles} set and object is created
     * successfully.
     * 
     * @param defaultBundle the bundle containing default values
     * @param otherBundleVariants (optional) other bundles to be added to this group
     * 
     * @throws IllegalArgumentException if <b>otherBundleVariants</b> array contain
     *             at least one {@link Bundle} which is not a language variant of
     *             <b>defaultBundle</b>, but a completely different bundle than
     *             <b>defaultBundle</b>
     */
    public BundleGroup(Bundle defaultBundle, Bundle... otherBundleVariants) {
	Objects.requireNonNull(defaultBundle, "The default Bundle cannot be null");

	if (!defaultBundle.isDefaultBundle()) {
	    throw new IllegalArgumentException("The defaultBundle cannot be a language variant. "
		    + "It must be the bundle with the default values, not the translated values");
	}

	this.defaultBundle = defaultBundle;
	add(defaultBundle);

	for (Bundle b : Objects.requireNonNull(otherBundleVariants)) {
	    add(b);
	}
    }

    private void checkBundle(Bundle bundle) {
	Objects.requireNonNull(bundle, "The provided Bundles cannot be null");
	if (!defaultBundle.getName().equals(bundle.getName())) {
	    throw new IllegalArgumentException(bundle + " is not a variant of " + defaultBundle);
	}
    }

    /**
     * Adds a bundle to this bundle group (if it is not already present).
     * 
     * @param bundle bundle to be added to this group
     * @return <tt>true</tt> if the bundle was added, <tt>false</tt> otherwise.
     * @see Set#add(Object)
     * 
     * @throws IllegalArgumentException if <b>otherBundleVariants</b> array contain
     *             at least one {@link Bundle} which is not a language variant of
     *             <b>defaultBundle</b>, but a completely different bundle than
     *             <b>defaultBundle</b>
     */
    public boolean add(Bundle bundle) {
	checkBundle(bundle);
	return bundles.put(bundle.getLanguage(), bundle) == null;
    }

    /**
     * @return the default {@link Bundle}
     */
    public Bundle getDefaultBundle() {
	return defaultBundle;
    }

    public int size() {
	return bundles.size();
    }

    public boolean contains(Bundle bundle) {
	return getBundle(bundle.getLanguage()) != null;
    }

    public Bundle getBundle(Language language) {
	return bundles.get(language);
    }

    public String getName() {
	return defaultBundle.getName();
    }

    public Set<Language> supportedLanguages() {
	return bundles.keySet();
    }

    public Set<String> stringPropertyNames() {
	Properties properties = defaultBundle.getProperties();
	if (properties == null) {
	    return Collections.emptySet();
	}
	return properties.stringPropertyNames();
    }

    public String getProperty(String key, Language language) {
	Bundle bundle = getBundle(language);
	if (bundle == null) {
	    return null;
	}
	return bundle.getProperties().getProperty(key);
    }

    @Override
    public String toString() {
	return "BundleGroup [name=" + getName() + ", bundles=" + bundles + "]";
    }

}
