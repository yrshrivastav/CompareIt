package com.compareit.services.extraction;

import com.compareit.dto.EcommercePlatform;
import com.compareit.dto.ExtractedProduct;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class FlipkartProductExtractor implements ProductExtractor {

    @Override
    public EcommercePlatform getSupportedPlatform() {
        return EcommercePlatform.FLIPKART;
    }

    @Override
    public ExtractedProduct extract(String productUrl) {

        log.info("Starting Flipkart product extraction for URL: {}", productUrl);

        try {
            Document doc = Jsoup.connect(productUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Accept-Language", "en-IN,en;q=0.9")
                    .timeout(15_000)
                    .get();

            ExtractedProduct product = new ExtractedProduct();
            product.setPlatform(EcommercePlatform.FLIPKART);
            product.setProductUrl(productUrl);

            // ---------------- TITLE ----------------
            product.setTitle(extractTitle(doc));
            if (product.getTitle() == null || product.getTitle().isBlank()) {
                throw new IllegalStateException("Flipkart title not found");
            }

            // ---------------- PRICE ----------------
            BigDecimal price = extractPriceFromJson(doc);
            if (price == null) {
                price = extractPriceFromDom(doc);
            }
            product.setPrice(price);

            // ---------------- RATING (STRICT) ----------------
            product.setRating(extractProductRatingFromJson(doc));

            // ---------------- BRAND & MODEL ----------------
            deriveBrandAndModel(product);

            log.info("Flipkart extraction successful for URL: {}", productUrl);
            return product;

        } catch (IOException e) {
            log.error("Flipkart fetch failed", e);
            throw new RuntimeException("Flipkart connection failed", e);
        }
    }

    // ---------------------------------------------------------------------
    // TITLE
    // ---------------------------------------------------------------------

    private String extractTitle(Document doc) {
        String[] selectors = {
                "span.B_NuCI",
                "h1 span"
        };

        for (String selector : selectors) {
            Element el = doc.selectFirst(selector);
            if (el != null && !el.text().isBlank()) {
                return el.text().trim();
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------
    // PRICE (JSON FIRST)
    // ---------------------------------------------------------------------

    private BigDecimal extractPriceFromJson(Document doc) {

        String json = extractEmbeddedJson(doc);
        if (json == null) return null;

        Pattern pattern = Pattern.compile(
                "\"(sellingPrice|price|finalPrice|discountedPrice)\"[^}]*?(\\d{3,7})"
        );

        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return new BigDecimal(matcher.group(2));
        }
        return null;
    }

    // ---------------------------------------------------------------------
    // PRICE (DOM FALLBACK)
    // ---------------------------------------------------------------------

    private BigDecimal extractPriceFromDom(Document doc) {

        String[] selectors = {
                "div._30jeq3._16Jk6d",
                "div._30jeq3",
                "div.Nx9bqj"
        };

        for (String selector : selectors) {
            Element el = doc.selectFirst(selector);
            if (el != null) {
                String text = el.text().replaceAll("[₹,]", "").trim();
                try {
                    return new BigDecimal(text);
                } catch (NumberFormatException ignored) {}
            }
        }

        log.warn("Flipkart price not found in DOM");
        return null;
    }

    // ---------------------------------------------------------------------
    // PRODUCT RATING (JSON ONLY – STRICT)
    // ---------------------------------------------------------------------

    private Double extractProductRatingFromJson(Document doc) {

        String json = extractEmbeddedJson(doc);
        if (json == null) {
            log.info("No embedded JSON found for rating");
            return null;
        }

        // Schema.org aggregateRating (MOST TRUSTED)
        Pattern schemaPattern = Pattern.compile(
                "\"aggregateRating\"\\s*:\\s*\\{[^}]*?\"ratingValue\"\\s*:\\s*(\\d+(?:\\.\\d+)?)",
                Pattern.DOTALL
        );

        Matcher schemaMatcher = schemaPattern.matcher(json);
        if (schemaMatcher.find()) {
            return Double.parseDouble(schemaMatcher.group(1));
        }

        // Product JSON averageRating (PID-based)
        Pattern productPattern = Pattern.compile(
                "\"averageRating\"\\s*:\\s*(\\d+(?:\\.\\d+)?)"
        );

        Matcher productMatcher = productPattern.matcher(json);
        if (productMatcher.find()) {
            return Double.parseDouble(productMatcher.group(1));
        }

        log.info("Product rating not verifiable — returning null");
        return null;
    }

    // ---------------------------------------------------------------------
    // EMBEDDED JSON
    // ---------------------------------------------------------------------

    private String extractEmbeddedJson(Document doc) {
        for (Element script : doc.select("script")) {
            String data = script.data();
            if (data.contains("aggregateRating")
                    || data.contains("averageRating")
                    || data.contains("sellingPrice")) {
                return data;
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------
    // BRAND & MODEL
    // ---------------------------------------------------------------------

    private void deriveBrandAndModel(ExtractedProduct product) {
        String title = product.getTitle();
        if (title == null) return;

        String[] tokens = title.split(" ");
        product.setBrand(tokens[0]);
        product.setModel(title.substring(tokens[0].length()).trim());
    }
}
