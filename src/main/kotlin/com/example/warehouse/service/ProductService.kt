package com.example.warehouse.service

import com.example.warehouse.dto.CreateProductRequest
import com.example.warehouse.dto.ProductResponse
import com.example.warehouse.dto.UpdateProductRequest
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.NotFoundException
import com.example.warehouse.model.Product
import com.example.warehouse.persistence.ProductPersistence
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productPersistence: ProductPersistence
) {

    fun create(request: CreateProductRequest): ProductResponse {
        println("create product")
        if (productPersistence.existsBySku(request.sku)) {
            throw ConflictException("Product with SKU already exists")
        }

        val product = productPersistence.save(
            Product(
                sku = request.sku,
                name = request.name,
                price = request.price
            )
        )

        return product.toResponse()
    }

    fun getById(id: Long): ProductResponse =
        productPersistence.findByIdOrNull(id)
            ?.toResponse()
            ?: throw NotFoundException("Product not found")


    fun getAll(): List<ProductResponse> =
        productPersistence.findAll().map { it.toResponse() }

    fun update(id: Long, request: UpdateProductRequest): ProductResponse {
        val product = productPersistence.findByIdOrNull(id)
            ?: throw NotFoundException("Product not found")

        request.name?.let { product.name = it }
        request.price?.let { product.price = it }

        return productPersistence.save(product).toResponse()
    }

    fun delete(id: Long) {
        if (!productPersistence.existsById(id)) {
            throw NotFoundException("Product not found")
        }
        productPersistence.deleteById(id)
    }
}


fun Product.toResponse() = ProductResponse(
    id = id,
    sku = sku,
    name = name,
    price = price,
    createdAt = createdAt
)