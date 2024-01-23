
# Kotlin Users API 


### Rodando o banco Oracle local:
```shell
docker container create -it --name oracle-test -p 1521:1521 -e ORACLE_PWD=welcome123 container-registry.oracle.com/database/express:latest
```