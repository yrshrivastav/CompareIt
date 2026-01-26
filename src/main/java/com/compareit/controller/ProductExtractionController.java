package com.compareit.controller;


import com.compareit.dto.EcommercePlatform;
import com.compareit.dto.ExtractedProduct;
import com.compareit.services.extraction.EcommercePlatformDetector;
import com.compareit.services.extraction.ProductExtractor;
import com.compareit.services.extraction.ProductExtractorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductExtractionController {

    private final EcommercePlatformDetector ecommercePlatformDetector;
    private final ProductExtractorFactory extractorFactory;

    @PostMapping("/extract")
    public ResponseEntity<?> extract(@RequestBody String productUrl) {
        try {
            EcommercePlatform platform = ecommercePlatformDetector.getEcommercePlatform(productUrl);

            ProductExtractor extractor = extractorFactory.getExtractor(platform);

            ExtractedProduct extractedProduct = extractor.extract(productUrl);

            return ResponseEntity.ok(extractedProduct);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
