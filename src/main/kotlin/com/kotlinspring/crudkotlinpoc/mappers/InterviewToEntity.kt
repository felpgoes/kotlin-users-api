package com.kotlinspring.crudkotlinpoc.mappers

import com.kotlinspring.crudkotlinpoc.dto.InterviewRequestDTO
import com.kotlinspring.crudkotlinpoc.entitiy.Interview

fun InterviewRequestDTO.toEntity(): Interview {
    return Interview(null, this.interviewDate, this.jobId, this.userId)
}
