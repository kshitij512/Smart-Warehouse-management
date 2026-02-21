package com.example.warehouse.resourse

import com.example.warehouse.dto.CreateProductRequest
import com.example.warehouse.dto.ProductResponse
import com.example.warehouse.dto.UpdateProductRequest
import com.example.warehouse.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Products",
    description = "APIs for managing products"
)
@RestController
@RequestMapping("/api/products")
class ProductResource(
    private val productService: ProductService
) {

    @Operation(
        summary = "Create a product",
        description = "Creates a new product with a unique SKU."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Product created successfully"),
            ApiResponse(responseCode = "409", description = "Product with SKU already exists")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateProductRequest
    ): ProductResponse =
        productService.create(request)

    @Operation(
        summary = "Get product by ID",
        description = "Fetches product details using product ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Product found"),
            ApiResponse(responseCode = "404", description = "Product not found")
        ]
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ProductResponse =
        productService.getById(id)

    @Operation(
        summary = "Get all products",
        description = "Returns a list of all products."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Products retrieved successfully")
        ]
    )
    @GetMapping
    fun getAll(): List<ProductResponse> =
        productService.getAll()

    @Operation(
        summary = "Update product",
        description = "Updates product name or price."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Product updated successfully"),
            ApiResponse(responseCode = "404", description = "Product not found")
        ]
    )
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductRequest
    ): ProductResponse =
        productService.update(id, request)

    @Operation(
        summary = "Delete product",
        description = "Deletes a product using product ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            ApiResponse(responseCode = "404", description = "Product not found")
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) =
        productService.delete(id)


    @Operation(
        summary = "Sku existence",
        description = "Sku available or not."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "SKU Exists"),
            ApiResponse(responseCode = "404", description = "Sku not found")
        ]
    )
    @GetMapping("/exists/{sku}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun skuExists(@PathVariable sku: String) =
        productService.checkSkuExists(sku)
}