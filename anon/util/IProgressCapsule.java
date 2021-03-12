/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public interface IProgressCapsule {
    public static final int PROGRESS_NOT_STARTED = -1;
    public static final int PROGRESS_FINISHED = 0;
    public static final int PROGRESS_ONGOING = 1;
    public static final int PROGRESS_ABORTED = 2;
    public static final int PROGRESS_FAILED = 3;

    public int getMaximum();

    public int getMinimum();

    public int getValue();

    public int getStatus();

    public void reset();

    public String getMessage();
}

