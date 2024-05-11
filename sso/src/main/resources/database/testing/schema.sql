-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Drop old tables
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Policies;

-- Create tables
CREATE TABLE IF NOT EXISTS Users (
    UUID uuid DEFAULT uuid_generate_v4(),
    username VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    email VARCHAR UNIQUE DEFAULT(NULL),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    blocked BOOLEAN NOT NULL,
    failedauth INT NOT NULL DEFAULT 0,
    attributes VARCHAR,
    PRIMARY KEY (UUID)
);

CREATE TABLE IF NOT EXISTS Policies (
    UUID uuid DEFAULT uuid_generate_v4(),
    name VARCHAR NOT NULL UNIQUE,
    rules VARCHAR NOT NULL,
    PRIMARY KEY (UUID)
);