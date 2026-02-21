package com.example.warehouse.service

import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.Customer
import com.example.warehouse.persistence.CustomerPersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerPersistence: CustomerPersistence
) {

    private val log = LoggerFactory.getLogger(CustomerService::class.java)

    /**
     * Fetches existing customer by email or creates a new one.
     */
    fun getOrCreate(
        name: String,
        email: String,
        address: String,
        phone: String
    ): Customer {
        log.info("Fetching or creating customer with email: {}", email)

        return customerPersistence.findByEmail(email)
            ?: customerPersistence.save(
                Customer(
                    name = name,
                    email = email,
                    address = address,
                    phone = phone
                )
            )
    }

    /**
     * Fetches customer by id.
     */
    fun getById(id: Long): Customer {
        log.info("Fetching customer with id: {}", id)

        return customerPersistence.findById(id)
            .orElseThrow { EntityNotFoundException("Customer not found") }
    }
}
