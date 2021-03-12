/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractDistributableDatabaseEntry;
import anon.infoservice.Database;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.URL;
import java.util.Enumeration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JavaVersionDBEntry
extends AbstractDistributableDatabaseEntry {
    public static final String CURRENT_JAVA_VENDOR = System.getProperty("java.vendor");
    public static final String CURRENT_JAVA_VERSION = System.getProperty("java.version");
    public static final String HTTP_REQUEST_STRING = "/currentjavaversion";
    public static final String HTTP_SERIALS_REQUEST_STRING = "/currentjavaversionSerials";
    public static final String PROPERTY_NAME = "jreVersionsFileName";
    public static final String VENDOR_ID_SUN_JAVA = "Sun";
    public static final String VENDOR_ID_BLACKDOWN_JAVA = "Blackdown";
    public static final String XML_ELEMENT_NAME = "JavaVersion";
    public static final String XML_ELEMENT_CONTAINER_NAME = "JavaVersionInfos";
    private static final String OS_NAME = System.getProperty("os.name", "");
    private static final String XML_ATTR_SUPPORT_FROM_VERSION = "supportFromVersion";
    private static final String XML_ATTR_VENDOR = "vendor";
    private static final String XML_ATTR_OPERATING_SYSTEM = "os";
    private static final String XML_ELEM_VERSION = "LatestVersion";
    private static final String XML_ATTR_VERSION_NAME = "name";
    private static final String XML_ATTR_FORCE = "force";
    private static final String XML_ELEM_DOWNLOAD_URL = "DownloadURL";
    private static final String XML_ELEM_VENDOR_LONG = "VendorLongName";
    private static final String XML_ELEM_LAST_UPDATE = "LastUpdate";
    private static final String[] VENDOR_IDS = new String[]{"Sun", "Blackdown"};
    private static final long TIMEOUT = Long.MAX_VALUE;
    private long m_lastUpdate;
    private String m_latestVersion;
    private String m_lastSupportedVersion;
    private String m_vendor;
    private URL m_downloadURL;
    private String m_vendorLongName;
    private String m_versionName;
    private boolean m_bForce;
    private Element m_xmlDescription;
    static /* synthetic */ Class class$anon$infoservice$JavaVersionDBEntry;

    public JavaVersionDBEntry(Element element) throws XMLParseException {
        super(Long.MAX_VALUE);
        String string;
        int n;
        if (element == null || !element.getNodeName().equals(XML_ELEMENT_NAME)) {
            throw new XMLParseException("##__root__##");
        }
        this.m_vendor = XMLUtil.parseAttribute((Node)element, XML_ATTR_VENDOR, null);
        if (!JavaVersionDBEntry.checkVendor(this.m_vendor)) {
            throw new XMLParseException(XML_ELEMENT_NAME, "Unknown vendor!");
        }
        this.m_lastSupportedVersion = XMLUtil.parseAttribute((Node)element, XML_ATTR_SUPPORT_FROM_VERSION, "");
        NodeList nodeList = element.getElementsByTagName(XML_ELEM_VERSION);
        for (n = 0; n < nodeList.getLength(); ++n) {
            string = XMLUtil.parseAttribute(nodeList.item(n), XML_ATTR_OPERATING_SYSTEM, "");
            if ((this.m_latestVersion != null || string.length() != 0) && OS_NAME.indexOf(string) < 0) continue;
            try {
                this.m_latestVersion = XMLUtil.parseValue(nodeList.item(n), null);
                this.m_versionName = XMLUtil.parseAttribute(nodeList.item(n), XML_ATTR_VERSION_NAME, null);
                this.m_bForce = XMLUtil.parseAttribute(nodeList.item(n), XML_ATTR_FORCE, false);
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.m_latestVersion == null) {
            throw new XMLParseException(XML_ELEM_VERSION);
        }
        Node node = XMLUtil.getFirstChildByName(element, XML_ELEM_LAST_UPDATE);
        this.m_lastUpdate = XMLUtil.parseValue(node, -1L);
        if (this.m_lastUpdate == -1L) {
            this.m_lastUpdate = System.currentTimeMillis();
        }
        nodeList = element.getElementsByTagName(XML_ELEM_DOWNLOAD_URL);
        for (n = 0; n < nodeList.getLength(); ++n) {
            string = XMLUtil.parseAttribute(nodeList.item(n), XML_ATTR_OPERATING_SYSTEM, "");
            if ((this.m_downloadURL != null || string.length() != 0) && OS_NAME.indexOf(string) < 0) continue;
            try {
                this.m_downloadURL = new URL(XMLUtil.parseValue(nodeList.item(n), null));
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (this.m_downloadURL == null) {
            throw new XMLParseException(XML_ELEM_DOWNLOAD_URL);
        }
        node = XMLUtil.getFirstChildByName(element, XML_ELEM_VENDOR_LONG);
        try {
            this.m_vendorLongName = XMLUtil.parseValue(node, null);
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_xmlDescription = element;
    }

    public boolean isUpdateForced() {
        return this.m_bForce;
    }

    public static JavaVersionDBEntry getNewJavaVersion() {
        Enumeration enumeration = Database.getInstance(class$anon$infoservice$JavaVersionDBEntry == null ? (class$anon$infoservice$JavaVersionDBEntry = JavaVersionDBEntry.class$("anon.infoservice.JavaVersionDBEntry")) : class$anon$infoservice$JavaVersionDBEntry).getEntrySnapshotAsEnumeration();
        while (enumeration.hasMoreElements()) {
            JavaVersionDBEntry javaVersionDBEntry = (JavaVersionDBEntry)enumeration.nextElement();
            if (!javaVersionDBEntry.isJavaTooOld()) continue;
            return javaVersionDBEntry;
        }
        return null;
    }

    public boolean isJavaTooOld() {
        return this.isJavaOK(false);
    }

    public boolean isJavaNoMoreSupported() {
        return this.isJavaOK(true);
    }

    private boolean isJavaOK(boolean bl) {
        if (CURRENT_JAVA_VENDOR == null) {
            return false;
        }
        String string = this.getVendor().toLowerCase();
        String string2 = CURRENT_JAVA_VENDOR.toLowerCase();
        return string2.indexOf("microsoft") >= 0 || string2.indexOf(string) >= 0 && (CURRENT_JAVA_VERSION == null || CURRENT_JAVA_VERSION.compareTo(bl ? this.getLastSupportedJREVersion() : this.getJREVersion()) < 0);
    }

    public String getLastSupportedJREVersion() {
        return this.m_lastSupportedVersion;
    }

    public URL getDownloadURL() {
        return this.m_downloadURL;
    }

    public Element getXmlStructure() {
        return this.m_xmlDescription;
    }

    public String getJREVersion() {
        return this.m_latestVersion;
    }

    public String getJREVersionName() {
        return this.m_versionName;
    }

    public long getVersionNumber() {
        return this.m_lastUpdate;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public String getVendor() {
        return this.m_vendor;
    }

    public String getVendorLongName() {
        if (this.m_vendorLongName == null || this.m_vendorLongName.trim().length() == 0) {
            return this.m_vendor;
        }
        return this.m_vendorLongName;
    }

    public String getId() {
        return this.m_vendor;
    }

    public String getPostFile() {
        return HTTP_REQUEST_STRING;
    }

    private static boolean checkVendor(String string) {
        if (string == null) {
            return false;
        }
        for (int i = 0; i < VENDOR_IDS.length; ++i) {
            if (!VENDOR_IDS[i].equals(string)) continue;
            return true;
        }
        return false;
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

