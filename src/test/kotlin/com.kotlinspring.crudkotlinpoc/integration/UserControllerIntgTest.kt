package com.kotlinspring.crudkotlinpoc.integration

import com.kotlinspring.crudkotlinpoc.dto.PaginationResponse
import com.kotlinspring.crudkotlinpoc.dto.StackDTO
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.exceptionHandler.GenericHandler
import com.kotlinspring.crudkotlinpoc.utils.OracleContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.core.ParameterizedTypeReference
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


@Tag("integration")
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
        fun `should retrieve user with success`() {
            val userDTO =
                UserDTO(null, "v", "Felipe", validBirthDate, mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

            val retrievedUserDTO = testRestTemplate.getForEntity<UserDTO>("$baseUrl/${savedUserDTO.id}")

            assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
            assertThat(retrievedUserDTO.body)
                .isNotNull
                .isEqualTo(savedUserDTO)
        }

        @Test
        fun `should retrieve user with not found error`() {
            val userId = "ID_LEGAL_123"
            val retrievedUser = testRestTemplate.getForEntity<GenericHandler.ErrorResponse>("$baseUrl/$userId")

            assertNotNull(retrievedUser.body)
            assertEquals(retrievedUser.statusCode, HttpStatus.NOT_FOUND)

            assertThat(retrievedUser.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.UserNotFoundException,
                        "User not found with id: ID_LEGAL_123"
                    )
                )
        }
    }

    @Nested
    inner class Store {
        @Test
        fun `should store user with success`() {
            val userDTO =
                UserDTO(null, "v", "Felipe", validBirthDate, mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

            assertThat(savedUserDTO)
                .isNotNull
                .isEqualTo(userDTO.copy(id = savedUserDTO.id))
        }

        @Test
        fun `should not store user with nick validation error`() {
            val userDTO = UserDTO(
                null,
                "vapo".repeat(10),
                "Felipe",
                validBirthDate,
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO =
                testRestTemplate.postForEntity(baseUrl, userDTO, GenericHandler.ErrorResponse::class.java)

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertThat(savedUserDTO.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException,
                        "O campo não pode exceder 32 caracteres"
                    )
                )
        }

        @Test
        fun `should not store user with name too large validation error`() {
            val userDTO = UserDTO(
                null,
                "V",
                "Felipe".repeat(50),
                validBirthDate,
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO =
                testRestTemplate.postForEntity(baseUrl, userDTO, GenericHandler.ErrorResponse::class.java)

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertThat(savedUserDTO.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException,
                        "O campo não pode exceder 255 caracteres"
                    )
                )
        }

        @Test
        fun `should not store user with name empty validation error`() {
            val userDTO = UserDTO(
                null,
                "V",
                "",
                validBirthDate,
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO =
                testRestTemplate.postForEntity(baseUrl, userDTO, GenericHandler.ErrorResponse::class.java)

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertThat(savedUserDTO.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException,
                        "O campo não pode ser vazio"
                    )
                )
        }

        @Test
        fun `should not store user with birth date validation error`() {
            val invalidBirthDate = "ameixa-02-04"

            val userDTO = mapOf(
                "id" to null,
                "nick" to "V",
                "name" to "Felipe",
                "birth_date" to invalidBirthDate,
                "stack" to mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO =
                testRestTemplate.postForEntity(baseUrl, userDTO, GenericHandler.ErrorResponse::class.java)

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertThat(savedUserDTO.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.DateTimeParseException,
                        "O valor \"$invalidBirthDate\" não é um tipo de Data valido."
                    )
                )
        }

        @Test
        fun `should not store user with empty stack validation error`() {
            val userDTO = UserDTO(
                null,
                "V",
                "Felipe",
                validBirthDate,
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("", 10))
            )
            val savedUserDTO =
                testRestTemplate.postForEntity(baseUrl, userDTO, GenericHandler.ErrorResponse::class.java)

            assertNotNull(savedUserDTO.body)
            assertEquals(savedUserDTO.statusCode, HttpStatus.BAD_REQUEST)

            assertThat(savedUserDTO.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.MethodArgumentNotValidException,
                        "Invalid Stack List"
                    )
                )
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should update user with success`() {
            val userDTO = UserDTO(
                null,
                "vvv",
                "Felipe Goes",
                validBirthDate,
                mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10))
            )
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)
            val updateBody = savedUserDTO.copy(name = "Goes")

            testRestTemplate.exchange(
                RequestEntity<UserDTO>(
                    updateBody,
                    HttpMethod.PUT,
                    URI("$baseUrl/${savedUserDTO.id}")
                ), UserDTO::class.java
            )

            val retrievedUserDTO = testRestTemplate.getForEntity<UserDTO>("$baseUrl/${savedUserDTO.id}")

            assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
            assertThat(retrievedUserDTO.body)
                .isNotNull
                .isEqualTo(updateBody)
        }

        @Test
        fun `should not update user with not found error`() {
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

            assertThat(result.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.UserNotFoundException,
                        "User not found with id: $userId"
                    )
                )
        }
    }

    @Nested
    inner class FindStack {
        @Test
        fun `should retrieve user stacks with success`() {
            val userDTO =
                UserDTO(null, "v", "Felipe", validBirthDate, mutableSetOf(StackDTO("NodeJS", 10), StackDTO("JS", 10)))
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)

            val userStacksResponse = testRestTemplate.exchange(
                RequestEntity.get("$baseUrl/${savedUserDTO.id}/stack").build(),
                object : ParameterizedTypeReference<List<StackDTO>>() {}
            )

            assertNotNull(userStacksResponse.body)
            assertEquals(userStacksResponse.statusCode, HttpStatus.OK)

            assertThat(userStacksResponse.body)
                .hasSize(2)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(userDTO.stack)
        }

        @Test
        fun `should not retrieve user stacks with not found error`() {
            val userId = "ID_LEGAL_123"
            val result = testRestTemplate.getForEntity<GenericHandler.ErrorResponse>("$baseUrl/$userId/stack")

            assertNotNull(result.body)
            assertEquals(result.statusCode, HttpStatus.NOT_FOUND)

            assertThat(result.body!!.errorMessages)
                .hasSize(1)
                .first()
                .isEqualTo(
                    GenericHandler.ErrorMessage(
                        GenericHandler.ErrorsCodesEnum.UserNotFoundException,
                        "User not found with id: $userId"
                    )
                )
        }

        @Test
        fun `should retrieve user with empty stacks with success`() {
            val userDTO = UserDTO(null, "v", "Felipe", validBirthDate, null)
            val savedUserDTO = testRestTemplate.postForObject(baseUrl, userDTO, UserDTO::class.java)
            val retrievedUserDTO = testRestTemplate.getForEntity<List<StackDTO>>("$baseUrl/${savedUserDTO.id}/stack")

            assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
            assertThat(retrievedUserDTO.body)
                .isNotNull
                .isEmpty()
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
    fun `should retrieve user stacks with success`(
        sort: Direction,
        field: String,
        size: Int,
        quantity: Int,
        page: Int,
        status: HttpStatus,
    ) {
        val startingWith = size * page
        val a = startingWith + size
        val endingWith = if (a <= quantity) a else quantity
        val users = createUsers(quantity)

        val direction = if (sort.isDescending) "-" else "+"

        val searchUrl = "$baseUrl?page=$page&pageSize=$size&sort=$direction$field"
        val retrievedUserDTO = testRestTemplate.getForEntity<PaginationResponse<LinkedHashMap<String, Any>>>(searchUrl)

        assertNotNull(retrievedUserDTO.body)
        assertEquals(retrievedUserDTO.statusCode, status)
        assertEquals(retrievedUserDTO.body!!.total, quantity.toLong())

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
            .slice(IntRange(startingWith, endingWith - 1))
            .map { it.id }

        val retrievedIds = retrievedUserDTO.body!!.records.map { it["id"] }

        assertThat(retrievedIds)
            .hasSize(endingWith - startingWith)
            .isEqualTo(sortedUsers)
    }

    @Test
    fun `should retrieve pagination with empty records with success`() {
        val retrievedUserDTO = testRestTemplate.getForEntity<PaginationResponse<UserDTO>>(baseUrl)

        assertEquals(retrievedUserDTO.statusCode, HttpStatus.OK)
        assertNotNull(retrievedUserDTO.body)
        assertEquals(retrievedUserDTO.body!!.records.size, 0)
        assertEquals(retrievedUserDTO.body!!.total, 0L)

    }

    companion object {
        @JvmStatic
        fun findWithSorts(): Stream<Arguments> {
            return Stream.of(
                Arguments.arguments(Direction.ASC, "name", 2, 2, 0, HttpStatus.OK),
                Arguments.arguments(Direction.DESC, "name", 2, 2, 0, HttpStatus.OK),
                Arguments.arguments(Direction.ASC, "nick", 5, 5, 0, HttpStatus.OK),
                Arguments.arguments(Direction.DESC, "name", 5, 10, 0, HttpStatus.PARTIAL_CONTENT),
                Arguments.arguments(Direction.DESC, "nick", 5, 10, 0, HttpStatus.PARTIAL_CONTENT),
                Arguments.arguments(Direction.DESC, "name", 5, 7, 1, HttpStatus.OK),
                Arguments.arguments(Direction.DESC, "nick", 5, 7, 1, HttpStatus.OK),
                Arguments.arguments(Direction.DESC, "name", 1, 10, 5, HttpStatus.PARTIAL_CONTENT),
                Arguments.arguments(Direction.DESC, "nick", 1, 10, 9, HttpStatus.OK),
            )

        }
    }
}