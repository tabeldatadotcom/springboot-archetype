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


# `v2020.07.18.11.30-final`

- Add missing limit and offset on datatables dao

# `v2020.06.11.22.03-final`

- Optimize code
- Add unit test

# `v2020.06.08.20.24-final`

- Add quality code with jacoco plugin and sonarqube

# `v2020.06.06.13.41-final`

- Fixing swagger-ui required context-path
- Move package to `com.maryanto.dimas.oauth-resource.archetype`

# `v2020.06.04.17.09-final`

- Update `.gitlab-ci.yml` for default env service installed
- Move package to `com.maryanto.dimas.archetype`
- Rename `ResourceServerApplication` to `MainApplication`

# `v2020.06.04.09.48-final`

- Update `.gitlab-ci.yml` for default env service installed

# `v2020.05.31.19.46-final`

- Move package archetype to `com.maryanto.dimas.spring-boot.archetype`

# `v2020.05.31.14.33-final`

- Move package archetype to `com.maryanto.dimas.spring.archetype`

# `v2020.05.31.13.27-final` 

- Move log file generated to `logs` folder
- Update docker-compose version of `postgresql:12.3`
- Enabled modular package management
- Enabled archetype generate from remote repository with nexus proxy
- Move parent project to `com.maryanto.dimas.projects:commons-core` -> `2020.05.31.11.35-release`
- Optimize gitlab ci/cd for generate archetype then deploy to nexus repository
- Move folder `kubernates` to `src/main` folder
- Move file `docker-compose.yaml` to `src/main/docker` folder

# `v1.0.5-release`

- Update dependency `spring-boot-starter-parent` -> `2.2.4.RELEASE`
- Update dependency `com.maryanto.dimas.plugins:web-commons` -> `2.0.3-release`
- Update dependency management `spring-cloud-dependencies` -> `Hoxton.SR1`
- Enabled circuit-breaker implement `org.springframework.cloud:spring-cloud-starter-netflix-hystrix`
- Remove dependency `org.apache.commons:commons-text`

# `v1.0.4-release`

- update `spring-boot-starter-parent:2.2.0.RELEASE`
- remove dependency `com.auth0:java-jwt`
- remove dependency `org.webjars:webjars-locator`
- remove dependency `org.webjars:jquery`
- remove dependency `org.webjars:bootstrap`
- remove dependency `org.webjars:font-awesome`
- remove dependency `org.webjars:popper.js`
- remove dependency `org.webjars.bower:moment`

# `v1.0.3-release`

- update `server.servlet.context-path=${project.artifactId}`
- docker build to maven phase install
- docker tags, push to maven phase deploy
- publish deploy to nexus repository

# `v1.0.2-release`

- Enable resource servers module validate JWT token it's self
- Fixing oauth client / resource id information

