package org.qiyan.examples.reactor;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Stream;

public class HelloWorld {
    public static void main(String[] args) {
        Mono<String> helloMono = Mono.just("hello");
        Mono<String> stringMono = helloMono.map(a -> a + "world");
        stringMono.subscribe(System.out::println);

        Stream<String> hello = Stream.of("hello");
        Stream<String> stringStream = hello.map(a -> a + "world");
        stringStream.forEach(System.out::println);
    }
}
