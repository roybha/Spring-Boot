
FROM eclipse-temurin:21


WORKDIR /app


COPY target/SpringWeb-1.0-SNAPSHOT.jar app.jar


EXPOSE 9000


ENTRYPOINT ["java", "-jar", "app.jar"]