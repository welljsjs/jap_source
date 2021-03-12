/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;

public interface IBIConnectionListener {
    public void gotCaptcha(ICaptchaSender var1, IImageEncodedCaptcha var2);
}

