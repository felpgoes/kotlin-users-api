package com.kotlinspring.crudkotlinpoc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrudKotlinPocApplication

fun main(args: Array<String>) {
	runApplication<CrudKotlinPocApplication>(*args)
}
