package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.JobDTO
import com.kotlinspring.crudkotlinpoc.dto.PaginationResponse
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.service.JobService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/jobs")
class JobController (private val jobService: JobService) {

    // GET /jobs - Retorna uma lista de vagas de emprego
    @GetMapping()
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
        @RequestParam() sort: String?
    ): ResponseEntity<PaginationResponse<JobDTO>> {
        val (users, hasNext) = jobService.findAll(page, pageSize, sort)

        if (hasNext){
            return ResponseEntity.status(206).body(users)
        }

        return ResponseEntity.ok().body(users)
    }

    @GetMapping("/{id}")
    fun find(@PathVariable("id") jobId: String) = jobService.find(jobId)

    // PUT /jobs/{id} - Altera uma vaga de emprego
    @PutMapping("/{id}")
    fun update(@PathVariable("id") jobId: String, @RequestBody @Valid body: JobDTO) = jobService.update(jobId, body)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id") jobId: String) = jobService.delete(jobId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun store(@RequestBody @Valid body: JobDTO) = jobService.create(body)

    // GET /jobs/{id}/match?page_size=30&page=3&sort=-name
    @GetMapping("/{id}/match")
    fun match(@PathVariable("id") jobId: String) = "Not implemented"
}