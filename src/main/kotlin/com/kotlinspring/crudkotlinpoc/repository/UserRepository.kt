package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, String>