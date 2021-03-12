/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.AnonServiceEventListener;
import gui.IStatusLine;
import jap.JAPObserver;
import jap.JAPViewIconified;

public interface IJAPMainView
extends JAPObserver,
IStatusLine,
AnonServiceEventListener {
    public void create(boolean var1);

    public void setVisible(boolean var1);

    public boolean isVisible();

    public void registerViewIconified(JAPViewIconified var1);

    public JAPViewIconified getViewIconified();

    public boolean isShowingPaymentError();

    public void showConfigDialog();

    public void showConfigDialog(String var1, Object var2);

    public void doClickOnCascadeChooser();

    public void disableSetAnonMode();

    public void onUpdateValues();
}

