
-- limpa

DROP TABLE IF EXISTS Item_Locacao, Locacao, Equipamento, Cliente, Categoria CASCADE;
DROP FUNCTION IF EXISTS fn_calcular_valor_locacao CASCADE;
DROP PROCEDURE IF EXISTS sp_registrar_devolucao CASCADE;
DROP VIEW IF EXISTS vw_locacoes_ativas CASCADE;


CREATE TABLE Categoria (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(15) NOT NULL
);

CREATE TABLE Equipamento (
    id SERIAL PRIMARY KEY,
    modelo VARCHAR(100) NOT NULL,
    numero_serie VARCHAR(50) NOT NULL UNIQUE,
    valor_diaria NUMERIC(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'Disponivel',
    id_categoria INT NOT NULL,
    CONSTRAINT chk_status CHECK (status IN ('Disponivel', 'Alugado', 'Manutencao')),
    CONSTRAINT chk_valor_diaria CHECK (valor_diaria > 0),
    CONSTRAINT fk_categoria FOREIGN KEY (id_categoria) REFERENCES Categoria(id) ON DELETE RESTRICT
);

CREATE TABLE Locacao (
    id SERIAL PRIMARY KEY,
    data_retirada DATE NOT NULL DEFAULT CURRENT_DATE,
    data_devolucao_prevista DATE NOT NULL,
    data_devolucao_real DATE,
    valor_total NUMERIC(10, 2) DEFAULT 0.00,
    id_cliente INT NOT NULL,
    CONSTRAINT chk_datas CHECK (data_devolucao_prevista >= data_retirada),
    CONSTRAINT fk_cliente FOREIGN KEY (id_cliente) REFERENCES Cliente(id) ON DELETE CASCADE
);

CREATE TABLE Item_Locacao (
    id_locacao INT NOT NULL,
    id_equipamento INT NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_locacao, id_equipamento),
    CONSTRAINT fk_locacao FOREIGN KEY (id_locacao) REFERENCES Locacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_equipamento FOREIGN KEY (id_equipamento) REFERENCES Equipamento(id) ON DELETE RESTRICT
);

-- ====================================================================
-- 3. FUNCTION (Agora inteira e no lugar certo)
-- ====================================================================


CREATE OR REPLACE FUNCTION fn_calcular_valor_locacao(
    p_data_inicio DATE, 
    p_data_fim DATE, 
    p_valor_diaria NUMERIC, 
    p_tem_desconto BOOLEAN
) 
RETURNS NUMERIC AS $$
DECLARE
    v_dias INT;
    v_total NUMERIC;
BEGIN
    v_dias := p_data_fim - p_data_inicio;
    
    IF v_dias <= 0 THEN
        v_dias := 1;
    END IF;
    
    v_total := v_dias * p_valor_diaria;
    
    IF p_tem_desconto THEN
        v_total := v_total * 0.90;
    END IF;
    
    RETURN ROUND(v_total, 2);
END;
$$ LANGUAGE plpgsql;

-- ====================================================================
-- 4. PROCEDURE (Corrigida no final)
-- ====================================================================
CREATE OR REPLACE PROCEDURE sp_registrar_devolucao(
    p_id_locacao INT, 
    p_data_devolucao DATE
) AS $$
DECLARE
    v_data_prevista DATE;
    v_atraso INT;
    v_multa NUMERIC := 0.00;
    v_valor_atual NUMERIC;
BEGIN
    UPDATE Locacao 
    SET data_devolucao_real = p_data_devolucao 
    WHERE id = p_id_locacao;

    UPDATE Equipamento 
    SET status = 'Disponivel' 
    WHERE id IN (SELECT id_equipamento FROM Item_Locacao WHERE id_locacao = p_id_locacao);

    SELECT data_devolucao_prevista, valor_total 
    INTO v_data_prevista, v_valor_atual 
    FROM Locacao 
    WHERE id = p_id_locacao;
    
    v_atraso := p_data_devolucao - v_data_prevista;
    
    IF v_atraso > 0 THEN
        v_multa := v_valor_atual * 0.15 * v_atraso;
        
        UPDATE Locacao 
        SET valor_total = valor_total + v_multa 
        WHERE id = p_id_locacao;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- ====================================================================
