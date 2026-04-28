package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.EsquemaTaticoRequestDto;
import com.letssoccer.letssoccer.dto.EsquemaTaticoResponseDto;
import com.letssoccer.letssoccer.service.EsquemaTaticoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clube")
public class EsquemaTaticoController {

    private final EsquemaTaticoService esquemaService;

    public EsquemaTaticoController(EsquemaTaticoService esquemaService) {
        this.esquemaService = esquemaService;
    }

    @PostMapping("/{id}/esquema-tatico")
    public ResponseEntity<EsquemaTaticoResponseDto> definirEsquema(
            @PathVariable Integer id,
            @RequestBody @Valid EsquemaTaticoRequestDto dto) {

        String esquema =
                esquemaService.definirEsquema(id, dto);
        return ResponseEntity.ok(
                new EsquemaTaticoResponseDto(
                        "Esquema tático definido com sucesso",
                        esquema
                )
        );
    }

}
