FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/removeBG-0.0.1-SNAPSHOT.jar removebg.jar
EXPOSE 9091
ENTRYPOINT ["java","-jar","removebg.jar"]