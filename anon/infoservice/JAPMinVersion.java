/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractDistributableDatabaseEntry;
import anon.infoservice.ServiceSoftware;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.File;
import org.w3c.dom.Element;

public class JAPMinVersion
extends AbstractDistributableDatabaseEntry {
    public static final String DEFAULT_ID = "JAPMinVersion";
    private static final long DATABASE_TIMEOUT = Long.MAX_VALUE;
    private ServiceSoftware m_japSoftware;
    private long m_lastUpdate;
    private Element m_xmlStructure;
    private byte[] m_bytesPostData;

    public static String getXmlElementName() {
        return "Jap";
    }

    public JAPMinVersion(File file) throws Exception {
        this(XMLUtil.readXMLDocument(file).getDocumentElement());
    }

    public JAPMinVersion(Element element) throws Exception {
        super(Long.MAX_VALUE);
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, ServiceSoftware.getXmlElementName());
        if (element2 == null) {
            throw new Exception("JAPMinVersion: Constructor: Error in XML structure: No software node.");
        }
        this.m_japSoftware = new ServiceSoftware(element2);
        String string = this.m_japSoftware.getVersion();
        if (string.charAt(2) != '.' || string.charAt(5) != '.') {
            throw new XMLParseException("Invalid version number format: " + string);
        }
        this.m_lastUpdate = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "LastUpdate"), -1L);
        if (this.m_lastUpdate == -1L) {
            this.m_lastUpdate = System.currentTimeMillis();
        }
        this.m_xmlStructure = element;
        this.m_bytesPostData = super.getPostData();
    }

    public String getId() {
        return DEFAULT_ID;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public long getVersionNumber() {
        return this.m_lastUpdate;
    }

    public ServiceSoftware getJapSoftware() {
        return this.m_japSoftware;
    }

    public String getPostFile() {
        return "/currentjapversion";
    }

    public Element getXmlStructure() {
        return this.m_xmlStructure;
    }

    public byte[] getPostData() {
        return this.m_bytesPostData;
    }
}

