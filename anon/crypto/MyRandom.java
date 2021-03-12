/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import java.security.SecureRandom;
import java.util.Random;

public final class MyRandom {
    Random m_TheRandom;

    public MyRandom(Random random) {
        this.m_TheRandom = random;
    }

    public MyRandom() {
        this(new SecureRandom());
    }

    public int nextInt(int n) {
        int n2;
        int n3 = Integer.MAX_VALUE;
        n3 -= n3 % n;
        do {
            n2 = this.m_TheRandom.nextInt();
        } while ((n2 &= Integer.MAX_VALUE) >= n3);
        return n2 % n;
    }

    public Random getRandSource() {
        return this.m_TheRandom;
    }

    public void setSeed(long l) {
        this.m_TheRandom.setSeed(l);
    }

    public void setSeed(long l, boolean bl) {
        if (bl) {
            if (this.m_TheRandom instanceof SecureRandom) {
                this.m_TheRandom = new Random(l);
            } else {
                this.m_TheRandom.setSeed(l);
            }
        } else if (this.m_TheRandom instanceof SecureRandom) {
            this.m_TheRandom.setSeed(l);
        } else {
            this.m_TheRandom = new SecureRandom();
            this.m_TheRandom.setSeed(l);
        }
    }

    public long nextLong() {
        return this.m_TheRandom.nextLong();
    }
}

