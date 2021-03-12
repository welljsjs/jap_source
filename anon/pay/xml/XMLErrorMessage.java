/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.AnonServerDescription;
import anon.error.AnonServiceException;
import anon.pay.PayAccount;
import anon.pay.xml.XMLAccountInfo;
import anon.pay.xml.XMLEasyCC;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLErrorMessage
extends AnonServiceException
implements IXMLEncodable {
    public static final int ERR_OK = 0;
    public static final int ERR_INTERNAL_SERVER_ERROR = 1;
    public static final int ERR_WRONG_FORMAT = 2;
    public static final int ERR_WRONG_DATA = 3;
    public static final int ERR_KEY_NOT_FOUND = 4;
    public static final int ERR_BAD_SIGNATURE = 5;
    public static final int ERR_BAD_REQUEST = 6;
    public static final int ERR_NO_ACCOUNTCERT = 7;
    public static final int ERR_NO_BALANCE = 8;
    public static final int ERR_NO_CONFIRMATION = 9;
    public static final int ERR_ACCOUNT_EMPTY = 10;
    public static final int ERR_CASCADE_LENGTH = 11;
    public static final int ERR_DATABASE_ERROR = 12;
    public static final int ERR_INSUFFICIENT_BALANCE = 13;
    public static final int ERR_NO_FLATRATE_OFFERED = 14;
    public static final int ERR_INVALID_CODE = 15;
    public static final int ERR_OUTDATED_CC = 16;
    public static final int ERR_INVALID_PRICE_CERTS = 17;
    public static final int ERR_MULTIPLE_LOGIN = 18;
    public static final int ERR_NO_RECORD_FOUND = 19;
    public static final int ERR_SUCCESS_BUT_WITH_ERRORS = 20;
    public static final int ERR_BLOCKED = 21;
    public static final int ERR_NOT_SYNCHRONIZED = 22;
    private int m_iErrorCode;
    private IXMLEncodable m_oMessageObject;
    private PayAccount m_account;
    private static final String[] m_errStrings = new String[]{"Success", "Internal Server Error", "Wrong format", "Wrong Data", "Key not found", "Bad Signature", "Bad request", "No account certificate", "No balance", "No cost confirmation", "Account is empty", "Cascade too long", "Database error", "Insufficient balance", "No flatrate offered", "Invalid code", "outdated CC", "Invalid price certificates", "multiple login is not allowed", "no record found", "operation succeeded, but there were errors", "this account is blocked", "your clock in not synchronized"};
    public static final String XML_ELEMENT_NAME = "ErrorMessage";

    public XMLErrorMessage(Document document, PayAccount payAccount, AnonServerDescription anonServerDescription) throws Exception {
        super(anonServerDescription, XMLErrorMessage.getMessage(Integer.parseInt(document.getDocumentElement().getAttribute("code")), XMLUtil.parseValue((Node)document.getDocumentElement(), "")));
        this.setValues(document.getDocumentElement(), payAccount);
    }

    public XMLErrorMessage(Element element, PayAccount payAccount, AnonServerDescription anonServerDescription) throws Exception {
        super(anonServerDescription, XMLErrorMessage.getMessage(Integer.parseInt(element.getAttribute("code")), XMLUtil.parseValue((Node)element, "")));
        this.setValues(element, payAccount);
    }

    public XMLErrorMessage(int n, String string) {
        this(n, string, null, null);
    }

    public XMLErrorMessage(int n) {
        this(n, (PayAccount)null, (AnonServerDescription)null);
    }

    public XMLErrorMessage(int n, PayAccount payAccount) {
        this(n, payAccount, (AnonServerDescription)null);
    }

    public XMLErrorMessage(int n, String string, PayAccount payAccount, AnonServerDescription anonServerDescription) {
        super(anonServerDescription, XMLErrorMessage.getMessage(n, string));
        this.m_iErrorCode = n;
        this.m_account = payAccount;
    }

    public XMLErrorMessage(int n, String string, IXMLEncodable iXMLEncodable, PayAccount payAccount, AnonServerDescription anonServerDescription) {
        super(anonServerDescription, XMLErrorMessage.getMessage(n, string));
        this.m_iErrorCode = n;
        this.m_oMessageObject = iXMLEncodable;
        this.m_account = payAccount;
    }

    public XMLErrorMessage(int n, AnonServerDescription anonServerDescription) {
        this(n, null, null, anonServerDescription);
    }

    public XMLErrorMessage(int n, PayAccount payAccount, AnonServerDescription anonServerDescription) {
        this(n, null, payAccount, anonServerDescription);
    }

    private static String getMessage(int n, String string) {
        if (string != null) {
            return string;
        }
        if (n < 0 || n > m_errStrings.length) {
            return "Unknown message";
        }
        return m_errStrings[n];
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        element.setAttribute("code", Integer.toString(this.m_iErrorCode));
        XMLUtil.setValue((Node)element, this.getMessage());
        if (this.m_oMessageObject != null) {
            Element element2 = document.createElement("MessageObject");
            Element element3 = this.m_oMessageObject.toXmlElement(document);
            element2.appendChild(element3);
            element.appendChild(element2);
        }
        return element;
    }

    public long getAccountNumber() {
        if (this.m_account == null) {
            return 0L;
        }
        return this.m_account.getAccountNumber();
    }

    public PayAccount getAccount() {
        return this.m_account;
    }

    public String getPIID() {
        if (this.m_account == null) {
            return null;
        }
        return this.m_account.getPIID();
    }

    public int getXmlErrorCode() {
        return this.m_iErrorCode;
    }

    public IXMLEncodable getMessageObject() {
        return this.m_oMessageObject;
    }

    public void setMessageObject(IXMLEncodable iXMLEncodable) {
        this.m_oMessageObject = iXMLEncodable;
    }

    private void setValues(Element element, PayAccount payAccount) throws Exception {
        if (!element.getTagName().equals(XML_ELEMENT_NAME)) {
            throw new Exception("Format error: Root element wrong tagname");
        }
        this.m_iErrorCode = Integer.parseInt(element.getAttribute("code"));
        this.m_account = payAccount;
        try {
            Node node = XMLUtil.getFirstChildByName(element, "MessageObject");
            if (node == null) {
                return;
            }
            Node node2 = XMLUtil.getFirstChildByName(node, "AccountInfo");
            if (node2 != null && node2 instanceof Element) {
                this.m_oMessageObject = new XMLAccountInfo((Element)node2);
            }
            if ((node2 = XMLUtil.getFirstChildByName(node, "CC")) != null && node2 instanceof Element) {
                this.m_oMessageObject = new XMLEasyCC((Element)node2);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

