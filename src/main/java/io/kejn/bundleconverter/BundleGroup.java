package io.kejn.bundleconverter;

import java.util.HashSet;
import java.util.Set;

public class BundleGroup {

    private Set<Bundle> bundles = new HashSet<>();

    /**
     * Adds a bundle to this bundle group (if it is not already present).
     * 
     * @param bundle bundle to be added to this group
     * @return <tt>true</tt> if the bundle was added, <tt>false</tt> otherwise.
     * @see Set#add(Object)
     */
    public boolean add(Bundle bundle) {
	return bundles.add(bundle);
    }

    /**
     * @return the default {@link Bundle}
     */
    public Bundle getBundle() {
	return bundles.iterator().next();
    }

    public int size() {
	return bundles.size();
    }

}
