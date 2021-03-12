/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.util.JAPMessages;
import anon.util.captcha.IImageEncodedCaptcha;
import anon.util.captcha.MyImage;
import gui.dialog.JAPDialog;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.MemoryImageSource;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import logging.LogHolder;
import logging.LogType;

public class CaptchaDialog
extends JAPDialog
implements ActionListener {
    private static final String MSG_TITLE = (class$gui$CaptchaDialog == null ? (class$gui$CaptchaDialog = CaptchaDialog.class$("gui.CaptchaDialog")) : class$gui$CaptchaDialog).getName() + "_title";
    private static final String MSG_SOLVE = (class$gui$CaptchaDialog == null ? (class$gui$CaptchaDialog = CaptchaDialog.class$("gui.CaptchaDialog")) : class$gui$CaptchaDialog).getName() + "_solve";
    private static final String MSG_OK = (class$gui$CaptchaDialog == null ? (class$gui$CaptchaDialog = CaptchaDialog.class$("gui.CaptchaDialog")) : class$gui$CaptchaDialog).getName() + "_ok";
    private static final String MSG_CANCEL = (class$gui$CaptchaDialog == null ? (class$gui$CaptchaDialog = CaptchaDialog.class$("gui.CaptchaDialog")) : class$gui$CaptchaDialog).getName() + "_cancel";
    private static final String MSG_WRONGCHARNUM = (class$gui$CaptchaDialog == null ? (class$gui$CaptchaDialog = CaptchaDialog.class$("gui.CaptchaDialog")) : class$gui$CaptchaDialog).getName() + "_wrongcharnum";
    private JTextField m_tfSolution;
    private JButton m_btnOk;
    private JButton m_btnCancel;
    private byte[] m_solution;
    private IImageEncodedCaptcha m_captcha;
    private String m_beginsWith;
    static /* synthetic */ Class class$gui$CaptchaDialog;

    public CaptchaDialog(IImageEncodedCaptcha iImageEncodedCaptcha, String string, Window window) {
        super(window, JAPMessages.getString(MSG_TITLE), true);
        this.m_captcha = iImageEncodedCaptcha;
        this.m_beginsWith = string;
        Container container = this.getContentPane();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        container.setLayout(new GridBagLayout());
        gridBagConstraints.anchor = 18;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        MyImage myImage = iImageEncodedCaptcha.getImage();
        Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(myImage.getWidth(), myImage.getHeight(), myImage.getPixels(), 0, myImage.getWidth()));
        JLabel jLabel = new JLabel(new ImageIcon(image));
        container.add((Component)jLabel, gridBagConstraints);
        ++gridBagConstraints.gridy;
        JLabel jLabel2 = new JLabel("<html>" + JAPMessages.getString(MSG_SOLVE) + "</html>");
        container.add((Component)jLabel2, gridBagConstraints);
        ++gridBagConstraints.gridy;
        final IImageEncodedCaptcha iImageEncodedCaptcha2 = iImageEncodedCaptcha;
        this.m_tfSolution = new JTextField(20){

            protected Document createDefaultModel() {
                return new PlainDocument(){

                    public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
                        if (this.getLength() + string.length() <= iImageEncodedCaptcha2.getCharacterNumber()) {
                            boolean bl = false;
                            for (int i = 0; i < string.length() && !bl; ++i) {
                                if (iImageEncodedCaptcha2.getCharacterSet().indexOf(string.toUpperCase().substring(i, i + 1)) >= 0) continue;
                                bl = true;
                            }
                            if (!bl) {
                                super.insertString(n, string.toUpperCase(), attributeSet);
                            }
                        }
                    }
                };
            }
        };
        container.add((Component)this.m_tfSolution, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 1;
        this.m_btnCancel = new JButton(JAPMessages.getString(MSG_CANCEL));
        this.m_btnCancel.addActionListener(this);
        container.add((Component)this.m_btnCancel, gridBagConstraints);
        ++gridBagConstraints.gridx;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.weightx = 1.0;
        this.m_btnOk = new JButton(JAPMessages.getString(MSG_OK));
        this.m_btnOk.addActionListener(this);
        container.add((Component)this.m_btnOk, gridBagConstraints);
        this.pack();
        this.setLocationRelativeTo(this.getOwner(), 0);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        Object object = actionEvent.getSource();
        if (object == this.m_btnCancel) {
            this.dispose();
        } else if (object == this.m_btnOk) {
            if (this.m_captcha.getCharacterNumber() == this.m_tfSolution.getText().length()) {
                try {
                    this.m_solution = this.m_captcha.solveCaptcha(this.m_tfSolution.getText().trim(), this.m_beginsWith.getBytes());
                    this.dispose();
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, "Error solving captcha!");
                }
            } else {
                JAPDialog.showErrorDialog((JAPDialog)this, JAPMessages.getString(MSG_WRONGCHARNUM) + " " + this.m_captcha.getCharacterNumber() + ".");
            }
        }
    }

    public byte[] getSolution() {
        return this.m_solution;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

