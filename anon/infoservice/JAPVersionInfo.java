/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractDistributableDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.JavaVersionDBEntry;
import anon.util.XMLUtil;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JAPVersionInfo
extends AbstractDistributableDatabaseEntry {
    public static final String ID_BETA = "/japDevelopment.jnlp";
    public static final String ID_STABLE = "/japRelease.jnlp";
    public static final int JAP_RELEASE_VERSION = 1;
    public static final int JAP_DEVELOPMENT_VERSION = 2;
    private static final long DATABASE_TIMEOUT = Long.MAX_VALUE;
    private int m_versionInfoType;
    private String m_version;
    private Date m_releaseDate;
    private String m_jarFileName;
    private URL[] m_codeBase;
    private Boolean[] m_bIncrementalAllowed;
    private String m_lastSupportedJavaVersion;
    private long m_lastUpdate;
    private Element m_xmlStructure;
    static /* synthetic */ Class class$anon$infoservice$JAPVersionInfo;

    public static String getXmlElementName() {
        return "jnlp";
    }

    public JAPVersionInfo(Element element, int n) throws Exception {
        super(Long.MAX_VALUE);
        int n2;
        Object object;
        Object object2;
        this.m_versionInfoType = n;
        this.m_version = XMLUtil.parseAttribute((Node)element, "version", "");
        this.m_version = this.m_version.trim();
        try {
            object2 = element.getAttribute("releaseDate") + " GMT";
            try {
                this.m_releaseDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").parse((String)object2);
            }
            catch (ParseException parseException) {
                this.m_releaseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse((String)object2);
            }
        }
        catch (Exception exception) {
            this.m_releaseDate = null;
        }
        object2 = new Vector();
        Vector<Boolean> vector = new Vector<Boolean>();
        ((Vector)object2).addElement(new URL(element.getAttribute("codebase")));
        vector.addElement(new Boolean(true));
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "resources");
        NodeList nodeList = element2.getElementsByTagName("jar");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            try {
                object = (Element)nodeList.item(i);
                String string = object.getAttribute("part");
                if (!string.equals("jap")) continue;
                this.m_jarFileName = object.getAttribute("href");
                Node node = XMLUtil.getFirstChildByName((Node)object, "codebases");
                if (node == null) break;
                Node node2 = XMLUtil.getFirstChildByName(node, "codebase");
                while (node2 != null) {
                    try {
                        URL uRL = new URL(XMLUtil.parseValue(node2, null));
                        Boolean bl = new Boolean(XMLUtil.parseAttribute(node2, "incremental", true));
                        if (((Vector)object2).contains(uRL)) {
                            vector.removeElementAt(((Vector)object2).indexOf(uRL));
                            ((Vector)object2).removeElement(uRL);
                        }
                        ((Vector)object2).addElement(uRL);
                        vector.addElement(bl);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    node2 = XMLUtil.getNextSiblingByName(node2, "codebase");
                }
                break;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.m_codeBase = new URL[((Vector)object2).size()];
        this.m_bIncrementalAllowed = new Boolean[vector.size()];
        ((Vector)object2).copyInto(this.m_codeBase);
        vector.copyInto(this.m_bIncrementalAllowed);
        NodeList nodeList2 = XMLUtil.getElementsByTagName(element2, "j2se");
        this.m_lastSupportedJavaVersion = JavaVersionDBEntry.CURRENT_JAVA_VERSION;
        if (nodeList2 != null) {
            for (int i = 0; i < nodeList2.getLength(); ++i) {
                object = XMLUtil.parseAttribute(nodeList2.item(i), "version", JavaVersionDBEntry.CURRENT_JAVA_VERSION);
                if (this.m_lastSupportedJavaVersion.compareTo((String)object) <= 0) continue;
                this.m_lastSupportedJavaVersion = object;
            }
        }
        if ((n2 = this.m_lastSupportedJavaVersion.indexOf("+")) > 0) {
            this.m_lastSupportedJavaVersion = this.m_lastSupportedJavaVersion.substring(0, n2);
        }
        this.m_lastUpdate = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "LastUpdate"), -1L);
        if (this.m_lastUpdate == -1L) {
            this.m_lastUpdate = System.currentTimeMillis();
        }
        this.m_xmlStructure = element;
    }

    public static JAPVersionInfo getRecommendedUpdate(String string, boolean bl) {
        JAPVersionInfo jAPVersionInfo = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPVersionInfo.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById(ID_STABLE);
        JAPVersionInfo jAPVersionInfo2 = (JAPVersionInfo)Database.getInstance(class$anon$infoservice$JAPVersionInfo == null ? (class$anon$infoservice$JAPVersionInfo = JAPVersionInfo.class$("anon.infoservice.JAPVersionInfo")) : class$anon$infoservice$JAPVersionInfo).getEntryById(ID_BETA);
        if (bl) {
            if (jAPVersionInfo != null && jAPVersionInfo.getJapVersion().compareTo(string) > 0) {
                return jAPVersionInfo;
            }
            return null;
        }
        if (jAPVersionInfo != null) {
            if (jAPVersionInfo2 == null && jAPVersionInfo.getJapVersion().compareTo(string) > 0) {
                return jAPVersionInfo;
            }
            if (jAPVersionInfo2 != null && jAPVersionInfo.getJapVersion().compareTo(string) > 0 && jAPVersionInfo2.getJapVersion().compareTo(string) > 0) {
                return jAPVersionInfo;
            }
        }
        if (jAPVersionInfo2 != null && jAPVersionInfo2.getJapVersion().compareTo(string) > 0) {
            return jAPVersionInfo2;
        }
        return null;
    }

    public boolean isJavaVersionStillSupported() {
        return JavaVersionDBEntry.CURRENT_JAVA_VERSION.compareTo(this.m_lastSupportedJavaVersion) >= 0;
    }

    public String getSupportedJavaVersion() {
        return this.m_lastSupportedJavaVersion;
    }

    public String getId() {
        String string = ID_STABLE;
        if (this.m_versionInfoType == 2) {
            string = ID_BETA;
        }
        return string;
    }

    public long getVersionNumber() {
        return this.m_lastUpdate;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public String getJapVersion() {
        return this.m_version;
    }

    public Date getDate() {
        return this.m_releaseDate;
    }

    public URL[] getCodeBase() {
        URL[] arruRL = new URL[this.m_codeBase.length];
        System.arraycopy(this.m_codeBase, 0, arruRL, 0, this.m_codeBase.length);
        return arruRL;
    }

    public boolean isIncrementalAllowed(int n) {
        return this.m_bIncrementalAllowed[n];
    }

    public String getJAPJarFileName() {
        return this.m_jarFileName;
    }

    public String getPostFile() {
        return this.getId();
    }

    public Element getXmlStructure() {
        return this.m_xmlStructure;
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

