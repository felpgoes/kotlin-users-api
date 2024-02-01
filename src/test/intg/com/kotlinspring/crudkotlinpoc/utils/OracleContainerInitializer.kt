package com.kotlinspring.crudkotlinpoc.utils

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@ContextConfiguration(initializers = [OracleContainerInitializer.Initializer::class])
abstract class OracleContainerInitializer {
    companion object {

        @Container
        private val oracleDB = OracleContainer(
            DockerImageName
                .parse("container-registry.oracle.com/database/express:21.3.0-xe")
                .asCompatibleSubstituteFor("gvenzl/oracle-xe")
        ).apply {
            withEnv("ORACLE_PWD", "welcome123")
        }
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            oracleDB.start()
            TestPropertyValues.of(
                "spring.datasource.url=${oracleDB.jdbcUrl}",
            ).applyTo(applicationContext.environment)
        }
    }

}