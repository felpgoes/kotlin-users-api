package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
@Validated
class UserController(private val userService: UserService) {
    @GetMapping("/{id}")
    fun find(@PathVariable("id") userId: String) = userService.find(userId)

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ) = run {
        val users = userService.findAll(page, size)
        mapOf("content" to users, "page" to page, "size" to size, "quantity" to users.size)
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