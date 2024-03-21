package com.kotlinspring.crudkotlinpoc.decorators

import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.validators.NotBlankNullableValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotBlankNullableValidator::class])
@MustBeDocumented
annotation class NotBlankNullable(
    val message: String = "Invalid Not Blank Nullable Value",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<UserDTO>> = []
)