package de.tforneberg.patchdb.error

import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun handleException(exception: BadRequestException?): ResponseEntity<GlobalBadRequestResponse> {
        val response:GlobalBadRequestResponse = exception?.let {
            GlobalBadRequestResponse(exception.bindingResult)
        } ?: GlobalBadRequestResponse("", "Bad request")
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler
    fun handleException(e: ResourceNotFoundException?): ResponseEntity<GlobalBadRequestResponse> {
        val response = GlobalBadRequestResponse("", "Resource not found")
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler
    fun handleException(e: Exception?): ResponseEntity<GlobalBadRequestResponse> {
        val response = GlobalBadRequestResponse("", "Bad request")
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }
}