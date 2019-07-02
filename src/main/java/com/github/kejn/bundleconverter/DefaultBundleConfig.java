package com.github.kejn.bundleconverter;

public class DefaultBundleConfig implements BundleConfig {
    @Override
    public String getCommentMark() {
        return "#";
    }

    @Override
    public String getKeyValueSeparator() {
        return "=";
    }

    @Override
    public String getVariantSeparator() {
        return "_";
    }

    @Override
    public int getLanguageIndex() {
        return 1;
    }

    @Override
    public int getCountryIndex() {
        return 2;
    }
}
