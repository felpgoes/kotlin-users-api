package com.kotlinspring.crudkotlinpoc.entitiy

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,
    var nick: String?,
    var name: String,
    var birthDate: LocalDateTime,

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var stack: List<Stack>? = mutableListOf()
) {
    override fun toString(): String {
        return "[id=$id, name=$name, birth_date=$birthDate, stack=${stack?.joinToString { it.name }}]"
    }
}