/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.TrustException;
import anon.infoservice.MixCascade;
import anon.util.JAPMessages;

public class ServiceUnreachableException
extends TrustException {
    private static final String MSG_NETWORK_BLOCKED = (class$anon$error$ServiceUnreachableException == null ? (class$anon$error$ServiceUnreachableException = ServiceUnreachableException.class$("anon.error.ServiceUnreachableException")) : class$anon$error$ServiceUnreachableException).getName() + ".networkBlocked";
    private static final long serialVersionUID = 1L;
    static /* synthetic */ Class class$anon$error$ServiceUnreachableException;

    public ServiceUnreachableException(MixCascade mixCascade) {
        super(mixCascade, JAPMessages.getString(MSG_NETWORK_BLOCKED), -9);
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

