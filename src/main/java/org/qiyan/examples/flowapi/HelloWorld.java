package org.qiyan.examples.flowapi;

import java.util.List;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Stream;

public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
        //a->(b,c)->d
        flow();

        //a->b->c
        //hello();

        Thread.sleep(1000L);
    }

    private static void flow() {
        SubmissionPublisher<String> root = new SubmissionPublisher<>();
        MapProcessor<String, String> b = new MapProcessor<>(input -> "(from:" + "b-" + Thread.currentThread().getName() + ",dept:" + input + ")");
        MapProcessor<String, String> c = new MapProcessor<>(input -> "(from:" + "c-" + Thread.currentThread().getName() + ",dept:" + input + ")");
        MapProcessor<List<String>, String> d = new MapProcessor<>(input -> "(from:" + "d-" + Thread.currentThread().getName() + ",dept:" + input + ")");
        FinalProcessor<String> print = new FinalProcessor<>(System.out::println);

        MergeProcessor merge = new MergeProcessor(2);

        root.subscribe(b);
        root.subscribe(c);
        b.subscribe(merge);
        c.subscribe(merge);
        merge.subscribe(d);
        d.subscribe(print);

        root.submit("(from:a-" + Thread.currentThread().getName() + ")");

    }

    private static void hello() {
        Stream.of("hello")
                .map(input -> input == null ? 0 : input.length())
                .map(input -> "The length of the string is: " + input)
                .forEach(System.out::println);


        SubmissionPublisher<String> root = new SubmissionPublisher<>();
        MapProcessor<String, Integer> lenProcessor = new MapProcessor<>(input -> null == input ? 0 : input.length());
        MapProcessor<Integer, String> strProcessor = new MapProcessor<>(input -> "The length of the string is: " + input);
        FinalProcessor<String> printProcessor = new FinalProcessor<>(System.out::println);

        root.subscribe(lenProcessor);
        lenProcessor.subscribe(strProcessor);
        strProcessor.subscribe(printProcessor);


        root.submit("hello");
    }
}
