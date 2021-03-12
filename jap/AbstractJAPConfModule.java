/*
 * Decompiled with CFR 0.150.
 */
package jap;

import gui.AWTUpdateQueue;
import gui.JAPHelpContext;
import jap.IJAPConfSavePoint;
import jap.JAPConf;
import jap.JAPConfAnon;
import jap.JAPModel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractJAPConfModule
implements JAPHelpContext.IHelpContext {
    private final AWTUpdateQueue AWT_UPDATE_QUEUE = new AWTUpdateQueue(new Runnable(){

        public void run() {
            try {
                AbstractJAPConfModule.this.onUpdateValues();
                AbstractJAPConfModule.this.m_rootPanel.validate();
            }
            catch (Throwable throwable) {
                LogHolder.log(1, LogType.GUI, throwable);
            }
        }
    });
    private JPanel m_rootPanel;
    protected IJAPConfSavePoint m_savePoint;
    private boolean m_bObserversInitialised = false;
    protected final Object LOCK_OBSERVABLE = new Object();

    protected AbstractJAPConfModule(IJAPConfSavePoint iJAPConfSavePoint) {
        this.m_rootPanel = new JPanel();
        this.m_rootPanel.addAncestorListener(new RootPanelAncestorListener());
        this.m_savePoint = iJAPConfSavePoint;
        this.recreateRootPanel();
        FontSizeObserver fontSizeObserver = new FontSizeObserver();
        JAPModel.getInstance().addObserver(fontSizeObserver);
        this.fontSizeChanged(new JAPModel.FontResize(JAPModel.getInstance().getFontSize(), JAPModel.getInstance().getFontSize()), fontSizeObserver.getDummyLabel());
    }

    public static GridBagConstraints createTabbedRootPanelContraints() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        return gridBagConstraints;
    }

    public abstract String getTabTitle();

    public abstract void recreateRootPanel();

    public final JPanel getRootPanel() {
        return this.m_rootPanel;
    }

    public final void createSavePoint() {
        if (this.m_savePoint != null) {
            this.m_savePoint.createSavePoint();
        }
    }

    public final boolean okPressed() {
        return this.onOkPressed();
    }

    public void fontSizeChanged(JAPModel.FontResize fontResize, JLabel jLabel) {
    }

    public final void cancelPressed() {
        if (this.m_savePoint != null) {
            this.m_savePoint.restoreSavePoint();
        }
        this.onCancelPressed();
    }

    public final void resetToDefaultsPressed() {
        if (this.m_savePoint != null) {
            this.m_savePoint.restoreDefaults();
        }
        this.onResetToDefaultsPressed();
    }

    public final void updateValues(boolean bl) {
        if (this instanceof JAPConfAnon) {
            // empty if block
        }
        this.AWT_UPDATE_QUEUE.update(bl);
    }

    protected void onRootPanelShown() {
    }

    protected boolean onOkPressed() {
        return true;
    }

    protected void onCancelPressed() {
    }

    protected void onResetToDefaultsPressed() {
    }

    protected void onUpdateValues() {
    }

    protected boolean initObservers() {
        boolean bl = this.m_bObserversInitialised;
        this.m_bObserversInitialised = true;
        return !bl;
    }

    public Component getHelpExtractionDisplayContext() {
        return JAPConf.getInstance().getContentPane();
    }

    public abstract /* synthetic */ String getHelpContext();

    protected class FontSizeObserver
    implements Observer {
        private JLabel DUMMY_LABEL = new JLabel();

        protected FontSizeObserver() {
        }

        public JLabel getDummyLabel() {
            return this.DUMMY_LABEL;
        }

        public void update(Observable observable, Object object) {
            if (object instanceof JAPModel.FontResize && object != null) {
                SwingUtilities.updateComponentTreeUI(this.DUMMY_LABEL);
                AbstractJAPConfModule.this.fontSizeChanged((JAPModel.FontResize)object, this.DUMMY_LABEL);
            }
        }
    }

    private class RootPanelAncestorListener
    implements AncestorListener {
        private RootPanelAncestorListener() {
        }

        public void ancestorAdded(AncestorEvent ancestorEvent) {
            if (ancestorEvent.getAncestor() == AbstractJAPConfModule.this.getRootPanel() && AbstractJAPConfModule.this.getRootPanel().isVisible()) {
                AbstractJAPConfModule.this.onRootPanelShown();
            }
        }

        public void ancestorMoved(AncestorEvent ancestorEvent) {
        }

        public void ancestorRemoved(AncestorEvent ancestorEvent) {
        }
    }
}

