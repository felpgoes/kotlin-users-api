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
        if (!id.isNullOrEmpty()) return id.hashCode()

        return super.hashCode()
    }
}
