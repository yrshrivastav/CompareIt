package com.compareit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExtractedProduct {
    //private Long id;
    private String title;
    private String brand;
    private String model;
    private BigDecimal price;
    private Double rating;
    private EcommercePlatform platform;
    private String productUrl;
}
