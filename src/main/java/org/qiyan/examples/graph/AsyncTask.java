package org.qiyan.examples.graph;

import org.asynchttpclient.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncTask {
    private static DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
            .setConnectTimeout(500)
            .setMaxConnections(100)
            .setMaxConnectionsPerHost(100);
    private static AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);

    public CompletableFuture<String> query(String url) {
        CompletableFuture<String> cf = new CompletableFuture<String>();
        BoundRequestBuilder request = client.prepareGet(url);

        return request.execute(new AsyncCompletionHandler<String>() {
            @Override
            public String onCompleted(Response response) throws Exception {
                return response.getResponseBody();
            }

            @Override
            public void onThrowable(Throwable t) {
                cf.completeExceptionally(t);
                super.onThrowable(t);
            }
        }).toCompletableFuture();
    }

    public void close(){
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String s = new AsyncTask().query("http://127.0.0.1:8080/sleep?timeout=1000").get();
        System.out.println(s);
    }

}
