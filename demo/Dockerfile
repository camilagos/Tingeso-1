FROM openjdk:21
ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} gestionReservas_back.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/gestionReservas_back.jar"]