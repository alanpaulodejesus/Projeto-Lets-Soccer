package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.EscalacaoRequestDto;
import com.letssoccer.letssoccer.dto.EscalacaoResponseDto;
import com.letssoccer.letssoccer.service.EscalacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody @Valid EscalacaoRequestDto dto
    ) {

        escalacaoService.salvarEscalacao(id, esquema, dto);

        return ResponseEntity.ok(
                new EscalacaoResponseDto(
                        "Escalação salva com sucesso!",
                        esquema,
                        dto.getJogadoresIds()
                )
        );
    }
}