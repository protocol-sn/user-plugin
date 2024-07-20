#FROM eclipse-temurin:21 AS java
#WORKDIR /workspace/app
#COPY mvnw .
#COPY .mvn .mvn
#COPY pom.xml .
#RUN \
# - mount=type=cache,target=/root/.m2 \
# ./mvnw dependency:resolve-plugins dependency:resolve
#COPY src src
#RUN \
# - mount=type=cache,target=/root/.m2 \
# ./mvnw package
#RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM gradle:8.7.0-jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
#WORKDIR /home/gradle/src
#RUN ./gradlew clean shadowJar


FROM eclipse-temurin:21
EXPOSE 8080
#COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]