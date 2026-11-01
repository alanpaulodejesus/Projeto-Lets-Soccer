package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.EscalacaoRequestDto;
import com.letssoccer.letssoccer.dto.EscalacaoResponseDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.service.EscalacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clube")
public class EscalacaoController {

    private final EscalacaoService escalacaoService;

    public EscalacaoController(EscalacaoService escalacaoService) {
        this.escalacaoService = escalacaoService;
    }

    @PostMapping("/{id}/escalacoes")
    public ResponseEntity<EscalacaoResponseDto> salvarEscalacao(
            @PathVariable Integer id,
            @RequestParam String esquema,
            @RequestBody @Valid EscalacaoRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<JogadorResponseDto> jogadores =
                escalacaoService.salvarEscalacao(id, esquema, dto, userDetails.getUsername());

        return ResponseEntity.ok(
                new EscalacaoResponseDto("Escalação salva com sucesso!", esquema, jogadores)
        );
    }

    @GetMapping("/{id}/escalacao")
    public ResponseEntity<EscalacaoResponseDto> buscarEscalacao(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                escalacaoService.buscarEscalacao(id, userDetails.getUsername())
        );
    }
}