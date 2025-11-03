-- ============================================
-- Dados de Exemplo para Sistema de Moeda Estudantil
-- ============================================
-- IMPORTANTE: Senhas criptografadas com BCrypt
-- Senha padrão para todos: "senha123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
--
-- ESTRUTURA: Herança JOINED com 3 níveis
-- - Tabela 'usuario': base (id, email, senha, role)
-- - Tabela 'usuario_cadastravel': intermediária para aluno e empresa (id FK para usuario)
-- - Tabela 'professor': herda direto de usuario (id FK para usuario)
-- - Tabela 'aluno': herda de usuario_cadastravel (id FK para usuario_cadastravel)
-- - Tabela 'empresa_parceira': herda de usuario_cadastravel (id FK para usuario_cadastravel)
-- ============================================

-- ============================================
-- INSERIR PROFESSORES
-- ============================================
-- Professor herda DIRETAMENTE de usuario (sem tabela intermediária)
-- Passo 1: Inserir em usuario
INSERT INTO usuario (email, senha, role) VALUES
('joao.silva@puc.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR'),
('maria.santos@puc.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR'),
('pedro.oliveira@puc.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PROFESSOR');

-- Passo 2: Inserir em professor (direto, sem intermediária)
INSERT INTO professor (id, cpf, nome, departamento, instituicao, saldo_moedas) 
SELECT u.id, '11111111111', 'Prof. João Silva', 'Engenharia de Software', 'PUC Minas', 1000.00
FROM usuario u WHERE u.email = 'joao.silva@puc.br';

INSERT INTO professor (id, cpf, nome, departamento, instituicao, saldo_moedas) 
SELECT u.id, '22222222222', 'Prof. Maria Santos', 'Ciência da Computação', 'PUC Minas', 1000.00
FROM usuario u WHERE u.email = 'maria.santos@puc.br';

INSERT INTO professor (id, cpf, nome, departamento, instituicao, saldo_moedas) 
SELECT u.id, '33333333333', 'Prof. Pedro Oliveira', 'Sistemas de Informação', 'PUC Minas', 1000.00
FROM usuario u WHERE u.email = 'pedro.oliveira@puc.br';

-- ============================================
-- INSERIR ALUNOS
-- ============================================
-- Aluno herda de usuario_cadastravel, que herda de usuario (3 níveis!)
-- Passo 1: Inserir em usuario
INSERT INTO usuario (email, senha, role) VALUES
('carlos.almeida@sga.pucminas.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO'),
('ana.costa@sga.pucminas.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO'),
('bruno.ferreira@sga.pucminas.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ALUNO');

-- Passo 2: Inserir em usuario_cadastravel (tabela intermediária)
INSERT INTO usuario_cadastravel (id) 
SELECT u.id FROM usuario u WHERE u.email = 'carlos.almeida@sga.pucminas.br';

INSERT INTO usuario_cadastravel (id) 
SELECT u.id FROM usuario u WHERE u.email = 'ana.costa@sga.pucminas.br';

INSERT INTO usuario_cadastravel (id) 
SELECT u.id FROM usuario u WHERE u.email = 'bruno.ferreira@sga.pucminas.br';

-- Passo 3: Inserir em aluno (referenciando usuario_cadastravel)
INSERT INTO aluno (id, cpf, nome, rg, instituicao, curso, endereco, saldo_moedas) 
SELECT uc.id, '12345678901', 'Carlos Almeida', 'MG-12.345.678', 'PUC Minas', 'Engenharia de Software', 'Rua das Flores, 123 - Belo Horizonte', 0.00
FROM usuario_cadastravel uc 
JOIN usuario u ON uc.id = u.id 
WHERE u.email = 'carlos.almeida@sga.pucminas.br';

