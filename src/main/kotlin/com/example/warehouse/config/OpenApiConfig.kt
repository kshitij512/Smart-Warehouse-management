package com.example.warehouse.config


import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    private val securitySchemeName = "bearerAuth"

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Smart Warehouse & Order Fulfillment API")
                    .version("1.0")
                    .description(
                        """
                        Smart Warehouse & Order Fulfillment Management System.
                        
                        Roles:
                        - ADMIN
                        - WAREHOUSE_MANAGER
                        - STAFF
                        
                        Authentication:
                        - Access token returned in login response
                        - Refresh token stored in HttpOnly cookie
                        """
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter JWT access token here")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
    }
}