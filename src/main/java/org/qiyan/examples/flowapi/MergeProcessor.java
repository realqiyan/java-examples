package org.qiyan.examples.flowapi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

/**
 * Merge自定义处理类
 */
public class MergeProcessor extends SubmissionPublisher implements Flow.Processor {
    private int count;

    private List<Object> result;
    private Flow.Subscription subscription;


    public MergeProcessor(int count) {
        this.count = count;
        this.result = new ArrayList<>(count);
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }


    @Override
    public void onNext(Object item) {
        count--;
        result.add(item);
        if (count <= 0) {
            submit(result);
        }
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        close();
    }
}
