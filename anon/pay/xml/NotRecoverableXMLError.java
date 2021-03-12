/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.AnonServerDescription;
import anon.error.INotRecoverableException;
import anon.pay.PayAccount;
import anon.pay.xml.XMLErrorMessage;

public class NotRecoverableXMLError
extends XMLErrorMessage
implements INotRecoverableException {
    private XMLErrorMessage m_source;

    public NotRecoverableXMLError(int n, String string, PayAccount payAccount, AnonServerDescription anonServerDescription) {
        super(n, string, payAccount, anonServerDescription);
        this.m_source = this;
    }

    public NotRecoverableXMLError(XMLErrorMessage xMLErrorMessage) {
        super(xMLErrorMessage.getXmlErrorCode(), xMLErrorMessage.getMessage(), xMLErrorMessage.getAccount(), xMLErrorMessage.getService());
        this.m_source = xMLErrorMessage;
    }

    public XMLErrorMessage getSource() {
        return this.m_source;
    }
}

