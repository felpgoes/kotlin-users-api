package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.StackDTO
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.exceptions.UserNotFoundException
import com.kotlinspring.crudkotlinpoc.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import java.time.LocalDateTime

@WebMvcTest(controllers = [UserController::class])
class UserControllerUnitTest {
    @MockkBean
    private lateinit var userServiceMock: UserService

    @Autowired
    private lateinit var userController: UserController

    private val validBirthDate: LocalDateTime = LocalDateTime.of(2000,2,4, 17,15,28)

    @Test
    fun shouldStoreUserWithSuccess() {
        val userDTO = UserDTO(
            null,
            "v",
            "Felipe",
            validBirthDate,
            mutableSetOf(StackDTO("NodeJS", 99), StackDTO("JS", 100))
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")
        val savedUserDTO = userController.store(userDTO)

        assertNotNull(savedUserDTO)
        assertEquals(savedUserDTO::class, UserDTO::class)
        assertEquals(savedUserDTO.name, userDTO.name)
        assertEquals(savedUserDTO.id, "aaa")
    }

    @Test
    fun shouldNotStoreUserWithNickValidationError() {
        val userDTO = UserDTO(
            null,
            "vapo".repeat(10),
            "Felipe",
            validBirthDate,
            mutableSetOf(StackDTO("NodeJS", 99), StackDTO("JS", 100))
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val rawError = assertThrows<ConstraintViolationException> { userController.store(userDTO) }
        val error = rawError.constraintViolations.first()

        assertNotNull(error)
        assertEquals(error.propertyPath.toString(), "store.body.nick")
        assertEquals(error.constraintDescriptor.annotation.annotationClass.toString(), "class org.hibernate.validator.constraints.Length")
        assertEquals(error.messageTemplate, "O campo não pode exceder 32 caracteres")
    }

    @Test
    fun shouldNotStoreUserWithNameTooLargeValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe".repeat(50),
            validBirthDate,
            mutableSetOf(StackDTO("NodeJS", 99), StackDTO("JS", 100))
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val rawError = assertThrows<ConstraintViolationException> { userController.store(userDTO) }
        val error = rawError.constraintViolations.first()

        assertNotNull(error)
        assertEquals(error.propertyPath.toString(), "store.body.name")
        assertEquals(error.constraintDescriptor.annotation.annotationClass.toString(), "class org.hibernate.validator.constraints.Length")
        assertEquals(error.messageTemplate, "O campo não pode exceder 255 caracteres")
    }

    @Test
    fun shouldNotStoreUserWithNameEmptyValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "",
            validBirthDate,
            mutableSetOf(StackDTO("NodeJS", 99), StackDTO("JS", 100))
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val rawError = assertThrows<ConstraintViolationException> { userController.store(userDTO) }
        val error = rawError.constraintViolations.first()

        assertNotNull(error)
        assertEquals(error.constraintDescriptor.annotation.annotationClass.toString(), "class jakarta.validation.constraints.NotBlank")
        assertEquals(error.propertyPath.toString(), "store.body.name")
        assertEquals(error.message, "O campo não pode ser vazio")
    }


    @Test
    fun shouldNotStoreUserWithEmptyStackValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe",
            validBirthDate,
            mutableSetOf(StackDTO("NodeJS", 77), StackDTO("", 1))
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val rawError = assertThrows<ConstraintViolationException> { userController.store(userDTO) }
        val error = rawError.constraintViolations.first()

        assertNotNull(error)
        assertEquals(error.constraintDescriptor.annotation.annotationClass.toString(), "class com.kotlinspring.crudkotlinpoc.decorators.ValidStackList")
        assertEquals(error.propertyPath.toString(), "store.body.stack")
        assertEquals(error.message, "Invalid Stack List")
    }

    @Test
    fun shouldNotStoreUserWithLevelGreaterThen100StackValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe",
            validBirthDate,
            mutableSetOf(StackDTO("NodeJS", 77), StackDTO("Swift", 999))
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val rawError = assertThrows<ConstraintViolationException> { userController.store(userDTO) }
        val error = rawError.constraintViolations.first()

        assertNotNull(error)
        assertEquals(error.constraintDescriptor.annotation.annotationClass.toString(), "class com.kotlinspring.crudkotlinpoc.decorators.ValidStackList")
        assertEquals(error.propertyPath.toString(), "store.body.stack")
        assertEquals(error.message, "Invalid Stack List")
    }

    @Test
    fun shouldNotStoreUserWithLevelLowerThen1StackValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe",
            validBirthDate,
            mutableSetOf(StackDTO("NodeJS", 0))
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val rawError = assertThrows<ConstraintViolationException> { userController.store(userDTO) }
        val error = rawError.constraintViolations.first()

        assertNotNull(error)
        assertEquals(error.constraintDescriptor.annotation.annotationClass.toString(), "class com.kotlinspring.crudkotlinpoc.decorators.ValidStackList")
        assertEquals(error.propertyPath.toString(), "store.body.stack")
        assertEquals(error.message, "Invalid Stack List")
    }
    @Test
    fun shouldRetrieveUserWithSuccess() {
        val id = "ID_LEGAL_123"
        val userDTO = UserDTO(id, "v", "Felipe", validBirthDate, mutableSetOf(StackDTO("NodeJS", 99), StackDTO("JS", 100)))

        every { userServiceMock.find(any()) } returns userDTO

        val retrievedUser = userController.find(id)

        assertNotNull(retrievedUser)
        assertEquals(retrievedUser.id, id)
        assertEquals(userDTO, retrievedUser)
    }

    @Test
    fun shouldRetrieveUserWithNotFoundError() {
        val userId = "99999-ID"
        every { userServiceMock.find(any()) } throws UserNotFoundException(userId)

        val error = assertThrows<UserNotFoundException> { userController.find(userId) }

        assertNotNull(error)
        assertEquals(error::class, UserNotFoundException::class)
        assertEquals(error.message, "User not found with id: $userId")
    }
}