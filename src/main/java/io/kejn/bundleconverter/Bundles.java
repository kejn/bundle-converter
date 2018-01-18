package io.kejn.bundleconverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.io.Files;

/**
 * Contains static methods
 * 
 * @author kejn
 */
public final class Bundles {

    private static final String FILE_EXTENSION = "properties";

    /**
     * Checks if the {@link Bundle}'s file extension is valid.
     * 
     * @param file the properties file to check
     * @return <code>true</code> if file extension equals (ignore case)
     *         {@link #FILE_EXTENSION}.
     */
    public static boolean fileExtensionIsValid(File file) {
	Objects.requireNonNull(file);
	return Files.getFileExtension(file.getName()).equalsIgnoreCase(FILE_EXTENSION);
    }

    public static File createFile(File directory, String bundleName, Language language) {
        return new File(createFileName(directory, bundleName, language));
    }

    public static String createFileName(File directory, String bundleName, Language language) {
	Objects.requireNonNull(directory);
	Objects.requireNonNull(bundleName);
	Objects.requireNonNull(language);

	if (!isExistingDirectory(directory)) {
	    throw new IllegalArgumentException("This file doesn't exist or it is not a directory: " + directory);
	}
	if (bundleName.trim().isEmpty()) {
	    throw new IllegalArgumentException("Bundle name cannot be empty");
	}

	StringBuilder sb = new StringBuilder();
	sb.append(directory.getAbsolutePath());
	sb.append(File.separator);
	sb.append(bundleName);
	if (Language.DEFAULT != language) {
	    sb.append("_");
	    sb.append(language.getIsoCode());
	}
	sb.append(".");
	sb.append(FILE_EXTENSION);
	return sb.toString();
    }

    private static boolean isExistingDirectory(File directory) {
	return directory.exists() && directory.isDirectory();
    }

    /**
     * Discovers all bundles in given directory and return them as a list of
     * {@link Bundle}s.
     * 
     * @param directory the directory where the discovery will be made
     * @return the list of all {@link Bundle}s found
     */
    public static List<Bundle> bundlesInDirectory(File directory) {
	Objects.requireNonNull(directory);

	if (!isExistingDirectory(directory)) {
	    return Collections.emptyList();
	}

	List<String> bundleNames = Arrays.asList(directory.list((dir, name) -> {
	    File f = new File(dir, name);
	    return !f.isDirectory() && fileExtensionIsValid(f);
	}));

	return bundleNames.stream().map(bundleName -> new Bundle(new File(directory, bundleName)))
		.collect(Collectors.toList());
    }

    /**
     * Discovers all bundles in given directory and return them as a list of
     * {@link BundleGroup}s.
     * 
     * @param directory the directory where the discovery will be made
     * @return the list of all {@link BundleGroup}s found
     */
    public static List<BundleGroup> groupsInDirectory(File directory) {
	Objects.requireNonNull(directory);

	List<Bundle> bundles = bundlesInDirectory(directory);

	Map<String, Set<Bundle>> groupsMap = new TreeMap<>();
	for (Bundle bundle : bundles) {
	    String name = bundle.getName();
	    Set<Bundle> set = groupsMap.get(name);
	    if (set == null) {
		set = new TreeSet<>();
		groupsMap.put(name, set);
	    }
	    set.add(bundle);
	}

	List<BundleGroup> groups = new ArrayList<>();
	for (Set<Bundle> set : groupsMap.values()) {
	    BundleGroup group = null;
	    for (Bundle bundle : set) {
		if (group == null) {
		    group = new BundleGroup(bundle);
		} else {
		    group.add(bundle);
		}
	    }
	    if (group != null) {
		groups.add(group);
	    }
	}
	return groups;
    }
}
