package com.kotlinspring.crudkotlinpoc.service

import com.kotlinspring.crudkotlinpoc.dto.PaginationDTO
import com.kotlinspring.crudkotlinpoc.dto.PaginationResponse
import com.kotlinspring.crudkotlinpoc.dto.StackDTO
import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.entitiy.Stack
import com.kotlinspring.crudkotlinpoc.entitiy.User
import com.kotlinspring.crudkotlinpoc.exceptions.UserNotFoundException
import com.kotlinspring.crudkotlinpoc.repository.StackRepository
import com.kotlinspring.crudkotlinpoc.repository.UserRepository
import jakarta.transaction.Transactional
import mu.KLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository, private val stackRepository: StackRepository) {

    companion object : KLogging()

    fun findAll(page: Int, size: Int, sort: String?): PaginationDTO<UserDTO> {
        var pageRequest = PageRequest.of(page, size)

        if (!sort.isNullOrEmpty()) {
            val rawType = sort[0].toString()
            val type = if (rawType == "-") Direction.DESC else Direction.ASC
            val field = sort.replace(rawType, "")
            pageRequest = pageRequest.withSort(Sort.by(type, field))
        }
        val total = userRepository.count()

        if (total == 0L) {
            return PaginationDTO(
                PaginationResponse(
                    emptyList(),
                    page,
                    size,
                    total
                ),
                false
            )
        }

        val users = userRepository
            .findAll(pageRequest)
            .map { user ->
                val stack = user.stack.map { st -> StackDTO(st.name, st.level) }
                UserDTO(user.id, user.nick, user.name, user.birthDate, stack.toMutableSet())
            }

        return PaginationDTO(
            PaginationResponse(
                users.content,
                users.pageable.pageNumber,
                users.pageable.pageSize,
                total
            ),
            users.hasNext()
        )
    }

    fun find(userId: String): UserDTO {
        val user = userRepository
            .findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

        val stack = user.stack.map { st -> StackDTO(st.name, st.level) }

        return UserDTO(user.id, user.nick, user.name, user.birthDate, stack.toMutableSet())
    }

    fun delete(userId: String) {
        val user = userRepository
            .findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

        userRepository.delete(user)
    }

    fun create(body: UserDTO): UserDTO {
        val user = User(null, body.nick, body.name, body.birthDate)
        body.stack?.forEach { user.stack.add(Stack(null, it.name, it.level, user)) }

        val savedUser = userRepository.save(user)

        return body.apply { id = savedUser.id }
    }

    @Transactional
    fun update(userId: String, body: UserDTO): UserDTO {
        val user = userRepository
            .findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

        val userToUpdate = user.copy(
            name = body.name,
            nick = body.nick,
            birthDate = body.birthDate
        )

        val newStacks = mutableSetOf<Stack>()
        body.stack?.forEach {
            val alreadyExists = userToUpdate.stack.find { it2 -> it2.name == it.name }
            newStacks.add(Stack(alreadyExists?.id, it.name, it.level, userToUpdate))
        }
        userToUpdate.stack.clear()
        userToUpdate.stack.addAll(newStacks)

        val updated = userRepository.save(userToUpdate)

        return UserDTO(updated.id, updated.nick, updated.name, updated.birthDate, body.stack)
    }

    fun findStacks(userId: String): List<StackDTO> {
        userRepository
            .findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

        return stackRepository.findByUserId(userId).map { StackDTO(it.name, it.level) }
    }
}
