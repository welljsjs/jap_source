/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractCascadeIDEntry;
import anon.infoservice.MixCascade;
import anon.util.ClassUtil;
import anon.util.XMLParseException;
import org.w3c.dom.Element;

public class PreviouslyKnownCascadeIDEntry
extends AbstractCascadeIDEntry {
    public static final String XML_ELEMENT_NAME = ClassUtil.getShortClassName(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = PreviouslyKnownCascadeIDEntry.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry);
    public static final String XML_ELEMENT_CONTAINER_NAME = "PreviouslyKnownCascades";
    static /* synthetic */ Class class$anon$infoservice$PreviouslyKnownCascadeIDEntry;

    public PreviouslyKnownCascadeIDEntry(MixCascade mixCascade) {
        super(mixCascade, Long.MAX_VALUE);
    }

    public PreviouslyKnownCascadeIDEntry(Element element) throws XMLParseException {
        super(element);
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

