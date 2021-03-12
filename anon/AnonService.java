/*
 * Decompiled with CFR 0.150.
 */
package anon;

import anon.AnonChannel;
import anon.AnonServerDescription;
import anon.AnonServiceEventListener;
import anon.IServiceContainer;
import anon.error.AnonServiceException;
import anon.infoservice.IMutableProxyInterface;
import anon.terms.TermsAndConditionConfirmation;
import java.net.ConnectException;

public interface AnonService {
    public static final String ANONLIB_VERSION = "00.20.001";

    public void initialize(AnonServerDescription var1, IServiceContainer var2, TermsAndConditionConfirmation var3, boolean var4) throws AnonServiceException;

    public int setProxy(IMutableProxyInterface var1);

    public void shutdown(boolean var1);

    public boolean isConnected();

    public AnonChannel createChannel(int var1) throws ConnectException;

    public void addEventListener(AnonServiceEventListener var1);

    public void removeEventListener(AnonServiceEventListener var1);

    public void removeEventListeners();
}

