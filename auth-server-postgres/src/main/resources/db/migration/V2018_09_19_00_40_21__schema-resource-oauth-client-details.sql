-- create table client details
create table resource.client_details
(
    id                      character varying(64)  not null primary key default uuid_generate_v4(),
    name                    character varying(50)  not null unique,
    password                character varying(255) not null,
    is_auto_approve         boolean                not null             default false,
    token_expired_in_second int                    not null             default 43200,
    created_by              character varying(100) not null,
    created_date            timestamp              not null             default now(),
    last_update_by          character varying(100),
    last_update_date        timestamp
);

-- create table applications
create table resource.applications
(
    id               character varying(64)  not null primary key default uuid_generate_v4(),
    name             character varying(50)  not null unique,
    created_by       character varying(100) not null,
    created_date     timestamp              not null             default now(),
    last_update_by   character varying(100),
    last_update_date timestamp
);

-- create table client_details -> applications
create table resource.client_detail_applications
(
    id               character varying(64)  not null primary key default uuid_generate_v4(),
    client_detail_id character varying(64)  not null,
    app_id           character varying(64)  not null,
    created_by       character varying(100) not null,
    created_date     timestamp              not null             default now(),
    last_update_by   character varying(100),
    last_update_date timestamp
);

alter table resource.client_detail_applications
    add constraint fk_client_detail_app_client_id foreign key (client_detail_id)
        references resource.client_details (id) on update cascade on delete cascade;

alter table resource.client_detail_applications
    add constraint fk_client_detail_app_app_id foreign key (app_id)
        references resource.applications (id) on update cascade on delete cascade;

alter table resource.client_detail_applications
    add constraint uq_client_detail_app unique (client_detail_id, app_id);

-- create table client_details -> redirect_uri
create table resource.client_detail_redirect_uris
(
    id           character varying(64)  not null primary key default uuid_generate_v4(),
    client_id    character varying(64)  not null,
    redirect_uri character varying(150) not null
);

alter table resource.client_detail_redirect_uris
    add constraint fk_redirect_uri_client_detail_id foreign key (client_id)
        REFERENCES resource.client_details (id) on update cascade on delete cascade;

alter table resource.client_detail_redirect_uris
    add constraint uq_redirect_uri unique (client_id, redirect_uri);

-- create table client_detail -> oauth.client_scope
create table resource.client_detail_scopes
(
    id        character varying(64) not null primary key default uuid_generate_v4(),
    client_id character varying(64) not null,
    scope_id  character varying(64) not null
);

alter table resource.client_detail_scopes
    add constraint fk_client_detail_scope_client_id foreign key (client_id)
        references resource.client_details (id) on update cascade on delete cascade;

alter table resource.client_detail_scopes
    add constraint fk_client_detail_scope_id foreign key (scope_id)
        references oauth.client_scopes (id) on update cascade on delete cascade;

alter table resource.client_detail_scopes
    add constraint uq_client_detail_scopes unique (client_id, scope_id);

-- create table client_details -> oauth.grant_types
create table resource.client_detail_grant_types
(
    id               character varying(64)  not null primary key default uuid_generate_v4(),
    client_id        character varying(64)  not null,
    grant_type       int                    not null,
    created_by       character varying(100) not null,
    created_date     timestamp              not null             default now(),
    last_update_by   character varying(100),
    last_update_date timestamp
);

alter table resource.client_detail_grant_types
    add constraint fk_client_detail_grant_types_client_id foreign key (client_id)
        references resource.client_details (id) on update cascade on delete cascade;

alter table resource.client_detail_grant_types
    add constraint fk_client_detail_grant_types_id foreign key (grant_type)
        references oauth.grant_types (id) on update cascade on delete cascade;

alter table resource.client_detail_grant_types
    add constraint uq_client_detail_grant_types unique (client_id, grant_type);