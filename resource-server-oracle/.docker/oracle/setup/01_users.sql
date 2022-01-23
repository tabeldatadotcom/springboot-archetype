alter session set "_ORACLE_SCRIPT"= true;
create user testing identified by testing account unlock;
grant create session, connect, resource to testing;
grant all privileges to testing;
ALTER USER testing PROFILE DEFAULT;
grant IMP_FULL_DATABASE to testing;