package com.example.warehouse.persistence

import com.example.warehouse.model.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerPersistence : JpaRepository<Customer, Long> {

    fun findByEmail(email: String): Customer?

    fun existsByEmail(email: String): Boolean
}
