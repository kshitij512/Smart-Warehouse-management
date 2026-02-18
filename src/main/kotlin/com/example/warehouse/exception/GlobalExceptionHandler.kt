package com.example.warehouse.exception

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(
        ex: ApiException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {

        log.warn("API error: {}", ex.message)

        val error = ApiError(
            status = ex.status.value(),
            error = ex.status.name,
            message = ex.message,
            path = request.requestURI
        )

        return ResponseEntity(error, ex.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {

        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        val error = ApiError(
            status = 400,
            error = "BAD_REQUEST",
            message = message,
            path = request.requestURI
        )

        return ResponseEntity.badRequest().body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {

        log.error("Unexpected error", ex)

        val error = ApiError(
            status = 500,
            error = "INTERNAL_SERVER_ERROR",
            message = "Something went wrong",
            path = request.requestURI
        )

        return ResponseEntity.internalServerError().body(error)
    }
}
