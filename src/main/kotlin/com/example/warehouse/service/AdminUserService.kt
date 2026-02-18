package com.example.warehouse.service

import com.example.warehouse.dto.CreateUserRequest
import com.example.warehouse.dto.UserResponse
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.NotFoundException
import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.persistence.UserPersistence
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdminUserService(
    private val userPersistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder
) {

     fun createUser(request: CreateUserRequest): UserResponse {

        if (request.role == Role.ADMIN) {
            throw ConflictException("Cannot create ADMIN users")
        }

        if (userPersistence.existsByEmail(request.email)) {
            throw ConflictException("Email already exists")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role,
            enabled = true
        )

        val saved = userPersistence.save(user)
        return saved.toResponse()
    }

     fun getAllUsers(): List<UserResponse> =
        userPersistence.findAll().map { it.toResponse() }

    fun getUserById(id: Long): UserResponse =
        userPersistence.findByIdOrNull(id)
            ?.toResponse()
            ?: throw NotFoundException("User not found")

    fun enableUser(id: Long) {
        val user = userPersistence.findByIdOrNull(id)
            ?: throw NotFoundException("User not found")
        user.enabled = true
        userPersistence.save(user)
    }

     fun disableUser(id: Long) {
        val user = userPersistence.findByIdOrNull(id)
            ?: throw NotFoundException("User Not Found")
        user.enabled = false
        userPersistence.save(user)
    }
}


fun User.toResponse() = UserResponse(
    id = this.id,
    name = this.name,
    email = this.email,
    role = this.role,
    enabled = this.enabled
)
