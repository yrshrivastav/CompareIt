package com.compareit.services.extraction;

import com.compareit.dto.EcommercePlatform;
import com.compareit.dto.ExtractedProduct;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

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
                    .timeout(10_000)
                    .get();

            ExtractedProduct product = new ExtractedProduct();
            product.setPlatform(EcommercePlatform.FLIPKART);
            product.setProductUrl(productUrl);

            product.setTitle(extractTitle(doc));
            log.debug("Flipkart title extracted: {}", product.getTitle());


            if (product.getTitle() == null || product.getTitle().isBlank()) {
                log.error("Flipkart title extraction failed for URL: {}", productUrl);
                throw new IllegalStateException("Failed to extract product title from Flipkart");
            }

            product.setPrice(extractPrice(doc));
            log.debug("Flipkart price extracted: {}", product.getPrice());

            product.setRating(extractRating(doc));
            log.debug("Flipkart rating extracted: {}", product.getRating());

            // Price & Rating using Selenium
            product.setPrice(fetchPriceWithSelenium(productUrl));
            log.debug("Flipkart price extracted: {}", product.getPrice());

            product.setRating(fetchRatingWithSelenium(productUrl));
            log.debug("Flipkart rating extracted: {}", product.getRating());

            deriveBrandAndModel(product);
            log.debug("Derived brand: {}, model: {}",
                    product.getBrand(), product.getModel());

            log.info("Flipkart extraction successful for URL: {}", productUrl);
            return product;

        } catch (IOException e) {
            log.error("Flipkart page fetch failed for URL: {}", productUrl, e);
            throw new RuntimeException("Failed to connect to Flipkart", e);
        }
    }

    private void deriveBrandAndModel(ExtractedProduct product) {

        String title = product.getTitle();

        if (title == null || title.isBlank()) {
            return;
        }

        // Normalize spaces
        title = title.trim().replaceAll("\\s+", " ");

        String[] tokens = title.split(" ");

        if (tokens.length == 1) {
            // Single word title
            product.setBrand(tokens[0]);
            product.setModel("");
            return;
        }

        // Heuristic:
        // Brand = first word
        // Model = rest
        product.setBrand(tokens[0]);
        product.setModel(title.substring(tokens[0].length()).trim());
    }


    private String extractTitle(Document doc) {

        String[] selectors = {
                "span.B_NuCI",           // mobiles
                "h1 span",               // generic products
                "h1._6EBuvT",             // some electronics
                "h1.yhB1nd"               // fallback
        };

        for (String selector : selectors) {
            Element titleEl = doc.selectFirst(selector);
            if (titleEl != null && !titleEl.text().isBlank()) {
                return titleEl.text().trim();
            }
        }

        return null;
    }


    private BigDecimal extractPrice(Document doc) {

        String[] selectors = {
                "div._30jeq3._16Jk6d",   // mobiles
                "div.Nx9bqj",            // electronics & accessories
                "div.CxhGGd"             // fallback
        };

        for (String selector : selectors) {
            Element priceEl = doc.selectFirst(selector);
            if (priceEl != null) {
                String priceText = priceEl.text()
                        .replace("₹", "")
                        .replace(",", "")
                        .trim();
                try {
                    return new BigDecimal(priceText);
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    /**
     * Selenium + WebDriverManager-based price extraction with fallback
     */
    private BigDecimal fetchPriceWithSelenium(String url) {
        // Setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        // Headless Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(url);

            WebElement priceEl = driver.findElement(By.cssSelector("._30jeq3"));
            String priceText = priceEl.getText().replace("₹", "").replace(",", "").trim();
            return new BigDecimal(priceText);
        } catch (Exception e) {
            log.warn("Selenium price extraction failed, using default price", e);
            return null;
        } finally {
            driver.quit();
        }
    }

    private Double extractRating(Document doc) {

        String[] selectors = {
                "div._3LWZlK",   // mobiles
                "div.XQDdHH"     // accessories
        };

        for (String selector : selectors) {
            Element ratingEl = doc.selectFirst(selector);
            if (ratingEl != null) {
                try {
                    return Double.parseDouble(ratingEl.text().trim());
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }

    /**
     * Selenium + WebDriverManager-based rating extraction with fallback
     */
    private Double fetchRatingWithSelenium(String url) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(url);

            WebElement ratingEl = driver.findElement(By.cssSelector("._3LWZlK"));
            return Double.parseDouble(ratingEl.getText().trim());
        } catch (Exception e) {
            log.warn("Selenium rating extraction failed, using default rating", e);
            return null;
        } finally {
            driver.quit();
        }
    }


}

