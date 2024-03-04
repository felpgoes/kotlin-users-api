package com.kotlinspring.crudkotlinpoc.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class JobRequirementLevelDTO (
    val min: Int,
    val max: Int?
)

data class JobRequirementDTO (
    val stack: String,
    val level: JobRequirementLevelDTO
)

data class JobDTO (
    val id: String?,

    @get:Length(max = 500, message = "O campo não pode exceder 500 caracteres")
    val name: String,

    @get:NotBlank(message = "O campo não pode ser vazio")
    val description: String,

    @get:NotNull(message = "O campo não pode ser nulo")
    val salary: Int,

    @Valid
    val requirements: MutableSet<JobRequirementDTO>
)
