/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion;

import anon.AnonChannel;
import anon.AnonServerDescription;
import anon.AnonService;
import anon.AnonServiceEventListener;
import anon.IServiceContainer;
import anon.infoservice.IMutableProxyInterface;
import anon.mixminion.MixminionSMTPChannel;
import anon.mixminion.MixminionServiceDescription;
import anon.terms.TermsAndConditionConfirmation;
import java.net.ConnectException;

public class Mixminion
implements AnonService {
    private static MixminionServiceDescription m_serviceDescription;
    public static final int MAX_ROUTE_LEN = 10;
    public static final int MIN_ROUTE_LEN = 2;
    private static Mixminion ms_theMixminionInstance;
    private IMutableProxyInterface m_proxyInterface;

    private Mixminion() {
    }

    public void initialize(AnonServerDescription anonServerDescription, IServiceContainer iServiceContainer, TermsAndConditionConfirmation termsAndConditionConfirmation, boolean bl) {
        m_serviceDescription = (MixminionServiceDescription)anonServerDescription;
    }

    public void setRouteLen(int n) {
        if (n >= 2 && n <= 10) {
            m_serviceDescription.setRouteLen(n);
        }
    }

    public static int getRouteLen() {
        return m_serviceDescription.getRouteLen();
    }

    public static String getMyEMail() {
        return m_serviceDescription.getMyEmail();
    }

    public int setProxy(IMutableProxyInterface iMutableProxyInterface) {
        this.m_proxyInterface = iMutableProxyInterface;
        return 0;
    }

    public IMutableProxyInterface getProxy() {
        return this.m_proxyInterface;
    }

    public void shutdown(boolean bl) {
    }

    public boolean isConnected() {
        return false;
    }

    public AnonChannel createChannel(int n) throws ConnectException {
        if (n != 2) {
            return null;
        }
        try {
            return new MixminionSMTPChannel();
        }
        catch (Exception exception) {
            throw new ConnectException("Could not create a Mixminion-Channel: " + exception.getMessage());
        }
    }

    public AnonChannel createChannel(String string, int n) throws ConnectException {
        return null;
    }

    public void addEventListener(AnonServiceEventListener anonServiceEventListener) {
    }

    public void removeEventListener(AnonServiceEventListener anonServiceEventListener) {
    }

    public void removeEventListeners() {
    }

    public static Mixminion getInstance() {
        if (ms_theMixminionInstance == null) {
            ms_theMixminionInstance = new Mixminion();
        }
        return ms_theMixminionInstance;
    }

    static {
        ms_theMixminionInstance = null;
    }
}

