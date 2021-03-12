/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.AuthorizationInfo;
import HTTPClient.NVPair;

public interface AuthorizationPrompter {
    public NVPair getUsernamePassword(AuthorizationInfo var1);
}

