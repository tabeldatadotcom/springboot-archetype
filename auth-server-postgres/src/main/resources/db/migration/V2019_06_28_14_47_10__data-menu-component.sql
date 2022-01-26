insert into component.menu (id, title, path, icon_id, is_menu, created_by, created_date, module, parent_id)
values ('001', 'Dashboard', '/dashboard', null, false, 'migration', now(), 'webapp', null),
       ('002', 'Data Master', '/master', null, true, 'migration', now(), 'webapp', null),
       ('003', 'Data Example', '/example', null, false, 'migration', now(), 'webapp', '002');
