package com.compareit.services.extraction;

import com.compareit.dto.EcommercePlatform;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductExtractorFactory {

    private final Map<EcommercePlatform, ProductExtractor> extractorMap;

    public ProductExtractorFactory(List<ProductExtractor> extractors) {
        this.extractorMap = extractors.stream()
                .collect(Collectors.toMap(
                        ProductExtractor::getSupportedPlatform,
                        Function.identity()
                ));
    }

    public ProductExtractor getExtractor(EcommercePlatform platform) {
        ProductExtractor extractor = extractorMap.get(platform);
        if (extractor == null) {
            throw new IllegalArgumentException(
                    "We are working hard to serve you better!"
            );
        }
        return extractor;
    }
}

