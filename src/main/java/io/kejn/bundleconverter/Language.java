package io.kejn.bundleconverter;

import static java.util.Arrays.stream;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The convenient mappings of ISO codes and display languages generated from
 * {@link java.util.Locale}.
 * <p>
 * There are some duplicates on <tt>"ISO code" &lt;-&gt; "display language"</tt>
 * mappings in {@link Locale}, so to keep also the compatibility with all ISO
 * codes, some enums are suffsixed with "_2" (as well as their
 * {@link #displayLanguage} value). Example: {@link #YIDDISH} and
 * {@link #YIDDISH_2}.
 * 
 * @author kejn
 */
public enum Language {
    ABKHAZIAN("ab", "Abkhazian"), //
    AFAR("aa", "Afar"), //
    AFRIKAANS("af", "Afrikaans"), //
    AKAN("ak", "Akan"), //
    ALBANIAN("sq", "Albanian"), //
    AMHARIC("am", "Amharic"), //
    ARABIC("ar", "Arabic"), //
    ARAGONESE("an", "Aragonese"), //
    ARMENIAN("hy", "Armenian"), //
    ASSAMESE("as", "Assamese"), //
    AVARIC("av", "Avaric"), //
    AVESTAN("ae", "Avestan"), //
    AYMARA("ay", "Aymara"), //
    AZERBAIJANI("az", "Azerbaijani"), //
    BAMBARA("bm", "Bambara"), //
    BASHKIR("ba", "Bashkir"), //
    BASQUE("eu", "Basque"), //
    BELARUSIAN("be", "Belarusian"), //
    BENGALI("bn", "Bengali"), //
    BIHARI("bh", "Bihari"), //
    BISLAMA("bi", "Bislama"), //
    BOSNIAN("bs", "Bosnian"), //
    BRETON("br", "Breton"), //
    BULGARIAN("bg", "Bulgarian"), //
    BURMESE("my", "Burmese"), //
    CATALAN("ca", "Catalan"), //
    CHAMORRO("ch", "Chamorro"), //
    CHECHEN("ce", "Chechen"), //
    CHINESE("zh", "Chinese"), //
    CHURCH_SLAVIC("cu", "Church Slavic"), //
    CHUVASH("cv", "Chuvash"), //
    CORNISH("kw", "Cornish"), //
    CORSICAN("co", "Corsican"), //
    CREE("cr", "Cree"), //
    CROATIAN("hr", "Croatian"), //
    CZECH("cs", "Czech"), //
    DANISH("da", "Danish"), //
    DEFAULT("", "Default"), //
    DIVEHI("dv", "Divehi"), //
    DUTCH("nl", "Dutch"), //
    DZONGKHA("dz", "Dzongkha"), //
    ENGLISH("en", "English"), //
    ESPERANTO("eo", "Esperanto"), //
    ESTONIAN("et", "Estonian"), //
    EWE("ee", "Ewe"), //
    FAROESE("fo", "Faroese"), //
    FIJIAN("fj", "Fijian"), //
    FINNISH("fi", "Finnish"), //
    FRENCH("fr", "French"), //
    FRISIAN("fy", "Frisian"), //
    FULAH("ff", "Fulah"), //
    GALLEGAN("gl", "Gallegan"), //
    GANDA("lg", "Ganda"), //
    GEORGIAN("ka", "Georgian"), //
    GERMAN("de", "German"), //
    GREEK("el", "Greek"), //
    GREENLANDIC("kl", "Greenlandic"), //
    GUARANI("gn", "Guarani"), //
    GUJARATI("gu", "Gujarati"), //
    HAITIAN("ht", "Haitian"), //
    HAUSA("ha", "Hausa"), //
    HEBREW("he", "Hebrew"), //
    HEBREW_2("iw", "Hebrew_2"), //
    HERERO("hz", "Herero"), //
    HINDI("hi", "Hindi"), //
    HIRI_MOTU("ho", "Hiri Motu"), //
    HUNGARIAN("hu", "Hungarian"), //
    ICELANDIC("is", "Icelandic"), //
    IDO("io", "Ido"), //
    IGBO("ig", "Igbo"), //
    INDONESIAN("id", "Indonesian"), //
    INDONESIAN_2("in", "Indonesian_2"), //
    INTERLINGUA("ia", "Interlingua"), //
    INTERLINGUE("ie", "Interlingue"), //
    INUKTITUT("iu", "Inuktitut"), //
    INUPIAQ("ik", "Inupiaq"), //
    IRISH("ga", "Irish"), //
    ITALIAN("it", "Italian"), //
    JAPANESE("ja", "Japanese"), //
    JAVANESE("jv", "Javanese"), //
    KANNADA("kn", "Kannada"), //
    KANURI("kr", "Kanuri"), //
    KASHMIRI("ks", "Kashmiri"), //
    KAZAKH("kk", "Kazakh"), //
    KHMER("km", "Khmer"), //
    KIKUYU("ki", "Kikuyu"), //
    KINYARWANDA("rw", "Kinyarwanda"), //
    KIRGHIZ("ky", "Kirghiz"), //
    KOMI("kv", "Komi"), //
    KONGO("kg", "Kongo"), //
    KOREAN("ko", "Korean"), //
    KURDISH("ku", "Kurdish"), //
    KWANYAMA("kj", "Kwanyama"), //
    LAO("lo", "Lao"), //
    LATIN("la", "Latin"), //
    LATVIAN("lv", "Latvian"), //
    LIMBURGISH("li", "Limburgish"), //
    LINGALA("ln", "Lingala"), //
    LITHUANIAN("lt", "Lithuanian"), //
    LUBA_KATANGA("lu", "Luba-Katanga"), //
    LUXEMBOURGISH("lb", "Luxembourgish"), //
    MACEDONIAN("mk", "Macedonian"), //
    MALAGASY("mg", "Malagasy"), //
    MALAY("ms", "Malay"), //
    MALAYALAM("ml", "Malayalam"), //
    MALTESE("mt", "Maltese"), //
    MANX("gv", "Manx"), //
    MAORI("mi", "Maori"), //
    MARATHI("mr", "Marathi"), //
    MARSHALLESE("mh", "Marshallese"), //
    MOLDAVIAN("mo", "Moldavian"), //
    MONGOLIAN("mn", "Mongolian"), //
    NAURU("na", "Nauru"), //
    NAVAJO("nv", "Navajo"), //
    NDONGA("ng", "Ndonga"), //
    NEPALI("ne", "Nepali"), //
    NORTH_NDEBELE("nd", "North Ndebele"), //
    NORTHERN_SAMI("se", "Northern Sami"), //
    NORWEGIAN("no", "Norwegian"), //
    NORWEGIAN_BOKMAL("nb", "Norwegian Bokmål"), //
    NORWEGIAN_NYNORSK("nn", "Norwegian Nynorsk"), //
    NYANJA("ny", "Nyanja"), //
    OCCITAN("oc", "Occitan"), //
    OJIBWA("oj", "Ojibwa"), //
    ORIYA("or", "Oriya"), //
    OROMO("om", "Oromo"), //
    OSSETIAN("os", "Ossetian"), //
    PALI("pi", "Pali"), //
    PANJABI("pa", "Panjabi"), //
    PERSIAN("fa", "Persian"), //
    POLISH("pl", "Polish"), //
    PORTUGUESE("pt", "Portuguese"), //
    PUSHTO("ps", "Pushto"), //
    QUECHUA("qu", "Quechua"), //
    RAETO_ROMANCE("rm", "Raeto-Romance"), //
    ROMANIAN("ro", "Romanian"), //
    RUNDI("rn", "Rundi"), //
    RUSSIAN("ru", "Russian"), //
    SAMOAN("sm", "Samoan"), //
    SANGO("sg", "Sango"), //
    SANSKRIT("sa", "Sanskrit"), //
    SARDINIAN("sc", "Sardinian"), //
    SCOTTISH_GAELIC("gd", "Scottish Gaelic"), //
    SERBIAN("sr", "Serbian"), //
    SHONA("sn", "Shona"), //
    SICHUAN_YI("ii", "Sichuan Yi"), //
    SINDHI("sd", "Sindhi"), //
    SINHALESE("si", "Sinhalese"), //
    SLOVAK("sk", "Slovak"), //
    SLOVENIAN("sl", "Slovenian"), //
    SOMALI("so", "Somali"), //
    SOUTH_NDEBELE("nr", "South Ndebele"), //
    SOUTHERN_SOTHO("st", "Southern Sotho"), //
    SPANISH("es", "Spanish"), //
    SUNDANESE("su", "Sundanese"), //
    SWAHILI("sw", "Swahili"), //
    SWATI("ss", "Swati"), //
    SWEDISH("sv", "Swedish"), //
    TAGALOG("tl", "Tagalog"), //
    TAHITIAN("ty", "Tahitian"), //
    TAJIK("tg", "Tajik"), //
    TAMIL("ta", "Tamil"), //
    TATAR("tt", "Tatar"), //
    TELUGU("te", "Telugu"), //
    THAI("th", "Thai"), //
    TIBETAN("bo", "Tibetan"), //
    TIGRINYA("ti", "Tigrinya"), //
    TONGA("to", "Tonga"), //
    TSONGA("ts", "Tsonga"), //
    TSWANA("tn", "Tswana"), //
    TURKISH("tr", "Turkish"), //
    TURKMEN("tk", "Turkmen"), //
    TWI("tw", "Twi"), //
    UIGHUR("ug", "Uighur"), //
    UKRAINIAN("uk", "Ukrainian"), //
    URDU("ur", "Urdu"), //
    UZBEK("uz", "Uzbek"), //
    VENDA("ve", "Venda"), //
    VIETNAMESE("vi", "Vietnamese"), //
    VOLAPUK("vo", "Volapük"), //
    WALLOON("wa", "Walloon"), //
    WELSH("cy", "Welsh"), //
    WOLOF("wo", "Wolof"), //
    XHOSA("xh", "Xhosa"), //
    YIDDISH("ji", "Yiddish"), //
    YIDDISH_2("yi", "Yiddish_2"), //
    YORUBA("yo", "Yoruba"), //
    ZHUANG("za", "Zhuang"), //
    ZULU("zu", "Zulu");

    private final String isoCode;

    private final String displayLanguage;

    private Language(String isoCode, String displayLanguage) {
        this.isoCode = isoCode;
        this.displayLanguage = displayLanguage;
    }

    /**
     * Returns a {@link Language} matching given ISO code. The check is case
     * insensitive.
     * 
     * @param isoCode the ISO code
     * @return a language matching given ISO code or <tt>null</tt> if no matching
     *         language could be found
     */
    public static Language forIsoCode(String isoCode) {
        for (Language language : values()) {
            if (language.isoCode.equalsIgnoreCase(isoCode)) {
                return language;
            }
        }
        return null;
    }

    /**
     * Returns a {@link Language} matching given display language. The check is case
     * insensitive.
     * 
     * @param displayLanguage the display language
     * @return a language matching given display language or <tt>null</tt> if no
     *         matching language could be found
     */
    public static Language forDisplayLanguage(String displayLanguage) {
        for (Language language : values()) {
            if (language.displayLanguage.equalsIgnoreCase(displayLanguage)) {
                return language;
            }
        }
        return null;
    }

    /**
     * Return the comma-separated list of the supported display languages that are
     * valid for usage with method {@link #forDisplayLanguage(String)}.
     * 
     * @return the string list of all {@link Language}s defined in this enum
     */
    public static String supportedDisplayLanguages() {
        return stream(values()).map(Language::getDisplayLanguage).collect(Collectors.joining(", "));
    }

    /**
     * Gets the display language value for this Language.
     * 
     * @return the display language
     */
    public String getDisplayLanguage() {
        return displayLanguage;
    }

    /**
     * Gets the ISO code value for this Language.
     * 
     * @return the ISO code
     */
    public String getIsoCode() {
        return isoCode;
    }

}