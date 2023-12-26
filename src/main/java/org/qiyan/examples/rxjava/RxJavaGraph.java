package org.qiyan.examples.rxjava;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
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
                return "(from:" + "b-" + Thread.currentThread().getName() + ",depend:" + input + ")";
            }).subscribeOn(Schedulers.computation()); // 使用computation调度器并发执行
        });

        // 创建节点C的Observable
        Observable<String> nodeC = root.flatMap(input -> {
            return Observable.fromCallable(() -> {
                // 模拟耗时操作
                TimeUnit.MILLISECONDS.sleep(100);
                return "(from:" + "c-" + Thread.currentThread().getName() + ",depend:" + input + ")";
            }).subscribeOn(Schedulers.computation()); // 使用computation调度器并发执行
        });

        // 合并节点B和C的结果，然后传递给节点D
        Observable<String> nodeD = Observable.zip(nodeB, nodeC, (b, c) -> {
            // 模拟耗时操作
            TimeUnit.MILLISECONDS.sleep(100);
            return "(from:" + "d-" + Thread.currentThread().getName() + ",depend:" + List.of(b, c) + ")";
        }).subscribeOn(Schedulers.computation()); // 使用computation调度器并发执行

        // 订阅并执行整个执行图
        nodeD.subscribe(result -> {
            log.info("Final result: " + result);
        });

        // 等待执行完成
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
