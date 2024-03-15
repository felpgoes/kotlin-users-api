package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JobRepository: JpaRepository<Job, String> {//, JobRepositoryCustom, JpaSpecificationExecutor<Job> {

    @Query("SELECT jb.id" +
            "FROM JOBS_REQUIREMENTS jr\n" +
            "LEFT JOIN USER_STACKS us ON\n" +
            "\tus.SCORE >= jr.MIN AND\n" +
            "\tus.NAME = jr.STACK AND\n" +
            "\tus.SCORE <= (CASE WHEN jr.MAX IS NOT NULL THEN jr.MAX ELSE us.SCORE END) AND\n" +
            "\tus.USER_ID = 'e73eacdd-7b44-4432-ae7c-747d824a2991'\n" +
            "INNER JOIN JOBS jb ON jb.id = jr.JOB_ID\n" +
            "LEFT JOIN users u ON u.id = us.USER_ID\n" +
            "HAVING count(us.id) >= count(jb.id)\n" +
            "GROUP BY jb.id, jb.NAME, to_char(jb.DESCRIPTION), jb.SALARY\n" +
            "ORDER BY jb.id", nativeQuery = true)
    fun getMatchedJobsIdByUserId(userId: String): List<String>

    fun findAllJobsByIdIn(ids: List<String>, pageable: Pageable): Page<Job>
}