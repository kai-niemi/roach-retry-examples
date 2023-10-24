# Transaction Retry Examples

This project provides examples of different transaction retry strategies for 
Spring Boot and the JavaEE stack.

## Modules

All sample projects use the same simple schema and entity model.

- [bmt-retry](bmt-retry) - JavaEE retry interceptor for bean managed transactions 
- [cmt-retry](cmt-retry) - JavaEE retry interceptor for container managed transactions 
- [roach-retry](roach-retry) - Spring Boot / Spring Data example using 
- [spring-data-cockroachdb](https://github.com/kai-niemi/spring-data-cockroachdb) with aspectj and meta-annotations
- [r4j-retry](r4j-retry) - Spring Boot example using [resilience4J](https://resilience4j.readme.io/docs)

## Building

### Prerequisites

- JDK17 (OpenJDK compatible)
- Maven 3+ (optional, embedded)

Install the JDK (Linux):

```bash
sudo apt-get -qq install -y openjdk-17-jdk
```

### Clone and build the project

Clone and build the example projects.

```bash
git clone git@github.com/kai-niemi/roach-retry-examples.git
cd roach-retry-examples
chmod +x mvnw
./mvnw clean install
```

### Maven Configuration

To build the `roach-retry` subproject, you need to authenticate to GitHub Packages by creating a personal 
access token (classic) that includes the `read:packages` scope. For more information, 
see [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages).

Add your personal access token to the servers section in your Maven [settings.xml](https://maven.apache.org/settings.html).
Note that the server and repository id's must match but doesnt need to have id `github`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">
    ...
    <servers>
        <server>
            <id>github</id>
            <username>your-github-name</username>
            <password>your-access-token</password>
        </server>
    </servers>
    ...
</settings>
```

The are two locations where this file may be placed:

- A user's home dir: `$user.dir/.m2/settings.xml` (default)
- The Maven install dir: `$M2_HOME/conf/settings.xml`

To include the roach-retry project, activate the all maven profile.

```bash
./mvnw -P all clean install
```

## Testing

See [how to test](HOW-TO-TEST.md) on how to run the different examples.

## Terms of Use

See [MIT](LICENSE.txt) for terms and conditions.
