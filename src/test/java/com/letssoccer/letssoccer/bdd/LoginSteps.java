package com.letssoccer.letssoccer.bdd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;

    @Before
    public void setup() {

        RestTemplate template = restTemplate.getRestTemplate();

        template.setRequestFactory(
                new HttpComponentsClientHttpRequestFactory()
        );

        template.setErrorHandler(new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

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

    @Quando("informo email {string} e senha {string}")
    public void enviarLogin(String email, String senha) {

        LoginRequestDto dto = new LoginRequestDto(email, senha);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequestDto> request = new HttpEntity<>(dto, headers);
        response = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                request,
                String.class
        );
    }

    @Entao("o sistema deve retornar o token")
    public void validarToken() {

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));

    }

    @Entao("o sistema deve retornar mensagem {string}")
    public void validarErro(String mensagem) throws JsonProcessingException {

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody(), "A resposta não possui body");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        assertTrue(json.has("mensagem"), "Campo 'mensagem' não encontrado no JSON");

        String mensagemResposta = json.get("mensagem").asText();
        assertEquals(mensagem, mensagemResposta);
    }
}
