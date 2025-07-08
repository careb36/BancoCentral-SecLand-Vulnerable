package com.secland.centralbank.controller;

import com.secland.centralbank.dto.LoginRequestDto;
import com.secland.centralbank.dto.LoginResponseDto;
import com.secland.centralbank.dto.RegisterUserDto;
import com.secland.centralbank.model.User;
import com.secland.centralbank.service.AuthService;
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

    // Inject Spring Security's central AuthenticationManager
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authService.register(registerUserDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        // We delegate the authentication task entirely to the AuthenticationManager.
        // It will use our CustomUserDetailsService and PasswordEncoder automatically.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );

        // If the line above doesn't throw an exception, the user is authenticated.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // For now, we return a simple success message and a simulated token.
        String token = "simulated.jwt.token.for." + loginRequestDto.getUsername();
        return ResponseEntity.ok(new LoginResponseDto("Login successful!", token));
    }
}