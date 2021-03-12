/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.error.AccountEmptyException;
import anon.infoservice.MixCascade;
import anon.pay.PayAccount;
import anon.pay.xml.XMLErrorMessage;
import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;
import java.util.EventListener;

public interface IPaymentListener
extends EventListener {
    public void accountCertRequested(MixCascade var1) throws AccountEmptyException;

    public void accountError(XMLErrorMessage var1, boolean var2);

    public void accountActivated(PayAccount var1);

    public void accountRemoved(PayAccount var1);

    public void accountAdded(PayAccount var1);

    public void creditChanged(PayAccount var1);

    public void gotCaptcha(ICaptchaSender var1, IImageEncodedCaptcha var2);
}

