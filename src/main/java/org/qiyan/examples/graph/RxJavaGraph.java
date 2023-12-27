package org.qiyan.examples.graph;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class RxJavaGraph {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //a->(b1,b2,b3...)->c
        String url = "https://vv.video.qq.com/checktime?otype=json";
        new RxJavaGraph().exec(url);
    }

    public void exec(String url) {
        AsyncTask task = new AsyncTask();
        // init
        task.async("init", url, null);
        long start = System.currentTimeMillis();

        int times = 300;
        // a->(b1,b2,b3)->c
        Flowable.just(url)
                .flatMap(input -> Flowable.fromFuture(task.async("a", input, null)))
                .flatMap(input ->
                        Flowable.range(1, times)
                                .flatMap(
                                        idx -> Flowable.fromFuture(task.async("b-" + idx, url, List.of(input)))
                                                .subscribeOn(Schedulers.io()), // 使用Schedulers.io()来并行执行
                                        false, // 异常是否直接终止
                                        times // 最大并发数
                                )
                                .toList() // 收集结果到List
                                .toFlowable() // 将Single转换回Flowable
                )
                .flatMap(list -> Flowable.fromFuture(task.async("c", url, list)))
                .subscribe(
                        input -> {
                            task.close();
                            log.info("final耗时:" + (System.currentTimeMillis() - start) + ",最终结果:" + input.getDepends().size());
                        },
                        error -> {
                            task.close();
                            log.error("Error occurred", error);
                        }
                );
    }
}
