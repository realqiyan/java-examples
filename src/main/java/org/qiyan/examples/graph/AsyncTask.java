package org.qiyan.examples.graph;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class AsyncTask {
    private AsyncHttpClient client = Dsl.asyncHttpClient(Dsl.config().setThreadPoolName("graph-async-http").setIoThreadsCount(10));
//    private ExecutorService executor = Executors.newFixedThreadPool(128);

    private CompletableFuture<String> query(String url) {
        BoundRequestBuilder request = client.prepareGet(url);
        return request.execute().toCompletableFuture()
                .thenApply(resp -> {
                    if (resp.getStatusCode() != 200) {
                        log.warn("status:{}", resp.getStatusCode());
                    }
                    return resp.getHeader("Date");
                })
                .exceptionally(e -> {
                    log.error(e.getMessage());
                    return e.getMessage();
                });

//        CompletableFuture<String> mock = new CompletableFuture<>();
//        executor.submit(() -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(30);
//                mock.complete("now:" + System.currentTimeMillis());
//            } catch (InterruptedException e) {
//                mock.completeExceptionally(e);
//            }
//        });
//        return mock;

    }

    public CompletableFuture<NodeResult> async(String taskName, String url, List<NodeResult> depends) {
        CompletableFuture<String> query = query(url);
        return query.thenApply((taskVal) -> new NodeResult(taskName, taskVal, depends));
    }

    public void close() {
        try {
            client.close();
//            executor.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        AsyncTask task = new AsyncTask();

        task.async("test", "http://www.baidu.com", null).thenAccept(result -> {
            log.info(result.toString());
            task.close();
        });

    }

}
