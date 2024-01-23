package com.kotlinspring.crudkotlinpoc.validators

import com.kotlinspring.crudkotlinpoc.decorators.ValidStackList
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import mu.KLogging

class ValidStackListValidator: ConstraintValidator<ValidStackList, List<String>> {

    companion object : KLogging()

    override fun isValid(p0: List<String>?, context: ConstraintValidatorContext?): Boolean {
        if (p0.isNullOrEmpty()) return true

        return p0.none { it.isNullOrEmpty() || it.isBlank() || it.length > 32 }
    }
}