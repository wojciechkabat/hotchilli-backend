CREATE TABLE IF NOT EXISTS refresh_tokens (
  id UUID PRIMARY KEY,
	user_id BIGINT,
	refresh_token varchar(512) NOT NULL,
	device_id varchar(255) NOT NULL
);

ALTER TABLE refresh_tokens ADD CONSTRAINT fk_user_id
  FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE refresh_tokens ADD CONSTRAINT unique_user_device UNIQUE (user_id, device_id);

CREATE TABLE IF NOT EXISTS roles (
	id BIGSERIAL PRIMARY KEY,
	value varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_roles (
	user_id BIGINT NOT NULL,
	role_id BIGINT NOT NULL
);

ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_userid
  FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE user_roles ADD CONSTRAINT fk_user_roles_roleid
  FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users ADD column 	password varchar(255);