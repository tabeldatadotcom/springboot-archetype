ARG JDK_VERSION=11-oraclelinux8
FROM openjdk:${JDK_VERSION}

LABEL maintener="Dimas Maryanto <software.dimas_m@icloud.com>"

# Created user
RUN groupadd www-data && \
adduser -r -g www-data www-data

WORKDIR /usr/local/share/applications
USER www-data

ENV APPLICATION_PORT=8080
ENV PROFILE=default
ENV DATABASE_USER=testing
ENV DATABASE_PASSWORD=testing
ENV DATABASE_HOST=localhost
ENV DATABASE_NAME=testing
ENV DATABASE_PORT=5432
ENV FLYWAY_ENABLED=true

ARG JAR_FILE="oauth2-server-oracle-2020.08.03.20.34-final.jar"
COPY --chown=www-data:www-data target/${JAR_FILE} spring-boot.jar

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "spring-boot.jar"]

CMD ["--server.port=${APPLICATION_PORT}", "--spring.profiles.active=${PROFILE}"]

EXPOSE ${APPLICATION_PORT}/tcp

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost:${APPLICATION_PORT}/actuator/health || exit 1
