/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.cells;

import anon.tor.cells.Cell;

public class CreateCell
extends Cell {
    public CreateCell() {
        super(1);
    }

    public CreateCell(int n) {
        super(1, n);
    }

    public CreateCell(int n, byte[] arrby) {
        super(1, n, arrby);
    }
}

