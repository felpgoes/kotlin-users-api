package com.kotlinspring.crudkotlinpoc.entitiy

import jakarta.persistence.*

@Entity
@Table(name = "user_stacks")
data class Stack(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,
    val name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User?
)
