/*
 * Decompiled with CFR 0.150.
 */
package anon.pay;

import java.net.MalformedURLException;
import java.net.URL;
import logging.LogHolder;
import logging.LogType;

public class PayMessage {
    private String m_shortMessage = null;
    private String m_messageText = null;
    private URL m_messageLink = null;

    public PayMessage(String string) {
        this.m_shortMessage = string;
    }

    public PayMessage(String string, String string2, URL uRL) {
        this.m_shortMessage = string;
        this.m_messageText = string2;
        this.m_messageLink = uRL;
    }

    public boolean equals(Object object) {
        if (!(object instanceof PayMessage)) {
            return false;
        }
        PayMessage payMessage = (PayMessage)object;
        boolean bl = this.m_shortMessage.equals(payMessage.getShortMessage());
        boolean bl2 = this.m_messageText.equals(payMessage.getMessageText());
        String string = this.m_messageLink.toString();
        String string2 = payMessage.toString();
        boolean bl3 = string.equalsIgnoreCase(string2);
        return bl && bl2 && bl3;
    }

    public void setShortMessage(String string) {
        this.m_shortMessage = string;
    }

    public String getShortMessage() {
        return this.m_shortMessage;
    }

    public void setMessageText(String string) {
        this.m_messageText = string;
    }

    public String getMessageText() {
        return this.m_messageText;
    }

    public void setMessageLink(String string) {
        try {
            this.m_messageLink = new URL(string);
        }
        catch (MalformedURLException malformedURLException) {
            LogHolder.log(7, LogType.PAY, "Could not get valid URL from the given String, messageLink will be null");
        }
    }

    public void setMessageLink(URL uRL) {
        this.m_messageLink = uRL;
    }

    public URL getMessageLink() {
        return this.m_messageLink;
    }
}

