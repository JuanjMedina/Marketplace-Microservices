package com.marketplace.productservice.service;

import com.marketplace.productservice.controller.dto.ApiResponseDTO;
import com.marketplace.productservice.entity.Product;

import java.util.List;
import java.util.Objects;

public interface IProductService {

    ApiResponseDTO<List<Product>>getAllProducts();

    ApiResponseDTO<Product> getProductById(String id);

    ApiResponseDTO<Product> createProduct(Product product);

    ApiResponseDTO<Product> updateProduct(String id, Product product);

    ApiResponseDTO<Void> deleteProduct(String id);
}
