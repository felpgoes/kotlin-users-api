package com.kotlinspring.crudkotlinpoc.dto

import com.kotlinspring.crudkotlinpoc.decorators.NotBlankNullable
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class JobRequirementLevelDTO (
    @field:NotNull(message = "O campo não pode ser nulo")
    @field:Min(0, message = "O level deve ser maior que zero")
    val min: Int,

    val max: Int?
)

data class JobRequirementDTO (
    @get:NotBlank(message = "O campo não pode ser vazio")
    val stack: String,

    @get:Valid
    val level: JobRequirementLevelDTO
)

data class JobDTO (
    val id: String?,

    @get:Length(max = 500, message = "O campo não pode exceder 500 caracteres")
    @get:NotBlank(message = "O campo não pode ser vazio")
    val name: String,

    @NotBlankNullable
    val description: String?,

    @get:NotNull(message = "O campo não pode ser nulo")
    @get:Min(0, message = "O salário deve ser maior que zero")
    val salary: Int,

    @get:Valid
    @get:NotEmpty(message = "Os requisitos da vagas são obrigatorios.")
    val requirements: MutableSet<JobRequirementDTO>
)
