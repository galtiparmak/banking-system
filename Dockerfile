FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/BankSystem.jar /app/BankSystem.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/BankSystem.jar"]