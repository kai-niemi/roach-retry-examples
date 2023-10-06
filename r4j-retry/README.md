# Transaction Retry Example for Spring Boot using Resilience4J

This project demonstrates resilience4J retries with Spring Boot.

## Setup

Create the database:

    cockroach sql --insecure --host=localhost -e "CREATE database roach_retry"

Build the app:

    ../mvnw clean install

Run the app:

    java -jar target/roach-retry.jar

## Running

See [how to test](../HOW-TO-TEST.md) on how to run the demo.