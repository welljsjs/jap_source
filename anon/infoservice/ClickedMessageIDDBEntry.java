/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractMarkedMessageIDDBEntry;
import anon.infoservice.MessageDBEntry;
import anon.util.XMLParseException;
import org.w3c.dom.Element;

public class ClickedMessageIDDBEntry
extends AbstractMarkedMessageIDDBEntry {
    public static final String XML_ELEMENT_NAME = "ClickedMessageIDDBEntry";
    public static final String XML_ELEMENT_CONTAINER_NAME = "ClickedMessageIDDBEntries";

    public ClickedMessageIDDBEntry(MessageDBEntry messageDBEntry) {
        super(messageDBEntry);
    }

    public ClickedMessageIDDBEntry(Element element) throws XMLParseException {
        super(element);
    }

    public String getXmlElementName() {
        return XML_ELEMENT_NAME;
    }
}

