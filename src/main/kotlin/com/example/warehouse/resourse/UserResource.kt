package com.example.warehouse.resourse

import com.example.warehouse.dto.CreateUserRequest
import com.example.warehouse.dto.UserResponse
import com.example.warehouse.service.AdminUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Admin Users",
    description = "Admin APIs for managing system users"
)
@RestController
@RequestMapping("/api/admin/users")
class UserResource(
    private val adminUserService: AdminUserService
) {

    @Operation(
        summary = "Create a new user",
        description = "Creates a new non-admin user. ADMIN role creation is not allowed."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User created successfully"),
            ApiResponse(responseCode = "409", description = "Email already exists or invalid role")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest
    ): UserResponse =
        adminUserService.createUser(request)

    @Operation(
        summary = "Get all users",
        description = "Returns a list of all users in the system."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Users retrieved successfully")
        ]
    )
    @GetMapping
    fun getAllUsers(): List<UserResponse> =
        adminUserService.getAllUsers()

    @Operation(
        summary = "Get user by ID",
        description = "Fetches user details using user ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User found"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse =
        adminUserService.getUserById(id)

    @Operation(
        summary = "Enable user account",
        description = "Activates a disabled user account."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "User enabled successfully"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    @PutMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun enableUser(@PathVariable id: Long) =
        adminUserService.enableUser(id)

    @Operation(
        summary = "Disable user account",
        description = "Disables an active user account."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "User disabled successfully"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    @PutMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun disableUser(@PathVariable id: Long) =
        adminUserService.disableUser(id)
}