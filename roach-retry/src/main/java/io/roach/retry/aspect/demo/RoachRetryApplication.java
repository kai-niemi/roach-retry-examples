package io.roach.retry.aspect.demo;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.cockroachdb.aspect.AdvisorOrder;
import org.springframework.data.cockroachdb.aspect.TransactionRetryAspect;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableJpaRepositories
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement(order = AdvisorOrder.TRANSACTION_ADVISOR)
@SpringBootApplication
@Configuration
public class RoachRetryApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RoachRetryApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @Bean
    public TransactionRetryAspect retryAspect() {
        return new TransactionRetryAspect();
    }
}