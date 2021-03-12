/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import org.apache.log4j.Level;

public class Priority {
    transient int level;
    transient String levelStr;
    transient int syslogEquivalent;
    public static final int OFF_INT = Integer.MAX_VALUE;
    public static final int FATAL_INT = 50000;
    public static final int ERROR_INT = 40000;
    public static final int WARN_INT = 30000;
    public static final int INFO_INT = 20000;
    public static final int DEBUG_INT = 10000;
    public static final int ALL_INT = Integer.MIN_VALUE;
    public static final Priority FATAL = new Level(50000, "FATAL", 0);
    public static final Priority ERROR = new Level(40000, "ERROR", 3);
    public static final Priority WARN = new Level(30000, "WARN", 4);
    public static final Priority INFO = new Level(20000, "INFO", 6);
    public static final Priority DEBUG = new Level(10000, "DEBUG", 7);

    protected Priority() {
        this.level = 10000;
        this.levelStr = "DEBUG";
        this.syslogEquivalent = 7;
    }

    protected Priority(int n, String string, int n2) {
        this.level = n;
        this.levelStr = string;
        this.syslogEquivalent = n2;
    }

    public boolean equals(Object object) {
        if (object instanceof Priority) {
            Priority priority = (Priority)object;
            return this.level == priority.level;
        }
        return false;
    }

    public final int getSyslogEquivalent() {
        return this.syslogEquivalent;
    }

    public boolean isGreaterOrEqual(Priority priority) {
        return this.level >= priority.level;
    }

    public static Priority[] getAllPossiblePriorities() {
        return new Priority[]{FATAL, ERROR, Level.WARN, INFO, DEBUG};
    }

    public final String toString() {
        return this.levelStr;
    }

    public final int toInt() {
        return this.level;
    }

    public static Priority toPriority(String string) {
        return Level.toLevel(string);
    }

    public static Priority toPriority(int n) {
        return Priority.toPriority(n, DEBUG);
    }

    public static Priority toPriority(int n, Priority priority) {
        return Level.toLevel(n, (Level)priority);
    }

    public static Priority toPriority(String string, Priority priority) {
        return Level.toLevel(string, (Level)priority);
    }
}

