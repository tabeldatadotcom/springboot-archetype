create table example_table
(
    id           character varying(64) not null primary key,
    name         character varying(100),
    created_date date                  not null default now(),
    created_time timestamp             not null,
    is_active    boolean                        default false,
    counter      int                   not null default 0,
    currency     decimal               not null,
    description  text,
    floating     double precision
);

insert into example_table(id, name, created_date, created_time, is_active, counter, currency, description, floating)
values ('001', 'Dimas Maryanto', now(), now(), true, 0, 100000, null, 0.1),
       ('002', 'Muhamad yusuf', now(), now(), true, 0, 100000, null, 0.1),
       ('003', 'Prima', now(), now(), true, 0, 100000, null, 0.1),
       ('004', 'Gufron', now(), now(), true, 0, 100000, null, 0.1),
       ('005', 'Abdul', now(), now(), true, 0, 100000, null, 0.1);
