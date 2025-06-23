package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
