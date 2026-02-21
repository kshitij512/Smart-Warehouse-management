package com.example.warehouse.dummydata

import com.example.warehouse.model.Customer
import com.example.warehouse.model.InventoryStock
import com.example.warehouse.model.Order
import com.example.warehouse.model.OrderItem
import com.example.warehouse.model.OrderStatus
import com.example.warehouse.model.Product
import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.model.Warehouse
import com.example.warehouse.persistence.InventoryStockPersistence
import com.example.warehouse.persistence.OrderItemPersistence
import com.example.warehouse.persistence.OrderPersistence
import com.example.warehouse.persistence.ProductPersistence
import com.example.warehouse.persistence.UserPersistence
import com.example.warehouse.persistence.WarehousePersistence
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@org.springframework.core.annotation.Order(1)
class UserDataSeeder(
    private val userPersistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (userPersistence.count() > 0) return

        val users = mutableListOf<User>()

        users.add(
            User(
                name = "Admin User",
                email = "admin@warehouse.com",
                password = passwordEncoder.encode("admin123"),
                role = Role.ADMIN
            )
        )

        for (i in 1..4) {
            users.add(
                User(
                    name = "Manager $i",
                    email = "manager$i@warehouse.com",
                    password = passwordEncoder.encode("manager123"),
                    role = Role.WAREHOUSE_MANAGER
                )
            )
        }

        for (i in 1..20) {
            users.add(
                User(
                    name = "Staff $i",
                    email = "staff$i@warehouse.com",
                    password = passwordEncoder.encode("staff123"),
                    role = Role.STAFF
                )
            )
        }

        userPersistence.saveAll(users)
        println("✅ Seeded users")
    }
}

@Component
@org.springframework.core.annotation.Order(2)

class WarehouseDataSeeder(
    private val warehousePersistence: WarehousePersistence,
    private val userPersistence: UserPersistence
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (warehousePersistence.count() > 0) return

        val managers = userPersistence.findAll()
            .filter { it.role == Role.WAREHOUSE_MANAGER }

        if (managers.isEmpty()) {
            println("⚠ No managers found. Skipping warehouse seeding.")
            return
        }

        val cities = listOf(
            "Mumbai", "Delhi", "Bangalore",
            "Chennai", "Hyderabad", "Kolkata"
        )

        val warehouses = managers.mapIndexed { index, manager ->
            Warehouse(
                name = "${cities.getOrElse(index) { "City$index" }} Hub",
                location = cities.getOrElse(index) { "City$index" },
                code = "WH-${index + 1}",
                capacity = (1000..5000).random(),
                manager = manager
            )
        }

        warehousePersistence.saveAll(warehouses)
        println("✅ Seeded warehouses")
    }
}

@Component
@org.springframework.core.annotation.Order(3)
class ProductDataSeeder(
    private val productPersistence: ProductPersistence
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (productPersistence.count() > 0) return

        val products = (1..40).map {
            Product(
                sku = "SKU-${1000 + it}",
                name = "Product $it",
                price = (100..5000).random().toDouble()
            )
        }

        productPersistence.saveAll(products)
        println("✅ Seeded products")
    }
}

@Component
@org.springframework.core.annotation.Order(4)
class InventoryDataSeeder(
    private val inventoryPersistence: InventoryStockPersistence,
    private val warehousePersistence: WarehousePersistence,
    private val productPersistence: ProductPersistence
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (inventoryPersistence.count() > 0) return

        val warehouses = warehousePersistence.findAll()
        val products = productPersistence.findAll()

        if (warehouses.isEmpty() || products.isEmpty()) {
            println("⚠ Missing warehouses/products. Skipping inventory seeding.")
            return
        }

        val inventoryList = mutableListOf<InventoryStock>()

        warehouses.forEach { warehouse ->
            products.shuffled().take(15).forEach { product ->
                inventoryList.add(
                    InventoryStock(
                        warehouse = warehouse,
                        product = product,
                        stockQuantity = (0..200).random(),
                        reorderThreshold = (10..30).random()
                    )
                )
            }
        }

        inventoryPersistence.saveAll(inventoryList)
        println("✅ Seeded inventory")
    }
}

@Component
@org.springframework.core.annotation.Order(5)
class OrderDataSeeder(
    private val orderPersistence: OrderPersistence,
    private val orderItemPersistence: OrderItemPersistence,
    private val warehousePersistence: WarehousePersistence,
    private val productPersistence: ProductPersistence,
    private val userPersistence: UserPersistence
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        if (orderPersistence.count() > 0) return

        val warehouses = warehousePersistence.findAll()
        val products = productPersistence.findAll()
        val staff = userPersistence.findAll()
            .filter { it.role == Role.STAFF }

        if (warehouses.isEmpty() || products.isEmpty() || staff.isEmpty()) {
            println("⚠ Missing required data. Skipping order seeding.")
            return
        }

        val statuses = OrderStatus.entries.toTypedArray()

        repeat(80) { index ->

            val warehouse = warehouses.random()

            val order = Order(
                warehouse = warehouse,
                customer = Customer(
                    name = "Customer ${index + 1}",
                    phone = "99999999${index + 1}",
                    email = "customer${index + 1}@mail.com",
                    address = "Address ${index + 1}"
                ),
                status = statuses.random(),
                totalAmount = 0.0
            )

            orderPersistence.save(order)

            var total = 0.0

            products.shuffled().take((1..3).random())
                .forEach { product ->

                    val qty = (1..5).random()
                    total += product.price * qty

                    orderItemPersistence.save(
                        OrderItem(
                            order = order,
                            product = product,
                            quantity = qty,
                            price = product.price
                        )
                    )
                }

            order.totalAmount = total
            order.assignedStaff = staff.random()

            orderPersistence.save(order)
        }

        println("✅ Seeded 80 orders")
    }
}