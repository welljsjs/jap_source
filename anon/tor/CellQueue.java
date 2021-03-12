/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.tor.cells.Cell;

final class CellQueue {
    private CellQueueEntry m_firstEntry = null;
    private CellQueueEntry m_lastEntry = null;
    private int m_iSize = 0;

    public synchronized void addElement(Cell cell) {
        CellQueueEntry cellQueueEntry = new CellQueueEntry(cell);
        if (this.m_lastEntry == null) {
            this.m_firstEntry = this.m_lastEntry = cellQueueEntry;
        } else {
            this.m_lastEntry.m_next = cellQueueEntry;
            this.m_lastEntry = cellQueueEntry;
        }
        ++this.m_iSize;
    }

    public synchronized Cell removeElement() {
        if (this.m_firstEntry == null) {
            return null;
        }
        Cell cell = this.m_firstEntry.m_Cell;
        this.m_firstEntry = this.m_firstEntry.m_next;
        if (this.m_firstEntry == null) {
            this.m_lastEntry = null;
        }
        --this.m_iSize;
        return cell;
    }

    public synchronized int size() {
        return this.m_iSize;
    }

    public synchronized boolean isEmpty() {
        return this.m_firstEntry == null;
    }

    final class CellQueueEntry {
        Cell m_Cell;
        CellQueueEntry m_next;

        CellQueueEntry(Cell cell) {
            this.m_Cell = cell;
            this.m_next = null;
        }
    }
}

