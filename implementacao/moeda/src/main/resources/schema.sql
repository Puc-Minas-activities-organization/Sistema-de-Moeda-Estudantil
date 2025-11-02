-- Schema SQL para Sistema de Moeda Estudantil
-- Banco de Dados: MySQL 8.0+

-- ============================================
-- TABELA: usuarios (base para todas as entidades)
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    dtype VARCHAR(31) NOT NULL, -- Discriminador JPA (Professor, Aluno, EmpresaParceira)
    
    -- Campos específicos do Aluno
    cpf VARCHAR(11) UNIQUE,
    nome VARCHAR(255),
    rg VARCHAR(20),
    instituicao VARCHAR(255),
    curso VARCHAR(255),
    endereco VARCHAR(500),
    saldo_moedas DECIMAL(10,2) DEFAULT 0.00,
    
    -- Campos específicos da Empresa Parceira
    cnpj VARCHAR(14) UNIQUE,
    
    -- Índices
    INDEX idx_email (email),
    INDEX idx_cpf (cpf),
    INDEX idx_cnpj (cnpj),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: beneficios
-- ============================================
CREATE TABLE IF NOT EXISTS beneficios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    custo DECIMAL(10,2) NOT NULL,
    descricao TEXT,
    foto VARCHAR(500),
    ativo BOOLEAN DEFAULT TRUE,
    empresa_parceira_id BIGINT NOT NULL,
    
    FOREIGN KEY (empresa_parceira_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_ativo (ativo),
    INDEX idx_empresa (empresa_parceira_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: transacoes_moeda
-- ============================================
CREATE TABLE IF NOT EXISTS transacoes_moeda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    remetente_id BIGINT NOT NULL,
    destinatario_id BIGINT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    mensagem TEXT NOT NULL,
    data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'CONCLUIDA',
    
    FOREIGN KEY (remetente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_remetente (remetente_id),
    INDEX idx_destinatario (destinatario_id),
    INDEX idx_data (data_transacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: resgates_beneficio
-- ============================================
CREATE TABLE IF NOT EXISTS resgates_beneficio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_resgate VARCHAR(20) NOT NULL UNIQUE,
    aluno_id BIGINT NOT NULL,
    beneficio_id BIGINT NOT NULL,
    valor_pago DECIMAL(10,2) NOT NULL,
    data_resgate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_utilizacao TIMESTAMP NULL,
    status VARCHAR(50) DEFAULT 'PENDENTE',
    
    FOREIGN KEY (aluno_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (beneficio_id) REFERENCES beneficios(id) ON DELETE CASCADE,
    INDEX idx_codigo (codigo_resgate),
    INDEX idx_aluno (aluno_id),
    INDEX idx_beneficio (beneficio_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- VIEWS ÚTEIS (Opcional)
-- ============================================

-- View para extrato de alunos
CREATE OR REPLACE VIEW vw_extrato_aluno AS
SELECT 
    a.id as aluno_id,
    a.nome,
    a.saldo_moedas,
    'RECEBIMENTO' as tipo,
    t.valor,
    t.mensagem as descricao,
    t.data_transacao as data,
    p.nome as origem
FROM usuarios a
LEFT JOIN transacoes_moeda t ON a.id = t.destinatario_id
LEFT JOIN usuarios p ON t.remetente_id = p.id
WHERE a.dtype = 'Aluno'

UNION ALL

SELECT 
    a.id as aluno_id,
    a.nome,
    a.saldo_moedas,
    'RESGATE' as tipo,
    -r.valor_pago as valor,
    CONCAT('Resgate: ', b.nome, ' (', r.codigo_resgate, ')') as descricao,
    r.data_resgate as data,
    e.nome as origem
FROM usuarios a
LEFT JOIN resgates_beneficio r ON a.id = r.aluno_id
LEFT JOIN beneficios b ON r.beneficio_id = b.id
LEFT JOIN usuarios e ON b.empresa_parceira_id = e.id
WHERE a.dtype = 'Aluno'

ORDER BY aluno_id, data DESC;

-- View para extrato de professores
CREATE OR REPLACE VIEW vw_extrato_professor AS
SELECT 
    p.id as professor_id,
    p.nome,
    'ENVIO' as tipo,
    -t.valor as valor,
    CONCAT('Enviado para ', a.nome, ': ', t.mensagem) as descricao,
    t.data_transacao as data
FROM usuarios p
LEFT JOIN transacoes_moeda t ON p.id = t.remetente_id
LEFT JOIN usuarios a ON t.destinatario_id = a.id
WHERE p.dtype = 'Professor'
ORDER BY professor_id, data DESC;
