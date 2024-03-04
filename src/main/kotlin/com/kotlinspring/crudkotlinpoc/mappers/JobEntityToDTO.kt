package com.kotlinspring.crudkotlinpoc.mappers

import com.kotlinspring.crudkotlinpoc.dto.JobDTO
import com.kotlinspring.crudkotlinpoc.dto.JobRequirementDTO
import com.kotlinspring.crudkotlinpoc.dto.JobRequirementLevelDTO
import com.kotlinspring.crudkotlinpoc.entitiy.Job

fun Job.toDTO(): JobDTO {
    if (this.id.isNullOrEmpty()) throw Error("Error legal")

    val requirements = this.requirements.map {
        val level = JobRequirementLevelDTO(it.min, it.max)
        JobRequirementDTO(it.stack, level)
    }

    return JobDTO(this.id, this.name, this.description.toString(), this.salary, requirements.toMutableSet())
}
