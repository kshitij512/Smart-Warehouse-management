package com.example.warehouse.resource

import com.example.warehouse.dto.CreateUserRequest
import com.example.warehouse.dto.UserResponse
import com.example.warehouse.service.AdminUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
@Tag(
    name = "Admin - Users",
    description = "Admin operations for managing users"
)
@RestController
@RequestMapping("/api/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService
) {

    @Operation(
        summary = "Create user",
        description = "Creates a new system user with assigned role."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User created successfully"),
            ApiResponse(responseCode = "400", description = "Validation error"),
            ApiResponse(responseCode = "403", description = "Access denied")
        ]
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest
    ): UserResponse =
        adminUserService.createUser(request)


    @Operation(summary = "Get all users")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Users retrieved"),
            ApiResponse(responseCode = "403", description = "Access denied")
        ]
    )
    @GetMapping
    fun getAllUsers(): List<UserResponse> =
        adminUserService.getAllUsers()


    @Operation(summary = "Get user by ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User found"),
            ApiResponse(responseCode = "404", description = "User not found")
        ]
    )
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse =
        adminUserService.getUserById(id)


    @Operation(summary = "Enable user")
    @ApiResponse(responseCode = "204", description = "User enabled")
    @PutMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun enableUser(@PathVariable id: Long) =
        adminUserService.enableUser(id)


    @Operation(summary = "Disable user")
    @ApiResponse(responseCode = "204", description = "User disabled")
    @PutMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun disableUser(@PathVariable id: Long) =
        adminUserService.disableUser(id)
}
