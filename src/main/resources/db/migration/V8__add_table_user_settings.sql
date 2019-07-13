CREATE TABLE user_settings (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  notifications_enabled boolean NOT NULL DEFAULT TRUE,
  notifications_language varchar(2) NOT NULL
);
 ALTER TABLE users ADD column settings_id bigint;
 ALTER TABLE users ADD CONSTRAINT fk_user_settings
  FOREIGN KEY (settings_id) REFERENCES user_settings (id);
