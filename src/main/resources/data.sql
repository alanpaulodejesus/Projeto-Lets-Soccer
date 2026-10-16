
INSERT INTO clube (nome, informacao)
SELECT 'Cruzeiro', 'Minas Gerais'
WHERE NOT EXISTS (
    SELECT 1
    FROM clube
    WHERE nome = 'Cruzeiro'
);


INSERT INTO jogador (nome, posicao, clube_id, foto)
SELECT
    dados.nome,
    dados.posicao,
    c.id,
    dados.foto
FROM (
    VALUES
        -- Goleiros
        ('Cássio', 'Goleiro', 'cassio.png'),
        ('Matheus Cunha', 'Goleiro', 'matheus-cunha.png'),
        ('Otávio', 'Goleiro', 'otavio.png'),

        -- Laterais
        ('William', 'Lateral', 'william.png'),
        ('Fagner', 'Lateral', 'fagner.png'),
        ('Kaiki', 'Lateral', 'kaiki.png'),
        ('Kauã Moraes', 'Lateral', 'kaua-moraes.png'),
        ('Kauã Prates', 'Lateral', 'kaua-prates.png'),

        -- Zagueiros
        ('Fabrício Bruno', 'Zagueiro', 'fabricio-bruno.png'),
        ('João Marcelo', 'Zagueiro', 'joao-marcelo.png'),
        ('Jonathan Jesus', 'Zagueiro', 'jonathan-jesus.png'),
        ('Lucas Villalba', 'Zagueiro', 'lucas-villalba.png'),
        ('Bruno Alves', 'Zagueiro', 'bruno-alves.png'),
        ('Janderson', 'Zagueiro', 'janderson.png'),

        -- Volantes
        ('Lucas Romero', 'Volante', 'lucas-romero.png'),
        ('Lucas Silva', 'Volante', 'lucas-silva.png'),
        ('Matheus Henrique', 'Volante', 'matheus-henrique.png'),
        ('Christian', 'Volante', 'christian.png'),

        -- Meias
        ('Matheus Pereira', 'Meia', 'matheus-pereira.png'),
        ('Gerson', 'Meia', 'gerson.png'),
        ('Japa', 'Meia', 'japa.png'),
        ('Rodriguinho', 'Meia', 'rodriguinho.png'),
        ('Vitinho', 'Meia', 'vitinho.png'),
        ('Ryan Guilherme', 'Meia', 'ryan-guilherme.png'),

        -- Atacantes
        ('Kaio Jorge', 'Atacante', 'kaio-jorge.png'),
        ('Wanderson', 'Atacante', 'wanderson.png'),
        ('Marquinhos', 'Atacante', 'marquinhos.png'),
        ('Chico da Costa', 'Atacante', 'chico-da-costa.png'),
        ('Néiser Villarreal', 'Atacante', 'neiser-villarreal.png'),
        ('Kaique Kenji', 'Atacante', 'kaique-kenji.png'),
        ('Keny Arroyo', 'Atacante', 'keny-arroyo.png')
) AS dados(nome, posicao, foto)
JOIN clube c
    ON c.nome = 'Cruzeiro'
WHERE NOT EXISTS (
    SELECT 1
    FROM jogador j
    WHERE j.nome = dados.nome
      AND j.clube_id = c.id
);




INSERT INTO clube (nome, informacao)
SELECT 'Atlético Mineiro', 'Minas Gerais'
WHERE NOT EXISTS (
    SELECT 1
    FROM clube
    WHERE nome = 'Atlético Mineiro'
);


INSERT INTO jogador (nome, posicao, clube_id, foto)
SELECT
    dados.nome,
    dados.posicao,
    c.id,
    dados.foto
FROM (
    VALUES
        -- Goleiros
        ('Everson', 'Goleiro', 'everson.png'),
        ('Gabriel Delfim', 'Goleiro', 'gabriel-delfim.png'),
        ('Robert', 'Goleiro', 'robert.png'),

        -- Laterais
        ('Saravia', 'Lateral', 'saravia.png'),
        ('Natanael', 'Lateral', 'natanael.png'),
        ('Guilherme Arana', 'Lateral', 'guilherme-arana.png'),
        ('Caio', 'Lateral', 'caio.png'),

        -- Zagueiros
        ('Junior Alonso', 'Zagueiro', 'junior-alonso.png'),
        ('Lyanco', 'Zagueiro', 'lyanco.png'),
        ('Vitor Hugo', 'Zagueiro', 'vitor-hugo.png'),
        ('Ivan Román', 'Zagueiro', 'ivan-roman.png'),

        -- Volantes
        ('Alan Franco', 'Volante', 'alan-franco.png'),
        ('Fausto Vera', 'Volante', 'fausto-vera.png'),
        ('Patrick Silva', 'Volante', 'patrick-silva.png'),
        ('Gabriel Menino', 'Volante', 'gabriel-menino.png'),
        ('Alexsander', 'Volante', 'alexsander.png'),

        -- Meias
        ('Gustavo Scarpa', 'Meia', 'gustavo-scarpa.png'),
        ('Igor Gomes', 'Meia', 'igor-gomes.png'),
        ('Bernard', 'Meia', 'bernard.png'),
        ('Reinier', 'Meia', 'reinier.png'),

        -- Atacantes
        ('Hulk', 'Atacante', 'hulk.png'),
        ('Cuello', 'Atacante', 'cuello.png'),
        ('Dudu', 'Atacante', 'dudu.png'),
        ('Biel', 'Atacante', 'biel.png'),
        ('Cadu', 'Atacante', 'cadu.png'),
        ('Caio Maia', 'Atacante', 'caio-maia.png')
) AS dados(nome, posicao, foto)
JOIN clube c
    ON c.nome = 'Atlético Mineiro'
WHERE NOT EXISTS (
    SELECT 1
    FROM jogador j
    WHERE j.nome = dados.nome
      AND j.clube_id = c.id
);
