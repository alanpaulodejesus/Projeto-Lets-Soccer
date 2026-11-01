-- Clubes com IDs fixos
INSERT INTO clube (id, nome, informacao) VALUES
    (1, 'Cruzeiro', 'Minas Gerais'),
    (2, 'Atlético Mineiro', 'Minas Gerais')
ON CONFLICT (id) DO NOTHING;

-- Jogadores do Cruzeiro (IDs 1 a 31)
INSERT INTO jogador (id, nome, posicao, clube_id, foto) VALUES
    -- Goleiros
    (1,  'Cássio', 'Goleiro', 1, 'cassio.png'),
    (2,  'Matheus Cunha', 'Goleiro', 1, 'matheus-cunha.png'),
    (3,  'Otávio', 'Goleiro', 1, 'otavio.png'),

    -- Laterais
    (4,  'William', 'Lateral', 1, 'william.png'),
    (5,  'Fagner', 'Lateral', 1, 'fagner.png'),
    (6,  'Kaiki', 'Lateral', 1, 'kaiki.png'),
    (7,  'Kauã Moraes', 'Lateral', 1, 'kaua-moraes.png'),
    (8,  'Kauã Prates', 'Lateral', 1, 'kaua-prates.png'),

    -- Zagueiros
    (9,  'Fabrício Bruno', 'Zagueiro', 1, 'fabricio-bruno.png'),
    (10, 'João Marcelo', 'Zagueiro', 1, 'joao-marcelo.png'),
    (11, 'Jonathan Jesus', 'Zagueiro', 1, 'jonathan-jesus.png'),
    (12, 'Lucas Villalba', 'Zagueiro', 1, 'lucas-villalba.png'),
    (13, 'Bruno Alves', 'Zagueiro', 1, 'bruno-alves.png'),
    (14, 'Janderson', 'Zagueiro', 1, 'janderson.png'),

    -- Volantes
    (15, 'Lucas Romero', 'Volante', 1, 'lucas-romero.png'),
    (16, 'Lucas Silva', 'Volante', 1, 'lucas-silva.png'),
    (17, 'Matheus Henrique', 'Volante', 1, 'matheus-henrique.png'),
    (18, 'Christian', 'Volante', 1, 'christian.png'),

    -- Meias
    (19, 'Matheus Pereira', 'Meia', 1, 'matheus-pereira.png'),
    (20, 'Gerson', 'Meia', 1, 'gerson.png'),
    (21, 'Japa', 'Meia', 1, 'japa.png'),
    (22, 'Rodriguinho', 'Meia', 1, 'rodriguinho.png'),
    (23, 'Vitinho', 'Meia', 1, 'vitinho.png'),
    (24, 'Ryan Guilherme', 'Meia', 1, 'ryan-guilherme.png'),

    -- Atacantes
    (25, 'Kaio Jorge', 'Atacante', 1, 'kaio-jorge.png'),
    (26, 'Wanderson', 'Atacante', 1, 'wanderson.png'),
    (27, 'Marquinhos', 'Atacante', 1, 'marquinhos.png'),
    (28, 'Chico da Costa', 'Atacante', 1, 'chico-da-costa.png'),
    (29, 'Néiser Villarreal', 'Atacante', 1, 'neiser-villarreal.png'),
    (30, 'Kaique Kenji', 'Atacante', 1, 'kaique-kenji.png'),
    (31, 'Keny Arroyo', 'Atacante', 1, 'keny-arroyo.png')
ON CONFLICT (id) DO NOTHING;

-- Jogadores do Atlético Mineiro (IDs 32 a 57)
INSERT INTO jogador (id, nome, posicao, clube_id, foto) VALUES
    -- Goleiros
    (32, 'Everson', 'Goleiro', 2, 'everson.png'),
    (33, 'Gabriel Delfim', 'Goleiro', 2, 'gabriel-delfim.png'),
    (34, 'Robert', 'Goleiro', 2, 'robert.png'),

    -- Laterais
    (35, 'Saravia', 'Lateral', 2, 'saravia.png'),
    (36, 'Natanael', 'Lateral', 2, 'natanael.png'),
    (37, 'Guilherme Arana', 'Lateral', 2, 'guilherme-arana.png'),
    (38, 'Caio', 'Lateral', 2, 'caio.png'),

    -- Zagueiros
    (39, 'Junior Alonso', 'Zagueiro', 2, 'junior-alonso.png'),
    (40, 'Lyanco', 'Zagueiro', 2, 'lyanco.png'),
    (41, 'Vitor Hugo', 'Zagueiro', 2, 'vitor-hugo.png'),
    (42, 'Ivan Román', 'Zagueiro', 2, 'ivan-roman.png'),

    -- Volantes
    (43, 'Alan Franco', 'Volante', 2, 'alan-franco.png'),
    (44, 'Fausto Vera', 'Volante', 2, 'fausto-vera.png'),
    (45, 'Patrick Silva', 'Volante', 2, 'patrick-silva.png'),
    (46, 'Gabriel Menino', 'Volante', 2, 'gabriel-menino.png'),
    (47, 'Alexsander', 'Volante', 2, 'alexsander.png'),

    -- Meias
    (48, 'Gustavo Scarpa', 'Meia', 2, 'gustavo-scarpa.png'),
    (49, 'Igor Gomes', 'Meia', 2, 'igor-gomes.png'),
    (50, 'Bernard', 'Meia', 2, 'bernard.png'),
    (51, 'Reinier', 'Meia', 2, 'reinier.png'),

    -- Atacantes
    (52, 'Hulk', 'Atacante', 2, 'hulk.png'),
    (53, 'Cuello', 'Atacante', 2, 'cuello.png'),
    (54, 'Dudu', 'Atacante', 2, 'dudu.png'),
    (55, 'Biel', 'Atacante', 2, 'biel.png'),
    (56, 'Cadu', 'Atacante', 2, 'cadu.png'),
    (57, 'Caio Maia', 'Atacante', 2, 'caio-maia.png')
ON CONFLICT (id) DO NOTHING;

-- Ajusta as sequences para os próximos INSERTs automáticos não colidirem
SELECT setval(pg_get_serial_sequence('clube', 'id'),   (SELECT MAX(id) FROM clube));
SELECT setval(pg_get_serial_sequence('jogador', 'id'), (SELECT MAX(id) FROM jogador));