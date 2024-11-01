CREATE SEQUENCE IF NOT EXISTS one_time_token_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS ONE_TIME_TOKEN (
    id BIGINT PRIMARY KEY DEFAULT nextval('one_time_token_sequence'::regclass),
    token VARCHAR(255),
    uuid VARCHAR(255),
    is_used BOOLEAN DEFAULT FALSE
);
