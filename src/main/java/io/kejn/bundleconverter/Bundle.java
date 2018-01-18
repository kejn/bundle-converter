package io.kejn.bundleconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.text.StringEscapeUtils;

import com.google.common.io.Files;

/**
 * Represents a file with {@value #FILE_EXTENSION} extension. It is
 * distinguished by file name.
 * 
 * @author kejn
 *
 */
public class Bundle implements Comparable<Bundle> {

    private static final String COMMENT_MARK = "#";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String UNDERSCORE = "_";
    private static final int UNDERSCORE_POSITION = 3;

    private final File file;

    private Properties properties;

    /*
     * API.
     */

    public static Bundle newExistingBundle(String filePath) {
        return new Bundle(new File(filePath));
    }

    public static Bundle newExistingBundle(File file) {
        return new Bundle(file);
    }

    public static Bundle newNotExistingBundle(String filePath, Properties properties) {
        return new Bundle(new File(filePath), properties);
    }

    public static Bundle newNotExistingBundle(File file, Properties properties) {
        return new Bundle(file, properties);
    }

    /**
     * Creates a new {@link Bundle} object using the specified {@link File} object.
     * 
     * @param file the file object that should hold a file with the '.properties'
     *            extension
     */
    public Bundle(File file) {
        if (!Bundles.fileExtensionIsValid(file)) {
            throw new IllegalArgumentException("Input file should have '.properties' extension");
        }
        this.file = file;
    }

    public Bundle(File file, Properties properties) {
        this(file);
        setProperties(properties);
    }

    public String getName() {
        return getNameWithVariants().split(UNDERSCORE)[0];
    }

    public String getNameWithVariants() {
        return Files.getNameWithoutExtension(file.getName());
    }

    public boolean isDefaultBundle() {
        int index = getNameWithVariants().indexOf(UNDERSCORE);
        int langUnderscoreIndex = getNameWithVariants().length() - UNDERSCORE_POSITION;
        return index < langUnderscoreIndex;
    }

    public Language getLanguage() {
        String[] nameWithVariants = getNameWithVariants().split(UNDERSCORE);
        String isoCode = nameWithVariants.length > 1 ? nameWithVariants[1] : "";
        return Language.forISOCode(isoCode);
    }

    public Properties getProperties() {
        if (properties == null && file.exists()) {
            try {
                properties = new Properties();
                properties.load(new FileInputStream(file));
            } catch (IOException e) {
                properties = null;
            }
        }
        return properties;
    }

    public void saveToFile(File templateFile) throws IOException {
        getProperties();
        if (properties == null) {
            throw new IllegalStateException("The Bundle points to a file which is not a .properties file");
        }

        FileWriter writer = new FileWriter(file);
        writer.write(formatProperties(templateFile));
        writer.close();
    }

    public void saveToFile() throws IOException {
        saveToFile(null);
    }

    /*
     * Private methods.
     */

    private String formatProperties(File templateFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line = null;

        if (templateFile == null) {
            for (String key : properties.stringPropertyNames()) {
                appendNextProperty(builder, key);
            }
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(templateFile));
            while ((line = reader.readLine()) != null) {
                appendNextProperty(builder, line);
            }
            reader.close();
        }
        return builder.toString();
    }

    private void appendNextProperty(StringBuilder builder, String keyOrPropertyString) {
        String line = keyOrPropertyString;
        if (!isCommentOrEmptyLine(line)) {
            line = translateProperty(line);
        }
        builder.append(StringEscapeUtils.escapeJava(line));
        builder.append(System.lineSeparator());
    }

    private boolean isCommentOrEmptyLine(String line) {
        return line.startsWith(COMMENT_MARK) || line.isEmpty();
    }

    private String translateProperty(String keyOrPropertyString) {
        StringTokenizer tokenizer = new StringTokenizer(keyOrPropertyString, KEY_VALUE_SEPARATOR);
        String key = tokenizer.nextToken().trim();
        String value = properties.getProperty(key);

        StringBuilder translatedProperty = new StringBuilder();
        if (value == null || value.isEmpty()) {
            translatedProperty.append(COMMENT_MARK);
        }
        translatedProperty.append(key);
        translatedProperty.append(KEY_VALUE_SEPARATOR);
        translatedProperty.append(value);
        return translatedProperty.toString();
    }

    private void setProperties(Properties properties) {
        this.properties = properties;
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
    public int compareTo(Bundle o) {
        if (o == null) {
            return -1;
        }
        return String.CASE_INSENSITIVE_ORDER.compare(this.getNameWithVariants(), o.getNameWithVariants());
    }

}
