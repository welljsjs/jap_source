/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.log4j.Priority;

public class Level
extends Priority
implements Serializable {
    public static final int TRACE_INT = 5000;
    public static final Level OFF = new Level(Integer.MAX_VALUE, "OFF", 0);
    public static final Level FATAL = new Level(50000, "FATAL", 0);
    public static final Level ERROR = new Level(40000, "ERROR", 3);
    public static final Level WARN = new Level(30000, "WARN", 4);
    public static final Level INFO = new Level(20000, "INFO", 6);
    public static final Level DEBUG = new Level(10000, "DEBUG", 7);
    public static final Level TRACE = new Level(5000, "TRACE", 7);
    public static final Level ALL = new Level(Integer.MIN_VALUE, "ALL", 7);
    static final long serialVersionUID = 3491141966387921974L;
    static /* synthetic */ Class class$org$apache$log4j$Level;

    protected Level(int n, String string, int n2) {
        super(n, string, n2);
    }

    public static Level toLevel(String string) {
        return Level.toLevel(string, DEBUG);
    }

    public static Level toLevel(int n) {
        return Level.toLevel(n, DEBUG);
    }

    public static Level toLevel(int n, Level level) {
        switch (n) {
            case -2147483648: {
                return ALL;
            }
            case 10000: {
                return DEBUG;
            }
            case 20000: {
                return INFO;
            }
            case 30000: {
                return WARN;
            }
            case 40000: {
                return ERROR;
            }
            case 50000: {
                return FATAL;
            }
            case 0x7FFFFFFF: {
                return OFF;
            }
            case 5000: {
                return TRACE;
            }
        }
        return level;
    }

    public static Level toLevel(String string, Level level) {
        if (string == null) {
            return level;
        }
        String string2 = string.toUpperCase();
        if (string2.equals("ALL")) {
            return ALL;
        }
        if (string2.equals("DEBUG")) {
            return DEBUG;
        }
        if (string2.equals("INFO")) {
            return INFO;
        }
        if (string2.equals("WARN")) {
            return WARN;
        }
        if (string2.equals("ERROR")) {
            return ERROR;
        }
        if (string2.equals("FATAL")) {
            return FATAL;
        }
        if (string2.equals("OFF")) {
            return OFF;
        }
        if (string2.equals("TRACE")) {
            return TRACE;
        }
        return level;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.level = objectInputStream.readInt();
        this.syslogEquivalent = objectInputStream.readInt();
        this.levelStr = objectInputStream.readUTF();
        if (this.levelStr == null) {
            this.levelStr = "";
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.level);
        objectOutputStream.writeInt(this.syslogEquivalent);
        objectOutputStream.writeUTF(this.levelStr);
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.getClass() == (class$org$apache$log4j$Level == null ? (class$org$apache$log4j$Level = Level.class$("org.apache.log4j.Level")) : class$org$apache$log4j$Level)) {
            return Level.toLevel(this.level);
        }
        return this;
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

