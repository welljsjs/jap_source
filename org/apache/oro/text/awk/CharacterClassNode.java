/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;
import org.apache.oro.text.awk.LeafNode;
import org.apache.oro.text.awk.SyntaxNode;

class CharacterClassNode
extends LeafNode {
    BitSet _characterSet = new BitSet(257);

    CharacterClassNode(int n) {
        super(n);
    }

    void _addToken(int n) {
        this._characterSet.set(n);
    }

    void _addTokenRange(int n, int n2) {
        while (n <= n2) {
            this._characterSet.set(n++);
        }
    }

    boolean _matches(char c) {
        return this._characterSet.get(c);
    }

    SyntaxNode _clone(int[] arrn) {
        int n = arrn[0];
        arrn[0] = n + 1;
        CharacterClassNode characterClassNode = new CharacterClassNode(n);
        characterClassNode._characterSet = (BitSet)this._characterSet.clone();
        return characterClassNode;
    }
}

