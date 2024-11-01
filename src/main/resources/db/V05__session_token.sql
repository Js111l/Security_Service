CREATE SEQUENCE IF NOT EXISTS user_session_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS USER_SESSION (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_session_seq'::regclass),
    session_id VARCHAR(255) NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT user_fkey FOREIGN KEY (user_id) REFERENCES app_user(id)
);
