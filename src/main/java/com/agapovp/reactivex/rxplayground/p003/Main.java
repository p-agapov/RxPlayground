package com.agapovp.reactivex.rxplayground.p003;

import com.agapovp.reactivex.rxplayground.Utils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = Utils.getLogger(Main.class.getCanonicalName(), Level.DEBUG);

    private static final Observable<String> observableFromCallable = Observable.fromCallable(() -> "String from callable...");

    private static final Observable<Long> observableTime = Observable.timer(1, TimeUnit.SECONDS);

    private static final Observable<Long> observableInterval = Observable.interval(1, TimeUnit.SECONDS);

    @SneakyThrows
    public static void main(String[] args) {

        @NonNull Disposable subscriberFromCallable = observableFromCallable.subscribe(logger::debug);
        @NonNull Disposable subscriberTime = observableTime.subscribe(logger::error);
        @NonNull Disposable subscriberInterval = observableInterval.subscribe(logger::debug);

        TimeUnit.SECONDS.sleep(5);

        subscriberFromCallable.dispose();
        subscriberTime.dispose();
        subscriberInterval.dispose();
    }
}
