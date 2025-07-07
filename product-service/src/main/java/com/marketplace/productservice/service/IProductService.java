package com.marketplace.productservice.service;

import com.marketplace.productservice.controller.dto.ApiResponseDTO;
import com.marketplace.productservice.controller.dto.ProductFilterCriteria;
import com.marketplace.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

public interface IProductService {

    ApiResponseDTO<Page<Product>> getAllProducts(ProductFilterCriteria filter, Pageable pagge);

    ApiResponseDTO<Product> getProductById(String id);

    ApiResponseDTO<Product> createProduct(Product product);

    ApiResponseDTO<Product> updateProduct(String id, Product product);

    ApiResponseDTO<Void> deleteProduct(String id);

    ApiResponseDTO<Product> updatePartialProduct(String id, Product product);
}
