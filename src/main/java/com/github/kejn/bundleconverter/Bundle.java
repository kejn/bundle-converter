package com.github.kejn.bundleconverter;

import com.google.common.io.Files;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents a file with '.properties' extension. It is distinguished by the
 * file name.
 * <p>
 * It uses alpha-2 ISO-code suffix in a file name, to determine the
 * {@link Language} of the translation. For example, "bundle.properties" will be
 * detected as a "default" bundle, but "bundle_es.properties" will as a Spanish
 * translation of some "bundle.properties" file. Different translations of the
 * same bundle can be wrapped and managed using the {@link BundleGroup} object.
 * <p>
 * The Bundle object can be created in two ways:
 * <ol>
 * <li>Using an existing File, from which the Properties can be later lazily
 * loaded. This should be used, when you need to use some properties from given
 * file in application runtime.<br>
 * <li>Using a specified File, to which you can later save the Properties, and
 * the specified Properties object. This should be used, when you want to easily
 * write to file the properties that you created at application runtime.
 * </ol>
 * Some helpful methods for {@link Bundle} creation could be also found in
 * {@link Bundles} class.
 * <p>
 * <b><u>EXAMPLES</u></b>
 * <p>
 * GETTING PROPERTIES USING AN EXISTING FILE
 * <hr>
 * <blockquote>
 *
 * <pre>
 * File file = new File("path/to/file.properties");
 * Bundle bundle = new Bundle(file);
 * Properties prop = bundle.getProperties();
 * </pre>
 *
 * </blockquote>
 * <p>
 * SAVING PROPERTIES CREATED AT RUNTIME TO A NEW FILE
 * <hr>
 * <blockquote>
 *
 * <pre>
 * Properties prop = new Properties();
 * prop.setProperty("key1", "value1");
 * [...]
 * File file = new File("path/to/new/file.properties");
 * Bundle bundle = new Bundle(file, prop);
 * bundle.saveToFile();
 * </pre>
 *
 * </blockquote>
 * <p>
 * UPDATING OR ADDING NEW PROPERTIES USING AN EXISTING FILE
 * <hr>
 * <blockquote>
 *
 * <pre>
 * File file = new File("path/to/file.properties");
 * Bundle bundle = new Bundle(file);
 * Properties prop = bundle.getProperties();
 * prop.setProperties("old.key", "updated value");
 * prop.setProperties("new.key", "new value");
 * [...]
 * bundle.saveToFile(); // warning! you will overwrite the old file here!
 * </pre>
 *
 * </blockquote>
 *
 * @author kejn
 *
 * @see Bundles
 * @see BundleGroup
 * @see Language
 */
public class Bundle implements Comparable<Bundle> {

    private final File file;
    private final BundleConfig config;

    private final String baseName;
    private final Language language;
    private final String country;

    private Properties properties;

    /*
     * API.
     */

    /**
     * Creates a new {@link Bundle} object using the properties from the specified
     * <b>file</b>.
     *
     * @param file the file that should have the '.properties' extension
     *
     * @throws IllegalArgumentException if the <b>file</b> does not have the
     *             '.properties' extension
     */
    public Bundle(File file) {
        this(file, new Properties());
    }

    /**
     * Creates a new {@link Bundle} with a handle to given <b>file</b> using the
     * provided <b>properties</b>.
     *
     * @param file the file that should have the '.properties' extension
     * @param properties CANNOT BE NULL; the initial properties values
     *
     * @throws IllegalArgumentException if the <b>file</b> does not have the
     *             '.properties' extension
     * @throws NullPointerException if the <b>properties</b> argument is null
     */
    public Bundle(File file, Properties properties) {
        this(file, properties, new DefaultBundleConfig());
    }

    public Bundle(File file, Properties properties, BundleConfig config) {
        if (!Bundles.fileExtensionIsValid(file)) {
            throw new IllegalArgumentException("Input file should have '.properties' extension");
        }
        this.file = file;
        this.properties = Objects.requireNonNull(properties);
        this.config = config;

        String fullName = getNameWithVariants();
        List<String> variantsList = Bundles.getVariantsList(fullName, config);
        this.baseName = Bundles.detectBaseName(fullName, config);
        this.language = Language.forIsoCode(CollectionUtil.getOrDefault(variantsList, config.getLanguageIndex(), ""));
        this.country = CollectionUtil.getOrDefault(variantsList, config.getCountryIndex(), "");
    }

    public BundleConfig getConfig() {
        return config;
    }

    /**
     * Returns the name of this bundle without the language ISO code. For example,
     * if the file name of the {@link #file} is "bundle_es.properties", then this
     * method will return only "bundle" (the file extension and the language ISO
     * code is skipped).
     *
     * @return the name of this bundle without the extension and language ISO code.
     *
     * @see #getNameWithVariants()
     */
    public String getName() {
        return baseName;
    }

