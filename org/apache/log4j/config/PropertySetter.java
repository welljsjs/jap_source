/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.config;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.config.PropertySetterException;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.OptionHandler;

public class PropertySetter {
    protected Object obj;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$log4j$Priority;

    public PropertySetter(Object object) {
        this.obj = object;
    }

    public static void setProperties(Object object, Properties properties, String string) {
        new PropertySetter(object).setProperties(properties, string);
    }

    public void setProperties(Properties properties, String string) {
        int n = string.length();
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String string2 = (String)enumeration.nextElement();
            if (!string2.startsWith(string) || string2.indexOf(46, n + 1) > 0) continue;
            String string3 = OptionConverter.findAndSubst(string2, properties);
            if ("layout".equals(string2 = string2.substring(n)) && this.obj instanceof Appender) continue;
            this.setProperty(string2, string3);
        }
        this.activate();
    }

    public void setProperty(String string, String string2) {
        if (string2 == null) {
            return;
        }
        Method method = this.getSetterMethod(string);
        if (method == null) {
            LogLog.warn("No such property [" + string + "] in " + this.obj.getClass().getName() + ".");
        } else {
            try {
                this.setProperty(method, string, string2);
            }
            catch (PropertySetterException propertySetterException) {
                LogLog.warn("Failed to set property [" + string + "] to value \"" + string2 + "\". ", propertySetterException.rootCause);
            }
        }
    }

    private Method getSetterMethod(String string) {
        Class<?> class_ = this.obj.getClass();
        Method[] arrmethod = class_.getMethods();
        for (int i = 0; i < arrmethod.length; ++i) {
            if (!arrmethod[i].getName().equals("set" + string) || arrmethod[i].getParameterTypes().length != 1) continue;
            return arrmethod[i];
        }
        return null;
    }

    public void setProperty(Method method, String string, String string2) throws PropertySetterException {
        Object object;
        if (method == null) {
            throw new PropertySetterException("No setter for property [" + string + "].");
        }
        Class<?>[] arrclass = method.getParameterTypes();
        if (arrclass.length != 1) {
            throw new PropertySetterException("#params for setter != 1");
        }
        try {
            object = this.convertArg(string2, arrclass[0]);
        }
        catch (Throwable throwable) {
            throw new PropertySetterException("Conversion to type [" + arrclass[0] + "] failed. Reason: " + throwable);
        }
        if (object == null) {
            throw new PropertySetterException("Conversion to type [" + arrclass[0] + "] failed.");
        }
        LogLog.debug("Setting property [" + string + "] to [" + object + "].");
        try {
            method.invoke(this.obj, object);
        }
        catch (Exception exception) {
            throw new PropertySetterException(exception);
        }
    }

    protected Object convertArg(String string, Class class_) {
        if (string == null) {
            return null;
        }
        String string2 = string.trim();
        if ((class$java$lang$String == null ? (class$java$lang$String = PropertySetter.class$("java.lang.String")) : class$java$lang$String).isAssignableFrom(class_)) {
            return string;
        }
        if (Integer.TYPE.isAssignableFrom(class_)) {
            return new Integer(string2);
        }
        if (Long.TYPE.isAssignableFrom(class_)) {
            return new Long(string2);
        }
        if (Boolean.TYPE.isAssignableFrom(class_)) {
            if ("true".equalsIgnoreCase(string2)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(string2)) {
                return Boolean.FALSE;
            }
        } else if ((class$org$apache$log4j$Priority == null ? (class$org$apache$log4j$Priority = PropertySetter.class$("org.apache.log4j.Priority")) : class$org$apache$log4j$Priority).isAssignableFrom(class_)) {
            return OptionConverter.toLevel(string2, Level.DEBUG);
        }
        return null;
    }

    public void activate() {
        if (this.obj instanceof OptionHandler) {
            ((OptionHandler)this.obj).activateOptions();
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

