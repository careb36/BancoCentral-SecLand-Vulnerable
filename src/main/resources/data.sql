-- Insertar Usuarios (de forma idempotente)
-- ON CONFLICT(id) DO NOTHING: Si un usuario con ese ID ya existe, simplemente ignora esta línea.
INSERT INTO users (id, username, password, full_name, created_at) VALUES
                                                                      (1, 'carolina_p', '$2a$10$wL455.T8/y.P.WJ2b2aN9uBFy1dI5a2kig7P4PDBxPj49L5L5aZ5W', 'Carolina Pereira', '2025-06-22 20:00:00'),
                                                                      (2, 'test_user', '$2a$10$9.d/R4/L/LqI1S.y2.zDDeuXy20o3f7V9.0d2i6C1h43j2.y5n2eW', 'Test User', '2025-06-22 20:00:00')
    ON CONFLICT (id) DO NOTHING;

-- Insertar Cuentas Bancarias (de forma idempotente)
INSERT INTO accounts (id, account_number, account_type, balance, user_id, created_at) VALUES
                                                                                          (101, 'SEC1001-001', 'Ahorros', 5000.75, 1, '2025-06-22 20:00:00'),
                                                                                          (102, 'SEC1001-002', 'Corriente', 1250.00, 1, '2025-06-22 20:00:00'),
                                                                                          (201, 'SEC2002-001', 'Ahorros', 800.50, 2, '2025-06-22 20:00:00')
    ON CONFLICT (id) DO NOTHING;