## Springboot archetype

This project related to Backend Technologies using Springboot v2.x.x for building Rest API. Backend stack:

- Springboot
- Spring JDBC
- Spring JPA
- Spring Web MVC
- Thymeleaf
- OAuth 2.0 (Authentication & Authorization)
- Docker
- Kubernetes

## Database support

- Oracle Database `12.2`  or later
- MySQL `8.0` or later
- MariaDB `10.x` or later
- PostgreSQL `12.6` or later

## Types

Project type dif by 2 modules, Authorization Serve & Resource Server

| no   | Database           | Authorization Server                   | Resource Server                                  |
| :--- | :---               | :---                                   | :---                                             |
| 1    | Oracle Database    |[auth-server](./auth-server-oracle)     | [resource-server](./resource-server-oracle)      |
| 2    | PostgreSQL Database|[auth-server](./auth-server-postgres)   | [resource-server](./resource-server-postgres)    |
