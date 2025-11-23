DROP TABLE IF EXISTS humg_user;

CREATE TABLE humg_user
(
    user_id       VARCHAR(10) NOT NULL UNIQUE,
    user_name     VARCHAR(32),
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "humg_user_key"        PRIMARY KEY (user_id)
);

CREATE
INDEX humg_user_idx_01
  ON humg_user (user_id);