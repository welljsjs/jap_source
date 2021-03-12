/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.crypto.CertPathInfo;
import anon.crypto.JAPCertificate;
import anon.crypto.MultiCertPath;
import anon.crypto.MyECPublicKey;
import anon.crypto.MyRSAPublicKey;
import anon.util.CountryMapper;
import anon.util.JAPMessages;
import anon.util.Util;
import gui.CertDetailsDialog;
import gui.GUIUtils;
import gui.JAPHtmlMultiLineLabel;
import gui.MultiCertTrustGraph;
import gui.dialog.JAPDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class MultiCertOverview
extends JAPDialog
implements MouseListener {
    private static final String TITLE = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_title";
    private static final String SUMMARY = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_summary";
    private static final String EXPLANATION = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_explanation";
    private static final String MSG_NUMBER_OF_CERTS_ONE = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_numberOfCertsOne";
    private static final String MSG_NUMBER_OF_CERTS_ONE_NOT_TRUSTED = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_numberOfCertsOneNotTrusted";
    private static final String MSG_NUMBER_OF_CERTS_ONE_EXPIRED = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_numberOfCertsOneExpired";
    private static final String MSG_NUMBER_OF_CERTS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_numberOfCerts";
    private static final String MSG_NUMBER_OF_TRUSTED_CERTS_ONE = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_numberOfTrustedCertsOne";
    private static final String MSG_NUMBER_OF_TRUSTED_CERTS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_numberOfTrustedCerts";
    private static final String MSG_IDENTITY_ONE = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_identityOne";
    private static final String MSG_IDENTITY = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_identity";
    private static final String MSG_SHOW_DETAILS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_details";
    private static final String MSG_SYMBOLS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_symbols";
    private static final String MSG_TRUSTED = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_trusted";
    private static final String MSG_NOT_TRUSTED = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_notTrusted";
    private static final String MSG_VALID = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_valid";
    private static final String MSG_INVALID = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_invalid";
    private static final String MSG_ROOT_CERTS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_rootCerts";
    private static final String HINT_ROOT_CERTS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_hintRootCerts";
    private static final String MSG_OP_CERTS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_opCerts";
    private static final String HINT_OP = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_hintOp";
    private static final String MSG_MIX_CERTS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_mixCerts";
    private static final String HINT_MIX = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_hintMix";
    private static final String MSG_IS_CERTS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_isCerts";
    private static final String HINT_IS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_hintIS";
    private static final String HINT_ARROW = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_hintArrow";
    private static final String HINT_CERT_DETAILS = (class$gui$MultiCertOverview == null ? (class$gui$MultiCertOverview = MultiCertOverview.class$("gui.MultiCertOverview")) : class$gui$MultiCertOverview).getName() + "_hintCertDetails";
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
    private static final String IMG_ARROW_NORTH = "certs/arrow_north_ok.png";
    private static final String IMG_ARROW_NORTH_NOK = "certs/arrow_north_nok.png";
    private static final String IMG_ARROW_NORTH_EAST = "certs/arrow_north_east_ok.png";
    private static final String IMG_ARROW_NORTH_EAST_NOK = "certs/arrow_north_east_nok.png";
    private static final String IMG_ARROW_NORTH_WEST = "certs/arrow_north_west_ok.png";
    private static final String IMG_ARROW_NORTH_WEST_NOK = "certs/arrow_north_west_nok.png";
    public static final String IMG_NOT_TRUSTED = "certs/not_trusted.png";
    public static final String IMG_TRUSTED = "certs/trusted_black.png";
    public static final String IMG_TRUSTED_DOUBLE = "certs/trusted_blue.png";
    public static final String IMG_TRUSTED_THREE_CERTS = "certs/trusted_green.png";
    public static final String IMG_INVALID = "certs/invalid.png";
    private static final String IMG_BOX_ORANGE = "certs/box_orange.png";
    private static final String IMG_BOX_PURPLE = "certs/box_purple.png";
    private static final String IMG_BOX_BLUE = "certs/box_blue.png";
    private MultiCertPath m_multiCertPath;
    private String m_name;
    private Hashtable m_buttonsAndNodes;
    private CertPathInfo[] m_pathInfos;
    private MultiCertTrustGraph m_graph;
    private JAPHtmlMultiLineLabel m_lblSummary;
    static /* synthetic */ Class class$gui$MultiCertOverview;

    public MultiCertOverview(Component component, MultiCertPath multiCertPath, String string, boolean bl) {
        super(component, JAPMessages.getString(TITLE, string != null ? string : multiCertPath.getSubject().getCommonName()));
        this.m_multiCertPath = multiCertPath;
        this.m_pathInfos = this.m_multiCertPath.getPathInfos();
        this.m_graph = new MultiCertTrustGraph(this.m_pathInfos);
        this.m_name = this.m_multiCertPath.getSubject().getCommonName().startsWith("<Mix id=") && string != null ? string : this.m_multiCertPath.getSubject().getCommonName();
        this.m_buttonsAndNodes = new Hashtable();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 2.0;
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 10;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        jPanel.add((Component)this.drawOverviewPanel(bl), gridBagConstraints);
        JTabbedPane jTabbedPane = new JTabbedPane();
        JPanel jPanel2 = this.drawSummaryPanel(bl);
        jTabbedPane.add(JAPMessages.getString(SUMMARY), jPanel2);
        jTabbedPane.add(JAPMessages.getString(EXPLANATION), this.drawExplanationPanel());
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridy = 2;
        jPanel.add((Component)jTabbedPane, gridBagConstraints);
        this.getContentPane().add(jPanel);
        this.pack();
        this.finishSummaryPanel(jPanel2);
        this.setVisible(true);
    }

    private JPanel drawSummaryPanel(boolean bl) {
        JPanel jPanel = new JPanel(new GridBagLayout());
        int n = this.m_multiCertPath.countPaths();
        int n2 = this.m_multiCertPath.countVerifiedPaths();
        int n3 = this.m_multiCertPath.countVerifiedAndValidPaths();
        String string = "<em>" + this.m_name + "</em>";
        String string2 = n <= 1 ? (n3 == 1 ? JAPMessages.getString(MSG_NUMBER_OF_CERTS_ONE, string) : (n2 == 1 ? JAPMessages.getString(MSG_NUMBER_OF_CERTS_ONE_EXPIRED, string) : JAPMessages.getString(MSG_NUMBER_OF_CERTS_ONE_NOT_TRUSTED, string))) : JAPMessages.getString(MSG_NUMBER_OF_CERTS, new Object[]{string, new Integer(n), new Integer(n3)});
        string2 = string2 + " ";
        if (n > 1) {
            string2 = n2 == 1 ? string2 + JAPMessages.getString(MSG_NUMBER_OF_TRUSTED_CERTS_ONE) : string2 + JAPMessages.getString(MSG_NUMBER_OF_TRUSTED_CERTS, new Integer(n2));
        }
        int n4 = this.m_graph.countTrustedRootNodes();
        String string3 = !bl && this.m_multiCertPath.getIssuer().getOrganisation() != null ? this.m_multiCertPath.getIssuer().getOrganisation() : (bl && this.m_multiCertPath.getSubject().getOrganisation() != null ? this.m_multiCertPath.getSubject().getOrganisation() : "");
        string3 = "<em>" + string3 + "</em>";
        if (n4 > 0) {
            string2 = string2 + " ";
            string2 = n4 == 1 ? string2 + JAPMessages.getString(MSG_IDENTITY_ONE, string3) : string2 + JAPMessages.getString(MSG_IDENTITY, new Object[]{string3, String.valueOf(n4)});
        }
        this.m_lblSummary = n2 == 0 ? new JAPHtmlMultiLineLabel("<font color='red'>" + string2 + "</font>") : new JAPHtmlMultiLineLabel(string2);
        return jPanel;
    }

    private void finishSummaryPanel(JPanel jPanel) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 20, 5, 5);
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel.add((Component)this.m_lblSummary, gridBagConstraints);
    }

    private JPanel drawExplanationPanel() {
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JLabel jLabel = new JLabel(JAPMessages.getString(MSG_SHOW_DETAILS));
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
        gridBagConstraints.fill = 1;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 3;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(JAPMessages.getString(MSG_SYMBOLS));
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(GUIUtils.loadImageIcon(IMG_NOT_TRUSTED, true, false));
        jLabel.setText(JAPMessages.getString(MSG_NOT_TRUSTED));
        gridBagConstraints.insets.left = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(GUIUtils.loadImageIcon(IMG_INVALID, true, false));
        jLabel.setText(JAPMessages.getString(MSG_INVALID));
        gridBagConstraints.gridy = 4;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(GUIUtils.loadImageIcon(IMG_BOX_PURPLE, true, false));
        jLabel.setText("DSA");
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(GUIUtils.loadImageIcon(IMG_BOX_ORANGE, true, false));
        jLabel.setText("RSA");
        gridBagConstraints.gridy = 3;
        jPanel.add((Component)jLabel, gridBagConstraints);
        jLabel = new JLabel(GUIUtils.loadImageIcon(IMG_BOX_BLUE, true, false));
        jLabel.setText("ECC");
        gridBagConstraints.gridy = 4;
        jPanel.add((Component)jLabel, gridBagConstraints);
        return jPanel;
    }

    private JPanel drawOverviewPanel(boolean bl) {
        JLabel jLabel;
        String string;
        CountryMapper countryMapper;
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        Insets insets = new Insets(0, 0, 0, 0);
        Insets insets2 = new Insets(10, 10, 10, 10);
        jPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        jPanel.setBackground(Color.white);
        JPanel jPanel2 = new JPanel(new GridLayout(2, 1));
        jPanel2.setBackground(Color.white);
        JLabel jLabel2 = new JLabel(JAPMessages.getString(MSG_ROOT_CERTS));
        jLabel2.setToolTipText(JAPMessages.getString(HINT_ROOT_CERTS));
        jPanel2.add(jLabel2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = 17;
        gridBagConstraints.insets = insets2;
        gridBagConstraints.fill = 0;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        jPanel2 = new JPanel(new GridLayout(2, 1));
        jPanel2.setBackground(Color.white);
        if (bl) {
            jLabel2 = new JLabel(JAPMessages.getString(MSG_IS_CERTS));
            countryMapper = new CountryMapper(this.m_multiCertPath.getSubject().getCountryCode(), JAPMessages.getLocale());
            string = this.m_name;
            if (string.length() > 35) {
                string = string.substring(0, 32) + "...";
            }
            jLabel = new JLabel(string, GUIUtils.loadImageIcon("flags/" + countryMapper.getISOCode() + ".png", true, false), 2);
            jLabel.setToolTipText(JAPMessages.getString(HINT_IS, new Object[]{this.m_name, countryMapper.toString()}));
        } else {
            jLabel2 = new JLabel(JAPMessages.getString(MSG_OP_CERTS));
            countryMapper = new CountryMapper(this.m_multiCertPath.getIssuer().getCountryCode(), JAPMessages.getLocale());
            string = this.m_multiCertPath.getIssuer().getOrganisation();
            if (string.length() > 35) {
                string = string.substring(0, 32) + "...";
            }
            jLabel = new JLabel(string, GUIUtils.loadImageIcon("flags/" + countryMapper.getISOCode() + ".png", true, false), 2);
            jLabel.setToolTipText(JAPMessages.getString(HINT_OP, new Object[]{this.m_multiCertPath.getIssuer().getOrganisation(), countryMapper.toString()}));
        }
        jPanel2.add(jLabel2);
        jPanel2.add(jLabel);
        gridBagConstraints.gridy += 2;
        jPanel.add((Component)jPanel2, gridBagConstraints);
        if (!bl) {
            jPanel2 = new JPanel(new GridLayout(2, 1));
            jPanel2.setBackground(Color.white);
            jLabel2 = new JLabel(JAPMessages.getString(MSG_MIX_CERTS));
            jPanel2.add(jLabel2);
            countryMapper = new CountryMapper(this.m_multiCertPath.getSubject().getCountryCode(), JAPMessages.getLocale());
            string = this.m_name;
            if (string.length() > 35) {
                string = string.substring(0, 32) + "...";
            }
            jLabel2 = new JLabel(string, GUIUtils.loadImageIcon("flags/" + countryMapper.getISOCode() + ".png", true, false), 2);
            jLabel2.setToolTipText(JAPMessages.getString(HINT_MIX, new Object[]{this.m_name, countryMapper.toString()}));
            jPanel2.add(jLabel2);
            gridBagConstraints.gridy += 2;
            jPanel.add((Component)jPanel2, gridBagConstraints);
        }
        this.drawTrustGraph(jPanel);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 5, 0);
        gridBagConstraints.fill = 3;
        jPanel.add((Component)new JSeparator(1), gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = 2;
        gridBagConstraints.insets = insets;
        jPanel.add((Component)new JSeparator(0), gridBagConstraints);
        for (int i = 0; i < this.m_multiCertPath.getMaxLength() - 3; ++i) {
            gridBagConstraints.gridy += 2;
            jPanel.add((Component)new JSeparator(0), gridBagConstraints);
        }
        if (!bl) {
            gridBagConstraints.gridy += 2;
            jPanel.add((Component)new JSeparator(0), gridBagConstraints);
        }
        return jPanel;
    }

    private void drawTrustGraph(JPanel jPanel) {
        MultiCertTrustGraph.Node node;
        int n = 2;
        Enumeration enumeration = this.m_graph.getRootNodes();
        while (enumeration.hasMoreElements()) {
            node = (MultiCertTrustGraph.Node)enumeration.nextElement();
            n += this.drawSubGraph(jPanel, node, n, 0);
        }
        enumeration = this.m_graph.getOperatorNodes();
        while (enumeration.hasMoreElements()) {
            node = (MultiCertTrustGraph.Node)enumeration.nextElement();
            n += this.drawSubGraph(jPanel, node, n, 2);
        }
        enumeration = this.m_graph.getEndNodes();
        while (enumeration.hasMoreElements()) {
            node = (MultiCertTrustGraph.Node)enumeration.nextElement();
            n += this.drawSubGraph(jPanel, node, n, 4);
        }
    }

    private int drawSubGraph(JPanel jPanel, MultiCertTrustGraph.Node node, int n, int n2) {
        int n3 = 0;
        int n4 = 0;
        if (node.hasChildNodes()) {
            Enumeration enumeration = node.getChildNodes();
            while (enumeration.hasMoreElements()) {
                MultiCertTrustGraph.Node node2 = (MultiCertTrustGraph.Node)enumeration.nextElement();
                n3 += this.drawSubGraph(jPanel, node2, n + n4++, n2 + 2);
            }
            this.drawCertPanel(jPanel, n, n2, n3, node);
            for (int i = 0; i < n4; ++i) {
                int n5 = Math.round((float)n3 / (float)(i + 1));
                if (i + 1 == n5) {
                    this.drawArrow(jPanel, n + i, n2 + 1, 1, node.isTrusted());
                    continue;
                }
                if (i + 1 > n5) {
                    this.drawArrow(jPanel, n + i, n2 + 1, 8, node.isTrusted());
                    continue;
                }
                this.drawArrow(jPanel, n + i, n2 + 1, 2, node.isTrusted());
            }
            return n3;
        }
        this.drawCertPanel(jPanel, n, n2, 1, node);
        return 1;
    }

    private void drawCertPanel(JPanel jPanel, int n, int n2, int n3, MultiCertTrustGraph.Node node) {
        JPanel jPanel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        JAPCertificate jAPCertificate = node.getCertificate();
        Color color = Color.white;
        if (jAPCertificate == null) {
            return;
        }
        JButton jButton = new JButton();
        if (jAPCertificate.getPublicKey() instanceof MyRSAPublicKey) {
            if (node.isTrusted()) {
                if (jAPCertificate.getValidity().isValid(new Date())) {
                    jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_OK, true, false));
                    jButton.setRolloverIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_OK_DARK, true, false));
                } else {
                    jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_INVALID, true, false));
                    jButton.setRolloverIcon(GUIUtils.loadImageIcon("certs/cert_orange_invalid_dark.png", true, false));
                }
            } else {
                jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_NOK, true, false));
                jButton.setRolloverIcon(GUIUtils.loadImageIcon(IMG_CERT_ORANGE_NOK_DARK, true, false));
            }
        } else if (jAPCertificate.getPublicKey() instanceof MyECPublicKey) {
            if (node.isTrusted()) {
                if (jAPCertificate.getValidity().isValid(new Date())) {
                    jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_OK, true, false));
                    jButton.setRolloverIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_OK_DARK, true, false));
                } else {
                    jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_INVALID, true, false));
                    jButton.setRolloverIcon(GUIUtils.loadImageIcon("certs/cert_orange_invalid_dark.png", true, false));
                }
            } else {
                jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_NOK, true, false));
                jButton.setRolloverIcon(GUIUtils.loadImageIcon(IMG_CERT_BLUE_NOK_DARK, true, false));
            }
        } else if (node.isTrusted()) {
            if (jAPCertificate.getValidity().isValid(new Date())) {
                jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_OK, true, false));
                jButton.setRolloverIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_OK_DARK, true, false));
            } else {
                jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_INVALID, true, false));
                jButton.setRolloverIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_INVALID_DARK, true, false));
            }
        } else {
            jButton.setIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_NOK, true, false));
            jButton.setRolloverIcon(GUIUtils.loadImageIcon(IMG_CERT_PURPLE_NOK_DARK, true, false));
        }
        jButton.setToolTipText(this.getToolTipText(jAPCertificate));
        jButton.setBorder(BorderFactory.createEmptyBorder());
        jButton.setBackground(color);
        this.m_buttonsAndNodes.put(jButton, node);
        jButton.addMouseListener(this);
        gridBagConstraints.anchor = 10;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        jPanel2.add((Component)jButton, gridBagConstraints);
        jPanel2.setBackground(color);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 10;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = n;
        gridBagConstraints.gridy = n2;
        gridBagConstraints.gridwidth = n3;
        gridBagConstraints.insets = new Insets(5, 10, 5, 10);
        jPanel.add((Component)jPanel2, gridBagConstraints);
    }

    private void drawArrow(JPanel jPanel, int n, int n2, int n3, boolean bl) {
        JLabel jLabel;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        if (n3 == 1) {
            jLabel = bl ? new JLabel(GUIUtils.loadImageIcon(IMG_ARROW_NORTH, true, false)) : new JLabel(GUIUtils.loadImageIcon(IMG_ARROW_NORTH_NOK, true, false));
        } else if (n3 == 8) {
            jLabel = bl ? new JLabel(GUIUtils.loadImageIcon(IMG_ARROW_NORTH_WEST, true, false)) : new JLabel(GUIUtils.loadImageIcon(IMG_ARROW_NORTH_WEST_NOK, true, false));
        } else if (n3 == 2) {
            jLabel = bl ? new JLabel(GUIUtils.loadImageIcon(IMG_ARROW_NORTH_EAST, true, false)) : new JLabel(GUIUtils.loadImageIcon(IMG_ARROW_NORTH_EAST_NOK, true, false));
        } else {
            return;
        }
        jLabel.setToolTipText(JAPMessages.getString(HINT_ARROW));
        gridBagConstraints.fill = 0;
        gridBagConstraints.gridx = n;
        gridBagConstraints.gridy = n2;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        jPanel.add((Component)jLabel, gridBagConstraints);
    }

    private String getToolTipText(JAPCertificate jAPCertificate) {
        Object[] arrobject = new String[]{Util.replaceAll(Util.replaceAll(jAPCertificate.getSubject().getCommonName(), "<", "&lt;"), ">", "&gt;"), jAPCertificate.getSubject().getOrganisation() != null ? jAPCertificate.getSubject().getOrganisation() : "", jAPCertificate.getIssuer().getCommonName(), jAPCertificate.getIssuer().getOrganisation() != null ? jAPCertificate.getIssuer().getOrganisation() : "", jAPCertificate.getValidity().isValid(new Date()) ? JAPMessages.getString(MSG_VALID) : "<b>" + JAPMessages.getString(MSG_INVALID) + "</b>", jAPCertificate.getValidity().getValidFrom().toString(), jAPCertificate.getValidity().getValidTo().toString(), jAPCertificate.getPublicKey().getAlgorithm(), String.valueOf(jAPCertificate.getPublicKey().getKeyLength()), jAPCertificate.getSignatureAlgorithmName()};
        return JAPMessages.getString(HINT_CERT_DETAILS, arrobject);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (this.m_buttonsAndNodes.containsKey(mouseEvent.getSource())) {
            MultiCertTrustGraph.Node node = (MultiCertTrustGraph.Node)this.m_buttonsAndNodes.get(mouseEvent.getSource());
            if (mouseEvent.getClickCount() == 1) {
                CertDetailsDialog certDetailsDialog = new CertDetailsDialog(this.getParentComponent(), node.getCertificate(), node.isTrusted(), JAPMessages.getLocale());
                certDetailsDialog.setVisible(true);
            }
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
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

