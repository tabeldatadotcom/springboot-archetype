create table component.menu_icons
(
    id           character varying(64)  not null primary key,
    icon         character varying(64)  not null unique,
    created_by   character varying(100) not null,
    created_date timestamp              not null default now()
);

create table component.menu
(
    id           character varying(64)  not null primary key,
    title        character varying(255),
    path         character varying(255),
    icon_id      character varying(64),
    is_menu      boolean                not null default false,
    created_by   character varying(100) not null,
    created_date timestamp              not null default now(),
    module       character varying(100) not null,
    parent_id    character varying(64)
);

alter table component.menu
    add constraint fk_url_parent_id foreign key (parent_id)
        references component.menu (id) on update cascade on delete cascade;

alter table component.menu
    add constraint fk_url_icon_id foreign key (icon_id)
        references component.menu_icons (id) on update cascade on delete cascade;

alter table component.menu
    add constraint uq_menu unique (path, module, parent_id);

create table component.menu_mapping_by_role
(
    id           character varying(64)  not null primary key,
    role_id      int                    not null,
    menu_id      character varying(64)  not null,
    created_by   character varying(100) not null,
    created_date timestamp              not null default now()
);

alter table component.menu_mapping_by_role
    add constraint fk_menu_mapping_role_id foreign key (role_id)
        references auth.roles (id) on update cascade on delete cascade;

alter table component.menu_mapping_by_role
    add constraint kf_menu_mapping_menu_id foreign key (menu_id)
        references component.menu (id) on update cascade on delete cascade;
