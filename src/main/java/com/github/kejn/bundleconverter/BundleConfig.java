package com.github.kejn.bundleconverter;

public interface BundleConfig {
    String getCommentMark();
    String getKeyValueSeparator();
    String getVariantSeparator();

    int getLanguageIndex();
    int getCountryIndex();

}
