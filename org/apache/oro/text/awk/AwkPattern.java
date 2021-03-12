/*
 * Decompiled with CFR 0.150.
 */
package org.apache.oro.text.awk;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.oro.text.awk.DFAState;
import org.apache.oro.text.awk.LeafNode;
import org.apache.oro.text.awk.SyntaxTree;
import org.apache.oro.text.regex.Pattern;

public final class AwkPattern
implements Pattern,
Serializable {
    static final int _INVALID_STATE = -1;
    static final int _START_STATE = 1;
    int _numStates;
    int _endPosition;
    int _options;
    String _expression;
    Vector _Dtrans;
    Vector[] _nodeList;
    Vector _stateList;
    BitSet _U;
    BitSet _emptySet;
    BitSet[] _followSet;
    BitSet _endStates;
    Hashtable _stateMap;
    boolean _matchesNullString;
    boolean[] _fastMap;
    boolean _hasBeginAnchor = false;
    boolean _hasEndAnchor = false;

    AwkPattern(String string, SyntaxTree syntaxTree) {
        this._expression = string;
        this._endPosition = syntaxTree._positions - 1;
        this._followSet = syntaxTree._followSet;
        this._Dtrans = new Vector();
        this._stateList = new Vector();
        this._endStates = new BitSet();
        this._U = new BitSet(syntaxTree._positions);
        this._U.or(syntaxTree._root._firstPosition());
        int[] arrn = new int[256];
        this._Dtrans.addElement(arrn);
        this._Dtrans.addElement(arrn);
        this._numStates = 1;
        if (this._U.get(this._endPosition)) {
            this._endStates.set(this._numStates);
        }
        DFAState dFAState = new DFAState((BitSet)this._U.clone(), this._numStates);
        this._stateMap = new Hashtable();
        this._stateMap.put(dFAState._state, dFAState);
        this._stateList.addElement(dFAState);
        this._stateList.addElement(dFAState);
        ++this._numStates;
        this._U.xor(this._U);
        this._emptySet = new BitSet(syntaxTree._positions);
        this._nodeList = new Vector[256];
        for (int i = 0; i < 256; ++i) {
            this._nodeList[i] = new Vector();
            for (int j = 0; j < syntaxTree._positions; ++j) {
                if (!syntaxTree._nodes[j]._matches((char)i)) continue;
                this._nodeList[i].addElement(syntaxTree._nodes[j]);
            }
        }
        this._fastMap = syntaxTree.createFastMap();
        this._matchesNullString = this._endStates.get(1);
    }

    void _createNewState(int n, int n2, int[] arrn) {
        DFAState dFAState = (DFAState)this._stateList.elementAt(n);
        int n3 = this._nodeList[n2].size();
        this._U.xor(this._U);
        while (n3-- > 0) {
            int n4 = ((LeafNode)this._nodeList[n2].elementAt((int)n3))._position;
            if (!dFAState._state.get(n4)) continue;
            this._U.or(this._followSet[n4]);
        }
        if (!this._stateMap.containsKey(this._U)) {
            DFAState dFAState2 = new DFAState((BitSet)this._U.clone(), this._numStates++);
            this._stateList.addElement(dFAState2);
            this._stateMap.put(dFAState2._state, dFAState2);
            this._Dtrans.addElement(new int[256]);
            if (!this._U.equals(this._emptySet)) {
                arrn[n2] = this._numStates - 1;
                if (this._U.get(this._endPosition)) {
                    this._endStates.set(this._numStates - 1);
                }
            } else {
                arrn[n2] = -1;
            }
        } else {
            arrn[n2] = this._U.equals(this._emptySet) ? -1 : ((DFAState)this._stateMap.get((Object)this._U))._stateNumber;
        }
    }

    int[] _getStateArray(int n) {
        return (int[])this._Dtrans.elementAt(n);
    }

    public String getPattern() {
        return this._expression;
    }

    public int getOptions() {
        return this._options;
    }
}

