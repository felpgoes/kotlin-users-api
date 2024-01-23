package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.Stack
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface StackRepository: CrudRepository<Stack, String> {
    fun findByUserId(userId: String): List<Stack>

//    fun deleteAllByUserId(userId: String)

    @Modifying
    @Query("DELETE FROM user_stacks where user_id = ?1", nativeQuery = true)
    fun deleteAllByUserId(userId: String)

}
