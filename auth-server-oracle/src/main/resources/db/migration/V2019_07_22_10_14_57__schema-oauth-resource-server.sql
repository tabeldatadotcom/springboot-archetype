create table resource_client_details
(
    id                      varchar2(64) default sys_guid()        not null primary key,
    name                    varchar2(50)                           not null unique,
    password                varchar2(255)                          not null,
    is_auto_approve         number(1)    default 1                 not null,
    token_expired_in_second int          default 43200             not null,
    created_by              varchar2(100)                          not null,
    created_date            timestamp    default current_timestamp not null,
    last_update_by          varchar2(100),
    last_update_date        timestamp
);

create table resource_application
(
    id               varchar2(64) default sys_guid()        not null primary key,
    name             varchar2(50)                           not null unique,
    created_by       varchar2(100)                          not null,
    created_date     timestamp    default current_timestamp not null,
    last_update_by   varchar2(100),
    last_update_date timestamp
);

create table resource_client_applications
(
    id               varchar2(64) default sys_guid()        not null primary key,
    client_detail_id varchar2(64)                           not null,
    app_id           varchar2(64)                           not null,
    created_by       varchar2(100)                          not null,
    created_date     timestamp    default current_timestamp not null,
    last_update_by   varchar2(100),
    last_update_date timestamp,
    constraint uq_client_detail_app unique (client_detail_id, app_id)
);

alter table resource_client_applications
    add constraint fk_client_apps_detail_id foreign key (client_detail_id)
        references resource_client_details (id) on delete cascade;

alter table resource_client_applications
    add constraint fk_client_apps_app_id foreign key (app_id)
        references resource_application (id) on delete cascade;

create table resource_client_redirect_uri
(
    id           varchar2(64) default sys_guid() not null primary key,
    client_id    varchar2(64)                    not null,
    redirect_uri varchar2(150)                   not null,
    constraint uq_client_uri unique (client_id, redirect_uri)
);

alter table resource_client_redirect_uri
    add constraint fk_client_uri_detail_id foreign key (client_id)
        references resource_client_details (id) on delete cascade;

create table resource_client_scopes
(
    id        varchar2(64) default sys_guid() not null,
    client_id varchar2(64)                    not null,
    scope_id  varchar2(64)                    not null
);

create table resource_client_grant_types
(
    id               varchar2(64) default sys_guid()        not null primary key,
    client_id        varchar2(64)                           not null,
    grant_type       int                                    not null,
    created_by       varchar2(100)                          not null,
    created_date     timestamp    default current_timestamp not null,
    last_update_by   varchar2(100),
    last_update_date timestamp,
    constraint uq_grant_type unique (client_id, grant_type)
);

alter table resource_client_grant_types
    add constraint fk_client_grant_detail_id foreign key (client_id)
        references resource_client_details (id) on delete cascade;

alter table resource_client_grant_types
    add constraint fk_client_grant_oauth_type_id foreign key (grant_type)
        references OAUTH_GRANT_TYPES (id) on delete cascade;
