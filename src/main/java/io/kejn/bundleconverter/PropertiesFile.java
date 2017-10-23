package io.kejn.bundleconverter;

import java.io.File;

/**
 * Represents a file with {@value #FILE_EXTENSION} extension.
 * 
 * @author kejn
 *
 */
public class PropertiesFile {

	private static final String FILE_EXTENSION = ".properties";

	private File file;

	public PropertiesFile(File file) {
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

}
