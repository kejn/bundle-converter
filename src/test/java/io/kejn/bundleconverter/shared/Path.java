package io.kejn.bundleconverter.shared;

import static java.lang.String.format;

/**
 * File paths shared in tests.
 * 
 * @author kejn
 */
public final class Path {

    public static final String DIR_PATH = "src/test/resources/testdata";

    public static final String UPPERCASE_BUNDLE = format("%s/bundleUppercase.PROPERTIES", DIR_PATH);
    public static final String DEFAULT_BUNDLE = format("%s/bundle.properties", DIR_PATH);
    public static final String POLISH_BUNDLE = format("%s/bundle_pl.properties", DIR_PATH);

    public static final String DEFAULT_BUNDLE_OTHER_LOCATION = format(
            "%s/otherlocation/bundle.properties", DIR_PATH);

    public static final String DEFAULT_VALUES = format("%s/values.properties", DIR_PATH);
    public static final String GERMAN_VALUES = format("%s/values_de.properties", DIR_PATH);
}
