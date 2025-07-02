package com.marketplace.productservice.controller.dto;

public record ProductDto(String name,
                         String description,
                         Double price,
                         Integer quantity) {

//    public ProductDto(String name, String description, String imageUrl, String sellerId, Double price, Integer quantity) {
//        this.name = name;
//        this.description = description;
//        this.sellerId = sellerId;
//        this.price = price;
//        this.quantity = quantity;
//    }
}
