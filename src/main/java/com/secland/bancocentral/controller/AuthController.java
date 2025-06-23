package com.secland.bancocentral.controller;

import com.secland.bancocentral.dto.LoginRequestDto;
import com.secland.bancocentral.dto.LoginResponseDto;
import com.secland.bancocentral.dto.RegisterUserDto;
import com.secland.bancocentral.model.User;
import com.secland.bancocentral.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager; // ¡Inyectamos el gestor de Spring!

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authService.register(registerUserDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            // Le pedimos a Spring Security que autentique al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Si la línea anterior no lanzó una excepción, el login es correcto
            String token = "simulated.jwt.token.for." + loginRequestDto.getUsername();
            return ResponseEntity.ok(new LoginResponseDto("Login exitoso", token));

        } catch (Exception e) {
            // Si la autenticación falla, Spring lanza una excepción que capturamos aquí
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}