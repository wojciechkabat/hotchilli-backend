CREATE TABLE guest_users (
  id BIGSERIAL PRIMARY KEY,
  device_id varchar(255) NOT NULL
);

ALTER TABLE refresh_tokens ADD column guest_user_id BIGINT;

ALTER TABLE refresh_tokens ADD CONSTRAINT fk_guest_user_id FOREIGN KEY (guest_user_id) REFERENCES guest_users (id);

ALTER TABLE refresh_tokens ADD CONSTRAINT unique_guest_user_device UNIQUE (guest_user_id, device_id);

