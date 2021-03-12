/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.NotRecoverableException;
import anon.infoservice.MixCascade;

public class UnknownProtocolVersionException
extends NotRecoverableException {
    private static final long serialVersionUID = 1L;
    private String m_version;
    private String m_protocol;

    public UnknownProtocolVersionException(MixCascade mixCascade, String string, String string2, int n) {
        super(mixCascade, "The version '" + string2 + "' of the protocol '" + string + "' is not supported by this client!", -10, n);
        this.m_version = string2;
        this.m_protocol = string2;
    }

    public String getVersion() {
        return this.m_version;
    }

    public String getProtocol() {
        return this.m_protocol;
    }
}

