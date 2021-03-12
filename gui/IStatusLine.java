/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.awt.event.ActionListener;

public interface IStatusLine {
    public int addStatusMsg(String var1, int var2, boolean var3);

    public int addStatusMsg(String var1, int var2, boolean var3, ActionListener var4);

    public void removeStatusMsg(int var1);
}

