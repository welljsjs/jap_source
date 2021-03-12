/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.LeafNode;
import org.apache.oro.text.awk.SyntaxNode;

final class SyntaxTree {
    int _positions;
    SyntaxNode _root;
    LeafNode[] _nodes;
    BitSet[] _followSet;

    SyntaxTree(SyntaxNode syntaxNode, int n) {
        this._root = syntaxNode;
        this._positions = n;
    }

    void _computeFollowPositions() {
        this._followSet = new BitSet[this._positions];
        this._nodes = new LeafNode[this._positions];
        int n = this._positions;
        while (0 < n--) {
            this._followSet[n] = new BitSet(this._positions);
        }
        this._root._followPosition(this._followSet, this._nodes);
    }

    private void __addToFastMap(BitSet bitSet, boolean[] arrbl, boolean[] arrbl2) {
        for (int i = 0; i < this._positions; ++i) {
            if (!bitSet.get(i) || arrbl2[i]) continue;
            arrbl2[i] = true;
            for (int j = 0; j < 256; ++j) {
                if (arrbl[j]) continue;
                arrbl[j] = this._nodes[i]._matches((char)j);
            }
        }
    }

    boolean[] createFastMap() {
        boolean[] arrbl = new boolean[256];
        boolean[] arrbl2 = new boolean[this._positions];
        this.__addToFastMap(this._root._firstPosition(), arrbl, arrbl2);
        return arrbl;
    }
}

