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
@RequestMapping("/api/products")
@AllArgsConstructor
@Tag(name = "Product Management", description = "API para gestionar productos del marketplace")
public class ProductController {

    private final IProductService productService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los productos",
            description = "Obtiene una lista paginada de productos con filtros opcionales como categoría, precio mínimo/máximo, estado, etc.",
            parameters = {
                    @Parameter(name = "filters", description = "Criterios de filtrado para productos", schema = @Schema(implementation = ProductFilterCriteria.class)),
                    @Parameter(name = "pageable", description = "Información de paginación", schema = @Schema(implementation = Pageable.class))
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de productos obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Products List Response",
                                    description = "Ejemplo de respuesta con lista paginada de productos",
                                    value = """
                                            {
                                              "message": "Products retrieved successfully",
                                              "success": true,
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": "prod-001",
                                                    "name": "Smartphone Samsung Galaxy S24",
                                                    "description": "Teléfono inteligente con pantalla de 6.1 pulgadas",
                                                    "price": 899.99,
                                                    "category": "Electronics",
                                                    "stock": 50,
                                                    "sellerId": "seller-123",
                                                    "status": "ACTIVE",
                                                    "createdAt": "2024-01-15T10:30:00",
                                                    "updatedAt": "2024-01-15T10:30:00"
                                                  },
                                                  {
                                                    "id": "prod-002",
                                                    "name": "Laptop Dell XPS 13",
                                                    "description": "Laptop ultrabook con procesador Intel i7",
                                                    "price": 1299.99,
                                                    "category": "Computers",
                                                    "stock": 25,
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
                    description = "Parámetros de filtro inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid Filter Parameters Response",
                                    description = "Ejemplo de respuesta cuando los parámetros de filtro son inválidos",
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
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Ejemplo de respuesta de error interno del servidor",
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
            @Parameter(description = "Criterios de filtrado para productos") ProductFilterCriteria filters,
            @Parameter(description = "Información de paginación") Pageable pageable) {
        ApiResponseDTO<Page<Product>> products = productService.getAllProducts(filters, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    @Operation(
            summary = "Obtener producto por ID",
            description = "Obtiene un producto específico mediante su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Found Response",
                                    description = "Ejemplo de respuesta cuando el producto es encontrado",
                                    value = """
                                            {
                                              "message": "Product found successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-001",
                                                "name": "Smartphone Samsung Galaxy S24",
                                                "description": "Teléfono inteligente con pantalla de 6.1 pulgadas y cámara de 50MP",
                                                "price": 899.99,
                                                "category": "Electronics",
                                                "stock": 50,
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
                    description = "Producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Ejemplo de respuesta cuando el producto no existe",
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
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Ejemplo de respuesta de error interno del servidor",
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
            @Parameter(description = "ID único del producto", required = true, example = "prod-001") @PathVariable("productId") String id) {
        ApiResponseDTO<Product> product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role')")
    @PostMapping
    @Operation(
            summary = "Crear nuevo producto",
            description = "Crea un nuevo producto en el marketplace. Requiere autenticación como administrador o vendedor.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Información del producto a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(
                                    name = "Create Product Request",
                                    description = "Ejemplo de solicitud para crear un producto",
                                    value = """
                                            {
                                              "name": "Smartphone Samsung Galaxy S24",
                                              "description": "Teléfono inteligente con pantalla de 6.1 pulgadas y cámara de 50MP",
                                              "price": 899.99,
                                              "category": "Electronics",
                                              "stock": 50,
                                              "status": "ACTIVE"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Created Response",
                                    description = "Ejemplo de respuesta cuando el producto se crea exitosamente",
                                    value = """
                                            {
                                              "message": "Product created successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-003",
                                                "name": "Smartphone Samsung Galaxy S24",
                                                "description": "Teléfono inteligente con pantalla de 6.1 pulgadas y cámara de 50MP",
                                                "price": 899.99,
                                                "category": "Electronics",
                                                "stock": 50,
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
                    description = "Datos del producto inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error Response",
                                    description = "Ejemplo de respuesta cuando la validación falla",
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
                    description = "No autorizado - Token inválido o expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Ejemplo de respuesta cuando el token es inválido",
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
                    description = "Acceso denegado - No tiene permisos suficientes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Ejemplo de respuesta cuando el usuario no tiene permisos",
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
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Ejemplo de respuesta de error interno del servidor",
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
            @Parameter(description = "Datos del nuevo producto", required = true) @Valid @RequestBody ProductDto productDto,
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
            summary = "Eliminar producto",
            description = "Elimina un producto del marketplace. Requiere autenticación como administrador o vendedor. Esta acción es irreversible.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto eliminado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Deleted Response",
                                    description = "Ejemplo de respuesta cuando el producto se elimina exitosamente",
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
                    description = "Producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Ejemplo de respuesta cuando el producto no existe",
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
                    description = "No autorizado - Token inválido o expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Ejemplo de respuesta cuando el token es inválido",
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
                    description = "Acceso denegado - No tiene permisos suficientes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Ejemplo de respuesta cuando el usuario no tiene permisos",
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
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Ejemplo de respuesta de error interno del servidor",
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
            @Parameter(description = "ID único del producto a eliminar", required = true, example = "prod-001") @PathVariable("productId") String id) {
        ApiResponseDTO<Void> response = productService.deleteProduct(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role')")
    @PutMapping("/{productId}")
    @Operation(
            summary = "Actualizar producto completo",
            description = "Actualiza todos los campos de un producto existente. Requiere autenticación como administrador o vendedor.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Información actualizada del producto",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(
                                    name = "Update Product Request",
                                    description = "Ejemplo de solicitud para actualizar un producto",
                                    value = """
                                            {
                                              "name": "Smartphone Samsung Galaxy S24 Ultra",
                                              "description": "Teléfono inteligente premium con pantalla de 6.8 pulgadas y cámara de 200MP",
                                              "price": 1199.99,
                                              "category": "Electronics",
                                              "stock": 30,
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
                    description = "Producto actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Updated Response",
                                    description = "Ejemplo de respuesta cuando el producto se actualiza exitosamente",
                                    value = """
                                            {
                                              "message": "Product updated successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-001",
                                                "name": "Smartphone Samsung Galaxy S24 Ultra",
                                                "description": "Teléfono inteligente premium con pantalla de 6.8 pulgadas y cámara de 200MP",
                                                "price": 1199.99,
                                                "category": "Electronics",
                                                "stock": 30,
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
                    description = "Datos del producto inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error Response",
                                    description = "Ejemplo de respuesta cuando la validación falla",
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
                    description = "Producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Ejemplo de respuesta cuando el producto no existe",
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
                    description = "No autorizado - Token inválido o expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Ejemplo de respuesta cuando el token es inválido",
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
                    description = "Acceso denegado - No tiene permisos suficientes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Ejemplo de respuesta cuando el usuario no tiene permisos",
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
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Ejemplo de respuesta de error interno del servidor",
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
            @Parameter(description = "ID único del producto a actualizar", required = true, example = "prod-001") @PathVariable("productId") String id,
            @Parameter(description = "Datos actualizados del producto", required = true) @RequestBody ProductDto productDto) {
        Product mapToProduct = ProductDto.mapToProduct(productDto, null);
        ApiResponseDTO<Product> updatedProduct = productService.updateProduct(id, mapToProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasRole('admin_client_role') or hasRole('seller_client_role')")
    @PatchMapping("/{productId}")
    @Operation(
            summary = "Actualizar producto parcial",
            description = "Actualiza campos específicos de un producto existente. Requiere autenticación como administrador o vendedor.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos específicos del producto a actualizar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductUpdateDto.class),
                            examples = @ExampleObject(
                                    name = "Partial Update Product Request",
                                    description = "Ejemplo de solicitud para actualizar parcialmente un producto",
                                    value = """
                                            {
                                              "price": 999.99,
                                              "stock": 75,
                                              "description": "Teléfono inteligente con pantalla de 6.1 pulgadas - OFERTA ESPECIAL"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Partially Updated Response",
                                    description = "Ejemplo de respuesta cuando el producto se actualiza parcialmente",
                                    value = """
                                            {
                                              "message": "Product updated successfully",
                                              "success": true,
                                              "data": {
                                                "id": "prod-001",
                                                "name": "Smartphone Samsung Galaxy S24",
                                                "description": "Teléfono inteligente con pantalla de 6.1 pulgadas - OFERTA ESPECIAL",
                                                "price": 999.99,
                                                "category": "Electronics",
                                                "stock": 75,
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
                    description = "Datos del producto inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error Response",
                                    description = "Ejemplo de respuesta cuando la validación falla",
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
                    description = "Producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Product Not Found Response",
                                    description = "Ejemplo de respuesta cuando el producto no existe",
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
                    description = "No autorizado - Token inválido o expirado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Unauthorized Response",
                                    description = "Ejemplo de respuesta cuando el token es inválido",
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
                    description = "Acceso denegado - No tiene permisos suficientes",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Access Denied Response",
                                    description = "Ejemplo de respuesta cuando el usuario no tiene permisos",
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
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error Response",
                                    description = "Ejemplo de respuesta de error interno del servidor",
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
            @Parameter(description = "ID único del producto a actualizar", required = true, example = "prod-001") @PathVariable("productId") String productId,
            @Parameter(description = "Datos parciales para actualizar el producto", required = true) @RequestBody ProductUpdateDto productUpdateDto) {
        Product mapToProduct = ProductUpdateDto.mapToProduct(productUpdateDto);
        ApiResponseDTO<Product> updatedProduct = productService.updatePartialProduct(productId, mapToProduct);
        return ResponseEntity.ok(updatedProduct);
    }
}
