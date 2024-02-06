package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.PaginationResponse
import com.kotlinspring.crudkotlinpoc.dto.StackDTO
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.exceptionHandler.GenericHandler
import com.kotlinspring.crudkotlinpoc.utils.OracleContainerInitializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.jdbc.JdbcTestUtils
import java.net.URI
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntgTest : OracleContainerInitializer() {

    private val baseUrl = "/users"

    private val validBirthDate: LocalDateTime = LocalDateTime.of(2000, 2, 4, 17, 15, 28)

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @AfterEach
    fun setDown(@Autowired jdbcTemplate: JdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "user_stacks")
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users")
    }

    @Nested
    inner class FindById {
        @Test
        fun shouldRetrieveUserWithSuccess() {
            val userDTO =
                UserDTO(null, "v", "Felipe", validBirthDate, mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
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
            val retrievedUser = testRestTemplate.getForEntity<GenericHandler.ErrorResponse>("$baseUrl/$userId")

            assertNotNull(retrievedUser.body)
            assertEquals(retrievedUser.statusCode, HttpStatus.NOT_FOUND)
            assertEquals(retrievedUser.body!!.errorMessages.size, 1)
            assertEquals(retrievedUser.body!!.errorMessages.first()::class, GenericHandler.ErrorMessage::class)

            val error = retrievedUser.body!!.errorMessages.first()
            assertEquals(error.code, GenericHandler.ErrorsCodesEnum.UserNotFoundException)
            assertEquals(error.description, "User not found with id: ID_LEGAL_123")
        }
    }

    @Nested
    inner class Store {
        @Test
        fun shouldStoreUserWithSuccess() {
            val userDTO =
                UserDTO(null, "v", "Felipe", validBirthDate, mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
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
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO = testRestTemplate.exchange(
                RequestEntity.post(baseUrl).body(userDTO),
                object : ParameterizedTypeReference<GenericHandler.ErrorResponse>() {}
            )

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertEquals(savedUserDTO.body!!.errorMessages.size, 1)

            val error = savedUserDTO.body!!.errorMessages.first()
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
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO = testRestTemplate.exchange(
                RequestEntity.post(baseUrl).body(userDTO),
                object : ParameterizedTypeReference<GenericHandler.ErrorResponse>() {}
            )

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertEquals(savedUserDTO.body!!.errorMessages.size, 1)

            val error = savedUserDTO.body!!.errorMessages.first()
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
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )

            val savedUserDTO = testRestTemplate.exchange(
                RequestEntity.post(baseUrl).body(userDTO),
                object : ParameterizedTypeReference<GenericHandler.ErrorResponse>() {}
            )

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertEquals(savedUserDTO.body!!.errorMessages.size, 1)

            val error = savedUserDTO.body!!.errorMessages.first()
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
                "stack" to mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )

            val savedUserDTO = testRestTemplate.exchange(
                RequestEntity.post(baseUrl).body(userDTO),
                object : ParameterizedTypeReference<GenericHandler.ErrorResponse>() {}
            )

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)
            assertEquals(savedUserDTO.body!!.errorMessages.size, 1)

            val error = savedUserDTO.body!!.errorMessages.first()
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
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("", 10))
            )
            val savedUserDTO = testRestTemplate.exchange(
                RequestEntity.post(baseUrl).body(userDTO),
                object : ParameterizedTypeReference<GenericHandler.ErrorResponse>() {}
            )

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertEquals(savedUserDTO.body!!.errorMessages.size, 1)

            val error = savedUserDTO.body!!.errorMessages.first()
            assertEquals(error.code, GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException)
            assertEquals(error.description, "Invalid Stack List")
        }
    }

    @Nested
    inner class Update {
        @Test
        fun shouldUpdateUserWithSuccess() {
            val userDTO = UserDTO(
                null,
                "vvv",
                "Felipe Goes",
                validBirthDate,
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)
            val updateBody = savedUserDTO.copy(name = "Goes")
            println("updateBody=$updateBody")

            val result = testRestTemplate.exchange(
                RequestEntity<UserDTO>(
                    updateBody,
                    HttpMethod.PUT,
                    URI("$baseUrl/${savedUserDTO.id}")
                ), UserDTO::class.java
            )

            println(result)
            val retrievedUserDTO = testRestTemplate.getForEntity<UserDTO>("$baseUrl/${savedUserDTO.id}")

            println("retrievedUserDTO=$retrievedUserDTO")
            assertNotNull(retrievedUserDTO)
            assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
            assertEquals(retrievedUserDTO.body!!.id, savedUserDTO.id)
            assertEquals(retrievedUserDTO.body!!.name, "Goes")
            assertNotEquals(retrievedUserDTO.body!!.name, savedUserDTO.name)
            assertEquals(updateBody, retrievedUserDTO.body)
        }

        @Test
        fun shouldNotUpdateUserWithNotFoundError() {
            val userId = "ID_LEGAL_123"
            val updateBody = UserDTO(
                null,
                "vvv",
                "Felipe Goes",
                validBirthDate,
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )

            val result = testRestTemplate.exchange(
                RequestEntity<UserDTO>(
                    updateBody,
                    HttpMethod.PUT,
                    URI("$baseUrl/$userId")
                ), GenericHandler.ErrorResponse::class.java
            )

            assertNotNull(result.body)
            assertEquals(result.statusCode, HttpStatus.NOT_FOUND)
            assertEquals(result.body!!.errorMessages.size, 1)
            assertEquals(result.body!!.errorMessages.first()::class, GenericHandler.ErrorMessage::class)

            val error = result.body!!.errorMessages.first()
            assertEquals(error.code, GenericHandler.ErrorsCodesEnum.UserNotFoundException)
            assertEquals(error.description, "User not found with id: $userId")
        }
    }

    @Nested
    inner class FindStack {
        @Test
        fun shouldRetrieveUserStacksWithSuccess() {
            val userDTO =
                UserDTO(null, "v", "Felipe", validBirthDate, mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

            val retrievedUserDTO = testRestTemplate.getForEntity<List<StackDTO>>("$baseUrl/${savedUserDTO.id}/stack")

            assertNotNull(retrievedUserDTO.body)
            assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
            assertEquals(retrievedUserDTO.body!!.size, userDTO.stack!!.size)
            assertEquals(retrievedUserDTO.body!![1], mapOf("name" to "NodeJS", "level" to 10))
            assertEquals(retrievedUserDTO.body!![0], mapOf("name" to "JS", "level" to 10))
        }

        @Test
        fun shouldNotRetrieveUserStacksWithNotFoundError() {
            val userId = "ID_LEGAL_123"
            val result = testRestTemplate.getForEntity<GenericHandler.ErrorResponse>("$baseUrl/$userId/stack")

            assertNotNull(result.body)
            assertEquals(result.statusCode, HttpStatus.NOT_FOUND)
            assertEquals(result.body!!.errorMessages.size, 1)
            assertEquals(result.body!!.errorMessages.first()::class, GenericHandler.ErrorMessage::class)

            val error = result.body!!.errorMessages.first()
            assertEquals(error.code, GenericHandler.ErrorsCodesEnum.UserNotFoundException)
            assertEquals(error.description, "User not found with id: $userId")
        }

        @Test
        fun shouldRetrieveUserWithEmptyStacksWithSuccess() {
            val userDTO = UserDTO(null, "v", "Felipe", validBirthDate, null)
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)
            val retrievedUserDTO = testRestTemplate.getForEntity<List<StackDTO>>("$baseUrl/${savedUserDTO.id}/stack")

            assertNotNull(retrievedUserDTO.body)
            assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
            assertEquals(retrievedUserDTO.body!!.size, 0)
        }

    }


    private fun createUsers(quantity: Int): List<UserDTO> {
        val users = mutableListOf<UserDTO>()

        for (i in 1..quantity) {
            val user = UserDTO(
                null,
                "F$i",
                "Felipe$i",
                validBirthDate,
                mutableSetOf(
                    StackDTO("Lang A", 99),
                    StackDTO("Lang B", 99)
                )
            )

            val saved = testRestTemplate.postForObject(baseUrl, user, UserDTO::class.java)
            println("------------ \n INDEX = $i \n --------------")
            println(saved)
            users.add(saved)
        }

        return users
    }

    private fun getValueByPropertyName(user: UserDTO, property: String): String {
        val prop = user::class.members.first { it.name == property } as KProperty1<UserDTO, *>
        return prop.get(user).toString()
    }

    @ParameterizedTest
    @MethodSource("findWithSorts")
    fun shouldRetrieveUserStacksWithSuccess(sort: Direction, field: String, size: Int, quantity: Int) {
        val users = createUsers(quantity)

        val direction = if (sort.isDescending) "-" else "+"

        val searchUrl = "$baseUrl?page=0&size=$size&sort=$direction$field"
        val retrievedUserDTO = testRestTemplate.getForEntity<PaginationResponse<LinkedHashMap<String, Any>>>(searchUrl)

        assertNotNull(retrievedUserDTO.body)
        assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
        assertEquals(retrievedUserDTO.body!!.total, quantity.toLong())
        assertEquals(retrievedUserDTO.body!!.records.size, quantity)
        assertTrue(retrievedUserDTO.body!!.records.size <= size)


        val sortedUsers = if (sort.isDescending) {
            users.sortedWith(
                compareBy(
                    { getValueByPropertyName(it, field) },
                    { getValueByPropertyName(it, field) }
                )
            ).reversed()
        } else {
            users.sortedWith(
                compareBy(
                    { getValueByPropertyName(it, field) },
                    { getValueByPropertyName(it, field) }
                )
            )
        }

        println(sortedUsers)
        println(retrievedUserDTO.body!!.records)
        assertEquals(sortedUsers.map {it.id}, retrievedUserDTO.body!!.records.map { it["id"] })
    }


    @Test
    fun shouldRetrievePaginationWithEmptyRecordsWithSuccess() {
        val retrievedUserDTO = testRestTemplate.getForEntity<PaginationResponse<UserDTO>>(baseUrl)

        assertNotNull(retrievedUserDTO.body)
        assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
        assertEquals(retrievedUserDTO.body!!.records.size, 0)
        assertEquals(retrievedUserDTO.body!!.total, 0L)

    }

    companion object {
        @JvmStatic
        fun findWithSorts(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(Direction.ASC, "name", 2, 2),
                Arguments.arguments(Direction.DESC, "name", 2, 2)

            )

        }
    }


}