/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.platform.AbstractOS;
import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class JAPHyperlinkAdapter
implements HyperlinkListener {
    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        URL uRL;
        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED && (uRL = hyperlinkEvent.getURL()) != null) {
            if (uRL.getProtocol().startsWith("mailto:")) {
                AbstractOS.getInstance().openEMail(uRL.toString());
            } else {
                AbstractOS.getInstance().openURL(uRL);
            }
        }
    }
}

