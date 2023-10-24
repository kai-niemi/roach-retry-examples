package io.roach.retry.spring.demo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableJpaRepositories
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement // (order = Ordered.LOWEST_PRECEDENCE - 1) // Bump up one level to enable extra advisors
//private int retryAspectOrder = Ordered.LOWEST_PRECEDENCE - 4; // R4J retry advice
@SpringBootApplication
@Configuration
//@EnableRetry
public class R4JRetryApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(R4JRetryApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @Bean
    public CockroachExceptionClassifier exceptionClassifier() {
        return new CockroachExceptionClassifier();
    }
}