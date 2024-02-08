package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.PaginationResponse
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Validated
class UserController(private val userService: UserService) {
    @GetMapping("/{id}")
    fun find(@PathVariable("id") userId: String) = userService.find(userId)

    @GetMapping("/{id}/stack")
    fun findStacks(@PathVariable("id") userId: String) = userService.findStacks(userId)

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "5") pageSize: Int,
        @RequestParam() sort: String?
    ): ResponseEntity<PaginationResponse<UserDTO>> {
        val (users, hasNext) = userService.findAll(page, pageSize, sort)

        if (hasNext){
            return ResponseEntity.status(206).body(users)
        }

        return ResponseEntity.ok().body(users)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id") userId: String) = userService.delete(userId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun store(@RequestBody @Valid body: UserDTO) = userService.create(body)

    @PutMapping("/{id}")
    fun update(@PathVariable("id") userId: String, @RequestBody @Valid body: UserDTO) = userService.update(userId, body)
}