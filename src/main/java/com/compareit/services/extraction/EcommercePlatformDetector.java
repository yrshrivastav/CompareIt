package com.compareit.services.extraction;

import com.compareit.dto.EcommercePlatform;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class EcommercePlatformDetector {

    public EcommercePlatform getEcommercePlatform(String productUrl) {

        if (productUrl == null || productUrl.isBlank()) {
            throw new IllegalArgumentException("Please make sure you entered a valid product URL");
        }

        String url = productUrl.toLowerCase();

        for (EcommercePlatform platform : EcommercePlatform.values()) {
            if (url.contains(platform.getDomain())) {
                return platform;
            }
        }

        throw new IllegalArgumentException("Sorry! We do not recognize this platform");
    }
}

