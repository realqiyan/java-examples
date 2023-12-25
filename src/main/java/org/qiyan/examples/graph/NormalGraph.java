package org.qiyan.examples.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NormalGraph {

    private static ExecutorService executor = Executors.newFixedThreadPool(32);

    private AsyncTask task = new AsyncTask();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //a->(b1,b2,b3...)->c
        //        String url = "http://127.0.0.1:8080/sleep?timeout=1000";
        String url = "http://gw.alicdn.com/tfs/TB176rg4VP7gK0jSZFjXXc5aXXa-286-118.png";
        new NormalGraph().exec(url);
    }

    private void exec(String url) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        //a
        NodeResult aVal = task.sync("a", url, null);
        long cost = System.currentTimeMillis() - start;
        System.out.println("耗时:" + cost);
        //(b1,b2,b3...)
        List<Future<NodeResult>> futures = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            int finalI = i;
            futures.add(executor.submit(() -> task.sync("b-" + finalI, url, List.of(aVal))));
        }
        List<NodeResult> results = new ArrayList<>();
        for (Future<NodeResult> future : futures) {
            results.add(future.get());
        }
        cost = System.currentTimeMillis() - start;
        System.out.println("耗时:" + cost);
        //c
        NodeResult cVal = task.sync("c", url, results);
        cost = System.currentTimeMillis() - start;
        System.out.println("耗时:" + cost + ",最终结果:" + cVal);
        task.close();
    }


}
