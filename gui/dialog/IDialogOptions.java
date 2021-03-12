/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

public interface IDialogOptions {
    public static final int MESSAGE_TYPE_PLAIN = -1;
    public static final int MESSAGE_TYPE_QUESTION = 3;
    public static final int MESSAGE_TYPE_ERROR = 0;
    public static final int MESSAGE_TYPE_WARNING = 2;
    public static final int MESSAGE_TYPE_INFORMATION = 1;
    public static final int OPTION_TYPE_DEFAULT = -1;
    public static final int OPTION_TYPE_OK_CANCEL = 2;
    public static final int OPTION_TYPE_YES_NO_CANCEL = 1;
    public static final int OPTION_TYPE_YES_NO = 0;
    public static final int OPTION_TYPE_EMPTY = Integer.MIN_VALUE;
    public static final int OPTION_TYPE_CANCEL = -2147483647;
    public static final int RETURN_VALUE_CANCEL = 2;
    public static final int RETURN_VALUE_OK = 0;
    public static final int RETURN_VALUE_CLOSED = -1;
    public static final int RETURN_VALUE_YES = 0;
    public static final int RETURN_VALUE_NO = 1;
    public static final int RETURN_VALUE_UNINITIALIZED = Integer.MIN_VALUE;
}

