package com.marketplace.productservice.controller;

import com.marketplace.productservice.controller.dto.ApiResponseDTO;
import com.marketplace.productservice.controller.dto.ProductDto;
import com.marketplace.productservice.controller.dto.ProductFilterCriteria;
import com.marketplace.productservice.controller.dto.ProductUpdateDto;
import com.marketplace.productservice.entity.Product;
import com.marketplace.productservice.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@Tag(name = "Product Management", description = "Operations for managing products in the marketplace")
public class ProductController {

    private final IProductService productService;

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Retrieve a paginated list of products with optional filters such as category, price range, status, etc.",
            parameters = {
                    @Parameter(name = "filters", description = "Filter criteria for products", schema = @Schema(implementation = ProductFilterCriteria.class)),
                    @Parameter(name = "pageable", description = "Pagination information", schema = @Schema(implementation = Pageable.class))
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of products retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Products List Response",
                                    description = "Example response with paginated list of products",
                                    value = """
                                            {
                                              "message": "Products retrieved successfully",
                                              "success": true,
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": "prod-001",
                                                    "name": "Smartphone Samsung Galaxy S24",
                                                    "description": "Smartphone with 6.1 inch display",
                                                    "price": 899.99,
                                                    "category": "Electronics",
                                                    "quantity": 50,
                                                    "sellerId": "seller-123",
                                                    "status": "ACTIVE",
                                                    "createdAt": "2024-01-15T10:30:00",
                                                    "updatedAt": "2024-01-15T10:30:00"
                                                  },
                                                  {
                                                    "id": "prod-002",
                                                    "name": "Laptop Dell XPS 13",
                                                    "description": "Ultrabook laptop with Intel i7 processor",
                                                    "price": 1299.99,
                                                    "category": "Computers",
                                                    "quantity": 25,
                                                    "sellerId": "seller-456",
                                                    "status": "ACTIVE",
                                                    "createdAt": "2024-01-16T14:20:00",
                                                    "updatedAt": "2024-01-16T14:20:00"
                                                  }
                                                ],
                                                "pageable": {
                                                  "sort": { "sorted": false, "unsorted": true },
                                                  "pageNumber": 0,
                                                  "pageSize": 10
                                                },
                                                "totalElements": 2,
                                                "totalPages": 1,
                                                "last": true,
                                                "first": true,
                                                "numberOfElements": 2
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid Filter Parameters Response",
                                    description = "Example response when filter parameters are invalid",
                                    value = """
                                            {
                                              "message": "Invalid filter parameters: price range is invalid",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Example response when internal server error occurs",
                                    value = """
                                            {
                                              "message": "Internal server error occurred while retrieving products",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponseDTO<Page<Product>>> getAllProducts(
            @Parameter(description = "Filter criteria for products") ProductFilterCriteria filters,
            @Parameter(description = "Pagination information") Pageable pageable) {
        ApiResponseDTO<Page<Product>> products = productService.getAllProducts(filters, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieve a specific product by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Found Response",
                                    description = "Example response when product is found",
                                    value = """
                                            {
                                              "message": "Product found successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-001",
                                                "name": "Smartphone Samsung Galaxy S24",
                                                "description": "Smartphone with 6.1 inch display and 50MP camera",
                                                "price": 899.99,
                                                "category": "Electronics",
                                                "quantity": 50,
                                                "sellerId": "seller-123",
                                                "status": "ACTIVE",
                                                "createdAt": "2024-01-15T10:30:00",
                                                "updatedAt": "2024-01-15T10:30:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Example response when product does not exist",
                                    value = """
                                            {
                                              "message": "Product not found with ID: prod-999",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Example response when internal server error occurs",
                                    value = """
                                            {
                                              "message": "Internal server error occurred while retrieving product",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponseDTO<Product>> getProductById(
            @Parameter(description = "Unique product ID", required = true, example = "prod-001") @PathVariable("productId") String id) {
        ApiResponseDTO<Product> product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role') or hasRole('buyer_client_role')")
    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Create a new product in the marketplace. Requires authentication as admin or seller.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product information to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(
                                    name = "Create Product Request",
                                    description = "Example request to create a product",
                                    value = """
                                            {
                                              "name": "Smartphone Samsung Galaxy S24",
                                              "description": "Smartphone with 6.1 inch display and 50MP camera",
                                              "price": 899.99,
                                              "category": "Electronics",
                                              "quantity": 50
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Created Response",
                                    description = "Example response when product is created successfully",
                                    value = """
                                            {
                                              "message": "Product created successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-003",
                                                "name": "Smartphone Samsung Galaxy S24",
                                                "description": "Smartphone with 6.1 inch display and 50MP camera",
                                                "price": 899.99,
                                                "category": "Electronics",
                                                "quantity": 50,
                                                "sellerId": "seller-789",
                                                "status": "ACTIVE",
                                                "createdAt": "2024-01-17T09:15:00",
                                                "updatedAt": "2024-01-17T09:15:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data or validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error Response",
                                    description = "Example response when validation fails",
                                    value = """
                                            {
                                              "message": "Validation failed: name cannot be blank, price must be greater than 0",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Example response when token is invalid",
                                    value = """
                                            {
                                              "message": "Authentication required",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Example response when user lacks permissions",
                                    value = """
                                            {
                                              "message": "Access denied - insufficient permissions",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Example response when internal server error occurs",
                                    value = """
                                            {
                                              "message": "Internal server error occurred while creating product",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponseDTO<Product>> createProduct(
            @Parameter(description = "New product data", required = true) @Valid @RequestBody ProductDto productDto,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        // Extract the seller ID from the JWT token
        String sellerId = jwt.getClaim("sub");
        Product mapToProduct = ProductDto.mapToProduct(productDto, sellerId);
        ApiResponseDTO<Product> createdProduct = productService.createProduct(mapToProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role')")
    @DeleteMapping("/{productId}")
    @Operation(
            summary = "Delete a product",
            description = "Delete a product from the marketplace. Requires authentication as admin or seller. This action is irreversible.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Deleted Response",
                                    description = "Example response when product is deleted successfully",
                                    value = """
                                            {
                                              "message": "Product deleted successfully",
                                              "success": true,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Example response when product does not exist",
                                    value = """
                                            {
                                              "message": "Product not found with ID: prod-999",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Example response when token is invalid",
                                    value = """
                                            {
                                              "message": "Authentication required",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Example response when user lacks permissions",
                                    value = """
                                            {
                                              "message": "Access denied - insufficient permissions",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Example response when internal server error occurs",
                                    value = """
                                            {
                                              "message": "Internal server error occurred while deleting product",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteProduct(
            @Parameter(description = "Unique product ID to delete", required = true, example = "prod-001") @PathVariable("productId") String id) {
        ApiResponseDTO<Void> response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role')")
    @PutMapping("/{productId}")
    @Operation(
            summary = "Update a product",
            description = "Update all fields of an existing product. Requires authentication as admin or seller.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product information",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(
                                    name = "Update Product Request",
                                    description = "Example request to update a product",
                                    value = """
                                            {
                                              "name": "Smartphone Samsung Galaxy S24 Ultra",
                                              "description": "Premium smartphone with 6.8 inch display and 200MP camera",
                                              "price": 1199.99,
                                              "category": "Electronics",
                                              "quantity": 30,
                                              "status": "ACTIVE"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Updated Response",
                                    description = "Example response when product is updated successfully",
                                    value = """
                                            {
                                              "message": "Product updated successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-001",
                                                "name": "Smartphone Samsung Galaxy S24 Ultra",
                                                "description": "Premium smartphone with 6.8 inch display and 200MP camera",
                                                "price": 1199.99,
                                                "category": "Electronics",
                                                "quantity": 30,
                                                "sellerId": "seller-123",
                                                "status": "ACTIVE",
                                                "createdAt": "2024-01-15T10:30:00",
                                                "updatedAt": "2024-01-17T15:45:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data or validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error Response",
                                    description = "Example response when validation fails",
                                    value = """
                                            {
                                              "message": "Validation failed: name cannot be blank, price must be greater than 0",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Example response when product does not exist",
                                    value = """
                                            {
                                              "message": "Product not found with ID: prod-999",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Example response when token is invalid",
                                    value = """
                                            {
                                              "message": "Authentication required",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Example response when user lacks permissions",
                                    value = """
                                            {
                                              "message": "Access denied - insufficient permissions",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Example response when internal server error occurs",
                                    value = """
                                            {
                                              "message": "Internal server error occurred while updating product",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponseDTO<Product>> updateProduct(
            @Parameter(description = "Unique product ID to update", required = true, example = "prod-001") @PathVariable("productId") String id,
            @Parameter(description = "Updated product data", required = true) @RequestBody ProductDto productDto) {
        Product mapToProduct = ProductDto.mapToProduct(productDto, null);
        ApiResponseDTO<Product> updatedProduct = productService.updateProduct(id, mapToProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role')")
    @PatchMapping("/{productId}")
    @Operation(
            summary = "Partially update a product",
            description = "Update specific fields of an existing product. Requires authentication as admin or seller.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Specific product fields to update",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductUpdateDto.class),
                            examples = @ExampleObject(
                                    name = "Partial Update Product Request",
                                    description = "Example request to partially update a product",
                                    value = """
                                            {
                                              "price": 999.99,
                                              "quantity": 75,
                                              "description": "Smartphone with 6.1 inch display - SPECIAL OFFER"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Partially Updated Response",
                                    description = "Example response when product is partially updated",
                                    value = """
                                            {
                                              "message": "Product updated successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-001",
                                                "name": "Smartphone Samsung Galaxy S24",
                                                "description": "Smartphone with 6.1 inch display - SPECIAL OFFER",
                                                "price": 999.99,
                                                "category": "Electronics",
                                                "quantity": 75,
                                                "sellerId": "seller-123",
                                                "status": "ACTIVE",
                                                "createdAt": "2024-01-15T10:30:00",
                                                "updatedAt": "2024-01-17T16:20:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data or validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error Response",
                                    description = "Example response when validation fails",
                                    value = """
                                            {
                                              "message": "Validation failed: price must be greater than 0",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Example response when product does not exist",
                                    value = """
                                            {
                                              "message": "Product not found with ID: prod-999",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or expired token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Example response when token is invalid",
                                    value = """
                                            {
                                              "message": "Authentication required",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Insufficient permissions",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Example response when user lacks permissions",
                                    value = """
                                            {
                                              "message": "Access denied - insufficient permissions",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Example response when internal server error occurs",
                                    value = """
                                            {
                                              "message": "Internal server error occurred while updating product",
                                              "success": false,
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<?> updatePartialProduct(
            @Parameter(description = "Unique product ID to update", required = true, example = "prod-001") @PathVariable("productId") String productId,
            @Parameter(description = "Partial product data to update", required = true) @RequestBody ProductUpdateDto productUpdateDto) {
        Product mapToProduct = ProductUpdateDto.mapToProduct(productUpdateDto);
        ApiResponseDTO<Product> updatedProduct = productService.updatePartialProduct(productId, mapToProduct);
        return ResponseEntity.ok(updatedProduct);
    }
}
