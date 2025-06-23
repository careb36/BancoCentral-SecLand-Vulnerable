package com.secland.bancocentral.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Constructor para todos los argumentos
public class LoginResponseDto {
    private String message;
    private String token; // Aquí iría el JWT en una implementación real
}
