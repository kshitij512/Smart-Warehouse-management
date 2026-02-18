package com.example.warehouse.config


import com.example.warehouse.model.Role
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {

    init {
        println("✅ SecurityConfig loaded")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:4200")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source;

    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors {  }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                it.requestMatchers("/api/admin/**")
                    .hasRole(Role.ADMIN.name)
                it.requestMatchers("/api/users/**")
                    .hasAnyRole(Role.ADMIN.name)
                it.requestMatchers("/api/products/**")
                    .hasAnyRole(Role.WAREHOUSE_MANAGER.name, Role.ADMIN.name)
                it.requestMatchers("/api/inventory/**")
                    .hasAnyRole(Role.WAREHOUSE_MANAGER.name)
                it.requestMatchers("/api/warehouses/**")
                    .hasAnyRole(Role.WAREHOUSE_MANAGER.name, Role.ADMIN.name)
                it.requestMatchers("/api/orders/**")
                    .hasAnyRole(Role.WAREHOUSE_MANAGER.name, Role.STAFF.name)

                it.anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter::class.java)
            .headers { headers ->
                headers.frameOptions { frame ->
                    frame.disable()
                }
            }
        return http.build()
    }


}