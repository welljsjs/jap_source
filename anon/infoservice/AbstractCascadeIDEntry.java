/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.MixCascade;
import anon.util.ClassUtil;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractCascadeIDEntry
extends AbstractDatabaseEntry
implements IXMLEncodable {
    private static final String XML_ID = "ID";
    private static final String XML_CASCADE_ID = "CascadeID";
    private static final String XML_ATTR_UPDATE_TIME = "updateTime";
    private static final String XML_ATTR_EXPIRE_TIME = "expireTime";
    private String m_ID;
    private long m_version;
    private String m_cascadeID;

    public AbstractCascadeIDEntry(MixCascade mixCascade, long l) throws IllegalArgumentException {
        super(l);
        if (mixCascade == null) {
            throw new IllegalArgumentException("Given cascade is null!");
        }
        this.m_ID = mixCascade.getMixIDsAsString();
        this.m_version = System.currentTimeMillis();
        this.m_cascadeID = mixCascade.getId();
    }

    public AbstractCascadeIDEntry(AbstractCascadeIDEntry abstractCascadeIDEntry, long l) throws IllegalArgumentException {
        super(l);
        if (abstractCascadeIDEntry == null) {
            throw new IllegalArgumentException("Given cascade is null!");
        }
        this.m_ID = abstractCascadeIDEntry.getId();
        this.m_version = System.currentTimeMillis();
        this.m_cascadeID = abstractCascadeIDEntry.getCascadeId();
    }

    public AbstractCascadeIDEntry(Element element) throws XMLParseException {
        super(XMLUtil.parseAttribute((Node)element, XML_ATTR_EXPIRE_TIME, 0L));
        if (element == null) {
            throw new XMLParseException("##__null__##");
        }
        if (!element.getNodeName().equals(ClassUtil.getShortClassName(this.getClass()))) {
            throw new XMLParseException("##__root__##");
        }
        this.m_version = XMLUtil.parseAttribute((Node)element, XML_ATTR_UPDATE_TIME, 0);
        this.m_ID = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_ID), null);
        this.m_cascadeID = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, XML_CASCADE_ID), null);
        if (this.m_ID == null || this.m_cascadeID == null) {
            throw new XMLParseException("This is no valid " + ClassUtil.getShortClassName(this.getClass()) + " node!");
        }
    }

    public final String getCascadeId() {
        return this.m_cascadeID;
    }

    public boolean isReferencedCascade(MixCascade mixCascade) {
        return mixCascade != null && mixCascade.getMixIDsAsString() != null && mixCascade.getMixIDsAsString().equals(this.getId());
    }

    public final String getId() {
        return this.m_ID;
    }

    public long getLastUpdate() {
        return this.m_version;
    }

    public final long getVersionNumber() {
        return this.m_version;
    }

    protected void toXmlElementAppend(Element element) {
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(ClassUtil.getShortClassName(this.getClass()));
        Element element2 = document.createElement(XML_ID);
        XMLUtil.setAttribute(element, XML_ATTR_UPDATE_TIME, this.m_version);
        XMLUtil.setAttribute(element, XML_ATTR_EXPIRE_TIME, this.getExpireTime());
        XMLUtil.setValue((Node)element2, this.m_ID);
        element.appendChild(element2);
        element2 = document.createElement(XML_CASCADE_ID);
        XMLUtil.setValue((Node)element2, this.m_cascadeID);
        element.appendChild(element2);
        this.toXmlElementAppend(element);
        return element;
    }
}

