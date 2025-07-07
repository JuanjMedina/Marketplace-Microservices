package com.marketplace.productservice.controller.dto;

import java.math.BigDecimal;

public record ProductFilterCriteria(
        String category,
        Boolean inStock,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
