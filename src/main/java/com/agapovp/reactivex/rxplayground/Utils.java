package com.agapovp.reactivex.rxplayground;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

public class Utils {

    @NotNull
    public static Logger getLogger(String name, Level level) {
        Logger logger = LogManager.getLogger(name);
        Configurator.setLevel(name, level);
        return logger;
    }
}
