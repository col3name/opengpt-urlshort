FROM openjdk:11-jdk
COPY app.jar /target
CMD ["java", "-jar", "/app/app.jar"]