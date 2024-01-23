package com.kotlinspring.crudkotlinpoc.service

import com.kotlinspring.crudkotlinpoc.dto.UserDTO
import com.kotlinspring.crudkotlinpoc.entitiy.Stack
import com.kotlinspring.crudkotlinpoc.entitiy.User
import com.kotlinspring.crudkotlinpoc.exceptions.UserNotFoundException
import com.kotlinspring.crudkotlinpoc.repository.StackRepository
import com.kotlinspring.crudkotlinpoc.repository.UserRepository
import jakarta.transaction.Transactional
import mu.KLogging
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class UserService (private val userRepository: UserRepository, private val stackRepository: StackRepository) {

    companion object : KLogging()

    fun findAll(page: Int, size: Int): List<UserDTO> = userRepository
        .findAll(PageRequest.of(if (page >= 2) page - 1 else 0, size))
        .toList()
        .map { user ->
            val stack = user.id?.let { stackRepository.findByUserId(it).map { st -> st.name} }

            UserDTO(user.id, user.nick, user.name, user.birthDate, stack)
        }


    fun find(userId: String): UserDTO {
        val user = userRepository.findById(userId)

        if (!user.isPresent) {
            throw UserNotFoundException(userId)
        }

        return user.get().let {
            val stack = stackRepository.findByUserId(userId).map { st -> st.name}

            UserDTO(it.id,it.nick,it.name, it.birthDate, stack)
        }
    }

    fun delete(userId: String) {
        val user = userRepository.findById(userId)

        if (!user.isPresent) {
            throw UserNotFoundException(userId)
        }

        userRepository.deleteById(userId)
    }

    fun create(body: UserDTO): UserDTO {
        val savedUser = userRepository.save(
            User(null,  body.nick, body.name, body.birth_date)
        )

        val stack = body.stack!!.map { stack -> Stack(null, stack, savedUser) }

        stack.let {
            stackRepository.saveAll(stack)
        }

        return body.apply { id = savedUser.id }
    }

    @Transactional
    fun update(userId: String, body: UserDTO): UserDTO {
        val user = userRepository.findById(userId)

        if (!user.isPresent) {
            throw UserNotFoundException(userId)
        }

        return user.get()
            .let {
                it.name = body.name
                it.nick = body.nick
                it.birthDate = body.birth_date
                logger.info("body=$body")
                val updatedUser = userRepository.save(it)
                logger.info("updatedUser=$updatedUser")

                stackRepository.deleteAllByUserId(userId)
                val stack = body.stack!!.map { stack -> Stack(null, stack, updatedUser) }
                logger.info("stack=$stack")

                stackRepository.saveAll(stack)

                UserDTO(it.id, it.nick, it.name,  it.birthDate, body.stack)
            }

    }
}
