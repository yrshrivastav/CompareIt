package com.compareit.dto;

public enum EcommercePlatform {

    AMAZON("amazon.in"),
    FLIPKART("flipkart.com"),
    MYNTRA("myntra.com"),
    AJIO("ajio.com"),
    CROMA("croma.com"),
    RELIANCE_DIGITAL("reliancedigital.in");

    private final String domain;

    EcommercePlatform(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }
}

