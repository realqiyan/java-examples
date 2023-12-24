package org.qiyan.examples.flowapi;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Final自定义处理类
 *
 * @param <T>
 */
public class FinalProcessor<T> implements Flow.Subscriber<T> {
    private Consumer<T> func;
    private Flow.Subscription subscription;

    public FinalProcessor(Consumer<T> func) {
        this.func = func;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }


    @Override
    public void onNext(T item) {
        func.accept(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
    }
}
