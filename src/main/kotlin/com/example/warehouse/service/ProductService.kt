package com.example.warehouse.service

import com.example.warehouse.dto.CreateProductRequest
import com.example.warehouse.dto.ProductResponse
import com.example.warehouse.dto.UpdateProductRequest
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.Product
import com.example.warehouse.persistence.ProductPersistence
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * Service class responsible for managing product-related operations
 * such as create, read, update, and delete.
 */
@Service
class ProductService(
    private val productPersistence: ProductPersistence
) {

    private val log = LoggerFactory.getLogger(ProductService::class.java)

    /**
     * Creates a new product after validating SKU uniqueness.
     */
    fun create(request: CreateProductRequest): ProductResponse {

        log.info("Create product request received for SKU: {}", request.sku)
        println("create product")

        // Check if product with same SKU already exists
        if (productPersistence.existsBySku(request.sku)) {
            log.warn("Product creation failed: SKU already exists [{}]", request.sku)
            throw ConflictException("Product with SKU already exists")
        }

        // Persist product entity
        val product = productPersistence.save(
            Product(
                sku = request.sku,
                name = request.name,
                price = request.price
            )
        )

        log.info("Product created successfully with ID: {} and SKU: {}", product.id, product.sku)

        return product.toResponse()
    }

    /**
     * Fetches a product by its unique ID.
     */
    fun getById(id: Long): ProductResponse {
        log.debug("Fetching product by ID: {}", id)

        return productPersistence.findByIdOrNull(id)
            ?.toResponse()
            ?: run {
                log.warn("Product not found with ID: {}", id)
                throw EntityNotFoundException("Product not found")
            }
    }

    /**
     * Fetches all available products.
     */
    fun getAll(): List<ProductResponse> {
        log.debug("Fetching all products")

        val products = productPersistence.findAll().map { it.toResponse() }

        log.info("Total products fetched: {}", products.size)
        return products
    }

    /**
     * Updates an existing product with provided fields.
     */
    fun update(id: Long, request: UpdateProductRequest): ProductResponse {

        log.info("Update product request received for ID: {}", id)

        // Fetch product entity
        val product = productPersistence.findByIdOrNull(id)
            ?: run {
                log.warn("Product update failed: Product not found with ID: {}", id)
                throw EntityNotFoundException("Product not found")
            }

        // Apply partial updates
        request.name?.let {
            log.debug("Updating product name for ID: {}", id)
            product.name = it
        }

        request.price?.let {
            log.debug("Updating product price for ID: {}", id)
            product.price = it
        }

        val updatedProduct = productPersistence.save(product)

        log.info("Product updated successfully for ID: {}", id)

        return updatedProduct.toResponse()
    }

    /**
     * Deletes a product by ID.
     */
    fun delete(id: Long) {

        log.info("Delete product request received for ID: {}", id)

        // Validate product existence
        if (!productPersistence.existsById(id)) {
            log.warn("Product deletion failed: Product not found with ID: {}", id)
            throw EntityNotFoundException("Product not found")
        }

        productPersistence.deleteById(id)

        log.info("Product deleted successfully for ID: {}", id)
    }

    /**
     * Checking sku exists or not.
     */
    fun checkSkuExists(sku : String): Boolean {
        log.debug("Fetching product by Sku: {}", sku)

        return productPersistence.existsBySku(sku)
    }
}

/**
 * Extension function to convert Product entity to ProductResponse DTO.
 */
fun Product.toResponse() = ProductResponse(
    id = id,
    sku = sku,
    name = name,
    price = price,
    createdAt = createdAt
)
