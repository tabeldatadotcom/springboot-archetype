create table AUTH_USERS
(
    id                   varchar2(64)                        not null primary key,
    username             varchar2(100)                       not null unique,
    password             varchar2(255)                       not null,
    email                varchar2(50),
    is_active            number(1) default 0                 not null,
    is_keep_active       number(1) default 0                 not null,
    is_locked            number(1) default 0                 not null,
    is_sudo              number(1) default 0                 not null,
    login_failed_counter integer   default 0,
    created_by           varchar2(100)                       not null,
    created_date         timestamp default current_timestamp not null,
    last_update_by       varchar2(100),
    last_update_date     timestamp
);

create sequence auth_roles_seq
    START WITH 1 increment by 1;

create table AUTH_ROLES
(
    id          integer       not null primary key,
    name        varchar2(100) not null unique,
    description varchar2(2000)
);

create table AUTH_PRIVILEGES
(
    id               varchar2(64)                        not null primary key,
    name             varchar2(100)                       not null,
    description      varchar2(2000),
    created_by       varchar2(100)                       not null,
    created_date     timestamp default current_timestamp not null,
    last_update_by   varchar2(100),
    last_update_date timestamp
);


create table AUTH_AUTHORITIES
(
    id           varchar2(64)                        not null primary key,
    privilege_id varchar2(64)                        not null,
    role_id      integer                             not null,
    created_by   varchar2(100)                       not null,
    created_date timestamp default current_timestamp not null,
    constraint uq_role_and_privilege unique (privilege_id, role_id)
);

alter table AUTH_AUTHORITIES
    add constraint fk_authorities_privilege_id foreign key (privilege_id)
        references AUTH_PRIVILEGES (id) on delete cascade;

alter table AUTH_AUTHORITIES
    add constraint fk_authorities_role_id foreign key (role_id)
        references AUTH_ROLES (id) on delete cascade;

create table AUTH_USER_PRIVILEGES
(
    id           varchar2(64)                        not null primary key,
    privilege_id varchar2(64)                        not null,
    user_id      varchar2(64)                        not null,
    created_by   varchar2(100)                       not null,
    created_date timestamp default current_timestamp not null
);

alter table AUTH_USER_PRIVILEGES
    add constraint fk_granted_privilege_id foreign key (privilege_id)
        references AUTH_PRIVILEGES (id) on delete cascade;

alter table AUTH_USER_PRIVILEGES
    add constraint fk_granted_user_id foreign key (user_id)
        references AUTH_USERS (id) on delete cascade;
