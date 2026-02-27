package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "usuariosucesso",
                "usuariosucesso@email.com",
                "123",
                "123"
        );

        when(repository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senhaCriptografada");

        service.cadastrar(dto);

        ArgumentCaptor<UsuarioEntity> captor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(repository).save(captor.capture());

        UsuarioEntity salvo = captor.getValue();
        assertEquals("usuariosucesso", salvo.getNome());
        assertEquals("usuariosucesso@email.com", salvo.getEmail());
        assertEquals("senhaCriptografada", salvo.getSenha());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "user",
                "user@email.com",
                "123",
                "123"
        );

        when(repository.existsByEmail(dto.email())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.cadastrar(dto));
        assertEquals("E-mail já cadastrado", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSenhasNaoConferem() {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "user",
                "user@email.com",
                "123",
                "456"
        );

        when(repository.existsByEmail(dto.email())).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.cadastrar(dto));
        assertEquals("Senhas não conferem", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveLancarExcecaoQuandoNomeInvalido(String nomeInvalido) {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                nomeInvalido,
                "user@email.com",
                "123",
                "123"
        );

        when(repository.existsByEmail(dto.email())).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.cadastrar(dto));
        assertEquals("Nome é obrigatório", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveLancarExcecaoQuandoEmailInvalido(String emailInvalido) {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "user",
                emailInvalido,
                "123",
                "123"
        );

        when(repository.existsByEmail(dto.email())).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.cadastrar(dto));
        assertEquals("Email é obrigatório", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveLancarExcecaoQuandoSenhaInvalida(String senhaInvalida) {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "user",
                "user@email.com",
                senhaInvalida,
                "123"
        );

        when(repository.existsByEmail(dto.email())).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.cadastrar(dto));
        assertEquals("Senha é obrigatória", ex.getMessage());

        verify(repository, never()).save(any());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveLancarExcecaoQuandoConfirmacaoSenhaInvalida(String confirmacaoSenhaInvalida) {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "user",
                "user@email.com",
                "123",
                confirmacaoSenhaInvalida
        );

        when(repository.existsByEmail(dto.email())).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> service.cadastrar(dto));
        assertEquals("Senha é obrigatória", ex.getMessage());

        verify(repository, never()).save(any());
    }
}