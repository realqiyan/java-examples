package org.qiyan.examples.flowapi;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

/**
 * Map自定义处理类
 *
 * @param <T>
 * @param <R>
 */
public class MapProcessor<T, R> extends SubmissionPublisher<R> implements Flow.Processor<T, R> {
    private Function<T, R> func;
    private Flow.Subscription subscription;

    public MapProcessor(Function<T, R> func) {
        this.func = func;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }


    @Override
    public void onNext(T item) {
        R result = func.apply(item);
        submit(result);
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
