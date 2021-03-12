/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.IServiceContainer;
import anon.client.Multiplexer;
import anon.client.StreamedControlChannel;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;

public abstract class XmlControlChannel
extends StreamedControlChannel {
    public XmlControlChannel(int n, Multiplexer multiplexer, IServiceContainer iServiceContainer, boolean bl) {
        super(n, multiplexer, iServiceContainer, bl);
    }

    public int sendXmlMessage(Document document) {
        return this.sendByteMessage(XMLUtil.toByteArray(document));
    }

    protected void processMessage(byte[] arrby) {
        try {
            this.processXmlMessage(XMLUtil.toXMLDocument(arrby));
        }
        catch (XMLParseException xMLParseException) {
            LogHolder.log(3, LogType.NET, "Error while parsing XML document!", xMLParseException);
        }
    }

    protected abstract void processXmlMessage(Document var1);
}

