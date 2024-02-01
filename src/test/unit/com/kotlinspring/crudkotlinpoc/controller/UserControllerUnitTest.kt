package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.exceptionHandler.GenericHandler
import com.kotlinspring.crudkotlinpoc.exceptions.UserNotFoundException
import com.kotlinspring.crudkotlinpoc.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class UserControllerUnitTest {
    private val baseUrl = "/users"

    private val validBirthDate: LocalDateTime = LocalDateTime.of(2000,2,4, 17,15,28)

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @MockkBean
    lateinit var userServiceMock: UserService

    @Test
    fun shouldStoreUserWithSuccess() {
        val userDTO = UserDTO(null, "v", "Felipe", validBirthDate, listOf("NodeJS", "JS"))

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")
        val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

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
            listOf("NodeJS", "JS")
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.campo, "Length.nick")
        assertEquals(error.message, "O campo não pode exceder 32 caracteres")
    }

    @Test
    fun shouldNotStoreUserWithNameTooLargeValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe".repeat(50),
            validBirthDate,
            listOf("NodeJS", "JS")
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.campo, "Length.name")
        assertEquals(error.message, "O campo não pode exceder 255 caracteres")
    }

    @Test
    fun shouldNotStoreUserWithNameEmptyValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "",
            validBirthDate,
            listOf("NodeJS", "JS")
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.campo, "NotBlank.name")
        assertEquals(error.message, "O campo não pode ser vazio")
    }

    @Test
    fun shouldNotStoreUserWithBirthDateValidationError() {
        val invalidBirthDate = "ameixa-02-04"

        val userDTO = mapOf(
            "id" to null,
            "nick" to "V",
            "name" to "Felipe",
            "birth_date" to invalidBirthDate,
            "stack" to listOf("NodeJS", "JS")
        )

        val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, String::class.java)

        assertEquals(savedUserDTO, "O valor \"$invalidBirthDate\" não é um tipo de Data valido.")
    }

    @Test
    fun shouldNotStoreUserWithEmptyStackValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe",
            validBirthDate,
            listOf("NodeJS", "")
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.campo, "ValidStackList.stack")
        assertEquals(error.message, "Invalid Stack List")
    }

    @Test
    fun shouldRetrieveUserWithSuccess() {
        val userDTO = UserDTO("ID_LEGAL_123", "v", "Felipe", validBirthDate, listOf("NodeJS", "JS"))

        every { userServiceMock.find(any()) } returns userDTO

        val savedUserDTO = testRestTemplate.getForEntity("$baseUrl/ID_LEGAL_123", UserDTO::class.java)

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.OK)


        assertEquals(savedUserDTO.body!!.id, "ID_LEGAL_123")
        assertEquals(userDTO, savedUserDTO.body)
    }

    @Test
    fun shouldRetrieveUserWithNotFoundError() {
        val userId = "ID_LEGAL_123"

        every { userServiceMock.find(any()) } throws UserNotFoundException(userId)

        val savedUserDTO = testRestTemplate.getForEntity("$baseUrl/$userId", String::class.java)

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.NOT_FOUND)
        assertEquals("User not found with id: $userId", savedUserDTO.body)
    }
}