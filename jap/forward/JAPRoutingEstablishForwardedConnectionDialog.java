/*
 * Decompiled with CFR 0.150.
 */
package jap.forward;

import anon.forward.client.ClientForwardException;
import anon.forward.client.ForwardConnectionDescriptor;
import anon.forward.client.ForwarderInformationGrabber;
import anon.forward.client.ProgressCounter;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.transport.address.Endpoint;
import anon.transport.address.IAddress;
import anon.util.JAPMessages;
import anon.util.captcha.IImageEncodedCaptcha;
import anon.util.captcha.MyImage;
import gui.JAPHtmlMultiLineLabel;
import gui.dialog.DialogContentPane;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import jap.JAPController;
import jap.JAPModel;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.io.ByteArrayInputStream;
import java.util.Observable;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import logging.LogHolder;
import logging.LogType;

public class JAPRoutingEstablishForwardedConnectionDialog {
    private boolean m_bForwardingSuccessful = false;
    private Component m_parentComponent;
    private Font m_fontSetting;

    public JAPRoutingEstablishForwardedConnectionDialog(Component component) {
        this.m_parentComponent = component;
        boolean bl = false;
        while (!bl) {
            MixCascade mixCascade;
            ForwardConnectionDescriptor forwardConnectionDescriptor;
            IImageEncodedCaptcha iImageEncodedCaptcha = null;
            if (!JAPModel.getInstance().getRoutingSettings().getForwardInfoService()) {
                iImageEncodedCaptcha = this.showConfigClientDialogGetForwarderInfo();
            }
            if (iImageEncodedCaptcha == null) {
                iImageEncodedCaptcha = this.showConfigClientDialogViaMail();
            }
            if (iImageEncodedCaptcha == null) {
                bl = true;
                continue;
            }
            ListenerInterface listenerInterface = this.showConfigClientDialogCaptcha(iImageEncodedCaptcha);
            if (listenerInterface == null) {
                bl = true;
                continue;
            }
            JAPModel.getInstance().getRoutingSettings().setTCPForwarder(listenerInterface.getHost(), listenerInterface.getPort());
            if (!this.showConfigClientDialogConnectToForwarder() || (forwardConnectionDescriptor = this.showConfigClientDialogGetOffer()) == null || (mixCascade = this.showConfigClientDialogStep2(forwardConnectionDescriptor)) == null) continue;
            bl = this.m_bForwardingSuccessful = this.showConfigClientDialogAnnounceCascade(mixCascade);
        }
    }

    public JAPRoutingEstablishForwardedConnectionDialog(Component component, IAddress iAddress) {
        LogHolder.log(7, LogType.NET, "Start establishing forward connection with a given address");
        this.m_parentComponent = component;
        JAPModel.getInstance().getRoutingSettings().setForwarderAddress(iAddress);
        if (!this.showConfigClientDialogConnectToForwarder()) {
            return;
        }
        ForwardConnectionDescriptor forwardConnectionDescriptor = this.showConfigClientDialogGetOffer();
        if (forwardConnectionDescriptor == null) {
            return;
        }
        MixCascade mixCascade = JAPController.getInstance().getCurrentMixCascade();
        if (mixCascade == null || !mixCascade.isUserDefined()) {
            mixCascade = this.showConfigClientDialogStep2(forwardConnectionDescriptor);
        }
        if (mixCascade == null) {
            return;
        }
        this.m_bForwardingSuccessful = this.showConfigClientDialogAnnounceCascade(mixCascade);
    }

    public boolean isForwardingSuccessful() {
        return this.m_bForwardingSuccessful;
    }

    private Component getRootComponent() {
        return this.m_parentComponent;
    }

    private Font getFontSetting() {
        return this.m_fontSetting;
    }

