CREATE TABLE IF NOT EXISTS teams (
name        VARCHAR(60)  PRIMARY KEY,
captain     VARCHAR      NOT NULL,
roster      VARCHAR
);

DELETE FROM teams;