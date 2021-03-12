/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPHelpContext;
import gui.JAPHtmlMultiLineLabel;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.IDialogOptions;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import gui.help.JAPHelp;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import javax.swing.border.TitledBorder;
import logging.LogHolder;
import logging.LogType;

public class DialogContentPane
implements JAPHelpContext.IHelpContext,
IDialogOptions {
    private static final int[] MESSAGE_TYPES = new int[]{-1, 1, 3, 2, 0};
    private static final Icon[] MESSAGE_ICONS = new Icon[MESSAGE_TYPES.length];
    public static final int ON_CLICK_DO_NOTHING = 0;
    public static final int ON_CLICK_HIDE_DIALOG = 1;
    public static final int ON_CLICK_DISPOSE_DIALOG = 2;
    public static final int ON_CLICK_SHOW_NEXT_CONTENT = 4;
    public static final int ON_YESOK_SHOW_NEXT_CONTENT = 8;
    public static final int ON_NO_SHOW_NEXT_CONTENT = 16;
    public static final int ON_CANCEL_SHOW_NEXT_CONTENT = 32;
    public static final int ON_CLICK_SHOW_PREVIOUS_CONTENT = 64;
    public static final int ON_YESOK_SHOW_PREVIOUS_CONTENT = 128;
    public static final int ON_NO_SHOW_PREVIOUS_CONTENT = 256;
    public static final int ON_CANCEL_SHOW_PREVIOUS_CONTENT = 512;
    public static final int ON_YESOK_HIDE_DIALOG = 1024;
    public static final int ON_NO_HIDE_DIALOG = 2048;
    public static final int ON_CANCEL_HIDE_DIALOG = 4096;
    public static final int ON_YESOK_DISPOSE_DIALOG = 8192;
    public static final int ON_NO_DISPOSE_DIALOG = 16384;
    public static final int ON_CANCEL_DISPOSE_DIALOG = 32768;
    public static final int ON_CANCEL_SEND_DIALOG_WINDOW_CLOSING_EVENT = 65536;
    public static final int ON_YESOK_SEND_DIALOG_WINDOW_CLOSING_EVENT = 131072;
    public static final int ON_NO_SEND_DIALOG_WINDOW_CLOSING_EVENT = 262144;
    public static final int ON_CLICK_SEND_DIALOG_WINDOW_CLOSING_EVENT = 524288;
    public static final int BUTTON_OPERATION_WIZARD = 266;
    public static final String MSG_OK;
    public static final String MSG_YES;
    public static final String MSG_NO;
    public static final String MSG_NEXT;
    public static final String MSG_PREVIOUS;
    public static final String MSG_FINISH;
    public static final String MSG_CANCEL;
    public static final String MSG_IGNORE;
    public static final String MSG_CONTINUE;
    public static final String MSG_OPERATION_FAILED;
    public static final String MSG_SEE_FULL_MESSAGE;
    public static final String MSG_OPEN_IN_BROWSER;
    public static final int DEFAULT_BUTTON_EMPTY = 0;
    public static final int DEFAULT_BUTTON_CANCEL = 1;
    public static final int DEFAULT_BUTTON_YES = 2;
    public static final int DEFAULT_BUTTON_OK = 2;
    public static final int DEFAULT_BUTTON_NO = 3;
    public static final int DEFAULT_BUTTON_HELP = 4;
    public static final int DEFAULT_BUTTON_KEEP = 5;
    private static final int MIN_TEXT_WIDTH = 100;
    private static final int UNLIMITED_SIZE = 2500;
    private static final int SPACE_AROUND_TEXT = 5;
    private static final String MORE_POINTS = "...";
    private static final int NUMBER_OF_HEURISTIC_ITERATIONS = 6;
    private static final boolean MOVE_PANE_NEXT = true;
    private static final boolean MOVE_PANE_PREVIOUS = false;
    private DialogContentPane m_nextContentPane;
    private DialogContentPane m_previousContentPane;
    private RootPaneContainer m_parentDialog;
    private JComponent m_contentPane;
    private JPanel m_titlePane;
    private JPanel m_rootPane;
    private Container m_panelOptions;
    private JAPHtmlMultiLineLabel m_lblMessage;
    private MouseListener m_linkedListener;
    private JAPHtmlMultiLineLabel m_lblText;
    private JAPHtmlMultiLineLabel m_lblSeeFullText;
    private int m_defaultButtonOperation;
    private int m_value;
    private JAPHelpContext.IHelpContext m_helpContext;
    private JButton m_btnHelp;
    private JButton m_btnYesOK;
    private JButton m_btnNo;
    private JButton m_btnCancel;
    private ButtonListener m_buttonListener;
    private Icon m_icon;
    private GridBagConstraints m_textConstraints;
    private Vector m_rememberedErrors = new Vector();
    private Vector m_rememberedUpdateErrors = new Vector();
    private Container m_currentlyActiveContentPane;
    private Vector m_componentListeners = new Vector();
    private ComponentListener m_currentlyActiveContentPaneComponentListener;
    private int m_defaultButton;
    private String m_strText;
    private JDialog m_tempDialog;
    private boolean m_bDisposed = false;
    private DialogContentPaneOptions m_options;
    private Layout m_layout;
    private int m_idStatusMessage = 0;
    static /* synthetic */ Class class$gui$dialog$DialogContentPane;

    public DialogContentPane(JDialog jDialog, String string) {
        this((RootPaneContainer)jDialog, string, new Layout(""), null);
    }

    public DialogContentPane(JAPDialog jAPDialog, String string) {
        this((RootPaneContainer)jAPDialog, string, new Layout(""), null);
    }

    public DialogContentPane(JDialog jDialog, String string, Layout layout) {
        this((RootPaneContainer)jDialog, string, layout, null);
    }

    public DialogContentPane(JAPDialog jAPDialog, String string, Layout layout) {
        this((RootPaneContainer)jAPDialog, string, layout, null);
    }

    public DialogContentPane(JDialog jDialog, String string, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jDialog, string, new Layout(""), dialogContentPaneOptions);
    }

    public DialogContentPane(JAPDialog jAPDialog, String string, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jAPDialog, string, new Layout(""), dialogContentPaneOptions);
    }

    public DialogContentPane(JDialog jDialog, String string, Layout layout, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jDialog, string, layout, dialogContentPaneOptions);
    }

    public DialogContentPane(JAPDialog jAPDialog, String string, Layout layout, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jAPDialog, string, layout, dialogContentPaneOptions);
    }

    public DialogContentPane(JDialog jDialog) {
        this((RootPaneContainer)jDialog, null, new Layout(""), null);
    }

    public DialogContentPane(JAPDialog jAPDialog) {
        this((RootPaneContainer)jAPDialog, null, new Layout(""), null);
    }

    public DialogContentPane(JDialog jDialog, Layout layout) {
        this((RootPaneContainer)jDialog, null, layout, null);
    }

    public DialogContentPane(JAPDialog jAPDialog, Layout layout) {
        this((RootPaneContainer)jAPDialog, null, layout, null);
    }

    public DialogContentPane(JDialog jDialog, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jDialog, null, new Layout(""), dialogContentPaneOptions);
    }

    public DialogContentPane(JAPDialog jAPDialog, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jAPDialog, null, new Layout(""), dialogContentPaneOptions);
    }

    public DialogContentPane(JDialog jDialog, Layout layout, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jDialog, null, layout, dialogContentPaneOptions);
    }

    public DialogContentPane(JAPDialog jAPDialog, Layout layout, DialogContentPaneOptions dialogContentPaneOptions) {
        this((RootPaneContainer)jAPDialog, null, layout, dialogContentPaneOptions);
    }

    private DialogContentPane(RootPaneContainer rootPaneContainer, String string, Layout layout, DialogContentPaneOptions dialogContentPaneOptions) {
        if (layout == null) {
            layout = new Layout((String)null);
        }
        this.m_layout = layout;
        if (dialogContentPaneOptions == null) {
            dialogContentPaneOptions = new DialogContentPaneOptions((JAPHelpContext.IHelpContext)null);
        }
        this.m_options = dialogContentPaneOptions;
        this.m_options.setUpdateCallback(new DialogContentPaneOptions.OptionsUpdateCallback(){

            public void update() {
                String string;
                if (DialogContentPane.this.m_btnYesOK != null && (string = DialogContentPane.this.getButtonYesOKText()) != null) {
                    DialogContentPane.this.m_btnYesOK.setText(string);
                }
                if (DialogContentPane.this.m_btnNo != null && (string = DialogContentPane.this.getButtonNoText()) != null) {
                    DialogContentPane.this.m_btnNo.setText(string);
                }
                if (DialogContentPane.this.m_btnCancel != null && (string = DialogContentPane.this.getButtonCancelText()) != null) {
                    DialogContentPane.this.m_btnCancel.setText(string);
                }
            }
        });
        int n = 0;
        if (rootPaneContainer == null) {
            throw new IllegalArgumentException("The parent dialog must not be null!");
        }
        if (this.m_options.getPreviousContentPane() != null && this.m_options.getPreviousContentPane().m_parentDialog != rootPaneContainer) {
            throw new IllegalArgumentException("Chained content panes must refer to the same dialog!");
        }
        if (this.m_options.getOptionType() != Integer.MIN_VALUE && this.m_options.getOptionType() != -1 && this.m_options.getOptionType() != -2147483647 && this.m_options.getOptionType() != 2 && this.m_options.getOptionType() != 1 && this.m_options.getOptionType() != 0) {
            throw new IllegalArgumentException("Unknown option type!");
        }
        if (this.m_layout.getMessageType() != -1 && this.m_layout.getMessageType() != 3 && this.m_layout.getMessageType() != 0 && this.m_layout.getMessageType() != 2 && this.m_layout.getMessageType() != 1) {
            throw new IllegalArgumentException("Unknown message type!");
        }
        this.m_defaultButtonOperation = this instanceof IWizardSuitable ? 266 : 0;
        this.m_parentDialog = rootPaneContainer;
        this.m_previousContentPane = this.m_options.getPreviousContentPane();
        this.m_icon = this.m_layout.getIcon();
        if (this.m_icon == null) {
            this.m_icon = DialogContentPane.getMessageIcon(this.m_layout.getMessageType());
        }
        this.m_helpContext = this.m_options.getHelpContext() != null ? (this.m_options.getHelpContext() instanceof JAPHelpContext.IURLHelpContext ? new JAPHelpContext.IURLHelpContext(){

            public Component getHelpExtractionDisplayContext() {
                return DialogContentPane.this.getContentPane();
            }

            public String getHelpContext() {
                return DialogContentPane.this.m_options.getHelpContext().getHelpContext();
            }

            public String getURLMessage() {
                return ((JAPHelpContext.IURLHelpContext)DialogContentPane.this.m_options.getHelpContext()).getURLMessage();
            }

            public URL getHelpURL() {
                return ((JAPHelpContext.IURLHelpContext)DialogContentPane.this.m_options.getHelpContext()).getHelpURL();
            }
        } : new JAPHelpContext.IHelpContext(){

            public Component getHelpExtractionDisplayContext() {
                return DialogContentPane.this.getContentPane();
            }

            public String getHelpContext() {
                return DialogContentPane.this.m_options.getHelpContext().getHelpContext();
            }
        }) : null;
        this.m_rootPane = new JPanel(new BorderLayout());
        this.m_titlePane = new JPanel(new GridBagLayout());
        this.m_rootPane.add((Component)this.m_titlePane, "Center");
        this.addDialogComponentListener(new DialogComponentListener());
        this.addDialogWindowListener(new DialogWindowListener());
        this.setContentPane(new JPanel());
        if (this.m_layout.getTitle() != null) {
            if (this.m_layout.getTitle().trim().length() > 0) {
                this.m_titlePane.setBorder(new TitledBorder(this.m_layout.getTitle()));
            }
            this.m_lblMessage = new JAPHtmlMultiLineLabel();
            this.m_lblMessage.setFontStyle(1);
            this.clearStatusMessage();
            this.m_rootPane.add((Component)this.m_lblMessage, "South");
        }
        if (string != null && string.trim().length() > 0) {
            this.m_strText = JAPHtmlMultiLineLabel.removeHTMLHEADAndBODYTags(string);
            this.m_lblText = new JAPHtmlMultiLineLabel("<font color=#000000>" + this.m_strText + "</font>", n);
            this.m_lblText.setFontStyle(0);
        }
        this.m_textConstraints = new GridBagConstraints();
        this.m_textConstraints.gridx = 0;
        this.m_textConstraints.gridy = 1;
        this.m_textConstraints.weightx = 1.0;
        this.m_textConstraints.weighty = 0.0;
        this.m_textConstraints.anchor = 11;
        this.m_textConstraints.fill = 2;
        this.m_textConstraints.insets = new Insets(5, 5, 5, 5);
        if (this.m_layout.isCentered()) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.weighty = 10.0;
            gridBagConstraints.anchor = 11;
            gridBagConstraints.fill = 3;
            this.m_titlePane.add((Component)new JPanel(), gridBagConstraints);
            gridBagConstraints.gridy = 4;
            this.m_titlePane.add((Component)new JPanel(), gridBagConstraints);
        }
        if (this.m_previousContentPane != null) {
            this.m_previousContentPane.setNextContentPane(this);
        }
        this.setButtonValue(Integer.MIN_VALUE);
        this.createOptions();
        if (this.m_options.getOptionType() == -1 || this.m_options.getOptionType() == 0 || this.m_options.getOptionType() == 2 || this.m_options.getOptionType() == 1) {
            this.setDefaultButton(2);
        } else if (this.m_options.getOptionType() == -2147483647) {
            this.setDefaultButton(1);
        } else if (this.getButtonHelp() != null) {
            this.setDefaultButton(4);
        } else {
            this.setDefaultButton(5);
        }
        this.addComponentListener(new ComponentAdapter(){

            public void componentShown(ComponentEvent componentEvent) {
                if (DialogContentPane.this.hasWizardLayout()) {
                    DialogContentPane.this.setTextOfWizardNextButton();
                }
                DialogContentPane.this.validateDialog();
            }
        });
    }

    public static Icon getMessageIcon(int n) {
        int n2;
        if (n == 1) {
            n2 = 1;
        } else if (n == 3) {
            n2 = 2;
        } else if (n == 2) {
            n2 = 3;
        } else if (n == 0) {
            n2 = 4;
        } else {
            return MESSAGE_ICONS[0];
        }
        Icon icon = MESSAGE_ICONS[n2];
        if (icon == null) {
            DialogContentPane.MESSAGE_ICONS[n2] = icon = DialogContentPane.findMessageIcon(new JOptionPane("", n));
        }
        return GUIUtils.createScaledIcon(icon, GUIUtils.getIconResizer());
    }

    public void pack() {
        DialogContentPane.pack_internal(this.getFirstContentPane());
    }

    private static void pack_internal(DialogContentPane dialogContentPane) {
        Accessible accessible;
        if (dialogContentPane == null) {
            return;
        }
        int n = 100;
        DialogContentPane dialogContentPane2 = dialogContentPane;
        do {
            n = Math.max(n, dialogContentPane2.getContentPane().getPreferredSize().width);
        } while ((dialogContentPane2 = dialogContentPane2.getNextContentPane()) != null);
        int n2 = 0;
        int n3 = 0;
        dialogContentPane2 = dialogContentPane;
        if (dialogContentPane.m_parentDialog instanceof JDialog) {
            accessible = (JDialog)dialogContentPane.m_parentDialog;
            do {
                if (dialogContentPane2.isDialogVisible()) {
                    throw new IllegalStateException("You may not optimise the dialog size while it is visible!");
                }
                dialogContentPane2.updateDialog(n, false);
                dialogContentPane2.m_rootPane.setPreferredSize(null);
                ((Window)accessible).pack();
                n2 = Math.max(n2, accessible.getSize().width);
                n3 = Math.max(n3, accessible.getSize().height);
            } while ((dialogContentPane2 = dialogContentPane2.getNextContentPane()) != null);
            ((Window)accessible).setSize(new Dimension(n2, n3));
        } else {
            accessible = (JAPDialog)dialogContentPane.m_parentDialog;
            do {
                if (dialogContentPane2.isDialogVisible()) {
                    throw new IllegalStateException("You may not optimise the dialog size while it is visible!");
                }
                dialogContentPane2.updateDialog(n, false);
                dialogContentPane2.m_rootPane.setPreferredSize(null);
                ((JAPDialog)accessible).pack();
                n2 = Math.max(n2, accessible.getSize().width);
                n3 = Math.max(n3, accessible.getSize().height);
            } while ((dialogContentPane2 = dialogContentPane2.getNextContentPane()) != null);
            ((JAPDialog)accessible).setSize(new Dimension(n2, n3));
        }
        dialogContentPane2 = dialogContentPane;
        do {
            dialogContentPane2.m_rootPane.setPreferredSize(new Dimension(2500, 2500));
        } while ((dialogContentPane2 = dialogContentPane2.getNextContentPane()) != null);
        if (dialogContentPane.isSkippedAsNextContentPane()) {
            if (!dialogContentPane.moveToNextContentPane()) {
                dialogContentPane.updateDialog();
            }
        } else {
            dialogContentPane.updateDialog();
        }
    }

    public final boolean hasPreviousContentPane() {
        DialogContentPane dialogContentPane = this;
        while ((dialogContentPane = dialogContentPane.getPreviousContentPane()) != null) {
            if (!dialogContentPane.isMoveBackAllowed()) {
                return false;
            }
            try {
                if (dialogContentPane.isSkippedAsPreviousContentPane()) continue;
                return true;
            }
            catch (Exception exception) {
                return false;
            }
        }
        return false;
    }

    public final boolean hasNextContentPane() {
        DialogContentPane dialogContentPane = this;
        while ((dialogContentPane = dialogContentPane.getNextContentPane()) != null) {
            if (!dialogContentPane.isMoveForwardAllowed()) {
                return false;
            }
            try {
                if (dialogContentPane.isSkippedAsNextContentPane()) continue;
                return true;
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.GUI, exception);
                return false;
            }
        }
        return false;
    }

    public final boolean hasWizardLayout() {
        DialogContentPane dialogContentPane = this;
        if (!(dialogContentPane instanceof IWizardSuitable) || this.getNextContentPane() == null && this.getPreviousContentPane() == null) {
            return false;
        }
        while ((dialogContentPane = dialogContentPane.getPreviousContentPane()) != null) {
            if (dialogContentPane instanceof IWizardSuitable) continue;
            return false;
        }
        dialogContentPane = this;
        while ((dialogContentPane = dialogContentPane.getNextContentPane()) != null) {
            if (dialogContentPane instanceof IWizardSuitable) continue;
            return false;
        }
        return this.getNextContentPane() != null || this.getPreviousContentPane() == null || !(this.getPreviousContentPane() instanceof WorkerContentPane) || this.hasPreviousContentPane() || this.getPreviousContentPane().getPreviousContentPane() != null;
    }

    public final DialogContentPane getFirstContentPane() {
        DialogContentPane dialogContentPane = this;
        while (dialogContentPane.getPreviousContentPane() != null) {
            dialogContentPane = dialogContentPane.getPreviousContentPane();
        }
        return dialogContentPane;
    }

    public final DialogContentPane getNextContentPane() {
        return this.m_nextContentPane;
    }

    public final DialogContentPane getPreviousContentPane() {
        return this.m_previousContentPane;
    }

    public final boolean moveToPreviousContentPane() {
        return this.moveToContentPane(false) != this;
    }

    public CheckError checkYesOK() {
        return null;
    }

    public CheckError checkNo() {
        return null;
    }

    public CheckError checkCancel() {
        return null;
    }

    public CheckError checkUpdate() {
        return null;
    }

    public boolean isSkippedAsNextContentPane() {
        return false;
    }

    public boolean isMoveBackAllowed() {
        return true;
    }

    public boolean isMoveForwardAllowed() {
        return true;
    }

    public boolean isSkippedAsPreviousContentPane() {
        return false;
    }

    public final boolean moveToNextContentPane() {
        return this.moveToContentPane(true) != this;
    }

    public final JComponent getContentPane() {
        return this.m_contentPane;
    }

    public final void showDialog() {
        if (this.m_parentDialog instanceof JDialog) {
            ((JDialog)this.m_parentDialog).setVisible(true);
        } else {
            ((JAPDialog)this.m_parentDialog).setVisible(true);
        }
    }

    public final void setContentPane(JComponent jComponent) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.anchor = 11;
        gridBagConstraints.fill = 1;
        if (this.m_contentPane != null) {
            this.m_titlePane.remove(this.m_contentPane);
        }
        this.m_titlePane.add((Component)jComponent, gridBagConstraints);
        this.m_contentPane = jComponent;
    }

    public final String getHelpContext() {
        if (this.m_helpContext == null) {
            return null;
        }
        return this.m_helpContext.getHelpContext();
    }

    public Component getHelpExtractionDisplayContext() {
        if (this.m_helpContext == null) {
            return null;
        }
        return this.m_helpContext.getHelpExtractionDisplayContext();
    }

    public final void clearStatusMessage(int n) {
        if (this.m_idStatusMessage == n) {
            this.clearStatusMessage();
        }
    }

    public final void clearStatusMessage() {
        if (this.m_lblMessage != null) {
            this.m_lblMessage.setText("T");
            this.m_lblMessage.setPreferredSize(this.m_lblMessage.getPreferredSize());
            this.m_lblMessage.removeMouseListener(this.m_linkedListener);
            this.m_linkedListener = null;
            this.m_lblMessage.setText("");
            this.m_lblMessage.setToolTipText(null);
        }
    }

    public void doAfterUpdate() {
    }

    public final void printStatusMessage(String string) {
        this.printStatusMessage(string, 1);
    }

    public final void printStatusMessage(String string, int n) {
        this.printStatusMessage(string, n, null);
    }

    public final void printStatusMessage(String string, int n, URL uRL) {
        if (this.m_lblMessage != null) {
            this.printStatusMessageInternal(string, n, uRL);
        } else {
            JAPDialog.showConfirmDialog((Component)this.getContentPane(), string, -1, n);
        }
    }

    public final int printErrorStatusMessage(Throwable throwable) {
        return this.printErrorStatusMessage(null, throwable);
    }

    public final int printErrorStatusMessage(String string) {
        return this.printErrorStatusMessage(string, null);
    }

    public final int printErrorStatusMessage(String string, Throwable throwable) {
        return this.printErrorStatusMessage(string, throwable, true);
    }

    public final void validateDialog() {
        if (this.getDialog() instanceof JDialog) {
            ((JDialog)this.getDialog()).validate();
        } else {
            ((JAPDialog)this.getDialog()).validate();
        }
    }

    public final synchronized CheckError updateDialog() {
        return this.updateDialog(100, true);
    }

    private final synchronized CheckError updateDialog(boolean bl) {
        return this.updateDialog(100, bl);
    }

    private int printErrorStatusMessage(String string, Throwable throwable, boolean bl) {
        boolean bl2 = false;
        int n = 0;
        try {
            string = JAPDialog.retrieveErrorMessage(string, throwable);
            if (string == null) {
                string = JAPMessages.getString(JAPDialog.MSG_ERROR_UNKNOWN);
                bl2 = true;
            }
            if (this.m_lblMessage != null) {
                if (bl) {
                    n = this.printStatusMessageInternal(string, 0);
                }
                LogHolder.log(3, LogType.GUI, string, 1);
                if (throwable != null) {
                    if (bl2) {
                        LogHolder.log(3, LogType.GUI, throwable);
                    } else {
                        LogHolder.log(7, LogType.GUI, throwable);
                    }
                }
            } else if (bl) {
                JAPDialog.showErrorDialog((Component)this.getContentPane(), string, throwable);
            }
        }
        catch (Throwable throwable2) {
            JAPDialog.showErrorDialog((Component)this.getContentPane(), throwable2);
        }
        return n;
    }

    private final synchronized CheckError updateDialog(int n, boolean bl) {
        if (this.isDisposed()) {
            return null;
        }
        CheckError checkError = bl ? this.checkUpdate() : null;
        if (checkError != null) {
            return checkError;
        }
        this.createOptions();
        Object[] arrobject = new Object[]{this.m_panelOptions};
        JOptionPane jOptionPane = new JOptionPane(this.m_rootPane, this.m_layout.getMessageType(), 0, this.m_icon, arrobject);
        if (this.m_tempDialog != null) {
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                return new CheckError("Interrupted!");
            }
            this.m_tempDialog.dispose();
            if (this.m_tempDialog.isDisplayable()) {
                Thread.currentThread().interrupt();
            }
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                return new CheckError("Interrupted!");
            }
        }
        if (this.m_lblText != null) {
            this.m_titlePane.remove(this.m_lblText);
        }
        this.m_tempDialog = jOptionPane.createDialog(null, "");
        this.m_tempDialog.pack();
        if (this.m_lblText != null) {
            if (this.isDialogVisible()) {
                this.m_lblText = new JAPHtmlMultiLineLabel(this.m_lblText.getText(), this.m_lblText.getFont(), 0);
            } else if (this.m_lblText.getPreferredSize().width > this.m_contentPane.getWidth() - 10) {
                this.m_lblText.setPreferredWidth(Math.max(this.m_lblText.getMinimumSize().width, Math.max(this.m_contentPane.getWidth() - 10, n)));
            }
            this.m_titlePane.add((Component)this.m_lblText, this.m_textConstraints);
        }
        this.clearStatusMessage();
        this.doAfterUpdate();
        if (this.m_currentlyActiveContentPane != null) {
            this.m_currentlyActiveContentPane.removeComponentListener(this.m_currentlyActiveContentPaneComponentListener);
        }
        this.m_currentlyActiveContentPane = this.m_tempDialog.getContentPane();
        this.m_currentlyActiveContentPaneComponentListener = new ContentPaneComponentListener();
        this.m_currentlyActiveContentPane.addComponentListener(this.m_currentlyActiveContentPaneComponentListener);
        this.m_parentDialog.setContentPane(this.m_currentlyActiveContentPane);
        if (this.isDialogVisible()) {
            Vector vector = (Vector)this.m_componentListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((ComponentListener)vector.elementAt(i)).componentShown(new ComponentEvent(this.m_currentlyActiveContentPane, 102));
            }
        }
        if (this.m_defaultButton == 2) {
            this.getDialog().getRootPane().setDefaultButton(this.m_btnYesOK);
        } else if (this.m_defaultButton == 1) {
            this.getDialog().getRootPane().setDefaultButton(this.m_btnCancel);
        } else if (this.m_defaultButton == 3) {
            this.getDialog().getRootPane().setDefaultButton(this.m_btnNo);
        } else if (this.m_defaultButton == 4) {
            this.getDialog().getRootPane().setDefaultButton(this.getButtonHelp());
        } else if (this.m_defaultButton != 5) {
            this.getDialog().getRootPane().setDefaultButton(null);
        }
        this.m_titlePane.invalidate();
        if (this.m_lblText != null) {
            this.m_lblText.invalidate();
        }
        this.m_rootPane.invalidate();
        this.m_contentPane.invalidate();
        if (this.m_parentDialog instanceof JAPDialog) {
            ((JAPDialog)this.m_parentDialog).validate();
        } else {
            ((JDialog)this.m_parentDialog).validate();
        }
        return null;
    }

    protected final JButton getButtonHelp() {
        return this.m_btnHelp;
    }

    protected final JButton getButtonCancel() {
        return this.m_btnCancel;
    }

    protected final JButton getButtonYesOK() {
        return this.m_btnYesOK;
    }

    public void setButtonCancelEnabled(boolean bl) {
        if (this.m_btnCancel != null) {
            this.m_btnCancel.setEnabled(bl);
        }
    }

    public boolean hideButtonCancel() {
        return this.hasWizardLayout() && !this.hasNextContentPane();
    }

    public boolean hideButtonNo() {
        return this.hasWizardLayout() && !this.hasPreviousContentPane();
    }

    public boolean hideButtonYesOK() {
        return false;
    }

    public String getButtonYesOKText() {
        return null;
    }

    public String getButtonNoText() {
        return null;
    }

    public String getButtonCancelText() {
        return null;
    }

    protected final JButton getButtonNo() {
        return this.m_btnNo;
    }

    public final void setDefaultButton(int n) {
        this.m_defaultButton = n < 0 || n > 5 ? 0 : n;
    }

    public final int getDefaultButton() {
        return this.m_defaultButton;
    }

    public final int getDefaultButtonOperation() {
        return this.m_defaultButtonOperation;
    }

    public final void setDefaultButtonOperation(int n) throws IllegalArgumentException {
        this.m_defaultButtonOperation = n;
    }

    public Object getValue() {
        return null;
    }

    public final int getButtonValue() {
        return this.m_value;
    }

    public final void setButtonValue(int n) {
        this.m_value = 2 == n || 0 == n || -1 == n || 0 == n || 1 == n ? n : Integer.MIN_VALUE;
    }

    public final boolean hasValidValue() {
        return this.getButtonValue() != 2 && this.getButtonValue() != -1 && this.getButtonValue() != Integer.MIN_VALUE;
    }

    public boolean isAutomaticFocusSettingEnabled() {
        return true;
    }

    public final boolean isActive() {
        return this.m_currentlyActiveContentPane != null && this.m_parentDialog.getContentPane() == this.m_currentlyActiveContentPane;
    }

    public final boolean isVisible() {
        return this.isActive() && this.isDialogVisible();
    }

    public String getText() {
        return this.m_strText;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void setText(String string) {
        if (this.m_strText == null) {
            throw new IllegalStateException("This content pane does not contain a text field!");
        }
        RootPaneContainer rootPaneContainer = this.getDialog();
        synchronized (rootPaneContainer) {
            int n;
            if (this.isVisible()) {
                this.m_lblText.setText(JAPHtmlMultiLineLabel.removeHTMLHEADAndBODYTags(string));
                this.getDialog().notifyAll();
                return;
            }
            JAPDialog jAPDialog = new JAPDialog((JAPDialog)null, "");
            RootPaneContainer rootPaneContainer2 = this.getDialog();
            boolean bl = this.isActive();
            boolean bl2 = false;
            boolean bl3 = false;
            Dimension dimension = rootPaneContainer2 instanceof JDialog ? ((JDialog)rootPaneContainer2).getSize() : ((JAPDialog)rootPaneContainer2).getSize();
            if (dimension.width == 0 || dimension.height == 0) {
                throw new IllegalStateException("The parent dialog has a size <=0! This is not allowed when changing the text.");
            }
            jAPDialog.setSize(dimension);
            if (this.m_lblText != null) {
                this.m_titlePane.remove(this.m_lblText);
            }
            this.m_strText = JAPHtmlMultiLineLabel.removeHTMLHEADAndBODYTags(string);
            if (this.m_strText == null || this.m_strText.trim().length() == 0) {
                this.m_strText = "";
                return;
            }
            this.m_lblText = new JAPHtmlMultiLineLabel(this.m_strText, 0);
            this.m_lblText.setFontStyle(0);
            this.m_titlePane.add((Component)this.m_lblText, this.m_textConstraints);
            this.m_parentDialog = jAPDialog;
            if (this.m_rootPane.getPreferredSize().equals(new Dimension(2500, 2500))) {
                bl2 = true;
            }
            this.m_rootPane.setPreferredSize(null);
            if (this.m_lblSeeFullText != null) {
                this.m_titlePane.remove(this.m_lblSeeFullText);
                this.m_lblSeeFullText = null;
            }
            this.updateDialog(false);
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                return;
            }
            this.m_lblText.setText(this.m_strText);
            this.m_lblText.setPreferredWidth(this.getContentPane().getSize().width);
            GridBagConstraints gridBagConstraints = (GridBagConstraints)this.m_textConstraints.clone();
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            this.m_lblSeeFullText = new JAPHtmlMultiLineLabel();
            this.m_lblSeeFullText.setPreferredSize(new Dimension(this.getContentPane().getSize().width, 0));
            this.m_titlePane.add((Component)this.m_lblSeeFullText, gridBagConstraints);
            this.updateDialog(false);
            this.m_titlePane.remove(this.m_lblSeeFullText);
            if (jAPDialog.getContentPane().getSize().height < jAPDialog.getContentPane().getPreferredSize().height) {
                n = jAPDialog.getSize().height;
                int n2 = jAPDialog.getSize().width;
                jAPDialog.pack();
                if ((double)jAPDialog.getSize().height > (double)dimension.height * 1.2 || (double)jAPDialog.getSize().width > (double)dimension.width * 1.2) {
                    jAPDialog.setSize(n2, n);
                } else if (rootPaneContainer2 instanceof JDialog) {
                    ((JDialog)rootPaneContainer2).setSize(jAPDialog.getSize());
                } else {
                    ((JAPDialog)rootPaneContainer2).setSize(jAPDialog.getSize());
                }
            }
            if (jAPDialog.getContentPane().getSize().height < jAPDialog.getContentPane().getPreferredSize().height) {
                this.m_lblSeeFullText = new JAPHtmlMultiLineLabel("<A href=''>(" + JAPMessages.getString(MSG_SEE_FULL_MESSAGE) + ")</A>", this.m_lblText.getFont(), 0);
                this.m_lblSeeFullText.setCursor(Cursor.getPredefinedCursor(12));
                this.m_lblSeeFullText.setPreferredSize(new Dimension(this.getContentPane().getSize().width, this.m_lblSeeFullText.getPreferredSize().height));
                this.m_lblSeeFullText.addMouseListener(new MouseAdapter(){

                    public void mouseClicked(MouseEvent mouseEvent) {
                        if (DialogContentPane.this.m_layout.getTitle() != null) {
                            JAPDialog.showMessageDialog((Component)DialogContentPane.this.m_lblSeeFullText, DialogContentPane.this.m_strText, DialogContentPane.this.m_layout.getTitle());
                        } else {
                            JAPDialog.showMessageDialog(DialogContentPane.this.m_lblSeeFullText, DialogContentPane.this.m_strText);
                        }
                    }
                });
                this.m_titlePane.add((Component)this.m_lblSeeFullText, gridBagConstraints);
                int n3 = this.m_lblText.getHTMLDocumentLength();
                int n4 = n3 / 2;
                int n5 = 0;
                for (n = 0; n < 6; ++n) {
                    if (n == 5) {
                        if (bl3) {
                            n4 = n5;
                        } else {
                            this.m_titlePane.remove(this.m_lblText);
                            this.m_lblText = null;
                            this.updateDialog(false);
                            break;
                        }
                    }
                    this.m_lblText.setText(this.m_strText);
                    this.m_lblText.cutHTMLDocument(n4);
                    this.m_lblText.setText(JAPHtmlMultiLineLabel.removeHTMLHEADAndBODYTags(this.m_lblText.getText()) + MORE_POINTS);
                    this.updateDialog(false);
                    if (jAPDialog.getContentPane().getSize().height < jAPDialog.getContentPane().getPreferredSize().height) {
                        if (bl3) {
                            n4 = n5 + n5 / (n + 2);
                            continue;
                        }
                        n4 /= 2;
                        continue;
                    }
                    bl3 = true;
                    if (n5 < n4) {
                        n5 = n4;
                    }
                    n4 += n4 / 2;
                }
                if (bl3 && n5 >= n3) {
                    this.m_lblText.setText(this.m_strText);
                    this.m_titlePane.remove(this.m_lblSeeFullText);
                }
            }
            if (this.m_lblText != null) {
                this.m_lblText.setText("<font color=#000000>" + JAPHtmlMultiLineLabel.removeHTMLHEADAndBODYTags(this.m_lblText.getText()) + "</font>");
            }
            this.m_parentDialog = rootPaneContainer2;
            if (bl2) {
                this.m_rootPane.setPreferredSize(new Dimension(2500, 2500));
            }
            if (bl) {
                this.updateDialog(false);
            }
            this.getDialog().notifyAll();
        }
    }

    public RootPaneContainer getDialog() {
        return this.m_parentDialog;
    }

    private Container getDialogContentPane() {
        if (this.m_parentDialog instanceof JAPDialog) {
            return ((JAPDialog)this.m_parentDialog).getContentPane();
        }
        return ((JDialog)this.m_parentDialog).getContentPane();
    }

    public final boolean isDialogVisible() {
        return this.m_parentDialog instanceof JAPDialog && ((JAPDialog)this.m_parentDialog).isVisible() || this.m_parentDialog instanceof JDialog && ((JDialog)this.m_parentDialog).isVisible();
    }

    public void addDialogWindowListener(WindowListener windowListener) {
        if (this.m_parentDialog instanceof JDialog) {
            ((JDialog)this.m_parentDialog).addWindowListener(windowListener);
        } else {
            ((JAPDialog)this.m_parentDialog).addWindowListener(windowListener);
        }
    }

    public synchronized void addComponentListener(ComponentListener componentListener) {
        if (componentListener != null) {
            this.m_componentListeners.addElement(componentListener);
        }
    }

    public synchronized void removeComponentListener(ComponentListener componentListener) {
        this.m_componentListeners.removeElement(componentListener);
    }

    public void addDialogComponentListener(ComponentListener componentListener) {
        if (this.m_parentDialog instanceof JDialog) {
            ((JDialog)this.m_parentDialog).addComponentListener(componentListener);
        } else {
            ((JAPDialog)this.m_parentDialog).addComponentListener(componentListener);
        }
    }

    public void removeDialogComponentListener(ComponentListener componentListener) {
        if (this.m_parentDialog instanceof JDialog) {
            ((JDialog)this.m_parentDialog).removeComponentListener(componentListener);
        } else {
            ((JAPDialog)this.m_parentDialog).removeComponentListener(componentListener);
        }
    }

    public void removeDialogWindowListener(WindowListener windowListener) {
        if (this.m_parentDialog instanceof JDialog) {
            ((JDialog)this.m_parentDialog).removeWindowListener(windowListener);
        } else {
            ((JAPDialog)this.m_parentDialog).removeWindowListener(windowListener);
        }
    }

    public boolean isDisposed() {
        return this.m_bDisposed;
    }

    public synchronized void dispose() {
        this.m_bDisposed = true;
        if (this.m_tempDialog != null) {
            this.m_tempDialog.dispose();
        }
        if (this.m_titlePane != null) {
            this.m_titlePane.removeAll();
        }
        this.m_titlePane = null;
        if (this.m_rootPane != null) {
            this.m_rootPane.removeAll();
        }
        this.m_rootPane = null;
        if (this.m_contentPane != null) {
            this.m_contentPane.removeAll();
        }
        this.m_contentPane = null;
        if (this.m_panelOptions != null) {
            this.m_panelOptions.removeAll();
        }
        this.m_panelOptions = null;
        this.m_parentDialog = null;
        this.m_lblText = null;
        this.m_componentListeners.removeAllElements();
        if (this.m_btnCancel != null) {
            this.m_btnCancel.removeActionListener(this.m_buttonListener);
        }
        if (this.m_btnYesOK != null) {
            this.m_btnYesOK.removeActionListener(this.m_buttonListener);
        }
        if (this.m_btnNo != null) {
            this.m_btnNo.removeActionListener(this.m_buttonListener);
        }
        if (this.m_btnHelp != null) {
            this.m_btnHelp.removeActionListener(this.m_buttonListener);
        }
        this.m_buttonListener = null;
        if (this.m_currentlyActiveContentPane != null) {
            this.m_currentlyActiveContentPane.removeComponentListener(this.m_currentlyActiveContentPaneComponentListener);
        }
        this.m_currentlyActiveContentPaneComponentListener = null;
        this.m_currentlyActiveContentPane = null;
        this.m_nextContentPane = null;
        this.m_previousContentPane = null;
    }

    public final void closeDialog(boolean bl) {
        block10: {
            try {
                if (bl) {
                    if (this.m_parentDialog instanceof JDialog) {
                        ((JDialog)this.m_parentDialog).dispose();
                    } else {
                        try {
                            ((JAPDialog)this.m_parentDialog).dispose();
                        }
                        catch (IllegalMonitorStateException illegalMonitorStateException) {
                            LogHolder.log(7, LogType.GUI, illegalMonitorStateException);
                        }
                    }
                } else if (this.m_parentDialog instanceof JDialog) {
                    ((JDialog)this.m_parentDialog).setVisible(false);
                } else {
                    ((JAPDialog)this.m_parentDialog).setVisible(false);
                }
            }
            catch (NullPointerException nullPointerException) {
                if (this.isDisposed()) break block10;
                throw nullPointerException;
            }
        }
    }

    private JAPDialog getJAPDialog() {
        if (this.m_parentDialog instanceof JAPDialog) {
            return (JAPDialog)this.m_parentDialog;
        }
        return null;
    }

    private static Icon findMessageIcon(JOptionPane jOptionPane) {
        Icon icon = null;
        block0: for (int i = 0; i < jOptionPane.getComponentCount(); ++i) {
            if (!(jOptionPane.getComponent(i) instanceof Container)) continue;
            Container container = (Container)jOptionPane.getComponent(i);
            for (int j = 0; j < container.getComponentCount(); ++j) {
                if (!(container.getComponent(j) instanceof JLabel)) continue;
                icon = ((JLabel)container.getComponent(j)).getIcon();
                continue block0;
            }
        }
        return icon;
    }

    private final synchronized int printStatusMessageInternal(String string, int n) {
        return this.printStatusMessageInternal(string, n, null);
    }

    private final synchronized int printStatusMessageInternal(String string, int n, final URL uRL) {
        String string2;
        if (string == null || string.trim().length() == 0) {
            return 0;
        }
        String string3 = JAPHtmlMultiLineLabel.removeTagsAndNewLines(string);
        String string4 = 0 == n || 2 == n ? "red" : "black";
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel(string3, this.m_lblMessage.getFont());
        if (jAPHtmlMultiLineLabel.getPreferredSize().width > this.m_lblMessage.getSize().width) {
            String string5 = 0 == n ? JAPMessages.getString(JAPDialog.MSG_TITLE_ERROR) : (2 == n ? JAPMessages.getString(JAPDialog.MSG_TITLE_WARNING) : JAPMessages.getString(JAPDialog.MSG_TITLE_INFO));
            this.clearStatusMessage();
            int n2 = 0;
            int n3 = string3.length() / 2;
            for (int i = 0; n3 > 1 && (i < 6 || n2 >= string3.length()); ++i) {
                jAPHtmlMultiLineLabel.setText(string3.substring(0, n3) + MORE_POINTS);
                if (jAPHtmlMultiLineLabel.getPreferredSize().width <= this.m_lblMessage.getSize().width) {
                    n2 = Math.max(n2, n3) - 2;
                    n3 += n3 / (i + 2);
                    continue;
                }
                n3 /= 2;
            }
            string3 = n2 <= 5 ? MORE_POINTS : string3.substring(0, n2) + MORE_POINTS;
            if (uRL == null) {
                string2 = " href=\"\"";
                this.m_lblMessage.setToolTipText(JAPMessages.getString(MSG_SEE_FULL_MESSAGE));
                this.m_linkedListener = new LinkedDialog(string, string5, -1, n);
                this.m_lblMessage.addMouseListener(this.m_linkedListener);
                this.m_lblMessage.setCursor(Cursor.getPredefinedCursor(12));
            } else {
                string2 = "";
            }
        } else {
            this.clearStatusMessage();
            string2 = "";
        }
        if (uRL != null) {
            string2 = " href=\"\"";
            this.m_lblMessage.setToolTipText(JAPMessages.getString(MSG_OPEN_IN_BROWSER));
            this.m_linkedListener = new MouseAdapter(){

                public void mouseClicked(MouseEvent mouseEvent) {
                    AbstractOS.getInstance().openURL(uRL);
                }
            };
            this.m_lblMessage.addMouseListener(this.m_linkedListener);
            this.m_lblMessage.setCursor(Cursor.getPredefinedCursor(12));
        }
        string3 = "<A style=\"color:" + string4 + "\"" + string2 + "> " + string3 + " </A>";
        this.m_lblMessage.setText(string3);
        ++this.m_idStatusMessage;
        return this.m_idStatusMessage;
    }

    private void setNextContentPane(DialogContentPane dialogContentPane) {
        if (this.m_nextContentPane != null) {
            this.m_nextContentPane.m_previousContentPane = null;
        }
        this.m_nextContentPane = dialogContentPane;
    }

    private DialogContentPane moveToContentPane(boolean bl) {
        CheckError checkError = null;
        DialogContentPane dialogContentPane = this;
        if (bl) {
            while ((dialogContentPane = dialogContentPane.getNextContentPane()) != null && dialogContentPane.isSkippedAsNextContentPane()) {
            }
        } else {
            while ((dialogContentPane = dialogContentPane.getPreviousContentPane()) != null && dialogContentPane.isSkippedAsPreviousContentPane()) {
            }
        }
        if (dialogContentPane != null) {
            checkError = dialogContentPane.updateDialog();
            boolean bl2 = false;
            this.checkErrors(null, this.m_rememberedUpdateErrors);
            if (checkError != null) {
                dialogContentPane = bl && this.isSkippedAsPreviousContentPane() || !bl && this.isSkippedAsNextContentPane() ? this.moveToContentPane(!bl) : this;
                dialogContentPane.checkErrors(checkError, this.m_rememberedUpdateErrors);
                return dialogContentPane;
            }
            if (dialogContentPane.isVisible()) {
                if (bl) {
                    if (dialogContentPane.m_btnYesOK != null && dialogContentPane.m_btnYesOK.isEnabled()) {
                        if (dialogContentPane.isAutomaticFocusSettingEnabled()) {
                            dialogContentPane.m_btnYesOK.requestFocus();
                        }
                        this.getDialog().getRootPane().setDefaultButton(dialogContentPane.m_btnYesOK);
                        bl2 = true;
                    }
                } else if (dialogContentPane.m_btnYesOK != null && dialogContentPane.m_btnYesOK.isEnabled()) {
                    if (dialogContentPane.isAutomaticFocusSettingEnabled()) {
                        dialogContentPane.m_btnYesOK.requestFocus();
                    }
                    this.getDialog().getRootPane().setDefaultButton(dialogContentPane.m_btnYesOK);
                    bl2 = true;
                } else if (dialogContentPane.m_btnNo != null && dialogContentPane.m_btnNo.isEnabled()) {
                    if (dialogContentPane.isAutomaticFocusSettingEnabled()) {
                        dialogContentPane.getButtonNo().requestFocus();
                    }
                    this.getDialog().getRootPane().setDefaultButton(dialogContentPane.getButtonNo());
                    bl2 = true;
                }
                if (!bl2 && dialogContentPane.m_btnCancel != null && dialogContentPane.m_btnCancel.isEnabled()) {
                    if (dialogContentPane.isAutomaticFocusSettingEnabled()) {
                        dialogContentPane.m_btnCancel.requestFocus();
                    }
                    this.getDialog().getRootPane().setDefaultButton(dialogContentPane.m_btnCancel);
                }
            }
        } else {
            dialogContentPane = this;
            JAPDialog jAPDialog = this.getJAPDialog();
            if ((this.getDefaultButtonOperation() & 2) > 0) {
                this.closeDialog(true);
            } else if ((this.getDefaultButtonOperation() & 1) > 0) {
                this.closeDialog(false);
            } else if (jAPDialog != null && jAPDialog.getDefaultCloseOperation() == 2) {
                this.closeDialog(true);
            } else if (jAPDialog != null && jAPDialog.getDefaultCloseOperation() == 1) {
                this.closeDialog(true);
            }
        }
        return dialogContentPane;
    }

    private boolean checkErrors(CheckError checkError, Vector vector) {
        for (int i = vector.size() - 1; i >= 0; --i) {
            ((CheckError)vector.elementAt(i)).undoErrorAction();
            vector.removeElementAt(i);
        }
        if (checkError != null) {
            if (checkError.hasDisplayableErrorMessage()) {
                this.printErrorStatusMessage(checkError.getMessage(), checkError.getThrowable(), false);
            }
            vector.addElement(checkError);
            checkError.doErrorAction();
            if (checkError.hasDisplayableErrorMessage()) {
                this.printStatusMessage(JAPDialog.retrieveErrorMessage(checkError.getMessage(), checkError.getThrowable()), 0, checkError.getURL());
            }
            return false;
        }
        return true;
    }

    private void createDefaultOptions() {
        this.m_panelOptions = new JPanel();
        if (this.m_btnCancel == null) {
            this.m_btnCancel = new JButton();
            this.m_btnCancel.addActionListener(this.m_buttonListener);
        }
        this.m_btnCancel.setText(JAPMessages.getString(MSG_CANCEL));
        String string = this.getButtonCancelText();
        if (string != null) {
            this.m_btnCancel.setText(string);
        }
        this.m_panelOptions.add(this.m_btnCancel);
        this.m_btnCancel.setVisible(false);
        if (!(this.hideButtonCancel() || 1 != this.m_options.getOptionType() && 2 != this.m_options.getOptionType() && -2147483647 != this.m_options.getOptionType())) {
            this.m_btnCancel.setVisible(true);
        }
        if (this.m_btnNo == null) {
            this.m_btnNo = new JButton();
            this.m_btnNo.addActionListener(this.m_buttonListener);
        }
        this.m_btnNo.setText(JAPMessages.getString(MSG_NO));
        string = this.getButtonNoText();
        if (string != null) {
            this.m_btnNo.setText(string);
        }
        this.m_panelOptions.add(this.m_btnNo);
        this.m_btnNo.setVisible(false);
        if (0 == this.m_options.getOptionType() || 1 == this.m_options.getOptionType()) {
            if (!this.hideButtonNo()) {
                this.m_btnNo.setVisible(true);
            }
            if (this.m_btnYesOK == null) {
                this.m_btnYesOK = new JButton();
                this.m_btnYesOK.addActionListener(this.m_buttonListener);
            }
            this.m_btnYesOK.setText(JAPMessages.getString(MSG_YES));
            string = this.getButtonYesOKText();
            if (string != null) {
                this.m_btnYesOK.setText(string);
            }
            this.m_panelOptions.add(this.m_btnYesOK);
        } else if (2 == this.m_options.getOptionType() || -1 == this.m_options.getOptionType()) {
            if (this.m_btnYesOK == null) {
                this.m_btnYesOK = new JButton();
                this.m_btnYesOK.addActionListener(this.m_buttonListener);
            }
            this.m_btnYesOK.setText(JAPMessages.getString(MSG_OK));
            string = this.getButtonYesOKText();
            if (string != null) {
                this.m_btnYesOK.setText(string);
            }
            this.m_panelOptions.add(this.m_btnYesOK);
        }
        if (this.m_btnYesOK != null & this.hideButtonYesOK()) {
            this.m_btnYesOK.setVisible(false);
        }
        this.addHelpButton();
    }

    private JButton addHelpButton() {
        if (this.getHelpContext() != null) {
            if (this.m_btnHelp == null) {
                if (this.m_helpContext instanceof JAPHelpContext.IURLHelpContext) {
                    this.m_btnHelp = new JButton(((JAPHelpContext.IURLHelpContext)this.m_helpContext).getURLMessage());
                    this.m_btnHelp.addActionListener(new ActionListener(){

                        public void actionPerformed(ActionEvent actionEvent) {
                            AbstractOS.getInstance().openURL(((JAPHelpContext.IURLHelpContext)DialogContentPane.this.m_helpContext).getHelpURL());
                        }
                    });
                } else {
                    this.m_btnHelp = JAPHelp.createHelpButton(this);
                }
            }
            this.m_panelOptions.add(this.m_btnHelp);
        }
        return this.m_btnHelp;
    }

    private void createWizardOptions() {
        this.m_panelOptions = Box.createHorizontalBox();
        if (this.addHelpButton() != null) {
            this.m_panelOptions.add(Box.createHorizontalStrut(5));
        }
        if (this.m_btnNo == null) {
            this.m_btnNo = new JButton();
            this.m_btnNo.addActionListener(this.m_buttonListener);
        }
        this.m_btnNo.setText("< " + JAPMessages.getString(MSG_PREVIOUS));
        String string = this.getButtonNoText();
        if (string != null) {
            this.m_btnNo.setText(string);
        }
        this.m_panelOptions.add(this.m_btnNo);
        this.m_btnNo.setEnabled(!this.hideButtonNo());
        this.m_btnNo.setVisible(!(this instanceof WorkerContentPane));
        if (this.m_btnYesOK == null) {
            this.m_btnYesOK = new JButton();
            this.m_btnYesOK.addActionListener(this.m_buttonListener);
        }
        this.setTextOfWizardNextButton();
        this.m_panelOptions.add(this.m_btnYesOK);
        this.m_btnYesOK.setEnabled(!this.hideButtonYesOK());
        this.m_btnYesOK.setVisible(!(this instanceof WorkerContentPane));
        this.m_panelOptions.add(Box.createHorizontalStrut(5));
        if (this.m_btnCancel == null) {
            this.m_btnCancel = new JButton();
            this.m_btnCancel.addActionListener(this.m_buttonListener);
        }
        this.m_btnCancel.setText(JAPMessages.getString(MSG_CANCEL));
        string = this.getButtonCancelText();
        if (string != null) {
            this.m_btnCancel.setText(string);
        }
        this.m_panelOptions.add(this.m_btnCancel);
        this.m_btnCancel.setEnabled(!this.hideButtonCancel());
        this.m_btnCancel.setVisible(true);
    }

    private void setTextOfWizardNextButton() {
        String string = this.getButtonYesOKText();
        if (string != null) {
            this.m_btnYesOK.setText(string);
        } else if (this.hasNextContentPane() || this.hasPreviousContentPane() && (this.getDefaultButtonOperation() & 0x80) > 0) {
            this.m_btnYesOK.setText(JAPMessages.getString(MSG_NEXT) + " >");
        } else {
            this.m_btnYesOK.setText(JAPMessages.getString(MSG_FINISH));
        }
        this.m_btnYesOK.invalidate();
    }

    public void setMouseListener(MouseListener mouseListener) {
        if (this.m_strText == null) {
            throw new IllegalStateException("This content pane does not contain a text field!");
        }
        this.m_lblText.addMouseListener(mouseListener);
        this.m_lblSeeFullText.addMouseListener(mouseListener);
    }

    private void createOptions() {
        boolean bl = this.hasWizardLayout();
        if (this.m_buttonListener == null) {
            this.m_buttonListener = new ButtonListener();
        }
        if (bl) {
            this.createWizardOptions();
        } else {
            this.createDefaultOptions();
        }
    }

    private boolean isSomethingDoneOnClick(CheckError checkError, int n, int n2, int n3, int n4, int n5) {
        boolean bl;
        boolean bl2 = bl = checkError == null && (this.getDefaultButtonOperation() & (0x47 | n | n2 | n3 | n4)) > 0;
        if (checkError == null && !bl && this.getJAPDialog() != null) {
            bl = (this.getDefaultButtonOperation() & (0x80000 | n5)) > 0;
        }
        return bl;
    }

    private CheckError doDefaultButtonOperation(CheckError checkError, int n, int n2, int n3, int n4, int n5) {
        if (!this.checkErrors(checkError, this.m_rememberedErrors)) {
            return checkError;
        }
        if (this.m_nextContentPane != null && (this.getDefaultButtonOperation() & n) > 0 && this.m_nextContentPane.isMoveForwardAllowed()) {
            this.moveToContentPane(true);
            return null;
        }
        if (this.m_previousContentPane != null && (this.getDefaultButtonOperation() & n2) > 0 && this.m_previousContentPane.isMoveBackAllowed()) {
            this.moveToContentPane(false);
            return null;
        }
        if ((this.getDefaultButtonOperation() & n4) > 0) {
            this.closeDialog(true);
            return null;
        }
        if ((this.getDefaultButtonOperation() & n3) > 0) {
            this.closeDialog(false);
            return null;
        }
        if (this.getJAPDialog() != null && (this.getDefaultButtonOperation() & n5) > 0) {
            this.getJAPDialog().doWindowClosing();
            return null;
        }
        return new CheckError(){

            public void doErrorAction() {
                DialogContentPane.this.doDefaultButtonOperation(null, 4, 64, 1, 2, 524288);
            }
        };
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        if (!JAPDialog.isConsoleOnly()) {
            for (int i = 0; i < MESSAGE_TYPES.length; ++i) {
                JOptionPane jOptionPane = new JOptionPane("", MESSAGE_TYPES[i]);
                jOptionPane.createDialog(null, "");
                DialogContentPane.MESSAGE_ICONS[i] = DialogContentPane.findMessageIcon(jOptionPane);
            }
        }
        MSG_OK = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_OK";
        MSG_YES = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_yes";
        MSG_NO = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_no";
        MSG_NEXT = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_next";
        MSG_PREVIOUS = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_previous";
        MSG_FINISH = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_finish";
        MSG_CANCEL = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_cancel";
        MSG_IGNORE = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + ".ignore";
        MSG_CONTINUE = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_continue";
        MSG_OPERATION_FAILED = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_operationFailed";
        MSG_SEE_FULL_MESSAGE = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + "_seeFullMessage";
        MSG_OPEN_IN_BROWSER = (class$gui$dialog$DialogContentPane == null ? (class$gui$dialog$DialogContentPane = DialogContentPane.class$("gui.dialog.DialogContentPane")) : class$gui$dialog$DialogContentPane).getName() + ".openInBrowser";
    }

    private class ButtonListener
    implements ActionListener {
        private ButtonListener() {
        }

        public void actionPerformed(ActionEvent actionEvent) {
            CheckError checkError;
            CheckError checkError2;
            if (actionEvent == null || actionEvent.getSource() == null) {
                return;
            }
            if (actionEvent.getSource() == DialogContentPane.this.m_btnCancel) {
                checkError2 = DialogContentPane.this.checkCancel();
                if (DialogContentPane.this.isSomethingDoneOnClick(checkError2, 32, 512, 4096, 32768, 65536)) {
                    DialogContentPane.this.setButtonValue(2);
                }
                checkError = DialogContentPane.this.doDefaultButtonOperation(checkError2, 32, 512, 4096, 32768, 65536);
            } else if (actionEvent.getSource() == DialogContentPane.this.m_btnYesOK) {
                checkError2 = DialogContentPane.this.checkYesOK();
                if (DialogContentPane.this.isSomethingDoneOnClick(checkError2, 8, 128, 1024, 8192, 131072)) {
                    if (0 == DialogContentPane.this.m_options.getOptionType() || 1 == DialogContentPane.this.m_options.getOptionType()) {
                        DialogContentPane.this.setButtonValue(0);
                    } else {
                        DialogContentPane.this.setButtonValue(0);
                    }
                }
                checkError = DialogContentPane.this.doDefaultButtonOperation(checkError2, 8, 128, 1024, 8192, 131072);
            } else {
                checkError2 = DialogContentPane.this.checkNo();
                if (DialogContentPane.this.isSomethingDoneOnClick(checkError2, 16, 256, 2048, 16384, 262144)) {
                    DialogContentPane.this.setButtonValue(1);
                }
                checkError = DialogContentPane.this.doDefaultButtonOperation(checkError2, 16, 256, 2048, 16384, 262144);
            }
            if (checkError != null && checkError2 == null) {
                DialogContentPane.this.checkErrors(checkError, DialogContentPane.this.m_rememberedErrors);
            }
        }
    }

    private class ContentPaneComponentListener
    extends ComponentAdapter {
        private ContentPaneComponentListener() {
        }

        public void componentHidden(ComponentEvent componentEvent) {
            Vector vector = (Vector)DialogContentPane.this.m_componentListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((ComponentListener)vector.elementAt(i)).componentHidden(componentEvent);
            }
        }

        public void componentShown(ComponentEvent componentEvent) {
            if (DialogContentPane.this.isVisible()) {
                if (DialogContentPane.this.m_lblText != null) {
                    DialogContentPane.this.m_titlePane.remove(DialogContentPane.this.m_lblText);
                    DialogContentPane.this.m_lblText = new JAPHtmlMultiLineLabel(DialogContentPane.this.m_lblText.getText(), DialogContentPane.this.m_lblText.getFont(), 0);
                    DialogContentPane.this.m_titlePane.add((Component)DialogContentPane.this.m_lblText, DialogContentPane.this.m_textConstraints);
                    DialogContentPane.this.m_titlePane.revalidate();
                }
                Vector vector = (Vector)DialogContentPane.this.m_componentListeners.clone();
                for (int i = 0; i < vector.size(); ++i) {
                    ((ComponentListener)vector.elementAt(i)).componentShown(componentEvent);
                }
            }
        }

        public void componentResized(ComponentEvent componentEvent) {
            Vector vector = (Vector)DialogContentPane.this.m_componentListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((ComponentListener)vector.elementAt(i)).componentResized(componentEvent);
            }
        }

        public void componentMoved(ComponentEvent componentEvent) {
            Vector vector = (Vector)DialogContentPane.this.m_componentListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((ComponentListener)vector.elementAt(i)).componentMoved(componentEvent);
            }
        }
    }

    private class DialogComponentListener
    extends ComponentAdapter {
        private DialogComponentListener() {
        }

        public void componentHidden(ComponentEvent componentEvent) {
            ComponentListener componentListener;
            if (DialogContentPane.this.getButtonValue() == Integer.MIN_VALUE) {
                DialogContentPane.this.setButtonValue(-1);
            }
            if ((componentListener = DialogContentPane.this.m_currentlyActiveContentPaneComponentListener) != null) {
                componentListener.componentHidden(new ComponentEvent(DialogContentPane.this.m_currentlyActiveContentPane, 103));
            }
        }

        public void componentShown(ComponentEvent componentEvent) {
        }
    }

    private class DialogWindowListener
    extends WindowAdapter {
        private DialogWindowListener() {
        }

        public void windowClosed(WindowEvent windowEvent) {
            ComponentListener componentListener;
            if (DialogContentPane.this.getButtonValue() == Integer.MIN_VALUE) {
                DialogContentPane.this.setButtonValue(-1);
            }
            if ((componentListener = DialogContentPane.this.m_currentlyActiveContentPaneComponentListener) != null) {
                componentListener.componentHidden(new ComponentEvent(DialogContentPane.this.m_currentlyActiveContentPane, 103));
            }
            if (!DialogContentPane.this.isDisposed()) {
                DialogContentPane.this.dispose();
            }
        }

        public void windowOpened(WindowEvent windowEvent) {
            ComponentListener componentListener;
            if (DialogContentPane.this.isVisible() && DialogContentPane.this.hasWizardLayout() && DialogContentPane.this.m_btnYesOK != null && DialogContentPane.this.m_btnYesOK.isEnabled()) {
                if (DialogContentPane.this.isAutomaticFocusSettingEnabled()) {
                    DialogContentPane.this.m_btnYesOK.requestFocus();
                }
                DialogContentPane.this.getDialog().getRootPane().setDefaultButton(DialogContentPane.this.m_btnYesOK);
            }
            if (DialogContentPane.this.getDialog() instanceof JDialog && (componentListener = DialogContentPane.this.m_currentlyActiveContentPaneComponentListener) != null) {
                componentListener.componentShown(new ComponentEvent(DialogContentPane.this.m_currentlyActiveContentPane, 102));
            }
        }
    }

    private class LinkedDialog
    extends MouseAdapter {
        private String m_strMessage;
        private String m_strTitle;
        private int m_optionType;
        private int m_messageType;

        public LinkedDialog(String string, String string2, int n, int n2) {
            this.m_strMessage = string;
            this.m_strTitle = string2;
            this.m_optionType = n;
            this.m_messageType = n2;
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            JAPDialog.showConfirmDialog((Component)DialogContentPane.this.m_lblMessage, this.m_strMessage, this.m_strTitle, this.m_optionType, this.m_messageType);
        }
    }

    public static class Layout {
        private String m_strTitle;
        private int m_messageType;
        private Icon m_icon;
        private boolean m_bCentered;

        public Layout() {
            this("", -1, null);
        }

        public Layout(int n) {
            this("", n, null);
        }

        public Layout(String string) {
            this(string, -1, null);
        }

        public Layout(Icon icon) {
            this("", -1, icon);
        }

        public Layout(int n, Icon icon) {
            this("", n, icon);
        }

        public Layout(String string, int n) {
            this(string, n, null);
        }

        public Layout(String string, Icon icon) {
            this(string, -1, icon);
        }

        public Layout(String string, int n, Icon icon) {
            this.m_strTitle = string;
            this.m_messageType = n;
            this.m_icon = icon;
            this.m_bCentered = true;
        }

        public boolean isCentered() {
            return this.m_bCentered;
        }

        public final String getTitle() {
            return this.m_strTitle;
        }

        public final int getMessageType() {
            return this.m_messageType;
        }

        public final Icon getIcon() {
            return this.m_icon;
        }
    }

    public static class CheckError {
        private String m_strMessage;
        private Throwable m_throwable;
        private URL m_url;

        public CheckError() {
            this("", null, null);
        }

        public CheckError(String string) {
            this(string, null, null);
        }

        public CheckError(String string, URL uRL) {
            this(string, null, uRL);
        }

        public CheckError(String string, Throwable throwable) {
            this.m_strMessage = string;
            this.m_throwable = throwable;
        }

        public CheckError(String string, Throwable throwable, URL uRL) {
            this.m_strMessage = string;
            this.m_throwable = throwable;
            this.m_url = uRL;
        }

        public URL getURL() {
            return this.m_url;
        }

        public void doErrorAction() {
        }

        public void undoErrorAction() {
        }

        public final Throwable getThrowable() {
            return this.m_throwable;
        }

        public final String getMessage() {
            return this.m_strMessage;
        }

        public final boolean hasDisplayableErrorMessage() {
            return JAPDialog.retrieveErrorMessage(this.m_strMessage, this.m_throwable) != null;
        }
    }

    public static interface IWizardSuitable {
    }
}

