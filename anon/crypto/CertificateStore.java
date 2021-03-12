/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto;

import anon.crypto.CertPath;
import anon.crypto.CertificateContainer;
import anon.crypto.CertificateInfoStructure;
import anon.crypto.JAPCertificate;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CertificateStore
extends Observable
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "TrustedCertificates";
    private Hashtable m_trustedCertificates = new Hashtable();
    private Hashtable m_lockTable = new Hashtable();
    private int m_lockIdPointer = 0;

    public static String getXmlSettingsRootNodeName() {
        return XML_ELEMENT_NAME;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getAllCertificates() {
        Vector<CertificateInfoStructure> vector = new Vector<CertificateInfoStructure>();
        CertificateStore certificateStore = this;
        synchronized (certificateStore) {
            Hashtable hashtable = this.m_trustedCertificates;
            synchronized (hashtable) {
                Enumeration enumeration = this.m_trustedCertificates.elements();
                while (enumeration.hasMoreElements()) {
                    vector.addElement(((CertificateContainer)enumeration.nextElement()).getInfoStructure());
                }
            }
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getUnavailableCertificatesByType(int n) {
        Vector<CertificateInfoStructure> vector = new Vector<CertificateInfoStructure>();
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustedCertificates.elements();
            while (enumeration.hasMoreElements()) {
                CertificateContainer certificateContainer = (CertificateContainer)enumeration.nextElement();
                if (certificateContainer.getCertificateType() != n || certificateContainer.isAvailable()) continue;
                vector.addElement(certificateContainer.getInfoStructure());
            }
        }
        return vector;
    }

    public CertificateInfoStructure getCertificateInfoStructure(JAPCertificate jAPCertificate, int n) {
        CertificateContainer certificateContainer = (CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(jAPCertificate, n));
        if (certificateContainer != null) {
            return certificateContainer.getInfoStructure();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CertificateInfoStructure getCertificateInfoStructure(JAPCertificate jAPCertificate) {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustedCertificates.elements();
            while (enumeration.hasMoreElements()) {
                CertificateContainer certificateContainer = (CertificateContainer)enumeration.nextElement();
                if (!certificateContainer.getCertificate().equals(jAPCertificate)) continue;
                return certificateContainer.getInfoStructure();
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getAvailableCertificatesByType(int n) {
        Vector<CertificateInfoStructure> vector = new Vector<CertificateInfoStructure>();
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustedCertificates.elements();
            while (enumeration.hasMoreElements()) {
                CertificateContainer certificateContainer = (CertificateContainer)enumeration.nextElement();
                if (certificateContainer.getCertificateType() != n || !certificateContainer.isAvailable()) continue;
                vector.addElement(certificateContainer.getInfoStructure());
            }
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int addCertificateWithVerification(CertPath certPath, int n, boolean bl) {
        int n2 = -1;
        if (n == 2 || n == 3) {
            boolean bl2 = false;
            Hashtable hashtable = this.m_trustedCertificates;
            synchronized (hashtable) {
                int n3 = 1;
                if (n == 3) {
                    n3 = 5;
                }
                CertificateContainer certificateContainer = (CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certPath.getFirstCertificate(), n));
                CertificateContainer certificateContainer2 = new CertificateContainer(certPath, n, true);
                if (certificateContainer == null || certificateContainer.getCertificate().getValidity().getValidFrom().before(certificateContainer2.getCertificate().getValidity().getValidFrom())) {
                    this.m_trustedCertificates.put(this.getCertificateId(certPath.getFirstCertificate(), n), certificateContainer2);
                    Enumeration enumeration = this.getAvailableCertificatesByType(n3).elements();
                    boolean bl3 = false;
                    while (enumeration.hasMoreElements() && !bl3) {
                        JAPCertificate jAPCertificate = ((CertificateInfoStructure)enumeration.nextElement()).getCertificate();
                        bl3 = certPath.isVerifier(jAPCertificate);
                        if (!bl3) continue;
                        certificateContainer2.setParentCertificate(jAPCertificate);
                    }
                    bl2 = true;
                }
                if (!bl) {
                    n2 = this.getNextAvailableLockId();
                    this.m_lockTable.put(new Integer(n2), this.getCertificateId(certPath.getFirstCertificate(), n));
                    ((CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certPath.getFirstCertificate(), n))).getLockList().addElement(new Integer(n2));
                } else {
                    ((CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certPath.getFirstCertificate(), n))).enableOnlyHardRemovable();
                }
            }
            if (bl2) {
                this.setChanged();
                this.notifyObservers(new Integer(n));
            }
        }
        return n2;
    }

    public synchronized int addCertificateWithoutVerification(JAPCertificate jAPCertificate, int n, boolean bl, boolean bl2) {
        return this.addCertificateWithoutVerification(CertPath.getRootInstance(jAPCertificate), n, bl, bl2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized int addCertificateWithoutVerification(CertPath certPath, int n, boolean bl, boolean bl2) {
        int n2 = -1;
        boolean bl3 = false;
        if (certPath == null) {
            return n2;
        }
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            if (!this.m_trustedCertificates.containsKey(this.getCertificateId(certPath.getFirstCertificate(), n))) {
                CertificateContainer certificateContainer = new CertificateContainer(certPath, n, false);
                this.m_trustedCertificates.put(this.getCertificateId(certPath.getFirstCertificate(), n), certificateContainer);
                if (n == 1 || n == 5) {
                    this.activateAllDependentCertificates(certPath.getFirstCertificate());
                }
                bl3 = true;
            }
            if (!bl) {
                n2 = this.getNextAvailableLockId();
                this.m_lockTable.put(new Integer(n2), this.getCertificateId(certPath.getFirstCertificate(), n));
                ((CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certPath.getFirstCertificate(), n))).getLockList().addElement(new Integer(n2));
            } else {
                ((CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certPath.getFirstCertificate(), n))).enableOnlyHardRemovable();
            }
            if (bl2) {
                ((CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certPath.getFirstCertificate(), n))).enableNotRemovable();
            }
        }
        if (bl3) {
            this.setChanged();
            this.notifyObservers(new Integer(n));
        }
        return n2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void removeCertificateLock(int n) {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            CertificateContainer certificateContainer = null;
            try {
                certificateContainer = (CertificateContainer)this.m_trustedCertificates.get(this.m_lockTable.get(new Integer(n)));
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.MISC, "Error while removing certificate lock. There is no lock with ID " + Integer.toString(n) + ".");
            }
            if (certificateContainer != null) {
                certificateContainer.getLockList().removeElement(new Integer(n));
                if (!certificateContainer.isOnlyHardRemovable() && certificateContainer.getLockList().size() == 0) {
                    this.removeCertificate(certificateContainer.getInfoStructure());
                }
            }
            this.m_lockTable.remove(new Integer(n));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void removeCertificate(CertificateInfoStructure certificateInfoStructure) {
        CertificateContainer certificateContainer = null;
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            certificateContainer = (CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certificateInfoStructure.getCertificate(), certificateInfoStructure.getCertificateType()));
            if (certificateContainer != null) {
                if (certificateContainer.getCertificateType() == 1 || certificateContainer.getCertificateType() == 5) {
                    this.deactivateAllDependentCertificates(certificateContainer.getCertificate());
                }
                Enumeration enumeration = certificateContainer.getLockList().elements();
                while (enumeration.hasMoreElements()) {
                    this.m_lockTable.put(enumeration.nextElement(), "");
                }
                this.m_trustedCertificates.remove(this.getCertificateId(certificateInfoStructure.getCertificate(), certificateInfoStructure.getCertificateType()));
            }
        }
        if (certificateContainer != null) {
            this.setChanged();
            this.notifyObservers(new Integer(certificateContainer.getCertificateType()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAllCertificates() {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_lockTable.keys();
            while (enumeration.hasMoreElements()) {
                this.m_lockTable.put(enumeration.nextElement(), "");
            }
            if (this.m_trustedCertificates.size() > 0) {
                Enumeration enumeration2 = this.m_trustedCertificates.keys();
                while (enumeration2.hasMoreElements()) {
                    Object k = enumeration2.nextElement();
                    CertificateContainer certificateContainer = (CertificateContainer)this.m_trustedCertificates.get(k);
                    if (certificateContainer.isNotRemovable()) continue;
                    this.m_trustedCertificates.remove(k);
                }
                this.setChanged();
            }
        }
        this.notifyObservers();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void reset() {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustedCertificates.elements();
            while (enumeration.hasMoreElements()) {
                CertificateContainer certificateContainer = (CertificateContainer)enumeration.nextElement();
                certificateContainer.setEnabled(certificateContainer.isEnabled());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void setEnabled(CertificateInfoStructure certificateInfoStructure, boolean bl) {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            CertificateContainer certificateContainer = (CertificateContainer)this.m_trustedCertificates.get(this.getCertificateId(certificateInfoStructure.getCertificate(), certificateInfoStructure.getCertificateType()));
            if (certificateContainer != null && certificateContainer.isEnabled() != bl) {
                certificateContainer.setEnabled(bl);
                if (certificateContainer.getCertificateType() == 1 || certificateContainer.getCertificateType() == 5) {
                    if (bl) {
                        this.activateAllDependentCertificates(certificateContainer.getCertificate());
                    } else {
                        this.deactivateAllDependentCertificates(certificateContainer.getCertificate());
                    }
                }
                this.setChanged();
            }
        }
        this.notifyObservers();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustedCertificates.elements();
            while (enumeration.hasMoreElements()) {
                CertificateContainer certificateContainer = (CertificateContainer)enumeration.nextElement();
                if (!certificateContainer.isOnlyHardRemovable() || certificateContainer.isNotRemovable()) continue;
                element.appendChild(certificateContainer.toXmlElement(document));
            }
        }
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadSettingsFromXml(Element element) {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            this.removeAllCertificates();
            NodeList nodeList = element.getElementsByTagName("CertificateContainer");
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Element element2 = (Element)nodeList.item(i);
                try {
                    CertificateContainer certificateContainer = new CertificateContainer(element2);
                    if (certificateContainer.getCertificateNeedsVerification()) {
                        this.addCertificateWithVerification(certificateContainer.getCertPath(), certificateContainer.getCertificateType(), true);
                    } else {
                        this.addCertificateWithoutVerification(certificateContainer.getCertPath(), certificateContainer.getCertificateType(), true, certificateContainer.isNotRemovable());
                    }
                    this.setEnabled(certificateContainer.getInfoStructure(), certificateContainer.isEnabled());
                    continue;
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.MISC, "Error while loading a CertificateContainer. Skipping this entry. Error: " + exception.toString() + " - Invalid container was: " + XMLUtil.toString(element2));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void activateAllDependentCertificates(JAPCertificate jAPCertificate) {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustedCertificates.elements();
            while (enumeration.hasMoreElements()) {
                JAPCertificate jAPCertificate2;
                CertificateContainer certificateContainer = (CertificateContainer)enumeration.nextElement();
                if (!certificateContainer.getCertificateNeedsVerification() || (jAPCertificate2 = certificateContainer.getParentCertificate()) != null || !certificateContainer.getCertPath().isVerifier(jAPCertificate)) continue;
                certificateContainer.setParentCertificate(jAPCertificate);
                certificateContainer.setEnabled(true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void deactivateAllDependentCertificates(JAPCertificate jAPCertificate) {
        Hashtable hashtable = this.m_trustedCertificates;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_trustedCertificates.elements();
            while (enumeration.hasMoreElements()) {
                JAPCertificate jAPCertificate2;
                CertificateContainer certificateContainer = (CertificateContainer)enumeration.nextElement();
                if (!certificateContainer.getCertificateNeedsVerification() || (jAPCertificate2 = certificateContainer.getParentCertificate()) == null || !jAPCertificate2.equals(jAPCertificate)) continue;
                certificateContainer.setParentCertificate(null);
                certificateContainer.setEnabled(false);
            }
        }
    }

    private int getNextAvailableLockId() {
        while (this.m_lockTable.containsKey(new Integer(this.m_lockIdPointer)) || this.m_lockIdPointer == -1) {
            ++this.m_lockIdPointer;
        }
        return this.m_lockIdPointer;
    }

    private String getCertificateId(JAPCertificate jAPCertificate, int n) {
        return jAPCertificate.getId() + Integer.toString(n);
    }
}

