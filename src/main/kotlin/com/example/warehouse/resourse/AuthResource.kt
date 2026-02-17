package com.example.warehouse.resourse

import com.example.warehouse.dto.AuthRequest
import com.example.warehouse.dto.AuthResponse
import com.example.warehouse.service.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthResource(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: AuthRequest,
        response: HttpServletResponse
    ): AuthResponse =
        authService.login(request, response)

    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): AuthResponse =
        authService.refresh(request, response)

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) =
        authService.logout(response)
}
