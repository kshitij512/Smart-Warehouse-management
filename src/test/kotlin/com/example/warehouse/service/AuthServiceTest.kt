package com.example.warehouse.service

import com.example.warehouse.dto.AuthRequest
import com.example.warehouse.exception.UnauthorizedException
import com.example.warehouse.model.Role
import com.example.warehouse.model.User
import com.example.warehouse.persistence.UserPersistence
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockKExtension::class)
class AuthServiceTest {

    @MockK lateinit var userPersistence: UserPersistence
    @MockK lateinit var jwtService: JwtService
    @MockK lateinit var passwordEncoder: PasswordEncoder
    @MockK lateinit var response: HttpServletResponse
    @MockK lateinit var request: HttpServletRequest

    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        authService = AuthService(userPersistence, jwtService, passwordEncoder)
    }

    // ---------- LOGIN ----------

    @Test
    fun `login success`() {
        val user = mockUser(enabled = true)

        every { userPersistence.findByEmail(any()) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true
        every { jwtService.generateAccessToken(any(), any()) } returns "access"
        every { jwtService.generateRefreshToken(any()) } returns "refresh"
        every { jwtService.addRefreshTokenCookie(response, any()) } just Runs

        val result = authService.login(
            AuthRequest("test@mail.com", "pass"),
            response
        )

        assertEquals("access", result.accessToken)
        assertEquals("ADMIN", result.role)
    }

    @Test
    fun `login fails for invalid credentials`() {
        val user = mockUser()

        every { userPersistence.findByEmail(any()) } returns user
        every { passwordEncoder.matches(any(), any()) } returns false

        assertThrows<AccessDeniedException> {
            authService.login(AuthRequest("test@mail.com", "wrong"), response)
        }
    }

    @Test
    fun `login fails when user disabled`() {
        val user = mockUser(enabled = false)

        every { userPersistence.findByEmail(any()) } returns user
        every { passwordEncoder.matches(any(), any()) } returns true

        assertThrows<UnauthorizedException> {
            authService.login(AuthRequest("test@mail.com", "pass"), response)
        }
    }

    // ---------- REFRESH ----------

    @Test
    fun `refresh fails when token missing`() {
        every { request.cookies } returns null

        assertThrows<ResponseStatusException> {
            authService.refresh(request, response)
        }
    }

    @Test
    fun `refresh success`() {
        val user = mockUser()

        every { request.cookies } returns arrayOf(Cookie("refreshToken", "old"))
        every { jwtService.extractUsername("old") } returns "test@mail.com"
        every { userPersistence.findByEmail(any()) } returns user
        every { jwtService.generateAccessToken(any(), any()) } returns "newAccess"
        every { jwtService.generateRefreshToken(any()) } returns "newRefresh"
        every { jwtService.addRefreshTokenCookie(response, any()) } just Runs

        val result = authService.refresh(request, response)

        assertEquals("newAccess", result.accessToken)
        assertEquals("ADMIN", result.role)
    }

    // ---------- LOGOUT ----------

    @Test
    fun `logout deletes refresh token cookie`() {
        every { jwtService.deleteRefreshTokenCookie(response) } just Runs

        authService.logout(response)

        verify { jwtService.deleteRefreshTokenCookie(response) }
    }

    // ---------- Helper ----------

    private fun mockUser(enabled: Boolean = true) =
        User(
            id = 1,
            name = "Test",
            email = "test@mail.com",
            password = "hashed",
            role = Role.ADMIN,
            enabled = enabled
        )
}