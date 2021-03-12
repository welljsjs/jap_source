/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.util.BitSet;

final class DFAState {
    int _stateNumber;
    BitSet _state;

    DFAState(BitSet bitSet, int n) {
        this._state = bitSet;
        this._stateNumber = n;
    }
}

