/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.spi;

import java.io.PrintWriter;
import java.util.Vector;
import org.apache.log4j.spi.NullWriter;

class VectorWriter
extends PrintWriter {
    private Vector v = new Vector();

    VectorWriter() {
        super(new NullWriter());
    }

    public void print(Object object) {
        this.v.addElement(object.toString());
    }

    public void print(char[] arrc) {
        this.v.addElement(new String(arrc));
    }

    public void print(String string) {
        this.v.addElement(string);
    }

    public void println(Object object) {
        this.v.addElement(object.toString());
    }

    public void println(char[] arrc) {
        this.v.addElement(new String(arrc));
    }

    public void println(String string) {
        this.v.addElement(string);
    }

    public void write(char[] arrc) {
        this.v.addElement(new String(arrc));
    }

    public void write(char[] arrc, int n, int n2) {
        this.v.addElement(new String(arrc, n, n2));
    }

    public void write(String string, int n, int n2) {
        this.v.addElement(string.substring(n, n + n2));
    }

    public void write(String string) {
        this.v.addElement(string);
    }

    public String[] toStringArray() {
        int n = this.v.size();
        String[] arrstring = new String[n];
        for (int i = 0; i < n; ++i) {
            arrstring[i] = (String)this.v.elementAt(i);
        }
        return arrstring;
    }
}

