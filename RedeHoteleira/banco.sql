-- Script do banco da Rede Hoteleira (MySQL / XAMPP)
-- Rodar no phpMyAdmin (aba SQL) ou no terminal: mysql -u root < banco.sql

CREATE DATABASE IF NOT EXISTS rede_hoteleira;
USE rede_hoteleira;

CREATE TABLE IF NOT EXISTS hotel (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(150) NOT NULL,
    cidade VARCHAR(80) NOT NULL,
    telefone VARCHAR(20),
    estrelas INT NOT NULL
);

CREATE TABLE IF NOT EXISTS quarto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT NOT NULL,
    numero INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    capacidade INT NOT NULL,
    valor_diaria DOUBLE NOT NULL,
    situacao VARCHAR(20) NOT NULL,
    UNIQUE (hotel_id, numero),
    FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

CREATE TABLE IF NOT EXISTS hospede (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    data_nascimento DATE,
    telefone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS funcionario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(100),
    salario DOUBLE NOT NULL,
    data_contratacao DATE NOT NULL,
    horario_entrada VARCHAR(5),
    horario_saida VARCHAR(5),
    ferias_inicio DATE,
    ferias_fim DATE,
    FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

CREATE TABLE IF NOT EXISTS servico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(200),
    valor DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS reserva (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hospede_id INT NOT NULL,
    quarto_id INT NOT NULL,
    data_entrada DATE NOT NULL,
    data_saida DATE NOT NULL,
    quantidade_hospedes INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    valor_total DOUBLE,
    FOREIGN KEY (hospede_id) REFERENCES hospede(id),
    FOREIGN KEY (quarto_id) REFERENCES quarto(id)
);

CREATE TABLE IF NOT EXISTS reserva_servico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reserva_id INT NOT NULL,
    servico_id INT NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DOUBLE NOT NULL,
    data DATE NOT NULL,
    FOREIGN KEY (reserva_id) REFERENCES reserva(id),
    FOREIGN KEY (servico_id) REFERENCES servico(id)
);

-- Dados de exemplo para testar (opcional)
INSERT INTO hotel (nome, endereco, cidade, telefone, estrelas) VALUES
('Hotel Central', 'Av. Borges de Medeiros, 100', 'Porto Alegre', '5130001000', 4),
('Hotel Serra', 'Rua das Hortênsias, 50', 'Gramado', '5430002000', 5);

INSERT INTO quarto (hotel_id, numero, tipo, capacidade, valor_diaria, situacao) VALUES
(1, 101, 'STANDARD', 2, 200, 'DISPONIVEL'),
(1, 201, 'LUXO', 3, 350, 'DISPONIVEL'),
(1, 301, 'SUITE', 4, 600, 'DISPONIVEL'),
(2, 10, 'STANDARD', 2, 250, 'DISPONIVEL'),
(2, 20, 'SUITE', 4, 800, 'DISPONIVEL');

INSERT INTO servico (nome, descricao, valor) VALUES
('Café da manhã', 'Café da manhã completo por pessoa', 40),
('Lavanderia', 'Lavagem de até 5 peças', 60),
('Serviço de quarto', 'Pedido de refeição no quarto', 80),
('Estacionamento', 'Diária de estacionamento', 30);
