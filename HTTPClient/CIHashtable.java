/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import java.util.Enumeration;
import java.util.Hashtable;

class CIHashtable
extends Hashtable {
    public CIHashtable(int n, float f) {
        super(n, f);
    }

    public CIHashtable(int n) {
        super(n);
    }

    public CIHashtable() {
    }

    public Object get(String string) {
        return super.get(new CIString(string));
    }

    public Object put(String string, Object object) {
        return super.put(new CIString(string), object);
    }

    public boolean containsKey(String string) {
        return super.contains(new CIString(string));
    }

    public Object remove(String string) {
        return super.remove(new CIString(string));
    }

    public Enumeration keys() {
        return new CIHashtableEnumeration(super.keys());
    }

    private static final class CIString {
        private String string;
        private int hash;
        private static final char[] lc = new char[256];

        public CIString(String string) {
            this.string = string;
            this.hash = CIString.calcHashCode(string);
        }

        public final String getString() {
            return this.string;
        }

        public int hashCode() {
            return this.hash;
        }

        private static final int calcHashCode(String string) {
            int n = 0;
            char[] arrc = lc;
            int n2 = string.length();
            for (int i = 0; i < n2; ++i) {
                n = 31 * n + arrc[string.charAt(i)];
            }
            return n;
        }

        public boolean equals(Object object) {
            if (object != null) {
                if (object instanceof CIString) {
                    return this.string.equalsIgnoreCase(((CIString)object).string);
                }
                if (object instanceof String) {
                    return this.string.equalsIgnoreCase((String)object);
                }
            }
            return false;
        }

        public String toString() {
            return this.string;
        }

        static {
            for (char c = '\u0000'; c < '\u0100'; c = (char)(c + '\u0001')) {
                CIString.lc[c] = Character.toLowerCase(c);
            }
        }
    }

    private static class CIHashtableEnumeration
    implements Enumeration {
        Enumeration HTEnum;

        public CIHashtableEnumeration(Enumeration enumeration) {
            this.HTEnum = enumeration;
        }

        public boolean hasMoreElements() {
            return this.HTEnum.hasMoreElements();
        }

        public Object nextElement() {
            Object e = this.HTEnum.nextElement();
            if (e instanceof CIString) {
                return ((CIString)e).getString();
            }
            return e;
        }
    }
}

