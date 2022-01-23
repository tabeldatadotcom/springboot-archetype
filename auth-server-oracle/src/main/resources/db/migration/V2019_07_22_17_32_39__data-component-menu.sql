-- data menu resource-postgresql96
insert into COMPONENT_MENUS(ID, TITLE, PATH, ICON_ID, IS_MENU, CREATED_BY, module, PARENT_ID)
VALUES ('001', 'Dashboard', '/dashboard', null, 0, 'migration', 'webapp', null);
insert into COMPONENT_MENUS(ID, TITLE, PATH, ICON_ID, IS_MENU, CREATED_BY, module, PARENT_ID)
VALUES ('002', 'Data Master', '/master', null, 1, 'migration', 'webapp', null);
insert into COMPONENT_MENUS(ID, TITLE, PATH, ICON_ID, IS_MENU, CREATED_BY, module, PARENT_ID)
VALUES ('003', 'Example Tabel', '/example', null, 0, 'migration', 'webapp', '002');

