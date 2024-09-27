package com.kotlinspring.crudkotlinpoc.repository

import com.kotlinspring.crudkotlinpoc.entitiy.Job
import com.kotlinspring.crudkotlinpoc.entitiy.JobRequirement
import com.kotlinspring.crudkotlinpoc.entitiy.Stack
import com.kotlinspring.crudkotlinpoc.entitiy.User
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.stereotype.Repository

@Repository
class JobRepositoryCustomImpl(private val entityManager: EntityManager): JobRepositoryCustom  {

    val jpaSpecificationExecutor = SimpleJpaRepository<Job, String>(Job::class.java, entityManager)

    private fun searchBetweenLevel (stack: List<Stack>, id: String): Specification <Job> {

//        SELECT jb.id, count(jb.id), count(us.id)
//        FROM JOBS_REQUIREMENTS jr
//        LEFT JOIN USER_STACKS us ON us.SCORE >= jr.MIN AND us.NAME = jr.STACK AND us.SCORE <= (CASE WHEN jr.MAX IS NOT NULL THEN jr.MAX ELSE us.SCORE END)
//        AND us.USER_ID = 'e73eacdd-7b44-4432-ae7c-747d824a2991'
//        INNER JOIN JOBS jb ON jb.id = jr.JOB_ID
//                LEFT JOIN users u ON u.id = us.USER_ID
//                HAVING count(jb.id) = count(us.id)
//        GROUP BY jb.id
//        ORDER BY jb.id

        return Specification <Job> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            val jobRequirementJoin = root.join<Job, JobRequirement>("requirements", JoinType.LEFT)
//            val jobRequirementJoin = root.join<Job, JobRequirement>("requirements", JoinType.LEFT)

            stack.forEach {
                val stackPredicate = cb.and(
                    cb.lessThanOrEqualTo(jobRequirementJoin.get("min"), it.level),
                    cb.greaterThanOrEqualTo(jobRequirementJoin.get("max"), it.level),
                    cb.equal(jobRequirementJoin.get<JobRequirement>("stack"), it.name),
                )
                predicates.add(stackPredicate)
            }

            cb.and(*predicates.toTypedArray())
        }
    }

    override fun getJobsByUserStacks(user: User, pageRequest: PageRequest): Page<Job> {

        val filter = searchBetweenLevel(user.stack.toList(), user.id!!)

        return jpaSpecificationExecutor.findAll(filter, pageRequest)
    }
}