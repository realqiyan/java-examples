package org.qiyan.examples.reactor;

import reactor.core.publisher.Mono;

import java.util.Arrays;

public class HelloWorld {
    public static void main(String[] args) {
        Mono.just("hello")
                .map(a -> a + "world")
                .subscribe(System.out::println);

        Arrays.asList("hello")
                .stream()
                .map(a -> a + "world")
                .forEach(System.out::println);
    }
}
