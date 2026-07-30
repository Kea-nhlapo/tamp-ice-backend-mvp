FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw \
    && ./mvnw --batch-mode --no-transfer-progress dependency:go-offline

COPY src/ src/
RUN ./mvnw --batch-mode --no-transfer-progress clean package

FROM eclipse-temurin:21-jre-jammy AS runtime

RUN apt-get update \
    && apt-get install --yes --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system tamp \
    && useradd --system --gid tamp --home-dir /app --shell /usr/sbin/nologin tamp

WORKDIR /app

COPY --from=build --chown=tamp:tamp /workspace/target/tamp-backend-*.jar app.jar

USER tamp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
