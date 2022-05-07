package com.github.linyuzai.connection.loadbalance.autoconfigure.websocket;

import lombok.NonNull;
import org.springframework.boot.web.reactive.context.ConfigurableReactiveWebEnvironment;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.WebApplicationContext;

public class WebSocketLoadBalanceImportSelector implements ImportSelector, EnvironmentAware {

    private Environment environment;

    @Override
    public String @NonNull [] selectImports(@NonNull AnnotationMetadata metadata) {
        ServerType type = environment.getProperty("concept-websocket.load-balance.server.type",
                ServerType.class, ServerType.AUTO);
        if (type == ServerType.AUTO) {
            type = deduceServerType();
        }
        boolean enableDefaultEndpoint = environment.getProperty("concept-websocket.load-balance.server.default-endpoint.enabled",
                boolean.class, true);
        return type.getConfigurations(enableDefaultEndpoint);
    }

    private ServerType deduceServerType() {
        if (isServletWebApplication()) {
            return ServerType.SERVLET;
        } else if (isReactiveWebApplication()) {
            return ServerType.REACTIVE;
        }
        throw new IllegalArgumentException("Server type can not deduce");
    }

    private boolean isServletWebApplication() {
        if (environment instanceof ConfigurableWebEnvironment) {
            return true;
        }
        if (environment instanceof WebApplicationContext) {
            return true;
        }
        return false;
    }

    private boolean isReactiveWebApplication() {
        if (environment instanceof ConfigurableReactiveWebEnvironment) {
            return true;
        }
        if (environment instanceof ReactiveWebApplicationContext) {
            return true;
        }
        return false;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}