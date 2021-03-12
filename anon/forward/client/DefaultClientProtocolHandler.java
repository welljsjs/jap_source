/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.client;

import anon.client.TrustModel;
import anon.forward.client.ClientForwardException;
import anon.forward.client.ForwardConnectionDescriptor;
import anon.forward.client.ProgressCounter;
import anon.infoservice.MixCascade;
import anon.transport.connection.IStreamConnection;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import anon.util.ZLibTools;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DefaultClientProtocolHandler {
    private static final int PROTOCOL_VERSION = 2;
    private static final int MAXIMUM_PROTOCOLMESSAGE_SIZE = 1000000;
    private static final int STATE_INITIALIZE = 0;
    private static final int STATE_OFFER_RECEIVED = 1;
    private static final int STATE_CASCADE_SELECTED = 2;
    private static final int STATE_FORWARDING = 3;
    private static final int STATE_CLOSED_AFTER_ERROR = 4;
    private static final byte[] MESSAGE_START_SIGNATURE = new byte[]{-1, 0, -16, 15};
    private static final byte[] MESSAGE_START_COMPRESS_SIGNATURE = new byte[]{-1, 15, -16, 15};
    private static final byte[] MESSAGE_END_SIGNATURE = new byte[]{-1, 0, -31, 30};
    private IStreamConnection m_connection;
    private int m_state;
    private int m_minDummyTrafficInterval;
    private MixCascade m_selectedMixCascade;
    private ProgressCounter m_progressCounter;

    public DefaultClientProtocolHandler(IStreamConnection iStreamConnection) {
        this.m_connection = iStreamConnection;
        this.m_state = 0;
        this.m_progressCounter = new ProgressCounter();
    }

    public ForwardConnectionDescriptor getConnectionDescriptor() throws ClientForwardException {
        ForwardConnectionDescriptor forwardConnectionDescriptor;
        block30: {
            forwardConnectionDescriptor = new ForwardConnectionDescriptor();
            if (this.m_state == 0) {
                Object object;
                Element element;
                byte[] arrby = null;
                try {
                    arrby = this.xmlToProtocolPacket(this.generateConnectionRequest());
                }
                catch (Exception exception) {
                    throw new ClientForwardException(255, "XML transforming error (" + exception.toString() + ").");
                }
                this.sendProtocolMessage(arrby);
                byte[] arrby2 = this.readProtocolMessage();
                Document document = null;
                try {
                    document = XMLUtil.toXMLDocument(arrby2);
                }
                catch (Exception exception) {
                    throw new ClientForwardException(255, "Error while parsing XML message (" + exception.toString() + ").");
                }
                NodeList nodeList = document.getElementsByTagName("JAPRouting");
                if (nodeList.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (JAPRouting node).");
                }
                Element element2 = (Element)nodeList.item(0);
                NodeList nodeList2 = element2.getElementsByTagName("Protocol");
                if (nodeList2.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (Protocol node).");
                }
                Element element3 = (Element)nodeList2.item(0);
                int n = -1;
                try {
                    n = Integer.parseInt(element3.getAttribute("version"));
                }
                catch (Exception exception) {
                    throw new ClientForwardException(255, "Error in XML structure (Protocol node -> version info).");
                }
                if (n != 2) {
                    throw new ClientForwardException(3, "Forwarder is using protocol version " + Integer.toString(n) + ", but we use version " + Integer.toString(2) + ".");
                }
                NodeList nodeList3 = element2.getElementsByTagName("Request");
                if (nodeList3.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (Request node).");
                }
                Element element4 = (Element)nodeList3.item(0);
                String string = element4.getAttribute("subject");
                if (!string.equals("connection")) {
                    throw new ClientForwardException(255, "Error in XML structure (Request node -> subject).");
                }
                String string2 = element4.getAttribute("msg");
                if (!string2.equals("offer")) {
                    throw new ClientForwardException(255, "Error in XML structure (Request node -> msg).");
                }
                NodeList nodeList4 = element4.getElementsByTagName("AllowedCascades");
                if (nodeList4.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (AllowedCascades node).");
                }
                Element element5 = (Element)nodeList4.item(0);
                NodeList nodeList5 = element5.getElementsByTagName("MixCascade");
                for (int i = 0; i < nodeList5.getLength(); ++i) {
                    element = (Element)nodeList5.item(i);
                    try {
                        object = new MixCascade(element);
                        if (!TrustModel.getCurrentTrustModel().isTrusted((MixCascade)object)) continue;
                        forwardConnectionDescriptor.addMixCascade((MixCascade)object);
                        continue;
                    }
                    catch (XMLParseException xMLParseException) {
                        LogHolder.log(3, LogType.MISC, "Error while parsing MixCascade", xMLParseException);
                    }
                }
                NodeList nodeList6 = element4.getElementsByTagName("QualityOfService");
                if (nodeList6.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (QualityOfService node).");
                }
                element = (Element)nodeList6.item(0);
                object = element.getElementsByTagName("MaximumBandwidth");
                if (object.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (MaximumBandwidth node).");
                }
                Element element6 = (Element)object.item(0);
                int n2 = -1;
                try {
                    n2 = Integer.parseInt(element6.getFirstChild().getNodeValue());
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (n2 < 0) {
                    throw new ClientForwardException(255, "Error in XML structure (MaximumBandwidth has illegal value).");
                }
                forwardConnectionDescriptor.setMaximumBandwidth(n2);
                NodeList nodeList7 = element.getElementsByTagName("GuaranteedBandwidth");
                if (nodeList7.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (GuaranteedBandwidth node).");
                }
                Element element7 = (Element)nodeList7.item(0);
                int n3 = -1;
                try {
                    n3 = Integer.parseInt(element7.getFirstChild().getNodeValue());
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (n3 < 0) {
                    throw new ClientForwardException(255, "Error in XML structure (GuaranteedBandwidth has illegal value).");
                }
                forwardConnectionDescriptor.setGuaranteedBandwidth(n3);
                NodeList nodeList8 = element2.getElementsByTagName("DummyTraffic");
                if (nodeList8.getLength() == 0) {
                    throw new ClientForwardException(255, "Error in XML structure (DummyTraffic node).");
                }
                Element element8 = (Element)nodeList8.item(0);
                try {
                    this.m_minDummyTrafficInterval = Integer.parseInt(element8.getAttribute("interval"));
                    if (this.m_minDummyTrafficInterval < -1) {
                        throw new Exception("Illegal value.");
                    }
                    break block30;
                }
                catch (Exception exception) {
                    throw new ClientForwardException(255, "Error in XML structure (DummyTraffic node -> interval info).");
                }
            }
            throw new ClientForwardException(2, "Wrong protocol state to call this method (current state: " + Integer.toString(this.m_state) + ").");
        }
        forwardConnectionDescriptor.setMinDummyTrafficInterval(this.m_minDummyTrafficInterval);
        this.m_state = 1;
        return forwardConnectionDescriptor;
    }

    public MixCascade getSelectedService() {
        return this.m_selectedMixCascade;
    }

    public void selectMixCascade(MixCascade mixCascade) throws ClientForwardException {
        byte[] arrby;
        if (this.m_state == 1) {
            Document document = null;
            try {
                document = XMLUtil.createDocument();
            }
            catch (Exception exception) {
                throw new ClientForwardException(255, "XML DocumentBuilder error (" + exception.toString() + ").");
            }
            Element element = document.createElement("JAPRouting");
            Element element2 = document.createElement("Request");
            element2.setAttribute("subject", "cascade");
            element2.setAttribute("msg", "select");
            Element element3 = document.createElement("MixCascade");
            element3.setAttribute("id", mixCascade.getId());
            element2.appendChild(element3);
            element.appendChild(element2);
            document.appendChild(element);
            arrby = null;
            try {
                arrby = this.xmlToProtocolPacket(document);
            }
            catch (Exception exception) {
                throw new ClientForwardException(255, "XML transforming error (" + exception.toString() + ").");
            }
        }
        throw new ClientForwardException(2, "Wrong protocol state to call this method (current state: " + Integer.toString(this.m_state) + ").");
        this.sendProtocolMessage(arrby);
        this.m_selectedMixCascade = mixCascade;
        this.m_state = 2;
    }

    private Document generateConnectionRequest() throws ClientForwardException {
        Document document = null;
        try {
            document = XMLUtil.createDocument();
        }
        catch (Exception exception) {
            throw new ClientForwardException(255, "XML DocumentBuilder error (" + exception.toString() + ").");
        }
        Element element = document.createElement("JAPRouting");
        Element element2 = document.createElement("Protocol");
        element2.setAttribute("version", Integer.toString(2));
        element.appendChild(element2);
        Element element3 = document.createElement("Request");
        element3.setAttribute("subject", "connection");
        element3.setAttribute("msg", "request");
        element3.setAttribute("compress", "zip");
        element.appendChild(element3);
        document.appendChild(element);
        return document;
    }

    private byte[] readProtocolMessage() throws ClientForwardException {
        byte[] arrby = new byte[MESSAGE_START_SIGNATURE.length + 4];
        byte[] arrby2 = null;
        byte[] arrby3 = null;
        boolean bl = false;
        try {
            int n = 0;
            while (n < arrby.length) {
                int n2 = this.m_connection.getInputStream().read(arrby, n, arrby.length - n);
                if (n2 == -1) {
                    throw new IOException("Read error: connection was closed.");
                }
                n = n2 + n;
            }
            byte[] arrby4 = new byte[MESSAGE_START_SIGNATURE.length];
            System.arraycopy(arrby, 0, arrby4, 0, MESSAGE_START_SIGNATURE.length);
            if (!this.checkSignature(arrby4, MESSAGE_START_SIGNATURE)) {
                if (this.checkSignature(arrby4, MESSAGE_START_COMPRESS_SIGNATURE)) {
                    bl = true;
                } else {
                    throw new ClientForwardException(2, "Protocol error (invalid start signature).");
                }
            }
            byte[] arrby5 = new byte[4];
            System.arraycopy(arrby, MESSAGE_START_SIGNATURE.length, arrby5, 0, 4);
            int n3 = 0;
            try {
                n3 = new DataInputStream(new ByteArrayInputStream(arrby5)).readInt();
            }
            catch (Exception exception) {
                throw new IOException("Error while reading message length.");
            }
            if (n3 < 0) {
                throw new ClientForwardException(2, "Protocol error (invalid length).");
            }
            byte[] arrby6 = new byte[n3 + MESSAGE_END_SIGNATURE.length];
            n = 0;
            this.m_progressCounter.setMax(n3);
            int n4 = 1000;
            while (n < arrby6.length) {
                int n5 = this.m_connection.getInputStream().read(arrby6, n, Math.min(arrby6.length - n, n4));
                if (n5 == -1) {
                    throw new IOException("Read error: connection was closed.");
                }
                n = n5 + n;
                this.m_progressCounter.incrValue(n5);
            }
            byte[] arrby7 = new byte[MESSAGE_END_SIGNATURE.length];
            System.arraycopy(arrby6, n3, arrby7, 0, MESSAGE_END_SIGNATURE.length);
            if (!this.checkSignature(arrby7, MESSAGE_END_SIGNATURE)) {
                throw new ClientForwardException(2, "Protocol error (invalid end signature).");
            }
            arrby3 = new byte[n3];
            System.arraycopy(arrby6, 0, arrby3, 0, n3);
            arrby2 = bl ? ZLibTools.decompress(arrby3) : arrby3;
        }
        catch (IOException iOException) {
            throw new ClientForwardException(1, "Connection error (" + iOException.toString() + ").");
        }
        this.m_progressCounter.close();
        return arrby2;
    }

    private void sendProtocolMessage(byte[] arrby) throws ClientForwardException {
        try {
            this.m_connection.getOutputStream().write(arrby);
            this.m_connection.getOutputStream().flush();
        }
        catch (IOException iOException) {
            throw new ClientForwardException(1, "Connection error (" + iOException.toString() + ").");
        }
    }

    private boolean checkSignature(byte[] arrby, byte[] arrby2) {
        boolean bl = false;
        try {
            if (arrby.length == arrby2.length) {
                bl = true;
                for (int i = 0; i < arrby.length && bl; ++i) {
                    if (arrby[i] == arrby2[i]) continue;
                    bl = false;
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return bl;
    }

    private byte[] xmlToProtocolPacket(Document document) throws Exception {
        return this.createProtocolPacket(XMLUtil.toByteArray(document));
    }

    private byte[] createProtocolPacket(byte[] arrby) {
        byte[] arrby2 = new byte[MESSAGE_START_SIGNATURE.length + 4 + arrby.length + MESSAGE_END_SIGNATURE.length];
        System.arraycopy(MESSAGE_START_SIGNATURE, 0, arrby2, 0, MESSAGE_START_SIGNATURE.length);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4);
        try {
            new DataOutputStream(byteArrayOutputStream).writeInt(arrby.length);
            System.arraycopy(byteArrayOutputStream.toByteArray(), 0, arrby2, MESSAGE_START_SIGNATURE.length, 4);
        }
        catch (Exception exception) {
            byte[] arrby3 = new byte[]{-1, -1, -1, -1};
            System.arraycopy(arrby3, 0, arrby2, MESSAGE_START_SIGNATURE.length, 4);
        }
        System.arraycopy(arrby, 0, arrby2, MESSAGE_START_SIGNATURE.length + 4, arrby.length);
        System.arraycopy(MESSAGE_END_SIGNATURE, 0, arrby2, MESSAGE_START_SIGNATURE.length + 4 + arrby.length, MESSAGE_END_SIGNATURE.length);
        return arrby2;
    }

    public ProgressCounter getPacketCounter() {
        return this.m_progressCounter;
    }
}