INSERT INTO aluno (id, cpf, nome, rg, instituicao, curso, endereco, saldo_moedas) 
SELECT uc.id, '23456789012', 'Ana Costa', 'MG-23.456.789', 'PUC Minas', 'Ciência da Computação', 'Av. Brasil, 456 - Contagem', 0.00
FROM usuario_cadastravel uc 
JOIN usuario u ON uc.id = u.id 
WHERE u.email = 'ana.costa@sga.pucminas.br';

INSERT INTO aluno (id, cpf, nome, rg, instituicao, curso, endereco, saldo_moedas) 
SELECT uc.id, '34567890123', 'Bruno Ferreira', 'MG-34.567.890', 'PUC Minas', 'Sistemas de Informação', 'Rua Minas Gerais, 789 - Betim', 0.00
FROM usuario_cadastravel uc 
JOIN usuario u ON uc.id = u.id 
WHERE u.email = 'bruno.ferreira@sga.pucminas.br';

-- ============================================
-- INSERIR EMPRESAS PARCEIRAS
-- ============================================
-- Empresa herda de usuario_cadastravel, que herda de usuario (3 níveis!)
-- Passo 1: Inserir em usuario
INSERT INTO usuario (email, senha, role) VALUES
('contato@restaurantepuc.com.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPRESA_PARCEIRA'),
('atendimento@livrariauniversitaria.com.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPRESA_PARCEIRA'),
('comercial@academiafit.com.br', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPRESA_PARCEIRA');

-- Passo 2: Inserir em usuario_cadastravel (tabela intermediária)
INSERT INTO usuario_cadastravel (id) 
SELECT u.id FROM usuario u WHERE u.email = 'contato@restaurantepuc.com.br';

INSERT INTO usuario_cadastravel (id) 
SELECT u.id FROM usuario u WHERE u.email = 'atendimento@livrariauniversitaria.com.br';

INSERT INTO usuario_cadastravel (id) 
SELECT u.id FROM usuario u WHERE u.email = 'comercial@academiafit.com.br';

-- Passo 3: Inserir em empresa_parceira (referenciando usuario_cadastravel)
INSERT INTO empresa_parceira (id, cnpj, nome) 
SELECT uc.id, '12345678000190', 'Restaurante PUC'
FROM usuario_cadastravel uc 
JOIN usuario u ON uc.id = u.id 
WHERE u.email = 'contato@restaurantepuc.com.br';

INSERT INTO empresa_parceira (id, cnpj, nome) 
SELECT uc.id, '23456789000191', 'Livraria Universitária'
FROM usuario_cadastravel uc 
JOIN usuario u ON uc.id = u.id 
WHERE u.email = 'atendimento@livrariauniversitaria.com.br';

INSERT INTO empresa_parceira (id, cnpj, nome) 
SELECT uc.id, '34567890000192', 'Academia Fit Campus'
FROM usuario_cadastravel uc 
JOIN usuario u ON uc.id = u.id 
WHERE u.email = 'comercial@academiafit.com.br';

-- ============================================
-- INSERIR BENEFÍCIOS
-- ============================================
-- Os IDs das empresas serão 7, 8 e 9 (após os 3 professores e 3 alunos)
INSERT INTO beneficio (nome, custo, descricao, foto, ativo, empresa_parceira_id) VALUES
-- Restaurante PUC (empresa_parceira_id será 7)
('Desconto 10% em Refeições', 50.00, 'Ganhe 10% de desconto em todas as refeições do restaurante universitário durante 1 mês', 'https://example.com/restaurante-desconto.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '12345678000190')),
('Refeição Completa Grátis', 100.00, 'Uma refeição completa (entrada, prato principal e sobremesa) totalmente grátis', 'https://example.com/refeicao-gratis.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '12345678000190')),
('Kit Lanche Saudável', 30.00, 'Kit com sanduíche natural, suco e fruta', 'https://example.com/kit-lanche.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '12345678000190')),

-- Livraria Universitária
('Vale Compra R$ 50', 100.00, 'Vale compra no valor de R$ 50,00 para livros, materiais escolares e papelaria', 'https://example.com/vale-50.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '23456789000191')),
('Desconto 15% em Livros Técnicos', 80.00, 'Desconto de 15% na compra de livros técnicos e acadêmicos', 'https://example.com/desconto-livros.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '23456789000191')),
('Kit Material Escolar', 60.00, 'Kit completo com cadernos, canetas, marca-textos e post-its', 'https://example.com/kit-material.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '23456789000191')),

-- Academia Fit Campus
('1 Mês de Academia', 150.00, 'Acesso total à academia por 1 mês, incluindo aulas coletivas', 'https://example.com/academia-1mes.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '34567890000192')),
('Pacote 3 Aulas de Personal', 120.00, 'Três sessões individuais com personal trainer', 'https://example.com/personal.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '34567890000192')),
('Avaliação Física Completa', 40.00, 'Avaliação física completa com bioimpedância e plano de treino personalizado', 'https://example.com/avaliacao.jpg', TRUE, (SELECT id FROM empresa_parceira WHERE cnpj = '34567890000192'));

-- ============================================
-- INSERIR TRANSAÇÕES DE EXEMPLO
-- ============================================
-- ATENÇÃO: Tabela real é 'transacoes_moeda' (plural)
-- Campos: professor_id e aluno_id (não remetente/destinatario)
-- Professor João envia moedas para alunos
INSERT INTO transacoes_moeda (professor_id, aluno_id, valor, mensagem, data_transacao, status) VALUES
((SELECT id FROM professor WHERE cpf = '11111111111'), (SELECT id FROM aluno WHERE cpf = '12345678901'), 100.00, 'Excelente participação no projeto de desenvolvimento web. Parabéns!', '2025-10-15 10:30:00', 'CONCLUIDA'),
((SELECT id FROM professor WHERE cpf = '11111111111'), (SELECT id FROM aluno WHERE cpf = '23456789012'), 80.00, 'Ótima apresentação do seminário sobre Design Patterns', '2025-10-20 14:15:00', 'CONCLUIDA');

-- Professor Maria envia moedas
INSERT INTO transacoes_moeda (professor_id, aluno_id, valor, mensagem, data_transacao, status) VALUES
((SELECT id FROM professor WHERE cpf = '22222222222'), (SELECT id FROM aluno WHERE cpf = '34567890123'), 120.00, 'Destaque na resolução dos exercícios de algoritmos avançados', '2025-10-18 09:00:00', 'CONCLUIDA'),
((SELECT id FROM professor WHERE cpf = '22222222222'), (SELECT id FROM aluno WHERE cpf = '12345678901'), 50.00, 'Comprometimento e dedicação nas aulas de banco de dados', '2025-10-25 16:00:00', 'CONCLUIDA');

-- Professor Pedro envia moedas
INSERT INTO transacoes_moeda (professor_id, aluno_id, valor, mensagem, data_transacao, status) VALUES
((SELECT id FROM professor WHERE cpf = '33333333333'), (SELECT id FROM aluno WHERE cpf = '23456789012'), 90.00, 'Trabalho excepcional na disciplina de Engenharia de Software', '2025-10-22 11:30:00', 'CONCLUIDA');

-- ============================================
-- INSERIR RESGATES DE EXEMPLO
-- ============================================
-- ATENÇÃO: Tabela real é 'resgates_beneficio' (plural)
-- Status: PENDENTE, USADO, CONCLUIDO, CANCELADO
-- Carlos resgatou benefícios
INSERT INTO resgates_beneficio (codigo_resgate, aluno_id, beneficio_id, valor_pago, data_resgate, data_utilizacao, status) VALUES
('RES-A1B2C3D4', (SELECT id FROM aluno WHERE cpf = '12345678901'), (SELECT id FROM beneficio WHERE nome = 'Desconto 10% em Refeições' LIMIT 1), 50.00, '2025-10-26 10:00:00', '2025-10-27 12:30:00', 'USADO'),
('RES-E5F6G7H8', (SELECT id FROM aluno WHERE cpf = '12345678901'), (SELECT id FROM beneficio WHERE nome = 'Kit Lanche Saudável' LIMIT 1), 30.00, '2025-10-28 15:00:00', NULL, 'PENDENTE');

-- Ana resgatou benefícios
INSERT INTO resgates_beneficio (codigo_resgate, aluno_id, beneficio_id, valor_pago, data_resgate, data_utilizacao, status) VALUES
('RES-I9J0K1L2', (SELECT id FROM aluno WHERE cpf = '23456789012'), (SELECT id FROM beneficio WHERE nome = 'Kit Material Escolar' LIMIT 1), 60.00, '2025-10-27 11:00:00', NULL, 'PENDENTE');

-- Bruno resgatou benefícios
INSERT INTO resgates_beneficio (codigo_resgate, aluno_id, beneficio_id, valor_pago, data_resgate, data_utilizacao, status) VALUES
('RES-M3N4O5P6', (SELECT id FROM aluno WHERE cpf = '34567890123'), (SELECT id FROM beneficio WHERE nome = 'Avaliação Física Completa' LIMIT 1), 40.00, '2025-10-29 14:00:00', '2025-10-30 10:00:00', 'USADO');

-- ============================================
-- ATUALIZAR SALDOS DOS ALUNOS (baseado nas transações)
-- ============================================
-- Carlos: recebeu 150, gastou 80 = saldo 70
UPDATE aluno SET saldo_moedas = 70.00 WHERE cpf = '12345678901';

-- Ana: recebeu 170, gastou 60 = saldo 110
UPDATE aluno SET saldo_moedas = 110.00 WHERE cpf = '23456789012';

-- Bruno: recebeu 120, gastou 40 = saldo 80
UPDATE aluno SET saldo_moedas = 80.00 WHERE cpf = '34567890123';

-- ============================================
-- ATUALIZAR SALDOS DOS PROFESSORES (baseado nas transações)
-- ============================================
-- João: tinha 1000, enviou 180 = saldo 820
UPDATE professor SET saldo_moedas = 820.00 WHERE cpf = '11111111111';

-- Maria: tinha 1000, enviou 170 = saldo 830
UPDATE professor SET saldo_moedas = 830.00 WHERE cpf = '22222222222';

-- Pedro: tinha 1000, enviou 90 = saldo 910
UPDATE professor SET saldo_moedas = 910.00 WHERE cpf = '33333333333';

-- ============================================
-- VERIFICAÇÕES (Comentadas - use para testes)
-- ============================================
-- Verificar professores (herança direta de usuario)
-- SELECT u.id, u.email, u.role, p.nome, p.cpf, p.saldo_moedas 
-- FROM usuario u 
-- JOIN professor p ON u.id = p.id;

-- Verificar alunos (herança com 3 níveis: usuario -> usuario_cadastravel -> aluno)
-- SELECT u.id, u.email, u.role, a.nome, a.cpf, a.saldo_moedas 
-- FROM usuario u 
-- JOIN usuario_cadastravel uc ON u.id = uc.id
-- JOIN aluno a ON uc.id = a.id;

-- Verificar empresas (herança com 3 níveis: usuario -> usuario_cadastravel -> empresa_parceira)
-- SELECT u.id, u.email, u.role, e.nome, e.cnpj 
-- FROM usuario u 
-- JOIN usuario_cadastravel uc ON u.id = uc.id
-- JOIN empresa_parceira e ON uc.id = e.id;

-- Verificar benefícios
-- SELECT * FROM beneficio;

-- Verificar transações (tabela: transacoes_moeda)
-- SELECT * FROM transacoes_moeda;

-- Verificar resgates (tabela: resgates_beneficio)
-- SELECT * FROM resgates_beneficio;
