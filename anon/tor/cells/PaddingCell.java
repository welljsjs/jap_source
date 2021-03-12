/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.cells;

import anon.tor.cells.Cell;

public class PaddingCell
extends Cell {
    public PaddingCell() {
        super(0);
    }

    public PaddingCell(int n) {
        super(0, n);
    }

    public PaddingCell(int n, byte[] arrby, int n2) {
        super(0, n, arrby, n2);
    }
}

