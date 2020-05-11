package com.agapovp.reactivex.rxplayground.p003;

import com.agapovp.reactivex.rxplayground.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Old {

    private static final Logger logger = Utils.getLogger(Old.class.getCanonicalName(), Level.DEBUG);

    public static <T> Observable delayed(T x) {
        return Observable.unsafeCreate(
                new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        Runnable r = () -> {
                            logger.debug("Starting...");
                            sleep(10, SECONDS);
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(x);
                                subscriber.onCompleted();
                            }
                            logger.debug("Ending...");
                        };
                        final Thread thread = new Thread(r);
                        thread.start();
                        subscriber.add(Subscriptions.create(thread::interrupt));
                    }
                });
    }

    public static void main(String[] args) {
        delayed(42).subscribe().unsubscribe();
    }

    private static void sleep(int timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }
}
