package com.kotlinspring.crudkotlinpoc.exceptions

class JobNotFoundException(userId: String): RuntimeException("Job not found with id: $userId")