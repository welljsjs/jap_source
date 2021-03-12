/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.crypto.CertPath;
import anon.crypto.CertificateInfoStructure;
import anon.crypto.JAPCertificate;
import anon.crypto.SignatureVerifier;
import anon.util.JAPMessages;
import gui.CAListCellRenderer;
import gui.CertDetailsDialog;
import gui.dialog.JAPDialog;
import jap.AbstractJAPConfModule;
import jap.JAPConfCertSavePoint;
import jap.JAPUtil;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

final class JAPConfCert
extends AbstractJAPConfModule
implements Observer {
    public static final String MSG_NO_CHECK_WARNING = (class$jap$JAPConfCert == null ? (class$jap$JAPConfCert = JAPConfCert.class$("jap.JAPConfCert")) : class$jap$JAPConfCert).getName() + "_noCheckWarning";
    private static final String MSG_DETAILS = (class$jap$JAPConfCert == null ? (class$jap$JAPConfCert = JAPConfCert.class$("jap.JAPConfCert")) : class$jap$JAPConfCert).getName() + "_details";
    private TitledBorder m_borderCert;
    private CertDetailsDialog.CertShortInfoPanel m_shortInfoPanel;
    private JButton m_bttnCertInsert;
    private JButton m_bttnCertRemove;
    private JButton m_bttnCertStatus;
    private JButton m_bttnCertDetails;
    private DefaultListModel m_listmodelCertList;
    private JList m_listCert;
    private JScrollPane m_scrpaneList;
    private Enumeration m_enumCerts;
    private JCheckBox m_cbCertCheckEnabled;
    private JPanel m_panelCAList;
    private Vector m_deletedCerts = new Vector();
    private boolean m_bDoNotUpdate = false;
    static /* synthetic */ Class class$jap$JAPConfCert;

    public JAPConfCert() {
        super(new JAPConfCertSavePoint());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                SignatureVerifier.getInstance().getVerificationCertificateStore().addObserver(this);
                this.update(SignatureVerifier.getInstance().getVerificationCertificateStore(), null);
                return true;
            }
        }
        return false;
    }

    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        jPanel.removeAll();
        this.m_borderCert = new TitledBorder(JAPMessages.getString("confCertTab"));
        jPanel.setBorder(this.m_borderCert);
        JPanel jPanel2 = this.createCALabel();
        this.m_panelCAList = this.createCertCAPanel();
        this.m_shortInfoPanel = new CertDetailsDialog.CertShortInfoPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel.add((Component)this.m_panelCAList, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = 2;
        jPanel.add((Component)new JSeparator(), gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        jPanel.add((Component)this.m_shortInfoPanel, gridBagConstraints);
    }

    public String getTabTitle() {
        return JAPMessages.getString("confCertTab");
    }

    private JPanel createCALabel() {
        JPanel jPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        this.m_cbCertCheckEnabled = new JCheckBox(JAPMessages.getString("certTrust") + ":");
        this.m_cbCertCheckEnabled.setSelected(true);
        this.m_cbCertCheckEnabled.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (!JAPConfCert.this.m_cbCertCheckEnabled.isSelected()) {
                    JAPDialog.showWarningDialog(JAPConfCert.this.m_cbCertCheckEnabled, JAPMessages.getString(MSG_NO_CHECK_WARNING));
                }
            }
        });
        this.m_cbCertCheckEnabled.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent itemEvent) {
                boolean bl = JAPConfCert.this.m_cbCertCheckEnabled.isSelected();
                JAPConfCert.this.m_shortInfoPanel.setEnabled(bl);
                JAPConfCert.this.m_bttnCertInsert.setEnabled(bl);
                Object e = JAPConfCert.this.m_listCert.getSelectedValue();
                boolean bl2 = false;
                if (e != null) {
                    bl2 = bl && !((CertificateInfoStructure)JAPConfCert.this.m_listCert.getSelectedValue()).isNotRemovable();
                }
                JAPConfCert.this.m_bttnCertRemove.setEnabled(bl2);
                JAPConfCert.this.m_bttnCertStatus.setEnabled(bl);
                JAPConfCert.this.m_bttnCertDetails.setEnabled(bl);
                JAPConfCert.this.m_listCert.setEnabled(bl);
                JAPConfCert.this.m_panelCAList.setEnabled(bl);
            }
        });
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 17;
        gridBagConstraints.fill = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 0);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel.add((Component)this.m_cbCertCheckEnabled, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
        jPanel.add((Component)new JLabel(), gridBagConstraints);
        return jPanel;
    }

    private JPanel createCertCAPanel() {
        JPanel jPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        this.m_listmodelCertList = new DefaultListModel();
        this.m_listCert = new JList(this.m_listmodelCertList);
        this.m_listCert.setSelectionMode(0);
        this.m_listCert.setCellRenderer(new CAListCellRenderer());
        this.m_listCert.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (JAPConfCert.this.m_listmodelCertList.getSize() == 0 || JAPConfCert.this.m_listCert.getSelectedValue() == null) {
                    JAPConfCert.this.m_shortInfoPanel.update((JAPCertificate)null);
                    JAPConfCert.this.m_bttnCertRemove.setEnabled(false);
                    JAPConfCert.this.m_bttnCertStatus.setEnabled(false);
                    JAPConfCert.this.m_bttnCertDetails.setEnabled(false);
                } else {
                    CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)JAPConfCert.this.m_listCert.getSelectedValue();
                    JAPConfCert.this.m_shortInfoPanel.update(certificateInfoStructure.getCertificate());
                    if (certificateInfoStructure.isEnabled()) {
                        JAPConfCert.this.m_bttnCertStatus.setText(JAPMessages.getString("certBttnDisable"));
                    } else {
                        JAPConfCert.this.m_bttnCertStatus.setText(JAPMessages.getString("certBttnEnable"));
                    }
                    JAPConfCert.this.m_bttnCertStatus.setEnabled(true);
                    JAPConfCert.this.m_bttnCertRemove.setEnabled(!certificateInfoStructure.isNotRemovable());
                    JAPConfCert.this.m_bttnCertDetails.setEnabled(true);
                }
            }
        });
        this.m_listCert.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    JAPConfCert.this.m_bttnCertDetails.doClick();
                }
            }
        });
        this.m_scrpaneList = new JScrollPane();
        this.m_scrpaneList.getViewport().add((Component)this.m_listCert, null);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new Insets(0, 10, 10, 10);
        gridBagConstraints.fill = 1;
        gridBagLayout.setConstraints(this.m_scrpaneList, gridBagConstraints);
        jPanel.add(this.m_scrpaneList);
        this.m_bttnCertInsert = new JButton(JAPMessages.getString("certBttnInsert"));
        this.m_bttnCertInsert.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                boolean bl = false;
                JAPCertificate jAPCertificate = null;
                try {
                    jAPCertificate = JAPUtil.openCertificate(new JFrame());
                }
                catch (Exception exception) {
                    bl = true;
                }
                if (jAPCertificate == null && bl) {
                    JAPDialog.showMessageDialog(JAPConfCert.this.getRootPanel(), JAPMessages.getString("certInputErrorTitle"));
                }
                if (jAPCertificate != null) {
                    CertificateInfoStructure certificateInfoStructure = new CertificateInfoStructure(CertPath.getRootInstance(jAPCertificate), null, 1, true, false, true, false);
                    JAPConfCert.this.m_listmodelCertList.addElement(certificateInfoStructure);
                    JAPConfCert.this.m_listCert.setSelectedIndex(JAPConfCert.this.m_listmodelCertList.getSize());
                }
            }
        });
        this.m_bttnCertRemove = new JButton(JAPMessages.getString("certBttnRemove"));
        this.m_bttnCertRemove.setEnabled(false);
        this.m_bttnCertRemove.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                if (JAPConfCert.this.m_listmodelCertList.getSize() > 0) {
                    JAPConfCert.this.m_deletedCerts.addElement(JAPConfCert.this.m_listmodelCertList.getElementAt(JAPConfCert.this.m_listCert.getSelectedIndex()));
                    JAPConfCert.this.m_listmodelCertList.remove(JAPConfCert.this.m_listCert.getSelectedIndex());
                }
                if (JAPConfCert.this.m_listmodelCertList.getSize() == 0) {
                    JAPConfCert.this.m_bttnCertRemove.setEnabled(false);
                    JAPConfCert.this.m_bttnCertStatus.setEnabled(false);
                    JAPConfCert.this.m_bttnCertDetails.setEnabled(false);
                    JAPConfCert.this.m_shortInfoPanel.update((JAPCertificate)null);
                } else {
                    JAPConfCert.this.m_shortInfoPanel.update((JAPCertificate)null);
                    JAPConfCert.this.m_listCert.setSelectedIndex(0);
                    CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)JAPConfCert.this.m_listCert.getSelectedValue();
                    JAPConfCert.this.m_shortInfoPanel.update(certificateInfoStructure.getCertificate());
                }
            }
        });
        this.m_bttnCertStatus = new JButton(JAPMessages.getString("certBttnEnable"));
        this.m_bttnCertStatus.setEnabled(false);
        this.m_bttnCertStatus.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)JAPConfCert.this.m_listCert.getSelectedValue();
                if (certificateInfoStructure.isEnabled()) {
                    certificateInfoStructure.setEnabled(false);
                    JAPConfCert.this.m_bttnCertStatus.setText(JAPMessages.getString("certBttnEnable"));
                } else {
                    certificateInfoStructure.setEnabled(true);
                    JAPConfCert.this.m_bttnCertStatus.setText(JAPMessages.getString("certBttnDisable"));
                }
                JAPConfCert.this.m_listCert.repaint();
            }
        });
        this.m_bttnCertDetails = new JButton(JAPMessages.getString(MSG_DETAILS));
        this.m_bttnCertDetails.setEnabled(false);
        this.m_bttnCertDetails.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)JAPConfCert.this.m_listCert.getSelectedValue();
                if (certificateInfoStructure == null) {
                    return;
                }
                CertDetailsDialog certDetailsDialog = new CertDetailsDialog(JAPConfCert.this.getRootPanel().getParent(), certificateInfoStructure.getCertificate().getX509Certificate(), true, JAPMessages.getLocale());
                certDetailsDialog.pack();
                certDetailsDialog.setVisible(true);
            }
        });
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(0, 10, 0, 0);
        gridBagLayout.setConstraints(this.m_bttnCertInsert, gridBagConstraints);
        jPanel.add(this.m_bttnCertInsert);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagLayout.setConstraints(this.m_bttnCertRemove, gridBagConstraints);
        jPanel.add(this.m_bttnCertRemove);
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagLayout.setConstraints(this.m_bttnCertStatus, gridBagConstraints);
        jPanel.add(this.m_bttnCertStatus);
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagLayout.setConstraints(this.m_bttnCertDetails, gridBagConstraints);
        jPanel.add(this.m_bttnCertDetails);
        return jPanel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void update(Observable observable, Object object) {
        if (this.m_bDoNotUpdate) {
            return;
        }
        if (observable == SignatureVerifier.getInstance().getVerificationCertificateStore() && (object == null || object instanceof Integer && (Integer)object == 1)) {
            Enumeration enumeration = SignatureVerifier.getInstance().getVerificationCertificateStore().getAllCertificates().elements();
            JAPConfCert jAPConfCert = this;
            synchronized (jAPConfCert) {
                int n = this.m_listCert.getSelectedIndex();
                this.m_listmodelCertList.clear();
                this.m_enumCerts = enumeration;
                while (this.m_enumCerts.hasMoreElements()) {
                    CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)this.m_enumCerts.nextElement();
                    if (certificateInfoStructure.getCertificateType() != 1) continue;
                    this.m_listmodelCertList.addElement(certificateInfoStructure);
                }
                if (this.m_listmodelCertList.getSize() > 0 && n >= 0 && n < this.m_listmodelCertList.getSize()) {
                    this.m_listCert.setSelectedIndex(n);
                }
            }
        }
    }

    protected void onUpdateValues() {
        if (this.m_cbCertCheckEnabled.isSelected() != SignatureVerifier.getInstance().isCheckSignatures()) {
            this.m_cbCertCheckEnabled.setSelected(SignatureVerifier.getInstance().isCheckSignatures());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean onOkPressed() {
        if (this.m_bDoNotUpdate) {
            return true;
        }
        JAPConfCert jAPConfCert = this;
        synchronized (jAPConfCert) {
            CertificateInfoStructure certificateInfoStructure;
            this.m_bDoNotUpdate = true;
            SignatureVerifier.getInstance().setCheckSignatures(this.m_cbCertCheckEnabled.isSelected());
            Enumeration enumeration = this.m_deletedCerts.elements();
            while (enumeration.hasMoreElements()) {
                certificateInfoStructure = (CertificateInfoStructure)enumeration.nextElement();
                SignatureVerifier.getInstance().getVerificationCertificateStore().removeCertificate(certificateInfoStructure);
            }
            this.m_deletedCerts.removeAllElements();
            enumeration = this.m_listmodelCertList.elements();
            while (enumeration.hasMoreElements()) {
                certificateInfoStructure = (CertificateInfoStructure)enumeration.nextElement();
                CertificateInfoStructure certificateInfoStructure2 = SignatureVerifier.getInstance().getVerificationCertificateStore().getCertificateInfoStructure(certificateInfoStructure.getCertificate(), 1);
                if (certificateInfoStructure2 != null) {
                    if (certificateInfoStructure2.isEnabled() == certificateInfoStructure.isEnabled()) continue;
                    SignatureVerifier.getInstance().getVerificationCertificateStore().setEnabled(certificateInfoStructure, certificateInfoStructure.isEnabled());
                    continue;
                }
                SignatureVerifier.getInstance().getVerificationCertificateStore().addCertificateWithoutVerification(certificateInfoStructure.getCertificate(), 1, true, false);
                SignatureVerifier.getInstance().getVerificationCertificateStore().setEnabled(certificateInfoStructure, certificateInfoStructure.isEnabled());
            }
            this.m_savePoint.createSavePoint();
            this.m_bDoNotUpdate = false;
        }
        this.update(SignatureVerifier.getInstance().getVerificationCertificateStore(), null);
        return true;
    }

    protected void onCancelPressed() {
        this.m_cbCertCheckEnabled.setSelected(SignatureVerifier.getInstance().isCheckSignatures());
        this.update(SignatureVerifier.getInstance().getVerificationCertificateStore(), null);
        this.m_deletedCerts.removeAllElements();
    }

    protected void onResetToDefaultsPressed() {
        super.onResetToDefaultsPressed();
        this.m_cbCertCheckEnabled.setSelected(true);
    }

    public String getHelpContext() {
        return "cert";
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

