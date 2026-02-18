package com.example.warehouse.persistence

import com.example.warehouse.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProductPersistence: JpaRepository<Product, Long> {
    fun findBySku(sku: String): Product?

    fun existsBySku(sku: String): Boolean


}