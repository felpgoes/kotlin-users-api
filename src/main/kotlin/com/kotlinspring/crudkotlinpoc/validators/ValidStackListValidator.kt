package com.kotlinspring.crudkotlinpoc.validators

import com.kotlinspring.crudkotlinpoc.decorators.ValidStackList
import com.kotlinspring.crudkotlinpoc.dto.StackDTO
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import mu.KLogging

class ValidStackListValidator: ConstraintValidator<ValidStackList, MutableSet<StackDTO>> {

    companion object : KLogging()

    override fun isValid(p0: MutableSet<StackDTO>?, context: ConstraintValidatorContext?): Boolean {
        if (p0.isNullOrEmpty()) return true

        return p0.all { stack ->
            val invalidName = stack.name.isEmpty() || stack.name.isBlank() || stack.name.length > 32
            val validLevel = (1..100).contains(stack.level)

            !invalidName && validLevel
        }
    }
}