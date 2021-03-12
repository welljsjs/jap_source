/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.client;

import anon.infoservice.InfoServiceHolder;
import anon.util.XMLUtil;
import anon.util.captcha.IImageEncodedCaptcha;
import anon.util.captcha.ZipBinaryImageCaptchaClient;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ForwarderInformationGrabber {
    public static final int RETURN_SUCCESS = 0;
    public static final int RETURN_INFOSERVICE_ERROR = 1;
    public static final int RETURN_UNKNOWN_ERROR = 2;
    public static final int RETURN_NO_CAPTCHA_IMPLEMENTATION = 3;
    private int m_errorCode;
    private IImageEncodedCaptcha m_captcha = null;

    public ForwarderInformationGrabber() {
        Element element = InfoServiceHolder.getInstance().getForwarder();
        if (element != null) {
            NodeList nodeList = element.getElementsByTagName("CaptchaEncoded");
            if (nodeList.getLength() > 0) {
                Element element2 = (Element)nodeList.item(0);
                this.m_errorCode = this.findCaptchaImplementation(element2);
            } else {
                this.m_errorCode = 2;
            }
        } else {
            this.m_errorCode = 1;
        }
    }

    public ForwarderInformationGrabber(String string) {
        try {
            Document document = XMLUtil.toXMLDocument(string);
            NodeList nodeList = document.getElementsByTagName("JapForwarder");
            if (nodeList.getLength() > 0) {
                Element element = (Element)nodeList.item(0);
                NodeList nodeList2 = element.getElementsByTagName("CaptchaEncoded");
                if (nodeList2.getLength() > 0) {
                    Element element2 = (Element)nodeList2.item(0);
                    this.m_errorCode = this.findCaptchaImplementation(element2);
                } else {
                    this.m_errorCode = 2;
                }
            } else {
                this.m_errorCode = 2;
            }
        }
        catch (Exception exception) {
            this.m_errorCode = 2;
        }
    }

    public int getErrorCode() {
        return this.m_errorCode;
    }

    public IImageEncodedCaptcha getCaptcha() {
        return this.m_captcha;
    }

    private int findCaptchaImplementation(Element element) {
        int n = 2;
        NodeList nodeList = element.getElementsByTagName("CaptchaDataFormat");
        if (nodeList.getLength() > 0) {
            Element element2 = (Element)nodeList.item(0);
            if ("ZIP_BINARY_IMAGE".equals(element2.getFirstChild().getNodeValue())) {
                try {
                    this.m_captcha = new ZipBinaryImageCaptchaClient(element);
                    n = 0;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, "Error while creating the captcha implementation!", exception);
                    n = 2;
                }
            } else {
                n = 3;
            }
        } else {
            n = 2;
        }
        return n;
    }
}

