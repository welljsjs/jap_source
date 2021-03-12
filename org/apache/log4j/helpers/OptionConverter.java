/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;

public class OptionConverter {
    static String DELIM_START = "${";
    static char DELIM_STOP = (char)125;
    static int DELIM_START_LEN = 2;
    static int DELIM_STOP_LEN = 1;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$log4j$Level;
    static /* synthetic */ Class class$org$apache$log4j$spi$Configurator;

    private OptionConverter() {
    }

    public static String[] concatanateArrays(String[] arrstring, String[] arrstring2) {
        int n = arrstring.length + arrstring2.length;
        String[] arrstring3 = new String[n];
        System.arraycopy(arrstring, 0, arrstring3, 0, arrstring.length);
        System.arraycopy(arrstring2, 0, arrstring3, arrstring.length, arrstring2.length);
        return arrstring3;
    }

    public static String convertSpecialChars(String string) {
        int n = string.length();
        StringBuffer stringBuffer = new StringBuffer(n);
        int n2 = 0;
        while (n2 < n) {
            int n3;
            if ((n3 = string.charAt(n2++)) == 92) {
                if ((n3 = string.charAt(n2++)) == 110) {
                    n3 = 10;
                } else if (n3 == 114) {
                    n3 = 13;
                } else if (n3 == 116) {
                    n3 = 9;
                } else if (n3 == 102) {
                    n3 = 12;
                } else if (n3 == 8) {
                    n3 = 8;
                } else if (n3 == 34) {
                    n3 = 34;
                } else if (n3 == 39) {
                    n3 = 39;
                } else if (n3 == 92) {
                    n3 = 92;
                }
            }
            stringBuffer.append((char)n3);
        }
        return stringBuffer.toString();
    }

    public static String getSystemProperty(String string, String string2) {
        try {
            return System.getProperty(string, string2);
        }
        catch (Throwable throwable) {
            LogLog.debug("Was not allowed to read system property \"" + string + "\".");
            return string2;
        }
    }

    public static Object instantiateByKey(Properties properties, String string, Class class_, Object object) {
        String string2 = OptionConverter.findAndSubst(string, properties);
        if (string2 == null) {
            LogLog.error("Could not find value for key " + string);
            return object;
        }
        return OptionConverter.instantiateByClassName(string2.trim(), class_, object);
    }

    public static boolean toBoolean(String string, boolean bl) {
        if (string == null) {
            return bl;
        }
        String string2 = string.trim();
        if ("true".equalsIgnoreCase(string2)) {
            return true;
        }
        if ("false".equalsIgnoreCase(string2)) {
            return false;
        }
        return bl;
    }

    public static int toInt(String string, int n) {
        if (string != null) {
            String string2 = string.trim();
            try {
                return Integer.valueOf(string2);
            }
            catch (NumberFormatException numberFormatException) {
                LogLog.error("[" + string2 + "] is not in proper int form.");
                numberFormatException.printStackTrace();
            }
        }
        return n;
    }

    public static Level toLevel(String string, Level level) {
        if (string == null) {
            return level;
        }
        int n = string.indexOf(35);
        if (n == -1) {
            if ("NULL".equalsIgnoreCase(string)) {
                return null;
            }
            return Level.toLevel(string, level);
        }
        Level level2 = level;
        String string2 = string.substring(n + 1);
        String string3 = string.substring(0, n);
        if ("NULL".equalsIgnoreCase(string3)) {
            return null;
        }
        LogLog.debug("toLevel:class=[" + string2 + "]" + ":pri=[" + string3 + "]");
        try {
            Class class_ = Loader.loadClass(string2);
            Class[] arrclass = new Class[]{class$java$lang$String == null ? (class$java$lang$String = OptionConverter.class$("java.lang.String")) : class$java$lang$String, class$org$apache$log4j$Level == null ? (class$org$apache$log4j$Level = OptionConverter.class$("org.apache.log4j.Level")) : class$org$apache$log4j$Level};
            Method method = class_.getMethod("toLevel", arrclass);
            Object[] arrobject = new Object[]{string3, level};
            Object object = method.invoke(null, arrobject);
            level2 = (Level)object;
        }
        catch (ClassNotFoundException classNotFoundException) {
            LogLog.warn("custom level class [" + string2 + "] not found.");
        }
        catch (NoSuchMethodException noSuchMethodException) {
            LogLog.warn("custom level class [" + string2 + "]" + " does not have a constructor which takes one string parameter", noSuchMethodException);
        }
        catch (InvocationTargetException invocationTargetException) {
            LogLog.warn("custom level class [" + string2 + "]" + " could not be instantiated", invocationTargetException);
        }
        catch (ClassCastException classCastException) {
            LogLog.warn("class [" + string2 + "] is not a subclass of org.apache.log4j.Level", classCastException);
        }
        catch (IllegalAccessException illegalAccessException) {
            LogLog.warn("class [" + string2 + "] cannot be instantiated due to access restrictions", illegalAccessException);
        }
        catch (Exception exception) {
            LogLog.warn("class [" + string2 + "], level [" + string3 + "] conversion failed.", exception);
        }
        return level2;
    }

