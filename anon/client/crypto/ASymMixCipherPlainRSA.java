/*
 * Decompiled with CFR 0.150.
 */
package anon.client.crypto;

import anon.client.crypto.IASymMixCipher;
import anon.crypto.MyRSA;
import anon.crypto.MyRSAPrivateKey;
import anon.crypto.MyRSAPublicKey;
import anon.util.Base64;
import anon.util.XMLUtil;
import java.math.BigInteger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ASymMixCipherPlainRSA
implements IASymMixCipher {
    MyRSA m_RSA = new MyRSA();
    private MyRSAPublicKey m_PublicKey = null;

    public int encrypt(byte[] arrby, int n, byte[] arrby2, int n2) {
        byte[] arrby3 = null;
        try {
            arrby3 = this.m_RSA.processBlock(arrby, n, 128);
        }
        catch (Exception exception) {
            return -1;
        }
        if (arrby3.length == 128) {
            System.arraycopy(arrby3, 0, arrby2, n2, 128);
        } else if (arrby3.length == 129) {
            System.arraycopy(arrby3, 1, arrby2, n2, 128);
        } else {
            for (int i = 0; i < 128 - arrby3.length; ++i) {
                arrby2[n2 + i] = 0;
            }
            System.arraycopy(arrby3, 0, arrby2, n2 + 128 - arrby3.length, arrby3.length);
        }
        return 128;
    }

    public int getOutputBlockSize() {
        return 128;
    }

    public int getInputBlockSize() {
        return 128;
    }

    public int getPaddingSize() {
        return 0;
    }

    public int setPublicKey(BigInteger bigInteger, BigInteger bigInteger2) {
        if (bigInteger == null || bigInteger2 == null) {
            return -1;
        }
        this.m_PublicKey = new MyRSAPublicKey(bigInteger, bigInteger2);
        try {
            this.m_RSA.init(this.m_PublicKey);
        }
        catch (Exception exception) {
            return -21;
        }
        return 0;
    }

    public int setPrivateKey(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5, BigInteger bigInteger6, BigInteger bigInteger7, BigInteger bigInteger8) {
        if (bigInteger == null || bigInteger2 == null || bigInteger3 == null || bigInteger4 == null || bigInteger5 == null || bigInteger6 == null || bigInteger7 == null || bigInteger8 == null) {
            return -1;
        }
        try {
            this.m_PublicKey = new MyRSAPublicKey(bigInteger, bigInteger2);
            MyRSAPrivateKey myRSAPrivateKey = new MyRSAPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
            this.m_RSA.init(myRSAPrivateKey);
        }
        catch (Exception exception) {
            return -21;
        }
        return 0;
    }

    public int setPublicKey(Element element) {
        try {
            Element element2 = (Element)XMLUtil.getFirstChildByName(element, "RSAKeyValue");
            BigInteger bigInteger = this.getBigIntegerFromXml(element2, "Modulus");
            BigInteger bigInteger2 = this.getBigIntegerFromXml(element2, "Exponent");
            return this.setPublicKey(bigInteger, bigInteger2);
        }
        catch (Exception exception) {
            return -1;
        }
    }

    public int setPrivateKey(Element element) {
        try {
            Element element2 = null;
            element2 = element.getNodeName().equals("RSAKeyPair") ? element : (Element)XMLUtil.getFirstChildByName(element, "RSAKeyPair");
            BigInteger bigInteger = this.getBigIntegerFromXml(element2, "Modulus");
            BigInteger bigInteger2 = this.getBigIntegerFromXml(element2, "Exponent");
            BigInteger bigInteger3 = this.getBigIntegerFromXml(element2, "D");
            BigInteger bigInteger4 = this.getBigIntegerFromXml(element2, "P");
            BigInteger bigInteger5 = this.getBigIntegerFromXml(element2, "Q");
            BigInteger bigInteger6 = this.getBigIntegerFromXml(element2, "DP");
            BigInteger bigInteger7 = this.getBigIntegerFromXml(element2, "DQ");
            BigInteger bigInteger8 = this.getBigIntegerFromXml(element2, "InverseQ");
            return this.setPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
        }
        catch (Exception exception) {
            return -1;
        }
    }

    public MyRSAPublicKey getPublicKey() {
        return this.m_PublicKey;
    }

    private BigInteger getBigIntegerFromXml(Element element, String string) {
        try {
            Element element2 = (Element)XMLUtil.getFirstChildByName(element, string);
            String string2 = XMLUtil.parseValue((Node)element2, (String)null);
            byte[] arrby = Base64.decode(string2);
            return new BigInteger(1, arrby);
        }
        catch (Exception exception) {
            return null;
        }
    }
}

