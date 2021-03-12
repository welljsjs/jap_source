/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

import anon.client.IllegalTCRequestPostConditionException;
import anon.infoservice.ServiceOperator;
import anon.terms.TermsAndConditions;
import anon.terms.template.TermsAndConditionsTemplate;
import anon.util.IXMLEncodable;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TermsAndConditionsRequest
implements IXMLEncodable {
    public static final String XML_ELEMENT_NAME = "Resources";
    public static final String XML_ELEMENT_CONTAINER_NAME = "TermsAndConditionsRequest";
    public static final String XML_ATTR_LOCALE = "locale";
    public static final String XML_ELEMENT_REQ_TRANSLATION = "Translation";
    public static final String XML_ELEMENT_RESOURCE_TEMPLATE = "Template";
    public static final String XML_ELEMENT_RESOURCE_CUSTOMIZED_SECT = "CustomizedSections";
    public static final String XML_MSG_TC_INTERRUPT = "TermsAndConditionsInterrupt";
    public static final String XML_MSG_TC_CONFIRM = "TermsAndConditionsConfirm";
    private Vector requestedTemplates = new Vector();
    private Hashtable requestedItems = new Hashtable();
    private Hashtable resourceRootElements = new Hashtable();

    public void addTemplateRequest(ServiceOperator serviceOperator, String string, String string2) {
        if (!this.requestedTemplates.contains(string2)) {
            this.requestedTemplates.addElement(string2);
            this.addResourceRequest(XML_ELEMENT_RESOURCE_TEMPLATE, serviceOperator, string);
        }
    }

    public void addCustomizedSectionsRequest(ServiceOperator serviceOperator, String string) {
        this.addResourceRequest(XML_ELEMENT_RESOURCE_CUSTOMIZED_SECT, serviceOperator, string);
    }

    private void addResourceRequest(String string, ServiceOperator serviceOperator, String string2) {
        TCRequestKey tCRequestKey = new TCRequestKey(serviceOperator, string2);
        TCRequestValue tCRequestValue = (TCRequestValue)this.requestedItems.get(tCRequestKey);
        if (tCRequestValue == null) {
            tCRequestValue = new TCRequestValue();
            this.requestedItems.put(tCRequestKey, tCRequestValue);
        }
        tCRequestValue.addResourceRequest(string);
    }

    public boolean hasResourceRequests() {
        return !this.requestedItems.isEmpty();
    }

    public Element toXmlElement(Document document) {
        Enumeration enumeration = this.requestedItems.keys();
        if (!enumeration.hasMoreElements()) {
            return null;
        }
        Element element = document.createElement(XML_ELEMENT_CONTAINER_NAME);
        document.appendChild(element);
        Element element2 = null;
        Element element3 = null;
        Enumeration enumeration2 = null;
        TCRequestKey tCRequestKey = null;
        while (enumeration.hasMoreElements()) {
            tCRequestKey = (TCRequestKey)enumeration.nextElement();
            element2 = (Element)this.resourceRootElements.get(tCRequestKey.getOperator());
            if (element2 == null) {
                element2 = document.createElement(XML_ELEMENT_NAME);
                XMLUtil.setAttribute(element2, "id", tCRequestKey.getOperator().getId());
                this.resourceRootElements.put(tCRequestKey.getOperator(), element2);
            }
            if ((enumeration2 = ((TCRequestValue)this.requestedItems.get(tCRequestKey)).getAllResourceRequests()).hasMoreElements()) {
                element3 = document.createElement(XML_ELEMENT_REQ_TRANSLATION);
                XMLUtil.setAttribute(element3, XML_ATTR_LOCALE, tCRequestKey.getLangCode());
                element2.appendChild(element3);
            }
            while (enumeration2.hasMoreElements()) {
                element3.appendChild(document.createElement((String)enumeration2.nextElement()));
            }
            element.appendChild(element2);
        }
        return element;
    }

    public void checkRequestPostCondition() throws IllegalTCRequestPostConditionException {
        IllegalTCRequestPostConditionException illegalTCRequestPostConditionException = new IllegalTCRequestPostConditionException();
        Enumeration enumeration = this.requestedItems.keys();
        TCRequestKey tCRequestKey = null;
        TermsAndConditions termsAndConditions = null;
        while (enumeration.hasMoreElements()) {
            tCRequestKey = (TCRequestKey)enumeration.nextElement();
            termsAndConditions = TermsAndConditions.getTermsAndConditions(tCRequestKey.getOperator());
            if (termsAndConditions != null) {
                if (!termsAndConditions.hasTranslation(tCRequestKey.getLangCode())) {
                    illegalTCRequestPostConditionException.addErrorMessage("Requested Translation [" + tCRequestKey.getLangCode() + "] was not loaded for terms and conditions of operator " + tCRequestKey.getOperator().getOrganization());
                } else {
                    String string = termsAndConditions.getTemplateReferenceId(tCRequestKey.getLangCode());
                    if (string == null || TermsAndConditionsTemplate.getById(string, false) == null) {
                        illegalTCRequestPostConditionException.addErrorMessage("Template '" + string + "' for translation [" + tCRequestKey.getLangCode() + "] of terms and conditions of operator " + tCRequestKey.getOperator().getOrganization() + " was not loaded.");
                    }
                }
                if (termsAndConditions.hasDefaultTranslation()) continue;
                illegalTCRequestPostConditionException.addErrorMessage("No default translation for terms and conditions of operator " + tCRequestKey.getOperator().getOrganization() + " were loaded.");
                TermsAndConditions.removeTermsAndConditions(tCRequestKey.getOperator());
                continue;
            }
            illegalTCRequestPostConditionException.addErrorMessage("Translation for " + tCRequestKey + " not loaded.");
        }
        if (illegalTCRequestPostConditionException.hasErrorMessages()) {
            throw illegalTCRequestPostConditionException;
        }
    }

    private static class TCRequestValue {
        Vector requestEntries = new Vector();

        private TCRequestValue() {
        }

        private void addResourceRequest(String string) {
            if (!this.requestEntries.contains(string)) {
                this.requestEntries.addElement(string);
            }
        }

        private Enumeration getAllResourceRequests() {
            return this.requestEntries.elements();
        }
    }

    private static class TCRequestKey {
        ServiceOperator operator = null;
        String langCode = null;

        private TCRequestKey(ServiceOperator serviceOperator, String string) {
            this.operator = serviceOperator;
            this.langCode = string;
        }

        public String toString() {
            return this.operator.getId() + this.langCode;
        }

        public int hashCode() {
            return this.toString().hashCode();
        }

        public boolean equals(Object object) {
            return ((TCRequestKey)object).toString().equals(this.toString());
        }

        public String getLangCode() {
            return this.langCode;
        }

        public ServiceOperator getOperator() {
            return this.operator;
        }
    }
}

