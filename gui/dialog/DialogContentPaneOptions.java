/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import gui.JAPHelpContext;
import gui.dialog.AbstractDialogExtraButton;
import gui.dialog.DialogContentPane;
import java.awt.Component;

public class DialogContentPaneOptions {
    private int m_optionType;
    private DialogContentPane m_previousContentPane;
    private JAPHelpContext.IHelpContext m_helpContext;
    private IOptionsUpdateCallbackHandler m_callbackHandler;

    public DialogContentPaneOptions(int n) {
        this(n, null, null, null);
    }

    public DialogContentPaneOptions(String string) {
        this(2, string, null);
    }

    public DialogContentPaneOptions(JAPHelpContext.IHelpContext iHelpContext) {
        this(2, iHelpContext, null, null);
    }

    public DialogContentPaneOptions(DialogContentPane dialogContentPane) {
        this(2, (JAPHelpContext.IHelpContext)null, dialogContentPane);
    }

    public DialogContentPaneOptions(JAPHelpContext.IHelpContext iHelpContext, DialogContentPane dialogContentPane) {
        this(2, iHelpContext, dialogContentPane);
    }

    public DialogContentPaneOptions(final String string, final DialogContentPane dialogContentPane) {
        this(2, new JAPHelpContext.IHelpContext(){

            public String getHelpContext() {
                return string;
            }

            public Component getHelpExtractionDisplayContext() {
                if (dialogContentPane != null) {
                    return dialogContentPane.getDialog().getContentPane();
                }
                return null;
            }
        }, dialogContentPane);
    }

    public DialogContentPaneOptions(int n, DialogContentPane dialogContentPane) {
        this(n, (JAPHelpContext.IHelpContext)null, dialogContentPane);
    }

    public DialogContentPaneOptions(int n, JAPHelpContext.IHelpContext iHelpContext) {
        this(n, iHelpContext, null, null);
    }

    public DialogContentPaneOptions(int n, String string) {
        this(n, string, (DialogContentPane)null);
    }

    public DialogContentPaneOptions(int n, final String string, DialogContentPane dialogContentPane) {
        this(n, new JAPHelpContext.IHelpContext(){

            public String getHelpContext() {
                return string;
            }

            public Component getHelpExtractionDisplayContext() {
                return null;
            }
        }, dialogContentPane);
    }

    public DialogContentPaneOptions(int n, JAPHelpContext.IHelpContext iHelpContext, IOptionsUpdateCallbackHandler iOptionsUpdateCallbackHandler) {
        this(n, iHelpContext, iOptionsUpdateCallbackHandler, null);
    }

    public DialogContentPaneOptions(int n, JAPHelpContext.IHelpContext iHelpContext, DialogContentPane dialogContentPane) {
        this(n, iHelpContext, null, dialogContentPane);
    }

    public DialogContentPaneOptions(int n, JAPHelpContext.IHelpContext iHelpContext, IOptionsUpdateCallbackHandler iOptionsUpdateCallbackHandler, DialogContentPane dialogContentPane) {
        this.m_optionType = n;
        this.m_helpContext = iHelpContext;
        this.m_previousContentPane = dialogContentPane;
        this.m_callbackHandler = iOptionsUpdateCallbackHandler;
    }

    protected void setUpdateCallback(OptionsUpdateCallback optionsUpdateCallback) {
        if (this.m_callbackHandler != null) {
            this.m_callbackHandler.setUpdateCallback(optionsUpdateCallback);
        }
    }

    public final int getOptionType() {
        return this.m_optionType;
    }

    public final JAPHelpContext.IHelpContext getHelpContext() {
        return this.m_helpContext;
    }

    public final DialogContentPane getPreviousContentPane() {
        return this.m_previousContentPane;
    }

    public int countExtraButtons() {
        return 0;
    }

    public AbstractDialogExtraButton getExtraButtonInternal(int n) {
        return null;
    }

    protected final AbstractDialogExtraButton getExtraButton(int n) {
        if (n < 0 || n >= this.countExtraButtons()) {
            return null;
        }
        return this.getExtraButtonInternal(n);
    }

    public static abstract class OptionsUpdateCallback {
        public abstract void update();
    }

    public static interface IOptionsUpdateCallbackHandler {
        public void setUpdateCallback(OptionsUpdateCallback var1);
    }
}

