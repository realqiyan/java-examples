package org.qiyan.examples.rxjava;

import io.reactivex.Flowable;

public class HelloWorld {
    public static void main(String[] args) {
        hello();

    }

    private static void hello() {
        Flowable.fromArray("hello")
                .map(input -> input.length())
                .map(input -> "The length of the string is: " + input)
                .subscribe(input -> System.out.println(input));
    }

}
