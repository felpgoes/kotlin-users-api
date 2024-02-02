package com.kotlinspring.crudkotlinpoc.validators

import com.kotlinspring.crudkotlinpoc.decorators.ValidStackList
import com.kotlinspring.crudkotlinpoc.dto.StackDTO
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import mu.KLogging

class ValidStackListValidator: ConstraintValidator<ValidStackList, List<StackDTO>> {

    companion object : KLogging()

    override fun isValid(p0: List<StackDTO>?, context: ConstraintValidatorContext?): Boolean {
        if (p0.isNullOrEmpty()) return true

        return p0.all { stack ->
            val invalidName = stack.name.isNullOrEmpty() || stack.name.isBlank() || stack.name.length > 32
            val validScore = stack.score == null || (1..100).contains(stack.score)
            !invalidName && validScore
        }
    }
}