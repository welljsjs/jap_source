/*
 * Decompiled with CFR 0.150.
 */
package anon.util.captcha;

import anon.util.captcha.MyImage;

public interface IImageEncodedCaptcha {
    public MyImage getImage();

    public String getCharacterSet();

    public int getCharacterNumber();

    public byte[] solveCaptcha(String var1, byte[] var2) throws Exception;
}

