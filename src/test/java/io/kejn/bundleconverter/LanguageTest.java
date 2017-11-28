package io.kejn.bundleconverter;

import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

/**
 * Tests for {@link Language} enum.
 * 
 * @author kejn
 */
public class LanguageTest {

    /**
     * Verifies that the {@link Language} class supports all display languages (in
     * English) that are supported by the {@link Locale} class.
     */
    @Test
    public void shouldSupportAllDisplayLanguagesSupportedByLocaleClass() {
	// given
	final String[] localeISOLangs = Locale.getISOLanguages();

	// then
	for (final String isoCode : localeISOLangs) {
	    Language language = Language.forISOCode(isoCode);
	    assertNotNull(String.format("ISO code '%s' is not supported", isoCode), language);
	}
    }

}
