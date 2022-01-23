create table oauth_access_token
(
    auth_id        varchar2(255)                          not null primary key,
    token_id       varchar2(255)                          not null,
    token          blob                                   not null,
    username       varchar2(100),
    client_id      varchar2(255),
    authentication blob,
    refresh_token  varchar2(255),
    ip_address     varchar2(20) default '127.0.0.1'       not null,
    login_at       timestamp    default current_timestamp not null
);

create table oauth_refresh_token
(
    token_id       varchar2(255),
    token          blob,
    authentication blob
);

create table oauth_history_access_token
(
    id         varchar2(255) default sys_guid()        not null primary key,
    access_id  varchar2(255)                           not null,
    token      blob                                    not null,
    client_id  varchar2(255),
    ip_address varchar2(20)                            not null,
    user_name  varchar2(100)                           not null,
    login_at   timestamp     default current_timestamp not null,
    is_logout  number(1)     default 0                 not null,
    logout_at  timestamp,
    logout_by  varchar2(100)
);

create table oauth_grant_types
(
    id          integer      not null primary key,
    name        varchar2(50) not null unique,
    description varchar2(2000)
);

insert into oauth_grant_types(id, name)
values (1, 'authorization_code');
insert into oauth_grant_types(id, name)
values (2, 'password');
insert into oauth_grant_types(id, name)
values (3, 'implicit');
insert into oauth_grant_types(id, name)
values (4, 'client_credentials');
insert into oauth_grant_types(id, name)
values (5, 'refresh_token');

create table oauth_client_scope
(
    id               varchar2(64) default sys_guid()        not null primary key,
    name             varchar2(50)                           not null unique,
    created_by       varchar2(100)                          not null,
    created_date     timestamp    default current_timestamp not null,
    last_update_by   varchar2(100),
    last_update_date timestamp
);

insert into oauth_client_scope(name, created_by)
values ('read', 'migration');
insert into oauth_client_scope(name, created_by)
values ('write', 'migration');
insert into oauth_client_scope(name, created_by)
values ('drop', 'migration');

