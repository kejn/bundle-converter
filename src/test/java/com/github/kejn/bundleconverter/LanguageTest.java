package com.github.kejn.bundleconverter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import com.github.kejn.bundleconverter.Language;

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
            Locale locale = new Locale(isoCode);
            assertNotNull(locale);

            String displayLang = locale.getDisplayLanguage(Locale.ROOT);
            Language language = Language.forDisplayLanguage(displayLang);
            assertNotNull(String.format("Display language '%s'(%s) is not supported", displayLang,
                    isoCode),
                    language);
        }
    }

    /**
     * For unknown display language <tt>null</tt> should be returned.
     */
    @Test
    public void shouldReturnNullForUnknownDisplayLanguage() {
        // given
        final String unknownDisplayLanguage = "polskii";

        // when
        Language language = Language.forDisplayLanguage(unknownDisplayLanguage);

        // then
        assertNull(language);
    }

    /**
     * Verifies that the {@link Language} class supports all ISO codes that are
     * supported by the {@link Locale} class.
     */
    @Test
    public void shouldSupportAllIsoCodesSupportedByLocaleClass() {
        // given
        final String[] localeISOLangs = Locale.getISOLanguages();

        // then
        for (final String isoCode : localeISOLangs) {
            Language language = Language.forIsoCode(isoCode);
            assertNotNull(String.format("ISO code '%s' is not supported", isoCode), language);
        }
    }

    /**
     * For unknown ISO code <tt>null</tt> should be returned.
     */
    @Test
    public void shouldReturnNullForUnknownISOCode() {
        // given
        final String unknownsIsoCode = "iso";

        // when
        Language language = Language.forIsoCode(unknownsIsoCode);

        // then
        assertNull(language);
    }

}
