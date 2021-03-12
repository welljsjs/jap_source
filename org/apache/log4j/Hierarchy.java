/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.CategoryKey;
import org.apache.log4j.DefaultCategoryFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.ProvisionNode;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;

public class Hierarchy
implements LoggerRepository,
RendererSupport {
    private LoggerFactory defaultFactory;
    private Vector listeners;
    Hashtable ht = new Hashtable();
    Logger root;
    RendererMap rendererMap;
    int thresholdInt;
    Level threshold;
    boolean emittedNoAppenderWarning = false;
    boolean emittedNoResourceBundleWarning = false;

    public Hierarchy(Logger logger) {
        this.listeners = new Vector(1);
        this.root = logger;
        this.setThreshold(Level.ALL);
        this.root.setHierarchy(this);
        this.rendererMap = new RendererMap();
        this.defaultFactory = new DefaultCategoryFactory();
    }

    public void addRenderer(Class class_, ObjectRenderer objectRenderer) {
        this.rendererMap.put(class_, objectRenderer);
    }

    public void addHierarchyEventListener(HierarchyEventListener hierarchyEventListener) {
        if (this.listeners.contains(hierarchyEventListener)) {
            LogLog.warn("Ignoring attempt to add an existent listener.");
        } else {
            this.listeners.addElement(hierarchyEventListener);
        }
    }

    public void clear() {
        this.ht.clear();
    }

    public void emitNoAppenderWarning(Category category) {
        if (!this.emittedNoAppenderWarning) {
            LogLog.warn("No appenders could be found for logger (" + category.getName() + ").");
            LogLog.warn("Please initialize the log4j system properly.");
            this.emittedNoAppenderWarning = true;
        }
    }

    public Logger exists(String string) {
        Object v = this.ht.get(new CategoryKey(string));
        if (v instanceof Logger) {
            return (Logger)v;
        }
        return null;
    }

    public void setThreshold(String string) {
        Level level = Level.toLevel(string, null);
        if (level != null) {
            this.setThreshold(level);
        } else {
            LogLog.warn("Could not convert [" + string + "] to Level.");
        }
    }

    public void setThreshold(Level level) {
        if (level != null) {
            this.thresholdInt = level.level;
            this.threshold = level;
        }
    }

    public void fireAddAppenderEvent(Category category, Appender appender) {
        if (this.listeners != null) {
            int n = this.listeners.size();
            for (int i = 0; i < n; ++i) {
                HierarchyEventListener hierarchyEventListener = (HierarchyEventListener)this.listeners.elementAt(i);
                hierarchyEventListener.addAppenderEvent(category, appender);
            }
        }
    }

    void fireRemoveAppenderEvent(Category category, Appender appender) {
        if (this.listeners != null) {
            int n = this.listeners.size();
            for (int i = 0; i < n; ++i) {
                HierarchyEventListener hierarchyEventListener = (HierarchyEventListener)this.listeners.elementAt(i);
                hierarchyEventListener.removeAppenderEvent(category, appender);
            }
        }
    }

    public Level getThreshold() {
        return this.threshold;
    }

    public Logger getLogger(String string) {
        return this.getLogger(string, this.defaultFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Logger getLogger(String string, LoggerFactory loggerFactory) {
        CategoryKey categoryKey = new CategoryKey(string);
        Hashtable hashtable = this.ht;
        synchronized (hashtable) {
            Object v = this.ht.get(categoryKey);
            if (v == null) {
                Logger logger = loggerFactory.makeNewLoggerInstance(string);
                logger.setHierarchy(this);
                this.ht.put(categoryKey, logger);
                this.updateParents(logger);
                return logger;
            }
            if (v instanceof Logger) {
                return (Logger)v;
            }
            if (v instanceof ProvisionNode) {
                Logger logger = loggerFactory.makeNewLoggerInstance(string);
                logger.setHierarchy(this);
                this.ht.put(categoryKey, logger);
                this.updateChildren((ProvisionNode)v, logger);
                this.updateParents(logger);
                return logger;
            }
            return null;
        }
    }

    public Enumeration getCurrentLoggers() {
        Vector vector = new Vector(this.ht.size());
        Enumeration enumeration = this.ht.elements();
        while (enumeration.hasMoreElements()) {
            Object v = enumeration.nextElement();
            if (!(v instanceof Logger)) continue;
            vector.addElement(v);
        }
        return vector.elements();
    }

    public Enumeration getCurrentCategories() {
        return this.getCurrentLoggers();
    }

    public RendererMap getRendererMap() {
        return this.rendererMap;
    }

    public Logger getRootLogger() {
        return this.root;
    }

    public boolean isDisabled(int n) {
        return this.thresholdInt > n;
    }

    public void overrideAsNeeded(String string) {
        LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetConfiguration() {
        this.getRootLogger().setLevel(Level.DEBUG);
        this.root.setResourceBundle(null);
        this.setThreshold(Level.ALL);
        Hashtable hashtable = this.ht;
        synchronized (hashtable) {
            this.shutdown();
            Enumeration enumeration = this.getCurrentLoggers();
            while (enumeration.hasMoreElements()) {
                Logger logger = (Logger)enumeration.nextElement();
                logger.setLevel(null);
                logger.setAdditivity(true);
                logger.setResourceBundle(null);
            }
        }
        this.rendererMap.clear();
    }

    public void setDisableOverride(String string) {
        LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
    }

    public void setRenderer(Class class_, ObjectRenderer objectRenderer) {
        this.rendererMap.put(class_, objectRenderer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        Logger logger = this.getRootLogger();
        logger.closeNestedAppenders();
        Hashtable hashtable = this.ht;
        synchronized (hashtable) {
            Logger logger2;
            Enumeration enumeration = this.getCurrentLoggers();
            while (enumeration.hasMoreElements()) {
                logger2 = (Logger)enumeration.nextElement();
                logger2.closeNestedAppenders();
            }
            logger.removeAllAppenders();
            enumeration = this.getCurrentLoggers();
            while (enumeration.hasMoreElements()) {
                logger2 = (Logger)enumeration.nextElement();
                logger2.removeAllAppenders();
            }
        }
    }

    private final void updateParents(Logger logger) {
        String string = logger.name;
        int n = string.length();
        boolean bl = false;
        int n2 = string.lastIndexOf(46, n - 1);
        while (n2 >= 0) {
            Serializable serializable;
            String string2 = string.substring(0, n2);
            CategoryKey categoryKey = new CategoryKey(string2);
            Object v = this.ht.get(categoryKey);
            if (v == null) {
                serializable = new ProvisionNode(logger);
                this.ht.put(categoryKey, serializable);
            } else {
                if (v instanceof Category) {
                    bl = true;
                    logger.parent = (Category)v;
                    break;
                }
                if (v instanceof ProvisionNode) {
                    ((ProvisionNode)v).addElement(logger);
                } else {
                    serializable = new IllegalStateException("unexpected object type " + v.getClass() + " in ht.");
                    ((Throwable)serializable).printStackTrace();
                }
            }
            n2 = string.lastIndexOf(46, n2 - 1);
        }
        if (!bl) {
            logger.parent = this.root;
        }
    }

    private final void updateChildren(ProvisionNode provisionNode, Logger logger) {
        int n = provisionNode.size();
        for (int i = 0; i < n; ++i) {
            Logger logger2 = (Logger)provisionNode.elementAt(i);
            if (logger2.parent.name.startsWith(logger.name)) continue;
            logger.parent = logger2.parent;
            logger2.parent = logger;
        }
    }
}

