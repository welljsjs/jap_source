/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.pay.xml.XMLGenericText;
import anon.util.JAPMessages;
import gui.JAPHyperlinkAdapter;
import gui.dialog.AbstractDialogExtraButton;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.JAPDialog;
import gui.dialog.WorkerContentPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class TermsAndConditionsPane
extends DialogContentPane
implements DialogContentPane.IWizardSuitable {
    public static final String MSG_HEADING = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_heading";
    private static final String MSG_ERROR_HAVE_TO_ACCEPT = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_havetoaccept";
    private static final String MSG_NO_TERMS_FOUND = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_notermsfound";
    private static final String MSG_I_ACCEPT = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_iaccept";
    private static final String MSG_CANCEL_HEADING = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_cancellation_heading";
    private static final String MSG_CANCEL_ERROR_HAVE_TO_ACCEPT = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_cancellation_havetoaccept";
    private static final String MSG_CANCEL_NO_POLICY_FOUND = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_cancellation_nopolicyfound";
    private static final String MSG_CANCEL_I_ACCEPT = (class$gui$dialog$TermsAndConditionsPane == null ? (class$gui$dialog$TermsAndConditionsPane = TermsAndConditionsPane.class$("gui.dialog.TermsAndConditionsPane")) : class$gui$dialog$TermsAndConditionsPane).getName() + "_cancellation_iaccept";
    private WorkerContentPane m_fetchTermsPane;
    private GridBagConstraints m_c = new GridBagConstraints();
    private Container m_rootPanel;
    private JCheckBox m_accepted;
    private JEditorPane m_termsPane;
    private JScrollPane m_scrollingTerms;
    private IMessages m_messages;
    static /* synthetic */ Class class$gui$dialog$TermsAndConditionsPane;

    public TermsAndConditionsPane(JAPDialog jAPDialog, WorkerContentPane workerContentPane, boolean bl, IMessages iMessages) {
        super(jAPDialog, new DialogContentPane.Layout(JAPMessages.getString(iMessages.getHeading()), -1){

            public boolean isCentered() {
                return false;
            }
        }, new DialogContentPaneOptions(2, (DialogContentPane)workerContentPane){

            public int countExtraButtons() {
                return 1;
            }

            public AbstractDialogExtraButton getExtraButtonInternal(int n) {
                return new AbstractDialogExtraButton(){

                    public String getText() {
                        return "Drucken";
                    }

                    public void doAction() {
                        System.out.println("print");
                    }
                };
            }
        });
        this.m_messages = iMessages;
        this.m_fetchTermsPane = workerContentPane;
        this.init(bl);
        if (bl) {
            this.m_accepted.setSelected(false);
        }
    }

    public TermsAndConditionsPane(JAPDialog jAPDialog, boolean bl, IMessages iMessages) {
        super(jAPDialog, new DialogContentPane.Layout(JAPMessages.getString(iMessages.getHeading()), -1){

            public boolean isCentered() {
                return false;
            }
        }, new DialogContentPaneOptions(2){

            public int countExtraButtons() {
                return 1;
            }

            public AbstractDialogExtraButton getExtraButtonInternal(int n) {
                return new AbstractDialogExtraButton(){

                    public String getText() {
                        return "Drucken";
                    }

                    public void doAction() {
                        System.out.println("print");
                    }
                };
            }
        });
        this.m_messages = iMessages;
        this.init(true);
        this.m_accepted.setSelected(bl);
    }

    private void init(boolean bl) {
        this.setDefaultButtonOperation(266);
        this.m_rootPanel = this.getContentPane();
        this.m_c = new GridBagConstraints();
        this.m_rootPanel.setLayout(new GridBagLayout());
        this.m_c.gridx = 0;
        this.m_c.gridy = 0;
        this.m_c.weightx = 1.0;
        this.m_c.weighty = 1.0;
        this.m_c.insets = new Insets(5, 5, 5, 5);
        this.m_c.anchor = 18;
        this.m_c.fill = 1;
        String string = JAPMessages.getString(this.m_messages.getNotFound());
        this.m_termsPane = new JEditorPane("text/html", string);
        this.m_termsPane.setEditable(false);
        this.m_termsPane.addHyperlinkListener(new JAPHyperlinkAdapter());
        this.m_scrollingTerms = new JScrollPane(this.m_termsPane);
        this.m_scrollingTerms.setHorizontalScrollBarPolicy(31);
        this.m_scrollingTerms.setPreferredSize(new Dimension(400, 200));
        this.m_rootPanel.add((Component)this.m_scrollingTerms, this.m_c);
        if (bl) {
            String string2 = JAPMessages.getString(this.m_messages.getIAccept());
            this.m_accepted = new JCheckBox(string2);
            this.m_c.weightx = 0.0;
            this.m_c.weighty = 0.0;
            ++this.m_c.gridy;
            this.m_rootPanel.add((Component)this.m_accepted, this.m_c);
        }
        this.addComponentListener(new ComponentAdapter(){

            public void componentShown(ComponentEvent componentEvent) {
                SwingUtilities.invokeLater(new Runnable(){

                    public void run() {
                        TermsAndConditionsPane.this.m_scrollingTerms.getVerticalScrollBar().setValue(0);
                    }
                });
            }
        });
    }

    public boolean isTermsAccepted() {
        if (this.m_accepted != null) {
            return this.m_accepted.isSelected();
        }
        return true;
    }

    private void showTerms() {
        String string;
        WorkerContentPane workerContentPane = this.m_fetchTermsPane;
        Object object = workerContentPane.getValue();
        if (object == null) {
            string = JAPMessages.getString(this.m_messages.getNotFound());
        } else {
            XMLGenericText xMLGenericText = (XMLGenericText)object;
            string = xMLGenericText.getText();
        }
        this.m_termsPane.setText(string);
    }

    public void setText(String string) {
        this.m_termsPane.setText(string);
    }

    public DialogContentPane.CheckError checkYesOK() {
        DialogContentPane.CheckError checkError = super.checkYesOK();
        if (checkError == null && !this.isTermsAccepted()) {
            checkError = new DialogContentPane.CheckError(JAPMessages.getString(this.m_messages.getErrorHaveToAccept()));
        }
        return checkError;
    }

    public DialogContentPane.CheckError checkUpdate() {
        if (this.m_fetchTermsPane != null) {
            this.showTerms();
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

    public static interface IMessages {
        public String getHeading();

        public String getErrorHaveToAccept();

        public String getNotFound();

        public String getIAccept();
    }

    public static final class CancellationPolicyMessages
    implements IMessages {
        public String getHeading() {
            return MSG_CANCEL_HEADING;
        }

        public String getErrorHaveToAccept() {
            return MSG_CANCEL_ERROR_HAVE_TO_ACCEPT;
        }

        public String getNotFound() {
            return MSG_CANCEL_NO_POLICY_FOUND;
        }

        public String getIAccept() {
            return MSG_CANCEL_I_ACCEPT;
        }
    }

    public static final class TermsAndConditionsMessages
    implements IMessages {
        public String getHeading() {
            return MSG_HEADING;
        }

        public String getErrorHaveToAccept() {
            return MSG_ERROR_HAVE_TO_ACCEPT;
        }

        public String getNotFound() {
            return MSG_NO_TERMS_FOUND;
        }

        public String getIAccept() {
            return MSG_I_ACCEPT;
        }
    }
}

