--liquibase formatted sql

--changeset vaibhav:initialschema:1
CREATE TABLE if not exists authorities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE if not exists user_auth (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password text NOT NULL,
    role_id BIGINT REFERENCES user_roles(id),
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE if not exists role_authorities (
    role_id BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES user_roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_authority FOREIGN KEY (authority_id) REFERENCES authorities (id) ON DELETE CASCADE
);


--changeset vaibhav:initialschema:2
CREATE TABLE if not exists user_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

alter table user_auth add column type_id BIGINT REFERENCES user_types(id);

--changeset vaibhav:initailschema:3
alter table user_auth
    add column modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    add column created_by varchar(255),
    add column modified_by varchar(255);

--changeset vaibhav:initialschema:4
ALTER TABLE user_auth
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true,
ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT false;




