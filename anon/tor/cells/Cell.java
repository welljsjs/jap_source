/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.cells;

import anon.tor.cells.CreatedCell;
import anon.tor.cells.DestroyCell;
import anon.tor.cells.PaddingCell;
import anon.tor.cells.RelayCell;

public abstract class Cell {
    public static final int CELL_SIZE = 512;
    public static final int CELL_PAYLOAD_SIZE = 509;
    private int m_circID = 0;
    private int m_command;
    protected byte[] m_payload;

    protected Cell(int n) {
        this.m_command = n;
        this.m_payload = new byte[509];
    }

    protected Cell(int n, int n2) {
        this(n);
        this.m_circID = n2;
    }

    protected Cell(int n, int n2, byte[] arrby) {
        this(n, n2);
        this.setPayload(arrby, 0);
    }

    protected Cell(int n, int n2, byte[] arrby, int n3) {
        this(n, n2);
        this.setPayload(arrby, n3);
    }

    public byte[] getCellData() {
        byte[] arrby = new byte[512];
        arrby[0] = (byte)(this.m_circID >> 8 & 0xFF);
        arrby[1] = (byte)(this.m_circID & 0xFF);
        arrby[2] = (byte)(this.m_command & 0xFF);
        System.arraycopy(this.m_payload, 0, arrby, 3, 509);
        return arrby;
    }

    public int getCommand() {
        return this.m_command;
    }

    public int getCircuitID() {
        return this.m_circID;
    }

    public byte[] getPayload() {
        return this.m_payload;
    }

    public void setPayload(byte[] arrby, int n) {
        int n2 = Math.min(509, arrby.length);
        System.arraycopy(arrby, n, this.m_payload, 0, n2);
    }

    public static Cell createCell(byte[] arrby) {
        if (arrby.length != 512) {
            return null;
        }
        Cell cell = null;
        int n = (arrby[0] & 0xFF) << 8 | arrby[1] & 0xFF;
        int n2 = arrby[2] & 0xFF;
        switch (n2) {
            case 2: {
                cell = new CreatedCell(n, arrby, 3);
                break;
            }
            case 3: {
                cell = new RelayCell(n, arrby, 3);
                break;
            }
            case 4: {
                cell = new DestroyCell(n, arrby, 3);
                break;
            }
            case 0: {
                cell = new PaddingCell(n, arrby, 3);
                break;
            }
            default: {
                cell = null;
            }
        }
        return cell;
    }
}

