/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public final class MyStringBuilder {
    private char[] value;
    private int aktPos;
    private int capacity;

    public MyStringBuilder(int n) {
        this.value = new char[n];
        this.aktPos = 0;
        this.capacity = n;
    }

    public void append(String string) {
        int n = string.length();
        if (string.length() > this.capacity) {
            this.capacity = n + this.value.length + 512;
            char[] arrc = new char[this.capacity];
            System.arraycopy(this.value, 0, arrc, 0, this.aktPos);
            this.value = arrc;
            this.capacity -= this.aktPos;
        }
        string.getChars(0, n, this.value, this.aktPos);
        this.aktPos += n;
        this.capacity -= n;
    }

    public void append(int n) {
        this.append(Integer.toString(n));
    }

    public void append(long l) {
        this.append(Long.toString(l));
    }

    public void setLength(int n) {
        this.aktPos = n;
    }

    public String toString() {
        return new String(this.value, 0, this.aktPos);
    }
}

