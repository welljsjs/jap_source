/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.crypto.CertPath;
import anon.crypto.CertificateInfoStructure;
import anon.crypto.JAPCertificate;
import anon.crypto.MyECPublicKey;
import anon.crypto.MyRSAPublicKey;
import anon.crypto.MyX509Extensions;
import anon.crypto.Validity;
import anon.crypto.X509DistinguishedName;
import anon.crypto.X509UnknownExtension;
import anon.util.CountryMapper;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.JAPHtmlMultiLineLabel;
import gui.TitledGridBagPanel;
import gui.dialog.JAPDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import logging.LogHolder;
import logging.LogType;

public class CertDetailsDialog
extends JAPDialog
implements MouseListener {
    private static final String CLASS_NAME = (class$gui$CertDetailsDialog == null ? (class$gui$CertDetailsDialog = CertDetailsDialog.class$("gui.CertDetailsDialog")) : class$gui$CertDetailsDialog).getName();
    public static final String MSG_CERTVALID = CLASS_NAME + "_certValid";
    public static final String MSG_CERTNOTVALID = CLASS_NAME + "_certNotValid";
    public static final String MSG_CERT_VERIFIED = CLASS_NAME + "_certVerified";
    public static final String MSG_CERT_NOT_VERIFIED = CLASS_NAME + "_certNotVerified";
    private static final String MSG_TITLE = CLASS_NAME;
    private static final String MSG_X509Attribute_ST = CLASS_NAME + "_attributeST";
    private static final String MSG_X509Attribute_L = CLASS_NAME + "_attributeL";
    private static final String MSG_X509Attribute_C = CLASS_NAME + "_attributeC";
    private static final String MSG_X509Attribute_CN = CLASS_NAME + "_attributeCN";
    private static final String MSG_X509Attribute_O = CLASS_NAME + "_attributeO";
    private static final String MSG_X509Attribute_OU = CLASS_NAME + "_attributeOU";
    private static final String MSG_X509Attribute_EMAIL = CLASS_NAME + "_attributeEMAIL";
    private static final String MSG_SHOW_CERT = CLASS_NAME + "_showCert";
    private static final String MSG_CERT_HIERARCHY = CLASS_NAME + "_certHierarchy";
    private static final String MSG_SYMBOLS = CLASS_NAME + "_symbols";
    private static final String MSG_DETAILS = CLASS_NAME + "_detailsTab";
    private static final String MSG_X509Attribute_EMAILADDRESS = CLASS_NAME + "_attributeEMAIL";
    private static final String MSG_X509Attribute_SURNAME = CLASS_NAME + "_attributeSURNAME";
    private static final String MSG_X509Attribute_GIVENNAME = CLASS_NAME + "_attributeGIVENNAME";
    private static final String MSG_ALERT_CERTDATE_EXPIRED = CLASS_NAME + "_alertCertValidityExpired";
    private static final String MSG_ALERT_CERTDATE_NOTYET = CLASS_NAME + "_alertCertNotYetValid";
    private static final String MSG_ALERT_SELF_SIGNED = CLASS_NAME + "_alertSelfSigned";
    private static final String MSG_ALERT_NOT_TRUSTED = CLASS_NAME + "_alertSignatureNotTrusted";
    private static final String UNKNOWN_EXTENSION = CLASS_NAME + "_alertUnknownExtension";
    private static final String TITLE_DISTINGUISHEDNAME = CLASS_NAME + "_titleDistinguishedName";
    private static final String TITLE_ISSUER = CLASS_NAME + "_titleIssuer";
    private static final String TITLE_VALIDITY = CLASS_NAME + "_titleValidity";
    private static final String TITLE_VALIDITY_GENERAL = CLASS_NAME + "_titleValidityGeneral";
    private static final String TITLE_VALIDITY_TO = CLASS_NAME + "_titleValidityTo";
    private static final String TITLE_VALIDITY_FROM = CLASS_NAME + "_titleValidityFrom";
    private static final String TITLE_EXTENSIONS = CLASS_NAME + "_titleExtensions";
    private static final String TITLE_IDENTIFICATION = CLASS_NAME + "_titleIdentification";
    private static final String TITLE_IDENTIFICATION_SHA1 = CLASS_NAME + "_titleIdentificationSHA1";
    private static final String TITLE_IDENTIFICATION_MD5 = CLASS_NAME + "_titleIdentificationMD5";
    private static final String TITLE_IDENTIFICATION_SKEIN = CLASS_NAME + "_titleIdentificationSKEIN";
    private static final String TITLE_IDENTIFICATION_SERIAL = CLASS_NAME + "_titleIdentificationSerial";
    private static final String TITLE_KEYS = CLASS_NAME + "_titleKeys";
    private static final String TITLE_KEYS_ALGORITHM = CLASS_NAME + "_titleKeysAlgorithm";
    private static final String TITLE_KEYS_KEYLENGTH = CLASS_NAME + "_titleKeysKeylength";
    private static final String TITLE_KEYS_SIGNALGORITHM = CLASS_NAME + "_titleKeysSignatureAlgorithm";
    private static final String MSG_CERT_INFO_BORDER = CLASS_NAME + "_certInfoBorder";
    private static final String CERT_VALID_INACTIVE = "certinactive.gif";
    private static final String CERT_INVALID_INACTIVE = "certinvalidinactive.gif";
    private final JLabel LABEL = new JLabel();
    private final Color TITLE_COLOR = Color.blue;
    private final Color ALERT_COLOR = Color.red;
    private final Font TITLE_FONT = new Font(this.LABEL.getFont().getName(), 1, (int)((double)this.LABEL.getFont().getSize() * 1.2));
    private final Font KEY_FONT = new Font(this.LABEL.getFont().getName(), 1, this.LABEL.getFont().getSize());
    private final Font VALUE_FONT = new Font(this.LABEL.getFont().getName(), 0, this.LABEL.getFont().getSize());
    private final Font ALERT_FONT = new Font(this.LABEL.getFont().getName(), 1, this.LABEL.getFont().getSize());
    public static final String IMG_CERTENABLEDICON = "cenabled.gif";
    public static final String IMG_CERTDISABLEDICON = "cdisabled.gif";
    public static final String IMG_WARNING = "warning.gif";
    private static final String IMG_PATH = "certs/";
    private static final String IMG_CERT_ORANGE_OK = "certs/cert_orange_ok.png";
    private static final String IMG_CERT_ORANGE_NOK = "certs/cert_orange_nok.png";
    private static final String IMG_CERT_ORANGE_INVALID = "certs/cert_orange_invalid.png";
    private static final String IMG_CERT_ORANGE_OK_DARK = "certs/cert_orange_ok_dark.png";
    private static final String IMG_CERT_ORANGE_NOK_DARK = "certs/cert_orange_nok_dark.png";
    private static final String IMG_CERT_ORANGE_INVALID_DARK = "certs/cert_orange_invalid_dark.png";
    private static final String IMG_CERT_PURPLE_OK = "certs/cert_purple_ok.png";
    private static final String IMG_CERT_PURPLE_NOK = "certs/cert_purple_nok.png";
    private static final String IMG_CERT_PURPLE_INVALID = "certs/cert_purple_invalid.png";
    private static final String IMG_CERT_PURPLE_OK_DARK = "certs/cert_purple_ok_dark.png";
    private static final String IMG_CERT_PURPLE_NOK_DARK = "certs/cert_purple_nok_dark.png";
    private static final String IMG_CERT_PURPLE_INVALID_DARK = "certs/cert_purple_invalid_dark.png";
    private static final String IMG_CERT_BLUE_OK = "certs/cert_blue_ok.png";
    private static final String IMG_CERT_BLUE_NOK = "certs/cert_blue_nok.png";
    private static final String IMG_CERT_BLUE_INVALID = "certs/cert_blue_invalid.png";
    private static final String IMG_CERT_BLUE_OK_DARK = "certs/cert_blue_ok_dark.png";
    private static final String IMG_CERT_BLUE_NOK_DARK = "certs/cert_blue_nok_dark.png";
    private static final String IMG_CERT_BLUE_INVALID_DARK = "certs/cert_orange_invalid_dark.png";
    private JLabel lbl_summaryIcon;
    private Locale m_Locale;
    private String str;
    private CertShortInfoPanel m_shortInfoPanel;
    private JList m_certList;
    private JTabbedPane m_tabbedPane;
    private DefaultListModel m_certListModel;
    private JAPCertificate m_detailedCert;
    static /* synthetic */ Class class$gui$CertDetailsDialog;

    public CertDetailsDialog(Component component, JAPCertificate jAPCertificate, boolean bl, Locale locale, CertPath certPath) {
        super(component, JAPMessages.getString(MSG_TITLE));
        this.m_Locale = locale;
        JTabbedPane jTabbedPane = new JTabbedPane();
        TitledGridBagPanel titledGridBagPanel = this.drawDetailsPanel(jAPCertificate, bl);
        JPanel jPanel = this.drawCertPathPanel(certPath);
        jTabbedPane.add(JAPMessages.getString(MSG_DETAILS), titledGridBagPanel);
        jTabbedPane.add(JAPMessages.getString(MSG_CERT_HIERARCHY), jPanel);
        JScrollPane jScrollPane = new JScrollPane(jTabbedPane, 20, 30);
        this.getContentPane().add(jScrollPane);
        this.m_tabbedPane = jTabbedPane;
        this.m_detailedCert = jAPCertificate;
        this.setSize();
        this.getContentPane().setVisible(true);
    }

    public CertDetailsDialog(Component component, JAPCertificate jAPCertificate, boolean bl, Locale locale) {
        super(component, JAPMessages.getString(MSG_TITLE));
        this.m_Locale = locale;
        TitledGridBagPanel titledGridBagPanel = this.drawDetailsPanel(jAPCertificate, bl);
        JScrollPane jScrollPane = new JScrollPane(titledGridBagPanel, 20, 30);
        this.getContentPane().add(jScrollPane);
        this.setSize();
        this.setVisible(true);
    }

    private void setSize() {
        this.pack();
        if (this.getSize().height > 480) {
            this.setSize(this.getSize().width, 480);
        }
        if (this.getSize().width > 640) {
            this.setSize(640, this.getSize().height);
        }
    }

    private Vector idsToNames(Vector vector) {
        Vector<String> vector2 = new Vector<String>(vector.size());
        String string = " ";
        if (vector != null && vector.size() > 0) {
            for (int i = 0; i < vector.size(); ++i) {
                String string2 = X509DistinguishedName.getAttributeNameFromAttributeIdentifier((String)vector.elementAt(i));
                string = string2.equals("ST") ? JAPMessages.getString(MSG_X509Attribute_ST) : (string2.equals("L") ? JAPMessages.getString(MSG_X509Attribute_L) : (string2.equals("C") ? JAPMessages.getString(MSG_X509Attribute_C) : (string2.equals("CN") ? JAPMessages.getString(MSG_X509Attribute_CN) : (string2.equals("O") ? JAPMessages.getString(MSG_X509Attribute_O) : (string2.equals("OU") ? JAPMessages.getString(MSG_X509Attribute_OU) : (string2.equals("E") ? JAPMessages.getString(MSG_X509Attribute_EMAIL) : (string2.equals("EmailAddress") ? JAPMessages.getString(MSG_X509Attribute_EMAILADDRESS) : (string2.equals("SURNAME") ? JAPMessages.getString(MSG_X509Attribute_SURNAME) : (string2.equals("GIVENNAME") ? JAPMessages.getString(MSG_X509Attribute_GIVENNAME) : string2)))))))));
                if (!string.equals(string2)) {
                    string = string + " (" + string2 + ")";
                }
                vector2.addElement(string);
            }
        }
        return vector2;
    }

    private TitledGridBagPanel drawDetailsPanel(JAPCertificate jAPCertificate, boolean bl) {
        Serializable serializable;
        JLabel jLabel;
        JLabel jLabel2;
        Serializable serializable2;
        Object object;
        Insets insets = new Insets(2, 5, 2, 5);
        TitledGridBagPanel titledGridBagPanel = new TitledGridBagPanel(null, insets);
        titledGridBagPanel.addMouseListener(this);
        this.lbl_summaryIcon = new JLabel();
        if (jAPCertificate.getPublicKey() instanceof MyRSAPublicKey) {
            if (bl) {
                if (jAPCertificate.getValidity().isValid(new Date())) {
                    this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_OK, true, false));
                } else {
                    this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_INVALID, true, false));
                }
            } else {
                this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_NOK, true, false));
            }
        } else if (jAPCertificate.getPublicKey() instanceof MyECPublicKey) {
            if (bl) {
                if (jAPCertificate.getValidity().isValid(new Date())) {
                    this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_OK, true, false));
                } else {
                    this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_INVALID, true, false));
                }
            } else {
                this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_NOK, true, false));
            }
        } else if (bl) {
            if (jAPCertificate.getValidity().isValid(new Date())) {
                this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_OK, true, false));
            } else {
                this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_INVALID, true, false));
            }
        } else {
            this.lbl_summaryIcon.setIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_NOK, true, false));
        }
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 13;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new Insets(1, 10, 1, 10);
        titledGridBagPanel.add((Component)this.lbl_summaryIcon, gridBagConstraints);
        gridBagConstraints.anchor = 17;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = insets;
        JLabel jLabel3 = new JLabel(jAPCertificate.getSubject().getCommonName(), 2);
        jLabel3.setForeground(this.TITLE_COLOR);
        jLabel3.setFont(this.TITLE_FONT);
        gridBagConstraints.gridwidth = 2;
        titledGridBagPanel.add((Component)jLabel3, gridBagConstraints);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        titledGridBagPanel.add((Component)new JLabel(JAPMessages.getString(TITLE_ISSUER), 4), gridBagConstraints);
        gridBagConstraints.gridx = 2;
        this.str = jAPCertificate.getIssuer().getOrganisation();
        if (this.str == null || this.str.equals("")) {
            this.str = jAPCertificate.getIssuer().getCommonName();
        }
        titledGridBagPanel.add((Component)new JLabel(this.str), gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        titledGridBagPanel.add((Component)new JLabel(JAPMessages.getString(TITLE_VALIDITY_TO), 4), gridBagConstraints);
        gridBagConstraints.gridx = 2;
        titledGridBagPanel.add((Component)new JLabel(jAPCertificate.getValidity().getValidTo().toString()), gridBagConstraints);
        titledGridBagPanel.addDummyRows(5);
        Date date = new Date();
        if (!jAPCertificate.getValidity().isValid(date)) {
            if (jAPCertificate.getValidity().getValidFrom().getTime() < date.getTime()) {
                object = JAPMessages.getString(MSG_ALERT_CERTDATE_EXPIRED);
                serializable2 = new JLabel((String)object, 2);
                ((JComponent)serializable2).setFont(this.ALERT_FONT);
                ((JComponent)serializable2).setForeground(this.ALERT_COLOR);
                titledGridBagPanel.addRow(null, null, (Component)serializable2, null);
            } else if (jAPCertificate.getValidity().getValidTo().getTime() > date.getTime()) {
                object = JAPMessages.getString(MSG_ALERT_CERTDATE_NOTYET);
                serializable2 = new JLabel((String)object, 2);
                ((JComponent)serializable2).setFont(this.ALERT_FONT);
                ((JComponent)serializable2).setForeground(this.ALERT_COLOR);
                titledGridBagPanel.addRow(null, null, (Component)serializable2, null);
            }
        }
        if (!bl) {
            object = JAPMessages.getString(MSG_ALERT_NOT_TRUSTED);
            if (jAPCertificate.verify(jAPCertificate)) {
                object = JAPMessages.getString(MSG_ALERT_SELF_SIGNED);
            }
            serializable2 = new JLabel((String)object, 2);
            ((JComponent)serializable2).setFont(this.ALERT_FONT);
            ((JComponent)serializable2).setForeground(this.ALERT_COLOR);
            titledGridBagPanel.addRow(null, null, (Component)serializable2, null);
        }
        object = jAPCertificate.getSubject();
        serializable2 = ((X509DistinguishedName)object).getAttributeIdentifiers();
        Vector vector = ((X509DistinguishedName)object).getAttributeValues();
        this.replaceCountryCodeByCountryName(vector, (Vector)serializable2);
        serializable2 = this.idsToNames((Vector)serializable2);
        JLabel jLabel4 = new JLabel(JAPMessages.getString(TITLE_DISTINGUISHEDNAME), 4);
        jLabel4.setFont(this.TITLE_FONT);
        jLabel4.setForeground(this.TITLE_COLOR);
        titledGridBagPanel.addRow((Component)jLabel4, null, new JSeparator(0));
        for (int i = 0; i < ((Vector)serializable2).size(); ++i) {
            jLabel2 = new JLabel(((Vector)serializable2).elementAt(i).toString(), 4);
            jLabel2.setFont(this.KEY_FONT);
            jLabel = new JLabel(vector.elementAt(i).toString(), 2);
            jLabel.setFont(this.VALUE_FONT);
            titledGridBagPanel.addRow((Component)jLabel2, null, jLabel);
        }
        Vector vector2 = jAPCertificate.getIssuer().getAttributeIdentifiers();
        Vector vector3 = jAPCertificate.getIssuer().getAttributeValues();
        this.replaceCountryCodeByCountryName(vector3, vector2);
        vector2 = this.idsToNames(vector2);
        JLabel jLabel5 = new JLabel(JAPMessages.getString(TITLE_ISSUER), 4);
        jLabel5.setFont(this.TITLE_FONT);
        jLabel5.setForeground(this.TITLE_COLOR);
        titledGridBagPanel.addRow((Component)jLabel5, null, new JSeparator(0));
        for (int i = 0; i < vector2.size(); ++i) {
            jLabel2 = new JLabel(vector2.elementAt(i).toString(), 4);
            jLabel2.setFont(this.KEY_FONT);
            jLabel = new JLabel(vector3.elementAt(i).toString(), 2);
            jLabel.setFont(this.VALUE_FONT);
            titledGridBagPanel.addRow((Component)jLabel2, null, jLabel);
        }
        MyX509Extensions myX509Extensions = jAPCertificate.getExtensions();
        JLabel jLabel6 = new JLabel(JAPMessages.getString(TITLE_EXTENSIONS), 4);
        jLabel6.setFont(this.TITLE_FONT);
        jLabel6.setForeground(this.TITLE_COLOR);
        if (myX509Extensions.getSize() > 0) {
            titledGridBagPanel.addRow((Component)jLabel6, null, new JSeparator(0));
        }
        String string = null;
        for (int i = 0; i < myX509Extensions.getExtensions().size(); ++i) {
            int n;
            if (myX509Extensions.getExtension(i) instanceof X509UnknownExtension) {
                string = myX509Extensions.getExtension(i).isCritical() ? "*" : "";
                jLabel2 = new JLabel(JAPMessages.getString(UNKNOWN_EXTENSION) + string, 4);
                jLabel2.setFont(this.KEY_FONT);
                serializable = new StringBuffer();
                for (n = 0; n < myX509Extensions.getExtension(i).getValues().size(); ++n) {
                    ((StringBuffer)serializable).append(myX509Extensions.getExtension(i).getIdentifier());
                }
                jLabel = new JLabel(((StringBuffer)serializable).toString(), 2);
                jLabel2.setFont(this.KEY_FONT);
                jLabel.setFont(this.VALUE_FONT);
                titledGridBagPanel.addRow((Component)jLabel2, null, jLabel);
                continue;
            }
            jLabel2 = new JLabel(myX509Extensions.getExtension(i).getName(), 4);
            jLabel2.setFont(this.KEY_FONT);
            serializable = myX509Extensions.getExtension(i).getValues();
            if (((Vector)serializable).size() == 0) {
                titledGridBagPanel.addRow((Component)jLabel2, null, null);
                continue;
            }
            jLabel = new JLabel(((Vector)serializable).elementAt(0).toString());
            jLabel.setFont(this.VALUE_FONT);
            titledGridBagPanel.addRow((Component)jLabel2, null, jLabel);
            for (n = 1; n < ((Vector)serializable).size(); ++n) {
                jLabel = new JLabel(((Vector)serializable).elementAt(n).toString());
                jLabel.setFont(this.VALUE_FONT);
                titledGridBagPanel.addRow(null, null, jLabel);
            }
        }
        Validity validity = jAPCertificate.getValidity();
        serializable = new Vector();
        ((Vector)serializable).addElement(new String(JAPMessages.getString(TITLE_VALIDITY_GENERAL)));
        ((Vector)serializable).addElement(new String(JAPMessages.getString(TITLE_VALIDITY_FROM)));
        ((Vector)serializable).addElement(new String(JAPMessages.getString(TITLE_VALIDITY_TO)));
        Vector<String> vector4 = new Vector<String>();
        if (validity.isValid(new Date())) {
            vector4.addElement(JAPMessages.getString(MSG_CERTVALID));
        } else {
            vector4.addElement(JAPMessages.getString(MSG_CERTNOTVALID));
        }
        vector4.addElement(validity.getValidFrom().toString());
        vector4.addElement(validity.getValidTo().toString());
        JLabel jLabel7 = new JLabel(JAPMessages.getString(TITLE_VALIDITY), 4);
        jLabel7.setFont(this.TITLE_FONT);
        jLabel7.setForeground(this.TITLE_COLOR);
        titledGridBagPanel.addRow((Component)jLabel7, null, new JSeparator(0));
        for (int i = 0; i < ((Vector)serializable).size(); ++i) {
            jLabel2 = new JLabel(((Vector)serializable).elementAt(i).toString(), 4);
            jLabel = new JLabel(vector4.elementAt(i).toString(), 2);
            jLabel2.setFont(this.KEY_FONT);
            jLabel.setFont(this.VALUE_FONT);
            titledGridBagPanel.addRow((Component)jLabel2, null, jLabel);
        }
        Vector<String> vector5 = new Vector<String>();
        vector5.addElement(JAPMessages.getString(TITLE_IDENTIFICATION_SHA1));
        vector5.addElement(JAPMessages.getString(TITLE_IDENTIFICATION_MD5));
        vector5.addElement(JAPMessages.getString(TITLE_IDENTIFICATION_SKEIN));
        vector5.addElement(JAPMessages.getString(TITLE_IDENTIFICATION_SERIAL));
        Vector<Object> vector6 = new Vector<Object>();
        vector6.addElement(jAPCertificate.getSHA1Fingerprint());
        vector6.addElement(jAPCertificate.getMD5Fingerprint());
        vector6.addElement(jAPCertificate.getSKEINFingerprint());
        vector6.addElement(jAPCertificate.getSerialNumber());
        JLabel jLabel8 = new JLabel(JAPMessages.getString(TITLE_IDENTIFICATION), 4);
        jLabel8.setFont(this.TITLE_FONT);
        jLabel8.setForeground(this.TITLE_COLOR);
        titledGridBagPanel.addRow((Component)jLabel8, null, new JSeparator(0));
        for (int i = 0; i < vector5.size(); ++i) {
            jLabel2 = new JLabel(vector5.elementAt(i).toString(), 4);
            jLabel = new JLabel(vector6.elementAt(i).toString());
            jLabel2.setFont(this.KEY_FONT);
            jLabel.setFont(this.VALUE_FONT);
            titledGridBagPanel.addRow((Component)jLabel2, null, jLabel);
        }
        Vector<String> vector7 = new Vector<String>();
        vector7.addElement(JAPMessages.getString(TITLE_KEYS_ALGORITHM));
        Vector<String> vector8 = new Vector<String>();
        vector8.addElement(new String(jAPCertificate.getPublicKey().getAlgorithm()));
        int n = jAPCertificate.getPublicKey().getKeyLength();
        vector7.addElement(JAPMessages.getString(TITLE_KEYS_KEYLENGTH));
        vector8.addElement(new Integer(n).toString() + " Bit");
        vector7.addElement(JAPMessages.getString(TITLE_KEYS_SIGNALGORITHM));
        vector8.addElement(jAPCertificate.getSignatureAlgorithmName());
        JLabel jLabel9 = new JLabel(JAPMessages.getString(TITLE_KEYS), 4);
        jLabel9.setFont(this.TITLE_FONT);
        jLabel9.setForeground(this.TITLE_COLOR);
        titledGridBagPanel.addRow((Component)jLabel9, null, new JSeparator(0));
        for (int i = 0; i < vector7.size(); ++i) {
            jLabel2 = new JLabel(vector7.elementAt(i).toString(), 4);
            jLabel = new JLabel(vector8.elementAt(i).toString());
            jLabel2.setFont(this.KEY_FONT);
            jLabel.setFont(this.VALUE_FONT);
            titledGridBagPanel.addRow((Component)jLabel2, null, jLabel);
        }
        return titledGridBagPanel;
    }

    private JPanel drawCertPathPanel(CertPath certPath) {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_CERT_HIERARCHY), 4);
        jLabel.setFont(this.TITLE_FONT);
        jLabel.setForeground(this.TITLE_COLOR);
        jPanel.add((Component)jLabel, gridBagConstraints);
        this.m_certListModel = new DefaultListModel();
        this.m_certList = new JList(this.m_certListModel);
        this.m_certList.setFont(this.VALUE_FONT);
        this.m_certList.setSelectionMode(0);
        this.m_certList.setCellRenderer(new CertPathListCellRenderer());
        this.m_certList.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (CertDetailsDialog.this.m_certListModel.getSize() != 0 && CertDetailsDialog.this.m_certList.getSelectedValue() != null) {
                    CertDetailsDialog.this.m_shortInfoPanel.update(((CertificateInfoStructure)CertDetailsDialog.this.m_certList.getSelectedValue()).getCertificate());
                }
            }
        });
        this.m_certList.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    CertDetailsDialog.this.showCert();
                }
            }
        });
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setHorizontalScrollBarPolicy(30);
        jScrollPane.setVerticalScrollBarPolicy(20);
        jScrollPane.getViewport().add(this.m_certList);
        ++gridBagConstraints.gridy;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 2.0;
        gridBagConstraints.insets = new Insets(5, 20, 10, 20);
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 11;
        jPanel.add((Component)jScrollPane, gridBagConstraints);
        JButton jButton = new JButton(JAPMessages.getString(MSG_SHOW_CERT) + "...");
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                CertDetailsDialog.this.showCert();
            }
        });
        ++gridBagConstraints.gridx;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 5, 10, 20);
        gridBagConstraints.fill = 0;
        gridBagConstraints.anchor = 18;
        jPanel.add((Component)jButton, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.anchor = 16;
        gridBagConstraints.fill = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel(JAPMessages.getString(MSG_SYMBOLS));
        jPanel.add((Component)jAPHtmlMultiLineLabel, gridBagConstraints);
        gridBagConstraints.insets = new Insets(5, 15, 5, 5);
        ++gridBagConstraints.gridy;
        JLabel jLabel2 = new JLabel(JAPMessages.getString(MSG_CERTVALID), GUIUtils.loadImageIcon(IMG_CERTENABLEDICON, false, false), 2);
        jPanel.add((Component)jLabel2, gridBagConstraints);
        ++gridBagConstraints.gridy;
        JLabel jLabel3 = new JLabel(JAPMessages.getString(MSG_CERTNOTVALID), GUIUtils.loadImageIcon(IMG_WARNING, false, false), 2);
        jPanel.add((Component)jLabel3, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.insets = new Insets(5, 15, 20, 5);
        JLabel jLabel4 = new JLabel(JAPMessages.getString(MSG_CERT_NOT_VERIFIED), GUIUtils.loadImageIcon(IMG_CERTDISABLEDICON, false, false), 2);
        jLabel4.setForeground(Color.red);
        jPanel.add((Component)jLabel4, gridBagConstraints);
        --gridBagConstraints.gridx;
        ++gridBagConstraints.gridy;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(10, 20, 10, 10);
        gridBagConstraints.fill = 2;
        gridBagConstraints.anchor = 11;
        jPanel.add((Component)new JSeparator(), gridBagConstraints);
        this.m_shortInfoPanel = new CertShortInfoPanel();
        if (certPath != null && this.m_certListModel.getSize() > 0) {
            this.m_certList.setSelectedValue(this.m_certListModel.lastElement(), true);
        }
        ++gridBagConstraints.gridy;
        gridBagConstraints.weighty = 1.0;
        jPanel.add((Component)this.m_shortInfoPanel, gridBagConstraints);
        return jPanel;
    }

    private void showCert() {
        boolean bl = true;
        if (this.m_shortInfoPanel.getShownCertificate() != null) {
            if (this.m_shortInfoPanel.getShownCertificate().equals(this.m_detailedCert)) {
                this.m_tabbedPane.setSelectedIndex(0);
            } else {
                if (this.m_certList.getSelectedIndex() == 0) {
                    bl = ((CertificateInfoStructure)this.m_certListModel.firstElement()).isEnabled();
                }
                CertDetailsDialog certDetailsDialog = new CertDetailsDialog(this.getContentPane(), this.m_shortInfoPanel.getShownCertificate(), bl, this.m_Locale);
                certDetailsDialog.setVisible(true);
            }
        }
    }

    private void replaceCountryCodeByCountryName(Vector vector, Vector vector2) {
        for (int i = 0; i < vector.size(); ++i) {
            if (!vector2.elementAt(i).equals(X509DistinguishedName.IDENTIFIER_C)) continue;
            try {
                vector.setElementAt(new CountryMapper(vector.elementAt(i).toString(), this.m_Locale).toString(), i);
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                LogHolder.log(7, LogType.GUI, "Invalid / Unknown country code");
                vector.setElementAt(vector.elementAt(i), i);
            }
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent != null && mouseEvent.getClickCount() >= 2) {
            this.dispose();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private final class CertPathListCellRenderer
    implements ListCellRenderer {
        private int m_itemcount = 0;

        private CertPathListCellRenderer() {
        }

        public Component getListCellRendererComponent(JList jList, Object object, int n, boolean bl, boolean bl2) {
            JPanel jPanel = new JPanel(new GridBagLayout());
            JLabel jLabel = new JLabel();
            JLabel jLabel2 = new JLabel();
            JLabel jLabel3 = new JLabel();
            JLabel jLabel4 = new JLabel();
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            int n2 = n * 2;
            if (n2 > 0) {
                char[] arrc = new char[n2];
                for (int i = 0; i < arrc.length; ++i) {
                    arrc[i] = 32;
                }
                jLabel.setText(new String(arrc));
            }
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = 17;
            jPanel.add((Component)jLabel, gridBagConstraints);
            ++gridBagConstraints.gridx;
            jPanel.add((Component)jLabel2, gridBagConstraints);
            ++gridBagConstraints.gridx;
            jPanel.add((Component)jLabel3, gridBagConstraints);
            ++gridBagConstraints.gridx;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = 2;
            jLabel4 = new JLabel(" ");
            jPanel.add((Component)jLabel4, gridBagConstraints);
            ++this.m_itemcount;
            CertificateInfoStructure certificateInfoStructure = (CertificateInfoStructure)object;
            String string = certificateInfoStructure.getCertificate().getSubject().getCommonName();
            if (string == null) {
                string = certificateInfoStructure.getCertificate().getSubject().toString();
            }
            jLabel3.setText(string);
            jLabel3.setEnabled(jList.isEnabled());
            jLabel2.setEnabled(jList.isEnabled());
            jLabel.setEnabled(jList.isEnabled());
            jLabel4.setEnabled(jList.isEnabled());
            if (bl) {
                jLabel3.setBackground(jList.getSelectionBackground());
                jLabel3.setForeground(jList.getSelectionForeground());
                jPanel.setBackground(jList.getSelectionBackground());
                jPanel.setForeground(jList.getSelectionForeground());
                jLabel2.setBackground(jList.getSelectionBackground());
                jLabel2.setForeground(jList.getSelectionForeground());
                jLabel.setBackground(jList.getSelectionBackground());
                jLabel.setForeground(jList.getSelectionBackground());
                jLabel4.setBackground(jList.getSelectionBackground());
                jLabel4.setForeground(jList.getSelectionForeground());
            } else {
                jLabel3.setBackground(jList.getBackground());
                jLabel3.setForeground(jList.getForeground());
                jPanel.setBackground(jList.getBackground());
                jPanel.setForeground(jList.getForeground());
                jLabel2.setBackground(jList.getBackground());
                jLabel2.setForeground(jList.getForeground());
                jLabel.setBackground(jList.getBackground());
                jLabel.setForeground(jList.getBackground());
                jLabel4.setBackground(jList.getBackground());
                jLabel4.setForeground(jList.getForeground());
            }
            jLabel3.setOpaque(bl);
            jPanel.setOpaque(bl);
            jLabel2.setOpaque(bl);
            jLabel.setOpaque(bl);
            jLabel4.setOpaque(bl);
            if (certificateInfoStructure.isEnabled()) {
                if (certificateInfoStructure.getCertificate().getValidity().isValid(new Date())) {
                    jLabel2.setIcon(GUIUtils.loadImageIcon(CertDetailsDialog.IMG_CERTENABLEDICON, false, false));
                } else {
                    jLabel2.setIcon(GUIUtils.loadImageIcon(CertDetailsDialog.IMG_WARNING, false, false));
                }
            } else {
                jLabel3.setForeground(Color.red);
                jLabel2.setIcon(GUIUtils.loadImageIcon(CertDetailsDialog.IMG_CERTDISABLEDICON, false, false));
            }
            if (certificateInfoStructure.equals(jList.getModel().getElementAt(jList.getModel().getSize() - 1))) {
                jLabel3.setFont(new Font(jLabel3.getFont().getName(), 1, jLabel3.getFont().getSize()));
            } else {
                jLabel3.setFont(jList.getFont());
            }
            return jPanel;
        }
    }

    public static class CertShortInfoPanel
    extends JPanel {
        private JLabel m_labelDate;
        private JLabel m_labelCN;
        private JLabel m_labelE;
        private JLabel m_labelCSTL;
        private JLabel m_labelO;
        private JLabel m_labelOU;
        private JLabel m_labelDateData;
        private JLabel m_labelCNData;
        private JLabel m_labelEData;
        private JLabel m_labelCSTLData;
        private JLabel m_labelOData;
        private JLabel m_labelOUData;
        private JLabel m_lblCertTitle;
        private JAPCertificate m_selectedCert;

        public CertShortInfoPanel() {
            GridBagLayout gridBagLayout = new GridBagLayout();
            this.setLayout(gridBagLayout);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = 17;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(0, 10, 0, 0);
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            this.m_lblCertTitle = new JLabel(JAPMessages.getString(MSG_CERT_INFO_BORDER));
            this.add((Component)this.m_lblCertTitle, gridBagConstraints);
            this.m_labelDate = new JLabel(JAPMessages.getString(TITLE_VALIDITY) + ":");
            this.m_labelCN = new JLabel(JAPMessages.getString(MSG_X509Attribute_CN) + ":");
            this.m_labelE = new JLabel(JAPMessages.getString(MSG_X509Attribute_EMAIL) + ":");
            this.m_labelCSTL = new JLabel(JAPMessages.getString(MSG_X509Attribute_L) + ":");
            this.m_labelO = new JLabel(JAPMessages.getString(MSG_X509Attribute_O) + ":");
            this.m_labelOU = new JLabel(JAPMessages.getString(MSG_X509Attribute_OU) + ":");
            this.m_labelDateData = new JLabel();
            this.m_labelCNData = new JLabel();
            this.m_labelEData = new JLabel();
            this.m_labelCSTLData = new JLabel();
            this.m_labelOData = new JLabel();
            this.m_labelOUData = new JLabel();
            gridBagConstraints.anchor = 17;
            gridBagConstraints.fill = 2;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.insets = new Insets(10, 15, 0, 0);
            gridBagLayout.setConstraints(this.m_labelCN, gridBagConstraints);
            this.add(this.m_labelCN);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(10, 10, 0, 10);
            gridBagLayout.setConstraints(this.m_labelCNData, gridBagConstraints);
            this.add(this.m_labelCNData);
            gridBagConstraints.anchor = 17;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.insets = new Insets(10, 15, 0, 0);
            gridBagLayout.setConstraints(this.m_labelO, gridBagConstraints);
            this.add(this.m_labelO);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(10, 10, 0, 10);
            gridBagLayout.setConstraints(this.m_labelOData, gridBagConstraints);
            this.add(this.m_labelOData);
            gridBagConstraints.anchor = 17;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.insets = new Insets(10, 15, 0, 0);
            gridBagLayout.setConstraints(this.m_labelOU, gridBagConstraints);
            this.add(this.m_labelOU);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(10, 10, 0, 10);
            gridBagLayout.setConstraints(this.m_labelOUData, gridBagConstraints);
            this.add(this.m_labelOUData);
            gridBagConstraints.anchor = 17;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.insets = new Insets(10, 15, 0, 0);
            gridBagLayout.setConstraints(this.m_labelCSTL, gridBagConstraints);
            this.add(this.m_labelCSTL);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(10, 10, 0, 10);
            gridBagLayout.setConstraints(this.m_labelCSTLData, gridBagConstraints);
            this.add(this.m_labelCSTLData);
            gridBagConstraints.anchor = 17;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.insets = new Insets(10, 15, 0, 0);
            gridBagLayout.setConstraints(this.m_labelE, gridBagConstraints);
            this.add(this.m_labelE);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(10, 10, 0, 10);
            gridBagLayout.setConstraints(this.m_labelEData, gridBagConstraints);
            this.add(this.m_labelEData);
            gridBagConstraints.anchor = 17;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 6;
            gridBagConstraints.fill = 2;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.insets = new Insets(10, 15, 10, 0);
            gridBagLayout.setConstraints(this.m_labelDate, gridBagConstraints);
            this.add(this.m_labelDate);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 6;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            gridBagLayout.setConstraints(this.m_labelDateData, gridBagConstraints);
            this.add(this.m_labelDateData);
            gridBagConstraints.anchor = 17;
        }

        public JAPCertificate getShownCertificate() {
            return this.m_selectedCert;
        }

        public void setEnabled(boolean bl) {
            this.m_lblCertTitle.setEnabled(bl);
            this.m_labelDate.setEnabled(bl);
            this.m_labelCN.setEnabled(bl);
            this.m_labelE.setEnabled(bl);
            this.m_labelCSTL.setEnabled(bl);
            this.m_labelO.setEnabled(bl);
            this.m_labelOU.setEnabled(bl);
            this.m_labelDateData.setEnabled(bl);
            this.m_labelCNData.setEnabled(bl);
            this.m_labelEData.setEnabled(bl);
            this.m_labelCSTLData.setEnabled(bl);
            this.m_labelOData.setEnabled(bl);
            this.m_labelOUData.setEnabled(bl);
            super.setEnabled(bl);
        }

        public void update(JAPCertificate jAPCertificate) {
            String string = null;
            this.m_selectedCert = jAPCertificate;
            this.m_labelCNData.setText("");
            this.m_labelEData.setText("");
            this.m_labelCSTLData.setText("");
            this.m_labelOData.setText("");
            this.m_labelOUData.setText("");
            this.m_labelDateData.setText("");
            if (jAPCertificate == null) {
                return;
            }
            StringBuffer stringBuffer = new StringBuffer();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            stringBuffer.append(simpleDateFormat.format(jAPCertificate.getValidity().getValidFrom()));
            stringBuffer.append(" - ");
            stringBuffer.append(simpleDateFormat.format(jAPCertificate.getValidity().getValidTo()));
            this.m_labelDateData.setText(stringBuffer.toString());
            X509DistinguishedName x509DistinguishedName = jAPCertificate.getSubject();
            if (x509DistinguishedName.getCommonName() != null && x509DistinguishedName.getCommonName().trim().length() > 0) {
                this.m_labelCNData.setText(x509DistinguishedName.getCommonName().trim());
            }
            if (x509DistinguishedName.getEmailAddress() != null && x509DistinguishedName.getEmailAddress().trim().length() > 0) {
                this.m_labelEData.setText(x509DistinguishedName.getEmailAddress().trim());
            } else if (x509DistinguishedName.getE_EmailAddress() != null && x509DistinguishedName.getE_EmailAddress().trim().length() > 0) {
                this.m_labelEData.setText(x509DistinguishedName.getE_EmailAddress());
            }
            if (x509DistinguishedName.getLocalityName() != null && x509DistinguishedName.getLocalityName().trim().length() > 0) {
                string = x509DistinguishedName.getLocalityName().trim();
            }
            if (x509DistinguishedName.getStateOrProvince() != null && x509DistinguishedName.getStateOrProvince().trim().length() > 0) {
                string = string != null ? string + ", " : "";
                string = string + x509DistinguishedName.getStateOrProvince().trim();
            }
            if (x509DistinguishedName.getCountryCode() != null) {
                String string2;
                try {
                    string2 = new CountryMapper(x509DistinguishedName.getCountryCode(), JAPMessages.getLocale()).toString();
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    string2 = x509DistinguishedName.getCountryCode();
                }
                if (string2.trim().length() > 0) {
                    string = string != null ? string + ", " : "";
                    string = string + string2.trim();
                }
            }
            this.m_labelCSTLData.setText(string);
            if (x509DistinguishedName.getOrganisation() != null && x509DistinguishedName.getOrganisation().trim().length() > 0) {
                this.m_labelOData.setText(x509DistinguishedName.getOrganisation().trim());
            }
            if (x509DistinguishedName.getOrganisationalUnit() != null && x509DistinguishedName.getOrganisationalUnit().trim().length() > 0) {
                this.m_labelOUData.setText(x509DistinguishedName.getOrganisationalUnit().trim());
            }
        }
    }
}

