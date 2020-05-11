package com.agapovp.reactivex.rxplayground.p002;

import com.agapovp.reactivex.rxplayground.Utils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Main {

    private static final Logger logger = Utils.getLogger(Main.class.getCanonicalName(), Level.DEBUG);
    private static final Random random = new Random();
    private static volatile boolean isRunning = true;

    private static final Observable<DataClass> nonCachedObservable = Observable.create(s -> {
        s.onNext(new DataClass(42));
        s.onComplete();
    });

    private static final Observable<DataClass> cachedObservable = Observable.create((ObservableOnSubscribe<DataClass>) s -> {
        s.onNext(new DataClass(42));
        s.onComplete();
    }).cache();

    private static final Observable<Integer> cachedInfiniteObservable = Observable.create((ObservableOnSubscribe<Integer>) s -> new Thread(() -> {
                int next;
                logger.debug("Started...");
                while (isRunning) {
                    next = random.nextInt(10);
                    s.onNext(next);
                    logger.debug("Next is: {}", next);
                    try {
                        TimeUnit.SECONDS.sleep(next);
                    } catch (InterruptedException e) {
                        logger.error(e);
                        Thread.currentThread().interrupt();
                    }
                }
                logger.debug("Ended...");
            }).start()
    ).cache();

    public static void main(String[] args) {

        cachedInfiniteObservable.subscribe().dispose();

        Observable.zip(nonCachedObservable, nonCachedObservable, Main::compareVerbally)
                .subscribe(logger::debug)
                .dispose();

        Observable.zip(cachedObservable, cachedObservable, DataClass::compareVerbally)
                .subscribe(logger::debug)
                .dispose();

        Observable.zip(nonCachedObservable, nonCachedObservable, nonCachedObservable, DataClass::compareVerbally)
                .subscribe(logger::debug)
                .dispose();

        Observable.zip(cachedObservable, cachedObservable, cachedObservable, DataClass::compareVerbally)
                .doOnComplete(Main::cancel)
                .subscribe()
                .dispose();
    }

    private static String compareVerbally(Object a, Object b) {
        return String.format("%d = %d is %s", a.hashCode(), b.hashCode(), a.equals(b));
    }

    private static synchronized void cancel() {
        int seconds = random.nextInt(10) + 10;

        logger.debug("Interrupting in: {} seconds...", seconds);
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }

        isRunning = false;
    }
}

final class DataClass {

    private final int data;

    public DataClass(int data) {
        this.data = data;
    }

    public String compareVerbally(DataClass obj) {
        return String.format("%d = %d is %s", this.hashCode(), obj.hashCode(), this.equals(obj));
    }

    public String compareVerbally(DataClass obj1, DataClass obj2) {
        return String.format(
                "%d = %d = %d is %s",
                this.hashCode(),
                obj1.hashCode(),
                obj2.hashCode(),
                this.equals(obj1) && this.equals(obj2)
        );
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }
}
