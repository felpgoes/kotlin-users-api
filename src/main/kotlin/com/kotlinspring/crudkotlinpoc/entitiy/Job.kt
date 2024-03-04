package com.kotlinspring.crudkotlinpoc.entitiy

import jakarta.persistence.*
import java.sql.Clob

@Entity
@Table
data class Job(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    @Column(nullable = false, length = 500)
    val name: String,

    @Column(nullable = false)
    val description: Clob,

    @Column(nullable = false)
    val salary: Int,

    @OneToMany(
        mappedBy = "job",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val requirements: Set<JobRequirement> = mutableSetOf()
)