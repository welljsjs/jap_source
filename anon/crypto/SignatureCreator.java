/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.PKCS12;
import anon.crypto.XMLSignature;
import java.util.Hashtable;
import org.w3c.dom.Node;

public class SignatureCreator {
    private static SignatureCreator ms_scInstance;
    private Hashtable m_signatureKeys = new Hashtable();
    static /* synthetic */ Class class$anon$crypto$SignatureCreator;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static SignatureCreator getInstance() {
        Class class_ = class$anon$crypto$SignatureCreator == null ? (class$anon$crypto$SignatureCreator = SignatureCreator.class$("anon.crypto.SignatureCreator")) : class$anon$crypto$SignatureCreator;
        synchronized (class_) {
            if (ms_scInstance == null) {
                ms_scInstance = new SignatureCreator();
            }
        }
        return ms_scInstance;
    }

    private SignatureCreator() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setSigningKey(int n, PKCS12 pKCS12) {
        Hashtable hashtable = this.m_signatureKeys;
        synchronized (hashtable) {
            this.m_signatureKeys.put(new Integer(n), pKCS12);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XMLSignature getSignedXml(int n, Node node) {
        PKCS12 pKCS12 = null;
        XMLSignature xMLSignature = null;
        Hashtable hashtable = this.m_signatureKeys;
        synchronized (hashtable) {
            pKCS12 = (PKCS12)this.m_signatureKeys.get(new Integer(n));
        }
        if (pKCS12 != null) {
            try {
                xMLSignature = XMLSignature.sign(node, pKCS12, n);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return xMLSignature;
    }

    public boolean signXml(int n, Node node) {
        return this.getSignedXml(n, node) != null;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

