package com.kotlinspring.crudkotlinpoc.exceptions

class UserNotFoundException(userId: String): RuntimeException("User not found with id: $userId")