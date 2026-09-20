package com.market.quotesservice.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Autowired(required = false)
    private ObservationRegistry observationRegistry;

    @Autowired(required = false)
    private Tracer tracer;

    @Autowired(required = false)
    private Propagator propagator;

    // Micrometer Propagator Setter (single generic type)
    private final Propagator.Setter<HttpRequest> httpHeadersSetter =
            (carrier, key, value) -> carrier.getHeaders().add(key, value);

    @Bean
    public RestTemplate restTemplate() {
        // This RestTemplate is only ever used to call external, non-Eureka-registered
        // hosts (finnhub.io, AlphaVantage, Yahoo Finance) - it must NOT be @LoadBalanced.
        // Spring Cloud LoadBalancer treats a load-balanced RestTemplate's host as a
        // service ID to resolve via the discovery client, so calls to "finnhub.io" were
        // failing immediately with "No servers available for service: finnhub.io" /
        // "Service Instance cannot be null" before ever reaching the network.
        //
        // Without explicit timeouts the JDK HTTP client blocks indefinitely on
        // DNS/connection failures (e.g. finnhub.io being unreachable), which
        // defeats resilience4j's retry/circuit-breaker/bulkhead since the
        // calling thread never returns to let them act.
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        if (observationRegistry != null && tracer != null && propagator != null) {
            restTemplate.getInterceptors().add(tracingInterceptor());
        }
        return restTemplate;
    }

    private ClientHttpRequestInterceptor tracingInterceptor() {
        return (request, body, execution) -> {
            if (tracer.currentSpan() != null) {
                propagator.inject(tracer.currentSpan().context(), request, httpHeadersSetter);
            }
            return execution.execute(request, body);
        };
    }
}
