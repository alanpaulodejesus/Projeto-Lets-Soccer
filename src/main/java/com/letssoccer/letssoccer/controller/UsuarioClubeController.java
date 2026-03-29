package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.UsuarioClubeRequestDto;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.sucess.MessageResponseDto;
import com.letssoccer.letssoccer.service.UsuarioClubeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioClubeController {

    private final UsuarioClubeService service;

    public UsuarioClubeController(UsuarioClubeService service) {
        this.service = service;
    }

    @PostMapping("/clube")
    public ResponseEntity<MessageResponseDto> selecionarClube(
            @AuthenticationPrincipal UsuarioEntity usuario,
            @RequestBody @Valid UsuarioClubeRequestDto dto
    ) {
        service.selecionarClube(usuario.getId(), dto.clubeId());
        return ResponseEntity.ok(
                new MessageResponseDto("Clube definido com sucesso!")
        );
    }
}
