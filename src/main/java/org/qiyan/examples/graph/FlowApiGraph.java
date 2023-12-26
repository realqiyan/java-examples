package org.qiyan.examples.graph;

import lombok.extern.slf4j.Slf4j;
import org.qiyan.examples.flowapi.AsyncMapProcessor;
import org.qiyan.examples.flowapi.FinalProcessor;
import org.qiyan.examples.flowapi.MapProcessor;
import org.qiyan.examples.flowapi.MergeProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
public class FlowApiGraph {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        String url = "http://127.0.0.1:8080/sleep?timeout=1000";
        String url = "http://gw.alicdn.com/tfs/TB176rg4VP7gK0jSZFjXXc5aXXa-286-118.png";
        new FlowApiGraph().exec(url);
    }

    private void exec(String url) throws ExecutionException, InterruptedException {
        AsyncTask task = new AsyncTask();
        // init
        task.async("init", url, null);

        long start = System.currentTimeMillis();
        // root节点a
        SubmissionPublisher<NodeResult> a = new SubmissionPublisher<>();
        // 节点b1,b2,b3...
        List<AsyncMapProcessor<NodeResult, NodeResult>> processors = new ArrayList<>();
        for (int i = 1; i <= 3000; i++) {
            int finalI = i;
            processors.add(new AsyncMapProcessor<>(input -> task.async("b-" + finalI, url, List.of(input))));
        }
        // 节点c
        AsyncMapProcessor<List<NodeResult>, NodeResult> c = new AsyncMapProcessor<>(input -> {
            log.info("merge耗时:{}", System.currentTimeMillis() - start);
            return task.async("c", url, input);
        });
        // 输出节点
        FinalProcessor<NodeResult> print = new FinalProcessor<>(s -> {
            log.info("final耗时:{},最终结果:{}", System.currentTimeMillis() - start, s.getDepends().size());
            task.close();
        });
        // 连接节点
        MergeProcessor merge = new MergeProcessor(processors.size());

        // 创建连接
        for (AsyncMapProcessor<NodeResult, NodeResult> processor : processors) {
            a.subscribe(processor);
            processor.subscribe(merge);
        }
        merge.subscribe(c);
        c.subscribe(print);
        // 提交任务
        NodeResult aResult = task.async("a", url, null).get();
        log.info("a耗时:{}", System.currentTimeMillis() - start);
        a.submit(aResult);
    }

}
