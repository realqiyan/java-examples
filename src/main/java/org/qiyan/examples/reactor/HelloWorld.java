package org.qiyan.examples.reactor;

import reactor.core.publisher.Mono;

public class HelloWorld {
    public static void main(String[] args) {
        Mono.just("hello")
                .map(a -> a + "world")
                .subscribe(System.out::println);
    }
}