-- 5. VIEW (Com as colunas exatas que o Java pede)
-- ====================================================================


CREATE OR REPLACE VIEW vw_locacoes_ativas AS
SELECT 
    l.id AS id_locacao,
    c.nome AS nome_cliente,
    e.modelo AS modelo_equipamento,
    l.data_retirada,
    l.data_devolucao_prevista,
    COALESCE(TO_CHAR(l.data_devolucao_real, 'DD/MM/YYYY'), 'Em andamento') AS status_devolucao,
    l.valor_total
FROM Locacao l
JOIN Cliente c ON l.id_cliente = c.id
JOIN Item_Locacao il ON l.id = il.id_locacao
JOIN Equipamento e ON il.id_equipamento = e.id
WHERE l.data_devolucao_real IS NULL;

-- ====================================================================
-- 6. INSERTS DE TESTE (Populando o Banco)
-- ====================================================================
INSERT INTO Categoria (nome) VALUES 
('Câmeras'), ('Lentes'), ('Iluminação'), ('Áudio'), ('Acessórios');

INSERT INTO Cliente (nome, cpf, telefone) VALUES 
('Produtora Foco PT', '123.456.789-01', '912345678'),
('Freelancer Silva', '987.654.321-02', '987654321'),
('Vision Filmes', '111.222.333-44', '955555555'),
('Lucas Diretor', '555.666.777-88', '944444444');

INSERT INTO Equipamento (modelo, numero_serie, valor_diaria, status, id_categoria) VALUES 
('Sony A7IV', 'SN-SONY-1002', 150.00, 'Disponivel', 1),
('Lente 24-70mm f2.8', 'SN-GM-5541', 80.00, 'Disponivel', 2),
('Bastão de Led Amaran', 'SN-AM-9982', 40.00, 'Disponivel', 3),
('Microfone Lapela DJI', 'SN-DJI-881', 50.00, 'Disponivel', 4),
('Tripé Manfrotto', 'SN-MANF-110', 25.00, 'Disponivel', 5);

INSERT INTO Locacao (data_retirada, data_devolucao_prevista, valor_total, id_cliente) VALUES 
('2026-06-11', '2026-06-14', fn_calcular_valor_locacao('2026-06-11', '2026-06-14', 150.00, FALSE), 1),
(CURRENT_DATE, CURRENT_DATE + 5, 80.00, 1),
('2026-06-12', '2026-06-15', fn_calcular_valor_locacao('2026-06-12', '2026-06-15', 50.00, TRUE), 3),
(CURRENT_DATE, CURRENT_DATE + 2, 25.00, 4),
(CURRENT_DATE, CURRENT_DATE + 3, 150.00, 2); -- Adicionada a Locação 5 e trocado a vírgula pelo ponto e vírgula

INSERT INTO Item_Locacao (id_locacao, id_equipamento, subtotal) VALUES 
(1, 1, 450.00),
(2, 2, 80.00),
(3, 4, 150.00),
(4, 5, 50.00),
(5, 1, 150.00);

UPDATE Equipamento SET status = 'Alugado' WHERE id IN (1, 2, 4, 5);


INSERT INTO Item_Locacao (id_locacao, id_equipamento, subtotal) VALUES 
(1, 1, 450.00),
(2, 2, 80.00),
(3, 4, 150.00),
(4, 5, 50.00),
(5, 1, 150.00);

UPDATE Equipamento SET status = 'Alugado' WHERE id IN (1, 2, 4, 5);

SELECT * FROM Equipamento;
SELECT * FROM Locacao;
SELECT * FROM vw_locacoes_ativas;