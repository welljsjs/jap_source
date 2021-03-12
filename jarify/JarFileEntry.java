/*
 * Decompiled with CFR 0.150.
 */
package jarify;

import java.io.InputStream;

class JarFileEntry {
    protected String name;
    private InputStream stream;
    private long size;
    private byte[] content = null;

    public JarFileEntry(String string, long l, InputStream inputStream) {
        this.name = string;
        this.size = l;
        this.stream = inputStream;
    }

    public String getName() {
        return this.name;
    }

    public long getSize() {
        return this.size;
    }

    public byte[] getContent() {
        if (this.content == null) {
            this.content = new byte[(int)this.size];
            int n = 0;
            int n2 = 0;
            try {
                while ((long)n != this.size) {
                    n2 = this.stream.read(this.content, n, (int)this.size - n);
                    n += n2;
                }
            }
            catch (Exception exception) {
                return null;
            }
            if ((long)n != this.size) {
                return null;
            }
        }
        return this.content;
    }
}

