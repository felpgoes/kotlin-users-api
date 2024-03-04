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

    enum class ErrorsCodesEnum {
        MethodArgumentNotValidException,
        DateTimeParseException,
        UserNotFoundException,
        HttpMessageNotReadableException,
        Exception,
    }

    data class ErrorResponse(
        val errorMessages: List<ErrorMessage>,
    ) {
        constructor(errorMessage: ErrorMessage) : this(listOf(errorMessage))
    }

    data class ErrorMessage(
        val code: ErrorsCodesEnum,
        val description: String,
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Method Argument Not Valid Exception Exception observed: ${ex.message}", ex)

        val errors = ex.bindingResult.allErrors
            .map { error -> ErrorMessage(ErrorsCodesEnum.MethodArgumentNotValidException, error.defaultMessage!!) }
            .sortedBy { it.code }

        logger.info("errors: $errors")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(errors))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handle(ex: UserNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("User Not Found Exception observed: ${ex.message}", ex)
        val response =
            ErrorResponse(ErrorMessage(ErrorsCodesEnum.UserNotFoundException, ex.message ?: "User Not Found"))

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Http Message Not Readable Exception observed: ${ex.message}", ex)
        if (ex.mostSpecificCause is DateTimeParseException) {
            val response = ErrorResponse(
                ErrorMessage(
                    ErrorsCodesEnum.DateTimeParseException,
                    "O valor \"${(ex.mostSpecificCause as DateTimeParseException).parsedString}\" não é um tipo de Data valido."
                )
            )

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response)
        }

        val response = ErrorResponse(
            ErrorMessage(ErrorsCodesEnum.HttpMessageNotReadableException, ex.message ?: "Cannot Read Http Message")
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Exception observed: ${ex.message}", ex)
        val response = ErrorResponse(
            ErrorMessage(ErrorsCodesEnum.Exception, ex.message ?: "Internal server error")
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }

}