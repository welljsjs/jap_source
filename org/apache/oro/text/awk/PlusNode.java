/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import org.apache.oro.text.awk.StarNode;
import org.apache.oro.text.awk.SyntaxNode;

final class PlusNode
extends StarNode {
    PlusNode(SyntaxNode syntaxNode) {
        super(syntaxNode);
    }

    boolean _nullable() {
        return false;
    }

    SyntaxNode _clone(int[] arrn) {
        return new PlusNode(this._left._clone(arrn));
    }
}

