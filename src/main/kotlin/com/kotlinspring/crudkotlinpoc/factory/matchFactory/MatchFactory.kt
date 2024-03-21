package com.kotlinspring.crudkotlinpoc.factory.matchFactory


import com.kotlinspring.crudkotlinpoc.dto.PaginationDTO
import com.kotlinspring.crudkotlinpoc.dto.PaginationResponse
import com.kotlinspring.crudkotlinpoc.repository.JobRepository
import com.kotlinspring.crudkotlinpoc.repository.UserRepository
import com.kotlinspring.crudkotlinpoc.service.MatchJobsService
import com.kotlinspring.crudkotlinpoc.service.MatchUsersService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MatchFactory(
    private val jobRepository: JobRepository,
    private val userRepository: UserRepository,
) {

    enum class MatchOptions {
        USER,
        JOB
    }

    private fun getHandler(name: MatchOptions): MatchServiceInterface<out Any, out Any> =
        when (name) {
            MatchOptions.USER -> MatchJobsService(jobRepository, userRepository)
            MatchOptions.JOB -> MatchUsersService(userRepository, jobRepository)
        }

    fun <M> match(id: String, page: Int, size: Int, sort: String?, handlerType: MatchOptions): PaginationDTO<M> {
        val handler = getHandler(handlerType)


        var pageRequest = PageRequest.of(page, size)

        if (!sort.isNullOrEmpty()) {
            val rawType = sort[0].toString()
            val type = if (rawType == "-") Sort.Direction.DESC else Sort.Direction.ASC
            val field = sort.replace(rawType, "")
            pageRequest = pageRequest.withSort(Sort.by(type, field))
        }

        val total = handler.count()

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
        val jobs = handler.findByMatch(id, pageRequest)

        return PaginationDTO(
            PaginationResponse(
                jobs.content as List<M>,
                jobs.pageable.pageNumber,
                jobs.pageable.pageSize,
                total
            ),
            jobs.hasNext()
        )
    }

}