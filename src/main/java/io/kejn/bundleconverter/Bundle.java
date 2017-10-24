package io.kejn.bundleconverter;

import java.io.File;

/**
 * Represents a file with {@value #FILE_EXTENSION} extension. It is
 * distinguished by file name.
 * 
 * @author kejn
 *
 */
public class Bundle {

    private static final String FILE_EXTENSION = ".properties";

    private File file;

    public Bundle(String path) {
	this(new File(path));
    }

    public Bundle(File file) {
	if (!fileExtensionIsValid(file)) {
	    throw new IllegalArgumentException("Input file should have '.properties' extension");
	}
	this.file = file;
    }

    private boolean fileExtensionIsValid(File file) {
	return file.getName().toLowerCase().endsWith(FILE_EXTENSION);
    }

    public File getFile() {
	return file;
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
	    if (file != null && file.getName().equals(other.getFile().getName())) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public String toString() {
	return "Bundle [" + file + "]";
    }
}
