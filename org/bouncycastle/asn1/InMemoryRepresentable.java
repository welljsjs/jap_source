/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;

public interface InMemoryRepresentable {
    public ASN1Primitive getLoadedObject() throws IOException;
}

