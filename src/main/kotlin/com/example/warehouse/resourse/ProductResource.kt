package com.example.warehouse.resourse

import com.example.warehouse.dto.CreateProductRequest
import com.example.warehouse.dto.ProductResponse
import com.example.warehouse.dto.UpdateProductRequest
import com.example.warehouse.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductResource(
    private val productService: ProductService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateProductRequest): ProductResponse =
        productService.create(request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ProductResponse =
        productService.getById(id)

    @GetMapping
    fun getAll(): List<ProductResponse> =
        productService.getAll()

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductRequest
    ): ProductResponse =
        productService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) =
        productService.delete(id)
}
