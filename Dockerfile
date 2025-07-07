FROM maven:3.9.10-eclipse-temurin-17

# required for installing playwright dependencies
RUN apt-get update && apt-get install -y sudo

WORKDIR /app

COPY . .

# Install playwright with all system dependencies
RUN mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"


CMD ["mvn", "clean", "test"]