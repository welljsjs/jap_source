/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Hashtable;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;
import org.apache.log4j.spi.ThrowableInformation;

public class LoggingEvent
implements Serializable {
    private static long startTime = System.currentTimeMillis();
    public final transient String fqnOfCategoryClass;
    private transient Category logger;
    public final String categoryName;
    public transient Priority level;
    private String ndc;
    private Hashtable mdcCopy;
    private boolean ndcLookupRequired = true;
    private boolean mdcCopyLookupRequired = true;
    private transient Object message;
    private String renderedMessage;
    private String threadName;
    private ThrowableInformation throwableInfo;
    public final long timeStamp;
    private LocationInfo locationInfo;
    static final long serialVersionUID = -868428216207166145L;
    static final Integer[] PARAM_ARRAY = new Integer[1];
    static final String TO_LEVEL = "toLevel";
    static final Class[] TO_LEVEL_PARAMS = new Class[]{Integer.TYPE};
    static final Hashtable methodCache = new Hashtable(3);
    static /* synthetic */ Class class$org$apache$log4j$Level;

    public LoggingEvent(String string, Category category, Priority priority, Object object, Throwable throwable) {
        this.fqnOfCategoryClass = string;
        this.logger = category;
        this.categoryName = category.getName();
        this.level = priority;
        this.message = object;
        if (throwable != null) {
            this.throwableInfo = new ThrowableInformation(throwable);
        }
        this.timeStamp = System.currentTimeMillis();
    }

    public LoggingEvent(String string, Category category, long l, Priority priority, Object object, Throwable throwable) {
        this.fqnOfCategoryClass = string;
        this.logger = category;
        this.categoryName = category.getName();
        this.level = priority;
        this.message = object;
        if (throwable != null) {
            this.throwableInfo = new ThrowableInformation(throwable);
        }
        this.timeStamp = l;
    }

    public LocationInfo getLocationInformation() {
        if (this.locationInfo == null) {
            this.locationInfo = new LocationInfo(new Throwable(), this.fqnOfCategoryClass);
        }
        return this.locationInfo;
    }

    public Level getLevel() {
        return (Level)this.level;
    }

    public String getLoggerName() {
        return this.categoryName;
    }

    public Object getMessage() {
        if (this.message != null) {
            return this.message;
        }
        return this.getRenderedMessage();
    }

    public String getNDC() {
        if (this.ndcLookupRequired) {
            this.ndcLookupRequired = false;
            this.ndc = NDC.get();
        }
        return this.ndc;
    }

    public Object getMDC(String string) {
        Object v;
        if (this.mdcCopy != null && (v = this.mdcCopy.get(string)) != null) {
            return v;
        }
        return MDC.get(string);
    }

    public void getMDCCopy() {
        if (this.mdcCopyLookupRequired) {
            this.mdcCopyLookupRequired = false;
            Hashtable hashtable = MDC.getContext();
            if (hashtable != null) {
                this.mdcCopy = (Hashtable)hashtable.clone();
            }
        }
    }

    public String getRenderedMessage() {
        if (this.renderedMessage == null && this.message != null) {
            if (this.message instanceof String) {
                this.renderedMessage = (String)this.message;
            } else {
                LoggerRepository loggerRepository = this.logger.getLoggerRepository();
                if (loggerRepository instanceof RendererSupport) {
                    RendererSupport rendererSupport = (RendererSupport)((Object)loggerRepository);
                    this.renderedMessage = rendererSupport.getRendererMap().findAndRender(this.message);
                } else {
                    this.renderedMessage = this.message.toString();
                }
            }
        }
        return this.renderedMessage;
    }

    public static long getStartTime() {
        return startTime;
    }

    public String getThreadName() {
        if (this.threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }
        return this.threadName;
    }

    public ThrowableInformation getThrowableInformation() {
        return this.throwableInfo;
    }

    public String[] getThrowableStrRep() {
        if (this.throwableInfo == null) {
            return null;
        }
        return this.throwableInfo.getThrowableStrRep();
    }

    private void readLevel(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        int n = objectInputStream.readInt();
        try {
            String string = (String)objectInputStream.readObject();
            if (string == null) {
                this.level = Level.toLevel(n);
            } else {
                Method method = (Method)methodCache.get(string);
                if (method == null) {
                    Class class_ = Loader.loadClass(string);
                    method = class_.getDeclaredMethod(TO_LEVEL, TO_LEVEL_PARAMS);
                    methodCache.put(string, method);
                }
                LoggingEvent.PARAM_ARRAY[0] = new Integer(n);
                this.level = (Level)method.invoke(null, PARAM_ARRAY);
            }
        }
        catch (Exception exception) {
            LogLog.warn("Level deserialization failed, reverting to default.", exception);
            this.level = Level.toLevel(n);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.readLevel(objectInputStream);
        if (this.locationInfo == null) {
            this.locationInfo = new LocationInfo(null, null);
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        this.getThreadName();
        this.getRenderedMessage();
        this.getNDC();
        this.getMDCCopy();
        this.getThrowableStrRep();
        objectOutputStream.defaultWriteObject();
        this.writeLevel(objectOutputStream);
    }

    private void writeLevel(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(this.level.toInt());
        Class<?> class_ = this.level.getClass();
        if (class_ == (class$org$apache$log4j$Level == null ? (class$org$apache$log4j$Level = LoggingEvent.class$("org.apache.log4j.Level")) : class$org$apache$log4j$Level)) {
            objectOutputStream.writeObject(null);
        } else {
            objectOutputStream.writeObject(class_.getName());
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

