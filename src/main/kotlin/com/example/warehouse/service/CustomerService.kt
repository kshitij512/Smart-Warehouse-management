package com.example.warehouse.service


import com.example.warehouse.exception.NotFoundException
import com.example.warehouse.model.Customer
import com.example.warehouse.persistence.CustomerPersistence
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerPersistence: CustomerPersistence
) {

    fun getOrCreate(
        name: String,
        email: String,
        address: String,
        phone: String
    ): Customer {

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

    fun getById(id: Long): Customer =
        customerPersistence.findById(id)
            .orElseThrow { NotFoundException("Customer not found") }
}
