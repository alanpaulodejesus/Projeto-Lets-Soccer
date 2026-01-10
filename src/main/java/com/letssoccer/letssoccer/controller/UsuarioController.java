package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import com.letssoccer.letssoccer.messages.sucess.MessageResponseDto;
import com.letssoccer.letssoccer.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<MessageResponseDto> cadastrar(
            @RequestBody @Valid UsuarioCadastroDto dto) {

        service.cadastrar(dto);
        return ResponseEntity.ok(
                new MessageResponseDto("Usuário cadastrado com sucesso")
        );
    }
}
