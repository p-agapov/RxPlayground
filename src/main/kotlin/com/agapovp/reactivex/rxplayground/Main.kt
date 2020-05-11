@file:JvmName(name = "MainKt")

package com.agapovp.reactivex.rxplayground

import io.reactivex.rxjava3.kotlin.toObservable
import org.apache.logging.log4j.Level

val logger = Utils.getLogger(Main::class.java.canonicalName, Level.DEBUG)

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            IntRange(1, 100)
                .toObservable()
                .skip(15)
                .take(50)
                .subscribe(logger::debug)
        }
    }
}
