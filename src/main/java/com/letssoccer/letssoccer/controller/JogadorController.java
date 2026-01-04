package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.JogadorRequestDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.messages.sucess.MessageResponseDto;
import com.letssoccer.letssoccer.messages.sucess.SuccessMessages;
import com.letssoccer.letssoccer.service.JogadorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clube/{clubeId}/jogador")
public class JogadorController {

    private final JogadorService jogadorService;

    public JogadorController(JogadorService jogadorService) {
        this.jogadorService = jogadorService;
    }

    @PostMapping
    public ResponseEntity<JogadorResponseDto> adicionarJogador(
            @PathVariable Integer clubeId,
            @RequestBody @Valid JogadorRequestDto dto) {

        return ResponseEntity.ok(
                jogadorService.adicionarJogador(clubeId, dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<JogadorResponseDto>> listarJogadores(
            @PathVariable Integer clubeId) {

        return ResponseEntity.ok(
                jogadorService.listarJogadoresDoClube(clubeId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDto> deletarJogador(@PathVariable Integer id) {
        jogadorService.deletarJogador(id);
        return ResponseEntity.ok(
                new MessageResponseDto(SuccessMessages.JOGADOR_EXCLUIDO)
        );
    }
}
