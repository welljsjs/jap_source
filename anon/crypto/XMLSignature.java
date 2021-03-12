/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertPath;
import anon.crypto.IMyPrivateKey;
import anon.crypto.IMyPublicKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MultiCertPath;
import anon.crypto.PKCS12;
import anon.crypto.XMLSignatureElement;
import anon.util.Base64;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SignatureException;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class XMLSignature {
    private static final String XML_ELEMENT_NAME = "Signature";
    private Vector m_signatureElements = new Vector();
    private MultiCertPath m_multiCertPath;
    private String m_xoredID;

    private XMLSignature() {
    }

    public int countSignatures() {
        return this.m_signatureElements.size();
    }

    protected Vector getSignatureElements() {
        Vector<Element> vector = new Vector<Element>();
        Enumeration enumeration = this.m_signatureElements.elements();
        while (enumeration.hasMoreElements()) {
            XMLSignatureElement xMLSignatureElement = (XMLSignatureElement)enumeration.nextElement();
            vector.addElement(xMLSignatureElement.getSignatureElement());
        }
        return vector;
    }

    public MultiCertPath getMultiCertPath() {
        return this.m_multiCertPath;
    }

    private CertPath[] getCertPaths() {
        CertPath[] arrcertPath = new CertPath[this.m_signatureElements.size()];
        for (int i = 0; i < this.m_signatureElements.size(); ++i) {
            arrcertPath[i] = ((XMLSignatureElement)this.m_signatureElements.elementAt(i)).getCertPath();
        }
        return arrcertPath;
    }

    public String getXORofSKIs() {
        return this.m_xoredID;
    }

    private void calculateXORofSKIs() {
        Vector<JAPCertificate> vector = new Vector<JAPCertificate>();
        Enumeration enumeration = this.m_signatureElements.elements();
        while (enumeration.hasMoreElements()) {
            vector.addElement(((XMLSignatureElement)enumeration.nextElement()).getCertPath().getFirstCertificate());
        }
        this.m_xoredID = JAPCertificate.calculateXORofSKIs(vector);
    }

    public boolean isVerified() {
        if (this.m_multiCertPath == null) {
            return false;
        }
        return this.m_multiCertPath.isVerified();
    }

    public static XMLSignature sign(Node node, PKCS12 pKCS12, int n) throws XMLParseException {
        return XMLSignature.signInternal(node, Util.toVector(pKCS12), n);
    }

    public synchronized boolean addCertificate(JAPCertificate jAPCertificate) {
        if (jAPCertificate != null) {
            Enumeration enumeration = this.m_signatureElements.elements();
            while (enumeration.hasMoreElements()) {
                XMLSignatureElement xMLSignatureElement = (XMLSignatureElement)enumeration.nextElement();
                if (!xMLSignatureElement.addCertificate(jAPCertificate)) continue;
                return true;
            }
        }
        return false;
    }

    public static XMLSignature sign(Node node, IMyPrivateKey iMyPrivateKey, int n) throws XMLParseException {
        return XMLSignature.signInternal(node, Util.toVector(iMyPrivateKey), n);
    }

    public static XMLSignature multiSign(Node node, Vector vector, int n) throws XMLParseException {
        return XMLSignature.signInternal(node, vector, n);
    }

    public static String getHashValueOfElement(Node node) {
        byte[] arrby = null;
        try {
            arrby = MessageDigest.getInstance("SHA-1").digest(XMLSignature.toCanonical(node));
        }
        catch (Exception exception) {
            LogHolder.log(4, LogType.PAY, "could not create hash value of node");
            return null;
        }
        return Base64.encode(arrby, false);
    }

    public static String getEncodedHashValue(Element element) {
        return XMLSignature.getHashValueOfElement(element);
    }

    private static XMLSignature signInternal(Node node, Vector vector, int n) throws XMLParseException {
        XMLSignature xMLSignature;
        block16: {
            Element element;
            PKCS12 pKCS12 = null;
            if (node == null || vector == null || vector.size() == 0) {
                return null;
            }
            if (node instanceof Document) {
                element = ((Document)node).getDocumentElement();
            } else if (node instanceof Element) {
                element = (Element)node;
            } else {
                return null;
            }
            xMLSignature = new XMLSignature();
            Vector vector2 = XMLSignature.removeSignatureFromInternal(element);
            byte[] arrby = XMLSignature.toCanonical(element);
            SHA1Digest sHA1Digest = new SHA1Digest();
            sHA1Digest.update(arrby, 0, arrby.length);
            byte[] arrby2 = new byte[sHA1Digest.getDigestSize()];
            sHA1Digest.doFinal(arrby2, 0);
            Enumeration enumeration = vector.elements();
            try {
                while (enumeration.hasMoreElements()) {
                    IMyPrivateKey iMyPrivateKey;
                    Object e = enumeration.nextElement();
                    if (e instanceof IMyPrivateKey) {
                        pKCS12 = null;
                        iMyPrivateKey = (IMyPrivateKey)e;
                    } else {
                        pKCS12 = (PKCS12)e;
                        iMyPrivateKey = pKCS12.getPrivateKey();
                    }
                    XMLSignatureElement xMLSignatureElement = new XMLSignatureElement(xMLSignature, element, iMyPrivateKey, arrby2);
                    if (pKCS12 != null) {
                        xMLSignatureElement.addCertificate(pKCS12.getX509Certificate());
                    }
                    xMLSignature.m_signatureElements.addElement(xMLSignatureElement);
                    xMLSignatureElement.verify(node, n, new Vector());
                }
                if (n == 0) break block16;
                try {
                    xMLSignature.m_multiCertPath = new MultiCertPath(xMLSignature.getCertPaths(), n);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    LogHolder.log(2, LogType.CRYPTO, illegalArgumentException);
                    return null;
                }
                xMLSignature.calculateXORofSKIs();
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.CRYPTO, "Could not sign XML document!", exception);
                if (xMLSignature.countSignatures() != 0) {
                    XMLSignature.removeSignatureFromInternal(element);
                }
                if (vector2 != null) {
                    Enumeration enumeration2 = vector2.elements();
                    while (enumeration2.hasMoreElements()) {
                        element.appendChild((Element)enumeration2.nextElement());
                    }
                }
                return null;
            }
        }
        return xMLSignature;
    }

    public static XMLSignature getVerified(Node node, int n, Vector vector) throws XMLParseException, SignatureException {
        XMLSignature xMLSignature = XMLSignature.findXMLSignature(node);
        if (xMLSignature == null) {
            LogHolder.log(7, LogType.CRYPTO, "Could not find the <Signature> node!");
            return null;
        }
        Enumeration enumeration = xMLSignature.m_signatureElements.elements();
        while (enumeration.hasMoreElements()) {
            XMLSignatureElement xMLSignatureElement = (XMLSignatureElement)enumeration.nextElement();
            if (xMLSignatureElement.verify(node, n, vector)) continue;
            throw new SignatureException("No verifier for a Signature found!");
        }
        try {
            xMLSignature.m_multiCertPath = new MultiCertPath(xMLSignature.getCertPaths(), n);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            LogHolder.log(6, LogType.CRYPTO, illegalArgumentException);
            return null;
        }
        xMLSignature.calculateXORofSKIs();
        return xMLSignature;
    }

    public static boolean verifyFast(Node node, Vector vector) {
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            IMyPublicKey iMyPublicKey = (IMyPublicKey)enumeration.nextElement();
            if (!XMLSignature.verifyFast(node, iMyPublicKey)) continue;
            return true;
        }
        return false;
    }

    public static boolean verifyFast(Node node, IMyPublicKey iMyPublicKey) {
        try {
            return XMLSignature.verify(node, iMyPublicKey) != null;
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.CRYPTO, throwable);
            return false;
        }
    }

    public static XMLSignature verify(Node node, IMyPublicKey iMyPublicKey) throws XMLParseException {
        XMLSignature xMLSignature = XMLSignature.findXMLSignature(node);
        if (xMLSignature == null) {
            LogHolder.log(3, LogType.CRYPTO, "No signature node found!");
            return null;
        }
        Enumeration enumeration = xMLSignature.m_signatureElements.elements();
        while (enumeration.hasMoreElements()) {
            try {
                XMLSignatureElement xMLSignatureElement = (XMLSignatureElement)enumeration.nextElement();
                if (!xMLSignatureElement.verifyFast(node, iMyPublicKey)) continue;
                return xMLSignature;
            }
            catch (Throwable throwable) {
            }
        }
        return null;
    }

    public static XMLSignature getUnverified(Node node) throws XMLParseException {
        if (node == null) {
            return null;
        }
        XMLSignature xMLSignature = XMLSignature.findXMLSignature(node);
        return xMLSignature;
    }

    public static boolean removeSignatureFrom(Node node) {
        return XMLSignature.removeSignatureFromInternal(node) != null;
    }

    private static Vector removeSignatureFromInternal(Node node) {
        Node node2;
        Element element;
        Vector<Element> vector = new Vector<Element>();
        Element element2 = null;
        if (node instanceof Document) {
            element = ((Document)node).getDocumentElement();
        } else if (node instanceof Element) {
            element = (Element)node;
        } else {
            return null;
        }
        while ((node2 = XMLUtil.getFirstChildByName(element, XML_ELEMENT_NAME)) != null) {
            try {
                element2 = (Element)element.removeChild(node2);
                vector.addElement(element2);
            }
            catch (ClassCastException classCastException) {}
        }
        if (vector.size() == 0) {
            return null;
        }
        return vector;
    }

    private static XMLSignature findXMLSignature(Node node) throws XMLParseException {
        Element element;
        if (node == null) {
            throw new XMLParseException("##__null__##");
        }
        if (node instanceof Document) {
            element = ((Document)node).getDocumentElement();
        } else if (node instanceof Element) {
            element = (Element)node;
        } else {
            return null;
        }
        Node node2 = XMLUtil.getFirstChildByName(element, XML_ELEMENT_NAME);
        XMLSignature xMLSignature = new XMLSignature();
        while (node2 != null) {
            try {
                XMLSignatureElement xMLSignatureElement = new XMLSignatureElement(xMLSignature, (Element)node2);
                xMLSignature.m_signatureElements.addElement(xMLSignatureElement);
            }
            catch (ClassCastException classCastException) {
                // empty catch block
            }
            node2 = XMLUtil.getNextSiblingByName(node2, XML_ELEMENT_NAME);
        }
        if (xMLSignature.m_signatureElements.size() == 0) {
            return null;
        }
        return xMLSignature;
    }

    public void clearCertificates() {
        Enumeration enumeration = this.m_signatureElements.elements();
        while (enumeration.hasMoreElements()) {
            XMLSignatureElement xMLSignatureElement = (XMLSignatureElement)enumeration.nextElement();
            xMLSignatureElement.clearCertificates();
        }
    }

    public static byte[] toCanonical(Node node, Vector vector) throws XMLParseException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (XMLSignature.makeCanonical(node, byteArrayOutputStream, false, vector, false, "UTF8") == -1) {
            throw new XMLParseException(node.getNodeName(), "Could not make the node canonical!");
        }
        try {
            byteArrayOutputStream.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] toCanonicalDeprecated(Node node) {
        if (node == null || node.getPreviousSibling() == null) {
            return null;
        }
        Node node2 = node.getParentNode();
        node2.removeChild(node);
        byte[] arrby = XMLUtil.toByteArray(node2.getOwnerDocument());
        node2.appendChild(node);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeShort(arrby.length);
            dataOutputStream.flush();
            byteArrayOutputStream.write(arrby);
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException iOException) {
            LogHolder.log(5, LogType.CRYPTO, "Could not make xml data canonical!", iOException);
            return null;
        }
    }

    public static byte[] toCanonical(Node node) throws XMLParseException {
        return XMLSignature.toCanonical(node, false);
    }

    public static byte[] toCanonical(Node node, boolean bl) throws XMLParseException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (XMLSignature.makeCanonical(node, byteArrayOutputStream, false, null, bl) == -1) {
            throw new XMLParseException(node.getNodeName(), "Could not make the node canonical!");
        }
        try {
            byteArrayOutputStream.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String toCanonicalString(Element element) {
        try {
            byte[] arrby = XMLSignature.toCanonical(element);
            return new String(arrby);
        }
        catch (Exception exception) {
            return "canonicalization error";
        }
    }

    private static int makeCanonical(Node node, OutputStream outputStream, boolean bl, Node node2) {
        return XMLSignature.makeCanonical(node, outputStream, bl, node2, false);
    }

    private static int makeCanonical(Node node, OutputStream outputStream, boolean bl, Node node2, boolean bl2) {
        return XMLSignature.makeCanonical(node, outputStream, bl, Util.toVector(node2), bl2, "UTF8");
    }

    private static int makeCanonical(Node node, OutputStream outputStream, boolean bl, Vector vector, boolean bl2, String string) {
        try {
            if (node == null) {
                return 0;
            }
            if (node instanceof Document) {
                if (bl2) {
                    outputStream.write(XMLUtil.createDocumentStructure());
                    outputStream.write(10);
                }
                node = ((Document)node).getDocumentElement();
            }
            if (vector != null && vector.contains(node)) {
                return 0;
            }
            if (node.getNodeType() == 1) {
                Object object;
                Element element = (Element)node;
                outputStream.write(60);
                if (string != null) {
                    outputStream.write(element.getNodeName().getBytes(string));
                } else {
                    outputStream.write(element.getNodeName().getBytes());
                }
                NamedNodeMap namedNodeMap = element.getAttributes();
                if (namedNodeMap.getLength() > 0) {
                    int n;
                    object = new String[namedNodeMap.getLength()];
                    String[] arrstring = new String[namedNodeMap.getLength()];
                    for (n = 0; n < namedNodeMap.getLength(); ++n) {
                        object[n] = namedNodeMap.item(n).getNodeName();
                        arrstring[n] = namedNodeMap.item(n).getNodeValue();
                    }
                    Util.sort(object, arrstring);
                    for (n = 0; n < namedNodeMap.getLength(); ++n) {
                        outputStream.write(32);
                        if (string != null) {
                            outputStream.write(object[n].getBytes(string));
                        } else {
                            outputStream.write(object[n].getBytes());
                        }
                        outputStream.write(61);
                        outputStream.write(34);
                        if (string != null) {
                            outputStream.write(arrstring[n].getBytes(string));
                        } else {
                            outputStream.write(arrstring[n].getBytes());
                        }
                        outputStream.write(34);
                    }
                }
                outputStream.write(62);
                if (element.hasChildNodes() && XMLSignature.makeCanonical(element.getFirstChild(), outputStream, true, vector, bl2, string) == -1) {
                    return -1;
                }
                outputStream.write(60);
                outputStream.write(47);
                if (string != null) {
                    outputStream.write(element.getNodeName().getBytes(string));
                } else {
                    outputStream.write(element.getNodeName().getBytes());
                }
                outputStream.write(62);
                if (bl && XMLSignature.makeCanonical((Node)(object = XMLUtil.getNextSibling(element)), outputStream, true, vector, bl2, string) == -1) {
                    return -1;
                }
            } else {
                if (node.getNodeType() == 3) {
                    String string2 = node.getNodeValue();
                    if (!bl2) {
                        string2 = string2.trim();
                    }
                    for (int i = 0; i < XMLUtil.SPECIAL_CHARS.length; ++i) {
                        string2 = Util.replaceAll(string2, XMLUtil.SPECIAL_CHARS[i], XMLUtil.ENTITIES[i], (String[])(XMLUtil.SPECIAL_CHARS[i].equals("&") ? XMLUtil.ENTITIES : null));
                    }
                    if (string != null) {
                        outputStream.write(string2.getBytes(string));
                    } else {
                        outputStream.write(string2.getBytes());
                    }
                    if (XMLSignature.makeCanonical(XMLUtil.getNextSibling(node), outputStream, true, vector, bl2, string) == -1) {
                        return -1;
                    }
                    return 0;
                }
                if (node.getNodeType() == 8) {
                    if (bl2) {
                        if (string != null) {
                            outputStream.write("<!--".getBytes(string));
                            outputStream.write(node.getNodeValue().getBytes(string));
                            outputStream.write("-->\n".getBytes(string));
                        } else {
                            outputStream.write("<!--".getBytes());
                            outputStream.write(node.getNodeValue().getBytes());
                            outputStream.write("-->\n".getBytes());
                        }
                    }
                    if (XMLSignature.makeCanonical(XMLUtil.getNextSibling(node), outputStream, true, vector, bl2, string) == -1) {
                        return -1;
                    }
                    return 0;
                }
                return -1;
            }
            return 0;
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.MISC, "Error while making canonical XML", throwable);
            return -1;
        }
    }

    public Element[] getXMLElements(Document document) {
        int n;
        Vector<Element> vector = new Vector<Element>();
        for (n = 0; n < this.m_signatureElements.size(); ++n) {
            Element element = ((XMLSignatureElement)this.m_signatureElements.elementAt(n)).toXmlElement(document);
            if (element == null) continue;
            vector.addElement(element);
        }
        Element[] arrelement = new Element[vector.size()];
        for (n = 0; n < vector.size(); ++n) {
            arrelement[n] = (Element)vector.elementAt(n);
        }
        return arrelement;
    }
}

