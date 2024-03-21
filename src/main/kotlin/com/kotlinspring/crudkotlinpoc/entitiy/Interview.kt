package com.kotlinspring.crudkotlinpoc.entitiy

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "interviews")
data class Interview (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    @Column(nullable = false)
    val interviewDate: LocalDateTime,

    val jobId: String,
    val userId: String,

    @ManyToOne
    @JoinColumn(name = "jobId", insertable = false, updatable = false)
    val job: Job? = null,

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    val user: User? = null,
)