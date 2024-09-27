package com.kotlinspring.crudkotlinpoc.controller

import com.kotlinspring.crudkotlinpoc.dto.*
import com.kotlinspring.crudkotlinpoc.service.JobService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@Tag("unit")
@WebMvcTest(controllers = [JobController::class])
class JobControllerUnitTest {
    @MockkBean
    private lateinit var jobServiceMock: JobService

    @Autowired
    private lateinit var jobController: JobController

    @Nested
    inner class Store {
        @Test
        fun `should create a job successfully`() {
            val requirements = mutableSetOf(
                JobRequirementDTO("NodeJS", JobRequirementLevelDTO(90, 100))
            )

            val jobDTO = JobDTO(
                null,
                "Sr. Developer",
                "Descrição que te manda pra outro site.",
                100000000,
                requirements
            )
            val jobDTOWithId = jobDTO.copy(id = "ID_LEGAL_123")

            every { jobServiceMock.create(any()) } returns jobDTOWithId
            val result = jobController.store(jobDTO)

            Assertions.assertNotNull(result)
            Assertions.assertEquals(result::class, JobDTO::class)
            Assertions.assertEquals(result, jobDTOWithId)
        }

        @Test
        fun `should throw error when name bigger then 500 characters`() {
            val requirements = mutableSetOf(
                JobRequirementDTO("NodeJS", JobRequirementLevelDTO(90, 100))
            )

            val jobDTO = JobDTO(
                null,
                "abcdefghij".repeat(51),
                "Descrição que te manda pra outro site.",
                100000000,
                requirements
            )
            every { jobServiceMock.create(any()) } returns jobDTO.copy(id = "a")

            val rawError = assertThrows<ConstraintViolationException> { jobController.store(jobDTO) }
            val error = rawError.constraintViolations.first()

            Assertions.assertNotNull(error)
            Assertions.assertEquals(error.propertyPath.toString(), "store.body.name")
            Assertions.assertEquals(
                error.constraintDescriptor.annotation.annotationClass.toString(),
                "class org.hibernate.validator.constraints.Length"
            )
            Assertions.assertEquals(error.messageTemplate, "O campo não pode exceder 500 caracteres")
        }

        @Test
        fun `should throw error when name is empty`() {
            val requirements = mutableSetOf(
                JobRequirementDTO("NodeJS", JobRequirementLevelDTO(90, 100))
            )

            val jobDTO = JobDTO(
                null,
                "",
                "Descrição que te manda pra outro site.",
                100000000,
                requirements
            )
            every { jobServiceMock.create(any()) } returns jobDTO.copy(id = "a")

            val rawError = assertThrows<ConstraintViolationException> { jobController.store(jobDTO) }
            val error = rawError.constraintViolations.first()

            Assertions.assertNotNull(error)
            Assertions.assertEquals(error.propertyPath.toString(), "store.body.name")
            Assertions.assertEquals(
                error.constraintDescriptor.annotation.annotationClass.toString(),
                "class jakarta.validation.constraints.NotBlank"
            )
            Assertions.assertEquals(error.messageTemplate, "O campo não pode ser vazio")
        }

        @Test
        fun `should throw error when name is empty but with spaces`() {
            val requirements = mutableSetOf(
                JobRequirementDTO("NodeJS", JobRequirementLevelDTO(90, 100))
            )

            val jobDTO = JobDTO(
                null,
                "  ",
                "Descrição que te manda pra outro site.",
                100000000,
                requirements
            )
            every { jobServiceMock.create(any()) } returns jobDTO.copy(id = "a")

            val rawError = assertThrows<ConstraintViolationException> { jobController.store(jobDTO) }
            val error = rawError.constraintViolations.first()

            Assertions.assertNotNull(error)
            Assertions.assertEquals(error.propertyPath.toString(), "store.body.name")
            Assertions.assertEquals(
                error.constraintDescriptor.annotation.annotationClass.toString(),
                "class jakarta.validation.constraints.NotBlank"
            )
            Assertions.assertEquals(error.messageTemplate, "O campo não pode ser vazio")
        }

        @Test
        fun `should throw error when salary is negative`() {
            val requirements = mutableSetOf(
                JobRequirementDTO("NodeJS", JobRequirementLevelDTO(90, 100))
            )

            val jobDTO = JobDTO(
                null,
                "Vaga legal",
                "Descrição que te manda pra outro site.",
                -1000,
                requirements
            )
            every { jobServiceMock.create(any()) } returns jobDTO.copy(id = "a")

            val rawError = assertThrows<ConstraintViolationException> { jobController.store(jobDTO) }
            val error = rawError.constraintViolations.first()

            Assertions.assertNotNull(error)
            Assertions.assertEquals(error.propertyPath.toString(), "store.body.salary")
            Assertions.assertEquals(
                error.constraintDescriptor.annotation.annotationClass.toString(),
                "class jakarta.validation.constraints.Min"
            )
            Assertions.assertEquals(error.messageTemplate, "O salário deve ser maior que zero")
        }

        @Test
        fun `should throw error when requirements is empty`() {
            val requirements = emptySet<JobRequirementDTO>().toMutableSet()

            val jobDTO = JobDTO(
                null,
                "Sr. Developer",
                "Descrição que te manda pra outro site.",
                100000000,
                requirements
            )
            every { jobServiceMock.create(any()) } returns jobDTO.copy(id = "a")

            val rawError = assertThrows<ConstraintViolationException> { jobController.store(jobDTO) }
            val error = rawError.constraintViolations.first()

            Assertions.assertNotNull(error)
            Assertions.assertEquals(error.propertyPath.toString(), "store.body.requirements")
            Assertions.assertEquals(
                error.constraintDescriptor.annotation.annotationClass.toString(),
                "class jakarta.validation.constraints.NotEmpty"
            )
            Assertions.assertEquals(error.messageTemplate, "Os requisitos da vagas são obrigatorios.")
        }

        @Test
        fun `should throw error when requirements has invalid requirement with empty stack name`() {
            val requirements = mutableSetOf(
                JobRequirementDTO("              ", JobRequirementLevelDTO(90, 100))
            )

            val jobDTO = JobDTO(
                null,
                "Sr. Developer",
                "Descrição que te manda pra outro site.",
                100000000,
                requirements
            )
            every { jobServiceMock.create(any()) } returns jobDTO.copy(id = "a")

            val rawError = assertThrows<ConstraintViolationException> { jobController.store(jobDTO) }

            val error = rawError.constraintViolations.first()

            Assertions.assertNotNull(error)
            Assertions.assertEquals(error.propertyPath.toString(), "store.body.requirements[].stack")
            Assertions.assertEquals(
                error.constraintDescriptor.annotation.annotationClass.toString(),
                "class jakarta.validation.constraints.NotBlank"
            )
            Assertions.assertEquals(error.messageTemplate, "O campo não pode ser vazio")
        }

        @Test
        fun `should throw error when requirements has invalid requirement with negative min level`() {
            val requirements = mutableSetOf(
                JobRequirementDTO("NodeJS", JobRequirementLevelDTO(-10, 100))
            )

            val jobDTO = JobDTO(
                null,
                "Sr. Developer",
                "Descrição que te manda pra outro site.",
                100000000,
                requirements
            )
            every { jobServiceMock.create(any()) } returns jobDTO.copy(id = "a")

            val rawError = assertThrows<ConstraintViolationException> { jobController.store(jobDTO) }
            val error = rawError.constraintViolations.first()

            Assertions.assertNotNull(error)
            Assertions.assertEquals(error.propertyPath.toString(), "store.body.requirements[].level.min")
            Assertions.assertEquals(
                error.constraintDescriptor.annotation.annotationClass.toString(),
                "class jakarta.validation.constraints.Min"
            )
            Assertions.assertEquals(error.messageTemplate, "O level deve ser maior que zero")
        }
    }
}