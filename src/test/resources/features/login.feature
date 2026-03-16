Feature: Login de usuário

  Scenario: Login com sucesso
    Given que existe um usuário cadastrado
    When informo email "user@email.com" e senha "123456"
    Then o sistema deve retornar o token

  Scenario: Login com senha inválida
    Given que existe um usuário cadastrado
    When informo email "user@email.com" e senha "55555"
    Then o sistema deve retornar mensagem "Email ou senha inválidos"