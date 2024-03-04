package com.kotlinspring.crudkotlinpoc.entitiy

import jakarta.persistence.*

@Entity
@Table(name = "job_requirements")
data class JobRequirement(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    @Column(length = 32)
    val stack: String,

    @Column(nullable = false, columnDefinition = "TINYINT")
    val min: Int,

    @Column(nullable = true, columnDefinition = "TINYINT")
    val max: Int?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    val job: Job
)