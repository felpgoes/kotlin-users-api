package com.kotlinspring.crudkotlinpoc.entitiy

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    val nick: String?,

    @Column(unique = true, nullable = false)
    val name: String,

    @Column(nullable = false)
    val birthDate: LocalDateTime,

    @OneToMany(
        mappedBy = "user",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val stack: MutableSet<Stack> = mutableSetOf()
) {
    override fun hashCode(): Int {
        if (!id.isNullOrEmpty()) return id.hashCode()

        return super.hashCode()
    }
}