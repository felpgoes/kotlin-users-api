package com.kotlinspring.crudkotlinpoc.dto

data class PaginationDTO<T> (
    val data: PaginationResponse<T>,
    val hasNext: Boolean
)

data class PaginationResponse <T> (
    val records: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Long,
)