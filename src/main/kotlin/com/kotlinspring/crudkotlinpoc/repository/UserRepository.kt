package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String>