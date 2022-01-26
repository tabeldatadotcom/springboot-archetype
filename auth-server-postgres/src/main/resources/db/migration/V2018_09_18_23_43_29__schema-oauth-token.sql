-- create table for access token
create table oauth.access_token
(
    auth_id        character varying(255) not null PRIMARY KEY,
    token_id       character varying(255) not null,
    token          bytea                  not null,
    user_name      character varying(100),
    client_id      character varying(255),
    authentication bytea,
    refresh_token  character varying(255),
    ip_address     character varying(20)  not null default '127.0.0.1',
    login_at       timestamp              not null default now()
);

-- create table refresh_token
create table oauth.refresh_token
(
    token_id       character varying(255),
    token          bytea,
    authentication bytea
);

-- create table history oauth access token
create table oauth.history_access_token
(
    id         character varying(255) not null primary key default uuid_generate_v4(),
    access_id  character varying(255) not null,
    token      bytea                  not null,
    client_id  character varying(255),
    ip_address character varying(20)  not null             default '127.0.0.1',
    user_name  character varying(100) not null,
    login_at   timestamp              not null             default now(),
    is_logout  boolean                not null             default false,
    logout_at  timestamp,
    logout_by  character varying(100)
);

-- create table grant type
create table oauth.grant_types
(
    id          serial                not null primary key,
    name        character varying(50) not null unique,
    description text
);

insert into oauth.grant_types (id, name, description)
VALUES (1, 'authorization_code', 'OAuth provider for programming language like PHP, Java, Ruby, etc'),
       (2, 'password', 'OAuth provider for programming language like Javascript, Typescript, Mobile Android or IOS'),
       (3, 'implicit', 'OAuth provider for authentication without authorization'),
       (4, 'client_credentials', null),
       (5, 'refresh_token', null);

-- create table client scopes
create table oauth.client_scopes
(
    id               character varying(64)  not null primary key default uuid_generate_v4(),
    name             character varying(50)  not null unique,
    created_by       character varying(100) not null,
    created_date     timestamp              not null             default now(),
    last_update_by   varchar(100),
    last_update_date timestamp
);

