/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractCascadeIDEntry;
import anon.infoservice.MixCascade;
import anon.util.ClassUtil;
import anon.util.XMLParseException;
import org.w3c.dom.Element;

public class CascadeIDEntry
extends AbstractCascadeIDEntry {
    public static final String XML_ELEMENT_NAME = ClassUtil.getShortClassName(class$anon$infoservice$CascadeIDEntry == null ? (class$anon$infoservice$CascadeIDEntry = CascadeIDEntry.class$("anon.infoservice.CascadeIDEntry")) : class$anon$infoservice$CascadeIDEntry);
    public static final String XML_ELEMENT_CONTAINER_NAME = "KnownCascades";
    private static final long EXPIRE_TIME = 604800000L;
    static /* synthetic */ Class class$anon$infoservice$CascadeIDEntry;

    public CascadeIDEntry(MixCascade mixCascade) throws IllegalArgumentException {
        super(mixCascade, System.currentTimeMillis() + 604800000L);
    }

    public CascadeIDEntry(Element element) throws XMLParseException {
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

