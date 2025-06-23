package com.secland.bancocentral.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequestDto {
    private Long sourceAccountId;
    private Long destinationAccountId;
    private BigDecimal amount;
    private String description;
}
