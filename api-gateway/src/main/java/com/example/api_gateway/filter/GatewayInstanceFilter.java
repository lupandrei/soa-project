package com.example.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayInstanceFilter implements GlobalFilter {

    @Value("${GATEWAY_ID:unknown}")
    private String gatewayId;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Add response header
        exchange.getResponse()
                .getHeaders()
                .add("X-Gateway-Instance", gatewayId);

        return chain.filter(exchange);
    }
}
