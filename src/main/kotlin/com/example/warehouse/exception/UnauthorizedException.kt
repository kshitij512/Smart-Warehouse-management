package com.example.warehouse.exception

import org.springframework.http.HttpStatus

class UnauthorizedException(message: String) :
    ApiException(HttpStatus.UNAUTHORIZED, message)
