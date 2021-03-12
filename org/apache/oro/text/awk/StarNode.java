/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.SyntaxNode;

class StarNode
extends SyntaxNode {
    SyntaxNode _left;

    StarNode(SyntaxNode syntaxNode) {
        this._left = syntaxNode;
    }

    boolean _nullable() {
        return true;
    }

    BitSet _firstPosition() {
        return this._left._firstPosition();
    }

    BitSet _lastPosition() {
        return this._left._lastPosition();
    }

    void _followPosition(BitSet[] arrbitSet, SyntaxNode[] arrsyntaxNode) {
        this._left._followPosition(arrbitSet, arrsyntaxNode);
        BitSet bitSet = this._lastPosition();
        BitSet bitSet2 = this._firstPosition();
        int n = bitSet.size();
        while (0 < n--) {
            if (!bitSet.get(n)) continue;
            arrbitSet[n].or(bitSet2);
        }
    }

    SyntaxNode _clone(int[] arrn) {
        return new StarNode(this._left._clone(arrn));
    }
}

