package com.kotlinspring.crudkotlinpoc.mappers

import com.kotlinspring.crudkotlinpoc.dto.InterviewResponseDTO
import com.kotlinspring.crudkotlinpoc.entitiy.Interview

fun Interview.toResponseDTO(): InterviewResponseDTO {
    return InterviewResponseDTO(this.id!!, this.jobId, this.userId, this.interviewDate)
}
