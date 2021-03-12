/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.ResourceLoader;
import anon.util.Util;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import logging.LogHolder;
import logging.LogType;

public final class JAPMessages {
    private static ResourceBundle ms_resourceBundle = null;
    private static ResourceBundle ms_defaultResourceBundle = null;
    private static Locale ms_locale;
    private static final Locale SYSTEM_LOCALE;
    private static Hashtable ms_cachedMessages;
    static /* synthetic */ Class class$java$util$PropertyResourceBundle;

    private JAPMessages() {
    }

    public static Locale getSystemLocale() {
        return SYSTEM_LOCALE;
    }

    public static boolean init(String string) {
        return JAPMessages.init(Locale.getDefault(), string);
    }

    private static String getBundleLocalisedFilename(String string, Locale locale) {
        String string2 = "_";
        if (string == null) {
            return null;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        string2 = locale == null || locale.getLanguage().trim().length() == 0 ? string2 + "en" : string2 + locale.getLanguage();
        string2 = string2 + ".properties";
        return string + string2;
    }

    public static synchronized boolean init(Locale locale, String string) {
        Exception exception = null;
        if (ms_locale != null) {
            Locale.setDefault(Locale.ENGLISH);
        }
        try {
            if (ms_defaultResourceBundle == null) {
                ms_defaultResourceBundle = ResourceBundle.getBundle(string, Locale.ENGLISH);
            }
        }
        catch (Exception exception2) {
            LogHolder.log(0, LogType.GUI, exception2);
            return false;
        }
        ms_resourceBundle = ms_defaultResourceBundle;
        InputStream inputStream = null;
        try {
            inputStream = ResourceLoader.loadResourceAsStream(JAPMessages.getBundleLocalisedFilename(string, locale), true);
            if (inputStream != null) {
                ms_resourceBundle = new PropertyResourceBundle(inputStream);
            }
        }
        catch (Exception exception3) {
            exception = exception3;
        }
        Util.closeStream(inputStream);
        if (inputStream == null) {
            try {
                ms_resourceBundle = ResourceBundle.getBundle(string, locale);
            }
            catch (Exception exception4) {
                try {
                    if (locale != null && locale.equals(Locale.getDefault())) {
                        if (exception != null) {
                            throw exception;
                        }
                        throw exception4;
                    }
                    locale = Locale.getDefault();
                    ms_resourceBundle = ResourceBundle.getBundle(string, locale);
                }
                catch (Exception exception5) {
                    LogHolder.log(3, LogType.MISC, exception5);
                }
            }
        }
        ms_cachedMessages = new Hashtable();
        ms_locale = locale;
        return true;
    }

    public static boolean isInitialised() {
        return ms_locale != null;
    }

    public static Locale getLocale() {
        if (ms_locale == null) {
            return Locale.getDefault();
        }
        return ms_locale;
    }

    public static void setLocale(Locale locale) {
        if (locale != null) {
            ms_locale = locale;
        }
    }

    public static String getString(String string) {
        String string2;
        block8: {
            if (ms_cachedMessages == null) {
                return string;
            }
            string2 = (String)ms_cachedMessages.get(string);
            if (string2 != null) {
                return string2;
            }
            try {
                string2 = ms_resourceBundle.getString(string);
                if (string2 == null || string2.trim().length() == 0) {
                    throw new MissingResourceException("Resource is empty", (class$java$util$PropertyResourceBundle == null ? (class$java$util$PropertyResourceBundle = JAPMessages.class$("java.util.PropertyResourceBundle")) : class$java$util$PropertyResourceBundle).getName(), string);
                }
            }
            catch (Exception exception) {
                try {
                    if (ms_resourceBundle != ms_defaultResourceBundle) {
                        string2 = ms_defaultResourceBundle.getString(string);
                        LogHolder.log(7, LogType.GUI, "Could not load message string '" + string + "' for the locale '" + ms_locale.getLanguage() + "'. Using default resource bundle.", 1);
                    }
                }
                catch (Exception exception2) {
                    string2 = null;
                }
                if (string2 != null && string2.trim().length() != 0) break block8;
                LogHolder.log(7, LogType.GUI, "Could not load messsage string: " + string, 1);
                string2 = string;
            }
        }
        ms_cachedMessages.put(string, string2);
        return string2;
    }

    public static String getString(String string, Object[] arrobject) {
        return MessageFormat.format(JAPMessages.getString(string), arrobject);
    }

    public static String getString(String string, Object object) {
        return JAPMessages.getString(string, Util.toArray(object));
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        SYSTEM_LOCALE = Locale.getDefault();
    }
}

