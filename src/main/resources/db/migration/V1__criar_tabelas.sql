CREATE TABLE setores (
                         id BIGSERIAL PRIMARY KEY,
                         nome VARCHAR(100) NOT NULL,
                         sigla VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE categorias (
                            id BIGSERIAL PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            descricao TEXT,
                            setor_id BIGINT NOT NULL,
                            CONSTRAINT fk_categoria_setor FOREIGN KEY (setor_id) REFERENCES setores(id)
);

CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          matricula VARCHAR(50) NOT NULL,
                          tipo VARCHAR(30) NOT NULL,
                          setor_id BIGINT,
                          CONSTRAINT fk_usuario_setor FOREIGN KEY (setor_id) REFERENCES setores(id)
);

CREATE TABLE chamados (
                          id BIGSERIAL PRIMARY KEY,
                          titulo VARCHAR(150) NOT NULL,
                          descricao TEXT NOT NULL,
                          local VARCHAR(100) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          prioridade VARCHAR(30) NOT NULL,
                          data_abertura TIMESTAMP NOT NULL,
                          data_fechamento TIMESTAMP,
                          solucao TEXT,
                          solicitante_id BIGINT NOT NULL,
                          tecnico_id BIGINT,
                          categoria_id BIGINT NOT NULL,
                          CONSTRAINT fk_chamado_solicitante FOREIGN KEY (solicitante_id) REFERENCES usuarios(id),
                          CONSTRAINT fk_chamado_tecnico FOREIGN KEY (tecnico_id) REFERENCES usuarios(id),
                          CONSTRAINT fk_chamado_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE interacoes (
                            id BIGSERIAL PRIMARY KEY,
                            texto TEXT NOT NULL,
                            data_hora TIMESTAMP NOT NULL,
                            chamado_id BIGINT NOT NULL,
                            autor_id BIGINT NOT NULL,
                            CONSTRAINT fk_interacao_chamado FOREIGN KEY (chamado_id) REFERENCES chamados(id),
                            CONSTRAINT fk_interacao_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id)
);