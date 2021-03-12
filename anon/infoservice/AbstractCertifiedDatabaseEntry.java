/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.MultiCertPath;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLSignature;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.ICertifiedDatabaseEntry;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractCertifiedDatabaseEntry
extends AbstractDatabaseEntry
implements ICertifiedDatabaseEntry {
    public AbstractCertifiedDatabaseEntry(long l) {
        super(l);
    }

    public abstract MultiCertPath getCertPath();

    public abstract XMLSignature getSignature();

    public abstract boolean isVerified();

    public boolean checkId() {
        if (!SignatureVerifier.getInstance().isCheckSignatures()) {
            return true;
        }
        XMLSignature xMLSignature = this.getSignature();
        if (xMLSignature == null) {
            LogHolder.log(6, LogType.CRYPTO, "Signature is NULL!");
            return false;
        }
        return this.getId() != null && this.getId().equalsIgnoreCase(xMLSignature.getXORofSKIs());
    }

    public abstract /* synthetic */ boolean isValid();
}

