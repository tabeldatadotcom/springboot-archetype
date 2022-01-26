-- create table master users
create table auth.users
(
    id                   character varying(64)  not null primary key default uuid_generate_v4(),
    username             character varying(100) not null unique,
    password             character varying(255) not null unique,
    email                character varying(50)  not null,
    is_active            boolean                not null             default false,
    is_keep_active       boolean                not null             default false,
    is_locked            boolean                not null             default false,
    is_sudo              boolean                not null             default false,
    login_failed_counter int                    not null             default 0,
    created_by           character varying(100) not null,
    created_date         timestamp              not null             default now(),
    last_update_by       character varying(1000),
    last_update_date     timestamp
);

-- create table master role
create table auth.roles
(
    id          serial                 not null primary key,
    name        character varying(100) not null unique,
    description text
);

-- create table master privileges
create table auth.privileges
(
    id                character varying(65)  not null primary key default uuid_generate_v4(),
    name              character varying(100) not null,
    description       text,
    created_by        character varying(100) not null,
    created_date      timestamp              not null             default now(),
    last_updated_by   character varying(100),
    last_updated_date timestamp
);

-- create table authorities
create table auth.authorities
(
    id           character varying(64)  not null primary key default uuid_generate_v4(),
    privilege_id character varying(64)  not null,
    role_id      int                    not null,
    created_by   character varying(100) not null,
    created_date timestamp              not null             default now()
);

alter table auth.authorities
    add constraint fk_authorities_privilege_id foreign key (privilege_id)
        references auth.privileges (id) on update cascade on delete cascade;

alter table auth.authorities
    add constraint fk_authorities_role_id foreign key (role_id)
        references auth.roles (id) on update cascade on delete cascade;

alter table auth.authorities
    add constraint uq_authorities_role_and_privilege unique (privilege_id, role_id);

-- create user privileges
create table auth.user_privileges
(
    id           character varying(64) primary key not null default uuid_generate_v4(),
    privilege_id character varying(64)             not null,
    user_id      character varying(64)             not null,
    created_by   character varying(64)             not null,
    created_date timestamp                         not null default now()
);

alter table auth.user_privileges
    add constraint fk_granted_privilege_id foreign key (privilege_id)
        references auth.privileges (id) on update cascade on delete cascade;

alter table auth.user_privileges
    add constraint fk_granted_user_id foreign key (user_id)
        references auth.users (id) on update cascade on delete cascade;

