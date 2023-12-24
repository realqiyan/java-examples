package org.qiyan.examples.reactor;

import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class HelloWorld {
    public static void main(String[] args) {
        //a->(b,c)->d
        reactorFlow();

        //a->b->c
        //hello();

    }

    private static void reactorFlow() {
        Mono<String> root = Mono.just("(from:a-" + Thread.currentThread().getName() + ")");
        Mono<String> b = root.flatMap(input -> Mono.fromCallable(() -> "(from:" + "b-" + Thread.currentThread().getName() + ",dept:" + input + ")"));
        Mono<String> c = root.flatMap(input -> Mono.fromCallable(() -> "(from:" + "c-" + Thread.currentThread().getName() + ",dept:" + input + ")"));
        Mono<List<String>> merge = b.zipWith(c, (r1, r2) -> Arrays.asList(r1, r2));
        Mono<String> d = merge.flatMap(input -> Mono.fromCallable(() -> "(from:" + "d-" + Thread.currentThread().getName() + ",dept:" + input + ")"));
        d.subscribe(System.out::println);
    }

    private static void hello() {
        Stream.of("hello")
                .map(input -> input == null ? 0 : input.length())
                .map(input -> "The length of the string is: " + input)
                .forEach(System.out::println);

        Stream<String> hello = Stream.of("hello");
        Stream<Integer> lengthStream = hello.map(input -> input == null ? 0 : input.length());
        Stream<String> stringStream = lengthStream.map(input -> "The length of the string is: " + input);
        stringStream.forEach(System.out::println);


        Mono.just("hello")
                .map(input -> input == null ? 0 : input.length())
                .map(input -> "The length of the string is: " + input)
                .subscribe(System.out::println);


        Mono<String> root = Mono.just("hello");
        Mono<Integer> lenMono = root.flatMap(input -> Mono.just(input == null ? 0 : input.length()));
        Mono<String> strMono = lenMono.flatMap(input -> Mono.just("The length of the string is: " + input));
        strMono.subscribe(System.out::println);
    }
}
