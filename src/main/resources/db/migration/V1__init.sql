CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username varchar(100) NOT NULL,
  age integer NOT NULL
);

CREATE TABLE pictures (
  id BIGSERIAL PRIMARY KEY,
  external_id varchar(100),
  url varchar(500) NOT NULL,
  user_id BIGSERIAL
);

CREATE TABLE votes (
  id BIGSERIAL PRIMARY KEY,
  voting_user_id BIGSERIAL NOT NULL,
  rated_user_id BIGSERIAL NOT NULL,
  value float8 NOT NULL,
  created_at timestamp NOT NULL
);
ALTER TABLE pictures ADD CONSTRAINT fk_picture_user_id
  FOREIGN KEY (user_id) REFERENCES users (id);