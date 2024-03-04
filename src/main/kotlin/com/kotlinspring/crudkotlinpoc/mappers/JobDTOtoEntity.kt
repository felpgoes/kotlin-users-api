package com.kotlinspring.crudkotlinpoc.mappers

import com.kotlinspring.crudkotlinpoc.dto.JobDTO
import com.kotlinspring.crudkotlinpoc.entitiy.Job
import com.kotlinspring.crudkotlinpoc.entitiy.JobRequirement
import javax.sql.rowset.serial.SerialClob

fun JobDTO.toEntity(): Job {
    val desc = SerialClob(this.description.toCharArray())
    val job = Job(this.id, this.name,  desc, this.salary)
    val requirements = this.requirements.map {
        JobRequirement(null, it.stack, it.level.min, it.level.max, job)
    }

    job.requirements.addAll(requirements)

    return job
}