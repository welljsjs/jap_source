/*
 * Decompiled with CFR 0.150.
 */
package anon.util.captcha;

public class MyImage {
    private int[] m_arPixels = null;
    private int m_Width = -1;
    private int m_Height = -1;

    public MyImage(int[] arrn, int n, int n2) {
        this.m_arPixels = arrn;
        this.m_Width = n;
        this.m_Height = n2;
    }

    public int getWidth() {
        return this.m_Width;
    }

    public int getHeight() {
        return this.m_Height;
    }

    public int[] getPixels() {
        return this.m_arPixels;
    }
}

