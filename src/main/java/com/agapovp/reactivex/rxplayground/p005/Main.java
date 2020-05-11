package com.agapovp.reactivex.rxplayground.p005;

import com.agapovp.reactivex.rxplayground.Utils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger logger = Utils.getLogger(Main.class.getCanonicalName(), Level.DEBUG);

    private static final String[] array = {"ZERO", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE",
            "TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"};

    public static void main(String[] args) throws InterruptedException {

        CompositeDisposable disposable = new CompositeDisposable();

        Observable<Long> observableDefer = Observable.defer(() -> Observable.just(System.currentTimeMillis()));

        Observable<String[][]> stringNumbersMatrix = Observable.just(
                new String[][]{
                        {"ZERO", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE"},
                        {"TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"}
                }
        );

        Observable<String> stringNumbers = Observable.fromArray(array);

        disposable.add(
                stringNumbersMatrix
                        .flatMap(Observable::fromArray)
                        .flatMap(Observable::fromArray)
                        .subscribe(logger::debug)
        );

        disposable.add(
                stringNumbers
                        .map(String::toLowerCase)
                        .collect(HashMap<Integer, String>::new, (map, string) -> map.put(string.hashCode(), string))
                        .subscribe(map -> logger.debug(String.format("%s - %d", map.toString(), map.size())))
        );

        disposable.add(
                stringNumbers
                        .doOnNext(string -> logger.debug(String.format("Applying lower case to %s in %s", string, Thread.currentThread().toString())))
                        .map(String::toLowerCase)
                        .doOnNext(string -> logger.debug(String.format("Applied lower case: %s", string)))
                        .collect(ArrayDeque<String>::new, ArrayDeque::add)
                        .flatMapObservable(Observable::fromIterable)
                        .subscribe(message -> logger.debug(String.format("Queue - %s", message)))
        );

        disposable.add(
                observableDefer.subscribe(logger::debug)
        );

        TimeUnit.SECONDS.sleep(3);

        disposable.add(
                observableDefer.subscribe(logger::debug)
        );

        disposable.dispose();
    }
}
