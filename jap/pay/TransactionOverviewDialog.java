/*
 * Decompiled with CFR 0.150.
 */
package jap.pay;

import anon.pay.BIConnection;
import anon.pay.PayAccount;
import anon.pay.PaymentInstanceDBEntry;
import anon.pay.xml.XMLErrorMessage;
import anon.pay.xml.XMLPassivePayment;
import anon.pay.xml.XMLPaymentOption;
import anon.pay.xml.XMLPaymentOptions;
import anon.pay.xml.XMLTransactionOverview;
import anon.util.IReturnRunnable;
import anon.util.IXMLEncodable;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPHtmlMultiLineLabel;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import jap.JAPController;
import jap.JAPUtil;
import jap.pay.AccountSettingsPanel;
import jap.pay.ActivePaymentDetails;
import jap.pay.PassivePaymentDetails;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import logging.LogHolder;
import logging.LogType;

public class TransactionOverviewDialog
extends JAPDialog
implements ActionListener {
    private static final String MSG_OK_BUTTON = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_ok_button";
    private static final String MSG_DETAILSBUTTON = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_detailsbutton";
    private static final String MSG_RELOADBUTTON = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_reloadbutton";
    private static final String MSG_CANCELBUTTON = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_cancelbutton";
    private static final String MSG_FETCHING = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_fetching";
    private static final String MSG_TAN = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_tan";
    private static final String MSG_AMOUNT = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_amount";
    private static final String MSG_STATUS = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_status";
    private static final String MSG_TRANSACTION_DATE = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_transaction_date";
    public static final String MSG_DETAILS_FAILED = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_details_failed";
    public static final String MSG_FETCHING_TAN = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_fetchingTAN";
    private static final String MSG_ACCOUNTNUMBER = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_accountnumber";
    private static final String MSG_VOLUMEPLAN = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_volumeplan";
    private static final String MSG_PAYMENTMETHOD = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_paymentmethod";
    private static final String MSG_USEDSTATUS = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_usedstatus";
    private static final String MSG_OPENSTATUS = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_openstatus";
    private static final String MSG_EXPIREDSTATUS = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_expiredstatus";
    private static final String MSG_PAYMENT_COMPLETED = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_paymentcompleted";
    private static final String MSG_PAYMENT_EXPIRED = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_paymentexpired";
    private static final String MSG_NO_OPEN_TRANSFERS = (class$jap$pay$TransactionOverviewDialog == null ? (class$jap$pay$TransactionOverviewDialog = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog")) : class$jap$pay$TransactionOverviewDialog).getName() + "_noopentransfers";
    private JTable m_transactionsTable;
    private JButton m_okButton;
    private JButton m_reloadButton;
    private JButton m_detailsButton;
    private JLabel m_fetchingLabel;
    private Vector m_accounts;
    static /* synthetic */ Class class$jap$pay$TransactionOverviewDialog;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$jap$pay$TransactionOverviewDialog$TablecellAmount;
    static /* synthetic */ Class class$java$lang$Object;

    public TransactionOverviewDialog(AccountSettingsPanel accountSettingsPanel, String string, boolean bl, Vector vector) {
        super(GUIUtils.getParentWindow(accountSettingsPanel.getRootPanel()), string, bl);
        if (vector.size() == 0) {
            JAPDialog.showMessageDialog(this, JAPMessages.getString(MSG_NO_OPEN_TRANSFERS));
        } else {
            try {
                this.m_accounts = vector;
                this.setDefaultCloseOperation(2);
                this.buildDialog();
                this.setModal(true);
                this.setSize(700, 300);
                this.setVisible(true);
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.PAY, "Could not create TransactionOverviewDialog: " + exception.getMessage());
            }
        }
    }

    private void buildDialog() throws Exception {
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        this.m_transactionsTable = new JTable();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = 1;
        jPanel.add((Component)new JScrollPane(this.m_transactionsTable), gridBagConstraints);
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 0;
        ++gridBagConstraints.gridy;
        this.m_fetchingLabel = new JLabel(JAPMessages.getString(MSG_FETCHING), GUIUtils.loadImageIcon("busy.gif", true), 10);
        this.m_fetchingLabel.setHorizontalTextPosition(10);
        jPanel.add((Component)this.m_fetchingLabel, gridBagConstraints);
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = 14;
        jPanel.add((Component)this.buildButtonPanel(), gridBagConstraints);
        this.getContentPane().add(jPanel);
        this.showTransactions();
    }

    private JPanel buildButtonPanel() {
        JPanel jPanel = new JPanel(new FlowLayout(0));
        this.m_detailsButton = new JButton(JAPMessages.getString(MSG_DETAILSBUTTON));
        this.m_detailsButton.addActionListener(this);
        jPanel.add(this.m_detailsButton);
        this.m_reloadButton = new JButton(JAPMessages.getString(MSG_RELOADBUTTON));
        this.m_reloadButton.addActionListener(this);
        jPanel.add(this.m_reloadButton);
        this.m_okButton = new JButton(JAPMessages.getString(MSG_CANCELBUTTON));
        this.m_okButton.addActionListener(this);
        jPanel.add(this.m_okButton);
        return jPanel;
    }

    private void showTransactions() {
        this.m_reloadButton.setEnabled(false);
        this.m_fetchingLabel.setVisible(true);
        Runnable runnable = new Runnable(){

            public void run() {
                JAPController.getInstance().updatePaymentInstances(false);
                XMLTransactionOverview xMLTransactionOverview = new XMLTransactionOverview(JAPMessages.getLocale().getLanguage());
                Object object = TransactionOverviewDialog.this.m_accounts.elements();
                while (object.hasMoreElements()) {
                    PayAccount payAccount = (PayAccount)object.nextElement();
                    if (payAccount.getTransaction() == null) continue;
                    xMLTransactionOverview.addTan(payAccount.getTransaction());
                }
                object = new MyTableModel(xMLTransactionOverview);
                TransactionOverviewDialog.this.m_transactionsTable.setEnabled(true);
                TransactionOverviewDialog.this.m_transactionsTable.setModel((TableModel)object);
                TransactionOverviewDialog.this.m_transactionsTable.addMouseListener(new MouseAdapter(){

                    public void mouseClicked(MouseEvent mouseEvent) {
                        if (mouseEvent.getClickCount() == 2) {
                            TransactionOverviewDialog.this.showTransactionDetailsDialog();
                        }
                    }
                });
                TransactionOverviewDialog.this.m_okButton.setText(JAPMessages.getString(MSG_OK_BUTTON));
                TransactionOverviewDialog.this.m_fetchingLabel.setVisible(false);
                TransactionOverviewDialog.this.m_reloadButton.setEnabled(true);
            }
        };
        Thread thread = new Thread(runnable, "TransactionOverviewDialog");
        thread.setDaemon(true);
        thread.start();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.m_okButton) {
            this.dispose();
        } else if (actionEvent.getSource() == this.m_reloadButton) {
            this.showTransactions();
        } else if (actionEvent.getSource() == this.m_detailsButton) {
            this.showTransactionDetailsDialog();
        }
    }

    public void showTransactionDetailsDialog() {
        try {
            int n = this.m_transactionsTable.getSelectedRow();
            final String string = (String)this.m_transactionsTable.getModel().getValueAt(n, 1);
            Object object = this.m_transactionsTable.getModel().getValueAt(n, 3);
            long l = ((TablecellAmount)object).getLongValue();
            String string2 = (String)this.m_transactionsTable.getModel().getValueAt(n, 6);
            String string3 = (String)this.m_transactionsTable.getModel().getValueAt(n, 4);
            String string4 = (String)this.m_transactionsTable.getModel().getValueAt(n, 5);
            boolean bl = false;
            boolean bl2 = false;
            if (string2.equalsIgnoreCase(JAPMessages.getString(MSG_USEDSTATUS))) {
                bl = true;
            } else if (string2.equalsIgnoreCase(JAPMessages.getString(MSG_EXPIREDSTATUS))) {
                bl2 = true;
            }
            final PayAccount payAccount = (PayAccount)this.m_accounts.elementAt(0);
            JAPDialog jAPDialog = new JAPDialog(this, JAPMessages.getString(MSG_FETCHING_TAN));
            IReturnRunnable iReturnRunnable = new IReturnRunnable(){
                Object xmlReply;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void run() {
                    PaymentInstanceDBEntry paymentInstanceDBEntry = payAccount.getBI();
                    BIConnection bIConnection = null;
                    try {
                        try {
                            bIConnection = new BIConnection(paymentInstanceDBEntry);
                            bIConnection.connect();
                            bIConnection.authenticate(payAccount);
                            this.xmlReply = bIConnection.fetchPaymentData(new Long(string).toString(), payAccount);
                        }
                        catch (Exception exception) {
                            this.xmlReply = exception;
                            Object var5_4 = null;
                            if (bIConnection != null) {
                                bIConnection.disconnect();
                            }
                        }
                        Object var5_3 = null;
                        if (bIConnection != null) {
                            bIConnection.disconnect();
                        }
                    }
                    catch (Throwable throwable) {
                        Object var5_5 = null;
                        if (bIConnection != null) {
                            bIConnection.disconnect();
                        }
                        throw throwable;
                    }
                }

                public Object getValue() {
                    return this.xmlReply;
                }
            };
            WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString(MSG_FETCHING_TAN), iReturnRunnable);
            workerContentPane.updateDialog();
            jAPDialog.pack();
            jAPDialog.setVisible(true);
            if (iReturnRunnable.getValue() == null) {
                return;
            }
            if (iReturnRunnable.getValue() instanceof Exception && !(iReturnRunnable.getValue() instanceof XMLErrorMessage)) {
                throw (Exception)iReturnRunnable.getValue();
            }
            if (!(iReturnRunnable.getValue() instanceof IXMLEncodable)) {
                throw new Exception("Illegal return value!");
            }
            IXMLEncodable iXMLEncodable = (IXMLEncodable)iReturnRunnable.getValue();
            if (iXMLEncodable instanceof XMLErrorMessage) {
                XMLErrorMessage xMLErrorMessage = (XMLErrorMessage)iXMLEncodable;
                if (xMLErrorMessage.getXmlErrorCode() == 19) {
                    if (bl) {
                        JAPDialog.showMessageDialog(this, JAPMessages.getString(MSG_PAYMENT_COMPLETED));
                    } else if (bl2) {
                        JAPDialog.showMessageDialog(this, JAPMessages.getString(MSG_PAYMENT_EXPIRED));
                    } else {
                        TransactionOverviewDialog.showActivePaymentDialog(this, string, l, payAccount, string3, string4, true);
                    }
                } else {
                    JAPDialog.showMessageDialog(this, JAPMessages.getString(MSG_DETAILS_FAILED));
                }
            } else {
                TransactionOverviewDialog.showPassivePaymentDialog(this, (XMLPassivePayment)iXMLEncodable, Long.parseLong(string), payAccount.getAccountNumber());
            }
        }
        catch (Exception exception) {
            LogHolder.log(7, LogType.PAY, "could not get transaction details");
        }
    }

    public static void showActivePaymentDialog(JAPDialog jAPDialog, String string, long l, final PayAccount payAccount, String string2, final String string3, final boolean bl) {
        IReturnRunnable iReturnRunnable = new IReturnRunnable(){
            Vector optionsToShow;

            public void run() {
                this.optionsToShow = TransactionOverviewDialog.getLocalizedActivePaymentsData(JAPMessages.getLocale().getLanguage(), payAccount, string3);
            }

            public Object getValue() {
                return this.optionsToShow;
            }
        };
        JAPDialog jAPDialog2 = new JAPDialog(jAPDialog, JAPMessages.getString(AccountSettingsPanel.MSG_FETCHINGOPTIONS));
        Runnable runnable = new Runnable(){

            public void run() {
                try {
                    payAccount.fetchAccountInfo(true);
                }
                catch (Exception exception) {
                    LogHolder.log(1, LogType.PAY, exception);
                }
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog2, JAPMessages.getString(AccountSettingsPanel.MSG_GETACCOUNTSTATEMENT), runnable);
        WorkerContentPane workerContentPane2 = new WorkerContentPane(jAPDialog2, JAPMessages.getString(AccountSettingsPanel.MSG_FETCHINGOPTIONS), workerContentPane, iReturnRunnable){

            public boolean isSkippedAsNextContentPane() {
                return !bl && payAccount.isCharged();
            }
        };
        workerContentPane2.pack();
        jAPDialog2.setResizable(false);
        jAPDialog2.setVisible(true);
        if (iReturnRunnable.getValue() != null && ((Vector)iReturnRunnable.getValue()).size() > 0) {
            ActivePaymentDetails activePaymentDetails = new ActivePaymentDetails(jAPDialog, (Vector)iReturnRunnable.getValue(), string, l, string2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Vector getLocalizedActivePaymentsData(String string, PayAccount payAccount, String string2) {
        Vector vector = new Vector();
        if (payAccount == null || payAccount.getBI() == null) {
            return vector;
        }
        PaymentInstanceDBEntry paymentInstanceDBEntry = payAccount.getBI();
        BIConnection bIConnection = null;
        try {
            try {
                bIConnection = new BIConnection(paymentInstanceDBEntry);
                bIConnection.connect();
                bIConnection.authenticate(payAccount);
                XMLPaymentOptions xMLPaymentOptions = bIConnection.fetchPaymentOptions();
                if (string.equals("")) {
                    string = "en";
                }
                Enumeration enumeration = xMLPaymentOptions.getAllOptions().elements();
                while (enumeration.hasMoreElements()) {
                    XMLPaymentOption xMLPaymentOption = (XMLPaymentOption)enumeration.nextElement();
                    if (xMLPaymentOption.getType().equals("passive") || !xMLPaymentOption.worksWithJapVersion("00.20.001")) continue;
                    Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
                    hashtable.put("name", xMLPaymentOption.getName());
                    hashtable.put("heading", new JAPHtmlMultiLineLabel(xMLPaymentOption.getHeading(string)).getHTMLDocumentText());
                    hashtable.put("detailedInfo", new JAPHtmlMultiLineLabel(xMLPaymentOption.getDetailedInfo(string)).getHTMLDocumentText());
                    hashtable.put("extraInfos", xMLPaymentOption.getLocalizedExtraInfoText(string));
                    vector.addElement(hashtable);
                }
                Object var11_11 = null;
                if (bIConnection != null) {
                    bIConnection.disconnect();
                }
            }
            catch (Exception exception) {
                LogHolder.log(7, LogType.PAY, "could not get payment options");
                Object var11_12 = null;
                if (bIConnection != null) {
                    bIConnection.disconnect();
                }
            }
        }
        catch (Throwable throwable) {
            Object var11_13 = null;
            if (bIConnection != null) {
                bIConnection.disconnect();
            }
            throw throwable;
        }
        return vector;
    }

    public static void showPassivePaymentDialog(JAPDialog jAPDialog, XMLPassivePayment xMLPassivePayment, long l, long l2) {
        PassivePaymentDetails passivePaymentDetails = new PassivePaymentDetails(jAPDialog, xMLPassivePayment, l, l2);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    protected class TablecellAmount {
        long m_theAmount;

        public TablecellAmount(long l) {
            this.m_theAmount = l;
        }

        public String toString() {
            return JAPUtil.formatEuroCentValue(this.m_theAmount, true);
        }

        public long getLongValue() {
            return this.m_theAmount;
        }
    }

    private class MyTableModel
    extends AbstractTableModel {
        private XMLTransactionOverview m_overview;

        public MyTableModel(XMLTransactionOverview xMLTransactionOverview) {
            this.m_overview = xMLTransactionOverview;
        }

        public int getColumnCount() {
            return 7;
        }

        public int getRowCount() {
            return this.m_overview.size();
        }

        public Class getColumnClass(int n) {
            switch (n) {
                case 0: {
                    return class$java$lang$String == null ? (class$java$lang$String = TransactionOverviewDialog.class$("java.lang.String")) : class$java$lang$String;
                }
                case 1: {
                    return class$java$lang$String == null ? (class$java$lang$String = TransactionOverviewDialog.class$("java.lang.String")) : class$java$lang$String;
                }
                case 2: {
                    return class$java$util$Date == null ? (class$java$util$Date = TransactionOverviewDialog.class$("java.util.Date")) : class$java$util$Date;
                }
                case 3: {
                    return class$jap$pay$TransactionOverviewDialog$TablecellAmount == null ? (class$jap$pay$TransactionOverviewDialog$TablecellAmount = TransactionOverviewDialog.class$("jap.pay.TransactionOverviewDialog$TablecellAmount")) : class$jap$pay$TransactionOverviewDialog$TablecellAmount;
                }
                case 5: {
                    return class$java$lang$String == null ? (class$java$lang$String = TransactionOverviewDialog.class$("java.lang.String")) : class$java$lang$String;
                }
                case 6: {
                    return class$java$lang$String == null ? (class$java$lang$String = TransactionOverviewDialog.class$("java.lang.String")) : class$java$lang$String;
                }
                case 7: {
                    return class$java$lang$String == null ? (class$java$lang$String = TransactionOverviewDialog.class$("java.lang.String")) : class$java$lang$String;
                }
            }
            return class$java$lang$Object == null ? (class$java$lang$Object = TransactionOverviewDialog.class$("java.lang.Object")) : class$java$lang$Object;
        }

        public Object getValueAt(int n, int n2) {
            Hashtable hashtable = (Hashtable)this.m_overview.getTans().elementAt(n);
            switch (n2) {
                case 0: {
                    String string = (String)hashtable.get("accountnumber");
                    if (string == null) {
                        return new String("");
                    }
                    return string;
                }
                case 1: {
                    return hashtable.get("tan");
                }
                case 2: {
                    try {
                        String string = (String)hashtable.get("date");
                        long l = Long.parseLong(string);
                        return new Date(l);
                    }
                    catch (Exception exception) {
                        return null;
                    }
                }
                case 3: {
                    try {
                        String string = (String)hashtable.get("amount");
                        long l = Long.parseLong(string);
                        TablecellAmount tablecellAmount = new TablecellAmount(l);
                        return tablecellAmount;
                    }
                    catch (Exception exception) {
                        return new String("");
                    }
                }
                case 4: {
                    try {
                        String string = (String)hashtable.get("volumeplan");
                        return string;
                    }
                    catch (Exception exception) {
                        return new String("");
                    }
                }
                case 5: {
                    try {
                        String string = (String)hashtable.get("paymentmethod");
                        if (string.equalsIgnoreCase("null")) {
                            return new String("");
                        }
                        return "<html>" + string + "</html>";
                    }
                    catch (Exception exception) {
                        return new String("");
                    }
                }
                case 6: {
                    try {
                        return this.transactionStatus(hashtable);
                    }
                    catch (Exception exception) {
                        return new String("");
                    }
                }
            }
            return JAPMessages.getString("unknown");
        }

        private String transactionStatus(Hashtable hashtable) {
            String string = (String)hashtable.get("used");
            boolean bl = new Boolean(string);
            if (bl) {
                return JAPMessages.getString(MSG_USEDSTATUS);
            }
            return JAPMessages.getString(MSG_OPENSTATUS);
        }

        public String getColumnName(int n) {
            switch (n) {
                case 0: {
                    return JAPMessages.getString(MSG_ACCOUNTNUMBER);
                }
                case 1: {
                    return JAPMessages.getString(MSG_TAN);
                }
                case 2: {
                    return JAPMessages.getString(MSG_TRANSACTION_DATE);
                }
                case 3: {
                    return JAPMessages.getString(MSG_AMOUNT);
                }
                case 4: {
                    return JAPMessages.getString(MSG_VOLUMEPLAN);
                }
                case 5: {
                    return JAPMessages.getString(MSG_PAYMENTMETHOD);
                }
                case 6: {
                    return JAPMessages.getString(MSG_STATUS);
                }
            }
            return "---";
        }

        public boolean isCellEditable(int n, int n2) {
            return false;
        }
    }
}

