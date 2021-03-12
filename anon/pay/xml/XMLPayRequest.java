/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.pay.xml.XMLEasyCC;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.sql.Timestamp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLPayRequest
implements IXMLEncodable {
    private XMLEasyCC m_cc = null;
    private Timestamp m_balanceNewerThan = null;
    private boolean m_bIsAccountRequest;
    private boolean m_bInitialCCRequest = false;
    private int prepaidBytes = 0;
    public static final Object XML_ELEMENT_NAME = "PayRequest";

    public XMLPayRequest(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
    }

    public XMLPayRequest(byte[] arrby) throws Exception {
        Document document = XMLUtil.toXMLDocument(arrby);
        this.setValues(document.getDocumentElement());
    }

    public XMLPayRequest(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
    }

    public XMLPayRequest(Element element) throws Exception {
        this.setValues(element);
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals(XML_ELEMENT_NAME) || !element.getAttribute("version").equals("1.0")) {
            throw new Exception("PayRequest wrong format or wrong version number");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "BalanceRequest");
        if (element2 != null) {
            Element element3 = (Element)XMLUtil.getFirstChildByName(element2, "NewerThan");
            this.m_balanceNewerThan = Timestamp.valueOf(XMLUtil.parseValue((Node)element3, ""));
        } else {
            this.m_balanceNewerThan = null;
        }
        element2 = (Element)XMLUtil.getFirstChildByName(element, "CC");
        this.m_cc = element2 != null ? new XMLEasyCC(element2) : null;
        this.m_bInitialCCRequest = XMLUtil.parseAttribute((Node)element, "initialCC", false);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "PrepaidBytes");
        this.prepaidBytes = XMLUtil.parseValue((Node)element2, 0);
        element2 = (Element)XMLUtil.getFirstChildByName(element, "AccountRequest");
        this.m_bIsAccountRequest = element2 != null;
    }

    public Element toXmlElement(Document document) {
        return null;
    }

    public XMLEasyCC getCC() {
        return this.m_cc;
    }

    public Timestamp getBalanceTimestamp() {
        return this.m_balanceNewerThan;
    }

    public boolean isAccountRequest() {
        return this.m_bIsAccountRequest;
    }

    public boolean isInitialCCRequest() {
        return this.m_bInitialCCRequest;
    }

    public int getPrepaidBytes() {
        return this.prepaidBytes;
    }
}

