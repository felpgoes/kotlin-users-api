package com.kotlinspring.crudkotlinpoc.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

data class InterviewRequestDTO (
    @get:NotBlank(message = "O campo não pode ser vazio")
    @get:Length(max = 36, message = "O campo deve conter um UUID valido")
    val jobId: String,

    @get:NotBlank(message = "O campo não pode ser vazio")
    @get:Length(max = 36, message = "O campo deve conter um UUID valido")
    val userId: String,

    val interviewDate: LocalDateTime,
)