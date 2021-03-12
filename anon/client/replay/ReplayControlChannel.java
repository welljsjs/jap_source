/*
 * Decompiled with CFR 0.150.
 */
package anon.client.replay;

import anon.IServiceContainer;
import anon.client.Multiplexer;
import anon.client.XmlControlChannel;
import anon.client.replay.ReplayTimestamp;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReplayControlChannel
extends XmlControlChannel {
    private MessageDistributor m_messageDistributor = new MessageDistributor();
    private Object m_internalSynchronization = new Object();

    public ReplayControlChannel(Multiplexer multiplexer, IServiceContainer iServiceContainer) {
        super(3, multiplexer, iServiceContainer, false);
    }

    public Observable getMessageDistributor() {
        return this.m_messageDistributor;
    }

    protected void processXmlMessage(Document document) {
        try {
            LogHolder.log(7, LogType.NET, "Received a message: " + XMLUtil.toString(document));
            Element element = document.getDocumentElement();
            if (element == null) {
                throw new XMLParseException("##__root__##", "No document element in received XML structure.");
            }
            if (!element.getNodeName().equals("Mixes")) {
                throw new XMLParseException("##__root__##", "Mixes node expected in received XML structure.");
            }
            Vector<ReplayTimestamp> vector = new Vector<ReplayTimestamp>();
            NodeList nodeList = element.getElementsByTagName("Mix");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Element element2 = (Element)nodeList.item(i);
                String string = XMLUtil.parseAttribute((Node)element2, "id", null);
                if (string == null) {
                    throw new XMLParseException("##__null__##", "XML structure of Mix " + Integer.toString(i) + " does not contain a Mix-ID.");
                }
                NodeList nodeList2 = element2.getElementsByTagName("Replay");
                if (nodeList2.getLength() == 0) {
                    throw new XMLParseException("##__null__##", "XML structure of Mix " + Integer.toString(i) + " does not contain a Replay node.");
                }
                NodeList nodeList3 = ((Element)nodeList2.item(0)).getElementsByTagName("ReplayTimestamp");
                if (nodeList3.getLength() == 0) {
                    throw new XMLParseException("##__null__##", "XML structure of Mix " + Integer.toString(i) + " does not contain a ReplayTimestamp node.");
                }
                int n = XMLUtil.parseAttribute(nodeList3.item(0), "offset", -1);
                if (n == -1) {
                    throw new XMLParseException("##__null__##", "XML structure of Mix " + Integer.toString(i) + " does not contain a valid ReplayTimestamp offset.");
                }
                int n2 = XMLUtil.parseAttribute(nodeList3.item(0), "interval", -1);
                if (n2 == -1) {
                    throw new XMLParseException("##__null__##", "XML structure of Mix " + Integer.toString(i) + " does not contain a valid ReplayTimestamp interval.");
                }
                vector.addElement(new ReplayTimestamp(string, n2, n));
            }
            this.m_messageDistributor.publishTimestamps(vector);
        }
        catch (Exception exception) {
            this.getServiceContainer().keepCurrentService(false);
            LogHolder.log(3, LogType.NET, exception);
            this.m_messageDistributor.publishException(exception);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void requestTimestamps() {
        try {
            Document document = XMLUtil.createDocument();
            if (document == null) {
                throw new Exception("ReplayControlChannel: requestTimestamps(): Cannot create XML document for request.");
            }
            Element element = document.createElement("GetTimestamps");
            document.appendChild(element);
            int n = 0;
            Object object = this.m_internalSynchronization;
            synchronized (object) {
                n = this.sendXmlMessage(document);
            }
            if (n != 0) {
                throw new Exception("ReplayControlChannel: requestTimestamps(): Errorcode '" + Integer.toString(n) + "' while sending request.");
            }
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.NET, exception);
            this.m_messageDistributor.publishException(exception);
        }
    }

    private class MessageDistributor
    extends Observable {
        private MessageDistributor() {
        }

        public void publishTimestamps(Vector vector) {
            this.publishObject(vector);
        }

        public void publishException(Exception exception) {
            this.publishObject(exception);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void publishObject(Object object) {
            MessageDistributor messageDistributor = this;
            synchronized (messageDistributor) {
                this.setChanged();
                this.notifyObservers(object);
            }
        }
    }
}

