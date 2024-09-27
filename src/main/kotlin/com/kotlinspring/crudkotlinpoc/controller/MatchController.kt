package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.JobDTO
import com.kotlinspring.crudkotlinpoc.dto.PaginationResponse
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.factory.matchFactory.MatchFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping
class MatchController (private val matchFactory: MatchFactory){

    private fun <T> match(id: String, page: Int, size: Int, sort: String?, type: MatchFactory.MatchOptions): ResponseEntity<PaginationResponse<T>> {
        val (users, hasNext) = matchFactory.match<T>(id, page, size, sort, type)


        if (hasNext){
            return ResponseEntity.status(206).body(users)
        }

        return ResponseEntity.ok().body(users)
    }

    @GetMapping("jobs/{id}/match")
    fun matchJobs(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
        @RequestParam() sort: String?,
        @PathVariable("id") jobId: String
    ): ResponseEntity<PaginationResponse<UserDTO>> = match(jobId, page, pageSize, sort, MatchFactory.MatchOptions.USER)

    @GetMapping("users/{id}/match")
    fun matchUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
        @RequestParam() sort: String?,
        @PathVariable("id") userId: String
    ): ResponseEntity<PaginationResponse<JobDTO>> = match(userId, page, pageSize, sort, MatchFactory.MatchOptions.JOB)
}