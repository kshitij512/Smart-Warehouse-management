package com.example.warehouse.resourse

import com.example.warehouse.dto.AuthRequest
import com.example.warehouse.dto.AuthResponse
import com.example.warehouse.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "Authentication", description = "Authentication and session management APIs")
@RestController
@RequestMapping("/api/auth")
class AuthResource(
    private val authService: AuthService
) {

    @Operation(
        summary = "User login",
        description = "Authenticates user credentials and returns an access token. A refresh token is stored in an HTTP-only cookie."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful"),
            ApiResponse(responseCode = "401", description = "Invalid credentials"),
            ApiResponse(responseCode = "403", description = "User is disabled")
        ]
    )
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: AuthRequest,
        response: HttpServletResponse
    ): AuthResponse =
        authService.login(request, response)

    @Operation(
        summary = "Refresh access token",
        description = "Generates a new access token using the refresh token stored in cookies."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            ApiResponse(responseCode = "401", description = "Invalid or missing refresh token")
        ]
    )
    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): AuthResponse =
        authService.refresh(request, response)

    @Operation(
        summary = "Logout user",
        description = "Logs out the user by deleting the refresh token cookie."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Logout successful")
        ]
    )
    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) =
        authService.logout(response)
}
