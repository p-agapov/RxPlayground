package com.agapovp.reactivex.rxplayground.p004;

import com.agapovp.reactivex.rxplayground.Utils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.*;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    private static final Logger logger = Utils.getLogger(Main.class.getCanonicalName(), Level.DEBUG);

    //Sends last value to all subscribers when onComplete(), ignores previous.
    private static final AsyncSubject<Integer> asyncSubject = AsyncSubject.create();
    //Sends last value that was before subscription (or default one), sends all values after.
    private static final BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.createDefault(42);
    private static final CompletableSubject completableSubject = CompletableSubject.create();
    private static final MaybeSubject<Integer> maybeSubject = MaybeSubject.create();
    private static final PublishSubject<Integer> publishSubject = PublishSubject.create();
    private static final ReplaySubject<Integer> replaySubject = ReplaySubject.create();
    private static final SingleSubject<Integer> singleSubject = SingleSubject.create();
    private static final UnicastSubject<Integer> unicastSubject = UnicastSubject.create();

    public static void main(String[] args) throws InterruptedException {

        Thread thread;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> IntStream.range(0, 51).distinct().forEach(logger::debug));

        @NonNull Observable<Integer> integerObservable = Observable
                .fromStream(
                        Stream.generate(new MySupplier())
                                .limit(10)
                                .filter(s -> s % 2 == 0)
                );

        Observable<Integer> infiniteObservable = Observable
                .create(emitter -> {
                    while (true) {
                        TimeUnit.MILLISECONDS.sleep(500);
                        emitter.onNext(228);
                    }
                });

        logger.debug("Before...");

        integerObservable.subscribe(asyncSubject);
        infiniteObservable.subscribeOn(Schedulers.newThread()).subscribe(behaviorSubject);
        integerObservable.subscribe(publishSubject);
        integerObservable.subscribe(replaySubject);
        integerObservable.subscribe(unicastSubject);

        asyncSubject
                .subscribeOn(Schedulers.newThread())
                .subscribe(logger::info);

        asyncSubject
                .subscribeOn(Schedulers.newThread())
                .subscribe(logger::info);

        behaviorSubject
                .subscribe(logger::info);

        TimeUnit.SECONDS.sleep(5);

        logger.debug("After...");
    }

    private static class MySupplier implements Supplier<Integer> {

        Integer integer = 0;

        @SneakyThrows
        @Override
        public Integer get() {
            TimeUnit.MILLISECONDS.sleep(500);
            logger.debug("Processing...");
            return integer++;
        }
    }
}
