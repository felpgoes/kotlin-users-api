package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.Interview
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InterviewRepository: CrudRepository<Interview, String>