package com.example.warehouse.service

import com.example.warehouse.dto.CreateUserRequest
import com.example.warehouse.dto.UserResponse
import com.example.warehouse.exception.ConflictException
import com.example.warehouse.exception.EntityNotFoundException
import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.persistence.UserPersistence
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service responsible for administrative user management operations.
 * Includes user creation, activation, deactivation, and retrieval.
 */
@Service
class AdminUserService(
    private val userPersistence: UserPersistence,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(AdminUserService::class.java)

    /**
     * Creates a new non-admin user.
     * ADMIN role creation is explicitly restricted.
     */
    fun createUser(request: CreateUserRequest): UserResponse {

        log.info("Create user request received for email: {}", request.email)

        // Prevent creation of ADMIN users
        if (request.role == Role.ADMIN) {
            log.warn("User creation failed: Attempt to create ADMIN user with email {}", request.email)
            throw ConflictException("Cannot create ADMIN users")
        }

        // Check for existing email
        if (userPersistence.existsByEmail(request.email)) {
            log.warn("User creation failed: Email already exists [{}]", request.email)
            throw ConflictException("Email already exists")
        }

        // Create user entity
        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role,
            enabled = true
        )

        val saved = userPersistence.save(user)

        log.info("User created successfully with ID: {} and email: {}", saved.id, saved.email)

        return saved.toResponse()
    }

    /**
     * Retrieves all users in the system.
     */
    fun getAllUsers(): List<UserResponse> {

        log.debug("Fetching all users")

        val users = userPersistence.findAll().map { it.toResponse() }

        log.info("Total users fetched: {}", users.size)

        return users
    }

    /**
     * Retrieves a user by ID.
     */
    fun getUserById(id: Long): UserResponse {

        log.debug("Fetching user by ID: {}", id)

        return userPersistence.findByIdOrNull(id)
            ?.toResponse()
            ?: run {
                log.warn("User not found with ID: {}", id)
                throw EntityNotFoundException("User not found")
            }
    }

    /**
     * Enables a user account.
     */
    fun enableUser(id: Long) {

        log.info("Enable user request received for ID: {}", id)

        val user = userPersistence.findByIdOrNull(id)
            ?: run {
                log.warn("Enable user failed: User not found with ID: {}", id)
                throw EntityNotFoundException("User not found")
            }

        user.enabled = true
        userPersistence.save(user)

        log.info("User enabled successfully for ID: {}", id)
    }

    /**
     * Disables a user account.
     */
    fun disableUser(id: Long) {

        log.info("Disable user request received for ID: {}", id)

        val user = userPersistence.findByIdOrNull(id)
            ?: run {
                log.warn("Disable user failed: User not found with ID: {}", id)
                throw EntityNotFoundException("User Not Found")
            }

        user.enabled = false
        userPersistence.save(user)

        log.info("User disabled successfully for ID: {}", id)
    }
}

/**
 * Extension function to convert User entity into UserResponse DTO.
 */
fun User.toResponse() = UserResponse(
    id = this.id,
    name = this.name,
    email = this.email,
    role = this.role,
    enabled = this.enabled
)
