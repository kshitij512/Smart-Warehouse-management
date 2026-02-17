package com.example.warehouse.service


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access.expiration}") val accessExpMs: Long,
    @Value("\${jwt.access.expiration}") val refreshExpMs: Long
) {

    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateAccessToken(username: String, role: String) =
        Jwts.builder()
            .subject(username)
            .claim("role", role)
            .expiration(Date(System.currentTimeMillis() + accessExpMs))
            .signWith(key)
            .compact()

    fun generateRefreshToken(username: String) =
        Jwts.builder()
            .subject(username)
            .expiration(Date(System.currentTimeMillis() + refreshExpMs))
            .signWith(key)
            .compact()

    fun addRefreshTokenCookie(
        response : HttpServletResponse,
        token : String
    ){
        val cookie = Cookie("refreshToken", token)
        cookie.isHttpOnly =true
        cookie.path = "api/auth"
        cookie.maxAge = refreshExpMs.toInt()
        response.addCookie(cookie)
    }

    fun deleteRefreshTokenCookie(
        response : HttpServletResponse,
    ){
        val cookie = Cookie("refreshToken", "")
        cookie.isHttpOnly =true
        cookie.path = "api/auth"
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    fun extractUsername(token: String) =
        Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token)
            .payload.subject
}