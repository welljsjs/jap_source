/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.PaymentInstanceDBEntry;
import anon.util.JAPMessages;
import gui.JapCouponField;
import jap.gui.LinkRegistrator;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PaymentInstancePanel
extends JPanel {
    private static final String MSG_BTN_DELETE = (class$jap$pay$PaymentInstancePanel == null ? (class$jap$pay$PaymentInstancePanel = PaymentInstancePanel.class$("jap.pay.PaymentInstancePanel")) : class$jap$pay$PaymentInstancePanel).getName() + ".btnDelete";
    private JapCouponField m_coupon1;
    private JapCouponField m_coupon2;
    private JapCouponField m_coupon3;
    private JapCouponField m_coupon4;
    private PaymentInstanceDBEntry m_paymentInstance;
    private JLabel m_lblHeadline;
    static /* synthetic */ Class class$jap$pay$PaymentInstancePanel;

    public PaymentInstancePanel(PaymentInstanceDBEntry paymentInstanceDBEntry, LinkRegistrator linkRegistrator) {
        this(paymentInstanceDBEntry, linkRegistrator, false);
    }

    public PaymentInstancePanel(PaymentInstanceDBEntry paymentInstanceDBEntry, LinkRegistrator linkRegistrator, boolean bl) {
        Object object;
        Serializable serializable;
        this.m_paymentInstance = paymentInstanceDBEntry;
        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.m_lblHeadline = new JLabel(paymentInstanceDBEntry.getName());
        if (!bl) {
            serializable = new JPanel();
            this.m_coupon1 = new JapCouponField(true);
            ((Container)serializable).add(this.m_coupon1);
            ((Container)serializable).add(new JLabel("-"));
            this.m_coupon2 = new JapCouponField(false);
            this.m_coupon1.setNextCouponField(this.m_coupon2);
            ((Container)serializable).add(this.m_coupon2);
            ((Container)serializable).add(new JLabel("-"));
            this.m_coupon3 = new JapCouponField(false);
            this.m_coupon2.setNextCouponField(this.m_coupon3);
            ((Container)serializable).add(this.m_coupon3);
            ((Container)serializable).add(new JLabel("-"));
            this.m_coupon4 = new JapCouponField(false);
            this.m_coupon3.setNextCouponField(this.m_coupon4);
            ((Container)serializable).add(this.m_coupon4);
            ++gridBagConstraints.gridy;
            this.add((Component)serializable, gridBagConstraints);
            object = new JButton(JAPMessages.getString(MSG_BTN_DELETE));
            ((AbstractButton)object).addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent actionEvent) {
                    PaymentInstancePanel.this.clearCode();
                }
            });
            ++gridBagConstraints.gridx;
            this.add((Component)object, gridBagConstraints);
        }
        serializable = null;
        object = PayAccountsFile.MSG_DO_PREMIUM_PAYMENT;
        if (PayAccountsFile.getInstance().isNewUser()) {
            serializable = paymentInstanceDBEntry.getFreeCodeURL();
        }
        if (serializable != null) {
            linkRegistrator.addBrowserInstallationInfo(this, gridBagConstraints, JAPMessages.getString(PayAccountsFile.MSG_GET_FREE_CODE), ((URL)serializable).toString(), false, 1);
            object = PayAccountsFile.MSG_DO_PREMIUM_PAYMENT_ALTERNATIVE;
        }
        if ((serializable = paymentInstanceDBEntry.getWebshopURL()) == null) {
            linkRegistrator.addBrowserInstallationInfo(this, gridBagConstraints, JAPMessages.getString((String)object), "CONF_PAYMENT", false, 2, paymentInstanceDBEntry.getId());
        } else {
            linkRegistrator.addBrowserInstallationInfo(this, gridBagConstraints, JAPMessages.getString((String)object), ((URL)serializable).toString(), false, 1);
        }
    }

    public PaymentInstanceDBEntry getPaymentInstance() {
        return this.m_paymentInstance;
    }

    public String getCode() {
        if (this.m_coupon1 == null) {
            return null;
        }
        String string = this.m_coupon1.getText() + this.m_coupon2.getText() + this.m_coupon3.getText() + this.m_coupon4.getText();
        if (string.length() == 0) {
            return null;
        }
        return string;
    }

    public boolean isComplete() {
        String string = this.getCode();
        return string == null || PayAccount.checkCouponCode(string) != null;
    }

    public void setHeadlineVisible(boolean bl) {
        this.m_lblHeadline.setVisible(bl);
    }

    public void clearCode() {
        if (this.m_coupon1 == null) {
            return;
        }
        this.m_coupon1.setText("");
        this.m_coupon2.setText("");
        this.m_coupon3.setText("");
        this.m_coupon4.setText("");
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

