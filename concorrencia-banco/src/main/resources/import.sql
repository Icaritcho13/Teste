-- Conta da PARTE 1 (sem controle de concorrência). id = 1, saldo = 1000,00
INSERT INTO conta_bancaria (saldo) VALUES (1000.00);

-- Conta da PARTE 2 (com @Version). id = 1, saldo = 1000,00, version = 0
-- O version = 0 é OBRIGATÓRIO: sem ele o primeiro UPDATE otimista compararia
-- contra NULL e o comportamento ficaria imprevisível.
INSERT INTO conta_bancaria_versionada (saldo, version) VALUES (1000.00, 0);
