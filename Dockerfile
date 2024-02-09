FROM openjdk:21-jdk

EXPOSE 8080

ADD ./build/libs/crud-kotlin-poc-0.0.1-SNAPSHOT-plain.jar docker.jar

ENTRYPOINT ["java", "-jar", "docker.jar"]