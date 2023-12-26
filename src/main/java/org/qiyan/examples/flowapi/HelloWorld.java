package org.qiyan.examples.flowapi;

import java.util.List;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Stream;

public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
        //a->b->c
        hello();
        Thread.sleep(1000L);
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
