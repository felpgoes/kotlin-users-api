package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.StackDTO
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.exceptionHandler.GenericHandler
import com.kotlinspring.crudkotlinpoc.utils.OracleContainerInitializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils
import java.time.LocalDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntgTest : OracleContainerInitializer() {

    private val baseUrl = "/users"

    private val validBirthDate: LocalDateTime = LocalDateTime.of(2000,2,4, 17,15,28)

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @AfterEach
    fun setDown(@Autowired jdbcTemplate: JdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_stacks")
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users")
    }

    @Test
    fun shouldStoreUserWithSuccess() {
        val userDTO = UserDTO(null, "v", "Felipe", validBirthDate, listOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
        val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

        assertNotNull(savedUserDTO)
        assertEquals(savedUserDTO::class, UserDTO::class)
        assertEquals(savedUserDTO.name, userDTO.name)
        assertNotNull(savedUserDTO.id)
    }

    @Test
    fun shouldNotStoreUserWithNickValidationError() {
        val userDTO = UserDTO(
            null,
            "vapo".repeat(10),
            "Felipe",
            validBirthDate,
            listOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
        )
        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.ErrorMessage>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.code, GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException)
        assertEquals(error.description, "O campo não pode exceder 32 caracteres")
    }

    @Test
    fun shouldNotStoreUserWithNameTooLargeValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe".repeat(50),
            validBirthDate,
            listOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
        )
        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.ErrorMessage>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.code, GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException)
        assertEquals(error.description, "O campo não pode exceder 255 caracteres")
    }

    @Test
    fun shouldNotStoreUserWithNameEmptyValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "",
            validBirthDate,
            listOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
        )

        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.ErrorMessage>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.code, GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException)
        assertEquals(error.description, "O campo não pode ser vazio")
    }

    @Test
    fun shouldNotStoreUserWithBirthDateValidationError() {
        val invalidBirthDate = "ameixa-02-04"

        val userDTO = mapOf(
            "id" to null,
            "nick" to "V",
            "name" to "Felipe",
            "birth_date" to invalidBirthDate,
            "stack" to listOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
        )

        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.ErrorMessage>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)
        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.code, GenericHandler.ErrorsCodesEnum.DateTimeParseException)
        assertEquals(error.description, "O valor \"$invalidBirthDate\" não é um tipo de Data valido.")
    }

    @Test
    fun shouldNotStoreUserWithEmptyStackValidationError() {
        val userDTO = UserDTO(
            null,
            "V",
            "Felipe",
            validBirthDate,
            listOf(StackDTO("NodeJS", 10), StackDTO("", 10))
        )
        val savedUserDTO = testRestTemplate.exchange(
            RequestEntity.post(baseUrl).body(userDTO),
            object: ParameterizedTypeReference<List<GenericHandler.ErrorMessage>>() {}
        )

        assertNotNull(savedUserDTO.body)
        assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

        assertEquals(savedUserDTO.body!!.size, 1)

        val error = savedUserDTO.body!!.first()
        assertEquals(error.code, GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException)
        assertEquals(error.description, "Invalid Stack List")
    }

    @Test
    fun shouldRetrieveUserWithSuccess() {
        val userDTO = UserDTO(null, "v", "Felipe", validBirthDate, listOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
        val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

        val retrievedUserDTO = testRestTemplate.getForEntity<UserDTO>("$baseUrl/${savedUserDTO.id}")

        assertNotNull(retrievedUserDTO.body)
        assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
        assertEquals(retrievedUserDTO.body!!.id, savedUserDTO.id)
        assertEquals(savedUserDTO, retrievedUserDTO.body)
    }

    @Test
    fun shouldRetrieveUserWithNotFoundError() {
        val userId = "ID_LEGAL_123"
        val retrievedUser = testRestTemplate.getForEntity<List<LinkedHashMap<String, String>>>("$baseUrl/$userId")

        assertNotNull(retrievedUser.body)
        assertEquals(retrievedUser.statusCode, HttpStatus.NOT_FOUND)
        assertEquals(retrievedUser.body!!.size, 1)
        assertEquals(retrievedUser.body!!.first()::class, LinkedHashMap::class)

        val error = retrievedUser.body!!.first()
        assertEquals(error["code"], GenericHandler.ErrorsCodesEnum.UserNotFoundException.toString())
        assertEquals(error["description"], "User not found with id: ID_LEGAL_123")
    }
}