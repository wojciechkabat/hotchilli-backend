CREATE TABLE IF NOT EXISTS PINS (
  id BIGSERIAL PRIMARY KEY,
	value varchar(6) NOT NULL,
	type varchar(20) NOT NULL,
	user_id BIGINT NOT NULL
);
ALTER TABLE PINS ADD CONSTRAINT fk_user_id
  FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE PINS ADD CONSTRAINT unique_pin_for_user UNIQUE (type, user_id);