    private IImageEncodedCaptcha showConfigClientDialogGetForwarderInfo() {
        JAPDialog jAPDialog = new JAPDialog(this.getRootComponent(), JAPMessages.getString("settingsRoutingClientConfigDialogInfoServiceTitle"));
        jAPDialog.setResizable(false);
        jAPDialog.setDefaultCloseOperation(2);
        final Vector vector = new Vector();
        final Vector vector2 = new Vector();
        Runnable runnable = new Runnable(){

            public void run() {
                ForwarderInformationGrabber forwarderInformationGrabber = new ForwarderInformationGrabber();
                Thread.interrupted();
                if (forwarderInformationGrabber.getErrorCode() == 0) {
                    vector2.addElement(forwarderInformationGrabber.getCaptcha());
                } else if (forwarderInformationGrabber.getErrorCode() == 1) {
                    vector.addElement(JAPMessages.getString("settingsRoutingClientGrabCapchtaInfoServiceError"));
                } else if (forwarderInformationGrabber.getErrorCode() == 3) {
                    vector.addElement(JAPMessages.getString("settingsRoutingClientGrabCapchtaImplementationError"));
                } else {
                    vector.addElement(JAPMessages.getString("settingsRoutingClientGrabCaptchaUnknownError"));
                }
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString("settingsRoutingClientConfigDialogInfoServiceLabel"), runnable);
        workerContentPane.updateDialog();
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        IImageEncodedCaptcha iImageEncodedCaptcha = null;
        if (vector2.size() > 0) {
            iImageEncodedCaptcha = (IImageEncodedCaptcha)vector2.firstElement();
        } else if (vector.size() > 0) {
            LogHolder.log(3, LogType.NET, (String)vector.firstElement());
        }
        return iImageEncodedCaptcha;
    }

    private IImageEncodedCaptcha showConfigClientDialogViaMail() {
        final JAPDialog jAPDialog = new JAPDialog(this.getRootComponent(), JAPMessages.getString("settingsRoutingClientConfigDialog1MailTitle"));
        final JPanel jPanel = new JPanel();
        jAPDialog.getContentPane().add(jPanel);
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel(JAPMessages.getString("settingsRoutingClientConfigDialog1MailInstructions1") + "japmailsystem@infoservice.inf.tu-dresden.de" + JAPMessages.getString("settingsRoutingClientConfigDialog1MailInstructions2"), this.getFontSetting());
        JLabel jLabel = new JLabel(JAPMessages.getString("settingsRoutingClientConfigDialog1MailAnswerLabel"));
        jLabel.setFont(this.getFontSetting());
        final JTextArea jTextArea = new JTextArea();
        jTextArea.setFont(this.getFontSetting());
        jTextArea.setRows(7);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jTextArea.addMouseListener(new MouseAdapter(){

            public void mousePressed(MouseEvent mouseEvent) {
                this.handlePopupEvent(mouseEvent);
            }

            public void mouseReleased(MouseEvent mouseEvent) {
                this.handlePopupEvent(mouseEvent);
            }

            private void handlePopupEvent(MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger()) {
                    JPopupMenu jPopupMenu = new JPopupMenu();
                    JMenuItem jMenuItem = new JMenuItem(JAPMessages.getString("settingsRoutingClientConfigDialog1MailAnswerPopupPaste"));
                    jMenuItem.addActionListener(new ActionListener(){

                        public void actionPerformed(ActionEvent actionEvent) {
                            jTextArea.paste();
                        }
                    });
                    jPopupMenu.add(jMenuItem);
                    jPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });
        JButton jButton = new JButton(JAPMessages.getString("settingsRoutingClientConfigDialog1MailInsertButton"));
        jButton.setFont(this.getFontSetting());
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                jTextArea.setText("");
                jTextArea.paste();
            }
        });
        final Vector vector = new Vector();
        final JButton jButton2 = new JButton(JAPMessages.getString("settingsRoutingClientConfigDialog1MailNextButton"));
        jButton2.setFont(this.getFontSetting());
        jButton2.setEnabled(false);
        jButton2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                ForwarderInformationGrabber forwarderInformationGrabber = new ForwarderInformationGrabber(jTextArea.getText());
                if (forwarderInformationGrabber.getErrorCode() == 0) {
                    vector.addElement(forwarderInformationGrabber.getCaptcha());
                    jAPDialog.dispose();
                }
                if (forwarderInformationGrabber.getErrorCode() == 3) {
                    JAPDialog.showErrorDialog((Component)jPanel, JAPMessages.getString("settingsRoutingClientGrabCapchtaImplementationError"));
                    jAPDialog.dispose();
                }
                if (forwarderInformationGrabber.getErrorCode() == 2) {
                    JAPDialog.showErrorDialog((Component)jPanel, JAPMessages.getString("settingsRoutingClientConfigDialog1MailParseError"));
                    jTextArea.setText("");
                }
            }
        });
        jTextArea.addCaretListener(new CaretListener(){

            public void caretUpdate(CaretEvent caretEvent) {
                if (!jTextArea.getText().equals("")) {
                    jButton2.setEnabled(true);
                } else {
                    jButton2.setEnabled(false);
                }
            }
        });
        JButton jButton3 = new JButton(JAPMessages.getString("cancelButton"));
        jButton3.setFont(this.getFontSetting());
        jButton3.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                jAPDialog.dispose();
            }
        });
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsRoutingClientConfigDialog1MailBorder"));
        titledBorder.setTitleFont(this.getFontSetting());
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        gridBagLayout.setConstraints(jAPHtmlMultiLineLabel, gridBagConstraints);
        jPanel.add(jAPHtmlMultiLineLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(15, 5, 0, 5);
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel.add(jLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 5, 2, 5);
        gridBagConstraints.weighty = 1.0;
        gridBagLayout.setConstraints(jScrollPane, gridBagConstraints);
        jPanel.add(jScrollPane);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(0, 5, 20, 5);
        gridBagConstraints.weighty = 0.0;
        gridBagLayout.setConstraints(jButton, gridBagConstraints);
        jPanel.add(jButton);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.insets = new Insets(0, 5, 10, 5);
        gridBagLayout.setConstraints(jButton3, gridBagConstraints);
        jPanel.add(jButton3);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new Insets(0, 5, 10, 5);
        gridBagLayout.setConstraints(jButton2, gridBagConstraints);
        jPanel.add(jButton2);
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        IImageEncodedCaptcha iImageEncodedCaptcha = null;
        if (vector.size() > 0) {
            iImageEncodedCaptcha = (IImageEncodedCaptcha)vector.firstElement();
        }
        return iImageEncodedCaptcha;
    }

    private ListenerInterface showConfigClientDialogCaptcha(final IImageEncodedCaptcha iImageEncodedCaptcha) {
        final JAPDialog jAPDialog = new JAPDialog(this.getRootComponent(), JAPMessages.getString("settingsRoutingClientConfigDialogCaptchaTitle"));
        final JPanel jPanel = new JPanel();
        jAPDialog.getContentPane().add(jPanel);
        MyImage myImage = iImageEncodedCaptcha.getImage();
        Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(myImage.getWidth(), myImage.getHeight(), myImage.getPixels(), 0, myImage.getWidth()));
        JLabel jLabel = new JLabel(new ImageIcon(image));
        JLabel jLabel2 = new JLabel(JAPMessages.getString("settingsRoutingClientConfigDialogCaptchaCharacterSetLabel") + " " + iImageEncodedCaptcha.getCharacterSet());
        jLabel2.setFont(this.getFontSetting());
        JLabel jLabel3 = new JLabel(JAPMessages.getString("settingsRoutingClientConfigDialogCaptchaCharacterNumberLabel") + " " + Integer.toString(iImageEncodedCaptcha.getCharacterNumber()));
        jLabel3.setFont(this.getFontSetting());
        JLabel jLabel4 = new JLabel(JAPMessages.getString("settingsRoutingClientConfigDialogCaptchaInsertCaptchaLabel"));
        jLabel4.setFont(this.getFontSetting());
        final JButton jButton = new JButton(JAPMessages.getString("settingsRoutingClientConfigDialogCaptchaNextButton"));
        jButton.setFont(this.getFontSetting());
        final CaptchaInputField captchaInputField = new CaptchaInputField(iImageEncodedCaptcha);
        captchaInputField.getDocument().addDocumentListener(new DocumentListener(){

            public void changedUpdate(DocumentEvent documentEvent) {
            }

            public void insertUpdate(DocumentEvent documentEvent) {
                if (documentEvent.getDocument().getLength() == iImageEncodedCaptcha.getCharacterNumber()) {
                    jButton.setEnabled(true);
                } else {
                    jButton.setEnabled(false);
                }
            }

            public void removeUpdate(DocumentEvent documentEvent) {
                if (documentEvent.getDocument().getLength() == iImageEncodedCaptcha.getCharacterNumber()) {
                    jButton.setEnabled(true);
                } else {
                    jButton.setEnabled(false);
                }
            }
        });
        captchaInputField.setFont(this.getFontSetting());
        final Vector vector = new Vector();
        if (captchaInputField.getText().length() != iImageEncodedCaptcha.getCharacterNumber()) {
            jButton.setEnabled(false);
        }
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    byte[] arrby = new byte[10];
                    byte[] arrby2 = iImageEncodedCaptcha.solveCaptcha(captchaInputField.getText().trim(), arrby);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby2, 10, 4);
                    String string = Integer.toString(byteArrayInputStream.read());
                    for (int i = 0; i < 3; ++i) {
                        string = string + "." + Integer.toString(byteArrayInputStream.read());
                    }
                    ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(arrby2, 14, 2);
                    int n = byteArrayInputStream2.read();
                    n = n * 256 + byteArrayInputStream2.read();
                    ListenerInterface listenerInterface = new ListenerInterface(string, n);
                    vector.addElement(listenerInterface);
                    jAPDialog.dispose();
                }
                catch (Exception exception) {
                    JAPDialog.showErrorDialog((Component)jPanel, JAPMessages.getString("settingsRoutingClientConfigDialogCaptchaError"));
                    captchaInputField.setText("");
                }
            }
        });
        JButton jButton2 = new JButton(JAPMessages.getString("cancelButton"));
        jButton2.setFont(this.getFontSetting());
        jButton2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                jAPDialog.dispose();
            }
        });
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsRoutingClientConfigDialogCaptchaBorder"));
        titledBorder.setTitleFont(this.getFontSetting());
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel.add(jLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 5, 0, 5);
        gridBagLayout.setConstraints(jLabel2, gridBagConstraints);
        jPanel.add(jLabel2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        gridBagLayout.setConstraints(jLabel3, gridBagConstraints);
        jPanel.add(jLabel3);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(10, 5, 0, 5);
        gridBagLayout.setConstraints(jLabel4, gridBagConstraints);
        jPanel.add(jLabel4);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(captchaInputField, gridBagConstraints);
        jPanel.add(captchaInputField);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(20, 5, 5, 5);
        gridBagLayout.setConstraints(jButton2, gridBagConstraints);
        jPanel.add(jButton2);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new Insets(20, 5, 5, 5);
        gridBagLayout.setConstraints(jButton, gridBagConstraints);
        jPanel.add(jButton);
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        ListenerInterface listenerInterface = null;
        if (vector.size() > 0) {
            listenerInterface = (ListenerInterface)vector.firstElement();
        }
        return listenerInterface;
    }

    private boolean showConfigClientDialogConnectToForwarder() {
        IAddress iAddress;
        final Vector vector = new Vector();
        Runnable runnable = new Runnable(){

            public void run() {
                if (!JAPModel.getInstance().getRoutingSettings().setRoutingMode(1)) {
                    vector.addElement(JAPMessages.getString("settingsRoutingClientConfigConnectToForwarderError"));
                }
            }
        };
        IAddress iAddress2 = iAddress = JAPModel.getInstance().getRoutingSettings().getForwarderAddress();
        do {
            vector.removeAllElements();
            String string = "";
            if (iAddress2 != null) {
                string = Endpoint.toURN(iAddress2);
            }
            JAPDialog jAPDialog = new JAPDialog(this.getRootComponent(), JAPMessages.getString("settingsRoutingClientConfigConnectToForwarderTitle"));
            jAPDialog.setResizable(false);
            jAPDialog.setDefaultCloseOperation(2);
            WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString("settingsRoutingClientConfigDialogConnectToForwarderLabel"), JAPMessages.getString("settingsRoutingClientConfigDialogConnectToForwarderInfoLabel") + " " + string, runnable);
            workerContentPane.updateDialog();
            jAPDialog.pack();
            jAPDialog.setVisible(true);
            if (!workerContentPane.hasValidValue() || vector.size() != 0) continue;
            return true;
        } while ((iAddress2 = JAPModel.getInstance().getRoutingSettings().nextForwarderAddress()) != iAddress);
        if (vector.size() > 0) {
            JAPDialog.showErrorDialog(this.getRootComponent(), (String)vector.firstElement());
        }
        return false;
    }

    private ForwardConnectionDescriptor showConfigClientDialogGetOffer() {
        JAPDialog jAPDialog = new JAPDialog(this.getRootComponent(), JAPMessages.getString("settingsRoutingClientConfigGetOfferTitle"));
        jAPDialog.setResizable(false);
        jAPDialog.setDefaultCloseOperation(0);
        final Vector vector = new Vector();
        final Vector vector2 = new Vector();
        Runnable runnable = new Runnable(){

            public void run() {
                try {
                    ForwardConnectionDescriptor forwardConnectionDescriptor = JAPModel.getInstance().getRoutingSettings().getConnectionDescriptor();
                    vector2.addElement(forwardConnectionDescriptor);
                }
                catch (ClientForwardException clientForwardException) {
                    LogHolder.log(3, LogType.NET, clientForwardException);
                    if (clientForwardException.getErrorCode() == 1) {
                        vector.addElement(JAPMessages.getString("settingsRoutingClientGetOfferConnectError"));
                    }
                    if (clientForwardException.getErrorCode() == 3) {
                        vector.addElement(JAPMessages.getString("settingsRoutingClientGetOfferVersionError"));
                    }
                    vector.addElement(JAPMessages.getString("settingsRoutingClientGetOfferUnknownError"));
                }
            }
        };
        ProgressCounter progressCounter = JAPModel.getInstance().getRoutingSettings().getPacketCounter();
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString("settingsRoutingClientConfigDialogGetOfferLabel"), runnable, (Observable)progressCounter){

            public DialogContentPane.CheckError checkCancel() {
                DialogContentPane.CheckError checkError = super.checkCancel();
                if (checkError == null) {
                    JAPModel.getInstance().getRoutingSettings().setRoutingMode(0);
                }
                return checkError;
            }
        };
        workerContentPane.updateDialog();
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        jAPDialog.dispose();
        ForwardConnectionDescriptor forwardConnectionDescriptor = null;
        if (vector2.size() > 0) {
            forwardConnectionDescriptor = (ForwardConnectionDescriptor)vector2.firstElement();
        } else if (vector.size() > 0) {
            JAPDialog.showErrorDialog(this.getRootComponent(), (String)vector.firstElement());
        }
        return forwardConnectionDescriptor;
    }

    private MixCascade showConfigClientDialogStep2(ForwardConnectionDescriptor forwardConnectionDescriptor) {
        final JAPDialog jAPDialog = new JAPDialog(this.getRootComponent(), JAPMessages.getString("settingsRoutingClientConfigDialog2Title"));
        jAPDialog.setDefaultCloseOperation(0);
        JPanel jPanel = new JPanel();
        jAPDialog.getContentPane().add(jPanel);
        JLabel jLabel = new JLabel(JAPMessages.getString("settingsRoutingClientConfigDialog2GuaranteedBandwidthLabel") + " " + Integer.toString(forwardConnectionDescriptor.getGuaranteedBandwidth()));
        jLabel.setFont(this.getFontSetting());
        JLabel jLabel2 = new JLabel(JAPMessages.getString("settingsRoutingClientConfigDialog2MaxBandwidthLabel") + " " + Integer.toString(forwardConnectionDescriptor.getMaximumBandwidth()));
        jLabel2.setFont(this.getFontSetting());
        JLabel jLabel3 = new JLabel();
        jLabel3.setFont(this.getFontSetting());
        if (forwardConnectionDescriptor.getMinDummyTrafficInterval() != -1) {
            jLabel3.setText(JAPMessages.getString("settingsRoutingClientConfigDialog2DummyTrafficLabel") + " " + Integer.toString(forwardConnectionDescriptor.getMinDummyTrafficInterval() / 1000));
        } else {
            jLabel3.setText(JAPMessages.getString("settingsRoutingClientConfigDialog2DummyTrafficLabel") + " " + JAPMessages.getString("settingsRoutingClientConfigDialog2DummyTrafficLabelNoNeed"));
        }
        final JButton jButton = new JButton(JAPMessages.getString("settingsRoutingClientConfigDialog2FinishButton"));
        JLabel jLabel4 = new JLabel(JAPMessages.getString("settingsRoutingClientConfigDialog2MixCascadesLabel"));
        jLabel4.setFont(this.getFontSetting());
        final JList jList = new JList(forwardConnectionDescriptor.getMixCascadeList());
        jList.setSelectionMode(0);
        jList.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                if (jList.getSelectedIndex() != -1) {
                    jButton.setEnabled(true);
                } else {
                    jButton.setEnabled(false);
                }
            }
        });
        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setFont(this.getFontSetting());
        final Vector vector = new Vector();
        jButton.setFont(this.getFontSetting());
        if (jList.getSelectedIndex() != -1) {
            jButton.setEnabled(true);
        } else {
            jButton.setEnabled(false);
        }
        jButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                vector.addElement((MixCascade)jList.getSelectedValue());
                jAPDialog.dispose();
            }
        });
        JButton jButton2 = new JButton(JAPMessages.getString("cancelButton"));
        jButton2.setFont(this.getFontSetting());
        jButton2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                JAPModel.getInstance().getRoutingSettings().setRoutingMode(0);
                jAPDialog.dispose();
            }
        });
        TitledBorder titledBorder = new TitledBorder(JAPMessages.getString("settingsRoutingClientConfigDialog2Border"));
        titledBorder.setTitleFont(this.getFontSetting());
        jPanel.setBorder(titledBorder);
        GridBagLayout gridBagLayout = new GridBagLayout();
        jPanel.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 5, 10, 5);
        gridBagLayout.setConstraints(jLabel, gridBagConstraints);
        jPanel.add(jLabel);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagLayout.setConstraints(jLabel2, gridBagConstraints);
        jPanel.add(jLabel2);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagLayout.setConstraints(jLabel3, gridBagConstraints);
        jPanel.add(jLabel3);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        gridBagLayout.setConstraints(jLabel4, gridBagConstraints);
        jPanel.add(jLabel4);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 20, 5);
        gridBagLayout.setConstraints(jScrollPane, gridBagConstraints);
        jPanel.add(jScrollPane);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        gridBagLayout.setConstraints(jButton2, gridBagConstraints);
        jPanel.add(jButton2);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        gridBagLayout.setConstraints(jButton, gridBagConstraints);
        jPanel.add(jButton);
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        MixCascade mixCascade = null;
        if (vector.size() > 0) {
            mixCascade = (MixCascade)vector.firstElement();
        }
        return mixCascade;
    }

    private boolean showConfigClientDialogAnnounceCascade(final MixCascade mixCascade) {
        JAPDialog jAPDialog = new JAPDialog(this.getRootComponent(), JAPMessages.getString("settingsRoutingClientConfigDialogAnnounceCascadeTitle"));
        jAPDialog.setResizable(false);
        jAPDialog.setDefaultCloseOperation(2);
        final Vector vector = new Vector();
        Runnable runnable = new Runnable(){

            public void run() {
                try {
                    JAPModel.getInstance().getRoutingSettings().selectMixCascade(mixCascade);
                }
                catch (ClientForwardException clientForwardException) {
                    LogHolder.log(3, LogType.NET, "JAPConfRouting: showConfigClientDialogAnnounceCascade: " + clientForwardException.toString());
                    if (clientForwardException.getErrorCode() == 1) {
                        vector.addElement(JAPMessages.getString("settingsRoutingClientAnnounceCascadeConnectError"));
                    }
                    vector.addElement(JAPMessages.getString("settingsRoutingClientAnnounceCascadeUnknownError"));
                }
            }
        };
        WorkerContentPane workerContentPane = new WorkerContentPane(jAPDialog, JAPMessages.getString("settingsRoutingClientConfigDialogAnnounceCascadeLabel"), runnable){

            public DialogContentPane.CheckError checkCancel() {
                DialogContentPane.CheckError checkError = super.checkCancel();
                if (checkError == null) {
                    JAPModel.getInstance().getRoutingSettings().setRoutingMode(0);
                }
                return checkError;
            }
        };
        workerContentPane.updateDialog();
        jAPDialog.pack();
        jAPDialog.setVisible(true);
        boolean bl = false;
        if (vector.size() == 0) {
            JAPController.getInstance().setCurrentMixCascade(mixCascade);
            JAPController.getInstance().setAnonMode(true);
            bl = true;
        } else if (vector.size() > 0) {
            JAPDialog.showErrorDialog(this.m_parentComponent, (String)vector.firstElement());
        }
        return bl;
    }

    private class CaptchaInputField
    extends JTextField {
        private static final long serialVersionUID = 1L;
        private IImageEncodedCaptcha m_captcha;

        public CaptchaInputField(IImageEncodedCaptcha iImageEncodedCaptcha) {
            this.m_captcha = iImageEncodedCaptcha;
        }

        protected Document createDefaultModel() {
            return new PlainDocument(){
                private static final long serialVersionUID = 1L;

                public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
                    if (this.getLength() + string.length() <= CaptchaInputField.this.m_captcha.getCharacterNumber()) {
                        boolean bl = false;
                        for (int i = 0; i < string.length() && !bl; ++i) {
                            if (CaptchaInputField.this.m_captcha.getCharacterSet().indexOf(string.toUpperCase().substring(i, i + 1)) >= 0) continue;
                            bl = true;
                        }
                        if (!bl) {
                            super.insertString(n, string.toUpperCase(), attributeSet);
                        }
                    }
                }
            };
        }
    }
}

