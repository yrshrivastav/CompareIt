package com.compareit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExtractedProduct {
    //private Long id;
    private String title;
    private String brand;
    private String model;
    private Double price;
    private Double rating;
    private EcommercePlatform platform;
    private String productUrl;
}
