package com.kotlinspring.crudkotlinpoc.service

import com.kotlinspring.crudkotlinpoc.dto.InterviewRequestDTO
import com.kotlinspring.crudkotlinpoc.dto.InterviewResponseDTO
import com.kotlinspring.crudkotlinpoc.mappers.toEntity
import com.kotlinspring.crudkotlinpoc.mappers.toResponseDTO
import com.kotlinspring.crudkotlinpoc.repository.InterviewRepository
import org.springframework.stereotype.Service

@Service
class InterviewService (private val interviewRepository: InterviewRepository) {
    fun create(body: InterviewRequestDTO): InterviewResponseDTO {
        val interview = body.toEntity()
        val response = interviewRepository.save(interview)

        return response.toResponseDTO()
    }
}