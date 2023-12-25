package org.qiyan.examples.graph;

import org.qiyan.examples.flowapi.AsyncMapProcessor;
import org.qiyan.examples.flowapi.FinalProcessor;
import org.qiyan.examples.flowapi.MapProcessor;
import org.qiyan.examples.flowapi.MergeProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class FlowApiGraph {

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    private static AsyncTask TASK = new AsyncTask();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();
        SubmissionPublisher<String> a = new SubmissionPublisher<>();
        AsyncMapProcessor<String, String> b = new AsyncMapProcessor<>(input -> task("b", input));
        AsyncMapProcessor<String, String> c = new AsyncMapProcessor<>(input -> task("c", input));
        AsyncMapProcessor<List<String>, String> d = new AsyncMapProcessor<>(input -> task("d", input.toString()));
        FinalProcessor<String> print = new FinalProcessor<>(new Consumer<String>() {
            @Override
            public void accept(String s) {
                long cost = System.currentTimeMillis() - start;
                System.out.println("耗时:" + cost + ",最终结果:" + s);
            }
        });

        MergeProcessor merge = new MergeProcessor(2);

        a.subscribe(b);
        a.subscribe(c);
        b.subscribe(merge);
        c.subscribe(merge);
        merge.subscribe(d);
        d.subscribe(print);

        a.submit(task("a", "root").get());


    }

    private static CompletableFuture<String> task(String taskName, String depend) {
        CompletableFuture<String> query = TASK.query("http://127.0.0.1:8080/sleep?timeout=1000");
        return query.thenApply((taskVal)-> "(from:" + taskName + "-" + taskVal + "-" + Thread.currentThread().getName() + ",depend:" + depend + ")");
    }
}
