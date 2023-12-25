package org.qiyan.examples.graph;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class AsyncTask {
    private AsyncHttpClient client = Dsl.asyncHttpClient();

    public CompletableFuture<String> query(String url) {
        BoundRequestBuilder request = client.prepareGet(url);
        return request.execute().toCompletableFuture()
                .thenApply(resp -> resp.getHeader("Date"))
                .exceptionally(e -> {
                    log.error(e.getMessage());
                    return e.getMessage();
                });
    }

    public CompletableFuture<NodeResult> async(String taskName, String url, List<NodeResult> depends) {
        CompletableFuture<String> query = query(url);
        return query.thenApply((taskVal) -> new NodeResult(taskName, taskVal, depends));
    }

    public NodeResult sync(String taskName, String url, List<NodeResult> depends) {
        CompletableFuture<String> taskCF = query(url);
        String taskVal = null;
        try {
            taskVal = taskCF.get();
        } catch (Exception e) {
            taskVal = e.getMessage();
        }
        return new NodeResult(taskName, taskVal, depends);
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String s = new AsyncTask().query("http://127.0.0.1:8080/sleep?timeout=1000").get();
        log.info(s);
    }

}
