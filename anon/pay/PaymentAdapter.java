/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import anon.error.AccountEmptyException;
import anon.infoservice.MixCascade;
import anon.pay.IPaymentListener;
import anon.pay.PayAccount;
import anon.pay.xml.XMLErrorMessage;
import anon.util.captcha.ICaptchaSender;
import anon.util.captcha.IImageEncodedCaptcha;

public class PaymentAdapter
implements IPaymentListener {
    public void accountActivated(PayAccount payAccount) {
    }

    public void accountAdded(PayAccount payAccount) {
    }

    public void accountCertRequested(MixCascade mixCascade) throws AccountEmptyException {
    }

    public void accountError(XMLErrorMessage xMLErrorMessage, boolean bl) {
    }

    public void accountRemoved(PayAccount payAccount) {
    }

    public void creditChanged(PayAccount payAccount) {
    }

    public void gotCaptcha(ICaptchaSender iCaptchaSender, IImageEncodedCaptcha iImageEncodedCaptcha) {
    }
}

