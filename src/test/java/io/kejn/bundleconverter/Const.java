package io.kejn.bundleconverter;

import static java.lang.String.format;

/**
 * Constants shared in tests.
 * 
 * @author kejn
 */
final class Const {

    private static final String VALID_DIR_PATH = "src/test/resources/testdata";

    static final String VALID_FILE_PATH_DEFAULT_BUNDLE = format("%s/bundle.properties", VALID_DIR_PATH);
    static final String VALID_FILE_PATH_POLISH_BUNDLE = format("%s/bundle_pl.properties", VALID_DIR_PATH);

    static final String VALID_FILE_PATH_DEFAULT_BUNDLE_OTHER_LOCATION = format("%s/otherlocation/bundle.properties",
	    VALID_DIR_PATH);

    static final String VALID_FILE_PATH_DEFAULT_VALUES = format("%s/values.properties", VALID_DIR_PATH);
}
