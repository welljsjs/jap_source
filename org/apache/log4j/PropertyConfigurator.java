/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Appender;
import org.apache.log4j.DefaultCategoryFactory;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyWatchdog;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.RendererSupport;

public class PropertyConfigurator
implements Configurator {
    protected Hashtable registry = new Hashtable(11);
    protected LoggerFactory loggerFactory = new DefaultCategoryFactory();
    static final String CATEGORY_PREFIX = "log4j.category.";
    static final String LOGGER_PREFIX = "log4j.logger.";
    static final String FACTORY_PREFIX = "log4j.factory";
    static final String ADDITIVITY_PREFIX = "log4j.additivity.";
    static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
    static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
    static final String APPENDER_PREFIX = "log4j.appender.";
    static final String RENDERER_PREFIX = "log4j.renderer.";
    static final String THRESHOLD_PREFIX = "log4j.threshold";
    public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";
    private static final String INTERNAL_ROOT_NAME = "root";
    static /* synthetic */ Class class$org$apache$log4j$spi$LoggerFactory;
    static /* synthetic */ Class class$org$apache$log4j$Appender;
    static /* synthetic */ Class class$org$apache$log4j$Layout;

    public void doConfigure(String string, LoggerRepository loggerRepository) {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(string);
            properties.load(fileInputStream);
            fileInputStream.close();
        }
        catch (IOException iOException) {
            LogLog.error("Could not read configuration file [" + string + "].", iOException);
            LogLog.error("Ignoring configuration file [" + string + "].");
            return;
        }
        this.doConfigure(properties, loggerRepository);
    }

    public static void configure(String string) {
        new PropertyConfigurator().doConfigure(string, LogManager.getLoggerRepository());
    }

    public static void configure(URL uRL) {
        new PropertyConfigurator().doConfigure(uRL, LogManager.getLoggerRepository());
    }

    public static void configure(Properties properties) {
        new PropertyConfigurator().doConfigure(properties, LogManager.getLoggerRepository());
    }

    public static void configureAndWatch(String string) {
        PropertyConfigurator.configureAndWatch(string, 60000L);
    }

    public static void configureAndWatch(String string, long l) {
        PropertyWatchdog propertyWatchdog = new PropertyWatchdog(string);
        propertyWatchdog.setDelay(l);
        propertyWatchdog.start();
    }

    public void doConfigure(Properties properties, LoggerRepository loggerRepository) {
        String string;
        String string2 = properties.getProperty("log4j.debug");
        if (string2 == null && (string2 = properties.getProperty("log4j.configDebug")) != null) {
            LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
        }
        if (string2 != null) {
            LogLog.setInternalDebugging(OptionConverter.toBoolean(string2, true));
        }
        if ((string = OptionConverter.findAndSubst(THRESHOLD_PREFIX, properties)) != null) {
            loggerRepository.setThreshold(OptionConverter.toLevel(string, Level.ALL));
            LogLog.debug("Hierarchy threshold set to [" + loggerRepository.getThreshold() + "].");
        }
        this.configureRootCategory(properties, loggerRepository);
        this.configureLoggerFactory(properties);
        this.parseCatsAndRenderers(properties, loggerRepository);
        LogLog.debug("Finished configuring.");
        this.registry.clear();
    }

    public void doConfigure(URL uRL, LoggerRepository loggerRepository) {
        Properties properties = new Properties();
        LogLog.debug("Reading configuration from URL " + uRL);
        try {
            properties.load(uRL.openStream());
        }
        catch (IOException iOException) {
            LogLog.error("Could not read configuration file from URL [" + uRL + "].", iOException);
            LogLog.error("Ignoring configuration file [" + uRL + "].");
            return;
        }
        this.doConfigure(properties, loggerRepository);
    }

    protected void configureLoggerFactory(Properties properties) {
        String string = OptionConverter.findAndSubst(LOGGER_FACTORY_KEY, properties);
        if (string != null) {
            LogLog.debug("Setting category factory to [" + string + "].");
            this.loggerFactory = (LoggerFactory)OptionConverter.instantiateByClassName(string, class$org$apache$log4j$spi$LoggerFactory == null ? (class$org$apache$log4j$spi$LoggerFactory = PropertyConfigurator.class$("org.apache.log4j.spi.LoggerFactory")) : class$org$apache$log4j$spi$LoggerFactory, this.loggerFactory);
            PropertySetter.setProperties(this.loggerFactory, properties, "log4j.factory.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void configureRootCategory(Properties properties, LoggerRepository loggerRepository) {
        String string = ROOT_LOGGER_PREFIX;
        String string2 = OptionConverter.findAndSubst(ROOT_LOGGER_PREFIX, properties);
        if (string2 == null) {
            string2 = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, properties);
            string = ROOT_CATEGORY_PREFIX;
        }
        if (string2 == null) {
            LogLog.debug("Could not find root logger information. Is this OK?");
        } else {
            Logger logger;
            Logger logger2 = logger = loggerRepository.getRootLogger();
            synchronized (logger2) {
                this.parseCategory(properties, logger, string, INTERNAL_ROOT_NAME, string2);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void parseCatsAndRenderers(Properties properties, LoggerRepository loggerRepository) {
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String string;
            String string2;
            String string3 = (String)enumeration.nextElement();
            if (string3.startsWith(CATEGORY_PREFIX) || string3.startsWith(LOGGER_PREFIX)) {
                Logger logger;
                string2 = null;
                if (string3.startsWith(CATEGORY_PREFIX)) {
                    string2 = string3.substring(CATEGORY_PREFIX.length());
                } else if (string3.startsWith(LOGGER_PREFIX)) {
                    string2 = string3.substring(LOGGER_PREFIX.length());
                }
                string = OptionConverter.findAndSubst(string3, properties);
                Logger logger2 = logger = loggerRepository.getLogger(string2, this.loggerFactory);
                synchronized (logger2) {
                    this.parseCategory(properties, logger, string3, string2, string);
                    this.parseAdditivityForLogger(properties, logger, string2);
                    continue;
                }
            }
            if (!string3.startsWith(RENDERER_PREFIX)) continue;
            string2 = string3.substring(RENDERER_PREFIX.length());
            string = OptionConverter.findAndSubst(string3, properties);
            if (!(loggerRepository instanceof RendererSupport)) continue;
            RendererMap.addRenderer((RendererSupport)((Object)loggerRepository), string2, string);
        }
    }

    void parseAdditivityForLogger(Properties properties, Logger logger, String string) {
        String string2 = OptionConverter.findAndSubst(ADDITIVITY_PREFIX + string, properties);
        LogLog.debug("Handling log4j.additivity." + string + "=[" + string2 + "]");
        if (string2 != null && !string2.equals("")) {
            boolean bl = OptionConverter.toBoolean(string2, true);
            LogLog.debug("Setting additivity for \"" + string + "\" to " + bl);
            logger.setAdditivity(bl);
        }
    }

    void parseCategory(Properties properties, Logger logger, String string, String string2, String string3) {
        Object object;
        LogLog.debug("Parsing for [" + string2 + "] with value=[" + string3 + "].");
        StringTokenizer stringTokenizer = new StringTokenizer(string3, ",");
        if (!string3.startsWith(",") && !string3.equals("")) {
            if (!stringTokenizer.hasMoreTokens()) {
                return;
            }
            object = stringTokenizer.nextToken();
            LogLog.debug("Level token is [" + (String)object + "].");
            if ("inherited".equalsIgnoreCase((String)object) || "null".equalsIgnoreCase((String)object)) {
                if (string2.equals(INTERNAL_ROOT_NAME)) {
                    LogLog.warn("The root logger cannot be set to null.");
                } else {
                    logger.setLevel(null);
                }
            } else {
                logger.setLevel(OptionConverter.toLevel((String)object, Level.DEBUG));
            }
            LogLog.debug("Category " + string2 + " set to " + logger.getLevel());
        }
        logger.removeAllAppenders();
        while (stringTokenizer.hasMoreTokens()) {
            String string4 = stringTokenizer.nextToken().trim();
            if (string4 == null || string4.equals(",")) continue;
            LogLog.debug("Parsing appender named \"" + string4 + "\".");
            object = this.parseAppender(properties, string4);
            if (object == null) continue;
            logger.addAppender((Appender)object);
        }
    }

    Appender parseAppender(Properties properties, String string) {
        Appender appender = this.registryGet(string);
        if (appender != null) {
            LogLog.debug("Appender \"" + string + "\" was already parsed.");
            return appender;
        }
        String string2 = APPENDER_PREFIX + string;
        String string3 = string2 + ".layout";
        appender = (Appender)OptionConverter.instantiateByKey(properties, string2, class$org$apache$log4j$Appender == null ? (class$org$apache$log4j$Appender = PropertyConfigurator.class$("org.apache.log4j.Appender")) : class$org$apache$log4j$Appender, null);
        if (appender == null) {
            LogLog.error("Could not instantiate appender named \"" + string + "\".");
            return null;
        }
        appender.setName(string);
        if (appender instanceof OptionHandler) {
            Layout layout;
            if (appender.requiresLayout() && (layout = (Layout)OptionConverter.instantiateByKey(properties, string3, class$org$apache$log4j$Layout == null ? (class$org$apache$log4j$Layout = PropertyConfigurator.class$("org.apache.log4j.Layout")) : class$org$apache$log4j$Layout, null)) != null) {
                appender.setLayout(layout);
                LogLog.debug("Parsing layout options for \"" + string + "\".");
                PropertySetter.setProperties(layout, properties, string3 + ".");
                LogLog.debug("End of parsing for \"" + string + "\".");
            }
            PropertySetter.setProperties(appender, properties, string2 + ".");
            LogLog.debug("Parsed \"" + string + "\" options.");
        }
        this.registryPut(appender);
        return appender;
    }

    void registryPut(Appender appender) {
        this.registry.put(appender.getName(), appender);
    }

    Appender registryGet(String string) {
        return (Appender)this.registry.get(string);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

