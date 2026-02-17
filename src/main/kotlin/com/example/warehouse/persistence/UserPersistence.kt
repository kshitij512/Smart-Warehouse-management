package com.example.warehouse.persistence

import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserPersistence: JpaRepository<User, Long> {


    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun findAllByRole(role: Role): List<User>

    fun findAllByEnabled(enabled: Boolean): List<User>

}