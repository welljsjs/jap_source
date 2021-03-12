/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.ModuleException;
import anon.infoservice.HTTPConnectionFactory;
import anon.infoservice.HttpRequestStructure;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.InfoServiceDBEntry;
import anon.infoservice.ListenerInterface;
import anon.util.Base64;
import anon.util.IProgressCallback;
import anon.util.JAPMessages;
import anon.util.ZLibTools;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public final class Util {
    public static final String VERSION_FORMAT = "00.00.000";
    private static final String WHITESPACE_ENCODED = "%20";
    private static final String WHITESPACE = " ";
    public static final int MAX_FORMAT_BYTES = 0;
    public static final int MAX_FORMAT_KBYTES = 1;
    public static final int MAX_FORMAT_MBYTES = 2;
    public static final int MAX_FORMAT_GBYTES = 3;
    public static final int MAX_FORMAT_KBIT_PER_SEC = 0;
    public static final int MAX_FORMAT_MBIT_PER_SEC = 1;
    public static final int MAX_FORMAT_GBIT_PER_SEC = 2;
    public static final int MAX_FORMAT_ALL = 4;

    private Util() {
    }

    public static boolean assertNotNull(Object object, Object object2) {
        return object != null && object2 != null;
    }

    public static boolean equals(Object object, Object object2) {
        return object == object2 || object != null && object2 != null && object.equals(object2);
    }

    public static String cutString(String string, int n) {
        if (string != null && string.length() > n) {
            string = string.substring(0, n).trim();
        }
        return string;
    }

    public static String stripString(String string, String string2) {
        if (string == null || string2 == null) {
            return null;
        }
        String string3 = "";
        StringTokenizer stringTokenizer = new StringTokenizer(string, string2);
        while (stringTokenizer.hasMoreTokens()) {
            string3 = string3 + stringTokenizer.nextToken().trim();
        }
        return string3;
    }

    public static String decodeString(String string) {
        String string2 = string;
        try {
            byte[] arrby = Base64.decode(string);
            if (arrby != null) {
                string2 = new String(ZLibTools.decompress(arrby));
            }
        }
        catch (Exception exception) {
            LogHolder.log(1, LogType.MISC, exception);
        }
        return string2;
    }

    public static boolean arraysEqual(byte[] arrby, byte[] arrby2) {
        if (arrby == null && arrby2 == null) {
            return true;
        }
        if (arrby == null || arrby2 == null) {
            return false;
        }
        if (arrby.length != arrby2.length) {
            return false;
        }
        for (int i = 0; i < arrby.length; ++i) {
            if (arrby[i] == arrby2[i]) continue;
            return false;
        }
        return true;
    }

    public static boolean arraysEqual(char[] arrc, char[] arrc2) {
        if (arrc == null && arrc2 == null) {
            return true;
        }
        if (arrc == null || arrc2 == null) {
            return false;
        }
        if (arrc.length != arrc2.length) {
            return false;
        }
        for (int i = 0; i < arrc.length; ++i) {
            if (arrc[i] == arrc2[i]) continue;
            return false;
        }
        return true;
    }

    public static final boolean arraysEqual(byte[] arrby, int n, byte[] arrby2, int n2, int n3) {
        if (n3 <= 0) {
            return true;
        }
        if (arrby == null || arrby2 == null || n < 0 || n2 < 0) {
            return false;
        }
        if (n + n3 > arrby.length || n2 + n3 > arrby2.length) {
            return false;
        }
        for (int i = 0; i < n3; ++i) {
            if (arrby[n + i] == arrby2[n2 + i]) continue;
            return false;
        }
        return true;
    }

    public static Vector toVector(Object object) {
        Vector<Object> vector = new Vector<Object>();
        if (object != null) {
            vector.addElement(object);
        }
        return vector;
    }

    public static Object[] toArray(Object object) {
        Object[] arrobject = object != null ? new Object[]{object} : new Object[]{};
        return arrobject;
    }

    private static void swap(String[] arrstring, String[] arrstring2, int n, int n2) {
        String string = arrstring[n];
        arrstring[n] = arrstring[n2];
        arrstring[n2] = string;
        string = arrstring2[n];
        arrstring2[n] = arrstring2[n2];
        arrstring2[n2] = string;
    }

    public static Vector sortStrings(Vector vector) {
        int n;
        Vector vector2 = new Vector();
        String[] arrstring = new String[vector.size()];
        int[] arrn = new int[vector.size()];
        String[] arrstring2 = new String[2];
        for (n = 0; n < arrstring.length; ++n) {
            int n2;
            arrstring[n] = vector.elementAt(n).toString().toLowerCase();
            arrn[n] = n;
            boolean bl = false;
            for (n2 = 0; n2 < arrstring2.length && n2 < arrstring[n].length(); ++n2) {
                if (!Util.isUmlaut(arrstring[n].charAt(n2), arrstring2, n2)) continue;
                bl = true;
            }
            if (!bl) continue;
            String string = "";
            for (n2 = 0; n2 < arrstring2.length && n2 < arrstring[n].length(); ++n2) {
                string = arrstring2[n2] == null ? string + arrstring[n].charAt(n2) : string + arrstring2[n2];
            }
            if (n2 < arrstring[n].length()) {
                string = string + arrstring[n].substring(n2, arrstring[n].length());
            }
            arrstring[n] = string;
        }
        Util.bubbleSortStrings(vector, arrstring, arrn);
        for (n = 0; n < arrstring.length; ++n) {
            vector2.addElement(vector.elementAt(arrn[n]));
        }
        return vector2;
    }

    public static double parseDouble(String string) throws NumberFormatException {
        int n = 0;
        int n2 = 0;
        int n3 = 1;
        boolean bl = true;
        int n4 = 1;
        if (string == null) {
            throw new NumberFormatException("NULL cannot be parsed as float!");
        }
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (Character.isDigit(c)) {
                if (bl) {
                    n = n * 10 + (c - 48);
                    continue;
                }
                n3 *= 10;
                n2 = n2 * 10 + (c - 48);
                continue;
            }
            if (bl && (c == '.' || c == ',') && string.length() > 1) {
                bl = false;
                continue;
            }
            if (c == '+') continue;
            if (c == '-' && i == 0) {
                n4 = -1;
                continue;
            }
            throw new NumberFormatException("No valid float value '" + string + "'!");
        }
        double d = ((double)n + (double)n2 / (double)n3) * (double)n4;
        return d;
    }

    public static void sort(String[] arrstring, String[] arrstring2) {
        Util.quicksort(arrstring, arrstring2, 0, arrstring.length - 1);
    }

    private static int divide(String[] arrstring, String[] arrstring2, int n, int n2) {
        int n3 = n;
        for (int i = n; i < n2; ++i) {
            if (arrstring[i].compareTo(arrstring[n2]) > 0) continue;
            Util.swap(arrstring, arrstring2, n3, i);
            ++n3;
        }
        Util.swap(arrstring, arrstring2, n3, n2);
        return n3;
    }

    private static void quicksort(String[] arrstring, String[] arrstring2, int n, int n2) {
        if (n2 > n) {
            int n3 = Util.divide(arrstring, arrstring2, n, n2);
            Util.quicksort(arrstring, arrstring2, n, n3 - 1);
            Util.quicksort(arrstring, arrstring2, n3 + 1, n2);
        }
    }

    public static void sort(Vector vector, Comparable comparable) {
        if (vector != null) {
            Util.quicksort(vector, 0, vector.size() - 1, comparable);
        }
    }

    private static int divide(Vector vector, int n, int n2, Comparable comparable) {
        int n3 = n;
        for (int i = n; i < n2; ++i) {
            if (comparable.compare(vector.elementAt(i), vector.elementAt(n2)) > 0) continue;
            Util.swap(vector, n3, i);
            ++n3;
        }
        Util.swap(vector, n3, n2);
        return n3;
    }

    private static void quicksort(Vector vector, int n, int n2, Comparable comparable) {
        if (n2 > n) {
            int n3 = Util.divide(vector, n, n2, comparable);
            Util.quicksort(vector, n, n3 - 1, comparable);
            Util.quicksort(vector, n3 + 1, n2, comparable);
        }
    }

    private static void swap(Vector vector, int n, int n2) {
        Object e = vector.elementAt(n);
        vector.setElementAt(vector.elementAt(n2), n);
        vector.setElementAt(e, n2);
    }

    private static void bubbleSortStrings(Vector vector, String[] arrstring, int[] arrn) {
        for (int i = 1; i <= vector.size(); ++i) {
            for (int j = vector.size() - 1; j > i; --j) {
                if (arrstring[j].compareTo(arrstring[j - 1]) >= 0) continue;
                String string = arrstring[j];
                int n = arrn[j];
                arrstring[j] = arrstring[j - 1];
                arrn[j] = arrn[j - 1];
                arrstring[j - 1] = string;
                arrn[j - 1] = n;
            }
        }
    }

    public static void interrupt(Thread thread, long l) {
        if (thread == null) {
            return;
        }
        try {
            thread.join(l);
        }
        catch (InterruptedException interruptedException) {
            LogHolder.log(1, LogType.MISC, interruptedException);
        }
        Util.interrupt(thread);
    }

    public static void interrupt(Thread thread) {
        if (thread != null) {
            int n = 0;
            while (thread.isAlive()) {
                if (n > 10) {
                    LogHolder.log(1, LogType.MISC, "Thread " + thread.getName() + " cannot get interrupted!");
                }
                thread.interrupt();
                Thread.yield();
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException interruptedException) {
                    LogHolder.log(4, LogType.MISC, interruptedException);
                }
                ++n;
            }
        }
    }

    private static boolean isUmlaut(char c, String[] arrstring, int n) {
        switch (c) {
            case '\u00e4': {
                arrstring[n] = "ae";
                return true;
            }
            case '\u00f6': {
                arrstring[n] = "oe";
                return true;
            }
            case '\u00fc': {
                arrstring[n] = "ue";
                return true;
            }
        }
        arrstring[n] = null;
        return false;
    }

    public static long convertVersionStringToNumber(String string) throws NumberFormatException {
        if (string == null) {
            throw new NumberFormatException("Version string is null!");
        }
        long l = 0L;
        StringTokenizer stringTokenizer = new StringTokenizer(string, ".");
        try {
            l = Long.parseLong(stringTokenizer.nextToken()) * 100000L + Long.parseLong(stringTokenizer.nextToken()) * 1000L + Long.parseLong(stringTokenizer.nextToken());
        }
        catch (NoSuchElementException noSuchElementException) {
            throw new NumberFormatException("Version string is too short!");
        }
        return l;
    }

    public static String replaceAll(String string, String string2, String string3) {
        return Util.replaceAll(string, string2, string3, null);
    }

    public static String replaceAll(String string, String string2, String string3, String[] arrstring) {
        StringBuffer stringBuffer = new StringBuffer("");
        int n = string.indexOf(string2, 0);
        int n2 = 0;
        boolean bl = true;
        String string4 = null;
        while (n != -1) {
            bl = true;
            if (arrstring != null) {
                string4 = string.substring(n);
                for (int i = 0; i < arrstring.length; ++i) {
                    if (!string4.startsWith(arrstring[i])) continue;
                    bl = false;
                    break;
                }
            }
            if (bl) {
                stringBuffer.append(string.substring(n2, n));
                stringBuffer.append(string3);
                n2 = n + string2.length();
            }
            n = string.indexOf(string2, bl ? n2 : n + string2.length());
        }
        stringBuffer.append(string.substring(n2));
        return stringBuffer.toString();
    }

    public static String encodeWhiteSpaces(String string) {
        StringBuffer stringBuffer = new StringBuffer("");
        int n = string.indexOf(WHITESPACE, 0);
        int n2 = 0;
        while (n != -1) {
            stringBuffer.append(string.substring(n2, n));
            stringBuffer.append(WHITESPACE_ENCODED);
            n2 = n + WHITESPACE.length();
            n = string.indexOf(WHITESPACE, n + 1);
        }
        stringBuffer.append(string.substring(n2));
        return stringBuffer.toString();
    }

    public static void closeStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            }
            catch (Throwable throwable) {
                LogHolder.log(3, LogType.MISC, throwable);
            }
        }
    }

    public static void closeStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, exception);
            }
        }
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        Util.copyStream(inputStream, outputStream, null);
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream, IProgressCallback iProgressCallback) throws IOException {
        if (inputStream == null) {
            throw new IOException("Input stream is null!");
        }
        if (outputStream == null) {
            throw new IOException("Output stream is null!");
        }
        int n = 0;
        long l = 0L;
        long l2 = 0L;
        long l3 = 0L;
        if (iProgressCallback != null) {
            n = iProgressCallback.getCurrentMaximum();
            l = iProgressCallback.getCurrentSize();
            l2 = l / (long)n;
        }
        byte[] arrby = new byte[2048];
        int n2 = -1;
        while ((n2 = inputStream.read(arrby)) != -1) {
            outputStream.write(arrby, 0, n2);
            if (n <= 0 || (l3 += (long)n2) < l2) continue;
            l3 -= l2;
            --n;
            iProgressCallback.setValue(iProgressCallback.getValue() + 1);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    public static String getStaticFieldValue(Class class_, String string) {
        String string2 = null;
        try {
            Field field = class_.getField(string);
            string2 = (String)field.get(null);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return string2;
    }

    public static String colonizeSKI(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < string.length(); ++i) {
            stringBuffer.append(string.charAt(i));
            if ((i + 1) % 2 != 0 || i == string.length() - 1) continue;
            stringBuffer.append(":");
        }
        return stringBuffer.toString();
    }

    public static InfoServiceDBEntry[] createDefaultInfoServices(String[] arrstring, String[] arrstring2, int[][] arrn) throws Exception {
        InfoServiceDBEntry[] arrinfoServiceDBEntry = new InfoServiceDBEntry[arrstring.length];
        for (int i = 0; i < arrinfoServiceDBEntry.length; ++i) {
            Vector<ListenerInterface> vector = new Vector<ListenerInterface>(arrn[i].length);
            for (int j = 0; j < arrn[i].length; ++j) {
                vector.addElement(new ListenerInterface(arrstring2[i], arrn[i][j]));
            }
            arrinfoServiceDBEntry[i] = new InfoServiceDBEntry(arrstring[i], arrstring[i], vector, true, true, 0L, 0L, false, null);
            arrinfoServiceDBEntry[i].markAsBootstrap();
        }
        return arrinfoServiceDBEntry;
    }

    public static String formatKbitPerSecValueWithUnit(long l) {
        return Util.formatKbitPerSecValueWithUnit(l, 4);
    }

    public static String formatKbitPerSecValueWithUnit(long l, int n) {
        return Util.formatKbitPerSecValueWithoutUnit(l, n) + WHITESPACE + Util.formatKbitPerSecValueOnlyUnit(l, n);
    }

    public static String formatKbitPerSecValueOnlyUnit(long l) {
        return Util.formatKbitPerSecValueOnlyUnit(l, 4);
    }

    public static String formatKbitPerSecValueOnlyUnit(long l, int n) {
        if (l < 1000L || n < 1) {
            return JAPMessages.getString("kbit/s");
        }
        if (l < 1000000L || n < 2) {
            return JAPMessages.getString("Mbit/s");
        }
        return JAPMessages.getString("Gbit/s");
    }

    public static String formatKbitPerSecValueWithoutUnit(long l) {
        return Util.formatKbitPerSecValueWithoutUnit(l, 4);
    }

    public static String formatKbitPerSecValueWithoutUnit(long l, int n) {
        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance(JAPMessages.getLocale());
        double d = l;
        if (l < 1000L || n < 1) {
            decimalFormat.applyPattern("#,####");
        } else if (l < 1000000L || n < 2) {
            d /= 1000.0;
            decimalFormat.applyPattern("#,##0.0");
        } else {
            d /= 1000000.0;
            decimalFormat.applyPattern("#,##0.0");
        }
        return decimalFormat.format(d);
    }

    public static String formatBytesValueWithUnit(long l) {
        return Util.formatBytesValueWithUnit(l, 4);
    }

    public static String formatBytesValueWithUnit(long l, int n) {
        return Util.formatBytesValueWithoutUnit(l, n) + WHITESPACE + Util.formatBytesValueOnlyUnit(l, n);
    }

    public static String formatBytesValueOnlyUnit(long l) {
        return Util.formatBytesValueOnlyUnit(l, 4);
    }

    public static String formatBytesValueOnlyUnit(long l, int n) {
        if (l < 1000L || n < 1) {
            return JAPMessages.getString("Byte");
        }
        if (l < 1000000L || n < 2) {
            return JAPMessages.getString("kByte");
        }
        if (l < 1000000000L || n < 3) {
            return JAPMessages.getString("MByte");
        }
        return JAPMessages.getString("GByte");
    }

    public static String formatBytesValueWithoutUnit(long l) {
        return Util.formatBytesValueWithoutUnit(l, 4);
    }

    public static String formatBytesValueWithoutUnit(long l, int n) {
        DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance(JAPMessages.getLocale());
        double d = l;
        if (l < 1000L || n < 1) {
            decimalFormat.applyPattern("#");
        } else if (l < 1000000L || n < 2) {
            d /= 1000.0;
            decimalFormat.applyPattern("#,###,##0.0");
        } else if (l < 1000000000L || n < 3) {
            d /= 1000000.0;
            decimalFormat.applyPattern("#,###,##0.0");
        } else {
            d /= 1.0E9;
            decimalFormat.applyPattern("#,###,##0.0");
        }
        return decimalFormat.format(d);
    }

    public static InetAddress getLocalHost() throws UnknownHostException {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName("127.0.0.1");
        }
        catch (UnknownHostException unknownHostException) {
            try {
                InetAddress[] arrinetAddress = InetAddress.getAllByName("localhost");
                if (arrinetAddress == null || arrinetAddress.length == 0) {
                    throw new UnknownHostException("localhost");
                }
                inetAddress = arrinetAddress[0];
            }
            catch (UnknownHostException unknownHostException2) {
                inetAddress = InetAddress.getByName(null);
            }
        }
        return inetAddress;
    }

    public static String toHTMLEntities(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c < '\u0000' || c > '\u007f') {
                stringBuffer.append("&#").append(Integer.toString(c)).append(";");
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public static String formatTimestamp(Date date, boolean bl) {
        return Util.formatTimestamp(date, bl, JAPMessages.getLocale().getLanguage());
    }

    public static String formatTimestamp(Date date, boolean bl, String string) {
        String string2 = JAPMessages.getLocale().getCountry();
        if (string.equalsIgnoreCase("en") && string2.equals(Locale.US)) {
            SimpleDateFormat simpleDateFormat = bl ? new SimpleDateFormat("MM/dd/yyyy - HH:mm") : new SimpleDateFormat("MM/dd/yyyy");
            return simpleDateFormat.format(date);
        }
        if (string.equalsIgnoreCase("en")) {
            SimpleDateFormat simpleDateFormat = bl ? new SimpleDateFormat("dd/MM/yyyy - HH:mm") : new SimpleDateFormat("dd/MM/yyyy");
            return simpleDateFormat.format(date);
        }
        SimpleDateFormat simpleDateFormat = string.equalsIgnoreCase("de") ? (bl ? new SimpleDateFormat("dd.MM.yyyy - HH:mm") : new SimpleDateFormat("dd.MM.yyyy")) : (bl ? new SimpleDateFormat("yyyy-MM-dd  HH:mm") : new SimpleDateFormat("yyyy-MM-dd"));
        return simpleDateFormat.format(date);
    }

    public static HTTPResponse doHttpGetRequest(ListenerInterface listenerInterface, ImmutableProxyInterface immutableProxyInterface, String string) throws IOException, ModuleException {
        if (listenerInterface == null) {
            return null;
        }
        HTTPConnection hTTPConnection = HTTPConnectionFactory.getInstance().createHTTPConnection(listenerInterface, immutableProxyInterface, 0, true, null);
        HttpRequestStructure httpRequestStructure = HttpRequestStructure.createGetRequest(string);
        LogHolder.log(6, LogType.NET, "Get: " + hTTPConnection.getHost() + ":" + Integer.toString(hTTPConnection.getPort()) + httpRequestStructure.getRequestFileName());
        return hTTPConnection.Get(httpRequestStructure.getRequestFileName());
    }

    public static class StringSortAsc
    implements Comparable {
        public int compare(Object object, Object object2) {
            if (object == null && object2 == null) {
                return 0;
            }
            if (object == null) {
                return -1;
            }
            if (object2 == null) {
                return 1;
            }
            return ((String)object).compareTo((String)object2);
        }
    }

    public static class IntegerSortDesc
    implements Comparable {
        public int compare(Object object, Object object2) {
            if (object == null && object2 == null) {
                return 0;
            }
            if (object == null) {
                return 1;
            }
            if (object2 == null) {
                return -1;
            }
            if ((Integer)object == Integer.MAX_VALUE) {
                return -1;
            }
            if ((Integer)object2 == Integer.MAX_VALUE) {
                return 1;
            }
            return (Integer)object2 - (Integer)object;
        }
    }

    public static class IntegerSortAsc
    implements Comparable {
        public int compare(Object object, Object object2) {
            if (object == null && object2 == null) {
                return 0;
            }
            if (object == null) {
                return -1;
            }
            if (object2 == null) {
                return 1;
            }
            if ((Integer)object == Integer.MAX_VALUE) {
                return 1;
            }
            if ((Integer)object2 == Integer.MAX_VALUE) {
                return -1;
            }
            return (Integer)object - (Integer)object2;
        }
    }

    public static class LongSortDesc
    implements Comparable {
        public int compare(Object object, Object object2) {
            if (object == null && object2 == null) {
                return 0;
            }
            if (object == null) {
                return 1;
            }
            if (object2 == null) {
                return -1;
            }
            if ((long)((Long)object).intValue() == Long.MAX_VALUE) {
                return -1;
            }
            if ((long)((Long)object2).intValue() == Long.MAX_VALUE) {
                return 1;
            }
            return (int)((Long)object2 - (Long)object);
        }
    }

    public static class LongSortAsc
    implements Comparable {
        public int compare(Object object, Object object2) {
            if (object == null && object2 == null) {
                return 0;
            }
            if (object == null) {
                return -1;
            }
            if (object2 == null) {
                return 1;
            }
            if ((long)((Long)object).intValue() == Long.MAX_VALUE) {
                return 1;
            }
            if ((long)((Long)object2).intValue() == Long.MAX_VALUE) {
                return -1;
            }
            return (int)((Long)object - (Long)object2);
        }
    }

    public static interface Comparable {
        public int compare(Object var1, Object var2);
    }
}

