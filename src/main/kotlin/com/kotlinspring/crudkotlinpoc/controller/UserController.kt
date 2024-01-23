package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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
    ) = userService.findAll(page, size)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id") userId: String) = userService.delete(userId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun store(@RequestBody @Valid body: UserDTO) = userService.create(body)

    @PutMapping("/{id}")
    fun update(@PathVariable("id") userId: String, @RequestBody @Valid body: UserDTO) = userService.update(userId, body)
}