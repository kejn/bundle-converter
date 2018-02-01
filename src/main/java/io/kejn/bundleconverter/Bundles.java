package io.kejn.bundleconverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.io.Files;

/**
 * Contains static methods that can be helpful when using or creating the
 * {@link Bundle} or {@link BundleGroup} objects.
 * 
 * @author kejn
 * 
 * @see Bundle
 * @see BundleGroup
 */
public final class Bundles {

    private Bundles() {
    }

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

    /**
     * Creates a valid {@link File} object that can be used as constructor argument
     * when creating the {@link Bundle}. The result file has a file path which
     * points to given <b>directory</b> with a '.properties' file with name equal to
     * <b>bundleName</b> suffixed with the ISO code of given <b>language</b>.
     * 
     * @param directory the target directory
     * @param bundleName the bundle name
     * @param language the language to find out the ISO code suffix
     * @return a valid {@link File} object that can be used as constructor argument
     *         when creating the {@link Bundle}
     */
    public static File createFile(File directory, String bundleName, Language language) {
        return new File(createFileName(directory, bundleName, language));
    }

    /**
     * Creates a valid file path that can be used for the {@link File} used as
     * constructor argument when creating the {@link Bundle}. The result file path
     * points to given <b>directory</b> with a '.properties' file with name equal to
     * <b>bundleName</b> suffixed with the ISO code of given <b>language</b>.
     * 
     * @param directory the target directory
     * @param bundleName the bundle name
     * @param language the language to find out the ISO code suffix
     * @return a valid {@link File} object that can be used as constructor argument
     *         when creating the {@link Bundle}
     */
    public static String createFileName(File directory, String bundleName, Language language) {
        Objects.requireNonNull(directory);
        Objects.requireNonNull(bundleName);
        Objects.requireNonNull(language);

        if (!isExistingDirectory(directory)) {
            throw new IllegalArgumentException("This file doesn't exist or it is not a directory: "
                    + directory);
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

        List<BundleGroup> groups = new ArrayList<>();

        List<Bundle> bundles = bundlesInDirectory(directory);
        Map<String, Set<Bundle>> groupsMap = listToGroupsMap(bundles);

        for (Set<Bundle> set : groupsMap.values()) {
            BundleGroup group = newBundleGroup(set);
            groups.add(group);
        }
        return groups;
    }

    private static Map<String, Set<Bundle>> listToGroupsMap(List<Bundle> bundles) {
        Map<String, Set<Bundle>> groupsMap = new TreeMap<>();
        for (Bundle bundle : bundles) {
            String name = bundle.getName();
            Set<Bundle> set = groupsMap.computeIfAbsent(name, key -> new TreeSet<>());
            set.add(bundle);
        }
        return groupsMap;
    }

    /**
     * Returns first found "default" bundle in given collection.
     * 
     * @param bundles the collection to look for "default" bundle
     * @return first found "default" bundle in given collection, or <tt>null</tt> if
     *         there is no "default" in given <b>bundles</b> collection
     */
    public static Bundle defaultBundleIn(Collection<Bundle> bundles) {
        for (Bundle bundle : bundles) {
            if (bundle.isDefaultBundle()) {
                return bundle;
            }
        }
        return null;
    }

    /**
     * Creates a {@link BundleGroup} using the provided <b>bundles</b>. The
     * <b>bundles</b> collection must contain a "default" bundle and all other
     * bundles must be other language variants of that "default" bundle.
     * 
     * @param bundles the collection to create a group
     * 
     * @throws IllegalArgumentException if the <b>bundles</b> collection does not
     *             contain the default bundle, or if <b>bundles</b> collection
     *             contain at least one {@link Bundle} which is not a language
     *             variant of the default bundle, but a completely different bundle
     *             than default bundle
     * 
     * @return a {@link BundleGroup} created using the provided <b>bundles</b>
     */
    public static BundleGroup newBundleGroup(Collection<Bundle> bundles) {
        Bundle defaultBundle = defaultBundleIn(bundles);
        if (defaultBundle == null) {
            throw new IllegalArgumentException("Bundles collection must contain a default bundle");
        }
        bundles.remove(defaultBundle);
        return new BundleGroup(defaultBundle, bundles);
    }

    /**
     * Creates a new {@link Bundle} object using a <b>filePath</b> to an existing
     * '.properties' file.
     * 
     * @param filePath the path to an existing '.properties' file
     * 
     * @return the {@link Bundle} object referencing an existing '.properties' file
     */
    public static Bundle newExistingBundle(String filePath) {
        return new Bundle(new File(filePath));
    }

    /**
     * Creates a new {@link Bundle} object using an existing '.properties'
     * <b>file</b>.
     * 
     * @param file an existing '.properties' file
     * 
     * @return the {@link Bundle} object referencing an existing '.properties' file
     */
    public static Bundle newExistingBundle(File file) {
        return new Bundle(file);
    }

    /**
     * Creates a new {@link Bundle} object using a <b>filePath</b> to a
     * not-yet-existing '.properties' file and initializes it with given
     * <b>properties</b>.
     * 
     * @param filePath the path to a not-yet-existing '.properties' file
     * @param properties CANNOT BE NULL; the initial properties values
     * 
     * @return the {@link Bundle} object referencing a not-yet-existing
     *         '.properties' file
     */
    public static Bundle newNotExistingBundle(String filePath, Properties properties) {
        return new Bundle(new File(filePath), properties);
    }

    /**
     * Creates a new {@link Bundle} object using a not-yet-existing '.properties'
     * <b>file</b> and initializes it with given <b>properties</b>.
     * 
     * @param file a not-yet-existing '.properties' file
     * @param properties CANNOT BE NULL; the initial properties values
     * 
     * @return the {@link Bundle} object referencing a not-yet-existing
     *         '.properties' file
     */
    public static Bundle newNotExistingBundle(File file, Properties properties) {
        return new Bundle(file, properties);
    }
}
