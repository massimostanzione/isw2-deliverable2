package it.uniroma2.dicii.isw2.deliverable2.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Unique logger for the whole project.
 * Implemented with the <i>singleton</i> design pattern.
 */
public class LoggerInst {
    private static Logger instance = null;

    static {
        InputStream stream = LoggerInst.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
            instance = Logger.getLogger(LoggerInst.class.getName());
        } catch (IOException e) {
            instance.severe(e.getMessage());
        }
    }

    private LoggerInst() {
    }

    public static Logger getSingletonInstance() {
        if (instance == null) {
            new LoggerInst();
        }
        return instance;
    }
}