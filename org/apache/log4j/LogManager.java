/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;

public class LogManager {
    public static final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
    static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
    public static final String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
    public static final String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
    public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";
    private static Object guard = null;
    private static RepositorySelector repositorySelector;

    public static void setRepositorySelector(RepositorySelector repositorySelector, Object object) throws IllegalArgumentException {
        if (guard != null && guard != object) {
            throw new IllegalArgumentException("Attempted to reset the LoggerFactory without possessing the guard.");
        }
        if (repositorySelector == null) {
            throw new IllegalArgumentException("RepositorySelector must be non-null.");
        }
        guard = object;
        LogManager.repositorySelector = repositorySelector;
    }

    public static LoggerRepository getLoggerRepository() {
        return repositorySelector.getLoggerRepository();
    }

    public static Logger getRootLogger() {
        return repositorySelector.getLoggerRepository().getRootLogger();
    }

    public static Logger getLogger(String string) {
        return repositorySelector.getLoggerRepository().getLogger(string);
    }

    public static Logger getLogger(Class class_) {
        return repositorySelector.getLoggerRepository().getLogger(class_.getName());
    }

    public static Logger getLogger(String string, LoggerFactory loggerFactory) {
        return repositorySelector.getLoggerRepository().getLogger(string, loggerFactory);
    }

    public static Logger exists(String string) {
        return repositorySelector.getLoggerRepository().exists(string);
    }

    public static Enumeration getCurrentLoggers() {
        return repositorySelector.getLoggerRepository().getCurrentLoggers();
    }

    public static void shutdown() {
        repositorySelector.getLoggerRepository().shutdown();
    }

    public static void resetConfiguration() {
        repositorySelector.getLoggerRepository().resetConfiguration();
    }

    static {
        Hierarchy hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
        repositorySelector = new DefaultRepositorySelector(hierarchy);
        String string = OptionConverter.getSystemProperty(DEFAULT_INIT_OVERRIDE_KEY, null);
        if (string == null || "false".equalsIgnoreCase(string)) {
            String string2 = OptionConverter.getSystemProperty(DEFAULT_CONFIGURATION_KEY, null);
            String string3 = OptionConverter.getSystemProperty(CONFIGURATOR_CLASS_KEY, null);
            URL uRL = null;
            if (string2 == null) {
                uRL = Loader.getResource(DEFAULT_XML_CONFIGURATION_FILE);
                if (uRL == null) {
                    uRL = Loader.getResource(DEFAULT_CONFIGURATION_FILE);
                }
            } else {
                try {
                    uRL = new URL(string2);
                }
                catch (MalformedURLException malformedURLException) {
                    uRL = Loader.getResource(string2);
                }
            }
            if (uRL != null) {
                LogLog.debug("Using URL [" + uRL + "] for automatic log4j configuration.");
                OptionConverter.selectAndConfigure(uRL, string3, LogManager.getLoggerRepository());
            } else {
                LogLog.debug("Could not find resource: [" + string2 + "].");
            }
        }
    }
}

