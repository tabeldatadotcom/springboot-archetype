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

# `v2020.06.11.21.43-final`

- Optimize import
- Add SpringBoot Main Test

# `v2020.06.07.12.30-final`

- Using `maven-docker-plugin` instance of `maven-dockerfile-plugin`
- Update `.gitlab-ci-archetype.yml` for enabled docker-build then push
- Update `pom.xml` property `docker.group` -> `spring-boot/oauth`
- Deploy artifact with dockerfile to `repository.dimas-maryanto.com:8087`

# `v2020.06.05.20.33-final`

- Update `.gitlab-ci.yml` for quick solve ready to deploy
- refactor package to `com.maryanto.dimas.archetype`
- rename file `OAuthSSOApplication` to `MainApplication`
- Enabled modular package management
- Enabled archetype generate from remote repository with nexus proxy
- Update parent project to `com.maryanto.dimas.projects:commons-core` -> `2020.05.30.17.24-release`
- Optimize gitlab ci/cd for generate archetype then deploy to nexus repository
- Update `org.mapstruct:mapstruct-processor` -> `1.3.1.Final`
- Update `org.springframework.boot:spring-boot-starter-parent` -> `2.3.0.RELEASE`
- Update `spring-boot-starter-test` exclude `org.junit.vintage:junit-vintage-engine`
- Refactor deprecated properties `logging.file` to `logging.file.name`
- Enabled circuit-breaker implement `org.springframework.cloud:spring-cloud-starter-netflix-hystrix`
- Update `server.servlet.context-path=${project.artifactId}`
- Docker build on phase install
- Docker tag, push on phase deploy
- Maven deploy to nexus repository
- Fixing oauth client / resource id information
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

