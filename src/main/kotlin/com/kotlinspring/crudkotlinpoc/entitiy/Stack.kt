package com.kotlinspring.crudkotlinpoc.entitiy

import jakarta.persistence.*

@Entity
@Table(name = "user_stacks", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "user_id"])])
data class Stack(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    @Column(length = 32)
    val name: String,

    @Column(nullable = false, name = "score")
    val level: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User?
) {
    override fun toString(): String {
        return "[id=$id, name=$name, level=$level]"
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stack

        if (id != other.id) return false
        if (name != other.name) return false
        if (level != other.level) return false
        if (user != other.user) return false

        return true
    }
}
