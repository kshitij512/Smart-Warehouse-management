package com.example.warehouse.service

import com.example.warehouse.persistence.UserPersistence
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Service responsible for loading user-specific data for Spring Security.
 * This class is used internally during authentication to fetch user details.
 */
@Service
class UserService(
    private val userPersistence: UserPersistence
) : UserDetailsService {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    /**
     * Loads user details by email.
     * Called by Spring Security during authentication.
     *
     * @param email user's email used as username
     * @return UserDetails required by Spring Security
     * @throws UsernameNotFoundException if user does not exist
     */
    override fun loadUserByUsername(email: String): UserDetails {

        log.debug("Loading user details for email: {}", email)

        // Fetch user entity from persistence layer
        val user = userPersistence.findByEmail(email)
            ?: run {
                log.warn("User not found while loading UserDetails for email: {}", email)
                throw UsernameNotFoundException("User not found")
            }

        log.info("User details loaded successfully for email: {}", email)

        // Map application user to Spring Security User
        return User(
            user.email,
            user.password,
            listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
        )
    }
}
