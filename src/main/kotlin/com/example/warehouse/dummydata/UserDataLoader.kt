package com.example.warehouse.dummydata

import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.persistence.UserPersistence
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserDataSeeder(
    private val userPersistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        // 🛑 Prevent duplicate seeding
        if (userPersistence.count() > 0) {
            return
        }

        val users = mutableListOf<User>()

        // 🔑 1 ADMIN
        users.add(
            User(
                name = "Admin User",
                email = "admin@warehouse.com",
                password = passwordEncoder.encode("admin123"),
                role = Role.ADMIN
            )
        )

        // 🧑‍💼 4 MANAGERS
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

        // 👷 20 STAFF
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

        println("✅ Seeded users: 1 ADMIN, 4 MANAGERS, 20 STAFF")


    }
}
