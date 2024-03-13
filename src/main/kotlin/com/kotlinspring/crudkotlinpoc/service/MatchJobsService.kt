package com.kotlinspring.crudkotlinpoc.service

import com.kotlinspring.crudkotlinpoc.dto.*
import com.kotlinspring.crudkotlinpoc.exceptions.JobNotFoundException
import com.kotlinspring.crudkotlinpoc.factory.matchFactory.MatchServiceInterface
import com.kotlinspring.crudkotlinpoc.mappers.toDTO
import com.kotlinspring.crudkotlinpoc.repository.JobRepository
import com.kotlinspring.crudkotlinpoc.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MatchJobsService(
    private val jobRepository: JobRepository,
    private val userRepository: UserRepository
): MatchServiceInterface<JobDTO, UserDTO> {

    override fun findByMatch(id: String, pageRequest: PageRequest): Page<UserDTO> {
        val job = jobRepository
            .findById(id)
            .orElseThrow { JobNotFoundException(id) }

        return userRepository
            .findAll(pageRequest)
            .map { user ->
                val stack = user.stack.map { st -> StackDTO(st.name, st.level) }
                UserDTO(user.id, user.nick, user.name, user.birthDate, stack.toMutableSet())
            }
    }

    override fun count(): Long = userRepository.count()
}