package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, String> {
    @Query("SELECT * \n" +
            "FROM users uss \n" +
            "WHERE uss.id \n" +
            "IN(\n" +
            "\tSELECT us.user_id\n" +
            "\tFROM USER_STACKS us \n" +
            "\tLEFT JOIN JOBS_REQUIREMENTS jr ON\n" +
            "\t\tus.SCORE >= jr.MIN AND\n" +
            "\t\tus.NAME = jr.STACK AND\n" +
            "\t\tus.SCORE <= (CASE WHEN jr.MAX IS NOT NULL THEN jr.MAX ELSE us.SCORE END) AND\n" +
            "\t\tjr.JOB_ID  = :jobId\n" +
            "\tHAVING count(jr.id) >= count(us.id)\n" +
            "\tGROUP BY us.user_id\n" +
            ")", nativeQuery = true)
    fun getMatchedUsersByJobId(jobId: String, pageable: Pageable): Page<User>
}