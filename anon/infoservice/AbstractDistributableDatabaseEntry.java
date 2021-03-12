/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.IVerifyable;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.IBoostrapable;
import anon.infoservice.IDistributable;
import anon.infoservice.IServiceContextContainer;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractDistributableDatabaseEntry
extends AbstractDatabaseEntry
implements IDistributable,
IXMLEncodable {
    public static final String XML_ATTR_SERIAL = "serial";
    public static final String XML_ATTR_VERIFIED = "verified";
    public static final String XML_ATTR_VALID = "valid";
    public static final String XML_ATTR_LAST_UPDATE = "lastUpdate";
    static /* synthetic */ Class class$anon$infoservice$AbstractDistributableDatabaseEntry;

    public AbstractDistributableDatabaseEntry(long l) {
        super(l);
    }

    public static String getHttpRequestString(Class class_) {
        return Util.getStaticFieldValue(class_, "HTTP_REQUEST_STRING");
    }

    public static String getHttpSerialsRequestString(Class class_) {
        return Util.getStaticFieldValue(class_, "HTTP_SERIALS_REQUEST_STRING");
    }

    public abstract Element getXmlStructure();

    public byte[] getPostData() {
        return XMLUtil.toString(this.getXmlStructure()).getBytes();
    }

    public int getPostEncoding() {
        return 0;
    }

    public final Element toXmlElement(Document document) {
        Element element = null;
        try {
            element = (Element)XMLUtil.importNode(document, this.getXmlStructure(), true);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (element == null && XMLUtil.getStorageMode() == 2) {
            LogHolder.log(4, LogType.MISC, "Got null XML element, maybe due to aggressive storage mode.");
        }
        return element;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public abstract /* synthetic */ String getPostFile();

    public abstract /* synthetic */ String getId();

    public static class SerialDBEntry
    extends AbstractDatabaseEntry
    implements IServiceContextContainer {
        private String m_id;
        private long m_version;
        private long m_lastUpdate;
        private boolean m_bVerified;
        private boolean m_bValid;
        private String m_context;

        public SerialDBEntry(String string, long l, long l2, boolean bl, boolean bl2, String string2) {
            super(0L);
            this.m_id = string;
            this.m_version = l;
            this.m_lastUpdate = l2;
            this.m_bVerified = bl;
            this.m_bValid = bl2;
            this.m_context = string2;
        }

        public boolean isVerified() {
            return this.m_bVerified;
        }

        public boolean isValid() {
            return this.m_bValid;
        }

        public long getLastUpdate() {
            return this.m_lastUpdate;
        }

        public String getId() {
            return this.m_id;
        }

        public long getVersionNumber() {
            return this.m_version;
        }

        public String getContext() {
            return this.m_context;
        }

        public void setContext(String string) {
            this.m_context = string;
        }
    }

    public static class Serials
    implements IXMLEncodable {
        private static final String XML_ELEMENT_NAME = "Serials";
        private Class m_thisDBEntryClass;

        public Serials(Class class_) throws IllegalArgumentException {
            if (class_ == null || !(class$anon$infoservice$AbstractDistributableDatabaseEntry == null ? (class$anon$infoservice$AbstractDistributableDatabaseEntry = AbstractDistributableDatabaseEntry.class$("anon.infoservice.AbstractDistributableDatabaseEntry")) : class$anon$infoservice$AbstractDistributableDatabaseEntry).isAssignableFrom(class_)) {
                throw new IllegalArgumentException("Illegal class argument!");
            }
            this.m_thisDBEntryClass = class_;
        }

        public Hashtable parse(Element element) throws XMLParseException {
            if (element == null || element.getNodeName() == null || !element.getNodeName().equals(XML_ELEMENT_NAME)) {
                throw new XMLParseException("##__null__##");
            }
            String string = null;
            NodeList nodeList = element.getElementsByTagName(XMLUtil.getXmlElementName(this.m_thisDBEntryClass));
            Hashtable<String, SerialDBEntry> hashtable = nodeList.getLength() > 0 ? new Hashtable(nodeList.getLength()) : new Hashtable<String, SerialDBEntry>();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                String string2 = XMLUtil.parseAttribute(nodeList.item(i), "id", null);
                if (string2 == null) continue;
                long l = XMLUtil.parseAttribute(nodeList.item(i), AbstractDistributableDatabaseEntry.XML_ATTR_SERIAL, 0L);
                long l2 = XMLUtil.parseAttribute(nodeList.item(i), AbstractDistributableDatabaseEntry.XML_ATTR_LAST_UPDATE, 0L);
                boolean bl = XMLUtil.parseAttribute(nodeList.item(i), AbstractDistributableDatabaseEntry.XML_ATTR_VERIFIED, false);
                boolean bl2 = XMLUtil.parseAttribute(nodeList.item(i), AbstractDistributableDatabaseEntry.XML_ATTR_VALID, false);
                string = XMLUtil.parseAttribute(nodeList.item(i), "context", "jondonym");
                if (string.equals("de.jondos.jondonym")) {
                    string = "jondonym";
                }
                hashtable.put(string2, new SerialDBEntry(string2, l, l2, bl, bl2, string));
            }
            return hashtable;
        }

        public Element toXmlElement(Document document) {
            if (document == null) {
                return null;
            }
            Element element = document.createElement(XML_ELEMENT_NAME);
            Enumeration enumeration = Database.getInstance(this.m_thisDBEntryClass).getEntrySnapshotAsEnumeration();
            while (enumeration.hasMoreElements()) {
                String string;
                AbstractDistributableDatabaseEntry abstractDistributableDatabaseEntry = (AbstractDistributableDatabaseEntry)enumeration.nextElement();
                if (abstractDistributableDatabaseEntry instanceof IBoostrapable && ((IBoostrapable)((Object)abstractDistributableDatabaseEntry)).isBootstrap()) continue;
                if (abstractDistributableDatabaseEntry.getVersionNumber() <= 0L) {
                    // empty if block
                }
                Element element2 = document.createElement(XMLUtil.getXmlElementName(this.m_thisDBEntryClass));
                element.appendChild(element2);
                XMLUtil.setAttribute(element2, "id", ((AbstractDatabaseEntry)abstractDistributableDatabaseEntry).getId());
                XMLUtil.setAttribute(element2, AbstractDistributableDatabaseEntry.XML_ATTR_LAST_UPDATE, abstractDistributableDatabaseEntry.getLastUpdate());
                XMLUtil.setAttribute(element2, AbstractDistributableDatabaseEntry.XML_ATTR_SERIAL, abstractDistributableDatabaseEntry.getVersionNumber());
                if (abstractDistributableDatabaseEntry instanceof IVerifyable) {
                    XMLUtil.setAttribute(element2, AbstractDistributableDatabaseEntry.XML_ATTR_VALID, ((IVerifyable)((Object)abstractDistributableDatabaseEntry)).isValid());
                    XMLUtil.setAttribute(element2, AbstractDistributableDatabaseEntry.XML_ATTR_VERIFIED, ((IVerifyable)((Object)abstractDistributableDatabaseEntry)).isVerified() && ((IVerifyable)((Object)abstractDistributableDatabaseEntry)).getCertPath().isVerified());
                }
                if (!(abstractDistributableDatabaseEntry instanceof IServiceContextContainer) || (string = ((IServiceContextContainer)((Object)abstractDistributableDatabaseEntry)).getContext()) == null) continue;
                XMLUtil.setAttribute(element2, "context", string);
            }
            return element;
        }
    }
}

