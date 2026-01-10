package com.letssoccer.letssoccer.controller;

import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.dto.LoginResponseDto;
import com.letssoccer.letssoccer.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto dto) {

        String token = authService.login(dto);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

}
