CREATE SEQUENCE IF NOT EXISTS user_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS SHOP_USER (
    id bigint PRIMARY KEY DEFAULT nextval('user_sequence'::regclass),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email_confirmed bool NOT NULL DEFAULT FALSE
);


CREATE SEQUENCE IF NOT EXISTS verification_token_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;


CREATE TABLE IF NOT EXISTS public.VERIFICATION_TOKEN (
    id BIGINT PRIMARY KEY DEFAULT nextval('verification_token_sequence'::regclass),
    uuid VARCHAR(255),
    expiration_date timestamp without time zone,
    user_id BIGINT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES SHOP_USER(id)
);


