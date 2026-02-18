package com.example.warehouse.resource

import com.example.warehouse.dto.AuthRequest
import com.example.warehouse.dto.AuthResponse
import com.example.warehouse.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*
@Tag(
    name = "Authentication",
    description = "Authentication APIs for login, refresh, and logout"
)
@RestController
@RequestMapping("/api/auth")
class AuthResource(
    private val authService: AuthService
) {

    @Operation(
        summary = "Login user",
        description = "Authenticates user credentials and returns access token. Refresh token is set as HttpOnly cookie."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "401", description = "Invalid credentials")
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
        description = "Generates new access token using refresh token stored in HttpOnly cookie."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Token refreshed"),
            ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
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
        description = "Clears refresh token cookie and logs user out."
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
