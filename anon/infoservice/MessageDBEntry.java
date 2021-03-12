/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.SignatureVerifier;
import anon.infoservice.AbstractDistributableDatabaseEntry;
import anon.infoservice.IDistributable;
import anon.util.Base64;
import anon.util.URLCoder;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SignatureException;
import java.util.Hashtable;
import java.util.Locale;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MessageDBEntry
extends AbstractDistributableDatabaseEntry
implements IDistributable {
    public static final String XML_ELEMENT_CONTAINER_NAME = "Messages";
    public static final String XML_ELEMENT_NAME = "Message";
    public static final String HTTP_REQUEST_STRING = "/messages";
    public static final String HTTP_SERIALS_REQUEST_STRING = "/messageserials";
    public static final String PROPERTY_NAME = "messageFileName";
    public static final String POST_FILE = "/message";
    private static final String XML_TEXT = "MessageText";
    private static final String XML_URL = "MessageURL";
    private static final String XML_ATTR_LANG = "lang";
    private static final String XML_ATTR_POPUP = "popup";
    private static final String XML_ATTR_ENCODING = "encoding";
    private static final String XML_ATTR_FREE = "free";
    private static final String XML_ELEM_POPUP_TEXT = "MessagePopupText";
    private static final String ENCODING_URL = "url";
    private static final String ENCODING_BASE64 = "base64";
    private static final long TIMEOUT = 604800000L;
    private int m_externalIdentifier;
    private long m_serial;
    private long m_lastUpdate;
    private boolean m_bIsDummy;
    private boolean m_bFree;
    private boolean m_bShowPopup;
    private String m_id;
    private Element m_xmlDescription;
    private Hashtable m_hashText = new Hashtable();
    private Hashtable m_hashPopupText = new Hashtable();
    private Hashtable m_hashUrl = new Hashtable();

    public MessageDBEntry(Element element) throws XMLParseException, SignatureException {
        super(System.currentTimeMillis() + 604800000L);
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        if (SignatureVerifier.getInstance().getVerifiedXml(element, 2) == null) {
            throw new SignatureException();
        }
        this.m_serial = XMLUtil.parseAttribute((Node)element, "serial", Long.MIN_VALUE);
        this.m_id = XMLUtil.parseAttribute((Node)element, "id", null);
        this.m_bShowPopup = XMLUtil.parseAttribute((Node)element, XML_ATTR_POPUP, false);
        this.m_bFree = XMLUtil.parseAttribute((Node)element, XML_ATTR_FREE, false);
        if (this.m_id == null) {
            throw new XMLParseException("No id given!");
        }
        this.m_bIsDummy = this.parseTextNodes(element.getElementsByTagName(XML_TEXT), this.m_hashText);
        if (!this.m_bIsDummy) {
            NodeList nodeList = element.getElementsByTagName(XML_URL);
            for (int i = 0; i < nodeList.getLength(); ++i) {
                String string = XMLUtil.parseValue(nodeList.item(i), null);
                String string2 = XMLUtil.parseAttribute(nodeList.item(i), XML_ATTR_LANG, "en");
                if (string == null) continue;
                try {
                    this.m_hashUrl.put(string2, new URL(string));
                    continue;
                }
                catch (MalformedURLException malformedURLException) {
                    // empty catch block
                }
            }
            this.parseTextNodes(element.getElementsByTagName(XML_ELEM_POPUP_TEXT), this.m_hashPopupText);
        }
        this.m_lastUpdate = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "LastUpdate"), -1L);
        if (this.m_lastUpdate == -1L) {
            this.m_lastUpdate = System.currentTimeMillis();
        }
        this.m_xmlDescription = element;
    }

    public URL getURL(Locale locale) {
        if (locale == null) {
            return null;
        }
        URL uRL = null;
        Object v = this.m_hashUrl.get(locale.getLanguage());
        if (v != null && v instanceof URL) {
            uRL = (URL)v;
        } else {
            v = this.m_hashUrl.get("en");
            if (v != null && v instanceof URL) {
                uRL = (URL)v;
            }
        }
        if (uRL == null) {
            LogHolder.log(4, LogType.MISC, "Could not get URL for message: " + this.getText(locale));
        }
        return uRL;
    }

    public String getText(Locale locale) {
        return this.getText(locale, this.m_hashText);
    }

    public String getPopupText(Locale locale) {
        return this.getText(locale, this.m_hashPopupText);
    }

    public int getExternalIdentifier() {
        return this.m_externalIdentifier;
    }

    public void setExternalIdentifier(int n) {
        this.m_externalIdentifier = n;
    }

    public boolean isPopupShown() {
        return this.m_bShowPopup;
    }

    public boolean isForFreeCascadesOnly() {
        return this.m_bFree;
    }

    public boolean isDummy() {
        return this.m_bIsDummy;
    }

    public long getVersionNumber() {
        return this.m_serial;
    }

    public String getId() {
        return this.m_id;
    }

    public String getPostFile() {
        return POST_FILE;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public Element getXmlStructure() {
        return this.m_xmlDescription;
    }

    private String getText(Locale locale, Hashtable hashtable) {
        if (locale == null) {
            return null;
        }
        String string = (String)hashtable.get(locale.getLanguage());
        if (string == null) {
            string = (String)hashtable.get("en");
        }
        return string;
    }

    private boolean parseTextNodes(NodeList nodeList, Hashtable hashtable) {
        for (int i = 0; i < nodeList.getLength(); ++i) {
            String string = XMLUtil.parseValue(nodeList.item(i), null);
            String string2 = XMLUtil.parseAttribute(nodeList.item(i), XML_ATTR_LANG, "en");
            String string3 = XMLUtil.parseAttribute(nodeList.item(i), XML_ATTR_ENCODING, ENCODING_BASE64);
            if (string == null || (string = string3.equals(ENCODING_URL) ? URLCoder.decode(string) : (string3.equals(ENCODING_BASE64) ? Base64.decodeToString(string) : null)) == null) continue;
            hashtable.put(string2, string);
        }
        boolean bl = hashtable.size() == 0 || hashtable.get("en") == null;
        return bl;
    }
}

