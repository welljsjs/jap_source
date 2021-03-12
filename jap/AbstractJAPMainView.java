/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;
import gui.AWTUpdateQueue;
import gui.JAPDll;
import jap.IJAPMainView;
import jap.JAPController;
import jap.JAPViewIconified;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public abstract class AbstractJAPMainView
extends JFrame
implements IJAPMainView {
    protected String m_Title;
    protected JAPController m_Controller;
    private boolean m_bChangingTitle = false;
    private final Object SYNC_TITLE = new Object();
    private final Object SYNC_PACK = new Object();
    private final AWTUpdateQueue AWT_UPDATE_QUEUE = new AWTUpdateQueue(new Runnable(){

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            Object object = AbstractJAPMainView.this.SYNC_PACK;
            synchronized (object) {
                AbstractJAPMainView.this.onUpdateValues();
            }
        }
    });

    public AbstractJAPMainView(String string, JAPController jAPController) {
        super(string);
        this.setName(string);
        this.m_Controller = jAPController;
        this.m_Title = string;
        this.setDefaultCloseOperation(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pack() {
        Object object = this.SYNC_PACK;
        synchronized (object) {
            super.pack();
        }
    }

    public abstract void saveWindowPositions();

    public Component getCurrentView() {
        return this.getContentPane();
    }

    public void setTitle(String string) {
        this.setName(string);
        super.setTitle(string);
    }

    public abstract void showIconifiedView(boolean var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setVisible(boolean bl) {
        Object object = this.SYNC_PACK;
        synchronized (object) {
            JAPViewIconified jAPViewIconified;
            if (bl && (jAPViewIconified = this.getViewIconified()) != null) {
                jAPViewIconified.setVisible(false);
            }
            super.setVisible(bl);
        }
    }

    public void showConfigDialog(String string, Object object) {
    }

    public final void showConfigDialog() {
        this.showConfigDialog(null, null);
    }

    public void packetMixed(long l) {
    }

    public final boolean isChangingTitle() {
        return this.m_bChangingTitle;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hideWindowInTaskbar() {
        Object object = this.SYNC_TITLE;
        synchronized (object) {
            this.m_bChangingTitle = true;
            this.setTitle(Double.toString(Math.random()));
            boolean bl = JAPDll.hideWindowInTaskbar(this.getTitle());
            if (bl) {
                this.setVisible(false);
            }
            this.setTitle(this.m_Title);
            this.m_bChangingTitle = false;
            return bl;
        }
    }

    public void updateValues(boolean bl) {
        this.AWT_UPDATE_QUEUE.update(bl);
    }

    public abstract /* synthetic */ void onUpdateValues();

    public abstract /* synthetic */ void disableSetAnonMode();

    public abstract /* synthetic */ void doClickOnCascadeChooser();

    public abstract /* synthetic */ boolean isShowingPaymentError();

    public abstract /* synthetic */ JAPViewIconified getViewIconified();

    public abstract /* synthetic */ void registerViewIconified(JAPViewIconified var1);

    public abstract /* synthetic */ void create(boolean var1);

    public abstract /* synthetic */ void transferedBytes(long var1, int var3);

    public abstract /* synthetic */ void channelsChanged(int var1);

    public abstract /* synthetic */ void removeStatusMsg(int var1);

    public abstract /* synthetic */ int addStatusMsg(String var1, int var2, boolean var3, ActionListener var4);

    public abstract /* synthetic */ int addStatusMsg(String var1, int var2, boolean var3);

    public abstract /* synthetic */ void integrityErrorSignaled(AnonServiceException var1);

    public abstract /* synthetic */ void dataChainErrorSignaled(AnonServiceException var1);

    public abstract /* synthetic */ void connectionEstablished(AnonServerDescription var1);

    public abstract /* synthetic */ void connecting(AnonServerDescription var1, boolean var2);

    public abstract /* synthetic */ void disconnected();

    public abstract /* synthetic */ void currentServiceChanged(AnonServerDescription var1);

    public abstract /* synthetic */ void connectionError(AnonServiceException var1);
}

