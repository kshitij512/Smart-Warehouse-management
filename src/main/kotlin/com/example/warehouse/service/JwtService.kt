package com.example.warehouse.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

/**
 * Service responsible for JWT token creation, validation,
 * and refresh token cookie management.
 */
@Service
class JwtService(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access.expiration}") val accessExpMs: Long,
    @Value("\${jwt.refresh.expiration}") val refreshExpMs: Long
) {

    private val log = LoggerFactory.getLogger(JwtService::class.java)

    // Secret key used for signing and verifying JWT tokens
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    /**
     * Generates a JWT access token containing username and role.
     */
    fun generateAccessToken(username: String, role: String) =
        Jwts.builder()
            .subject(username)
            .claim("role", role)
            .expiration(Date(System.currentTimeMillis() + accessExpMs))
            .signWith(key)
            .compact()
            .also {
                log.debug("Access token generated for username: {}", username)
            }

    /**
     * Generates a JWT refresh token containing only username.
     */
    fun generateRefreshToken(username: String) =
        Jwts.builder()
            .subject(username)
            .expiration(Date(System.currentTimeMillis() + refreshExpMs))
            .signWith(key)
            .compact()
            .also {
                log.debug("Refresh token generated for username: {}", username)
            }

    /**
     * Adds refresh token as HTTP-only cookie to the response.
     */
    fun addRefreshTokenCookie(
        response: HttpServletResponse,
        token: String
    ) {
        log.debug("Adding refresh token cookie to response")

        val cookie = Cookie("refreshToken", token)
        cookie.isHttpOnly = true
        cookie.path = "api/auth"
        cookie.maxAge = refreshExpMs.toInt()
        response.addCookie(cookie)

        log.info("Refresh token cookie added successfully")
    }

    /**
     * Deletes refresh token cookie by setting maxAge to zero.
     */
    fun deleteRefreshTokenCookie(
        response: HttpServletResponse,
    ) {
        log.debug("Deleting refresh token cookie")

        val cookie = Cookie("refreshToken", "")
        cookie.isHttpOnly = true
        cookie.path = "api/auth"
        cookie.maxAge = 0
        response.addCookie(cookie)

        log.info("Refresh token cookie deleted successfully")
    }

    /**
     * Extracts username from the given JWT token.
     */
    fun extractUsername(token: String) =
        Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token)
            .payload.subject
            .also {
                log.debug("Username extracted from token")
            }
}
