/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.ITrustModel;
import anon.client.TrustModel;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.AbstractMixCascadeContainer;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.infoservice.StatusInfo;
import anon.pay.PayAccountsFile;
import anon.util.JAPMessages;
import anon.util.Util;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractAutoSwitchedMixCascadeContainer
extends AbstractMixCascadeContainer {
    public static final MixCascade INITIAL_DUMMY_SERVICE;
    private static final long MINIMUM_NEXT_WAITING_TIME = 250L;
    private long m_lLastNextRequest = 0L;
    private Hashtable m_alreadyTriedCascades;
    private Random m_random;
    private MixCascade m_initialCascade;
    private MixCascade m_currentCascade;
    private boolean m_bKeepCurrentCascade;
    private boolean m_bSkipInitialCascade;
    private String m_strStartupServiceId;
    private boolean m_bInitialRun;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute;

    public AbstractAutoSwitchedMixCascadeContainer(boolean bl, MixCascade mixCascade, String string) {
        if (mixCascade == null) {
            if (INITIAL_DUMMY_SERVICE == null) {
                throw new NullPointerException("Initial cascade is null!");
            }
            mixCascade = INITIAL_DUMMY_SERVICE;
            mixCascade.showAsTrusted(true);
        }
        this.m_bInitialRun = mixCascade == INITIAL_DUMMY_SERVICE;
        this.m_strStartupServiceId = string;
        this.m_bSkipInitialCascade = bl;
        this.m_alreadyTriedCascades = new Hashtable();
        this.m_random = new Random(System.currentTimeMillis());
        this.m_random.nextInt();
        this.m_currentCascade = this.m_initialCascade = mixCascade;
        this.m_bKeepCurrentCascade = false;
    }

    public final MixCascade getInitialCascade() {
        return this.m_initialCascade;
    }

    public void reset() {
        this.m_alreadyTriedCascades.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MixCascade getNextRandomCascade() {
        Hashtable hashtable = this.m_alreadyTriedCascades;
        synchronized (hashtable) {
            this.reset();
            return this.getNextCascade(true, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final MixCascade getNextCascade() {
        Hashtable hashtable = this.m_alreadyTriedCascades;
        synchronized (hashtable) {
            try {
                Thread.sleep(Math.max(0L, this.m_lLastNextRequest + 250L - System.currentTimeMillis()));
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            this.m_lLastNextRequest = System.currentTimeMillis();
            return this.getNextCascade(false, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final MixCascade getNextCascade(boolean bl, boolean bl2) {
        Hashtable hashtable = this.m_alreadyTriedCascades;
        synchronized (hashtable) {
            if (!TrustModel.getCurrentTrustModel().hasTrustedCascades()) {
                this.m_alreadyTriedCascades.put(this.m_currentCascade.getId(), this.m_currentCascade);
            } else if (!(this.isServiceAutoSwitched() || bl || this.m_strStartupServiceId != null && this.m_strStartupServiceId.equals(INITIAL_DUMMY_SERVICE.getId()) || this.m_currentCascade == INITIAL_DUMMY_SERVICE && !this.isSuitableCascade((MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AbstractAutoSwitchedMixCascadeContainer.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.m_strStartupServiceId)))) {
                this.reset();
                this.m_bKeepCurrentCascade = false;
                if (this.m_initialCascade == INITIAL_DUMMY_SERVICE) {
                    if (this.m_strStartupServiceId == null && this.m_currentCascade != INITIAL_DUMMY_SERVICE) {
                        this.m_initialCascade = this.m_currentCascade;
                    } else {
                        MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AbstractAutoSwitchedMixCascadeContainer.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.m_strStartupServiceId);
                        if (mixCascade != null) {
                            this.m_initialCascade = mixCascade;
                        }
                    }
                }
                if (this.m_currentCascade == INITIAL_DUMMY_SERVICE) {
                    this.update(this.m_initialCascade, false);
                }
            } else if (this.m_bKeepCurrentCascade && !bl && TrustModel.getCurrentTrustModel().isTrusted(this.m_currentCascade) && this.m_currentCascade != INITIAL_DUMMY_SERVICE) {
                this.m_bKeepCurrentCascade = false;
                this.m_alreadyTriedCascades.put(this.m_currentCascade.getId(), this.m_currentCascade);
            } else {
                int n;
                MixCascade mixCascade = null;
                boolean bl3 = true;
                Vector vector = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AbstractAutoSwitchedMixCascadeContainer.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList();
                if (vector.size() > 0) {
                    int n2;
                    n = this.m_random.nextInt();
                    if (n < 0) {
                        n *= -1;
                        bl3 = false;
                    }
                    n %= vector.size();
                    Vector<MixCascade> vector2 = new Vector<MixCascade>();
                    for (n2 = 0; n2 < vector.size(); ++n2) {
                        mixCascade = (MixCascade)vector.elementAt(n);
                        if (this.m_alreadyTriedCascades.containsKey(mixCascade.getId())) {
                            mixCascade = null;
                        } else if (!this.isSuitableCascade(mixCascade)) {
                            this.m_alreadyTriedCascades.put(mixCascade.getId(), mixCascade);
                            mixCascade = null;
                        } else {
                            AbstractDatabaseEntry abstractDatabaseEntry;
                            if (!bl2 && mixCascade.getNumberOfOperators() <= 1) {
                                Hashtable hashtable2 = Database.getInstance(class$anon$infoservice$MixCascade == null ? AbstractAutoSwitchedMixCascadeContainer.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getEntryHash();
                                Enumeration<Object> enumeration = this.m_alreadyTriedCascades.keys();
                                while (enumeration.hasMoreElements()) {
                                    hashtable2.remove(enumeration.nextElement());
                                }
                                enumeration = hashtable2.elements();
                                while (enumeration.hasMoreElements()) {
                                    abstractDatabaseEntry = (MixCascade)enumeration.nextElement();
                                    if (((MixCascade)abstractDatabaseEntry).getNumberOfOperators() <= 1 || !this.isSuitableCascade((MixCascade)abstractDatabaseEntry)) continue;
                                    mixCascade = null;
                                    break;
                                }
                            }
                            if (mixCascade != null) {
                                abstractDatabaseEntry = StatusInfo.getStatusInfo(mixCascade);
                                double d = Math.max(0.05, Math.pow(mixCascade.getDistribution(), 2.0) / Math.pow(6.0, 2.0));
                                if (this.m_random.nextDouble() <= d) break;
                                if (!vector2.contains(mixCascade)) {
                                    vector2.addElement(mixCascade);
                                }
                                mixCascade = null;
                            }
                        }
                        if (bl3) {
                            n = (n + 1) % vector.size();
                            continue;
                        }
                        if (--n >= 0) continue;
                        n = vector.size() - 1;
                    }
                    if (mixCascade == null && vector2.size() > 0) {
                        for (n2 = 0; n2 < vector2.size(); ++n2) {
                            if (mixCascade != null && mixCascade.getDistribution() >= ((MixCascade)vector2.elementAt(n2)).getDistribution() || !this.isSuitableCascade((MixCascade)vector2.elementAt(n2))) continue;
                            mixCascade = (MixCascade)vector2.elementAt(n2);
                        }
                    }
                }
                if (mixCascade == null && !bl2) {
                    return this.getNextCascade(bl, true);
                }
                if (mixCascade == null) {
                    this.reset();
                    vector = Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = AbstractAutoSwitchedMixCascadeContainer.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryList(true);
                    for (n = 0; n < vector.size(); ++n) {
                        if (!this.isSuitableCascade((MixCascade)vector.elementAt(n))) continue;
                        mixCascade = (MixCascade)vector.elementAt(n);
                        LogHolder.log(4, LogType.MISC, "Tried all services, but did not find any suitable... Now using random service: " + mixCascade);
                        break;
                    }
                    if (mixCascade == null && (mixCascade = this.m_currentCascade) != INITIAL_DUMMY_SERVICE) {
                        LogHolder.log(4, LogType.MISC, "Tried all services, but did not find any suitable... Now re-using current service: " + mixCascade);
                    }
                }
                this.m_alreadyTriedCascades.put(mixCascade.getId(), mixCascade);
                this.update(mixCascade, false);
            }
            if (this.m_bSkipInitialCascade || this.m_initialCascade == INITIAL_DUMMY_SERVICE) {
                this.m_initialCascade = this.m_currentCascade;
            }
            this.m_bSkipInitialCascade = false;
        }
        return this.m_currentCascade;
    }

    public abstract boolean isServiceAutoSwitched();

    public abstract boolean isReconnectedAutomatically();

    public abstract boolean hasUserAllowedPaidServices(String var1);

    public final String getStartupServiceId() {
        return this.m_strStartupServiceId;
    }

    private final boolean isSuitableCascade(MixCascade mixCascade) {
        if (mixCascade == null) {
            return false;
        }
        TrustModel trustModel = TrustModel.getCurrentTrustModel();
        if (!trustModel.isPaymentForced() && (mixCascade.isPayment() ? PayAccountsFile.getInstance().getChargedAccount(mixCascade.getPIID()) == null && (!this.hasUserAllowedPaidServices(mixCascade.getPIID()) || PayAccountsFile.getInstance().getAccountWaitingForTransaction(mixCascade.getPIID()) != null) : !PayAccountsFile.getInstance().isNewUser() && trustModel.getAttribute(class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute == null ? (class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute = AbstractAutoSwitchedMixCascadeContainer.class$("anon.client.TrustModel$ForcePremiumIfChargedAccountAttribute")) : class$anon$client$TrustModel$ForcePremiumIfChargedAccountAttribute).getTrustCondition() == 2 && this.hasUserAllowedPaidServices(mixCascade.getPIID()) && PayAccountsFile.getInstance().getAccountWaitingForTransaction(mixCascade.getPIID()) == null && trustModel.hasPremiumCascades())) {
            return false;
        }
        if (this.m_initialCascade != null && this.m_bSkipInitialCascade && mixCascade.equals(this.m_initialCascade)) {
            return false;
        }
        return trustModel.isTrusted(mixCascade);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void update(MixCascade mixCascade, boolean bl) {
        this.m_bInitialRun = false;
        Hashtable hashtable = this.m_alreadyTriedCascades;
        synchronized (hashtable) {
            this.m_bKeepCurrentCascade = true;
            if (this.m_currentCascade == null && mixCascade != null || this.m_currentCascade != null && mixCascade == null || !this.m_currentCascade.equals(mixCascade) || !this.getTrustModel().isTrusted(this.m_currentCascade)) {
                this.setChanged();
            }
            this.m_currentCascade = mixCascade;
            if (this.m_initialCascade == null) {
                this.m_initialCascade = this.m_currentCascade;
            }
            if (bl) {
                this.m_bInitialRun = true;
            }
            this.notifyObservers(this.m_currentCascade);
        }
    }

    public final MixCascade getCurrentCascade() {
        return this.m_currentCascade;
    }

    public final boolean setCurrentCascade(MixCascade mixCascade) {
        if (!this.getTrustModel().isTrusted(mixCascade)) {
            return false;
        }
        this.update(mixCascade, true);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void keepCurrentService(boolean bl) {
        Hashtable hashtable = this.m_alreadyTriedCascades;
        synchronized (hashtable) {
            this.m_bKeepCurrentCascade = bl;
        }
    }

    public final ITrustModel getTrustModel() {
        return TrustModel.getCurrentTrustModel();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        MixCascade mixCascade;
        try {
            mixCascade = new MixCascade("-", "-", "0.0.0.0", 6544, System.currentTimeMillis()){

                public String getName() {
                    return JAPMessages.getString("noCascadesAvail");
                }

                public Vector getDecomposedCascadeName() {
                    return Util.toVector(this.getName());
                }
            };
            mixCascade.showAsTrusted(true);
            mixCascade.setUserDefined(false, null);
        }
        catch (Exception exception) {
            mixCascade = null;
        }
        INITIAL_DUMMY_SERVICE = mixCascade;
    }
}

