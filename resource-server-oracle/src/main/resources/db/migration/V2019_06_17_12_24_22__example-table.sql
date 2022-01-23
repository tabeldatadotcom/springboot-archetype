create table example_table
(
    id           varchar2(64) default SYS_GUID()        not null primary key,
    name         varchar2(100),
    created_date date         default current_date,
    created_time timestamp    default current_timestamp not null,
    is_active    number(1)    default 0,
    counter      integer      default 0                 not null,
    currency     decimal                                not null,
    description  varchar2(2400),
    floating     double precision
);

insert into example_table(id, name, created_date, created_time, is_active, counter, currency, description, floating)
values ('001', 'Dimas Maryanto', current_date, current_timestamp, 1, 0, 100000, null, 0.1);
insert into example_table(id, name, created_date, created_time, is_active, counter, currency, description, floating)
values ('002', 'Muhamad yusuf', current_date, current_timestamp, 1, 0, 100000, null, 0.1);
insert into example_table(id, name, created_date, created_time, is_active, counter, currency, description, floating)
values ('003', 'Prima', current_date, current_timestamp, 1, 0, 100000, null, 0.1);
insert into example_table(id, name, created_date, created_time, is_active, counter, currency, description, floating)
values ('004', 'Gufron', current_date, current_timestamp, 1, 0, 100000, null, 0.1);
insert into example_table(id, name, created_date, created_time, is_active, counter, currency, description, floating)
values ('005', 'Abdul', current_date, current_timestamp, 1, 0, 100000, null, 0.1);
