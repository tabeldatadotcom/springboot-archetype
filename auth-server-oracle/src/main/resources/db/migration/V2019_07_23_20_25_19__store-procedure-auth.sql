create or replace type auth_authentication_object as object
(
    username varchar2(100),
    password varchar2(255),
    enabled  number(1)
);
/

create or replace type auth_authenction_list is table of auth_authentication_object;
/

-- select * from table(auth_authentication(:param));
create or replace function auth_authentication(uname in auth_users.username%TYPE)
    return auth_authenction_list
    is
    return_value auth_authenction_list;
begin
    select auth_authentication_object(u.username, u.password, u.is_active)
        bulk collect
    into return_value
    from auth_users u
    where u.username = uname
      and u.is_active = 1
      and u.is_locked = 0;

    return return_value;
end;
/

create or replace type auth_authorization_object as object
(
    username  varchar2(100),
    authority varchar2(100)
);
/

create or replace type auth_authorization_list is table of auth_authorization_object;
/

create or replace function auth_authorization(uname in auth_users.username%TYPE)
    return auth_authorization_list
    is
    return_value auth_authorization_list;
    var_is_sudo  number(1) := 0;
begin
    select u.is_sudo into var_is_sudo from auth_users u where u.username = uname;

    DBMS_OUTPUT.PUT_LINE('sudo : ' || var_is_sudo);
    if var_is_sudo = 0 then
        DBMS_OUTPUT.PUT_LINE('condition = 1');
        select auth_authorization_object(u.USERNAME, role.NAME)
            bulk collect
        into return_value
        from AUTH_USERS u
                 join AUTH_USER_PRIVILEGES granted on u.id = granted.USER_ID
                 join AUTH_PRIVILEGES privilege on granted.PRIVILEGE_ID = privilege.ID
                 join AUTH_AUTHORITIES authority on privilege.ID = authority.PRIVILEGE_ID
                 join AUTH_ROLES role on authority.ROLE_ID = role.ID
        where u.USERNAME = uname;
    else
        DBMS_OUTPUT.PUT_LINE('else condition');
        select auth_authorization_object(uname, role.name) bulk collect
        into return_value
        from AUTH_ROLES role;
    end if;
    return return_value;
end;
/
