package io.kejn.bundleconverter;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link PropertiesFile} class.
 * 
 * @author kejn
 */
public class PropertiesFileTest {

	private static final String VALID_FILE_PATH = "src/test/resources/testdata/bundle.properties";

	private PropertiesFile propertiesFile;

	@Before
	public void setUp() {
		propertiesFile = null;
	}

	private void fileIsOK() {
		assertNotNull(propertiesFile);
		assertNotNull(propertiesFile.getFile());
	}

	/**
	 * All loaded files should have the ".properties" extension.
	 */
	@Test
	public void shouldAcceptOnlyPropertiesFilesValidFileName() {
		// given
		final File bundle = new File(VALID_FILE_PATH);

		// when
		propertiesFile = new PropertiesFile(bundle);

		// then
		fileIsOK();
	}

	/**
	 * Files with extensions other than '.properties' should not be accepted.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void shouldAcceptOnlyPropertiesFilesInvalidFileName() {
		// given
		final File invalidFile = new File("bundle.propertY");

		// when
		propertiesFile = new PropertiesFile(invalidFile);
	}

	/**
	 * File extension check should ignore case. Both lowercase and uppercase should
	 * be accepted. It means that the '.PROPERTIES' extension should be accepted as
	 * well as '.properties'.
	 */
	@Test
	public void fileExtensionCheckShouldBeCaseInsensitive() {
		// given
		final File validFile = new File(VALID_FILE_PATH.toUpperCase());

		// when
		propertiesFile = new PropertiesFile(validFile);

		// then
		fileIsOK();
	}

	/**
	 * The {@link File} passed as an argument to the {@link PropertiesFile}
	 * constructor must exist.
	 */
	@Test
	public void filePassedAsConstructorArgumentMustExist() {
		// given
		final File validFile = new File(VALID_FILE_PATH);

		// when
		propertiesFile = new PropertiesFile(validFile);

		// then
		assertTrue(validFile.exists());
		fileIsOK();
	}

}
