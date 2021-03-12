/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.infoservice.ImmutableProxyInterface;
import anon.util.IPasswordReader;

public class StoredPasswordReader
implements IPasswordReader {
    private String password;

    public StoredPasswordReader(char[] arrc) {
        if (arrc == null) {
            throw new NullPointerException("Stored password must not be null!");
        }
        this.password = new String(arrc);
    }

    public String readPassword(ImmutableProxyInterface immutableProxyInterface) {
        return this.password;
    }
}

