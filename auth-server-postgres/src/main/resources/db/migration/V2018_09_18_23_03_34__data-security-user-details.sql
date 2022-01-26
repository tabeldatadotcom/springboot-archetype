insert into auth.users (id, username, email, password, is_active, is_keep_active, is_locked, is_sudo,
                        login_failed_counter,
                        created_by, created_date, last_update_by, last_update_date)
values ('admin', 'admin', 'admin@example.com', '$2a$11$hDHpHn.Y4Vkg42/hfs4.EeaNqd7w7Ol431vtdyjG99bTErbFYiSpm', true,
        true, false, true, 0, 'migration', now(), null, null),
       ('user', 'user', 'user@example.com', '$2a$11$hAA3UdVhJOKhRs5ZQ/Erb.gXYArGpXMD54b6XrIecVSp.K59ROaIO', true, false,
        false, false, 0, 'migration', now(), null, null),
       ('default', 'default', 'default@example.com', '$2a$11$AXYPPPkahtX6BGmYRy9Kq.UZOesq10//ZsObUvUS3a.BUiB12UB7a',
        true, false, false, false, 0, 'migration', now(), null, null);

insert into auth.roles(id, name, description)
values (0, 'ROLE_PUBLIC', 'Public role'),
       (1, 'ROLE_USER_CREATE', 'Role untuk membuat user detail'),
       (2, 'ROLE_USER_UPDATE', 'Role untuk meng-update user detail'),
       (3, 'ROLE_USER_DELETE', 'Role untuk meng-delete user detail'),
       (4, 'ROLE_USER_VIEW_ALL', 'Role untuk melihat data semua users'),
       (5, 'ROLE_USER_VIEW', 'Role untuk melihat data user detail'),
       (6, 'ROLE_APPLICATION_CREATE', 'Role untuk menambahkan application / resource id'),
       (7, 'ROLE_APPLICATION_UPDATE', 'Role untuk mengupdate application / resource id'),
       (8, 'ROLE_APPLICATION_DELETE', 'Role untuk meng-hapus application / resource id'),
       (9, 'ROLE_APPLICATION_VIEW', 'Role untuk melihat application / resource id'),
       (10, 'ROLE_APPLICATION_VIEW_ALL', 'Role untuk melihat semua application / resource id');

insert into auth.privileges (id, name, description, created_by, created_date, last_updated_by, last_updated_date)
values ('app-create', 'PRIVILAGE_CREATE_APP', null, 'migration', now(), null, null),
       ('register-user', 'PRIVILAGE_CREATE_USER', null, 'migration', now(), null, null),
       ('public', 'PRIVILAGE_PUBLIC', null, 'migration', now(), null, null);


insert into auth.authorities (id, privilege_id, role_id, created_by, created_date)
values (uuid_generate_v4(), 'public', 0, 'migration', now()),
       (uuid_generate_v4(), 'app-create', 6, 'migration', now()),
       (uuid_generate_v4(), 'register-user', 1, 'migration', now());

insert into auth.user_privileges(id, privilege_id, user_id, created_by, created_date)
values (uuid_generate_v4(), 'public', 'admin', 'migration', now()),
       (uuid_generate_v4(), 'app-create', 'admin', 'migration', now()),
       (uuid_generate_v4(), 'register-user', 'admin', 'migration', now()),
       (uuid_generate_v4(), 'public', 'user', 'migration', now()),
       (uuid_generate_v4(), 'register-user', 'user', 'migration', now()),
       (uuid_generate_v4(), 'public', 'default', 'migration', now());
