package com.kotlinspring.crudkotlinpoc.decorators

import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.validators.ValidStackListValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidStackListValidator::class])
@MustBeDocumented
annotation class ValidStackList(
    val message: String = "Invalid Stack List",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<UserDTO>> = []
)