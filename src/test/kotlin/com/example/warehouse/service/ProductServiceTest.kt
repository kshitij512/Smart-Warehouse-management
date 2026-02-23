package com.example.warehouse.service

import com.example.warehouse.dto.CreateProductRequest
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.Product
import com.example.warehouse.persistence.ProductPersistence
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ProductServiceTest {

    private val productPersistence: ProductPersistence = mockk()
    private val productService = ProductService(productPersistence)

    @Test
    fun `create product success`() {
        val request = CreateProductRequest(
            sku = "SKU123",
            name = "Laptop",
            price = 50000.0
        )

        val savedProduct = Product(
            sku = "SKU123",
            name = "Laptop",
            price = 50000.0
        ).apply {
            id = 1L
            createdAt = LocalDateTime.now()
        }

        every { productPersistence.existsBySku("SKU123") } returns false
        every { productPersistence.save(any()) } returns savedProduct

        val response = productService.create(request)

        assertEquals("SKU123", response.sku)
        assertEquals("Laptop", response.name)

        verify(exactly = 1) { productPersistence.save(any()) }
    }

    @Test
    fun `create product throws conflict if sku exists`() {
        every { productPersistence.existsBySku("SKU123") } returns true

        val request = CreateProductRequest(
            sku = "SKU123",
            name = "Laptop",
            price = 50000.0
        )

        assertThrows<ConflictException> {
            productService.create(request)
        }

        verify(exactly = 0) { productPersistence.save(any()) }
    }

    @Test
    fun `get product by id throws not found`() {
        every { productPersistence.findByIdOrNull(1L) } returns null

        assertThrows<EntityNotFoundException> {
            productService.getById(1L)
        }
    }

    @Test
    fun `delete product throws not found`() {
        every { productPersistence.existsById(1L) } returns false

        assertThrows<EntityNotFoundException> {
            productService.delete(1L)
        }

        verify(exactly = 0) { productPersistence.deleteById(any()) }
    }
}