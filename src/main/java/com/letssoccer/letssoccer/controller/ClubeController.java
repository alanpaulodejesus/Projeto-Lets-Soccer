package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.service.ClubeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "https://micro-servico-evento.onrender.com")
@RestController
@RequestMapping("/clube")
public class ClubeController {

    private final ClubeService clubeService;

    public ClubeController(ClubeService clubeService) {
        this.clubeService = clubeService;
    }

    @PostMapping
    public ResponseEntity<ClubeResponseDto> criarClube(@RequestBody @Valid ClubeRequestDto clubeRequestDto) throws Exception {
        ClubeResponseDto clubeResponseDTO = clubeService.criarClube(clubeRequestDto);
        return ResponseEntity.ok(clubeResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ClubeResponseDto>> listarClubes() {
        List<ClubeResponseDto> clube = clubeService.listarClubes();
        return ResponseEntity.ok(clube);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubeResponseDto> buscarPorId(@PathVariable Integer id) {
        ClubeResponseDto clube = clubeService.buscarClubePorId(id);
        return ResponseEntity.ok(clube);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubeResponseDto> atualizarClube (
            @PathVariable Integer id,
            @RequestBody @Valid ClubeRequestDto clubeRequestDto) throws Exception {

        ClubeResponseDto atualizado =
                clubeService.atualizarClube(id, clubeRequestDto);

        return ResponseEntity.ok(atualizado);
    }

    //@DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarClube(@PathVariable Integer id) {
        clubeService.deletarClube(id);
        return ResponseEntity.ok().build();
    }

    //@DeleteMapping()
    public ResponseEntity<Void> deletarClubes() {
        clubeService.deletarClubes();
        return ResponseEntity.ok().build();
    }
}
