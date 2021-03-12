/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.forward.ForwardUtils;
import anon.forward.server.ForwardConnection;
import anon.forward.server.ForwardServerManager;
import anon.forward.server.IProtocolHandler;
import anon.infoservice.ListenerInterface;
import anon.infoservice.MixCascade;
import anon.util.XMLUtil;
import anon.util.ZLibTools;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DefaultProtocolHandler
implements IProtocolHandler {
    private static final int PROTOCOL_VERSION = 2;
    private static final int MAXIMUM_PROTOCOLMESSAGE_SIZE = 100000;
    private static final byte[] MESSAGE_START_SIGNATURE = new byte[]{-1, 0, -16, 15};
    private static final byte[] MESSAGE_START_COMPRESS_SIGNATURE = new byte[]{-1, 15, -16, 15};
    private static final byte[] MESSAGE_END_SIGNATURE = new byte[]{-1, 0, -31, 30};
    private static final int STATE_WAIT_FOR_CLIENT_REQUEST = 0;
    private static final int STATE_WAIT_FOR_CASCADE_SELECTION = 1;
    private static final int STATE_CONNECTED_TO_MIX = 2;
    private static final int STATE_CONNECTION_CLOSED = 3;
    private static final int STATE_WAIT_FOR_INFOSERVICE_CLOSE = 4;
    private Socket m_serverConnection;
    private ByteArrayOutputStream m_incomingMessageBuffer = new ByteArrayOutputStream();
    private int m_incomingMessageLength = -1;
    private ByteArrayInputStream m_outgoingMessageBuffer = new ByteArrayInputStream(new byte[0]);
    private int m_currentState;
    private ForwardConnection m_parentConnection;
    private boolean m_doCompress;

    public DefaultProtocolHandler(ForwardConnection forwardConnection) throws Exception {
        this.m_parentConnection = forwardConnection;
        this.m_serverConnection = null;
        this.m_currentState = 0;
        this.m_doCompress = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int available() throws Exception {
        int n = 0;
        if (this.m_serverConnection != null) {
            n = this.m_serverConnection.getInputStream().available();
        } else {
            ByteArrayInputStream byteArrayInputStream = this.m_outgoingMessageBuffer;
            synchronized (byteArrayInputStream) {
                n = this.m_outgoingMessageBuffer.available();
            }
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int read(byte[] arrby) throws Exception {
        int n = 0;
        if (this.m_serverConnection != null) {
            n = this.m_serverConnection.getInputStream().read(arrby);
            if (n != 0) {
                LogHolder.log(7, LogType.TRANSPORT, "We read " + n + " bytes from the server (read from handler)");
            }
        } else {
            ByteArrayInputStream byteArrayInputStream = this.m_outgoingMessageBuffer;
            synchronized (byteArrayInputStream) {
                n = this.m_outgoingMessageBuffer.read(arrby);
            }
            if (n == -1) {
                n = 0;
            } else {
                LogHolder.log(7, LogType.TRANSPORT, "We read " + n + " bytes from the server (read from handler, message protocol)");
            }
        }
        return n;
    }

    public void write(byte[] arrby) throws Exception {
        if (this.m_serverConnection != null) {
            this.m_serverConnection.getOutputStream().write(arrby);
            this.m_serverConnection.getOutputStream().flush();
        } else {
            this.messageHandler(arrby);
        }
    }

    public void close() {
        if (this.m_serverConnection != null) {
            try {
                this.m_serverConnection.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.m_serverConnection = null;
        }
        this.m_incomingMessageBuffer = null;
        this.m_outgoingMessageBuffer = null;
        this.m_currentState = 3;
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

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void messageHandler(byte[] arrby) throws Exception {
        byte[] arrby2;
        boolean bl = false;
        if (this.m_incomingMessageBuffer.size() < MESSAGE_START_SIGNATURE.length + 4) {
            if (this.m_incomingMessageBuffer.size() < MESSAGE_START_SIGNATURE.length) {
                this.m_incomingMessageBuffer.write(arrby);
                if (this.m_incomingMessageBuffer.size() >= MESSAGE_START_SIGNATURE.length) {
                    arrby2 = new byte[MESSAGE_START_SIGNATURE.length];
                    System.arraycopy(this.m_incomingMessageBuffer.toByteArray(), 0, arrby2, 0, MESSAGE_START_SIGNATURE.length);
                    if (!this.checkSignature(arrby2, MESSAGE_START_SIGNATURE)) {
                        if (!this.checkSignature(arrby2, MESSAGE_START_COMPRESS_SIGNATURE)) throw new Exception("DefaultProtocolHandler: messageHandler: Protocol error (invalid start signature).");
                        bl = true;
                    }
                }
            } else {
                this.m_incomingMessageBuffer.write(arrby);
            }
            if (this.m_incomingMessageBuffer.size() >= MESSAGE_START_SIGNATURE.length + 4) {
                arrby2 = new byte[4];
                System.arraycopy(this.m_incomingMessageBuffer.toByteArray(), MESSAGE_START_SIGNATURE.length, arrby2, 0, 4);
                try {
                    this.m_incomingMessageLength = new DataInputStream(new ByteArrayInputStream(arrby2)).readInt();
                }
                catch (Exception exception) {
                    throw new Exception("DefaultProtocolHandler: messageHandler: Error while reading message length.");
                }
                if (this.m_incomingMessageLength < 0 || this.m_incomingMessageLength > 100000) {
                    this.m_incomingMessageLength = -1;
                    throw new Exception("DefaultProtocolHandler: messageHandler: Protocol error (invalid length).");
                }
            }
        } else {
            this.m_incomingMessageBuffer.write(arrby);
        }
        if (this.m_incomingMessageLength == -1 || this.m_incomingMessageBuffer.size() < MESSAGE_START_SIGNATURE.length + 4 + this.m_incomingMessageLength + MESSAGE_END_SIGNATURE.length) return;
        arrby2 = new byte[MESSAGE_END_SIGNATURE.length];
        System.arraycopy(this.m_incomingMessageBuffer.toByteArray(), MESSAGE_START_SIGNATURE.length + 4 + this.m_incomingMessageLength, arrby2, 0, MESSAGE_END_SIGNATURE.length);
        if (!this.checkSignature(arrby2, MESSAGE_END_SIGNATURE)) {
            throw new Exception("DefaultProtocolHandler: messageHandler: Protocol error (invalid end signature).");
        }
        byte[] arrby3 = new byte[this.m_incomingMessageLength];
        System.arraycopy(this.m_incomingMessageBuffer.toByteArray(), MESSAGE_START_SIGNATURE.length + 4, arrby3, 0, this.m_incomingMessageLength);
        byte[] arrby4 = bl ? ZLibTools.decompress(arrby3) : arrby3;
        byte[] arrby5 = new byte[this.m_incomingMessageBuffer.size() - MESSAGE_START_SIGNATURE.length - 4 - this.m_incomingMessageLength - MESSAGE_END_SIGNATURE.length];
        System.arraycopy(this.m_incomingMessageBuffer.toByteArray(), MESSAGE_START_SIGNATURE.length + 4 + this.m_incomingMessageLength + MESSAGE_END_SIGNATURE.length, arrby5, 0, arrby5.length);
        this.m_incomingMessageBuffer.reset();
        this.m_incomingMessageLength = -1;
        this.m_incomingMessageBuffer.write(arrby5);
        this.messageReceived(arrby4);
        if (this.m_incomingMessageBuffer.size() <= 0) return;
        this.m_incomingMessageBuffer.reset();
        this.messageHandler(arrby5);
    }

    private void messageReceived(byte[] arrby) throws Exception {
        LogHolder.log(7, LogType.FORWARDING, "We received a Forwarding XML Control message from a client");
        Document document = XMLUtil.toXMLDocument(arrby);
        NodeList nodeList = document.getElementsByTagName("JAPRouting");
        if (nodeList.getLength() == 0) {
            throw new Exception("DefaultProtocolHandler: messageReceived: Error in XML structure (JAPRouting node).");
        }
        Element element = (Element)nodeList.item(0);
        this.handleProtocol(element);
    }

    private void handleProtocol(Element element) throws Exception {
        switch (this.m_currentState) {
            case 0: {
                this.handleInitialRequestMessage(element);
                break;
            }
            case 1: {
                this.handleClientCascadeSelectMessage(element);
                break;
            }
            default: {
                throw new Exception("DefaultProtocolHandler: handleProtocol: Protocol error.");
            }
        }
    }

    private void handleInitialRequestMessage(Element element) throws Exception {
        NodeList nodeList = element.getElementsByTagName("Request");
        if (nodeList.getLength() == 0) {
            throw new Exception("DefaultProtocolHandler: handleInitialRequestMessage: Error in XML structure (Request node).");
        }
        Element element2 = (Element)nodeList.item(0);
        String string = element2.getAttribute("subject");
        if (!string.equals("connection")) {
            throw new Exception("DefaultProtocolHandler: handleInitialRequestMessage: Error in XML structure (Request node, wrong subject).");
        }
        String string2 = element2.getAttribute("msg");
        if (string2.equals("request")) {
            String string3 = element2.getAttribute("compress");
            if (string3.equals("zip")) {
                LogHolder.log(7, LogType.FORWARDING, "Start compress protocol");
                this.m_doCompress = true;
            }
            this.m_currentState = 1;
            this.sendProtocolDataToClient(this.xmlToProtocolPacket(this.generateConnectionOfferXml()));
        } else if (string2.equals("verify")) {
            this.m_currentState = 4;
            this.sendProtocolDataToClient(this.xmlToProtocolPacket(this.generateConnectionAcknowledgement()));
        } else {
            throw new Exception("DefaultProtocolHandler: handleInitialRequestMessage: Error in XML structure (Request node, wrong msg).");
        }
    }

    private void handleClientCascadeSelectMessage(Element element) throws Exception {
        NodeList nodeList = element.getElementsByTagName("Request");
        LogHolder.log(7, LogType.TRANSPORT, "We receveid a CascadeSelectMessage : try to start cascade connection");
        if (nodeList.getLength() == 0) {
            throw new Exception("DefaultProtocolHandler: handleClientCascadeSelectMessage: Error in XML structure (Request node).");
        }
        Element element2 = (Element)nodeList.item(0);
        String string = element2.getAttribute("subject");
        if (!string.equals("cascade")) {
            throw new Exception("DefaultProtocolHandler: handleClientCascadeSelectMessage: Error in XML structure (Request node, wrong subject).");
        }
        String string2 = element2.getAttribute("msg");
        if (!string2.equals("select")) {
            throw new Exception("DefaultProtocolHandler: handleClientCascadeSelectMessage: Error in XML structure (Request node, wrong msg).");
        }
        NodeList nodeList2 = element2.getElementsByTagName("MixCascade");
        if (nodeList2.getLength() == 0) {
            throw new Exception("DefaultProtocolHandler: handleClientCascadeSelectMessage: Error in XML structure (MixCascade node).");
        }
        Element element3 = (Element)nodeList2.item(0);
        String string3 = element3.getAttribute("id");
        MixCascade mixCascade = ForwardServerManager.getInstance().getAllowedCascadesDatabase().getMixCascadeById(string3);
        if (mixCascade == null) {
            throw new Exception("DefaultProtocolHandler: handleClientCascadeSelectMessage: Selected cascade not available.");
        }
        if (!this.connectTo(mixCascade)) {
            this.close();
            throw new Exception("DefaultProtocolHandler: handleClientCascadeSelectMessage: Error connecting the selected cascade.");
        }
        this.emptyBuffers();
        this.m_currentState = 2;
    }

    private boolean connectTo(MixCascade mixCascade) {
        for (int i = 0; i < mixCascade.getNumberOfListenerInterfaces() && this.m_serverConnection == null; ++i) {
            ListenerInterface listenerInterface = mixCascade.getListenerInterface(i);
            try {
                this.m_serverConnection = ForwardUtils.getInstance().createConnection(listenerInterface.getHost(), listenerInterface.getPort());
                this.m_serverConnection.setSoTimeout(0);
                continue;
            }
            catch (Exception exception) {
                this.m_serverConnection = null;
            }
        }
        return this.m_serverConnection != null;
    }

    private void emptyBuffers() throws Exception {
        this.m_serverConnection.getOutputStream().write(this.m_incomingMessageBuffer.toByteArray());
    }

    private Document generateConnectionOfferXml() throws Exception {
        Document document = XMLUtil.createDocument();
        Element element = document.createElement("JAPRouting");
        Element element2 = document.createElement("Protocol");
        element2.setAttribute("version", Integer.toString(2));
        element.appendChild(element2);
        Element element3 = document.createElement("Request");
        element3.setAttribute("subject", "connection");
        element3.setAttribute("msg", "offer");
        element3.appendChild(ForwardServerManager.getInstance().getAllowedCascadesDatabase().toXmlNode(document));
        Element element4 = document.createElement("QualityOfService");
        Element element5 = document.createElement("MaximumBandwidth");
        element5.appendChild(document.createTextNode(Integer.toString(this.m_parentConnection.getParentScheduler().getMaximumBandwidth())));
        element4.appendChild(element5);
        Element element6 = document.createElement("GuaranteedBandwidth");
        element6.appendChild(document.createTextNode(Integer.toString(this.m_parentConnection.getParentScheduler().getGuaranteedBandwidth())));
        element4.appendChild(element6);
        element3.appendChild(element4);
        Element element7 = document.createElement("DummyTraffic");
        element7.setAttribute("interval", Integer.toString(ForwardServerManager.getInstance().getDummyTrafficInterval()));
        element3.appendChild(element7);
        element.appendChild(element3);
        document.appendChild(element);
        return document;
    }

    private Document generateConnectionAcknowledgement() throws Exception {
        Document document = XMLUtil.createDocument();
        Element element = document.createElement("JAPRouting");
        Element element2 = document.createElement("Request");
        element2.setAttribute("subject", "connection");
        element2.setAttribute("msg", "acknowledge");
        element.appendChild(element2);
        document.appendChild(element);
        return document;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendProtocolDataToClient(byte[] arrby) {
        ByteArrayInputStream byteArrayInputStream = this.m_outgoingMessageBuffer;
        synchronized (byteArrayInputStream) {
            byte[] arrby2 = new byte[this.m_outgoingMessageBuffer.available() + arrby.length];
            this.m_outgoingMessageBuffer.read(arrby2, 0, this.m_outgoingMessageBuffer.available());
            System.arraycopy(arrby, 0, arrby2, arrby2.length - arrby.length, arrby.length);
            this.m_outgoingMessageBuffer = new ByteArrayInputStream(arrby2);
        }
    }

    private byte[] xmlToProtocolPacket(Document document) throws Exception {
        return this.createProtocolPacket(XMLUtil.toByteArray(document));
    }

    private byte[] createProtocolPacket(byte[] arrby) {
        byte[] arrby2;
        int n;
        byte[] arrby3 = null;
        if (this.m_doCompress) {
            arrby3 = ZLibTools.compress(arrby);
            n = arrby3.length;
            arrby2 = new byte[MESSAGE_START_SIGNATURE.length + 4 + n + MESSAGE_END_SIGNATURE.length];
            System.arraycopy(MESSAGE_START_COMPRESS_SIGNATURE, 0, arrby2, 0, MESSAGE_START_SIGNATURE.length);
        } else {
            n = arrby.length;
            arrby2 = new byte[MESSAGE_START_SIGNATURE.length + 4 + n + MESSAGE_END_SIGNATURE.length];
            System.arraycopy(MESSAGE_START_SIGNATURE, 0, arrby2, 0, MESSAGE_START_SIGNATURE.length);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4);
        try {
            new DataOutputStream(byteArrayOutputStream).writeInt(n);
            System.arraycopy(byteArrayOutputStream.toByteArray(), 0, arrby2, MESSAGE_START_SIGNATURE.length, 4);
        }
        catch (Exception exception) {
            byte[] arrby4 = new byte[]{-1, -1, -1, -1};
            System.arraycopy(arrby4, 0, arrby2, MESSAGE_START_SIGNATURE.length, 4);
        }
        if (this.m_doCompress) {
            System.arraycopy(arrby3, 0, arrby2, MESSAGE_START_SIGNATURE.length + 4, n);
        } else {
            System.arraycopy(arrby, 0, arrby2, MESSAGE_START_SIGNATURE.length + 4, n);
        }
        System.arraycopy(MESSAGE_END_SIGNATURE, 0, arrby2, MESSAGE_START_SIGNATURE.length + 4 + n, MESSAGE_END_SIGNATURE.length);
        return arrby2;
    }
}

