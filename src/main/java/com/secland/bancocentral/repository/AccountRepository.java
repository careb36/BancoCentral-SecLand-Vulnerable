package com.secland.bancocentral.repository;

import com.secland.bancocentral.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}