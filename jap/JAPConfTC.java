/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.Database;
import anon.infoservice.ServiceOperator;
import anon.terms.TermsAndConditions;
import anon.terms.TermsAndConditionsResponseHandler;
import anon.util.JAPMessages;
import gui.JAPHyperlinkAdapter;
import gui.UpperLeftStartViewport;
import gui.dialog.JAPDialog;
import jap.AbstractJAPConfModule;
import jap.IJAPConfSavePoint;
import jap.JAPConf;
import jap.JAPController;
import jap.TermsAndConditionsOperatorTable;
import jap.TermsAndCondtionsTableController;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class JAPConfTC
extends AbstractJAPConfModule
implements Observer,
TermsAndCondtionsTableController {
    private static final String MSG_TAB_TITLE = (class$jap$JAPConfTC == null ? (class$jap$JAPConfTC = JAPConfTC.class$("jap.JAPConfTC")) : class$jap$JAPConfTC).getName() + "_tabTitle";
    private static final String MSG_ERR_REJECT_IMPOSSIBLE = (class$jap$JAPConfTC == null ? (class$jap$JAPConfTC = JAPConfTC.class$("jap.JAPConfTC")) : class$jap$JAPConfTC).getName() + "_errRejectImpossible";
    private TermsAndConditionsOperatorTable m_tblOperators;
    private JEditorPane m_termsPane;
    private JScrollPane m_scrollingTerms;
    static /* synthetic */ Class class$jap$JAPConfTC;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;

    protected JAPConfTC(IJAPConfSavePoint iJAPConfSavePoint) {
        super(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean initObservers() {
        if (super.initObservers()) {
            Object object = this.LOCK_OBSERVABLE;
            synchronized (object) {
                TermsAndConditionsResponseHandler.get().addObserver(this);
                return true;
            }
        }
        return false;
    }

    public String getTabTitle() {
        return JAPMessages.getString(MSG_TAB_TITLE);
    }

    public void recreateRootPanel() {
        JPanel jPanel = this.getRootPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = 18;
        gridBagConstraints.fill = 1;
        this.m_tblOperators = new TermsAndConditionsOperatorTable();
        this.m_tblOperators.setController(this);
        JScrollPane jScrollPane = new JScrollPane(this.m_tblOperators);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jScrollPane.setPreferredSize(this.m_tblOperators.getPreferredSize());
        jPanel.add((Component)jScrollPane, gridBagConstraints);
        ++gridBagConstraints.gridy;
        gridBagConstraints.weighty = 0.8;
        this.m_termsPane = new JEditorPane("text/html", "");
        this.m_termsPane.addHyperlinkListener(new JAPHyperlinkAdapter());
        this.m_termsPane.setEditable(false);
        this.m_scrollingTerms = new JScrollPane();
        this.m_scrollingTerms.setViewport(new UpperLeftStartViewport());
        this.m_scrollingTerms.getViewport().add(this.m_termsPane);
        jPanel.add((Component)this.m_scrollingTerms, gridBagConstraints);
        jPanel.validate();
    }

    public String getHelpContext() {
        return "services_tc";
    }

    protected void onRootPanelShown() {
        this.m_tblOperators.setOperators(Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = JAPConfTC.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryList());
    }

    protected boolean onOkPressed() {
        Vector[] arrvector = new Vector[]{this.m_tblOperators.getTermsAccepted(), this.m_tblOperators.getTermsRejected()};
        TermsAndConditions termsAndConditions = null;
        boolean bl = false;
        boolean bl2 = false;
        for (int i = 0; i < arrvector.length; ++i) {
            boolean bl3 = bl = i == 0;
            if (arrvector[i] == null) continue;
            for (int j = 0; j < arrvector[i].size(); ++j) {
                termsAndConditions = (TermsAndConditions)arrvector[i].elementAt(j);
                if (termsAndConditions == null) continue;
                if (!bl && !JAPController.getInstance().isOperatorOfConnectedMix(termsAndConditions.getOperator())) {
                    if (bl2) continue;
                    JAPDialog.showErrorDialog((JAPDialog)JAPConf.getInstance(), JAPMessages.getString(MSG_ERR_REJECT_IMPOSSIBLE, termsAndConditions.getOperator().getOrganization()));
                    bl2 = true;
                    continue;
                }
                termsAndConditions.setAccepted(bl);
            }
        }
        this.m_tblOperators.setOperators(Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = JAPConfTC.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryList());
        return true;
    }

    protected void onCancelPressed() {
        this.m_tblOperators.setOperators(Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = JAPConfTC.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryList());
    }

    public void update(Observable observable, Object object) {
        this.onUpdateValues();
        this.getRootPanel().revalidate();
    }

    protected void onUpdateValues() {
        this.m_tblOperators.setOperators(Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = JAPConfTC.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryList());
    }

    public boolean handleOperatorAction(ServiceOperator serviceOperator, boolean bl) {
        return bl;
    }

    public void handleSelectLineAction(ServiceOperator serviceOperator) {
        TermsAndConditions termsAndConditions = TermsAndConditions.getTermsAndConditions(serviceOperator);
        if (termsAndConditions == null) {
            return;
        }
        String string = termsAndConditions.getHTMLText(JAPMessages.getLocale());
        this.m_termsPane.setText(string);
    }

    public void handleAcceptAction(ServiceOperator serviceOperator, boolean bl) {
        if (!bl && !JAPController.getInstance().isOperatorOfConnectedMix(serviceOperator)) {
            JAPDialog.showErrorDialog((JAPDialog)JAPConf.getInstance(), JAPMessages.getString(MSG_ERR_REJECT_IMPOSSIBLE, serviceOperator.getOrganization()));
            throw new IllegalStateException(JAPMessages.getString(MSG_ERR_REJECT_IMPOSSIBLE, serviceOperator.getOrganization()));
        }
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

