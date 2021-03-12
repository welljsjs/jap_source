/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.MultiCertPath;
import anon.crypto.XMLSignature;

public interface ICertifiedDatabaseEntry {
    public MultiCertPath getCertPath();

    public XMLSignature getSignature();

    public boolean isVerified();

    public boolean isValid();

    public boolean checkId();
}