    /**
     * Returns the name of this bundle including the language ISO code (and other variants if present).
     * It is simply the file name without extension. For example, if the file name of the
     * {@link #file} is "bundle_es.properties", then this method will return only
     * "bundle_es" (the file extension is skipped).
     *
     * @return the name of this bundle including the language ISO code
     */
    public String getNameWithVariants() {
        return Files.getNameWithoutExtension(file.getName());
    }

    /**
     * Checks if this object is a "default" language variant of given bundle.
     *
     * @return <code>true</code> if this is a "default" language variant of given
     *         bundle
     *
     * @see #getLanguage()
     */
    public boolean isDefaultBundle() {
        return getNameWithVariants().equals(getName());
    }

    /**
     * @return the {@link Language} of this bundle using the ISO code in the
     *         {@link #file} name.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * @return the country ISO code of this bundle determined from the {@link #file} name.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Return the {@link #properties} of this bundle. It attempts to load the
     * properties from {@link #file} if they were not loaded yet (or initialized
     * with a proper constructor).
     *
     * @return the {@link #properties} of this bundle (CAN BE NULL)
     */
    public Properties getProperties() {
        if (properties.isEmpty() && file.exists()) {
            try {
                properties.load(new FileInputStream(file));
            } catch (IOException e) {
            }
        }
        return properties;
    }

    /**
     * Saves to {@link #file} the {@link #properties} of this object matching
     * property keys specified in <b>templateFile</b> and using its file structure.
     * It preserves the comments and keys order.<br>
     * <br>
     * <b>Note:</b> If the {@link #properties} contain some keys not included in the
     * <b>templateFile</b>, then the result file will be missing these extra
     * properties.
     *
     * @param templateFile the template file
     *
     * @throws IllegalStateException if the {@link #file} points to a file, which is
     *             not a '.properties' file.
     * @throws IOException if the {@link #file} exists but is a directory rather
     *             than a regular file, does not exist but cannot be created, or
     *             cannot be opened for any other reason
     */
    public void saveToFile(File templateFile) throws IOException {
        getProperties();
        if (properties == null) {
            throw new IllegalStateException(
                    "The Bundle points to a file which is not a '.properties' file");
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(formatProperties(templateFile));
        }
    }

    /**
     * Saves all {@link #properties} of this object to {@link #file}.
     *
     * @throws IllegalStateException if the {@link #file} points to a file, which is
     *             not a '.properties' file.
     * @throws IOException if the {@link #file} exists but is a directory rather
     *             than a regular file, does not exist but cannot be created, or
     *             cannot be opened for any other reason
     */
    public void saveToFile() throws IOException {
        saveToFile(null);
    }

    /*
     * Private methods.
     */

    private String formatProperties(File templateFile) throws IOException {
        StringBuilder builder = new StringBuilder();

        Collection<String> keysOrPropertyStrings = properties.stringPropertyNames();
        if (templateFile != null) {
            keysOrPropertyStrings = getPropertyStringList(templateFile);
        }
        for (String keyOrPropertyString : keysOrPropertyStrings) {
            appendNextProperty(builder, keyOrPropertyString);
        }
        return builder.toString();
    }

    private List<String> getPropertyStringList(File templateFile) throws IOException {
        Objects.requireNonNull(templateFile);

        List<String> keysOrPropertyStrings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(templateFile))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                keysOrPropertyStrings.add(line);
            }
        }
        return keysOrPropertyStrings;
    }

    private void appendNextProperty(StringBuilder builder, String keyOrPropertyString) {
        Objects.requireNonNull(builder);
        Objects.requireNonNull(keyOrPropertyString);

        String line = keyOrPropertyString;
        if (!isCommentOrEmptyLine(line)) {
            line = translateProperty(line);
        }
        builder.append(StringEscapeUtils.escapeJava(line));
        builder.append(System.lineSeparator());
    }

    private boolean isCommentOrEmptyLine(String line) {
        return line.startsWith(config.getCommentMark()) || line.isEmpty();
    }

    private String translateProperty(String keyOrPropertyString) {
        StringTokenizer tokenizer = new StringTokenizer(keyOrPropertyString, config.getKeyValueSeparator());
        String key = tokenizer.nextToken().trim();
        String value = properties.getProperty(key);

        StringBuilder translatedProperty = new StringBuilder();
        if (value == null || value.isEmpty()) {
            translatedProperty.append(config.getCommentMark());
        }
        translatedProperty.append(key);
        translatedProperty.append(config.getKeyValueSeparator());
        translatedProperty.append(value);
        return translatedProperty.toString();
    }

    /*
     * Methods overridden from Object.
     */

    @Override
    public int hashCode() {
        return getNameWithVariants().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bundle) {
            Bundle other = (Bundle) obj;
            return compareTo(other) == 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Bundle[" + file + "]";
    }

    /*
     * Comparable interface implementation.
     */

    @Override
    public int compareTo(Bundle other) {
        if (other == null) {
            return -1;
        }
        return String.CASE_INSENSITIVE_ORDER.compare(this.getNameWithVariants(), other
                .getNameWithVariants());
    }

}
