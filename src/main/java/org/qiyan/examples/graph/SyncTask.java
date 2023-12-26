package org.qiyan.examples.graph;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Dsl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SyncTask {
    private ExecutorService executor = Executors.newFixedThreadPool(128);

    private HttpClient client = HttpClient.newBuilder().executor(executor).build();

    private String query(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.headers().firstValue("Date").get();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public NodeResult sync(String taskName, String url, List<NodeResult> depends) {
        String val = query(url);
        return new NodeResult(taskName, val, depends);
    }

    public void close() {
        try {
            executor.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SyncTask task = new SyncTask();
        NodeResult result = task.sync("test", "http://www.baidu.com", null);
        log.info(result.toString());
        task.close();
    }

}
