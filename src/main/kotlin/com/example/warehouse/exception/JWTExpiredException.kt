package com.example.warehouse.exception

import org.springframework.http.HttpStatus

class JWTExpiredException(
    message: String = "JWT token has expired"
) : ApiException(status = HttpStatus.FORBIDDEN,message)