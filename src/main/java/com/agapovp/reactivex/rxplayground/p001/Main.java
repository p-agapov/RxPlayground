package com.agapovp.reactivex.rxplayground.p001;

import com.agapovp.reactivex.rxplayground.Utils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = Utils.getLogger(Main.class.getCanonicalName(), Level.DEBUG);

    public static void main(String[] args) throws InterruptedException {

        @NonNull Observable<Integer> observableFromArray = Observable.fromArray(1, 2, 3, 5, 6, 7, 8, 9, 10);

        @NonNull Observable<Integer> observableFromRange = Observable.range(1, 100);

        Single<Runnable> singleEmitter = Single.create(emitter -> {
            try {
                emitter.onSuccess(() -> logger.debug("OK!"));
            } catch (Exception e) {
                emitter.onError(e);
            }
        });

        Observable.empty();

        Observable.never();

        Observable.error(new UnsupportedOperationException("NEIN!"));

        Single<Runnable> singleFromSupplier = Single.fromSupplier(() -> (Runnable) () -> logger.debug("OK2!"));

        Observable<Integer> observableUnstable = Observable.create(s -> {
            logger.debug(String.format("Observable beginning: %s", Thread.currentThread().getName()));
            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    s.onNext(i);
                    logger.debug(String.format("Observable in new thread: %s", Thread.currentThread().getName()));
                }
                s.onComplete();
            }).start();
            logger.debug(String.format("Observable ending: %s", Thread.currentThread().getName()));
        });

        Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                logger.debug("Subscribed!");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                logger.debug("Processed: {}", integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                logger.error(e);
            }

            @Override
            public void onComplete() {
                logger.debug("Complete!");
            }
        };

        Thread.sleep(5000);

        logger.debug("Main before: {}", Thread.currentThread().getName());
        observableUnstable.skip(10).take(5).subscribe(observer);
        logger.debug("Main after: {}", Thread.currentThread().getName());
        observableUnstable.subscribe(s -> logger.debug("OLOLOL: {}", s));

        Thread.sleep(5000);

        Single.zip(singleEmitter, singleFromSupplier, (runnable, runnable2) -> {
            runnable.run();
            runnable2.run();
            return (Runnable) () -> logger.debug("Я закончил, ПЁС!");
        }).subscribe(Runnable::run).dispose();

        observableFromArray
                .skip(1)
                .take(1)
                .subscribe(logger::debug)
                .dispose();

        observableFromRange.subscribe(logger::debug).dispose();
    }
}
