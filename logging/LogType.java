/*
 * Decompiled with CFR 0.150.
 */
package logging;

public final class LogType {
    private static final String[] STR_LOG_TYPES = new String[]{"NUL", "GUI", "NET", "MISC", "PAY", "TOR", "CRYPTO", "FILTER", "AGREEMENT", "DB", "TRANSPORT", "FORWARDING", "ALL"};
    private static final String STR_ADD_LOG_TYPE = "+";
    private static final int[] LOG_TYPES = LogType.getAvailableLogTypes();
    public static final int NUL = LOG_TYPES[0];
    public static final int GUI = LOG_TYPES[1];
    public static final int NET = LOG_TYPES[2];
    public static final int MISC = LOG_TYPES[3];
    public static final int PAY = LOG_TYPES[4];
    public static final int TOR = LOG_TYPES[5];
    public static final int CRYPTO = LOG_TYPES[6];
    public static final int FILTER = LOG_TYPES[7];
    public static final int AGREEMENT = LOG_TYPES[8];
    public static final int DB = LOG_TYPES[9];
    public static final int TRANSPORT = LOG_TYPES[10];
    public static final int FORWARDING = LOG_TYPES[11];
    public static final int ALL = LogType.createLogTypeALL();

    private LogType() {
    }

    public static boolean isValidLogType(int n) {
        return n >= 0 && n <= ALL;
    }

    public static int[] getAvailableLogTypes() {
        int[] arrn = new int[STR_LOG_TYPES.length - 1];
        arrn[0] = 0;
        int n = 1;
        for (int i = 1; i < arrn.length; ++i) {
            arrn[i] = n;
            n <<= 1;
        }
        return arrn;
    }

    public static int getNumberOfLogTypes() {
        return STR_LOG_TYPES.length - 1;
    }

    public static String getLogTypeName(int n) {
        String string = "";
        if (n == 0) {
            string = STR_LOG_TYPES[0];
        } else if ((n & ALL) == ALL) {
            string = STR_LOG_TYPES[STR_LOG_TYPES.length - 1];
        } else {
            for (int i = 1; i < LOG_TYPES.length; ++i) {
                if ((n & LOG_TYPES[i]) <= 0) continue;
                string = string + STR_LOG_TYPES[i] + STR_ADD_LOG_TYPE;
            }
            string = string.length() == 0 ? STR_LOG_TYPES[0] : string.substring(0, string.length() - STR_ADD_LOG_TYPE.length());
        }
        return string;
    }

    private static int createLogTypeALL() {
        int n = 0;
        for (int i = 0; i < LOG_TYPES.length; ++i) {
            n += LOG_TYPES[i];
        }
        return n;
    }
}

