package com.example.warehouse.service

import com.example.warehouse.dto.AuthRequest
import com.example.warehouse.dto.AuthResponse
import com.example.warehouse.exception.UnauthorizedException
import com.example.warehouse.persistence.UserPersistence
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

/**
 * Service class responsible for authentication-related operations
 * such as login, token refresh, and logout.
 */
@Service
class AuthService(
    private val userPersistence: UserPersistence,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(AuthService::class.java)

    /**
     * Handles user login.
     * - Validates user credentials
     * - Generates access and refresh tokens
     * - Stores refresh token in HTTP-only cookie
     */
    fun login(
        request: AuthRequest,
        response: HttpServletResponse
    ): AuthResponse {

        log.info("Login attempt started for email: {}", request.email)

        // Fetch user by email
        val user = userPersistence.findByEmail(request.email)
            ?: run {
                log.warn("Login failed: User not found for email {}", request.email)
                throw AccessDeniedException("Invalid credentials")
            }

        // Validate password
        if (!passwordEncoder.matches(request.password, user.password)) {
            log.warn("Login failed: Invalid password for email {}", request.email)
            throw AccessDeniedException("Invalid credentials")
        }

        // Check if user account is enabled
        if (!user.enabled) {
            log.warn("Login failed: User account is disabled for email {}", request.email)
            throw UnauthorizedException("User is disabled")
        }

        log.debug("User authenticated successfully for email {}", request.email)

        // Generate JWT access token
        val accessToken =
            jwtService.generateAccessToken(user.email, user.role.name)

        // Generate JWT refresh token
        val refreshToken =
            jwtService.generateRefreshToken(user.email)

        // Add refresh token to response cookies
        jwtService.addRefreshTokenCookie(response, refreshToken)

        log.info("Login successful for email {}", request.email)

        return AuthResponse(
            accessToken = accessToken,
            role = user.role.name
        )
    }

    /**
     * Handles access token refresh using refresh token stored in cookies.
     */
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): AuthResponse {

        log.info("Token refresh request received")

        // Extract refresh token from cookies
        val refreshToken = request.cookies
            ?.firstOrNull { it.name == "refreshToken" }
            ?.value
            ?: run {
                log.warn("Refresh token missing in request cookies")
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            }

        // Extract user email from refresh token
        val email = jwtService.extractUsername(refreshToken)

        log.debug("Refresh token validated for email {}", email)

        // Fetch user details
        val user = userPersistence.findByEmail(email)
            ?: run {
                log.warn("Token refresh failed: User not found for email {}", email)
                throw AccessDeniedException("Invalid credentials")
            }

        // Generate new access token
        val newAccessToken =
            jwtService.generateAccessToken(user.email, user.role.name)

        // Generate new refresh token
        val newRefreshToken =
            jwtService.generateRefreshToken(user.email)

        // Replace refresh token cookie
        jwtService.addRefreshTokenCookie(response, newRefreshToken)

        log.info("Token refreshed successfully for email {}", email)

        return AuthResponse(
            accessToken = newAccessToken,
            role = user.role.name
        )
    }

    /**
     * Handles user logout by deleting refresh token cookie.
     */
    fun logout(response: HttpServletResponse) {
        log.info("Logout request received")
        jwtService.deleteRefreshTokenCookie(response)
        log.info("Logout successful, refresh token cookie removed")
    }
}
