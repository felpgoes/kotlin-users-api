package com.kotlinspring.crudkotlinpoc.factory.matchFactory

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface MatchServiceInterface<T, N> {

    fun findByMatch(id: String, pageRequest: PageRequest): Page<N>
    fun count(): Long
}