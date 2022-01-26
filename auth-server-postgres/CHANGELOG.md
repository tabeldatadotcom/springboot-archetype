# `v2020.08.03.20.34-final`

- Update gitlab ci/cd improve for docker build in docker container
- Remove maven proxy replace using env variable inside gitlab runner

# `v2020.08.03.13.32-final`

- Add stage `publish` to `gitlab-ci.yaml` for task `docker-publish` and `maven-deploy`
- move archetype package to `com.maryanto.dimas.spring-boot.archetype`

# `v2020.08.02.19.30-final`

- Update gitlab ci/cd using docker executor
- Update `com.maryanto.dimas.projects:commons-core -> 2020.08.02.17.06-release`
- Remove maven proxy replace using env variable inside gitlab runner

# `v2020.07.21.19.58-final`

- replace property `$APP_NAME` with `$CI_PROJECT_NAME` in file `gitlab-ci.yml`

# `v2020.06.26.15.28-final`

- Add resource_client from `resource-oracle`, `resource-postgres`, `resource-mysql`, `resource-mssql`
- Component menu refactor field `resource_id` to module

# `v2020.06.11.21.46-final`

- Optimize import
- Add SpringBoot contextPath test
 

# `v2020.06.09.06.47-final`

- Add gitlab template for issue
- Enabled build dockerfile using `maven-dockerfile-plugin` via gitlab ci/cd
- Implement quality code using jacoco then reported to sonarqube

# `v2020.06.04.21.51-final`

- Update swagger-ui properties using `pom.xml` configuration injected from `application.yml`
- Update `.gitlab-ci.yml` for quick solve ready to deploy
- refactor package to `com.maryanto.dimas.archetype`
- rename file `OAuthSSOApplication` to `MainApplication`
- Enabled modular package management
- Enabled archetype generate from remote repository with nexus proxy
- Update parent project to `com.maryanto.dimas.projects:commons-core` -> `2020.05.30.17.24-release`
- Optimize gitlab ci/cd for generate archetype then deploy to nexus repository
- Update `spring-boot-starter-test` exclude `org.junit.vintage:junit-vintage-engine`
- Fixing hibernate.dialect not null for `postgresql` profile
- Refactor deprecated properties `logging.file` to `logging.file.name`
- Enabled circuit-breaker implement `org.springframework.cloud:spring-cloud-starter-netflix-hystrix`
- Add simple documentation
- Docker build on phase install
- Docker tag, push on phase deploy
- Maven deploy to nexus repository
- Secure with JWT
- Enable deployment with docker
- Enable deployment with Kubenetes and Istio
- Enabled sso for `Oauth 2.0`
    - grant type authorization code
    - grant type password
- Dynamic privileges by role
- Credential users by jdbc and encrypt by BCrypt
- Enabled resource server, url pattern => `/api/**`
- Securing Web MVC, url any pattern example `/app/**` except `/api/**`
- Fixing `authorization code` and `password` grant type from token to authentication and authorization
- Automatic testing for OAuth2 `password` grant type
- Enabled run as service / systemctl / systemd
- Enabled oauth client details with jdbc / client service
- Enabled jdbc token store
- Enabled login screen
- Enabled webjars
    - bootstrap
    - jquery
    - poper.js
    - moment
- Thymeleaf + Thymeleaf security
- Enabled oauth logout & force logout api
- Enabled history login and logout

