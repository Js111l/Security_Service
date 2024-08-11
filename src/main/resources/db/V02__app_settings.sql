

CREATE SEQUENCE IF NOT EXISTS app_settings_sequence
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS public.APPLICATION_SETTINGS (
    id BIGINT PRIMARY KEY DEFAULT nextval('app_settings_sequence'::regclass),
    setting_type VARCHAR(255) NOT NULL,
    setting_value VARCHAR(255) NOT NULL
);

INSERT INTO public.application_settings
(id, setting_type, setting_value)
VALUES
(nextval('app_settings_sequence'::regclass), 'MAIL_HOST', ''),
(nextval('app_settings_sequence'::regclass), 'MAIL_USERNAME', ''),
(nextval('app_settings_sequence'::regclass), 'MAIL_PASSWORD', ''),
(nextval('app_settings_sequence'::regclass), 'MAIL_SMTP_STARTTLS_ENABLE', ''),
(nextval('app_settings_sequence'::regclass), 'MAIL_SMTP_AUTH', ''),
(nextval('app_settings_sequence'::regclass), 'MAIL_TRANSPORT_PROTOCOL', ''),
(nextval('app_settings_sequence'::regclass), 'MAIL_PORT', ''),
(nextval('app_settings_sequence'::regclass), 'MAIL_DEBUG', '');
