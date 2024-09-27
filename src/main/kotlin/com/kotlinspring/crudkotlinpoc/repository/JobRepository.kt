package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JobRepository: JpaRepository<Job, String> {//, JobRepositoryCustom, JpaSpecificationExecutor<Job> {

    @Query("SELECT *\n" +
            "FROM jobs \n" +
            "WHERE id \n" +
            "IN(\n" +
            "\tSELECT jr.job_id\n" +
            "\tFROM JOBS_REQUIREMENTS jr\n" +
            "\tLEFT JOIN USER_STACKS us ON\n" +
            "\t\tus.SCORE >= jr.MIN AND\n" +
            "\t\tus.NAME = jr.STACK AND\n" +
            "\t\tus.SCORE <= (CASE WHEN jr.MAX IS NOT NULL THEN jr.MAX ELSE us.SCORE END) AND\n" +
            "\t\tus.USER_ID = :userId\n" +
            "\tHAVING count(us.id) >= count(jr.id)\n" +
            "\tGROUP BY jr.job_id\n" +
            ")", nativeQuery = true)
    fun getMatchedJobsIdByUserId(userId: String, pageable: Pageable): Page<Job>
}