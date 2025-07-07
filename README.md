# QA Assignment

This repository contains automated tests for the Whalebone QA assignment.


## Requirements
### For containerized deployment (recommended):
- Docker
### For local deployment:
- Java 17+
- Maven 3.9+

## Running Tests

### In a container (recommended)
- **Build the image**  
  ```sh
  docker build -t qa-assignment .
  ```

- **Run the container**  
  ```sh
  docker run -it qa-assignment
  ```
### In local environment  
- **Install Playwright browser with dependencies (only supported on Debian-based Linux)**  
  ```sh
  mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"
  ```

- **Run the tests**  
  ```sh
  mvn clean test
  ```

## Test Overview

- **API Tests:**  
  Located in [`io.whalebone.teams.ApiTests`](src/test/java/io/whalebone/teams/ApiTests.java), these tests validate the `/api/teams` endpoint, including:
  - Verifying the team count
  - Verifying the oldest team
  - Testing for cities with multiple teams
  - Verifying Metropolitan division teams
  - Verifying the nationalities of players in roster page

- **UI Tests:**  
  Located in [`io.whalebone.ui`](src/test/java/io/whalebone/ui/), these tests UI features of [uitestingplayground.com](http://uitestingplayground.com/):
  - Login form tests ([`SampleAppTests`](src/test/java/io/whalebone/ui/SampleAppTests.java))
  - Delay test ([`DelayTest`](src/test/java/io/whalebone/ui/DelayTests.java))
  - Progress bar test ([`ProgressBarTest`](src/test/java/io/whalebone/ui/ProgressBarTest.java))
 
