package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface JobRepository: JpaRepository<Job, String>, JobRepositoryCustom, JpaSpecificationExecutor<Job>