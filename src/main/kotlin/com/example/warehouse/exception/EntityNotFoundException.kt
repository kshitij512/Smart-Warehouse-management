package com.example.warehouse.exception

import org.springframework.http.HttpStatus

class EntityNotFoundException(message: String) :
    ApiException(HttpStatus.NOT_FOUND, message)
