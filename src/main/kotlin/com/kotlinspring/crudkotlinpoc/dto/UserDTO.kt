package com.kotlinspring.crudkotlinpoc.dto

import com.kotlinspring.crudkotlinpoc.decorators.ValidStackList
import jakarta.validation.constraints.*
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

data class UserDTO (
    var id: String?,

    @get:Length(max = 32, message = "O campo não pode exceder 32 caracteres")
    val nick: String?,

    @get:NotBlank(message = "O campo não pode ser vazio")
    @get:Length(max = 255, message = "O campo não pode exceder 255 caracteres")
    val name: String,

    val birth_date: LocalDateTime, // 2024-01-16T21:56:05.197Z

    @ValidStackList
    val stack: List<String>?
) {
    override fun toString(): String {
        return "[id=$id, name=$name, birth_date=$birth_date, stack=${stack?.joinToString { it }}]"
    }
}