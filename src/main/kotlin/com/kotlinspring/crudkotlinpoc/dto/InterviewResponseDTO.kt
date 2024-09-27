package com.kotlinspring.crudkotlinpoc.dto

import java.time.LocalDateTime

data class InterviewResponseDTO (
    val id: String,
    val jobId: String,
    val userId: String,
    val interviewDate: LocalDateTime,
)

