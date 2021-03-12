/*
 * Decompiled with CFR 0.150.
 */
package org.bouncycastle.math.field;

import org.bouncycastle.math.field.FiniteField;

public interface ExtensionField
extends FiniteField {
    public FiniteField getSubfield();

    public int getDegree();
}

