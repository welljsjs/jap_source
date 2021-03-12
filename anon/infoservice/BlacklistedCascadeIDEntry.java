/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractCascadeIDEntry;
import anon.infoservice.Database;
import anon.infoservice.DatabaseMessage;
import anon.infoservice.MixCascade;
import anon.infoservice.PreviouslyKnownCascadeIDEntry;
import anon.util.ClassUtil;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BlacklistedCascadeIDEntry
extends AbstractCascadeIDEntry {
    public static final boolean DEFAULT_AUTO_BLACKLIST = false;
    public static final String XML_ELEMENT_NAME = ClassUtil.getShortClassName(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = BlacklistedCascadeIDEntry.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry);
    public static final String XML_ELEMENT_CONTAINER_NAME = "BlacklistedCascades";
    public static final String XML_ATTR_AUTO_BLACKLIST_NEW_CASCADES = "autoBlacklistNewCascades";
    private static boolean m_bNewCascadesInBlacklist = false;
    private static Observer ms_observer;
    static /* synthetic */ Class class$anon$infoservice$BlacklistedCascadeIDEntry;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$PreviouslyKnownCascadeIDEntry;

    public BlacklistedCascadeIDEntry(MixCascade mixCascade) {
        super(mixCascade, Long.MAX_VALUE);
    }

    public BlacklistedCascadeIDEntry(Element element) throws XMLParseException {
        super(element);
        m_bNewCascadesInBlacklist = XMLUtil.parseAttribute((Node)element, XML_ATTR_AUTO_BLACKLIST_NEW_CASCADES, m_bNewCascadesInBlacklist);
    }

    public static boolean hasActiveElements() {
        Vector vector = Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = BlacklistedCascadeIDEntry.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).getEntryList();
        boolean bl = false;
        for (int i = 0; i < vector.size(); ++i) {
            MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? BlacklistedCascadeIDEntry.class$("anon.infoservice.MixCascade") : class$anon$infoservice$MixCascade).getEntryById(((BlacklistedCascadeIDEntry)vector.elementAt(i)).getCascadeId());
            if (!((BlacklistedCascadeIDEntry)vector.elementAt(i)).isReferencedCascade(mixCascade)) continue;
            bl = true;
            break;
        }
        return bl;
    }

    protected void toXmlElementAppend(Element element) {
        XMLUtil.setAttribute(element, XML_ATTR_AUTO_BLACKLIST_NEW_CASCADES, m_bNewCascadesInBlacklist);
    }

    public static synchronized void putNewCascadesInBlacklist(boolean bl) {
        if (ms_observer == null) {
            ms_observer = new Observer(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void update(Observable observable, Object object) {
                    Class class_ = class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = BlacklistedCascadeIDEntry.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry;
                    synchronized (class_) {
                        DatabaseMessage databaseMessage = (DatabaseMessage)object;
                        if (databaseMessage.getMessageData() == null || !(databaseMessage.getMessageData() instanceof MixCascade)) {
                            return;
                        }
                        MixCascade mixCascade = (MixCascade)databaseMessage.getMessageData();
                        if (!mixCascade.isUserDefined() && Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = BlacklistedCascadeIDEntry.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).getEntryById(mixCascade.getMixIDsAsString()) == null) {
                            Database.getInstance(class$anon$infoservice$PreviouslyKnownCascadeIDEntry == null ? (class$anon$infoservice$PreviouslyKnownCascadeIDEntry = BlacklistedCascadeIDEntry.class$("anon.infoservice.PreviouslyKnownCascadeIDEntry")) : class$anon$infoservice$PreviouslyKnownCascadeIDEntry).update(new PreviouslyKnownCascadeIDEntry(mixCascade));
                            if ((databaseMessage.getMessageCode() == 1 || databaseMessage.getMessageCode() == 2) && m_bNewCascadesInBlacklist) {
                                Database.getInstance(class$anon$infoservice$BlacklistedCascadeIDEntry == null ? (class$anon$infoservice$BlacklistedCascadeIDEntry = BlacklistedCascadeIDEntry.class$("anon.infoservice.BlacklistedCascadeIDEntry")) : class$anon$infoservice$BlacklistedCascadeIDEntry).update(new BlacklistedCascadeIDEntry(mixCascade));
                            }
                        }
                    }
                }
            };
            Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = BlacklistedCascadeIDEntry.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).addObserver(ms_observer);
        }
        if (m_bNewCascadesInBlacklist != bl) {
            m_bNewCascadesInBlacklist = bl;
        }
    }

    public static synchronized boolean areNewCascadesInBlacklist() {
        return m_bNewCascadesInBlacklist;
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

