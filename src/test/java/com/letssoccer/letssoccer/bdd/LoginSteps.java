package com.letssoccer.letssoccer.bdd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @Dado("que existe um usuário cadastrado")
    public void existeUsuario() {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "User",
                "user@email.com",
                "123456",
                "123456"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UsuarioCadastroDto> request =
                new HttpEntity<>(dto, headers);

        restTemplate.postForEntity(
                "/usuarios/cadastro",
                request,
                String.class
        );
    }

    @Quando("eu informo email {string} e senha {string}")
    public void enviarLogin(String email, String senha) {

        LoginRequestDto dto = new LoginRequestDto(email, senha);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequestDto> request = new HttpEntity<>(dto, headers);

        try {
            response = restTemplate.postForEntity(
                    "/login",
                    request,
                    String.class
            );
        } catch (Exception e) {
            response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Entao("o sistema deve retornar um token")
    public void validarToken() {

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));

    }

    @Entao("o sistema deve retornar erro {string}")
    public void validarErro(String mensagem) throws JsonProcessingException {
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.readTree(response.getBody());

        String mensagemResposta = json.get("mensagem").asText();

        assertEquals(mensagem, mensagemResposta);

    }

}
