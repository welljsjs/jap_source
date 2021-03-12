/*
 * Decompiled with CFR 0.150.
 */
package jap.pay.wizardnew;

import anon.pay.xml.XMLPaymentOption;
import anon.pay.xml.XMLTransCert;
import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import anon.util.Util;
import gui.GUIUtils;
import gui.LinkMouseListener;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import jap.JAPController;
import jap.JAPUtil;
import jap.pay.wizardnew.MethodSelectionPane;
import jap.pay.wizardnew.VolumePlanSelectionPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logging.LogHolder;
import logging.LogType;

public class PaymentInfoPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable,
ActionListener {
    private static final String MSG_INFOS = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_infos";
    private static final String MSG_BUTTONCOPY = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_buttoncopy";
    private static final String MSG_BUTTONOPEN = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_buttonopen";
    public static final String MSG_PAYPAL_ITEM_NAME = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_paypalitemname";
    private static final String MSG_COULD_OPEN = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_reminderLink";
    private static final String MSG_REMINDER_PAYMENT = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_reminderPayment";
    private static final String MSG_REMINDER_PAYMENT_EXPLAIN = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_reminderPaymentExplain";
    private static final String MSG_EXPLAIN_COULD_OPEN = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_reminderLinkExplain";
    private static final String MSG_NO_FURTHER_PAYMENT = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_noFurtherPayment";
    private static final String MSG_USE_OTHER_METHOD = (class$jap$pay$wizardnew$PaymentInfoPane == null ? (class$jap$pay$wizardnew$PaymentInfoPane = PaymentInfoPane.class$("jap.pay.wizardnew.PaymentInfoPane")) : class$jap$pay$wizardnew$PaymentInfoPane).getName() + "_useOtherMethod";
    private Container m_rootPanel;
    private GridBagConstraints m_c;
    private JButton m_bttnCopy;
    private JButton m_bttnOpen;
    private String m_language;
    private XMLPaymentOption m_selectedOption;
    private XMLTransCert m_transCert;
    private JCheckBox m_linkOpenedInBrowser;
    private JLabel m_imageLabel;
    private LinkMouseListener.ILinkGenerator m_paymentLinkGenerator;
    private boolean m_bURL;
    static /* synthetic */ Class class$jap$pay$wizardnew$PaymentInfoPane;
    static /* synthetic */ Class class$jap$JAPConstants;

    public PaymentInfoPane(JAPDialog jAPDialog, DialogContentPane dialogContentPane) {
        super(jAPDialog, "Dummy", new DialogContentPane.Layout(JAPMessages.getString(MSG_INFOS), -1), new DialogContentPaneOptions(2, dialogContentPane));
        this.setDefaultButtonOperation(266);
        this.m_language = JAPMessages.getLocale().getLanguage();
        this.m_rootPanel = this.getContentPane();
        this.m_rootPanel.setLayout(new GridBagLayout());
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        this.m_linkOpenedInBrowser = new JCheckBox(JAPMessages.getString(MSG_COULD_OPEN));
        this.m_rootPanel.add((Component)this.m_linkOpenedInBrowser, this.m_c);
    }

    public static String createPaysafecardLink(String string, long l, String string2) {
        string = Util.replaceAll(string, "%t", string2);
        String string3 = PaymentInfoPane.amountAsString(l);
        string = Util.replaceAll(string, "%a", string3);
        string = Util.replaceAll(string, "%l", JAPMessages.getLocale().getLanguage());
        string = Util.replaceAll(string, "%c", "EUR");
        return string;
    }

    public static String createPaypalLink(String string, long l, String string2, String string3) {
        String string4 = "EUR";
        String string5 = PaymentInfoPane.amountAsString(l);
        String string6 = JAPMessages.getLocale().getLanguage().toLowerCase();
        String string7 = JAPMessages.getString(MSG_PAYPAL_ITEM_NAME) + "%20-%20" + string2;
        string = Util.replaceAll(string, "%t", string3);
        string = Util.replaceAll(string, "%item%", string7);
        string = Util.replaceAll(string, "%amount%", string5);
        string = Util.replaceAll(string, "%currency%", string4);
        string6 = string6.startsWith("de") || string6.startsWith("fr") || string6.startsWith("es") || string6.startsWith("it") || string6.startsWith("au") || string6.startsWith("cn") || string6.startsWith("gb") || string6.startsWith("jp") || string6.startsWith("nl") ? string6.toUpperCase() : "US";
        string = Util.replaceAll(string, "%lang%", string6);
        return string;
    }

    public static String createEgoldLink(String string, long l, String string2, String string3) {
        String string4 = PaymentInfoPane.amountAsString(l);
        String string5 = JAPMessages.getLocale().getLanguage();
        String string6 = string5.toLowerCase();
        if (!string6.equals("en") && !string6.equals("de")) {
            string6 = "en";
        }
        String string7 = JAPMessages.getString(MSG_PAYPAL_ITEM_NAME) + "%20-%20" + string2;
        string = Util.replaceAll(string, "%t", string3);
        string = Util.replaceAll(string, "%item%", string7);
        string = Util.replaceAll(string, "%amount%", string4);
        string = Util.replaceAll(string, "%currency%", "EUR");
        string = Util.replaceAll(string, "%2fen%2f", "%2f" + string6 + "%2f");
        return string;
    }

    private static String amountAsString(long l) {
        String string;
        String string2;
        String string3 = new Long(l).toString();
        string3.trim();
        if (string3.length() == 1) {
            string2 = "0";
            string = "0" + string3;
        } else if (string3.length() < 3) {
            string2 = "0";
            string = string3;
        } else {
            string2 = string3.substring(0, string3.length() - 2);
            string = string3.substring(string3.length() - 2, string3.length());
        }
        String string4 = string2 + "%2e" + string;
        return string4;
    }

    public void showInfo() {
        DialogContentPane dialogContentPane = this.getPreviousContentPane();
        while (!(dialogContentPane instanceof MethodSelectionPane)) {
            dialogContentPane = dialogContentPane.getPreviousContentPane();
        }
        MethodSelectionPane methodSelectionPane = (MethodSelectionPane)dialogContentPane;
        XMLPaymentOption xMLPaymentOption = methodSelectionPane.getSelectedPaymentOption();
        dialogContentPane = this.getPreviousContentPane();
        do {
            if (!(dialogContentPane instanceof WorkerContentPane) || !(((WorkerContentPane)dialogContentPane).getValue() instanceof XMLTransCert)) continue;
            this.m_transCert = (XMLTransCert)((WorkerContentPane)dialogContentPane).getValue();
            break;
        } while (this.m_transCert == null && (dialogContentPane = dialogContentPane.getPreviousContentPane()) != null);
        if (this.m_transCert == null) {
            throw new NullPointerException("TransCert is null!");
        }
        String string = "";
        this.m_selectedOption = xMLPaymentOption;
        this.m_rootPanel.removeAll();
        this.m_rootPanel = this.getContentPane();
        this.m_rootPanel.setLayout(new GridBagLayout());
        this.m_c = new GridBagConstraints();
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 0.0;
        this.m_c.weightx = 0.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 0;
        final String string2 = xMLPaymentOption.getExtraInfo(this.m_language);
        this.m_bURL = xMLPaymentOption.getExtraInfoType(this.m_language).equalsIgnoreCase("link");
        if (string2 != null) {
            dialogContentPane = this.getPreviousContentPane();
            while (!(dialogContentPane instanceof VolumePlanSelectionPane)) {
                dialogContentPane = dialogContentPane.getPreviousContentPane();
            }
            VolumePlanSelectionPane volumePlanSelectionPane = (VolumePlanSelectionPane)dialogContentPane;
            String string3 = volumePlanSelectionPane.getAmount();
            final String string4 = xMLPaymentOption.getName();
            final String string5 = volumePlanSelectionPane.getSelectedVolumePlan().getDisplayName();
            final int n = Integer.parseInt(string3);
            final String string6 = String.valueOf(this.m_transCert.getTransferNumber());
            this.m_paymentLinkGenerator = new LinkMouseListener.ILinkGenerator(){

                public String createLink() {
                    String string;
                    if (PaymentInfoPane.this.m_bURL) {
                        string = string4.toLowerCase().indexOf("paypal") != -1 ? PaymentInfoPane.createPaypalLink(string2, n, string5, string6) : PaymentInfoPane.createPaysafecardLink(string2, n, string6);
                    } else {
                        string = Util.replaceAll(string2, "%t", string6);
                        String string22 = JAPUtil.formatEuroCentValue(n, true);
                        string = Util.replaceAll(string, "%a", string22);
                        string = Util.replaceAll(string, "%c", "");
                    }
                    return string;
                }
            };
            ++this.m_c.gridy;
            this.m_rootPanel.add((Component)new JLabel(" "), this.m_c);
            String string7 = PaymentInfoPane.getMethodImageFilename(xMLPaymentOption.getName());
            ImageIcon imageIcon = GUIUtils.loadImageIcon(string7, false, false);
            ++this.m_c.gridy;
            if (imageIcon != null) {
                JPanel jPanel = new JPanel();
                jPanel.setLayout(new BoxLayout(jPanel, 0));
                this.m_imageLabel = new JLabel(imageIcon);
                this.m_imageLabel.setAlignmentX(0.5f);
                if (string2.indexOf("://") > 0 || string2.toLowerCase().startsWith("mailto:")) {
                    this.m_imageLabel.addMouseListener(new LinkMouseListener(this.m_paymentLinkGenerator){

                        public void mouseClicked(MouseEvent mouseEvent) {
                            super.mouseClicked(mouseEvent);
                            PaymentInfoPane.this.actionPerformed(null);
                        }
                    });
                }
                jPanel.add(this.m_imageLabel);
                this.m_c.gridwidth = 2;
                this.m_rootPanel.add((Component)jPanel, this.m_c);
                this.m_c.gridwidth = 1;
                ++this.m_c.gridy;
            }
            this.m_bttnOpen = new JButton(JAPMessages.getString(MSG_BUTTONOPEN));
            this.m_bttnOpen.addActionListener(this);
            this.m_rootPanel.add((Component)this.m_bttnOpen, this.m_c);
            this.m_bttnOpen.setVisible(false);
            ++this.m_c.gridx;
            this.m_bttnCopy = new JButton(JAPMessages.getString(MSG_BUTTONCOPY));
            this.m_bttnCopy.addActionListener(this);
            this.m_rootPanel.add((Component)this.m_bttnCopy, this.m_c);
            this.m_bttnCopy.setEnabled(false);
            if (this.m_bURL) {
                this.m_bttnOpen.setVisible(true);
                this.m_bttnOpen.setEnabled(true);
                string = xMLPaymentOption.getDetailedInfo(this.m_language);
            } else {
                this.m_bttnCopy.setEnabled(true);
                string = xMLPaymentOption.getDetailedInfo(this.m_language) + "<br><b>" + this.m_paymentLinkGenerator.createLink() + "</b>";
            }
        }
        this.m_c.gridx = 0;
        ++this.m_c.gridy;
        this.m_rootPanel.add((Component)new JLabel(" "), this.m_c);
        this.setText(string);
        ++this.m_c.gridy;
        this.m_c.anchor = 15;
        this.m_c.gridwidth = 2;
        this.m_rootPanel.add((Component)this.m_linkOpenedInBrowser, this.m_c);
        if (this.m_bURL) {
            this.m_linkOpenedInBrowser.setText(JAPMessages.getString(MSG_COULD_OPEN));
        } else {
            this.m_linkOpenedInBrowser.setText(JAPMessages.getString(MSG_REMINDER_PAYMENT));
        }
    }

    public XMLTransCert getTransCert() {
        return this.m_transCert;
    }

    public static String getMethodImageFilename(String string) {
        String string2;
        Class class_ = class$jap$JAPConstants == null ? (class$jap$JAPConstants = PaymentInfoPane.class$("jap.JAPConstants")) : class$jap$JAPConstants;
        String string3 = "IMAGE_" + string.toUpperCase();
        Field field = null;
        try {
            field = class_.getDeclaredField(string3);
        }
        catch (NoSuchFieldException noSuchFieldException) {
            LogHolder.log(7, LogType.PAY, "could not load image for payment method " + string + ", there is not variable " + string3 + " in JAPConstants");
            return null;
        }
        try {
            string2 = (String)field.get(null);
        }
        catch (Exception exception) {
            LogHolder.log(7, LogType.PAY, "could not load image for payment method" + string + " , reason: " + exception);
            return null;
        }
        return string2;
    }

    private void copyExtraInfoToClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String string = this.m_paymentLinkGenerator.createLink();
        if (this.m_selectedOption.getExtraInfoType(this.m_language).equalsIgnoreCase("text")) {
            string = Util.replaceAll(string, "<br>", "\n");
            string = Util.replaceAll(string, "<p>", "\n\n");
            string = Util.replaceAll(string, "&uuml;", "\u00fc");
            string = Util.replaceAll(string, "&Uuml;", "\u00dc");
            string = Util.replaceAll(string, "&auml;", "\u00e4");
            string = Util.replaceAll(string, "&Auml;", "\u00c4");
            string = Util.replaceAll(string, "&ouml;", "\u00f6");
            string = Util.replaceAll(string, "&Ouml;", "\u00d6");
            string = Util.replaceAll(string, "&szlig;", "\u00df");
            string = Util.replaceAll(string, "&nbsp;", " ");
        } else {
            string = Util.replaceAll(string, "<br>", "");
            string = Util.replaceAll(string, "<p>", "");
            string = Util.replaceAll(string, "&nbsp;", "%20");
            string = Util.replaceAll(string, " ", "%20");
        }
        string = Util.replaceAll(string, "<html>", " ");
        string = Util.replaceAll(string, "</html>", " ");
        string = Util.replaceAll(string, "<font color=blue><u>", "");
        string = Util.replaceAll(string, "</u></font>", "");
        string = string.trim();
        StringSelection stringSelection = new StringSelection(string);
        clipboard.setContents(stringSelection, null);
    }

    public void openURL() {
        String string = this.m_paymentLinkGenerator.createLink();
        AbstractOS abstractOS = AbstractOS.getInstance();
        string = Util.replaceAll(string, "<br>", "");
        string = Util.replaceAll(string, "<p>", "");
        string = Util.replaceAll(string, "<html>", " ");
        string = Util.replaceAll(string, "</html>", " ");
        string = Util.replaceAll(string, "&nbsp;", "%20");
        string = Util.replaceAll(string, " ", "%20");
        string = Util.replaceAll(string, "<font color=blue><u>", "");
        string = Util.replaceAll(string, "</u></font>", "");
        string = string.trim();
        LogHolder.log(7, LogType.PAY, "Opening " + string + " in browser.");
        try {
            URL uRL = new URL(string);
            JAPController.getInstance().allowDirectProxyDomain(uRL);
            abstractOS.openURL(uRL);
        }
        catch (MalformedURLException malformedURLException) {
            LogHolder.log(2, LogType.PAY, "Malformed URL");
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent != null) {
            if (actionEvent.getSource() == this.m_bttnCopy) {
                this.copyExtraInfoToClipboard();
            } else if (actionEvent.getSource() == this.m_bttnOpen) {
                this.openURL();
            }
        }
        this.m_bttnCopy.setEnabled(true);
    }

    public DialogContentPane.CheckError checkUpdate() {
        this.m_linkOpenedInBrowser.setSelected(false);
        this.showInfo();
        return null;
    }

    public DialogContentPane.CheckError checkYesOK() {
        if (!this.m_linkOpenedInBrowser.isSelected()) {
            if (this.m_bURL) {
                return new DialogContentPane.CheckError(JAPMessages.getString(MSG_EXPLAIN_COULD_OPEN));
            }
            return new DialogContentPane.CheckError(JAPMessages.getString(MSG_REMINDER_PAYMENT_EXPLAIN));
        }
        return null;
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

