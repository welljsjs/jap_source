/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.xml.XMLBalance;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.util.Calendar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLVolumePlan
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "VolumePlan";
    private Document m_docTheVolumePlan;
    private String m_name;
    private String m_displayName;
    private int m_price;
    private double m_priceBC;
    private boolean m_volumeLimited;
    private boolean m_durationLimited;
    private long m_volumeKbytes;
    private int m_duration;
    private String m_durationUnit;
    private boolean m_bIsMonthlyVolume = false;
    private String m_strSinceAnonlibVersion;
    private boolean m_bIsFree = false;
    private boolean m_bIsActive = false;
    private long m_lExtraVolume;

    public XMLVolumePlan() {
    }

    public XMLVolumePlan(String string) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        Document document = XMLUtil.readXMLDocument(byteArrayInputStream);
        this.setValues(document.getDocumentElement());
        this.m_docTheVolumePlan = document;
    }

    public XMLVolumePlan(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheVolumePlan = XMLUtil.createDocument();
        this.m_docTheVolumePlan.appendChild(XMLUtil.importNode(this.m_docTheVolumePlan, element, true));
    }

    public XMLVolumePlan(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
        this.m_docTheVolumePlan = document;
    }

    public XMLVolumePlan(String string, String string2, int n, int n2, String string3, long l, long l2, boolean bl, String string4, boolean bl2, boolean bl3, double d) {
        this.m_priceBC = d;
        this.m_strSinceAnonlibVersion = string4;
        this.m_name = string;
        this.m_bIsFree = bl2;
        this.m_bIsActive = bl3;
        this.m_displayName = string2;
        this.m_price = n;
        this.m_durationLimited = true;
        this.m_duration = n2;
        this.m_durationUnit = string3;
        this.m_volumeKbytes = l;
        this.m_lExtraVolume = l2;
        this.m_volumeLimited = true;
        this.m_bIsMonthlyVolume = bl;
        this.m_docTheVolumePlan = XMLUtil.createDocument();
        this.m_docTheVolumePlan.appendChild(this.internal_toXmlElement(this.m_docTheVolumePlan));
    }

    public XMLVolumePlan(String string, String string2, int n, boolean bl, boolean bl2, int n2, String string3, long l, String string4) {
        this.m_strSinceAnonlibVersion = string4;
        this.m_name = string;
        this.m_displayName = string2;
        this.m_price = n;
        this.m_durationLimited = bl;
        this.m_duration = n2;
        this.m_durationUnit = string3;
        this.m_volumeKbytes = l;
        this.m_volumeLimited = bl2;
        this.m_docTheVolumePlan = XMLUtil.createDocument();
        this.m_docTheVolumePlan.appendChild(this.internal_toXmlElement(this.m_docTheVolumePlan));
    }

    public String getFirstSupportedAnonlibVersion() {
        return this.m_strSinceAnonlibVersion;
    }

    public boolean isActive() {
        return this.m_bIsActive;
    }

    public boolean isFree() {
        return this.m_bIsFree;
    }

    public boolean isMonthlyVolume() {
        return this.m_bIsMonthlyVolume;
    }

    public String getName() {
        return this.m_name;
    }

    public String getDisplayName() {
        if (this.m_displayName != null && !this.m_displayName.equals("")) {
            return this.m_displayName;
        }
        return this.m_name;
    }

    public int getPrice() {
        return this.m_price;
    }

    public boolean isVolumeLimited() {
        return this.m_volumeLimited;
    }

    public boolean isDurationLimited() {
        return this.m_durationLimited;
    }

    public int getDuration() {
        return this.m_duration;
    }

    public String getDurationUnit() {
        return this.m_durationUnit;
    }

    public Calendar calculateEndDate(Calendar calendar) {
        int n = this.getDurationUnit().equals("days") || this.getDurationUnit().equals("day") ? 5 : (this.getDurationUnit().equalsIgnoreCase("weeks") || this.getDurationUnit().equalsIgnoreCase("week") ? 3 : (this.getDurationUnit().equalsIgnoreCase("years") || this.getDurationUnit().equalsIgnoreCase("year") ? 1 : 2));
        return XMLBalance.calculateEndDate(calendar, this.getDuration(), n);
    }

    public int getDurationInDays() {
        return 0;
    }

    public long getVolumeKbytes() {
        return this.m_volumeKbytes;
    }

    public long getExtraVolumeKbytes() {
        return this.m_lExtraVolume;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheVolumePlan.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    protected void setValues(Element element) throws Exception {
        if (!element.getTagName().equals(XML_ELEMENT_NAME)) {
            throw new Exception("XMLVolumePlan: wrong XML structure");
        }
        this.m_strSinceAnonlibVersion = XMLUtil.parseAttribute((Node)element, "sinceVersion", null);
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "Name");
        this.m_name = XMLUtil.parseValue((Node)element2, (String)null);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "DisplayName");
        this.m_displayName = XMLUtil.parseValue((Node)element2, (String)null);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Price");
        String string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_price = Integer.parseInt(string);
        this.m_bIsFree = XMLUtil.parseAttribute((Node)element2, "free", false);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "VolumeLimited");
        this.m_volumeLimited = XMLUtil.parseValue((Node)element2, false);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "DurationLimited");
        this.m_durationLimited = XMLUtil.parseValue((Node)element2, false);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "VolumeKbytes");
        this.m_bIsMonthlyVolume = XMLUtil.parseAttribute((Node)element2, "monthly", false);
        string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_volumeKbytes = Long.parseLong(string);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "ExtraVolumeKbytes");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_lExtraVolume = Long.parseLong(string);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "Duration");
        string = XMLUtil.parseValue((Node)element2, (String)null);
        this.m_duration = Integer.parseInt(string);
        this.m_durationUnit = XMLUtil.parseAttribute((Node)element2, "unit", "");
    }

    private Element internal_toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        if (this.m_strSinceAnonlibVersion != null) {
            XMLUtil.setAttribute(element, "sinceVersion", this.m_strSinceAnonlibVersion);
        }
        Element element2 = document.createElement("Name");
        XMLUtil.setValue((Node)element2, this.m_name);
        element.appendChild(element2);
        element2 = document.createElement("DisplayName");
        XMLUtil.setValue((Node)element2, this.m_displayName);
        element.appendChild(element2);
        element2 = document.createElement("Price");
        XMLUtil.setValue((Node)element2, this.m_price);
        element.appendChild(element2);
        XMLUtil.setAttribute(element2, "free", this.m_bIsFree);
        element2 = document.createElement("PriceBitcoins");
        XMLUtil.setValue((Node)element2, this.m_priceBC);
        element.appendChild(element2);
        element2 = document.createElement("DurationLimited");
        XMLUtil.setValue((Node)element2, this.m_durationLimited);
        element.appendChild(element2);
        element2 = document.createElement("VolumeLimited");
        XMLUtil.setValue((Node)element2, this.m_volumeLimited);
        element.appendChild(element2);
        element2 = document.createElement("ExtraVolumeKbytes");
        XMLUtil.setValue((Node)element2, this.m_lExtraVolume);
        element.appendChild(element2);
        element2 = document.createElement("VolumeKbytes");
        XMLUtil.setValue((Node)element2, this.m_volumeKbytes);
        element.appendChild(element2);
        XMLUtil.setAttribute(element2, "monthly", this.m_bIsMonthlyVolume);
        element2 = document.createElement("Duration");
        XMLUtil.setValue((Node)element2, this.m_duration);
        element2.setAttribute("unit", this.m_durationUnit);
        element.appendChild(element2);
        return element;
    }
}