    public static long toFileSize(String string, long l) {
        if (string == null) {
            return l;
        }
        String string2 = string.trim().toUpperCase();
        long l2 = 1L;
        int n = string2.indexOf("KB");
        if (n != -1) {
            l2 = 1024L;
            string2 = string2.substring(0, n);
        } else {
            n = string2.indexOf("MB");
            if (n != -1) {
                l2 = 0x100000L;
                string2 = string2.substring(0, n);
            } else {
                n = string2.indexOf("GB");
                if (n != -1) {
                    l2 = 0x40000000L;
                    string2 = string2.substring(0, n);
                }
            }
        }
        if (string2 != null) {
            try {
                return Long.valueOf(string2) * l2;
            }
            catch (NumberFormatException numberFormatException) {
                LogLog.error("[" + string2 + "] is not in proper int form.");
                LogLog.error("[" + string + "] not in expected format.", numberFormatException);
            }
        }
        return l;
    }

    public static String findAndSubst(String string, Properties properties) {
        String string2 = properties.getProperty(string);
        if (string2 == null) {
            return null;
        }
        try {
            return OptionConverter.substVars(string2, properties);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            LogLog.error("Bad option value [" + string2 + "].", illegalArgumentException);
            return string2;
        }
    }

    public static Object instantiateByClassName(String string, Class class_, Object object) {
        if (string != null) {
            try {
                Class class_2 = Loader.loadClass(string);
                if (!class_.isAssignableFrom(class_2)) {
                    LogLog.error("A \"" + string + "\" object is not assignable to a \"" + class_.getName() + "\" variable.");
                    LogLog.error("The class \"" + class_.getName() + "\" was loaded by ");
                    LogLog.error("[" + class_.getClassLoader() + "] whereas object of type ");
                    LogLog.error("\"" + class_2.getName() + "\" was loaded by [" + class_2.getClassLoader() + "].");
                    return object;
                }
                return class_2.newInstance();
            }
            catch (Exception exception) {
                LogLog.error("Could not instantiate class [" + string + "].", exception);
            }
        }
        return object;
    }

    public static String substVars(String string, Properties properties) throws IllegalArgumentException {
        StringBuffer stringBuffer = new StringBuffer();
        int n = 0;
        while (true) {
            int n2;
            if ((n2 = string.indexOf(DELIM_START, n)) == -1) {
                if (n == 0) {
                    return string;
                }
                stringBuffer.append(string.substring(n, string.length()));
                return stringBuffer.toString();
            }
            stringBuffer.append(string.substring(n, n2));
            int n3 = string.indexOf(DELIM_STOP, n2);
            if (n3 == -1) {
                throw new IllegalArgumentException('\"' + string + "\" has no closing brace. Opening brace at position " + n2 + '.');
            }
            String string2 = string.substring(n2 += DELIM_START_LEN, n3);
            String string3 = OptionConverter.getSystemProperty(string2, null);
            if (string3 == null && properties != null) {
                string3 = properties.getProperty(string2);
            }
            if (string3 != null) {
                String string4 = OptionConverter.substVars(string3, properties);
                stringBuffer.append(string4);
            }
            n = n3 + DELIM_STOP_LEN;
        }
    }

    public static void selectAndConfigure(URL uRL, String string, LoggerRepository loggerRepository) {
        Configurator configurator = null;
        String string2 = uRL.getFile();
        if (string == null && string2 != null && string2.endsWith(".xml")) {
            string = "org.apache.log4j.xml.DOMConfigurator";
        }
        if (string != null) {
            LogLog.debug("Preferred configurator class: " + string);
            configurator = (Configurator)OptionConverter.instantiateByClassName(string, class$org$apache$log4j$spi$Configurator == null ? (class$org$apache$log4j$spi$Configurator = OptionConverter.class$("org.apache.log4j.spi.Configurator")) : class$org$apache$log4j$spi$Configurator, null);
            if (configurator == null) {
                LogLog.error("Could not instantiate configurator [" + string + "].");
                return;
            }
        } else {
            configurator = new PropertyConfigurator();
        }
        configurator.doConfigure(uRL, loggerRepository);
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

