# Transaction Retry Example for JavaEE and BMTs
                                   
This project demonstrates an AOP-driven retry strategy for JavaEE 
apps using the following stack:

- Stateless session beans with bean managed transactions (BMT)
- `@AroundAdvice` interceptor for retries
- `@TransactionBoundary` meta annotation with interceptor binding
- JAX-RS REST endpoint for testing
- TomEE 8 as embedded container with web profile
- JPA and Hibernate for data access

## Setup

Create the database:

    cockroach sql --insecure --host=localhost -e "CREATE database orders"

Create the schema:

    cockroach sql --insecure --host=locahlost --database orders  < src/resources/conf/V1_1__create.sql

Start the app:
             
    ../mvnw clean install tomee:run
    
The default listen port is `8090` (can be changed in pom.xml):

## Running

See [how to test](../HOW-TO-TEST.md) on how to run the demo.