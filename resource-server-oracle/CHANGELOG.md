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

# `v2020.07.18.11.38-final`

- add select all to datatables dao on param length is less then 0

# `v2020.06.06.13.41-final`

- Fixing swagger-ui required context-path
- Move package to `com.maryanto.dimas.oauth-resource.archetype`
- Update `.gitlab-ci.yml` for default env service installed
- Move package to `com.maryanto.dimas.archetype`
- Rename `ResourceServerApplication` to `MainApplication`
- Move log file generated to `logs` folder
- Enabled modular package management
- Enabled archetype generate from remote repository with nexus proxy
- Move parent project to `com.maryanto.dimas.projects:commons-core` -> `2020.05.31.11.35-release`
- Optimize gitlab ci/cd for generate archetype then deploy to nexus repository
- Move folder `kubernates` to `src/main` folder
- Move file `docker-compose.yaml` to `src/main/docker` folder
- Update dependency `spring-boot-starter-parent` -> `2.2.4.RELEASE`
- Enabled circuit-breaker implement `org.springframework.cloud:spring-cloud-starter-netflix-hystrix`
- update `server.servlet.context-path=${project.artifactId}`
- docker build to maven phase install
- docker tags, push to maven phase deploy
- publish deploy to nexus repository
- Enable resource servers module validate JWT token it's self
- Fixing oauth client / resource id information

