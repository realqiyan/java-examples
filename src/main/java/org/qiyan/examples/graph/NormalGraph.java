package org.qiyan.examples.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NormalGraph {

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    private static AsyncTask TASK = new AsyncTask();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        //a
        String aVal = task("a", "root");
        //(b,c)
        List<Future<String>> futures = new ArrayList<>(2);
        futures.add(executorService.submit(() -> task("b", aVal)));
        futures.add(executorService.submit(() -> task("c", aVal)));
        List<String> strings = new ArrayList<>(2);
        for (Future<String> future : futures) {
            strings.add(future.get());
        }
        //d
        String dVal = task("d", strings.toString());
        long cost = System.currentTimeMillis() - start;
        System.out.println("耗时:" + cost + ",最终结果:" + dVal);
        TASK.close();

    }

    private static String task(String taskName, String depend) throws ExecutionException, InterruptedException {
        CompletableFuture<String> task = TASK.query("http://127.0.0.1:8080/sleep?timeout=1000");
        String taskVal = task.get();
        return "(from:" + taskName + "-" + taskVal + "-" + Thread.currentThread().getName() + ",depend:" + depend + ")";
    }
}
