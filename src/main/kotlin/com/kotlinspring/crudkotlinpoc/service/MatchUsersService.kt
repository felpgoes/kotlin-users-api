package com.kotlinspring.crudkotlinpoc.service

import com.kotlinspring.crudkotlinpoc.dto.*
import com.kotlinspring.crudkotlinpoc.exceptions.UserNotFoundException
import com.kotlinspring.crudkotlinpoc.factory.matchFactory.MatchServiceInterface
import com.kotlinspring.crudkotlinpoc.mappers.toDTO
import com.kotlinspring.crudkotlinpoc.repository.JobRepository
import com.kotlinspring.crudkotlinpoc.repository.UserRepository
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MatchUsersService(
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository
) : MatchServiceInterface<UserDTO, JobDTO> {

    override fun findByMatch(id: String, pageRequest: PageRequest): Page<JobDTO> {
//        val user = userRepository
//            .findById(id)
//            .orElseThrow { UserNotFoundException(id) }

        val jobIds = jobRepository.getMatchedJobsIdByUserId(id)
        val jobs2 = jobRepository.findAllJobsByIdIn(jobIds, pageRequest)

        return jobs2.map { it.toDTO() }
    }

    override fun count(): Long = jobRepository.count()
}