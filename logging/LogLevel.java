/*
 * Decompiled with CFR 0.150.
 */
package logging;

public final class LogLevel {
    public static final int EMERG = 0;
    public static final int ALERT = 1;
    public static final int EXCEPTION = 2;
    public static final int ERR = 3;
    public static final int WARNING = 4;
    public static final int NOTICE = 5;
    public static final int INFO = 6;
    public static final int DEBUG = 7;
    private static final String[] STR_Levels = new String[]{"Emergency", "Alert    ", "Exception", "Error    ", "Warning  ", "Notice   ", "Info     ", "Debug    "};

    public static int getLevelCount() {
        return STR_Levels.length;
    }

    public static String getLevelName(int n) {
        if (n < 0 || n > STR_Levels.length - 1) {
            return null;
        }
        return STR_Levels[n];
    }
}

