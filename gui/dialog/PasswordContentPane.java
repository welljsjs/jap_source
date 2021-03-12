/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.util.IMiscPasswordReader;
import anon.util.JAPMessages;
import anon.util.Util;
import gui.GUIUtils;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;

public class PasswordContentPane
extends DialogContentPane
implements IMiscPasswordReader,
DialogContentPane.IWizardSuitable {
    public static final int PASSWORD_NEW = 1;
    public static final int PASSWORD_ENTER = 2;
    public static final int PASSWORD_CHANGE = 3;
    public static final int NO_MINIMUM_LENGTH = 0;
    public static final String MSG_ENTER_LBL = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_enterPasswordLabel";
    private static final int FIELD_LENGTH = 15;
    private static final String MSG_TOO_SHORT = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_tooShort";
    private static final String MSG_CAPS_LOCK_PRESSED = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_pressedCapsLock";
    private static final String MSG_WRONG_PASSWORD = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_wrongPassword";
    public static final String MSG_ENTER_PASSWORD_TITLE = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_title";
    private static final String MSG_CONFIRM_LBL = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_confirmPasswordLabel";
    private static final String MSG_ENTER_OLD_LBL = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_enterOldPasswordLabel";
    private static final String MSG_ENTER_NEW_LBL = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_enterNewPasswordLabel";
    private static final String MSG_PASSWORDS_DONT_MATCH = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_passwordsDontMatch";
    private static final String MSG_INSERT_FROM_CLIP = (class$gui$dialog$PasswordContentPane == null ? (class$gui$dialog$PasswordContentPane = PasswordContentPane.class$("gui.dialog.PasswordContentPane")) : class$gui$dialog$PasswordContentPane).getName() + "_insertFromClipboard";
    private JPasswordField m_textOldPasswd;
    private JPasswordField m_textNewPasswd;
    private JPasswordField m_textConfirmPasswd;
    private char[] m_passwd = null;
    private char[] m_oldPasswd = null;
    private int m_type;
    private int m_minLength;
    private JLabel m_lblNew1;
    private JLabel m_lblNew2;
    private JLabel m_lblOld;
    private JPopupMenu m_popup = new JPopupMenu();
    private JPasswordField m_currentPopup;
    static /* synthetic */ Class class$gui$dialog$PasswordContentPane;
    static /* synthetic */ Class class$java$awt$Toolkit;

    public PasswordContentPane(JAPDialog jAPDialog, int n, String string, int n2) {
        this(jAPDialog, null, n, string, n2);
    }

    public PasswordContentPane(JAPDialog jAPDialog, int n, String string) {
        this(jAPDialog, null, n, string, 0);
    }

    public PasswordContentPane(JAPDialog jAPDialog, DialogContentPane dialogContentPane, int n, String string) {
        this(jAPDialog, dialogContentPane, n, string, 0);
    }

    public PasswordContentPane(JAPDialog jAPDialog, DialogContentPane dialogContentPane, int n, String string, int n2) {
        super(jAPDialog, string != null ? string : "", new DialogContentPane.Layout(JAPMessages.getString(MSG_ENTER_PASSWORD_TITLE), 3), new DialogContentPaneOptions(2, dialogContentPane));
        this.setDefaultButtonOperation(266);
        if (n < 1 || n > 3) {
            throw new IllegalArgumentException("Unknown type!");
        }
        this.m_type = n;
        if (n2 < 0) {
            n2 = 0;
        }
        this.m_minLength = n2;
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JMenuItem jMenuItem = new JMenuItem(JAPMessages.getString(MSG_INSERT_FROM_CLIP));
        MouseAdapter mouseAdapter = new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (GUIUtils.isMouseButton(mouseEvent, 8) || GUIUtils.isMouseButton(mouseEvent, 4)) {
                    PasswordContentPane.this.m_currentPopup = (JPasswordField)mouseEvent.getComponent();
                    PasswordContentPane.this.m_popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        };
        this.getContentPane().setLayout(gridBagLayout);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.m_popup = new JPopupMenu();
        jMenuItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = GUIUtils.getSystemClipboard();
                Transferable transferable = clipboard.getContents(this);
                if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        PasswordContentPane.this.m_currentPopup.setText((String)transferable.getTransferData(DataFlavor.stringFlavor));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        });
        this.m_popup.add(jMenuItem);
        if (n == 3) {
            this.m_lblOld = new JLabel(this.getOldPasswordLabel());
            gridBagLayout.setConstraints(this.m_lblOld, gridBagConstraints);
            this.getContentPane().add(this.m_lblOld);
            this.m_textOldPasswd = new JPasswordField(15);
            this.m_textOldPasswd.setEchoChar('*');
            this.m_textOldPasswd.addMouseListener(mouseAdapter);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = 2;
            gridBagLayout.setConstraints(this.m_textOldPasswd, gridBagConstraints);
            this.getContentPane().add(this.m_textOldPasswd);
        }
        if (n == 3 || n == 1) {
            this.m_lblNew1 = new JLabel(this.getNewPasswordLabel());
            gridBagConstraints.gridx = 0;
            ++gridBagConstraints.gridy;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.fill = 0;
            this.getContentPane().add((Component)this.m_lblNew1, gridBagConstraints);
            this.m_textNewPasswd = new JPasswordField(15);
            this.m_textNewPasswd.setEchoChar('*');
            this.m_textNewPasswd.addMouseListener(mouseAdapter);
            gridBagConstraints.fill = 2;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagLayout.setConstraints(this.m_textNewPasswd, gridBagConstraints);
            this.getContentPane().add(this.m_textNewPasswd);
        }
        this.m_lblNew2 = n == 2 ? new JLabel(JAPMessages.getString(MSG_ENTER_LBL)) : new JLabel(JAPMessages.getString(MSG_CONFIRM_LBL));
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridx = 0;
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 0.0;
        this.getContentPane().add((Component)this.m_lblNew2, gridBagConstraints);
        this.m_textConfirmPasswd = new JPasswordField(15);
        this.m_textConfirmPasswd.setEchoChar('*');
        this.m_textConfirmPasswd.addMouseListener(mouseAdapter);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        gridBagLayout.setConstraints(this.m_textConfirmPasswd, gridBagConstraints);
        this.getContentPane().add(this.m_textConfirmPasswd);
        CapsLockAdapter capsLockAdapter = new CapsLockAdapter();
        this.m_textConfirmPasswd.addKeyListener(capsLockAdapter);
        if (this.m_textNewPasswd != null) {
            this.m_textNewPasswd.addKeyListener(capsLockAdapter);
        }
        if (this.m_textOldPasswd != null) {
            this.m_textOldPasswd.addKeyListener(capsLockAdapter);
        }
        this.addComponentListener(new SetFocusComponentAdapter());
    }

    public String readPassword(Object object) {
        DialogContentPane.CheckError checkError = this.updateDialog();
        if (checkError != null) {
            return null;
        }
        this.setButtonValue(-1);
        this.showDialog();
        if (object != null) {
            this.printStatusMessage(object.toString());
        }
        if (this.getButtonValue() != 0 || this.getPassword() == null) {
            return null;
        }
        return new String(this.getPassword());
    }

    public char[] getPassword() {
        if (this.getButtonValue() != 0) {
            return null;
        }
        if (this.m_passwd == null) {
            return new char[]{'\u0000'};
        }
        return this.m_passwd;
    }

    public boolean isAutomaticFocusSettingEnabled() {
        return false;
    }

    public String getNewPasswordLabel() {
        return JAPMessages.getString(MSG_ENTER_NEW_LBL);
    }

    public String getOldPasswordLabel() {
        return JAPMessages.getString(MSG_ENTER_OLD_LBL);
    }

    public char[] getOldPassword() {
        if (!this.hasValidValue()) {
            return null;
        }
        if (this.m_oldPasswd == null) {
            return new char[]{'\u0000'};
        }
        return this.m_oldPasswd;
    }

    public char[] getComparedPassword() {
        return null;
    }

    public DialogContentPane.CheckError checkYesOK() {
        DialogContentPane.CheckError checkError = null;
        if (this.m_type == 1 || this.m_type == 3) {
            if (this.m_minLength > 0 && (this.m_textNewPasswd.getPassword() == null || this.m_textNewPasswd.getPassword().length < this.m_minLength)) {
                checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_TOO_SHORT, new Integer(this.m_minLength))){

                    public void doErrorAction() {
                        PasswordContentPane.this.m_lblNew1.setForeground(Color.red);
                    }

                    public void undoErrorAction() {
                        PasswordContentPane.this.m_lblNew1.setForeground(new JLabel().getForeground());
                    }
                };
            }
            if (!Util.arraysEqual(this.m_textConfirmPasswd.getPassword(), this.m_textNewPasswd.getPassword())) {
                checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_PASSWORDS_DONT_MATCH)){

                    public void doErrorAction() {
                        PasswordContentPane.this.m_lblNew1.setForeground(Color.red);
                        PasswordContentPane.this.m_lblNew2.setForeground(Color.red);
                    }

                    public void undoErrorAction() {
                        PasswordContentPane.this.m_lblNew1.setForeground(new JLabel().getForeground());
                        PasswordContentPane.this.m_lblNew2.setForeground(new JLabel().getForeground());
                    }
                };
            } else {
                this.m_passwd = this.m_textNewPasswd.getPassword();
            }
        } else if (this.m_type == 2) {
            this.m_passwd = this.m_textConfirmPasswd.getPassword();
        }
        if (this.m_type == 3) {
            if (this.getComparedPassword() != null && !Util.arraysEqual(this.getComparedPassword(), this.m_textOldPasswd.getPassword())) {
                checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_WRONG_PASSWORD)){

                    public void doErrorAction() {
                        PasswordContentPane.this.m_lblOld.setForeground(Color.red);
                    }

                    public void undoErrorAction() {
                        PasswordContentPane.this.m_lblOld.setForeground(new JLabel().getForeground());
                    }
                };
            } else {
                this.m_oldPasswd = this.m_textOldPasswd.getPassword();
            }
        }
        return checkError;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class SetFocusComponentAdapter
    extends ComponentAdapter {
        private SetFocusComponentAdapter() {
        }

        public void componentShown(ComponentEvent componentEvent) {
            if (PasswordContentPane.this.m_type == 3) {
                PasswordContentPane.this.m_textOldPasswd.requestFocus();
            } else if (PasswordContentPane.this.m_type == 1) {
                PasswordContentPane.this.m_textNewPasswd.requestFocus();
            } else {
                PasswordContentPane.this.m_textConfirmPasswd.requestDefaultFocus();
            }
        }
    }

    private class CapsLockAdapter
    extends KeyAdapter {
        private int m_messageID = 0;

        private CapsLockAdapter() {
        }

        public void keyPressed(KeyEvent keyEvent) {
            boolean bl = false;
            try {
                bl = (Boolean)(class$java$awt$Toolkit == null ? (class$java$awt$Toolkit = PasswordContentPane.class$("java.awt.Toolkit")) : class$java$awt$Toolkit).getMethod("getLockingKeyState", Integer.TYPE).invoke(PasswordContentPane.this.getContentPane().getToolkit(), new Integer(20));
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (bl) {
                this.m_messageID = PasswordContentPane.this.printErrorStatusMessage(JAPMessages.getString(MSG_CAPS_LOCK_PRESSED));
            } else {
                PasswordContentPane.this.clearStatusMessage(this.m_messageID);
            }
        }
    }
}

