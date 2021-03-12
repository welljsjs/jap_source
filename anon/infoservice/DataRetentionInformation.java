/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.MixCascade;
import anon.util.XMLDuration;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataRetentionInformation {
    public static final String XML_ELEMENT_NAME = "DataRetention";
    public static final String XML_ELEMENT_LOGGED_ELEMENTS = "LoggedElements";
    public static final String XML_ELEMENT_RETENTION_PERIOD = "RetentionPeriod";
    public static final String XML_ELEMENT_DESCRIPTION = "Description";
    public static final String XML_ELEMENT_URL = "URL";
    public static final int NOTHING = 1;
    public static final int INPUT_TIME = 2;
    public static final int OUTPUT_TIME = 4;
    public static final int INPUT_CHANNEL_ID = 8;
    public static final int OUTPUT_CHANNEL_ID = 16;
    public static final int INPUT_SOURCE_IP_ADDRESS = 32;
    public static final int INPUT_SOURCE_IP_PORT = 64;
    public static final int OUTPUT_SOURCE_IP_ADDRESS = 128;
    public static final int OUTPUT_SOURCE_IP_PORT = 256;
    public static final int OUTPUT_TARGET_IP_ADDRESS = 512;
    public static final int OUTPUT_TARGET_DOMAIN = 1024;
    public static final String FIELD_NAME_INPUT_SOURCE_IP_ADDRESS_MIX = "INPUT_SOURCE_IP_ADDRESS_MIX";
    public static final String FIELD_NAME_INPUT_SOURCE_IP_PORT_MIX = "INPUT_SOURCE_IP_PORT_MIX";
    private static final int[] FIELDS = new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
    private static final String[] FIELD_NAMES = new String[]{"INPUT_TIME", "OUTPUT_TIME", "INPUT_CHANNEL_ID", "OUTPUT_CHANNEL_ID", "INPUT_SOURCE_IP_ADDRESS", "INPUT_SOURCE_IP_PORT", "OUTPUT_SOURCE_IP_ADDRESS", "OUTPUT_SOURCE_IP_PORT", "OUTPUT_TARGET_IP_ADDRESS", "OUTPUT_TARGET_DOMAIN"};
    private XMLDuration m_duration;
    private Hashtable m_hashURLs = new Hashtable();
    private int m_loggedElements = 1;

    public DataRetentionInformation(Element element) throws XMLParseException {
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        Node node = XMLUtil.getFirstChildByName(element, XML_ELEMENT_LOGGED_ELEMENTS);
        this.m_loggedElements = 0;
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "InputTime"), false)) {
            this.m_loggedElements += 2;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "OutputTime"), false)) {
            this.m_loggedElements += 4;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "InputChannelID"), false)) {
            this.m_loggedElements += 8;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "OutputChannelID"), false)) {
            this.m_loggedElements += 16;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "InputSourceIPAddress"), false)) {
            this.m_loggedElements += 32;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "InputSourceIPPort"), false)) {
            this.m_loggedElements += 64;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "OutputSourceIPAddress"), false)) {
            this.m_loggedElements += 128;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "OutputSourceIPPort"), false)) {
            this.m_loggedElements += 256;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "OutputTargetIPAddress"), false)) {
            this.m_loggedElements += 512;
        }
        if (XMLUtil.parseValue(XMLUtil.getFirstChildByName(node, "OutputTargetDomain"), false)) {
            this.m_loggedElements += 1024;
        }
        if (this.m_loggedElements == 0) {
            this.m_loggedElements = 1;
        }
        this.m_duration = new XMLDuration(XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ELEMENT_RETENTION_PERIOD), null));
        if (this.m_duration.getSign() < 0) {
            throw new XMLParseException("Negative retention duration is not allowed!");
        }
        NodeList nodeList = XMLUtil.getElementsByTagName(element, XML_ELEMENT_DESCRIPTION);
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                try {
                    this.m_hashURLs.put(XMLUtil.parseAttribute(nodeList.item(i), "lang", "en"), new URL(XMLUtil.parseValue(XMLUtil.getFirstChildByName(nodeList.item(i), XML_ELEMENT_URL), null)));
                    continue;
                }
                catch (MalformedURLException malformedURLException) {
                    throw new XMLParseException(malformedURLException.getMessage());
                }
            }
        }
    }

    private DataRetentionInformation() {
        this.m_duration = new XMLDuration();
    }

    public static DataRetentionInformation getCascadeDataRetentionInformation(MixCascade mixCascade) {
        DataRetentionInformation dataRetentionInformation;
        if (mixCascade == null) {
            return null;
        }
        Vector<DataRetentionInformation> vector = new Vector<DataRetentionInformation>();
        for (int i = 0; i < mixCascade.getNumberOfMixes(); ++i) {
            if (mixCascade.getMixInfo(i) == null || mixCascade.getMixInfo(i).getDataRetentionInformation() == null) {
                if (i == 0) {
                    return null;
                }
                dataRetentionInformation = new DataRetentionInformation();
            } else {
                dataRetentionInformation = mixCascade.getMixInfo(i).getDataRetentionInformation();
                if (!(i != 0 || dataRetentionInformation == null || dataRetentionInformation.isLogged(8) && dataRetentionInformation.isLogged(32))) {
                    return null;
                }
            }
            vector.addElement(dataRetentionInformation);
        }
        dataRetentionInformation = DataRetentionInformation.getCascadeDataRetentionInformation(vector);
        return dataRetentionInformation;
    }

    private static DataRetentionInformation getCascadeDataRetentionInformation(Vector vector) {
        DataRetentionInformation dataRetentionInformation = new DataRetentionInformation();
        if (vector == null) {
            return dataRetentionInformation;
        }
        Vector vector2 = (Vector)vector.clone();
        if (vector2.size() == 0) {
            return dataRetentionInformation;
        }
        DataRetentionInformation dataRetentionInformation2 = (DataRetentionInformation)vector.elementAt(0);
        dataRetentionInformation.m_loggedElements = dataRetentionInformation2.getLoggedElementIDs();
        dataRetentionInformation.m_duration = dataRetentionInformation2.m_duration;
        dataRetentionInformation.m_hashURLs = (Hashtable)dataRetentionInformation2.m_hashURLs.clone();
        block0: for (int i = 1; i < vector.size(); ++i) {
            dataRetentionInformation2 = (DataRetentionInformation)vector.elementAt(i);
            dataRetentionInformation.m_loggedElements = i == vector.size() - 1 ? (dataRetentionInformation.m_loggedElements &= dataRetentionInformation2.getLoggedElementIDs() | 0x10) : (dataRetentionInformation.m_loggedElements &= dataRetentionInformation2.getLoggedElementIDs());
            if (dataRetentionInformation.m_duration.isLongerThan(dataRetentionInformation2.m_duration)) {
                dataRetentionInformation.m_duration = dataRetentionInformation2.m_duration;
            }
            if (dataRetentionInformation.m_hashURLs.size() == dataRetentionInformation2.m_hashURLs.size()) {
                Enumeration enumeration = dataRetentionInformation2.m_hashURLs.keys();
                while (enumeration.hasMoreElements()) {
                    Object k = enumeration.nextElement();
                    if (dataRetentionInformation.m_hashURLs.containsKey(k) && dataRetentionInformation.m_hashURLs.get(k).equals(dataRetentionInformation2.m_hashURLs.get(k))) continue;
                    dataRetentionInformation.m_hashURLs.clear();
                    continue block0;
                }
                continue;
            }
            dataRetentionInformation.m_hashURLs.clear();
        }
        if (dataRetentionInformation.m_loggedElements == 0) {
            dataRetentionInformation.m_loggedElements = 1;
        }
        return dataRetentionInformation;
    }

    public static int getLoggedElementsLength() {
        return FIELDS.length;
    }

    public static int getLoggedElementID(int n) {
        if (n < 0 || n > FIELDS.length) {
            return -1;
        }
        return FIELDS[n];
    }

    public static String getLoggedElementName(int n) {
        if (n < 0 || n > FIELD_NAMES.length) {
            return null;
        }
        return FIELD_NAMES[n];
    }

    public boolean isLogged(int n) {
        return (n & this.m_loggedElements) == n;
    }

    public int getLoggedElementIDs() {
        return this.m_loggedElements;
    }

    public URL getURL(String string) {
        URL uRL = (URL)this.m_hashURLs.get(string);
        if (uRL == null) {
            uRL = (URL)this.m_hashURLs.get("en");
        }
        return uRL;
    }

    public XMLDuration getDuration() {
        return this.m_duration;
    }
}

