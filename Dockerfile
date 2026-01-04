# this dockerfile represents staged (layered) docker build

# first stage downloads and caches dependencies
# which are then used to create an artifact
FROM eclipse-temurin:21-jdk-jammy AS builder
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /opt/app
COPY --chown=appuser:appgroup .mvn/ .mvn
COPY --chown=appuser:appgroup mvnw pom.xml ./
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN chown -R appuser:appgroup ./src
RUN ./mvnw clean install -DskipTests

# second stage is responsible for executing an app
FROM eclipse-temurin:21-jre-jammy
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
WORKDIR /opt/app
RUN chown -R appuser:appgroup /opt/app
COPY --chown=appuser:appgroup --from=builder /opt/app/target/*.jar /opt/app/app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]
