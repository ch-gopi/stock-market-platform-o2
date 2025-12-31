package com.market.quotesservice.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

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
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
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
