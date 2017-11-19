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
public class Bundle {

    private static final String FILE_EXTENSION = "properties";
    private static final String UNDERSCORE = "_";

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
	if (!fileExtensionIsValid(file)) {
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
	return !getNameWithVariants().contains(UNDERSCORE);
    }

    public Language getLanguage() {
	String[] nameWithVariants = getNameWithVariants().split(UNDERSCORE);
	String language = nameWithVariants.length > 1 ? nameWithVariants[1] : "";
	return Language.forISOCode(language);
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
     * Private methods.
     */

    private boolean fileExtensionIsValid(File file) {
	return Files.getFileExtension(file.getName()).equalsIgnoreCase(FILE_EXTENSION);
    }

    /*
     * Methods overridden from Object.
     */

    @Override
    public int hashCode() {
	return file.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Bundle) {
	    Bundle other = (Bundle) obj;
	    if (getNameWithVariants().equals(other.getNameWithVariants())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public String toString() {
	return "Bundle[" + file + "]";
    }

}
