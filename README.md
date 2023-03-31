# Transaction Retry Examples

This project provides examples of different transaction retry strategies for 
Spring Boot and the JavaEE stack.

## Modules

All sample projects use the same simple schema and entity model.

- [roach-retry](roach-retry) - Spring Boot example using roach-retry with aspectj and meta-annotations
- [spring-retry](cmt-retry) - Spring Boot example using spring-retry
- [bmt-retry](bmt-retry) - JavaEE retry interceptor for bean managed transactions 
- [cmt-retry](cmt-retry) - JavaEE retry interceptor for container managed transactions 

## Building

### Prerequisites

- JDK17 (OpenJDK compatible)
- Maven 3+ (optional, embedded)

Install the JDK (Linux):

```bash
sudo apt-get -qq install -y openjdk-17-jdk
```

### Clone and build the project

First clone the roach-retry project and build it locally. 

```bash
git clone git@github.com/kai-niemi/roach-retry.git
cd roach-retry
chmod +x mvnw
./mvnw clean install
```
              
Then clone and build the example projects.

```bash
git clone git@github.com/kai-niemi/roach-retry-examples.git
cd roach-retry-examples
chmod +x mvnw
./mvnw clean install
```

## Testing

See [how to test](HOW-TO-TEST.md) on how to run the different examples.

## Terms of Use

See [MIT](LICENSE.txt) for terms and conditions.
