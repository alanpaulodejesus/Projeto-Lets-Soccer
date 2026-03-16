package com.letssoccer.letssoccer.bdd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsuarioSteps {

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
    @Dado("informo nome {string}, email {string}, senha {string} e cofirmação de senha {string}")
    public void informoDadosValidosDeUsuario(String nome, String email, String senha, String confirmacaoSenha) {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(nome, email, senha, confirmacaoSenha);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UsuarioCadastroDto> request =
                new HttpEntity<>(dto, headers);

        response = restTemplate.postForEntity(
                "/usuarios/cadastro",
                request,
                String.class
        );
    }

    @Entao("o usuário deve ser salvo no sistema exibindo mensagem {string}")
    public void validarCadastroUsuarioComSucesso(String mensagem) throws JsonProcessingException {

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        String mensagemResposta = json.get("mensagem").asText();
        assertEquals(mensagem, mensagemResposta);
    }

    @Entao("deve retornar mensagem {string}")
    public void validarCadastroUsuarioExistente(String mensagem) throws JsonProcessingException {

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        String mensagemResposta = json.get("mensagem").asText();
        assertEquals(mensagem, mensagemResposta);
    }
}
