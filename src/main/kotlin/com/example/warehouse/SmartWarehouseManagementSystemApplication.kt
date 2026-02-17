package com.example.warehouse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
class SmartWarehouseManagementSystemApplication

fun main(args: Array<String>) {
	runApplication<SmartWarehouseManagementSystemApplication>(*args)
}
