package com.secland.bancocentral.service;

import com.secland.bancocentral.dto.TransferRequestDto;
import com.secland.bancocentral.model.Transaction;

public interface TransactionService {
    Transaction performTransfer(TransferRequestDto transferRequestDto);
}
