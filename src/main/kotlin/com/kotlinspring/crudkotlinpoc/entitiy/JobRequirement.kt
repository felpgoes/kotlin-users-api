package com.kotlinspring.crudkotlinpoc.entitiy

import jakarta.persistence.*

@Entity
@Table(name = "jobs_requirements", uniqueConstraints = [UniqueConstraint(columnNames = ["stack", "job_id"])])
data class JobRequirement(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    @Column(length = 32)
    val stack: String,

    @Column(nullable = false, columnDefinition = "number(3, 0) CHECK (min > 0 AND min <= 100)")
    val min: Int,

    @Column(nullable = true, columnDefinition = "number(3, 0) CHECK (max > 0 AND max <= 100)")
    val max: Int?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    val job: Job
)