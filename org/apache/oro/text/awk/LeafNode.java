/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.SyntaxNode;

abstract class LeafNode
extends SyntaxNode {
    static final int _NUM_TOKENS = 256;
    static final int _END_MARKER_TOKEN = 256;
    protected int _position;
    protected BitSet _positionSet;

    LeafNode(int n) {
        this._position = n;
        this._positionSet = new BitSet(n + 1);
        this._positionSet.set(n);
    }

    abstract boolean _matches(char var1);

    final boolean _nullable() {
        return false;
    }

    final BitSet _firstPosition() {
        return this._positionSet;
    }

    final BitSet _lastPosition() {
        return this._positionSet;
    }

    final void _followPosition(BitSet[] arrbitSet, SyntaxNode[] arrsyntaxNode) {
        arrsyntaxNode[this._position] = this;
    }
}

