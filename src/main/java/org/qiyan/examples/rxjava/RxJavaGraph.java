package org.qiyan.examples.rxjava;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RxJavaGraph {

    public static void rxjavaFlow() {
        // 创建根节点Observable
        Observable<String> root = Observable.fromCallable(() -> {
            // 模拟耗时操作
            TimeUnit.MILLISECONDS.sleep(100);
            return "(from:a-" + Thread.currentThread().getName() + ")";
        }).subscribeOn(Schedulers.computation()); // 使用computation调度器执行

        // 创建节点B的Observable
        Observable<String> nodeB = root.flatMap(input -> {
            return Observable.fromCallable(() -> {
                // 模拟耗时操作
                TimeUnit.MILLISECONDS.sleep(100);
                return "(from:" + "b-" + Thread.currentThread().getName() + ",dept:" + input + ")";
            }).subscribeOn(Schedulers.computation()); // 使用computation调度器并发执行
        });

        // 创建节点C的Observable
        Observable<String> nodeC = root.flatMap(input -> {
            return Observable.fromCallable(() -> {
                // 模拟耗时操作
                TimeUnit.MILLISECONDS.sleep(100);
                return "(from:" + "c-" + Thread.currentThread().getName() + ",dept:" + input + ")";
            }).subscribeOn(Schedulers.computation()); // 使用computation调度器并发执行
        });

        // 合并节点B和C的结果，然后传递给节点D
        Observable<String> nodeD = Observable.zip(nodeB, nodeC, (b, c) -> {
            // 模拟耗时操作
            TimeUnit.MILLISECONDS.sleep(100);
            return "(from:" + "d-" + Thread.currentThread().getName() + ",dept:" + List.of(b, c) + ")";
        }).subscribeOn(Schedulers.computation()); // 使用computation调度器并发执行

        // 订阅并执行整个执行图
        nodeD.subscribe(result -> {
            System.out.println("Final result: " + result);
        });

        // 等待执行完成
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
