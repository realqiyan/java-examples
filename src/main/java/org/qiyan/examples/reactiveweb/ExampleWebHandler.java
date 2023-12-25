package org.qiyan.examples.reactiveweb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ExampleWebHandler {
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("Hello World"));
    }

    public Mono<ServerResponse> sleep(ServerRequest request) {
        String value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try {
            Optional<String> timeoutOptional = request.queryParam("timeout");
            int timeout = timeoutOptional.isPresent() ? Integer.parseInt(timeoutOptional.get()) : 500;
            TimeUnit.MILLISECONDS.sleep(timeout);
            value += ",cost:" + timeout;
        } catch (Exception e) {
            log.error("sleep error,message:" + e.getMessage(), e);
        }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(value));
    }
}
