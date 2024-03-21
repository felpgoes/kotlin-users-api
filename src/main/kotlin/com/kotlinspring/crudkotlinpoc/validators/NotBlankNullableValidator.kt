package com.kotlinspring.crudkotlinpoc.validators

import com.kotlinspring.crudkotlinpoc.decorators.NotBlankNullable
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class NotBlankNullableValidator: ConstraintValidator<NotBlankNullable, String?> {
    override fun isValid(p0: String?, context: ConstraintValidatorContext?): Boolean {
        if (p0 == null) return true

        return p0.isNotBlank()
    }
}