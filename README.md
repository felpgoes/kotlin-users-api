# Kotlin Users API

## Rodando o banco Oracle local:

```shell
docker container create -it --name oracle-test -p 1521:1521 -e ORACLE_PWD=welcome123 container-registry.oracle.com/database/express:latest
```

## Tabela de erros

| Código                          | Descrição                                         | Response Status | 
|---------------------------------|---------------------------------------------------|-----------------|
| MethodArgumentNotValidException | O corpo da mensagem possuí um valor invalido      | 400             | 
| DateTimeParseException          | A data passada não é do Padrão ISO8601            | 400             | 
| UserNotFoundException           | O usuário buscado não foi encontrado              | 404             |
| HttpMessageNotReadableException | O corpo da requisição não está no padrão esperado | 400             | 
| Exception                       | Houve um problema na execução da aplicação        | 500             |

# Realização das instruções
## Etapa 1

---
- [x] Criar Atributos
- [x] Criar Endpoints
- [x] Utilizar Stack Spring MVC, Spring Data, Spring Test
- [x] Utilizar Oracle
- [x] Desenvolver Tests

## Etapa 2

---
- [x] Adicionar paginação e ordenação na listagem de usuários
- [x] Padronizar as mensagens de erros
- [x] Adicionar o campo level na lista de tecnologias
- [x] Alterar todos os endpoints para retornar o novo campo level
- [x] Deve ser possível buscar a lista de tecnologias de um usuário
- [x] Implementar testes unitários e de integração
- [x] Etapas opcionais

#### Adicional
- [x] Utilizar Java 21
- [x] Validar na função hashCode se o `id` é nulo. 
- [x] Remover uso de `var` nas entidades
- [x] Criar novo construtor para o ErrorResponse receber apenas um ErrorMessage
- [x] Utilizar a annotation @Repository no UserRepository
- [x] Remover usos o Optional.isPresent na UserService
- [x] Substituir o Camel Case do nome dos testes para espaços
- [x] Utilizar Tags do JUnit nos testes 
- [x] Substituir `exchange` por `testRestTemplate.[METHOD]`
- [x] Fazer a validação das Collections com `assertThat` ao invés de multiplos usos do `assertEquals`

