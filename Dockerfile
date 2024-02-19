FROM eclipse-temurin:17-jdk-alpine as builder

RUN mkdir /app
WORKDIR /app
COPY . ./
RUN ./gradlew shadowJar --no-daemon && sh -c "cp build/libs/*.jar ./roulette.jar"

FROM eclipse-temurin:17-jdk-alpine

USER 0
RUN addgroup -g 2000 roulette && adduser -u 2000 -G roulette -s /bin/sh -D roulette && mkdir /app && chown 2000:2000 /app
USER 2000
WORKDIR /app
COPY --from=builder /app/roulette.jar ./
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "roulette.jar"]
