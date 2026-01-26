package com.compareit.services.extraction;

import com.compareit.dto.EcommercePlatform;
import com.compareit.dto.ExtractedProduct;

public interface ProductExtractor {
    ExtractedProduct extract(String productUrl);
    EcommercePlatform getSupportedPlatform();
}
