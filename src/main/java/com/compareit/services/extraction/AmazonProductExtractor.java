package com.compareit.services.extraction;

import com.compareit.dto.EcommercePlatform;
import com.compareit.dto.ExtractedProduct;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@Slf4j
public class AmazonProductExtractor implements ProductExtractor {

    @Override
    public ExtractedProduct extract(String productUrl) {

        log.info("Starting Amazon product extraction for URL: {}", productUrl);

        try {
            Document doc = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10_000)
                    .get();

            ExtractedProduct product = new ExtractedProduct();
            product.setPlatform(EcommercePlatform.AMAZON);
            product.setProductUrl(productUrl);

            product.setTitle(extractTitle(doc));
            log.debug("Amazon title extracted: {}", product.getTitle());


            if (product.getTitle() == null || product.getTitle().isBlank()) {
                log.error("Amazon title extraction failed for URL: {}", productUrl);
                throw new IllegalStateException(
                        "Failed to extract product title from Amazon page"
                );
            }

            product.setPrice(extractPrice(doc));
            log.debug("Amazon price extracted: {}", product.getPrice());

            product.setRating(extractRating(doc));
            log.debug("Amazon rating extracted: {}", product.getRating());

            deriveBrandAndModel(product);
            log.debug(
                    "Derived brand: {}, model: {}",
                    product.getBrand(),
                    product.getModel()
            );

            log.info("Amazon extraction successful for URL: {}", productUrl);
            return product;

        } catch (IOException e) {
            log.error("Amazon page fetch failed for URL: {}", productUrl, e);
            throw new RuntimeException("Failed to connect to Amazon", e);

        } catch (IllegalStateException e) {
            log.error("Amazon extraction logic failed for URL: {}", productUrl, e);
            throw e;
        }
    }



    @Override
    public EcommercePlatform getSupportedPlatform() {
        return EcommercePlatform.AMAZON;
    }

    private void deriveBrandAndModel(ExtractedProduct product) {

        if (product.getTitle() == null || product.getTitle().isBlank()) {
            product.setBrand("Unknown");
            product.setModel("Unknown");
            return;
        }

        String title = product.getTitle();

        // very simple heuristic for now
        // this WILL improve later
        String[] words = title.split(" ");

        product.setBrand(words[0]); // Samsung
        product.setModel(
                title.replace(words[0], "").trim()
        );
    }

    private String extractTitle(Document doc) {
        Element titleElement = doc.selectFirst("#productTitle");
        return titleElement != null ? titleElement.text().trim() : "Unknown";
    }

    private BigDecimal extractPrice(Document doc) {

        // Amazon shows price in multiple possible places
        String[] selectors = {
                "#priceblock_ourprice",
                "#priceblock_dealprice",
                ".a-price .a-offscreen"
        };

        for (String selector : selectors) {
            Element priceElement = doc.selectFirst(selector);
            if (priceElement != null) {
                String priceText = priceElement.text();

                // Example: ₹42,999.00
                priceText = priceText
                        .replace("₹", "")
                        .replace(",", "")
                        .trim();

                try {
                    return new BigDecimal(priceText);
                } catch (NumberFormatException e) {
                    // ignore and try next selector
                }
            }
        }

        return null; // price not found
    }

    private Double extractRating(Document doc) {

        // Example text: "4.6 out of 5 stars"
        Element ratingElement = doc.selectFirst("span.a-icon-alt");

        if (ratingElement != null) {
            String ratingText = ratingElement.text();

            try {
                return Double.parseDouble(ratingText.split(" ")[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null; // rating not found
    }




}



























//public class AmazonProductExtractor implements ProductExtractor{
//    @Override
//    public ExtractedProduct extract(String productUrl) {
//        ExtractedProduct product = new ExtractedProduct();
//        product.setProductUrl(productUrl);
//        product.setPlatform(EcommercePlatform.AMAZON);
//        product.setBrand("Samsung");
//        product.setModel("Galaxy S24");
//        product.setPrice(42999.00);
//        product.setRating(4.6);
//        product.setTitle("Samsung Galaxy S24");
//        return product;
//    }
//
//    @Override
//    public EcommercePlatform getSupportedPlatform() {
//        return EcommercePlatform.AMAZON;
//    }
//}
