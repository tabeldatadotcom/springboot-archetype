insert into resource.client_details(id, name, password, is_auto_approve, token_expired_in_second, created_by,
                                    created_date, last_update_by, last_update_date)
values ('resource-postgresql96', 'resource-postgresql96',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', true, 43200, 'migration', now(), null, null),
       ('oauth2-resource-oracle', 'oauth2-resource-oracle',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', true, 43200, 'migration', now(), null, null),
       ('oauth2-resource-mysql', 'oauth2-resource-mysql',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', true, 43200, 'migration', now(), null, null),
       ('oauth2-resource-mssql', 'oauth2-resource-mssql',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', true, 43200, 'migration', now(), null, null);

insert into resource.applications (id, name, created_by, created_date, last_update_by, last_update_date)
values ('example', 'example', 'migration', now(), null, null);

insert into resource.client_detail_applications(id, client_detail_id, app_id, created_by, created_date, last_update_by, last_update_date)
values
       (uuid_generate_v4(), 'resource-postgresql96', 'example', 'migration', now(), null, null),
       (uuid_generate_v4(), 'oauth2-resource-oracle', 'example', 'migration', now(), null, null),
       (uuid_generate_v4(), 'oauth2-resource-mysql', 'example', 'migration', now(), null, null),
       (uuid_generate_v4(), 'oauth2-resource-mssql', 'example', 'migration', now(), null, null);

insert into oauth.client_scopes(id, name, created_by, created_date, last_update_by, last_update_date)
values (2, 'write', 'migration', now(), null, null),
       (3, 'update', 'migration', now(), null, null),
       (4, 'delete', 'migration', now(), null, null);
