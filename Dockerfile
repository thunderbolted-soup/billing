# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY billing-core/pom.xml billing-core/pom.xml
COPY billing-api/pom.xml billing-api/pom.xml
COPY billing-worker/pom.xml billing-worker/pom.xml

RUN ./mvnw dependency:go-offline -B

COPY billing-core/src billing-core/src
COPY billing-api/src billing-api/src
COPY billing-worker/src billing-worker/src

ARG MODULE_NAME
RUN ./mvnw package -DskipTests -pl ${MODULE_NAME} -am && \
    java -Djarmode=layertools -jar ${MODULE_NAME}/target/*.jar extract

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
