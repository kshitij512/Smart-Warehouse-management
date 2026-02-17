package com.example.warehouse.service

import com.example.warehouse.dto.AuthRequest
import com.example.warehouse.dto.AuthResponse
import com.example.warehouse.persistence.UserPersistence
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userPersistence: UserPersistence,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
){

     fun login(
        request: AuthRequest,
        response: HttpServletResponse
    ): AuthResponse {

        val user = userPersistence.findByEmail(request.email)
            ?: throw RuntimeException("Invalid credentials")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw RuntimeException("Invalid credentials")
        }

        if (!user.enabled) {
            throw RuntimeException("User is disabled")
        }

        val accessToken =
            jwtService.generateAccessToken(user.email, user.role.name)

        val refreshToken =
            jwtService.generateRefreshToken(user.email)

        jwtService.addRefreshTokenCookie(response, refreshToken)

        return AuthResponse(
            accessToken = accessToken,
            role = user.role.name
        )
    }

     fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): AuthResponse {

        val refreshToken = request.cookies
            ?.firstOrNull { it.name == "refreshToken" }
            ?.value
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val email = jwtService.extractUsername(refreshToken)

        val user = userPersistence.findByEmail(email)
            ?: throw RuntimeException("Invalid credentials")

        val newAccessToken =
            jwtService.generateAccessToken(user.email, user.role.name)

        val newRefreshToken =
            jwtService.generateRefreshToken(user.email)

        jwtService.addRefreshTokenCookie(response, newRefreshToken)

        return AuthResponse(
            accessToken = newAccessToken,
            role = user.role.name
        )
    }

     fun logout(response: HttpServletResponse) {
        jwtService.deleteRefreshTokenCookie(response)
    }
}
