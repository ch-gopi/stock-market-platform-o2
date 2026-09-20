package com.market.apigateway.controller;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    // Circuit-breaker fallbacks are forwarded with the ORIGINAL HTTP method (GET, POST, PUT, DELETE, ...),
    // so these must accept all methods - otherwise a tripped breaker on a POST/PUT/DELETE call
    // (e.g. login, add-to-watchlist) surfaces a misleading 405 instead of the intended 503.

    @RequestMapping(path = "/fallback/watchlist", method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH
    })
    public ResponseEntity<String> watchlistFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Watchlist service is unavailable,please try after sometime");
    }

    @RequestMapping(path = "/fallback/quotes", method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH
    })
    public ResponseEntity<String> quotesFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Quotes service is unavailable,please try after sometime");
    }

    @RequestMapping(path = "/fallback/historical", method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH
    })
    public ResponseEntity<String> historicalFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Historical service is unavailable,please try after sometime");
    }

    @RequestMapping(path = "/fallback/user", method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH
    })
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User service is unavailable,please try after sometime");
    }

    @RequestMapping(path = "/fallback/marketsearch", method = {
            RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH
    })
    public ResponseEntity<String> marketSearchFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Marketsearch service is unavailable,please try after sometime");
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getRemoteAddress())
                .map(address -> address.getAddress().getHostAddress())
                .defaultIfEmpty("unknown");
    }

}
