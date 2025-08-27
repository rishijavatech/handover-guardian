--liquibase formatted sql

--changeset vaibhav:initialschema:1
CREATE TABLE if not exists authorities (
    id BIGSERIAL PRIMARY KEY,
    role VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL
);

CREATE TABLE if not exists user_auth (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password text NOT NULL,
    role VARCHAR(50) NOT NULL,
    type varchar(50) not null,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset vaibhav:initialschema:2
alter table user_auth drop column type;
