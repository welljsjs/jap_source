/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.MyRSA;
import anon.crypto.MyRSAPublicKey;
import anon.util.Base64;
import anon.util.IMiscPasswordReader;
import anon.util.SingleStringPasswordReader;
import anon.util.XMLUtil;
import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class XMLEncryption {
    public static final String XML_ELEMENT_NAME = "EncryptedData";
    private static final int SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 1000;

    private XMLEncryption() {
    }

    public static Element encryptElement(Element element, String string) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] arrby = new byte[20];
        secureRandom.nextBytes(arrby);
        byte[] arrby2 = null;
        byte[] arrby3 = null;
        try {
            arrby2 = XMLUtil.toString(element).getBytes();
            arrby3 = XMLEncryption.codeDataCTS(true, arrby2, XMLEncryption.generatePBEKey(string, arrby));
        }
        catch (Exception exception) {
            throw new IOException("Exception while encrypting: " + exception.toString());
        }
        Document document = element.getOwnerDocument();
        Node node = element.getParentNode();
        Element element2 = document.createElement(XML_ELEMENT_NAME);
        element2.setAttribute("Type", "http://www.w3.org/2001/04/xmlenc#Element");
        element2.setAttribute("xmlns", "http://www.w3.org/2001/04/xmlenc#");
        Element element3 = document.createElement("EncryptionMethod");
        element3.setAttribute("Algorithm", "aes-cts");
        element2.appendChild(element3);
        Element element4 = document.createElement("ds:KeyInfo");
        element4.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        Element element5 = document.createElement("ds:Salt");
        XMLUtil.setValue((Node)element5, Base64.encodeBytes(arrby));
        element4.appendChild(element5);
        element2.appendChild(element4);
        Element element6 = document.createElement("CipherData");
        element2.appendChild(element6);
        Element element7 = document.createElement("CipherValue");
        element6.appendChild(element7);
        XMLUtil.setValue((Node)element7, Base64.encodeBytes(arrby3));
        node.removeChild(element);
        node.appendChild(element2);
        return element2;
    }

    private static CipherParameters generatePBEKey(String string, byte[] arrby) {
        PKCS12PBEParams pKCS12PBEParams = new PKCS12PBEParams(arrby, 1000);
        PKCS12ParametersGenerator pKCS12ParametersGenerator = new PKCS12ParametersGenerator(new SHA1Digest());
        pKCS12ParametersGenerator.init(PBEParametersGenerator.PKCS12PasswordToBytes(string.toCharArray()), pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue());
        return pKCS12ParametersGenerator.generateDerivedParameters(128);
    }

    private static byte[] codeDataCTS(boolean bl, byte[] arrby, CipherParameters cipherParameters) throws Exception {
        CTSBlockCipher cTSBlockCipher = new CTSBlockCipher(new AESFastEngine());
        cTSBlockCipher.init(bl, cipherParameters);
        byte[] arrby2 = new byte[((BufferedBlockCipher)cTSBlockCipher).getOutputSize(arrby.length)];
        int n = 0;
        if (arrby.length != 0) {
            n = ((BufferedBlockCipher)cTSBlockCipher).processBytes(arrby, 0, arrby.length, arrby2, 0);
        }
        ((BufferedBlockCipher)cTSBlockCipher).doFinal(arrby2, n);
        return arrby2;
    }

    private static byte[] codeDataCBCwithHMAC(boolean bl, byte[] arrby, CipherParameters cipherParameters, CipherParameters cipherParameters2) throws Exception {
        PaddedBufferedBlockCipher paddedBufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
        paddedBufferedBlockCipher.init(bl, cipherParameters);
        byte[] arrby2 = new byte[paddedBufferedBlockCipher.getOutputSize(arrby.length)];
        int n = 0;
        if (arrby.length != 0) {
            n = paddedBufferedBlockCipher.processBytes(arrby, 0, arrby.length, arrby2, 0);
        }
        n += paddedBufferedBlockCipher.doFinal(arrby2, n);
        if (!bl && n != arrby2.length) {
            byte[] arrby3 = new byte[n];
            System.arraycopy(arrby2, 0, arrby3, 0, n);
            arrby2 = arrby3;
        }
        return arrby2;
    }

    public static Element decryptElement(Element element, String string) throws Exception {
        return XMLEncryption.decryptElement(element, new SingleStringPasswordReader(string));
    }

    public static Element decryptElement(Element element, IMiscPasswordReader iMiscPasswordReader) throws Exception {
        String string;
        String string2;
        Document document = element.getOwnerDocument();
        Node node = element.getParentNode();
        if (iMiscPasswordReader == null) {
            iMiscPasswordReader = new SingleStringPasswordReader("");
        }
        if ((string2 = element.getAttribute("Type")) == null || !string2.equals("http://www.w3.org/2001/04/xmlenc#Element")) {
            throw new IOException("Wrong XML Format");
        }
        Element element2 = (Element)XMLUtil.getFirstChildByName(element, "CipherData");
        element2 = (Element)XMLUtil.getFirstChildByName(element2, "CipherValue");
        byte[] arrby = Base64.decode(XMLUtil.parseValue((Node)element2, (String)null));
        Element element3 = (Element)XMLUtil.getFirstChildByName(element, "ds:KeyInfo");
        Element element4 = (Element)XMLUtil.getFirstChildByName(element3, "ds:Salt");
        byte[] arrby2 = Base64.decode(XMLUtil.parseValue((Node)element4, (String)null));
        byte[] arrby3 = null;
        Document document2 = null;
        Element element5 = null;
        Exception exception = null;
        while ((string = iMiscPasswordReader.readPassword(null)) != null) {
            try {
                arrby3 = XMLEncryption.codeDataCTS(false, arrby, XMLEncryption.generatePBEKey(string, arrby2));
                document2 = XMLUtil.toXMLDocument(arrby3);
                element5 = (Element)XMLUtil.importNode(document, document2.getDocumentElement(), true);
                exception = null;
                break;
            }
            catch (Exception exception2) {
                exception = exception2;
            }
        }
        if (exception != null) {
            throw new IOException("Exception while decrypting (maybe password wrong): " + exception.toString());
        }
        node.removeChild(element);
        node.appendChild(element5);
        return element5;
    }

    public static boolean encryptElement(Element element, MyRSAPublicKey myRSAPublicKey) {
        Node node = XMLEncryption.getEncryptedElement(element, myRSAPublicKey);
        if (node == null) {
            return false;
        }
        Node node2 = element.getParentNode();
        node2.removeChild(element);
        node2.appendChild(node);
        return true;
    }

    public static Node getEncryptedElement(Element element, MyRSAPublicKey myRSAPublicKey) {
        byte[] arrby;
        byte[] arrby2 = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(arrby2);
        ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(arrby2, 0, 16), arrby2, 16, 16);
        byte[] arrby3 = null;
        byte[] arrby4 = null;
        try {
            arrby3 = XMLUtil.toString(element).getBytes();
            arrby4 = XMLEncryption.codeDataCBCwithHMAC(true, arrby3, parametersWithIV, null);
        }
        catch (Exception exception) {
            return null;
        }
        MyRSA myRSA = new MyRSA();
        try {
            myRSA.init(myRSAPublicKey);
            arrby = myRSA.processBlockOAEP(arrby2, 0, arrby2.length);
        }
        catch (Exception exception) {
            return null;
        }
        Document document = element.getOwnerDocument();
        Element element2 = document.createElement(XML_ELEMENT_NAME);
        element2.setAttribute("Type", "http://www.w3.org/2001/04/xmlenc#Element");
        element2.setAttribute("xmlns", "http://www.w3.org/2001/04/xmlenc#");
        Element element3 = document.createElement("EncryptionMethod");
        element3.setAttribute("Algorithm", "http://www.w3.org/2001/04/xmlenc#aes128-cbc");
        element2.appendChild(element3);
        Element element4 = document.createElement("ds:KeyInfo");
        element4.setAttribute("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        element2.appendChild(element4);
        Element element5 = document.createElement("EncryptedKey");
        element4.appendChild(element5);
        element3 = document.createElement("EncryptionMethod");
        element3.setAttribute("Algorithm", "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p");
        element5.appendChild(element3);
        Element element6 = document.createElement("CipherData");
        element5.appendChild(element6);
        Element element7 = document.createElement("CipherValue");
        element6.appendChild(element7);
        XMLUtil.setValue((Node)element7, Base64.encodeBytes(arrby));
        element6 = document.createElement("CipherData");
        element2.appendChild(element6);
        element7 = document.createElement("CipherValue");
        element6.appendChild(element7);
        XMLUtil.setValue((Node)element7, Base64.encodeBytes(arrby4));
        return element2;
    }
}

