package org.qiyan.examples.graph;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class NormalGraph {

    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //a->(b1,b2,b3...)->c
        //        String url = "http://127.0.0.1:8080/sleep?timeout=1000";
        String url = "https://vv.video.qq.com/checktime?otype=json";
        new NormalGraph().exec(url);
        executor.shutdown();
    }

    private void exec(String url) throws ExecutionException, InterruptedException {
        SyncTask task = new SyncTask();
        // init
        task.sync("init", url, null);
        long start = System.currentTimeMillis();
        //a
        NodeResult aVal = task.sync("a", url, null);
        long cost = System.currentTimeMillis() - start;
        log.info("a耗时:" + cost);
        //(b1,b2,b3...)
        List<Future<NodeResult>> futures = new ArrayList<>();

        for (int i = 1; i <= 300; i++) {
            int finalI = i;
            futures.add(executor.submit(() -> task.sync("b-" + finalI, url, List.of(aVal))));
        }
        int idx = 0, total = futures.size();
        List<NodeResult> results = new ArrayList<>(total);
        for (Future<NodeResult> future : futures) {
            NodeResult result = future.get();
            idx++;
            log.info("merge:{}/{} result:{}", idx, total, result);
            results.add(result);
        }
        cost = System.currentTimeMillis() - start;
        log.info("merge耗时:" + cost);
        //c
        NodeResult cVal = task.sync("c", url, results);
        cost = System.currentTimeMillis() - start;
        log.info("final耗时:" + cost + ",最终结果:" + cVal.getDepends().size());
        task.close();
    }


}
