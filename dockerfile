FROM eclipse-temurin:17-jdk-alpine
COPY main-app/target/report.jar /report.jar
ENTRYPOINT ["java","-jar","/report.jar"]
EXPOSE 8080