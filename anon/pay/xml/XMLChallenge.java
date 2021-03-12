/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.util.Base64;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLChallenge
implements IXMLEncodable {
    public static final String TYPE_PAYMENT_INSTANCE = "PaymentInstance";
    public static final String TYPE_MIX = "Mix";
    private byte[] m_arbChallenge;
    private int m_prepaidBytes;
    public static final String XML_ELEMENT_NAME = "Challenge";
    private String strID;
    private String strType;

    public XMLChallenge(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
    }

    public XMLChallenge(Element element) throws Exception {
        this.setValues(element);
    }

    public XMLChallenge(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
    }

    public XMLChallenge(byte[] arrby, String string, String string2) {
        this.strID = string;
        this.strType = string2;
        this.m_arbChallenge = arrby;
    }

    private void setValues(Element element) throws Exception {
        if (!element.getTagName().equals(XML_ELEMENT_NAME)) {
            throw new Exception("XMLChallenge wrong XML structure");
        }
        this.strID = XMLUtil.parseAttribute((Node)element, "id", null);
        this.strType = XMLUtil.parseAttribute((Node)element, "type", null);
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "DontPanic");
        this.m_arbChallenge = Base64.decode(XMLUtil.parseValue((Node)element2, ""));
        this.m_prepaidBytes = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "PrepaidBytes"), 0);
    }

    public String getType() {
        return this.strType;
    }

    public String getId() {
        return this.strID;
    }

    public int getPrepaidBytes() {
        return this.m_prepaidBytes;
    }

    public byte[] getChallengeForSigning() {
        byte[] arrby = new byte[this.m_arbChallenge.length];
        System.arraycopy(this.m_arbChallenge, 0, arrby, 0, arrby.length);
        return arrby;
    }

    public byte[] getChallengeForCaptcha() {
        String string = "<DontPanic>" + Base64.encodeBytes(this.m_arbChallenge) + "</DontPanic>";
        return string.getBytes();
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("id", this.strID);
        element.setAttribute("type", this.strType);
        Element element2 = document.createElement("DontPanic");
        element.appendChild(element2);
        XMLUtil.setValue((Node)element2, Base64.encodeBytes(this.m_arbChallenge));
        return element;
    }
}

