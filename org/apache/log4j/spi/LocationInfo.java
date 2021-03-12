/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.spi;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;

public class LocationInfo
implements Serializable {
    transient String lineNumber;
    transient String fileName;
    transient String className;
    transient String methodName;
    public String fullInfo;
    private static StringWriter sw = new StringWriter();
    private static PrintWriter pw = new PrintWriter(sw);
    public static final String NA = "?";
    static final long serialVersionUID = -1325822038990805636L;
    static boolean inVisualAge = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LocationInfo(Throwable throwable, String string) {
        String string2;
        if (throwable == null) {
            return;
        }
        StringWriter stringWriter = sw;
        synchronized (stringWriter) {
            throwable.printStackTrace(pw);
            string2 = sw.toString();
            sw.getBuffer().setLength(0);
        }
        int n = string2.lastIndexOf(string);
        if (n == -1) {
            return;
        }
        if ((n = string2.indexOf(Layout.LINE_SEP, n)) == -1) {
            return;
        }
        int n2 = string2.indexOf(Layout.LINE_SEP, n += Layout.LINE_SEP_LEN);
        if (n2 == -1) {
            return;
        }
        if (!inVisualAge) {
            n = string2.lastIndexOf("at ", n2);
            if (n == -1) {
                return;
            }
            n += 3;
        }
        this.fullInfo = string2.substring(n, n2);
    }

    public String getClassName() {
        if (this.fullInfo == null) {
            return NA;
        }
        if (this.className == null) {
            int n = this.fullInfo.lastIndexOf(40);
            if (n == -1) {
                this.className = NA;
            } else {
                n = this.fullInfo.lastIndexOf(46, n);
                int n2 = 0;
                if (inVisualAge) {
                    n2 = this.fullInfo.lastIndexOf(32, n) + 1;
                }
                this.className = n == -1 ? NA : this.fullInfo.substring(n2, n);
            }
        }
        return this.className;
    }

    public String getFileName() {
        if (this.fullInfo == null) {
            return NA;
        }
        if (this.fileName == null) {
            int n = this.fullInfo.lastIndexOf(58);
            if (n == -1) {
                this.fileName = NA;
            } else {
                int n2 = this.fullInfo.lastIndexOf(40, n - 1);
                this.fileName = this.fullInfo.substring(n2 + 1, n);
            }
        }
        return this.fileName;
    }

    public String getLineNumber() {
        if (this.fullInfo == null) {
            return NA;
        }
        if (this.lineNumber == null) {
            int n = this.fullInfo.lastIndexOf(41);
            int n2 = this.fullInfo.lastIndexOf(58, n - 1);
            this.lineNumber = n2 == -1 ? NA : this.fullInfo.substring(n2 + 1, n);
        }
        return this.lineNumber;
    }

    public String getMethodName() {
        if (this.fullInfo == null) {
            return NA;
        }
        if (this.methodName == null) {
            int n = this.fullInfo.lastIndexOf(40);
            int n2 = this.fullInfo.lastIndexOf(46, n);
            this.methodName = n2 == -1 ? NA : this.fullInfo.substring(n2 + 1, n);
        }
        return this.methodName;
    }

    static {
        try {
            Class<?> class_ = Class.forName("com.ibm.uvm.tools.DebugSupport");
            inVisualAge = true;
            LogLog.debug("Detected IBM VisualAge environment.");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

