package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.exceptionHandler.GenericHandler
import com.kotlinspring.crudkotlinpoc.exceptions.UserNotFoundException
import com.kotlinspring.crudkotlinpoc.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        val savedCourseDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

        Assertions.assertNotNull(savedCourseDTO)
        Assertions.assertEquals(savedCourseDTO::class, UserDTO::class)
        Assertions.assertEquals(savedCourseDTO.name, userDTO.name)
        Assertions.assertEquals(savedCourseDTO.id, "aaa")
    }

    @Test
    fun shouldNotStoreUserWithNickValidationError() {
        val userDTO = UserDTO(
            null,
            "vapovapovapovapovapovapovapovapovapovapovapovapovapovapo",
            "Felipe",
            validBirthDate,
            listOf("NodeJS", "JS")
        )

        every { userServiceMock.create(any()) } returns userDTO.copy(id = "aaa")

        val savedCourseDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        Assertions.assertNotNull(savedCourseDTO.body)
        Assertions.assertEquals(savedCourseDTO.statusCode, HttpStatus.BAD_REQUEST)

        Assertions.assertEquals(savedCourseDTO.body!!.size, 1)

        val error = savedCourseDTO.body!!.first()
        Assertions.assertEquals(error.campo, "Length.nick")
        Assertions.assertEquals(error.message, "O campo não pode exceder 32 caracteres")
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

        val savedCourseDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        Assertions.assertNotNull(savedCourseDTO.body)
        Assertions.assertEquals(savedCourseDTO.statusCode, HttpStatus.BAD_REQUEST)

        Assertions.assertEquals(savedCourseDTO.body!!.size, 1)

        val error = savedCourseDTO.body!!.first()
        Assertions.assertEquals(error.campo, "Length.name")
        Assertions.assertEquals(error.message, "O campo não pode exceder 255 caracteres")
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

        val savedCourseDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        Assertions.assertNotNull(savedCourseDTO.body)
        Assertions.assertEquals(savedCourseDTO.statusCode, HttpStatus.BAD_REQUEST)

        Assertions.assertEquals(savedCourseDTO.body!!.size, 1)

        val error = savedCourseDTO.body!!.first()
        Assertions.assertEquals(error.campo, "NotBlank.name")
        Assertions.assertEquals(error.message, "O campo não pode ser vazio")
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

        val savedCourseDTO = testRestTemplate.postForObject(baseUrl, userDTO, String::class.java)

        Assertions.assertEquals(savedCourseDTO, "O valor \"$invalidBirthDate\" não é um tipo de Data valido.")
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

        val savedCourseDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.InvalidFieldResponse>>() {}
        )

        Assertions.assertNotNull(savedCourseDTO.body)
        Assertions.assertEquals(savedCourseDTO.statusCode, HttpStatus.BAD_REQUEST)

        Assertions.assertEquals(savedCourseDTO.body!!.size, 1)

        val error = savedCourseDTO.body!!.first()
        Assertions.assertEquals(error.campo, "ValidStackList.stack")
        Assertions.assertEquals(error.message, "Invalid Stack List")
    }

    @Test
    fun shouldRetrieveUserWithSuccess() {
        val userDTO = UserDTO("ID_LEGAL_123", "v", "Felipe", validBirthDate, listOf("NodeJS", "JS"))

        every { userServiceMock.find(any()) } returns userDTO

        val savedCourseDTO = testRestTemplate.getForEntity("$baseUrl/ID_LEGAL_123", UserDTO::class.java)

        Assertions.assertNotNull(savedCourseDTO.body)
        Assertions.assertEquals(savedCourseDTO.statusCode, HttpStatus.OK)


        Assertions.assertEquals(savedCourseDTO.body!!.id, "ID_LEGAL_123")
        Assertions.assertEquals(userDTO, savedCourseDTO.body)
    }

    @Test
    fun shouldRetrieveUserWithNotFoundError() {
        val userId = "ID_LEGAL_123"

        every { userServiceMock.find(any()) } throws UserNotFoundException(userId)

        val savedCourseDTO = testRestTemplate.getForEntity("$baseUrl/$userId", String::class.java)

        Assertions.assertNotNull(savedCourseDTO.body)
        Assertions.assertEquals(savedCourseDTO.statusCode, HttpStatus.NOT_FOUND)
        Assertions.assertEquals("User not found with id: $userId", savedCourseDTO.body)
    }
}