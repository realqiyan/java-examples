package org.qiyan.examples.rxjava;

import io.reactivex.Flowable;

public class HelloWorld {
    public static void main(String[] args) {
        Flowable<String> root = Flowable.fromArray("hello");

    }
}
