package com.kotlinspring.crudkotlinpoc.service

import com.kotlinspring.crudkotlinpoc.dto.*
import com.kotlinspring.crudkotlinpoc.exceptions.JobNotFoundException
import com.kotlinspring.crudkotlinpoc.mappers.toDTO
import com.kotlinspring.crudkotlinpoc.mappers.toEntity
import com.kotlinspring.crudkotlinpoc.repository.JobRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class JobService (private val jobRepository: JobRepository) {
    fun create(body: JobDTO): JobDTO {
        val result = jobRepository.save(body.toEntity())

        return result.toDTO()
    }

    fun findAll(page: Int, size: Int, sort: String?): PaginationDTO<JobDTO> {
        var pageRequest = PageRequest.of(page, size)

        if (!sort.isNullOrEmpty()) {
            val rawType = sort[0].toString()
            val type = if (rawType == "-") Sort.Direction.DESC else Sort.Direction.ASC
            val field = sort.replace(rawType, "")
            pageRequest = pageRequest.withSort(Sort.by(type, field))
        }
        val total = jobRepository.count()

        if (total == 0L) {
            return PaginationDTO(
                PaginationResponse(
                    emptyList(),
                    page,
                    size,
                    total
                ),
                false
            )
        }

        val jobs = jobRepository
            .findAll(pageRequest)
            .map { it.toDTO() }

        return PaginationDTO(
            PaginationResponse(
                jobs.content,
                jobs.pageable.pageNumber,
                jobs.pageable.pageSize,
                total
            ),
            jobs.hasNext()
        )
    }

    fun find(jobId: String): JobDTO {
        return jobRepository
            .findById(jobId)
            .orElseThrow { JobNotFoundException(jobId) }
            .toDTO()
    }

    fun update(jobId: String, body: JobDTO): JobDTO {

        // TESTAR
        val oldJob = jobRepository
            .findById(jobId)
            .orElseThrow { JobNotFoundException(jobId) }


        val newJob = body.copy(id = jobId).toEntity()

        val requirements = newJob.requirements.map {
            val old = oldJob.requirements.find { it2 -> it2.stack == it.stack }

            it.copy(id = old!!.id)
        }

        newJob.requirements.addAll(requirements)

        return jobRepository.save(newJob).toDTO()
    }

    fun delete(jobId: String) {
        val job = jobRepository
            .findById(jobId)
            .orElseThrow { JobNotFoundException(jobId) }

        jobRepository.delete(job)
    }
}

