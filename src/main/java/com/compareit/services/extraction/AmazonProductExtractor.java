package com.compareit.services.extraction;

import com.compareit.dto.EcommercePlatform;
import com.compareit.dto.ExtractedProduct;
import org.springframework.stereotype.Service;

@Service
public class AmazonProductExtractor implements ProductExtractor{
    @Override
    public ExtractedProduct extract(String productUrl) {
        ExtractedProduct product = new ExtractedProduct();
        product.setProductUrl(productUrl);
        product.setPlatform(EcommercePlatform.AMAZON);
        product.setBrand("Samsung");
        product.setModel("Galaxy S24");
        product.setPrice(42999.00);
        product.setRating(4.6);
        product.setTitle("Samsung Galaxy S24");
        return product;
    }

    @Override
    public EcommercePlatform getSupportedPlatform() {
        return EcommercePlatform.AMAZON;
    }
}
