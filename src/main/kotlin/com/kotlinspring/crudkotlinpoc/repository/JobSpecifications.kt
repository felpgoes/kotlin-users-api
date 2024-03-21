package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.Job
import com.kotlinspring.crudkotlinpoc.entitiy.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface JobRepositoryCustom {
    fun getJobsByUserStacks(user: User, pageRequest: PageRequest): Page<Job>
}