-- insert client details
insert into RESOURCE_CLIENT_DETAILS (ID, NAME, PASSWORD, IS_AUTO_APPROVE, CREATED_BY, CREATED_DATE)
values ('resource-postgresql96', 'resource-postgresql96',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', 1,
        'migration', current_timestamp);
insert into RESOURCE_CLIENT_DETAILS (ID, NAME, PASSWORD, IS_AUTO_APPROVE, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-oracle', 'oauth2-resource-oracle',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', 1,
        'migration', current_timestamp);
insert into RESOURCE_CLIENT_DETAILS (ID, NAME, PASSWORD, IS_AUTO_APPROVE, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-mysql', 'oauth2-resource-mysql',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', 1,
        'migration', current_timestamp);
insert into RESOURCE_CLIENT_DETAILS (ID, NAME, PASSWORD, IS_AUTO_APPROVE, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-postgres', 'oauth2-resource-postgres',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', 1,
        'migration', current_timestamp);
insert into RESOURCE_CLIENT_DETAILS (ID, NAME, PASSWORD, IS_AUTO_APPROVE, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-mssql', 'oauth2-resource-mssql',
        '$2a$11$52ykJMuT3eooYzfYkZYIF.kZaStN4AGBCMI43aFoBC/Br5QcqHp3G', 1,
        'migration', current_timestamp);

-- insert applications
insert into RESOURCE_APPLICATION(ID, NAME, CREATED_BY, CREATED_DATE)
values ('example', 'Example Project', 'migration', current_timestamp);

-- insert client detail application
insert into RESOURCE_CLIENT_APPLICATIONS(CLIENT_DETAIL_ID, APP_ID, CREATED_BY, CREATED_DATE)
values ('resource-postgresql96', 'example', 'migration', current_timestamp);
insert into RESOURCE_CLIENT_APPLICATIONS(CLIENT_DETAIL_ID, APP_ID, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-oracle', 'example', 'migration', current_timestamp);
insert into RESOURCE_CLIENT_APPLICATIONS(CLIENT_DETAIL_ID, APP_ID, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-mysql', 'example', 'migration', current_timestamp);
insert into RESOURCE_CLIENT_APPLICATIONS(CLIENT_DETAIL_ID, APP_ID, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-postgres', 'example', 'migration', current_timestamp);
insert into RESOURCE_CLIENT_APPLICATIONS(CLIENT_DETAIL_ID, APP_ID, CREATED_BY, CREATED_DATE)
values ('oauth2-resource-mssql', 'example', 'migration', current_timestamp);
