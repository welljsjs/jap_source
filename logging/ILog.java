/*
 * Decompiled with CFR 0.150.
 */
package logging;

public interface ILog {
    public void log(int var1, int var2, String var3);

    public void setLogType(int var1);

    public int getLogType();

    public void setLogLevel(int var1);

    public int getLogLevel();
}

