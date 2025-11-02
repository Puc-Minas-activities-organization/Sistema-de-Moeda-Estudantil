-- Dados de Exemplo para Sistema de Moeda Estudantil
-- IMPORTANTE: Senhas são criptografadas com BCrypt
-- Senha padrão para todos: "senha123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- ============================================
-- INSERIR PROFESSORES
-- ============================================
INSERT INTO usuarios (email, senha, role, dtype, nome, saldo_moedas) VALUES
('joao.silva@puc.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR', 'Professor', 'Prof. João Silva', 1000.00),
('maria.santos@puc.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR', 'Professor', 'Prof. Maria Santos', 1000.00),
('pedro.oliveira@puc.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR', 'Professor', 'Prof. Pedro Oliveira', 1000.00);

-- ============================================
-- INSERIR ALUNOS
-- ============================================
INSERT INTO usuarios (email, senha, role, dtype, cpf, nome, rg, instituicao, curso, endereco, saldo_moedas) VALUES
('carlos.almeida@sga.pucminas.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO', 'Aluno', '12345678901', 'Carlos Almeida', 'MG-12.345.678', 'PUC Minas', 'Engenharia de Software', 'Rua das Flores, 123 - Belo Horizonte', 0.00),
('ana.costa@sga.pucminas.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO', 'Aluno', '23456789012', 'Ana Costa', 'MG-23.456.789', 'PUC Minas', 'Ciência da Computação', 'Av. Brasil, 456 - Contagem', 0.00),
('bruno.ferreira@sga.pucminas.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO', 'Aluno', '34567890123', 'Bruno Ferreira', 'MG-34.567.890', 'PUC Minas', 'Sistemas de Informação', 'Rua Minas Gerais, 789 - Betim', 0.00);

-- ============================================
-- INSERIR EMPRESAS PARCEIRAS
-- ============================================
INSERT INTO usuarios (email, senha, role, dtype, cnpj, nome) VALUES
('contato@restaurantepuc.com.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPRESA_PARCEIRA', 'EmpresaParceira', '12345678000190', 'Restaurante PUC'),
('atendimento@livrariauniversitaria.com.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPRESA_PARCEIRA', 'EmpresaParceira', '23456789000191', 'Livraria Universitária'),
('comercial@academiafit.com.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPRESA_PARCEIRA', 'EmpresaParceira', '34567890000192', 'Academia Fit Campus');

-- ============================================
-- INSERIR BENEFÍCIOS
-- ============================================
INSERT INTO beneficios (nome, custo, descricao, foto, ativo, empresa_parceira_id) VALUES
-- Restaurante PUC (ID 4)
('Desconto 10% em Refeições', 50.00, 'Ganhe 10% de desconto em todas as refeições do restaurante universitário durante 1 mês', 'https://example.com/restaurante-desconto.jpg', TRUE, 4),
('Refeição Completa Grátis', 100.00, 'Uma refeição completa (entrada, prato principal e sobremesa) totalmente grátis', 'https://example.com/refeicao-gratis.jpg', TRUE, 4),
('Kit Lanche Saudável', 30.00, 'Kit com sanduíche natural, suco e fruta', 'https://example.com/kit-lanche.jpg', TRUE, 4),

-- Livraria Universitária (ID 5)
('Vale Compra R$ 50', 100.00, 'Vale compra no valor de R$ 50,00 para livros, materiais escolares e papelaria', 'https://example.com/vale-50.jpg', TRUE, 5),
('Desconto 15% em Livros Técnicos', 80.00, 'Desconto de 15% na compra de livros técnicos e acadêmicos', 'https://example.com/desconto-livros.jpg', TRUE, 5),
('Kit Material Escolar', 60.00, 'Kit completo com cadernos, canetas, marca-textos e post-its', 'https://example.com/kit-material.jpg', TRUE, 5),

-- Academia Fit Campus (ID 6)
('1 Mês de Academia', 150.00, 'Acesso total à academia por 1 mês, incluindo aulas coletivas', 'https://example.com/academia-1mes.jpg', TRUE, 6),
('Pacote 3 Aulas de Personal', 120.00, 'Três sessões individuais com personal trainer', 'https://example.com/personal.jpg', TRUE, 6),
('Avaliação Física Completa', 40.00, 'Avaliação física completa com bioimpedância e plano de treino personalizado', 'https://example.com/avaliacao.jpg', TRUE, 6);

