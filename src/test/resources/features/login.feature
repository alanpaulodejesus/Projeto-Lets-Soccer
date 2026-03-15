Feature: Login de usuário

  Scenario: Login com sucesso
    Given que existe um usuário cadastrado
    When eu informo email "user@email.com" e senha "123456"
    Then o sistema deve retornar um token

  Scenario: Login com senha inválida
    Given que existe um usuário cadastrado
    When eu informo email "user@email.com" e senha "55555"
    Then o sistema deve retornar erro "Email ou senha inválidos"