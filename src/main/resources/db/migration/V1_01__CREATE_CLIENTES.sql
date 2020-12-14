CREATE TABLE public.clientes (
    id  BIGSERIAL NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    data_nascimento DATE NOT NULL,
    nome VARCHAR(150) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT cliente_cpf_uk unique (cpf)
);