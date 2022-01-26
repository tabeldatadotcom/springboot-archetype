CREATE OR REPLACE FUNCTION auth.authentication(uname character varying(100))
    RETURNS TABLE
            (
                username character varying(100),
                password character varying(255),
                enabled  boolean
            )
AS
$$
DECLARE
    data record;
BEGIN
    FOR data IN (select u.username as uid, u.password as passwd, u.is_active as is_enabled
                 from auth.users u
                 where u.username = uname
                   and is_locked = false)
        LOOP
            username := data.uid;
            password := data.passwd;
            enabled := data.is_enabled;
            RETURN NEXT;
        END LOOP;
END;
$$
    LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION auth.authorization(uname character varying(100))
    RETURNS TABLE
            (
                username  character varying(100),
                authority character varying(100)
            )
AS
$$
DECLARE
    data record;
    sudo boolean;
BEGIN
    select is_sudo into sudo from auth.users u where u.username = uname;
    raise notice 'is sudo for % -> % ', uname, sudo;
    if sudo is false then
        FOR data IN (select distinct role.name as role_name, u.username as user_id
                     from auth.users u
                              join auth.user_privileges granted on u.id = granted.user_id
                              join auth.privileges privilege on granted.privilege_id = privilege.id
                              join auth.authorities authority on authority.privilege_id = privilege.id
                              join auth.roles role on authority.role_id = role.id
                     where u.username = uname)
            LOOP
                username := data.user_id;
                authority := data.role_name;
                RETURN NEXT;
            END LOOP;
    else
        FOR data IN (select name as role_name from auth.roles)
            LOOP
                username := uname;
                authority := data.role_name;
                RETURN NEXT;
            END LOOP;
    end if;
END;
$$
    LANGUAGE 'plpgsql';