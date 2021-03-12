/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import HTTPClient.Codecs;
import HTTPClient.NVPair;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ListenerInterface;
import anon.util.IPasswordReader;
import anon.util.IXMLEncodable;
import anon.util.StoredPasswordReader;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class ProxyInterface
extends ListenerInterface
implements ImmutableProxyInterface,
IXMLEncodable,
Cloneable {
    private static String XML_USE_AUTHENTICATION = "UseAuthentication";
    private static String XML_USE_PROXY = "UseProxy";
    private static String XML_AUTHENTICATION_USER_ID = "AuthenticationUserID";
    private static String XML_AUTHENTICATION_PASSWORD = "AuthenticationPassword";
    private ListenerInterface m_listenerInterface;
    private String m_authenticationPassword = null;
    private String m_authenticationUserID = null;
    private boolean m_bUseAuthentication = false;
    private boolean m_bUseInterface = true;
    private IPasswordReader m_passwordReader;
    private static final long AUTH_PASS_CANCEL_WAIT_TIME = 6000L;
    private volatile long m_authPassLastCancelTime = 0L;
    private boolean m_bAuthPassDialogShown = false;

    public ProxyInterface(Element element, IPasswordReader iPasswordReader) throws XMLParseException {
        super(element);
        this.m_passwordReader = iPasswordReader;
        try {
            this.setAuthenticationUserID(XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_AUTHENTICATION_USER_ID), null));
        }
        catch (IllegalStateException illegalStateException) {
            this.setAuthenticationUserID(null);
        }
        try {
            this.setUseAuthentication(Boolean.valueOf(XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_USE_AUTHENTICATION), null)));
        }
        catch (IllegalStateException illegalStateException) {
            this.setUseAuthentication(false);
        }
        this.setUseInterface(Boolean.valueOf(XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_USE_PROXY), null)));
        this.m_listenerInterface = new ListenerInterface(this.getHost(), this.getPort(), this.getProtocol()){

            public String getHost() {
                return ProxyInterface.this.getHost();
            }

            public int getPort() {
                return ProxyInterface.this.getPort();
            }

            public int getProtocol() {
                return ProxyInterface.this.getProtocol();
            }
        };
    }

    public ProxyInterface(String string, int n, IPasswordReader iPasswordReader) throws IllegalArgumentException {
        this(string, n, 1, iPasswordReader);
    }

    public ProxyInterface(ListenerInterface listenerInterface, IPasswordReader iPasswordReader) throws IllegalArgumentException {
        this(listenerInterface != null ? listenerInterface.getHost() : null, listenerInterface != null ? listenerInterface.getPort() : -1, listenerInterface != null ? listenerInterface.getProtocol() : 1, iPasswordReader);
    }

    public ProxyInterface(String string, int n, int n2, IPasswordReader iPasswordReader) throws IllegalArgumentException {
        this(string, n, n2, null, iPasswordReader, false, true);
    }

    public ProxyInterface(String string, int n, String string2, String string3, IPasswordReader iPasswordReader, boolean bl, boolean bl2) throws IllegalArgumentException {
        this(string, n, ListenerInterface.recognizeProtocol(string2), string3, iPasswordReader, bl, bl2);
    }

    public ProxyInterface(String string, int n, int n2, String string2, IPasswordReader iPasswordReader, boolean bl, boolean bl2) throws IllegalArgumentException {
        super(string, n, n2);
        this.m_passwordReader = iPasswordReader;
        this.setAuthenticationUserID(string2);
        this.setUseAuthentication(bl);
        this.setUseInterface(bl2);
        this.m_listenerInterface = new ListenerInterface(this.getHost(), this.getPort(), this.getProtocol()){

            public String getHost() {
                return ProxyInterface.this.getHost();
            }

            public int getPort() {
                return ProxyInterface.this.getPort();
            }

            public int getProtocol() {
                return ProxyInterface.this.getProtocol();
            }
        };
    }

    public static String getXMLElementName() {
        return "ProxyInterface";
    }

    public static boolean isValidUserID(String string) {
        return string != null && string.trim().length() > 0;
    }

    public boolean isAuthenticationUsed() {
        return this.m_bUseAuthentication;
    }

    public boolean isAuthenticationPasswordSaveable() {
        return this.m_passwordReader instanceof StoredPasswordReader;
    }

    public boolean setUseAuthentication(boolean bl) throws IllegalStateException {
        if (!this.isAuthenticationUsed() && bl) {
            String string = null;
            if (this.m_passwordReader == null) {
                string = "No password reader!";
            }
            if (this.getProtocol() != 1 && this.getProtocol() != 3) {
                string = "Wrong protocol type!";
            }
            if (!ProxyInterface.isValidUserID(this.getAuthenticationUserID())) {
                string = "Invalid user ID!";
            }
            if (string != null) {
                throw new IllegalStateException(": Cannot set proxy authentication! " + string);
            }
        }
        this.m_bUseAuthentication = bl;
        return this.m_bUseAuthentication;
    }

    public ListenerInterface getListenerInterface() {
        return this.m_listenerInterface;
    }

    public String getAuthenticationPassword() throws IllegalStateException {
        return this.getAuthenticationPassword(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getAuthenticationPassword(boolean bl) throws IllegalStateException {
        if (!(this.m_passwordReader instanceof StoredPasswordReader) && bl) {
            return this.m_authenticationPassword;
        }
        if (this.m_passwordReader == null) {
            throw new IllegalStateException("No password reader!");
        }
        if (this.m_authPassLastCancelTime >= System.currentTimeMillis()) {
            return this.m_authenticationPassword;
        }
        ProxyInterface proxyInterface = this;
        synchronized (proxyInterface) {
            if (this.m_bAuthPassDialogShown) {
                return this.m_authenticationPassword;
            }
            this.m_bAuthPassDialogShown = true;
        }
        if (this.m_authenticationPassword == null || this.m_authenticationPassword.length() == 0) {
            this.m_authenticationPassword = this.m_passwordReader.readPassword(this);
        }
        if (this.m_authenticationPassword == null) {
            this.m_authPassLastCancelTime = System.currentTimeMillis() + 6000L;
        }
        this.m_bAuthPassDialogShown = false;
        return this.m_authenticationPassword;
    }

    public void clearAuthenticationPassword() {
        this.m_authenticationPassword = null;
    }

    public String getAuthenticationUserID() {
        return this.m_authenticationUserID;
    }

    public void setAuthenticationUserID(String string) {
        if (string != null && string.trim().length() > 0) {
            if (this.m_authenticationUserID == null || !this.m_authenticationUserID.equals(string)) {
                this.m_authenticationUserID = string;
                this.m_authenticationPassword = null;
                if (this.isAuthenticationUsed() && this.isValid()) {
                    this.getAuthenticationPassword();
                }
            }
        } else {
            this.m_authenticationUserID = null;
            this.m_authenticationPassword = null;
            this.setUseAuthentication(false);
        }
    }

    public String getProxyAuthorizationHeaderAsString() throws IllegalStateException {
        if (!this.isAuthenticationUsed()) {
            throw new IllegalStateException("Authentication mode is not activated! Unknown state!");
        }
        return "Proxy-Authorization: Basic " + Codecs.base64Encode(this.getAuthenticationUserID() + ":" + this.getAuthenticationPassword()) + "\r\n";
    }

    public NVPair getProxyAuthorizationHeader() throws IllegalStateException {
        if (!this.isAuthenticationUsed()) {
            throw new IllegalStateException("Authentication mode is not activated! Unknown state!");
        }
        return new NVPair("Proxy-Authorization", "Basic " + Codecs.base64Encode(this.getAuthenticationUserID() + ":" + this.getAuthenticationPassword()));
    }

    public Object clone() {
        ProxyInterface proxyInterface = new ProxyInterface(this.getHost(), this.getPort(), this.getProtocol(), this.m_authenticationUserID, this.m_passwordReader, this.m_bUseAuthentication, this.m_bUseInterface);
        return proxyInterface;
    }

    public boolean equals(Object object) {
        ProxyInterface proxyInterface;
        return object instanceof ProxyInterface && super.equals((Object)(proxyInterface = (ProxyInterface)object)) && Util.equals(this.getAuthenticationUserID(), proxyInterface.getAuthenticationUserID()) && this.isAuthenticationPasswordSaveable() == proxyInterface.isAuthenticationPasswordSaveable() && Util.equals(this.getAuthenticationPassword(true), proxyInterface.getAuthenticationPassword(true)) && this.isValid() == proxyInterface.isValid() && this.isAuthenticationUsed() == proxyInterface.isAuthenticationUsed();
    }

    public Element toXmlElement(Document document) {
        Element element = this.toXmlElementInternal(document, ProxyInterface.getXMLElementName());
        Element element2 = document.createElement(XML_AUTHENTICATION_USER_ID);
        element2.appendChild(document.createTextNode(this.getAuthenticationUserID()));
        Element element3 = document.createElement(XML_USE_PROXY);
        XMLUtil.setValue((Node)element3, this.isValid());
        Element element4 = document.createElement(XML_USE_AUTHENTICATION);
        XMLUtil.setValue((Node)element4, this.isAuthenticationUsed());
        if (this.m_passwordReader instanceof StoredPasswordReader) {
            Element element5 = document.createElement(XML_AUTHENTICATION_PASSWORD);
            XMLUtil.setValue((Node)element5, this.m_passwordReader.readPassword(null));
        }
        element.appendChild(element2);
        element.appendChild(element4);
        element.appendChild(element3);
        return element;
    }

    public boolean isValid() {
        return super.isValid() && this.m_bUseInterface;
    }

    public void setUseInterface(boolean bl) {
        super.setUseInterface(bl);
        this.m_bUseInterface = bl;
        if (this.isValid() && this.isAuthenticationUsed() && this.m_passwordReader != null && this.m_authenticationPassword == null) {
            this.getAuthenticationPassword();
        }
    }
}

