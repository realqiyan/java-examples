package org.qiyan.examples.reactor;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Stream;

public class HelloWorld {
    public static void main(String[] args) {
        Mono<String> helloMono = Mono.just("hello");
        Mono<Integer> lengthMono = helloMono.map(input -> input == null ? 0 : input.length());
        Mono<String> stringMono = lengthMono.map(input -> "The length of the string is: " + input);
        stringMono.subscribe(System.out::println);

        Stream<String> hello = Stream.of("hello");
        Stream<Integer> lengthStream = hello.map(input -> input == null ? 0 : input.length());
        Stream<String> stringStream = lengthStream.map(input -> "The length of the string is: " + input);
        stringStream.forEach(System.out::println);
    }
}
