/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice.update;

import anon.pay.PayAccount;
import anon.pay.PayAccountsFile;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLBalance;
import anon.util.Updater;
import java.util.Enumeration;
import logging.LogHolder;
import logging.LogType;

public class AccountUpdater
extends Updater {
    private boolean m_successfulUpdate = false;
    private long m_lLastUpdate = Long.MAX_VALUE;
    private boolean m_bInternalCalculation;
    static /* synthetic */ Class class$anon$pay$xml$XMLAccountInfo;

    public AccountUpdater(final boolean bl) {
        super(new Updater.IUpdateInterval(){

            public long getUpdateInterval() {
                return 900000L;
            }
        }, new Updater.ObservableInfo(PayAccountsFile.getInstance()){

            public Integer getUpdateChanged() {
                return PayAccountsFile.CHANGED_AUTO_UPDATE;
            }

            public boolean isUpdateDisabled() {
                if (bl) {
                    return PayAccountsFile.getInstance().isBalanceAutoUpdateEnabled();
                }
                return !PayAccountsFile.getInstance().isBalanceAutoUpdateEnabled();
            }

            public boolean updateImmediately() {
                return true;
            }
        });
        this.m_bInternalCalculation = bl;
    }

    public Class getUpdatedClass() {
        return class$anon$pay$xml$XMLAccountInfo == null ? (class$anon$pay$xml$XMLAccountInfo = AccountUpdater.class$("anon.pay.xml.XMLAccountInfo")) : class$anon$pay$xml$XMLAccountInfo;
    }

    protected void updateInternal() {
        this.m_successfulUpdate = false;
        if (Thread.currentThread().isInterrupted()) {
            this.m_successfulUpdate = true;
            return;
        }
        Enumeration enumeration = PayAccountsFile.getInstance().getAccounts();
        boolean bl = false;
        while (enumeration.hasMoreElements() && !Thread.currentThread().isInterrupted()) {
            PayAccount payAccount = (PayAccount)enumeration.nextElement();
            try {
                XMLAccountInfo xMLAccountInfo;
                if (!this.m_bInternalCalculation && payAccount.shouldUpdateAccountInfo()) {
                    bl = true;
                    LogHolder.log(7, LogType.PAY, "Fetching statement for account: " + payAccount.getAccountNumber());
                    xMLAccountInfo = payAccount.fetchAccountInfo(false);
                    if (xMLAccountInfo == null) continue;
                    this.m_successfulUpdate = true;
                    this.m_lLastUpdate = System.currentTimeMillis();
                    continue;
                }
                if (!this.m_bInternalCalculation || (xMLAccountInfo = payAccount.getAccountInfo()) == null) continue;
                XMLBalance xMLBalance = xMLAccountInfo.getBalance();
                if (!(payAccount.hasExpired() || xMLBalance == null || payAccount.getCurrentSpent() >= 0L && (xMLBalance.getVolumeBytesMonthly() <= 0L || xMLBalance.getOverusageDate() == null && xMLAccountInfo.getLastBalanceUpdateLocalTime().getTime() <= System.currentTimeMillis()))) {
                    xMLAccountInfo = payAccount.fetchAccountInfo(true);
                    if (xMLAccountInfo == null) continue;
                    this.m_successfulUpdate = true;
                    this.m_lLastUpdate = System.currentTimeMillis();
                    continue;
                }
                xMLAccountInfo.checkMonthlyBytesUpdatedOn();
                this.m_successfulUpdate = true;
                this.m_lLastUpdate = System.currentTimeMillis();
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.PAY, "Could not fetch statement for account: " + payAccount.getAccountNumber(), exception);
                if (payAccount.getAccountInfo() == null) continue;
                payAccount.getAccountInfo().checkMonthlyBytesUpdatedOn();
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            this.m_successfulUpdate = true;
            return;
        }
        if (PayAccountsFile.getInstance().getNumAccounts() == 0) {
            this.m_successfulUpdate = true;
        } else if (!this.m_bInternalCalculation && !bl) {
            this.m_successfulUpdate = true;
        }
    }

    public final long getLastUpdate() {
        return this.m_lLastUpdate;
    }

    protected boolean wasUpdateSuccessful() {
        return this.m_successfulUpdate;
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

