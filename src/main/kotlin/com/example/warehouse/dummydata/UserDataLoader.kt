package com.example.warehouse.dummydata

import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.persistence.UserPersistence
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserDataLoader(
    private val userPersistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {



    override fun run(vararg args: String?) {

        if (!userPersistence.existsByEmail("admin@warehouse.com")) {

            val admin = User(
                name = "System Admin",
                email = "admin@warehouse.com",
                password = passwordEncoder.encode("admin123"),
                role = Role.ADMIN,
                enabled = true
            )

            userPersistence.save(admin)
        }
    }
}