-- ============================================
-- INSERIR TRANSAÇÕES DE EXEMPLO
-- ============================================
-- Professor João (ID 1) envia moedas para Carlos (ID 4)
INSERT INTO transacoes_moeda (remetente_id, destinatario_id, valor, mensagem, data_transacao, status) VALUES
(1, 4, 100.00, 'Excelente participação no projeto de desenvolvimento web. Parabéns!', '2025-10-15 10:30:00', 'CONCLUIDA'),
(1, 5, 80.00, 'Ótima apresentação do seminário sobre Design Patterns', '2025-10-20 14:15:00', 'CONCLUIDA');

-- Professor Maria (ID 2) envia moedas
INSERT INTO transacoes_moeda (remetente_id, destinatario_id, valor, mensagem, data_transacao, status) VALUES
(2, 6, 120.00, 'Destaque na resolução dos exercícios de algoritmos avançados', '2025-10-18 09:00:00', 'CONCLUIDA'),
(2, 4, 50.00, 'Comprometimento e dedicação nas aulas de banco de dados', '2025-10-25 16:00:00', 'CONCLUIDA');

-- Professor Pedro (ID 3) envia moedas
INSERT INTO transacoes_moeda (remetente_id, destinatario_id, valor, mensagem, data_transacao, status) VALUES
(3, 5, 90.00, 'Trabalho excepcional na disciplina de Engenharia de Software', '2025-10-22 11:30:00', 'CONCLUIDA');

-- ============================================
-- INSERIR RESGATES DE EXEMPLO
-- ============================================
-- Carlos (ID 4) resgatou: 100 + 50 = 150 moedas recebidas
INSERT INTO resgates_beneficio (codigo_resgate, aluno_id, beneficio_id, valor_pago, data_resgate, data_utilizacao, status) VALUES
('RES-A1B2C3D4', 4, 1, 50.00, '2025-10-26 10:00:00', '2025-10-27 12:30:00', 'USADO'),
('RES-E5F6G7H8', 4, 3, 30.00, '2025-10-28 15:00:00', NULL, 'PENDENTE');

-- Ana (ID 5) resgatou: 80 + 90 = 170 moedas recebidas
INSERT INTO resgates_beneficio (codigo_resgate, aluno_id, beneficio_id, valor_pago, data_resgate, data_utilizacao, status) VALUES
('RES-I9J0K1L2', 5, 6, 60.00, '2025-10-27 11:00:00', NULL, 'PENDENTE');

-- Bruno (ID 6) resgatou: 120 moedas recebidas
INSERT INTO resgates_beneficio (codigo_resgate, aluno_id, beneficio_id, valor_pago, data_resgate, data_utilizacao, status) VALUES
('RES-M3N4O5P6', 6, 9, 40.00, '2025-10-29 14:00:00', '2025-10-30 10:00:00', 'USADO');

-- ============================================
-- ATUALIZAR SALDOS DOS ALUNOS (baseado nas transações)
-- ============================================
-- Carlos: recebeu 150, gastou 80 = saldo 70
UPDATE usuarios SET saldo_moedas = 70.00 WHERE id = 4;

-- Ana: recebeu 170, gastou 60 = saldo 110
UPDATE usuarios SET saldo_moedas = 110.00 WHERE id = 5;

-- Bruno: recebeu 120, gastou 40 = saldo 80
UPDATE usuarios SET saldo_moedas = 80.00 WHERE id = 6;

-- ============================================
-- ATUALIZAR SALDOS DOS PROFESSORES (baseado nas transações)
-- ============================================
-- João: tinha 1000, enviou 180 = saldo 820
UPDATE usuarios SET saldo_moedas = 820.00 WHERE id = 1;

-- Maria: tinha 1000, enviou 170 = saldo 830
UPDATE usuarios SET saldo_moedas = 830.00 WHERE id = 2;

-- Pedro: tinha 1000, enviou 90 = saldo 910
UPDATE usuarios SET saldo_moedas = 910.00 WHERE id = 3;

-- ============================================
-- VERIFICAÇÕES (Comentadas - use para testes)
-- ============================================
-- SELECT * FROM usuarios;
-- SELECT * FROM beneficios;
-- SELECT * FROM transacoes_moeda;
-- SELECT * FROM resgates_beneficio;
-- SELECT * FROM vw_extrato_aluno WHERE aluno_id = 4;
-- SELECT * FROM vw_extrato_professor WHERE professor_id = 1;
