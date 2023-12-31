package org.qiyan.examples.reactiveweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class WebConfiguration {

    @Bean
    public RouterFunction<ServerResponse> home(ExampleWebHandler handler) {
        return RouterFunctions.route(GET("/").and(accept(MediaType.APPLICATION_JSON)), handler::hello);
    }

    @Bean
    public RouterFunction<ServerResponse> sleep(ExampleWebHandler handler) {
        return RouterFunctions.route(GET("/sleep").and(accept(MediaType.APPLICATION_JSON)), handler::sleep);
    }

}
