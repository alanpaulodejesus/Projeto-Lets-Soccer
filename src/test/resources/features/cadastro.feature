#Feature: Cadastro de usuário

  #Scenario: Cadastro com sucesso
    #When envio dados válidos de cadastro
    #Then o usuário deve ser salvo no sistema

  #Scenario: Cadastro com email duplicado
    #Given que já existe um usuário com email "user@email.com"
    #When tento cadastrar novamente
    #Then deve retornar erro "E-mail já cadastrado"