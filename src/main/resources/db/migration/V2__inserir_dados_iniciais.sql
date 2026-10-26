-- Inserir Setores
INSERT INTO setores (id, nome, sigla) VALUES (1, 'Coordenação de Tecnologia da Informação', 'COTIC');
INSERT INTO setores (id, nome, sigla) VALUES (2, 'Coordenação de Manutenção e Infraestrutura', 'CMINF');

-- Inserir Categorias
INSERT INTO categorias (id, nome, descricao, setor_id) VALUES (1, 'Redes e Internet', 'Problemas com cabo de rede ou Wi-Fi', 1);
INSERT INTO categorias (id, nome, descricao, setor_id) VALUES (2, 'Sistemas e Software', 'Falhas em sistemas acadêmicos ou e-mails', 1);
INSERT INTO categorias (id, nome, descricao, setor_id) VALUES (3, 'Projetores e Multimídia', 'Manutenção em projetores de aula', 2);
INSERT INTO categorias (id, nome, descricao, setor_id) VALUES (4, 'Elétrica e Hidráulica', 'Problemas em tomadas ou lâmpadas', 2);

-- Inserir Usuários (2 Solicitantes, 2 Técnicos)
INSERT INTO usuarios (id, nome, email, matricula, tipo, setor_id) VALUES (1, 'Ana Solicitante', 'ana@ifrn.edu.br', '2026101', 'SOLICITANTE', NULL);
INSERT INTO usuarios (id, nome, email, matricula, tipo, setor_id) VALUES (2, 'Carlos Solicitante', 'carlos@ifrn.edu.br', '2026102', 'SOLICITANTE', NULL);
INSERT INTO usuarios (id, nome, email, matricula, tipo, setor_id) VALUES (3, 'João Técnico TI', 'joao.ti@ifrn.edu.br', '2026201', 'TECNICO', 1);
INSERT INTO usuarios (id, nome, email, matricula, tipo, setor_id) VALUES (4, 'Maria Técnica Manutenção', 'maria.manut@ifrn.edu.br', '2026202', 'TECNICO', 2);