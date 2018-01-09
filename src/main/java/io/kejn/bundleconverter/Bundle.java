package io.kejn.bundleconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.common.io.Files;

/**
 * Represents a file with {@value #FILE_EXTENSION} extension. It is
 * distinguished by file name.
 * 
 * @author kejn
 *
 */
public class Bundle implements Comparable<Bundle> {

    private static final String UNDERSCORE = "_";
    private static final int UNDERSCORE_POSITION = 3;

    private final File file;

    private Properties properties;

    /*
     * API.
     */

    /**
     * Creates a new {@link Bundle} object using the specified path to the
     * properties file.
     * 
     * @param filePath the path to the file with '.properties' extension
     */
    public Bundle(String filePath) {
	this(new File(filePath));
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
	return String.CASE_INSENSITIVE_ORDER.compare(this.getNameWithVariants(), o.getNameWithVariants());
    }

}
