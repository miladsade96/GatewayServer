package tech.miladsadeghi.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/microservices/accounts/**")
                        .filters(f -> f
                                .rewritePath("/microservices/accounts/(?<segment>.*)", "/${segment}")
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, body) -> {
                                            exchange.getResponse().getHeaders()
                                                    .add("X-Response-Time", LocalDateTime.now().toString());
                                            return Mono.just(body);
                                        })
                                .circuitBreaker(config -> config.setName("accountsCircuitBreaker"))
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(p -> p
                        .path("/microservices/cards/**")
                        .filters(f -> f
                                .rewritePath("/microservices/cards/(?<segment>.*)", "/${segment}")
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, body) -> {
                                            exchange.getResponse().getHeaders()
                                                    .add("X-Response-Time", LocalDateTime.now().toString());
                                            return Mono.just(body);
                                        })
                        )
                        .uri("lb://CARDS")
                )
                .route(p -> p
                        .path("/microservices/loans/**")
                        .filters(f -> f
                                .rewritePath("/microservices/loans/(?<segment>.*)", "/${segment}")
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, body) -> {
                                            exchange.getResponse().getHeaders()
                                                    .add("X-Response-Time", LocalDateTime.now().toString());
                                            return Mono.just(body);
                                        })
                        )
                        .uri("lb://LOANS")
                )
                .build();
    }

}
