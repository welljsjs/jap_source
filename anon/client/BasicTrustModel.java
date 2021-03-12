/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.ITrustModel;
import anon.crypto.SignatureVerifier;
import anon.error.ServiceSignatureException;
import anon.error.ServiceUnreachableException;
import anon.error.TrustException;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.util.JAPMessages;
import java.util.Date;

public class BasicTrustModel
implements ITrustModel {
    public final void checkTrust(MixCascade mixCascade) throws TrustException, ServiceSignatureException {
        this.checkTrust(mixCascade, false);
    }

    public void checkTrust(MixCascade mixCascade, boolean bl) throws TrustException, ServiceSignatureException {
        int n;
        int n2 = 0;
        ServiceSignatureException serviceSignatureException = null;
        if (mixCascade == null || SignatureVerifier.getInstance().isCheckSignatures() && !mixCascade.isUserDefined() && !mixCascade.isVerified()) {
            throw new ServiceSignatureException(mixCascade, JAPMessages.getString("invalidSignature"), 0);
        }
        if (SignatureVerifier.getInstance().isCheckSignatures()) {
            serviceSignatureException = new ServiceSignatureException(mixCascade, JAPMessages.getString("invalidSignature"), 0);
            if (mixCascade.isValid() && mixCascade.isVerified()) {
                serviceSignatureException = null;
            } else {
                for (n = 0; n < mixCascade.getNumberOfMixes(); ++n) {
                    if (mixCascade.getMixInfo(n) == null || mixCascade.getMixInfo(n).getCertPath() == null || !mixCascade.getMixInfo(n).getCertPath().isValid(new Date()) || n != 0 && n != mixCascade.getNumberOfMixes() - 1) continue;
                    serviceSignatureException = null;
                    break;
                }
            }
            if (serviceSignatureException != null) {
                throw serviceSignatureException;
            }
        }
        if (mixCascade.areListenerInterfacesBlocked()) {
            throw new ServiceUnreachableException(mixCascade);
        }
        for (n = 0; n < mixCascade.getNumberOfMixes(); ++n) {
            MixInfo mixInfo = mixCascade.getMixInfo(n);
            if ((mixInfo != null || !SignatureVerifier.getInstance().isCheckSignatures()) && (mixInfo == null || !SignatureVerifier.getInstance().isCheckSignatures() || mixInfo.isVerified())) continue;
            ++n2;
            if (serviceSignatureException != null) continue;
            serviceSignatureException = new ServiceSignatureException(mixCascade, JAPMessages.getString("invalidSignature") + " (Mix " + (n + 1) + ")", n);
        }
        if (serviceSignatureException != null) {
            if (n2 > 1 || mixCascade.getNumberOfOperatorsShown() == 1 || mixCascade.getNumberOfMixes() <= 1) {
                serviceSignatureException = new ServiceSignatureException(mixCascade, JAPMessages.getString("invalidSignature"));
            }
            throw serviceSignatureException;
        }
    }

    public final boolean isTrusted(MixCascade mixCascade) {
        if (mixCascade == null) {
            return false;
        }
        if (mixCascade != null && mixCascade.isShownAsTrusted()) {
            return true;
        }
        try {
            this.checkTrust(mixCascade);
            return true;
        }
        catch (TrustException trustException) {
            return false;
        }
        catch (ServiceSignatureException serviceSignatureException) {
            return false;
        }
    }

    public final boolean isTrusted(MixCascade mixCascade, StringBuffer stringBuffer) {
        if (mixCascade != null && mixCascade.isShownAsTrusted()) {
            return true;
        }
        try {
            this.checkTrust(mixCascade);
            return true;
        }
        catch (TrustException trustException) {
            stringBuffer.append(trustException.getMessage());
            return false;
        }
        catch (ServiceSignatureException serviceSignatureException) {
            stringBuffer.append(serviceSignatureException.getMessage());
            return false;
        }
    }
}

