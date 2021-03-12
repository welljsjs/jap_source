/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.infoservice.MixCascade;
import anon.pay.IPaymentListener;
import anon.pay.PayAccount;
import anon.pay.xml.XMLErrorMessage;
import anon.util.JAPMessages;
import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;
import anon.util.captcha.MyImage;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class CaptchaContentPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable,
IPaymentListener {
    private static final String MSG_TITLE = (class$gui$dialog$CaptchaContentPane == null ? (class$gui$dialog$CaptchaContentPane = CaptchaContentPane.class$("gui.dialog.CaptchaContentPane")) : class$gui$dialog$CaptchaContentPane).getName() + "_title";
    private static final String MSG_SOLVE = (class$gui$dialog$CaptchaContentPane == null ? (class$gui$dialog$CaptchaContentPane = CaptchaContentPane.class$("gui.dialog.CaptchaContentPane")) : class$gui$dialog$CaptchaContentPane).getName() + "_solve";
    private static final String MSG_WRONGCHARNUM = (class$gui$dialog$CaptchaContentPane == null ? (class$gui$dialog$CaptchaContentPane = CaptchaContentPane.class$("gui.dialog.CaptchaContentPane")) : class$gui$dialog$CaptchaContentPane).getName() + "_wrongcharnum";
    private static final String MSG_CAPTCHAERROR = (class$gui$dialog$CaptchaContentPane == null ? (class$gui$dialog$CaptchaContentPane = CaptchaContentPane.class$("gui.dialog.CaptchaContentPane")) : class$gui$dialog$CaptchaContentPane).getName() + "_captchaerror";
    private JTextField m_tfSolution;
    private byte[] m_solution;
    private IImageEncodedCaptcha m_captcha;
    private String m_beginsWith;
    private JLabel m_imageLabel;
    private Object m_syncObject;
    private ICaptchaSender m_captchaSource;
    static /* synthetic */ Class class$gui$dialog$CaptchaContentPane;

    public CaptchaContentPane(JAPDialog jAPDialog, DialogContentPane dialogContentPane) {
        super(jAPDialog, JAPMessages.getString(MSG_SOLVE), new DialogContentPane.Layout(JAPMessages.getString(MSG_TITLE), -1), new DialogContentPaneOptions(2, dialogContentPane));
        this.setDefaultButtonOperation(386);
        JComponent jComponent = this.getContentPane();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        jComponent.setLayout(new GridBagLayout());
        gridBagConstraints.anchor = 10;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        this.m_imageLabel = new JLabel();
        this.m_imageLabel.setPreferredSize(new Dimension(300, 100));
        jComponent.add((Component)this.m_imageLabel, gridBagConstraints);
        ++gridBagConstraints.gridy;
        this.m_tfSolution = new JTextField();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.weightx = 1.0;
        jComponent.add((Component)this.m_tfSolution, gridBagConstraints);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DialogContentPane.CheckError checkNo() {
        this.m_captchaSource.getNewCaptcha();
        Object object = this.m_syncObject;
        synchronized (object) {
            this.m_syncObject.notifyAll();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DialogContentPane.CheckError checkYesOK() {
        DialogContentPane.CheckError checkError;
        block8: {
            checkError = null;
            if (this.m_captcha.getCharacterNumber() == this.m_tfSolution.getText().length()) {
                try {
                    this.m_solution = this.m_captcha.solveCaptcha(this.m_tfSolution.getText().trim(), this.m_beginsWith.getBytes());
                    if (this.m_solution != null) {
                        this.m_captchaSource.setCaptchaSolution(this.m_solution);
                        Object object = this.m_syncObject;
                        synchronized (object) {
                            this.m_syncObject.notifyAll();
                            break block8;
                        }
                    }
                    checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_CAPTCHAERROR));
                }
                catch (Exception exception) {
                    checkError = new DialogContentPane.CheckError(null, exception);
                }
            } else {
                checkError = new DialogContentPane.CheckError(JAPMessages.getString(MSG_WRONGCHARNUM, new Integer(this.m_captcha.getCharacterNumber())));
            }
        }
        return checkError;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DialogContentPane.CheckError checkCancel() {
        Object object = this.m_syncObject;
        synchronized (object) {
            this.m_syncObject.notifyAll();
        }
        return null;
    }

    private void setCaptcha(IImageEncodedCaptcha iImageEncodedCaptcha, String string) {
        this.m_beginsWith = string;
        this.m_captcha = iImageEncodedCaptcha;
        MyImage myImage = iImageEncodedCaptcha.getImage();
        Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(myImage.getWidth(), myImage.getHeight(), myImage.getPixels(), 0, myImage.getWidth()));
        this.m_imageLabel.setIcon(new ImageIcon(image));
        MyDocument myDocument = new MyDocument();
        myDocument.setCaptcha(this.m_captcha);
        this.m_tfSolution.setDocument(myDocument);
    }

    public byte[] getSolution() {
        return this.m_solution;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gotCaptcha(ICaptchaSender iCaptchaSender, IImageEncodedCaptcha iImageEncodedCaptcha) {
        this.getPreviousContentPane().getButtonCancel().setEnabled(true);
        this.setCaptcha(iImageEncodedCaptcha, "<Don");
        this.m_captchaSource = iCaptchaSender;
        SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                CaptchaContentPane.this.updateDialog();
                CaptchaContentPane.this.m_tfSolution.requestFocus();
            }
        });
        Object object = this.m_syncObject = new Object();
        synchronized (object) {
            try {
                this.m_syncObject.wait();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    public boolean isSkippedAsNextContentPane() {
        return true;
    }

    public boolean isSkippedAsPreviousContentPane() {
        return true;
    }

    public void accountCertRequested(MixCascade mixCascade) {
    }

    public void accountError(XMLErrorMessage xMLErrorMessage, boolean bl) {
    }

    public void accountActivated(PayAccount payAccount) {
    }

    public void accountRemoved(PayAccount payAccount) {
    }

    public void accountAdded(PayAccount payAccount) {
    }

    public void creditChanged(PayAccount payAccount) {
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class MyDocument
    extends PlainDocument {
        private IImageEncodedCaptcha m_captcha;

        private MyDocument() {
        }

        public void setCaptcha(IImageEncodedCaptcha iImageEncodedCaptcha) {
            this.m_captcha = iImageEncodedCaptcha;
        }

        public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
            if (this.getLength() + string.length() <= this.m_captcha.getCharacterNumber()) {
                boolean bl = false;
                for (int i = 0; i < string.length() && !bl; ++i) {
                    if (this.m_captcha.getCharacterSet().indexOf(string.toUpperCase().substring(i, i + 1)) >= 0) continue;
                    bl = true;
                }
                if (!bl) {
                    super.insertString(n, string.toUpperCase(), attributeSet);
                }
            }
        }
    }
}

