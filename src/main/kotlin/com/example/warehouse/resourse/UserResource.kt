package com.example.warehouse.resourse

import com.example.warehouse.dto.CreateUserRequest
import com.example.warehouse.dto.UserResponse
import com.example.warehouse.service.AdminUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest
    ): UserResponse =
        adminUserService.createUser(request)

    @GetMapping
    fun getAllUsers(): List<UserResponse> =
        adminUserService.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse =
        adminUserService.getUserById(id)

    @PutMapping("/{id}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun enableUser(@PathVariable id: Long) =
        adminUserService.enableUser(id)

    @PutMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun disableUser(@PathVariable id: Long) =
        adminUserService.disableUser(id)
}
