/*
 * Decompiled with CFR 0.150.
 */
package anon.util.captcha;

import anon.crypto.MyAES;
import anon.util.Base64;
import anon.util.ZLibTools;
import anon.util.captcha.BinaryImageExtractor;
import anon.util.captcha.IImageEncodedCaptcha;
import anon.util.captcha.MyImage;
import java.math.BigInteger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ZipBinaryImageCaptchaClient
implements IImageEncodedCaptcha {
    public static final String CAPTCHA_DATA_FORMAT = "ZIP_BINARY_IMAGE";
    private MyImage m_captchaImage;
    private int m_captchaKeyBits;
    private int m_extraKeyBits;
    private String m_characterSet;
    private int m_characterNumber;
    private byte[] m_encodedData;

    public ZipBinaryImageCaptchaClient(Element element) throws Exception {
        NodeList nodeList = element.getElementsByTagName("CaptchaDataFormat");
        if (nodeList.getLength() == 0) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error in XML structure (CaptchaDataFormat node).");
        }
        Element element2 = (Element)nodeList.item(0);
        if (!CAPTCHA_DATA_FORMAT.equals(element2.getFirstChild().getNodeValue())) {
            throw new Exception("ZipBinaryImageCaptchaClient: Wrong captcha format.");
        }
        NodeList nodeList2 = element.getElementsByTagName("CaptchaData");
        if (nodeList2.getLength() == 0) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error in XML structure. (CaptchaData node).");
        }
        Element element3 = (Element)nodeList2.item(0);
        byte[] arrby = Base64.decode(element3.getFirstChild().getNodeValue());
        byte[] arrby2 = ZLibTools.decompress(arrby);
        if (arrby2 == null) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error while decompressing the captcha data.");
        }
        this.m_captchaImage = BinaryImageExtractor.binaryToImage(arrby2);
        if (this.m_captchaImage == null) {
            throw new Exception("ZipBinaryImageCaptchaClient: The image is invalid.");
        }
        NodeList nodeList3 = element.getElementsByTagName("DataCipher");
        if (nodeList3.getLength() == 0) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error in XML structure. (DataCipher node).");
        }
        Element element4 = (Element)nodeList3.item(0);
        this.m_encodedData = Base64.decode(element4.getFirstChild().getNodeValue());
        NodeList nodeList4 = element.getElementsByTagName("CaptchaKeyBits");
        if (nodeList4.getLength() == 0) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error in XML structure. (CaptchaKeyBits node).");
        }
        Element element5 = (Element)nodeList4.item(0);
        this.m_captchaKeyBits = Integer.parseInt(element5.getFirstChild().getNodeValue());
        NodeList nodeList5 = element.getElementsByTagName("ExtraKeyBits");
        if (nodeList5.getLength() == 0) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error in XML structure. (ExtraKeyBits node).");
        }
        Element element6 = (Element)nodeList5.item(0);
        this.m_extraKeyBits = Integer.parseInt(element6.getFirstChild().getNodeValue());
        NodeList nodeList6 = element.getElementsByTagName("CaptchaCharacters");
        if (nodeList6.getLength() == 0) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error in XML structure. (CaptchaCharacters node).");
        }
        Element element7 = (Element)nodeList6.item(0);
        this.m_characterSet = element7.getFirstChild().getNodeValue();
        NodeList nodeList7 = element.getElementsByTagName("CaptchaCharacterNumber");
        if (nodeList7.getLength() == 0) {
            throw new Exception("ZipBinaryImageCaptchaClient: Error in XML structure. (CaptchaCharacterNumber node).");
        }
        Element element8 = (Element)nodeList7.item(0);
        this.m_characterNumber = Integer.parseInt(element8.getFirstChild().getNodeValue());
    }

    public MyImage getImage() {
        return this.m_captchaImage;
    }

    public String getCharacterSet() {
        return this.m_characterSet;
    }

    public int getCharacterNumber() {
        return this.m_characterNumber;
    }

    public byte[] solveCaptcha(String string, byte[] arrby) throws Exception {
        int n;
        int n2;
        if (string.length() != this.m_characterNumber) {
            throw new Exception("ZipBinaryImageCaptchaClient: solveCaptcha: The specified key has an invalid size.");
        }
        BigInteger bigInteger = new BigInteger(Integer.toString(this.m_characterSet.length()));
        BigInteger bigInteger2 = new BigInteger("0");
        for (int i = 0; i < this.m_characterNumber; ++i) {
            n2 = this.m_characterSet.indexOf(string.substring(i, i + 1));
            if (n2 == -1) {
                throw new Exception("ZipBinaryImageCaptchaClient: solveCaptcha: The specified key contains invalid characters.");
            }
            BigInteger bigInteger3 = new BigInteger(Integer.toString(n2));
            bigInteger2 = bigInteger2.multiply(bigInteger).add(bigInteger3);
        }
        byte[] arrby2 = new byte[this.m_captchaKeyBits / 8];
        for (n2 = 0; n2 < arrby2.length; ++n2) {
            arrby2[n2] = 0;
        }
        byte[] arrby3 = bigInteger2.toByteArray();
        int n3 = Math.min(arrby2.length, arrby3.length);
        System.arraycopy(arrby3, arrby3.length - n3, arrby2, arrby2.length - n3, n3);
        byte[] arrby4 = null;
        int n4 = this.m_extraKeyBits % 8;
        arrby4 = n4 == 0 ? new byte[this.m_extraKeyBits / 8] : new byte[this.m_extraKeyBits / 8 + 1];
        for (n = 0; n < arrby4.length; ++n) {
            arrby4[n] = 0;
        }
        n = 1;
        do {
            int n5;
            byte[] arrby5 = new byte[16];
            for (int i = 0; i < arrby5.length; ++i) {
                arrby5[i] = 0;
            }
            System.arraycopy(arrby2, 0, arrby5, arrby5.length - arrby2.length, arrby2.length);
            System.arraycopy(arrby4, 0, arrby5, arrby5.length - arrby2.length - arrby4.length, arrby4.length);
            MyAES myAES = new MyAES();
            myAES.init(false, arrby5);
            int n6 = this.m_encodedData.length;
            int n7 = n6 / 16;
            byte[] arrby6 = new byte[16];
            byte[] arrby7 = new byte[16];
            byte[] arrby8 = new byte[n7 * 16];
            for (n5 = 0; n5 < n7; ++n5) {
                System.arraycopy(this.m_encodedData, n5 * 16, arrby7, 0, 16);
                arrby6 = myAES.processBlockECB(arrby7);
                System.arraycopy(arrby6, 0, arrby8, n5 * 16, 16);
            }
            n = 1;
            for (n5 = 0; n5 < arrby.length; ++n5) {
                if (arrby8[n5] == arrby[n5]) continue;
                n = 0;
            }
            if (n == 0) {
                try {
                    arrby4 = this.generateNextKey(arrby4, n4);
                }
                catch (Exception exception) {
                    return null;
                }
            } else {
                return arrby8;
            }
        } while (n == 0);
        return null;
    }

    private byte[] generateNextKey(byte[] arrby, int n) throws Exception {
        n %= 8;
        byte[] arrby2 = new byte[arrby.length];
        boolean bl = true;
        for (int i = arrby2.length - 1; i >= 0; --i) {
            byte by = arrby[i];
            if (bl) {
                by = (byte)(by + 1);
                if (i != 0 || n == 0) {
                    if (by != 0) {
                        bl = false;
                    }
                } else {
                    int n2 = 255;
                    if ((by = (byte)((n2 >>>= 8 - n) & by)) != 0) {
                        bl = false;
                    }
                }
            }
            arrby2[i] = by;
        }
        if (bl) {
            throw new Exception("ZipBinaryImageCaptchaClient: generateNextKey: No more keys available.");
        }
        return arrby2;
    }
}

