create table component_menu_icons
(
    id           varchar2(64) default sys_guid()        not null primary key,
    icon         varchar2(64)                           not null unique,
    created_by   varchar2(100)                          not null,
    created_date timestamp    default current_timestamp not null
);

create table component_menus
(
    id           varchar2(64) default sys_guid()        not null primary key,
    title        varchar2(255),
    path         varchar2(255),
    icon_id      varchar2(64),
    is_menu      number(1)    default 0                 not null,
    created_by   varchar2(100)                          not null,
    created_date timestamp    default current_timestamp not null,
    module       varchar2(64)                           not null,
    parent_id    varchar2(64),
    constraint uq_menu unique (path, module, parent_id)
);

alter table component_menus
    add constraint fk_menus_url_parent_id foreign key (parent_id)
        references component_menus (id) on delete cascade;

create table component_menu_map_by_role
(
    id           varchar2(64) default sys_guid()        not null primary key,
    role_id      int                                    not null,
    menu_id      varchar2(64)                           not null,
    created_by   varchar2(100)                          not null,
    created_date timestamp    default current_timestamp not null
);

alter table component_menu_map_by_role
    add constraint fk_menu_map_role_id foreign key (role_id)
        references AUTH_ROLES (id) on delete cascade;

alter table component_menu_map_by_role
    add constraint fk_menu_map_menu_id foreign key (menu_id)
        references component_menus (id) on delete cascade;