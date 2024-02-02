package com.kotlinspring.crudkotlinpoc.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.kotlinspring.crudkotlinpoc.decorators.ValidStackList
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserDTO (
    var id: String?,

    @get:Length(max = 32, message = "O campo não pode exceder 32 caracteres")
    val nick: String?,

    @get:NotBlank(message = "O campo não pode ser vazio")
    @get:Length(max = 255, message = "O campo não pode exceder 255 caracteres")
    val name: String,

    val birthDate: LocalDateTime, // 2024-01-16T21:56:05.197Z

    @ValidStackList
    val stack: List<String>?
) {
    override fun toString(): String {
        return "[id=$id, name=$name, birthDate=$birthDate, stack=${stack?.joinToString { it }}]"
    }
}