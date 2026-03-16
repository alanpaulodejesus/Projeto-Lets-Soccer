Feature: Cadastro de usuário

  Scenario: Cadastro com sucesso
    Given informo nome "User", email "user2@email.com", senha "123456" e cofirmação de senha "123456"
    Then o sistema exibe mensagem "Usuário cadastrado com sucesso"

  Scenario: Cadastro com email duplicado
    Given que existe um usuário cadastrado
    When informo nome "User", email "user@email.com", senha "123456" e cofirmação de senha "123456"
    Then deve retornar mensagem "E-mail já cadastrado"

  Scenario: Cadastro e login
    Given informo nome "User", email "user4@email.com", senha "123456" e cofirmação de senha "123456"
    When o sistema exibe mensagem "Usuário cadastrado com sucesso"
    And informo email "user4@email.com" e senha "123456"
    Then o sistema deve retornar o token