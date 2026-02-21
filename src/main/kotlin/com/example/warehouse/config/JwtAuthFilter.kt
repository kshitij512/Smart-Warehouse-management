package com.example.warehouse.config;


import com.example.warehouse.exception.JWTExpiredException
import com.example.warehouse.service.JwtService
import com.example.warehouse.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserService
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
       val header = request.getHeader("Authorization")
        try {
            if(header != null && header.startsWith("Bearer ")){
                val token = header.substring(7)
                val username = jwtService.extractUsername(token)

                val userDetails = userDetailsService.loadUserByUsername(username)

                val auth = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                SecurityContextHolder.getContext().authentication = auth
            }
            filterChain.doFilter(request, response)
        } catch (e: JWTExpiredException) {
            sendError(response, e.message)
        }
    }

    private fun sendError(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.writer.write(
            """
            {
              "status": 401,
              "error": "Unauthorized",
              "message": "$message"
            }
            """.trimIndent()
        )
    }

}