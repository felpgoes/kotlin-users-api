package com.kotlinspring.crudkotlinpoc.exceptionHandler

import com.kotlinspring.crudkotlinpoc.exceptions.UserNotFoundException
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.format.DateTimeParseException

@Component
@ControllerAdvice
class GenericHandler {
    companion object : KLogging()

    data class InvalidFieldResponse (
        val campo: String,
        val message: String
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<List<InvalidFieldResponse>> {
        logger.error("Method Argument Not Valid Exception Exception observed: ${ex.message}", ex)

        val errors = ex.bindingResult.allErrors
            .map { error -> InvalidFieldResponse(error.codes?.get(1) ?: "Desconhecido", error.defaultMessage!!) }
            .sortedBy { it.campo }

        logger.info("errors: $errors")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateTimeParseException(ex: DateTimeParseException, request: WebRequest): ResponseEntity<Any> {
        logger.error("DateTime Parse Exception observed: ${ex.parsedString}", ex)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("A data ${ex.cause} não corresponde ao tipo ISO8601")
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handle(ex: UserNotFoundException, request: WebRequest): ResponseEntity<Any> {
        logger.error("User Not Found Exception observed: ${ex.message}", ex)

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.message)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<Any> {
        logger.error("Http Message Not Readable Exception observed: ${ex.message}", ex)
        if(ex.mostSpecificCause is DateTimeParseException) {

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("O valor \"${(ex.mostSpecificCause as DateTimeParseException).parsedString}\" não é um tipo de Data valido.")
        }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        logger.error("Exception observed: ${ex.message}", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ex.message)
    }

}