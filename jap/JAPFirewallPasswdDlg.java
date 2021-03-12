/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.ImmutableProxyInterface;
import anon.util.IPasswordReader;
import anon.util.JAPMessages;
import gui.dialog.DialogContentPane;
import gui.dialog.JAPDialog;
import gui.dialog.PasswordContentPane;
import jap.JAPController;

final class JAPFirewallPasswdDlg
implements IPasswordReader {
    JAPFirewallPasswdDlg() {
    }

    public String readPassword(ImmutableProxyInterface immutableProxyInterface) {
        JAPDialog jAPDialog = new JAPDialog(JAPController.getInstance().getCurrentView(), JAPMessages.getString("passwdDlgTitle"), true);
        jAPDialog.setAlwaysOnTop(true);
        PasswordContentPane passwordContentPane = new PasswordContentPane(jAPDialog, 2, JAPMessages.getString("passwdDlgInput") + "<br><br>" + JAPMessages.getString("passwdDlgHost") + ": " + immutableProxyInterface.getHost() + "<br>" + JAPMessages.getString("passwdDlgPort") + ": " + immutableProxyInterface.getPort() + "<br>" + JAPMessages.getString("passwdDlgUserID") + ": " + immutableProxyInterface.getAuthenticationUserID()){

            public DialogContentPane.CheckError checkCancel() {
                return super.checkCancel();
            }
        };
        passwordContentPane.updateDialog();
        jAPDialog.pack();
        jAPDialog.setResizable(false);
        jAPDialog.setVisible(true);
        if (passwordContentPane.getButtonValue() != 0 || passwordContentPane.getPassword() == null) {
            return null;
        }
        return new String(passwordContentPane.getPassword());
    }
}

