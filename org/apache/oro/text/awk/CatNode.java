/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.SyntaxNode;

final class CatNode
extends SyntaxNode {
    SyntaxNode _left;
    SyntaxNode _right;

    CatNode() {
    }

    boolean _nullable() {
        return this._left._nullable() && this._right._nullable();
    }

    BitSet _firstPosition() {
        if (this._left._nullable()) {
            BitSet bitSet = this._left._firstPosition();
            BitSet bitSet2 = this._right._firstPosition();
            BitSet bitSet3 = new BitSet(Math.max(bitSet.size(), bitSet2.size()));
            bitSet3.or(bitSet2);
            bitSet3.or(bitSet);
            return bitSet3;
        }
        return this._left._firstPosition();
    }

    BitSet _lastPosition() {
        if (this._right._nullable()) {
            BitSet bitSet = this._left._lastPosition();
            BitSet bitSet2 = this._right._lastPosition();
            BitSet bitSet3 = new BitSet(Math.max(bitSet.size(), bitSet2.size()));
            bitSet3.or(bitSet2);
            bitSet3.or(bitSet);
            return bitSet3;
        }
        return this._right._lastPosition();
    }

    void _followPosition(BitSet[] arrbitSet, SyntaxNode[] arrsyntaxNode) {
        this._left._followPosition(arrbitSet, arrsyntaxNode);
        this._right._followPosition(arrbitSet, arrsyntaxNode);
        BitSet bitSet = this._left._lastPosition();
        BitSet bitSet2 = this._right._firstPosition();
        int n = bitSet.size();
        while (0 < n--) {
            if (!bitSet.get(n)) continue;
            arrbitSet[n].or(bitSet2);
        }
    }

    SyntaxNode _clone(int[] arrn) {
        CatNode catNode = new CatNode();
        catNode._left = this._left._clone(arrn);
        catNode._right = this._right._clone(arrn);
        return catNode;
    }
}

