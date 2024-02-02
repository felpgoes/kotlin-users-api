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


--- 
# Realização das instruções
## Etapa 1
- [x] Criar Atributos
- [x] Criar Endpoints
- [x] Utilizar Stack Spring MVC, Spring Data, Spring Test
- [x] Utilizar Oracle
- [x] Desenvolver Tests

## Etapa 2
- [ ] Adicionar paginação e ordenação na listagem de usuários
- [x] Padronizar as mensagens de erros
- [x] Adicionar o campo level na lista de tecnologias
- [x] Alterar todos os endpoints para retornar o novo campo level
- [x] Deve ser possível buscar a lista de tecnologias de um usuário
- [ ] Implementar testes unitários e de integração
- [ ] Etapas opcionais


