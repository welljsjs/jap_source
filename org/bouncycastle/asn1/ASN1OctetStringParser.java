/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.asn1;

import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.InMemoryRepresentable;

public interface ASN1OctetStringParser
extends ASN1Encodable,
InMemoryRepresentable {
    public InputStream getOctetStream();
}

