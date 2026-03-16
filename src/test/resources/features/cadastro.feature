Feature: Cadastro de usuário

  Scenario: Cadastro com sucesso
    Given informo nome "User", email "user2@email.com", senha "123456" e cofirmação de senha "123456"
    Then o usuário deve ser salvo no sistema exibindo mensagem "Usuário cadastrado com sucesso"

  Scenario: Cadastro com email duplicado
    Given informo nome "User", email "user3@email.com", senha "123456" e cofirmação de senha "123456"
    Given informo nome "User", email "user3@email.com", senha "123456" e cofirmação de senha "123456"
    Then deve retornar mensagem "E-mail já cadastrado"