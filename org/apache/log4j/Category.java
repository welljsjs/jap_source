/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.NullEnumeration;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

public class Category
implements AppenderAttachable {
    protected String name;
    protected volatile Level level;
    protected volatile Category parent;
    private static final String FQCN = (class$org$apache$log4j$Category == null ? (class$org$apache$log4j$Category = Category.class$("org.apache.log4j.Category")) : class$org$apache$log4j$Category).getName();
    protected ResourceBundle resourceBundle;
    protected LoggerRepository repository;
    AppenderAttachableImpl aai;
    protected boolean additive = true;
    static /* synthetic */ Class class$org$apache$log4j$Category;

    protected Category(String string) {
        this.name = string;
    }

    public synchronized void addAppender(Appender appender) {
        if (this.aai == null) {
            this.aai = new AppenderAttachableImpl();
        }
        this.aai.addAppender(appender);
        this.repository.fireAddAppenderEvent(this, appender);
    }

    public void assertLog(boolean bl, String string) {
        if (!bl) {
            this.error(string);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void callAppenders(LoggingEvent loggingEvent) {
        int n = 0;
        Category category = this;
        while (category != null) {
            Category category2 = category;
            synchronized (category2) {
                if (category.aai != null) {
                    n += category.aai.appendLoopOnAppenders(loggingEvent);
                }
                if (!category.additive) {
                    break;
                }
            }
            category = category.parent;
        }
        if (n == 0) {
            this.repository.emitNoAppenderWarning(this);
        }
    }

    synchronized void closeNestedAppenders() {
        Enumeration enumeration = this.getAllAppenders();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Appender appender = (Appender)enumeration.nextElement();
                if (!(appender instanceof AppenderAttachable)) continue;
                appender.close();
            }
        }
    }

    public void debug(Object object) {
        if (this.repository.isDisabled(10000)) {
            return;
        }
        if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.DEBUG, object, null);
        }
    }

    public void debug(Object object, Throwable throwable) {
        if (this.repository.isDisabled(10000)) {
            return;
        }
        if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.DEBUG, object, throwable);
        }
    }

    public void error(Object object) {
        if (this.repository.isDisabled(40000)) {
            return;
        }
        if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.ERROR, object, null);
        }
    }

    public void error(Object object, Throwable throwable) {
        if (this.repository.isDisabled(40000)) {
            return;
        }
        if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.ERROR, object, throwable);
        }
    }

    public static Logger exists(String string) {
        return LogManager.exists(string);
    }

    public void fatal(Object object) {
        if (this.repository.isDisabled(50000)) {
            return;
        }
        if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.FATAL, object, null);
        }
    }

    public void fatal(Object object, Throwable throwable) {
        if (this.repository.isDisabled(50000)) {
            return;
        }
        if (Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.FATAL, object, throwable);
        }
    }

    protected void forcedLog(String string, Priority priority, Object object, Throwable throwable) {
        this.callAppenders(new LoggingEvent(string, this, priority, object, throwable));
    }

    public boolean getAdditivity() {
        return this.additive;
    }

    public synchronized Enumeration getAllAppenders() {
        if (this.aai == null) {
            return NullEnumeration.getInstance();
        }
        return this.aai.getAllAppenders();
    }

    public synchronized Appender getAppender(String string) {
        if (this.aai == null || string == null) {
            return null;
        }
        return this.aai.getAppender(string);
    }

    public Level getEffectiveLevel() {
        Category category = this;
        while (category != null) {
            if (category.level != null) {
                return category.level;
            }
            category = category.parent;
        }
        return null;
    }

    public Priority getChainedPriority() {
        Category category = this;
        while (category != null) {
            if (category.level != null) {
                return category.level;
            }
            category = category.parent;
        }
        return null;
    }

    public static Enumeration getCurrentCategories() {
        return LogManager.getCurrentLoggers();
    }

    public static LoggerRepository getDefaultHierarchy() {
        return LogManager.getLoggerRepository();
    }

    public LoggerRepository getHierarchy() {
        return this.repository;
    }

    public LoggerRepository getLoggerRepository() {
        return this.repository;
    }

    public static Category getInstance(String string) {
        return LogManager.getLogger(string);
    }

    public static Category getInstance(Class class_) {
        return LogManager.getLogger(class_);
    }

    public final String getName() {
        return this.name;
    }

    public final Category getParent() {
        return this.parent;
    }

    public final Level getLevel() {
        return this.level;
    }

    public final Level getPriority() {
        return this.level;
    }

    public static final Category getRoot() {
        return LogManager.getRootLogger();
    }

    public ResourceBundle getResourceBundle() {
        Category category = this;
        while (category != null) {
            if (category.resourceBundle != null) {
                return category.resourceBundle;
            }
            category = category.parent;
        }
        return null;
    }

    protected String getResourceBundleString(String string) {
        ResourceBundle resourceBundle = this.getResourceBundle();
        if (resourceBundle == null) {
            return null;
        }
        try {
            return resourceBundle.getString(string);
        }
        catch (MissingResourceException missingResourceException) {
            this.error("No resource is associated with key \"" + string + "\".");
            return null;
        }
    }

    public void info(Object object) {
        if (this.repository.isDisabled(20000)) {
            return;
        }
        if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.INFO, object, null);
        }
    }

    public void info(Object object, Throwable throwable) {
        if (this.repository.isDisabled(20000)) {
            return;
        }
        if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.INFO, object, throwable);
        }
    }

    public boolean isAttached(Appender appender) {
        if (appender == null || this.aai == null) {
            return false;
        }
        return this.aai.isAttached(appender);
    }

    public boolean isDebugEnabled() {
        if (this.repository.isDisabled(10000)) {
            return false;
        }
        return Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel());
    }

    public boolean isEnabledFor(Priority priority) {
        if (this.repository.isDisabled(priority.level)) {
            return false;
        }
        return priority.isGreaterOrEqual(this.getEffectiveLevel());
    }

    public boolean isInfoEnabled() {
        if (this.repository.isDisabled(20000)) {
            return false;
        }
        return Level.INFO.isGreaterOrEqual(this.getEffectiveLevel());
    }

    public void l7dlog(Priority priority, String string, Throwable throwable) {
        if (this.repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            String string2 = this.getResourceBundleString(string);
            if (string2 == null) {
                string2 = string;
            }
            this.forcedLog(FQCN, priority, string2, throwable);
        }
    }

    public void l7dlog(Priority priority, String string, Object[] arrobject, Throwable throwable) {
        if (this.repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            String string2 = this.getResourceBundleString(string);
            String string3 = string2 == null ? string : MessageFormat.format(string2, arrobject);
            this.forcedLog(FQCN, priority, string3, throwable);
        }
    }

    public void log(Priority priority, Object object, Throwable throwable) {
        if (this.repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, priority, object, throwable);
        }
    }

    public void log(Priority priority, Object object) {
        if (this.repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, priority, object, null);
        }
    }

    public void log(String string, Priority priority, Object object, Throwable throwable) {
        if (this.repository.isDisabled(priority.level)) {
            return;
        }
        if (priority.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(string, priority, object, throwable);
        }
    }

    public synchronized void removeAllAppenders() {
        if (this.aai != null) {
            this.aai.removeAllAppenders();
            this.aai = null;
        }
    }

    public synchronized void removeAppender(Appender appender) {
        if (appender == null || this.aai == null) {
            return;
        }
        this.aai.removeAppender(appender);
    }

    public synchronized void removeAppender(String string) {
        if (string == null || this.aai == null) {
            return;
        }
        this.aai.removeAppender(string);
    }

    public void setAdditivity(boolean bl) {
        this.additive = bl;
    }

    final void setHierarchy(LoggerRepository loggerRepository) {
        this.repository = loggerRepository;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setPriority(Priority priority) {
        this.level = (Level)priority;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public static void shutdown() {
        LogManager.shutdown();
    }

    public void warn(Object object) {
        if (this.repository.isDisabled(30000)) {
            return;
        }
        if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.WARN, object, null);
        }
    }

    public void warn(Object object, Throwable throwable) {
        if (this.repository.isDisabled(30000)) {
            return;
        }
        if (Level.WARN.isGreaterOrEqual(this.getEffectiveLevel())) {
            this.forcedLog(FQCN, Level.WARN, object, throwable);
        }
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

