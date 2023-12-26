package org.qiyan.examples.flowapi;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Merge自定义处理类
 */
@Slf4j
public class MergeProcessor extends SubmissionPublisher implements Flow.Processor {
    private AtomicBoolean finish = new AtomicBoolean(false);
    private int total;
    private List<Object> result;
    private Flow.Subscription subscription;
    public MergeProcessor(int total) {
        this.total = total;
        this.result = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object item) {
        result.add(item);
        log.info("merge:{}/{} result:{}", result.size(), total, item);
        if (result.size() == total && finish.compareAndExchange(false, true)) {
            submit(result);
        }
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("MergeProcessor onError: {}", throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
        close();
    }
}
