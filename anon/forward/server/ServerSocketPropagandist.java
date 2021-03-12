/*
 * Decompiled with CFR 0.150.
 */
package anon.forward.server;

import anon.infoservice.Database;
import anon.infoservice.InfoServiceDBEntry;
import anon.util.XMLUtil;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ServerSocketPropagandist
extends Observable
implements Runnable {
    public static final int STATE_REGISTERED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_RECONNECTING = 2;
    public static final int STATE_HALTED = 3;
    public static final int RETURN_SUCCESS = 0;
    public static final int RETURN_VERIFICATION_ERROR = 1;
    public static final int RETURN_INFOSERVICE_ERROR = 2;
    public static final int RETURN_UNKNOWN_ERROR = 3;
    private static final int RETURN_FORWARDERID_ERROR = 4;
    private static final int FORWARDER_VERIFY_ERROR_CODE = 1;
    private static final int FORWARDER_RENEW_ERROR_CODE = 11;
    private static final long FORWARDER_RENEW_PERIOD = 600000L;
    private int m_portNumber;
    private InfoServiceDBEntry m_infoService;
    private String m_forwarderId;
    private int m_currentErrorCode;
    private Thread m_propagandaThread;
    private int m_currentConnectionState;
    static /* synthetic */ Class class$anon$infoservice$InfoServiceDBEntry;

    public ServerSocketPropagandist(int n) {
        this(n, null);
    }

    public ServerSocketPropagandist(int n, InfoServiceDBEntry infoServiceDBEntry) {
        this.m_portNumber = n;
        this.m_infoService = infoServiceDBEntry;
        this.m_propagandaThread = new Thread(this);
        this.m_propagandaThread.setName("ServerSocketPropagandist");
        this.m_propagandaThread.setDaemon(true);
        this.m_currentErrorCode = this.announceNewForwarder();
        this.m_currentConnectionState = this.m_currentErrorCode != 0 ? 1 : 0;
        this.m_propagandaThread.start();
    }

    public int getPort() {
        return this.m_portNumber;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopPropaganda() {
        Thread thread = this.m_propagandaThread;
        synchronized (thread) {
            try {
                this.m_propagandaThread.interrupt();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public int getCurrentState() {
        return this.m_currentConnectionState;
    }

    public int getCurrentErrorCode() {
        return this.m_currentErrorCode;
    }

    public InfoServiceDBEntry getInfoService() {
        return this.m_infoService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        boolean bl = false;
        while (!bl) {
            Thread thread = this.m_propagandaThread;
            synchronized (thread) {
                try {
                    bl = Thread.interrupted();
                    if (!bl) {
                        this.m_propagandaThread.wait(600000L);
                    }
                }
                catch (InterruptedException interruptedException) {
                    bl = true;
                }
            }
            if (bl) continue;
            boolean bl2 = false;
            if (this.m_currentConnectionState == 0 && this.renewForwarder() != 0) {
                this.m_currentConnectionState = 2;
                bl2 = true;
            }
            if (this.m_currentConnectionState != 1 && this.m_currentConnectionState != 2) continue;
            int n = this.announceNewForwarder();
            if (n == 0) {
                this.m_currentConnectionState = 0;
                bl2 = true;
            }
            if (n != this.m_currentErrorCode) {
                bl2 = true;
            }
            this.m_currentErrorCode = n;
            if (!bl2) continue;
            this.setChanged();
            this.notifyObservers(null);
        }
        this.m_currentConnectionState = 3;
        this.m_currentErrorCode = 0;
        this.setChanged();
        this.notifyObservers(null);
    }

    private int announceNewForwarder() {
        int n = 3;
        if (this.m_infoService != null) {
            return this.announceNewForwarder(this.m_infoService);
        }
        Vector vector = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = ServerSocketPropagandist.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getEntryList();
        for (int i = 0; i < vector.size(); ++i) {
            InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)vector.elementAt(i);
            int n2 = infoServiceDBEntry.hasPrimaryForwarderList() ? this.announceNewForwarder(infoServiceDBEntry) : 2;
            if (n == 0 || n2 == 3) continue;
            n = n2;
        }
        return n;
    }

    private int announceNewForwarder(InfoServiceDBEntry infoServiceDBEntry) {
        int n;
        block13: {
            n = 3;
            try {
                Document document = XMLUtil.createDocument();
                Element element = document.createElement("JapForwarder");
                Element element2 = document.createElement("PlainInformation");
                Element element3 = document.createElement("Port");
                element3.appendChild(document.createTextNode(Integer.toString(this.m_portNumber)));
                element2.appendChild(element3);
                element.appendChild(element2);
                document.appendChild(element);
                try {
                    Element element4 = infoServiceDBEntry.postNewForwarder(element);
                    if (element4 == null) {
                        return 2;
                    }
                    NodeList nodeList = element4.getElementsByTagName("PlainInformation");
                    if (nodeList.getLength() == 0) {
                        NodeList nodeList2 = element4.getElementsByTagName("ErrorInformation");
                        if (nodeList2.getLength() == 0) {
                            n = 3;
                            break block13;
                        }
                        Element element5 = (Element)nodeList2.item(0);
                        NodeList nodeList3 = element5.getElementsByTagName("Error");
                        if (nodeList3.getLength() == 0) {
                            n = 3;
                            break block13;
                        }
                        Element element6 = (Element)nodeList3.item(0);
                        try {
                            if (Integer.parseInt(element6.getAttribute("code")) == 1) {
                                n = 1;
                                break block13;
                            }
                            n = 3;
                            LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: announceNewForwarder: The infoservice returned an unknwon error: Errorcode " + Integer.parseInt(element6.getAttribute("code")) + ": " + element6.getFirstChild().getNodeValue());
                        }
                        catch (Exception exception) {
                            n = 3;
                            LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: announceNewForwarder: Error while parsing the error information returned by the infoservice: " + exception.toString());
                        }
                        break block13;
                    }
                    Element element7 = (Element)nodeList.item(0);
                    NodeList nodeList4 = element7.getElementsByTagName("Forwarder");
                    if (nodeList4.getLength() == 0) {
                        n = 3;
                        LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: announceNewForwarder: Error while parsing the infoservice answer (Forwarder node).");
                        break block13;
                    }
                    Element element8 = (Element)nodeList4.item(0);
                    String string = element8.getAttribute("id");
                    if (string == null || new String("").equals(string)) {
                        n = 3;
                        LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: announceNewForwarder: Got an invalid id from the infoservice.");
                        break block13;
                    }
                    this.m_forwarderId = string;
                    n = 0;
                }
                catch (Exception exception) {
                    n = 2;
                    LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: announceNewForwarder: InfoService communication error: " + exception.toString());
                }
            }
            catch (Exception exception) {
                n = 3;
                LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: announceNewForwarder: Unexpected error while creating the request document: " + exception.toString());
            }
        }
        return n;
    }

    private int renewForwarder() {
        int n = 3;
        if (this.m_infoService != null) {
            return this.renewForwarder(this.m_infoService);
        }
        Vector vector = Database.getInstance(class$anon$infoservice$InfoServiceDBEntry == null ? (class$anon$infoservice$InfoServiceDBEntry = ServerSocketPropagandist.class$("anon.infoservice.InfoServiceDBEntry")) : class$anon$infoservice$InfoServiceDBEntry).getEntryList();
        for (int i = 0; i < vector.size(); ++i) {
            InfoServiceDBEntry infoServiceDBEntry = (InfoServiceDBEntry)vector.elementAt(i);
            int n2 = infoServiceDBEntry.hasPrimaryForwarderList() ? this.renewForwarder(infoServiceDBEntry) : 2;
            if (n == 0 || n2 == 3) continue;
            n = n2;
        }
        return n;
    }

    private int renewForwarder(InfoServiceDBEntry infoServiceDBEntry) {
        int n;
        block11: {
            n = 3;
            try {
                Document document = XMLUtil.createDocument();
                Element element = document.createElement("JapForwarder");
                Element element2 = document.createElement("PlainInformation");
                Element element3 = document.createElement("Forwarder");
                element3.setAttribute("id", this.m_forwarderId);
                element2.appendChild(element3);
                element.appendChild(element2);
                document.appendChild(element);
                try {
                    Element element4 = infoServiceDBEntry.postRenewForwarder(element);
                    if (element4 == null) {
                        return 2;
                    }
                    NodeList nodeList = element4.getElementsByTagName("ErrorInformation");
                    if (nodeList.getLength() == 0) {
                        n = 0;
                        break block11;
                    }
                    Element element5 = (Element)nodeList.item(0);
                    NodeList nodeList2 = element5.getElementsByTagName("Error");
                    if (nodeList2.getLength() == 0) {
                        n = 3;
                        break block11;
                    }
                    Element element6 = (Element)nodeList2.item(0);
                    try {
                        if (Integer.parseInt(element6.getAttribute("code")) == 11) {
                            n = 4;
                        } else {
                            n = 3;
                            LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: renewForwarder: The infoservice returned an unknwon error: Errorcode " + Integer.parseInt(element6.getAttribute("code")) + ": " + element6.getFirstChild().getNodeValue());
                        }
                    }
                    catch (Exception exception) {
                        n = 3;
                        LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: renewForwarder: Error while parsing the error information returned by the infoservice: " + exception.toString());
                    }
                }
                catch (Exception exception) {
                    n = 2;
                    LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: renewForwarder: InfoService communication error: " + exception.toString());
                }
            }
            catch (Exception exception) {
                n = 3;
                LogHolder.log(3, LogType.NET, "ServerSocketPropagandist: renewForwarder: Unexpected error while creating the request document: " + exception.toString());
            }
        }
        return n;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

