package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.InterviewRequestDTO
import com.kotlinspring.crudkotlinpoc.service.InterviewService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/interview")
class InterviewController (private val interviewService: InterviewService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun store(@RequestBody @Valid body: InterviewRequestDTO) = interviewService.create(body)
}