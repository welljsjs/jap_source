/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.crypto.CertPathInfo;
import anon.crypto.JAPCertificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MultiCertTrustGraph {
    Hashtable m_rootNodes;
    Hashtable m_opNodes;
    Hashtable m_endNodes;

    public MultiCertTrustGraph(CertPathInfo[] arrcertPathInfo) {
        this.createGraph(arrcertPathInfo);
    }

    private void createGraph(CertPathInfo[] arrcertPathInfo) {
        JAPCertificate jAPCertificate;
        JAPCertificate jAPCertificate2;
        JAPCertificate jAPCertificate3;
        int n;
        this.m_rootNodes = new Hashtable();
        this.m_opNodes = new Hashtable();
        this.m_endNodes = new Hashtable();
        for (n = 0; n < arrcertPathInfo.length; ++n) {
            jAPCertificate3 = arrcertPathInfo[n].getRootCertificate();
            jAPCertificate2 = arrcertPathInfo[n].getSecondCertificate();
            jAPCertificate = arrcertPathInfo[n].getFirstCertificate();
            if (jAPCertificate3 != null) {
                this.m_rootNodes.put(jAPCertificate3, new Node(jAPCertificate3, arrcertPathInfo[n].isVerified(arrcertPathInfo[n].getlength() - 1)));
            }
            if (jAPCertificate2 != null) {
                this.m_opNodes.put(jAPCertificate2, new Node(jAPCertificate2, arrcertPathInfo[n].isVerified(1)));
            }
            this.m_endNodes.put(jAPCertificate, new Node(jAPCertificate, arrcertPathInfo[n].isVerified(0)));
        }
        for (n = 0; n < arrcertPathInfo.length; ++n) {
            Node node;
            Node node2;
            jAPCertificate3 = arrcertPathInfo[n].getRootCertificate();
            jAPCertificate2 = arrcertPathInfo[n].getSecondCertificate();
            jAPCertificate = arrcertPathInfo[n].getFirstCertificate();
            if (jAPCertificate2 != null) {
                node2 = (Node)this.m_opNodes.get(jAPCertificate2);
                if (jAPCertificate3 != null) {
                    node = (Node)this.m_rootNodes.get(jAPCertificate3);
                    node.addChild(node2);
                    this.m_opNodes.remove(jAPCertificate2);
                }
                node = node2;
                node2 = (Node)this.m_endNodes.get(jAPCertificate);
                node.addChild(node2);
                this.m_endNodes.remove(jAPCertificate);
                continue;
            }
            if (jAPCertificate3 == null) continue;
            node2 = (Node)this.m_endNodes.get(jAPCertificate);
            node = (Node)this.m_rootNodes.get(jAPCertificate3);
            node.addChild(node2);
            this.m_endNodes.remove(jAPCertificate);
        }
    }

    public Enumeration getRootNodes() {
        return this.m_rootNodes.elements();
    }

    public Enumeration getOperatorNodes() {
        return this.m_opNodes.elements();
    }

    public Enumeration getEndNodes() {
        return this.m_endNodes.elements();
    }

    public int countTrustedRootNodes() {
        int n = 0;
        Enumeration enumeration = this.getRootNodes();
        Date date = new Date();
        block0: while (enumeration.hasMoreElements()) {
            Node node = (Node)enumeration.nextElement();
            if (!node.isTrusted() || !node.getCertificate().getValidity().isValid(date) || !node.hasChildNodes()) continue;
            Enumeration enumeration2 = node.getChildNodes();
            while (enumeration2.hasMoreElements()) {
                Node node2 = (Node)enumeration2.nextElement();
                if (!node2.getCertificate().getValidity().isValid(date)) continue;
                ++n;
                continue block0;
            }
        }
        return n;
    }

    public final class Node {
        private JAPCertificate m_cert;
        private Vector m_childNodes;
        private boolean m_trusted;

        public Node(JAPCertificate jAPCertificate, boolean bl) {
            this.m_cert = jAPCertificate;
            this.m_trusted = bl;
            this.m_childNodes = new Vector();
        }

        public void addChild(Node node) {
            if (!this.m_childNodes.contains(node)) {
                this.m_childNodes.addElement(node);
            }
        }

        public JAPCertificate getCertificate() {
            return this.m_cert;
        }

        public boolean isTrusted() {
            return this.m_trusted;
        }

        public Enumeration getChildNodes() {
            return this.m_childNodes.elements();
        }

        public boolean hasChildNodes() {
            return this.m_childNodes.size() > 0;
        }

        public int getWidth() {
            if (this.m_childNodes.size() == 0) {
                return 1;
            }
            return this.m_childNodes.size();
        }
    }
}

