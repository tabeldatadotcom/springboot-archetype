-- generate random unique string
drop extension if exists "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- create schema public,oauth,auth,resource
create schema if not exists auth;
create schema if not exists oauth;
create schema if not exists resource;
create schema if not exists component;
