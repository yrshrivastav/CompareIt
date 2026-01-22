package com.compareit.app.services;

import com.compareit.app.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);

    List<ProductDto> getAllProducts();

    ProductDto getProductById(Long id);

    void deleteProduct(Long id);
}
