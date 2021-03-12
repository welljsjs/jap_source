/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

import anon.client.IllegalTCRequestPostConditionException;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Database;
import anon.infoservice.ServiceOperator;
import anon.terms.TermsAndConditions;
import anon.terms.TermsAndConditionsRequest;
import anon.terms.template.TermsAndConditionsTemplate;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.IOException;
import java.security.SignatureException;
import java.util.Observable;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TermsAndConditionsResponseHandler
extends Observable {
    public static final String XML_ELEMENT_INVALID_REQUEST_NAME = "InvalidTermsAndConditionsRequest";
    public static final String XML_ELEMENT_RESPONSE_NAME = "TermsAndConditionsResponse";
    private static final TermsAndConditionsResponseHandler SINGLETON = new TermsAndConditionsResponseHandler();
    static /* synthetic */ Class class$anon$terms$template$TermsAndConditionsTemplate;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;

    private TermsAndConditionsResponseHandler() {
    }

    public void handleXMLResourceResponse(Document document, TermsAndConditionsRequest termsAndConditionsRequest) throws XMLParseException, IOException, IllegalTCRequestPostConditionException, SignatureException {
        if (document.getDocumentElement().getTagName().equals(XML_ELEMENT_INVALID_REQUEST_NAME)) {
            throw new IOException("Error: Mix reported invalid TC request");
        }
        if (!document.getDocumentElement().getTagName().equals(XML_ELEMENT_RESPONSE_NAME)) {
            throw new XMLParseException("No TC response.");
        }
        Node node = XMLUtil.getFirstChildByName(document.getDocumentElement(), "Resources");
        Node node2 = null;
        String string = "";
        while (node != null) {
            IXMLEncodable iXMLEncodable;
            AbstractDatabaseEntry abstractDatabaseEntry;
            string = XMLUtil.parseAttribute(node, "id", "");
            if (string.equals("")) {
                throw new XMLParseException("invalid attributes: id not set");
            }
            node2 = XMLUtil.getFirstChildByName(node, "Template");
            while (node2 != null) {
                abstractDatabaseEntry = new TermsAndConditionsTemplate((Element)node2.getFirstChild());
                if (!((TermsAndConditionsTemplate)abstractDatabaseEntry).isVerified()) {
                    throw new SignatureException("TermsAndConditionsTemplate cannot be verified!");
                }
                iXMLEncodable = Database.getInstance(class$anon$terms$template$TermsAndConditionsTemplate == null ? TermsAndConditionsResponseHandler.class$("anon.terms.template.TermsAndConditionsTemplate") : class$anon$terms$template$TermsAndConditionsTemplate);
                ((Database)iXMLEncodable).update(abstractDatabaseEntry);
                node2 = (Element)XMLUtil.getNextSiblingByName(node2, "Template");
            }
            node2 = XMLUtil.getFirstChildByName(node, "CustomizedSections");
            while (node2 != null) {
                abstractDatabaseEntry = (ServiceOperator)Database.getInstance(class$anon$infoservice$ServiceOperator == null ? TermsAndConditionsResponseHandler.class$("anon.infoservice.ServiceOperator") : class$anon$infoservice$ServiceOperator).getEntryById(string.toUpperCase());
                if (abstractDatabaseEntry == null) {
                    throw new XMLParseException("invalid id " + string + ": no operator found with this subject key identifier");
                }
                iXMLEncodable = TermsAndConditions.getTermsAndConditions((ServiceOperator)abstractDatabaseEntry);
                if (iXMLEncodable == null) {
                    throw new IllegalStateException("a tc container for operator " + ((ServiceOperator)abstractDatabaseEntry).getOrganization() + " must exist but does not!");
                }
                try {
                    ((TermsAndConditions)iXMLEncodable).addTranslation((Element)XMLUtil.getFirstChildByName(node2, "TCTranslation"));
                }
                catch (SignatureException signatureException) {
                    LogHolder.log(3, LogType.MISC, "Signature validition error while receiving mix tc answer: ", signatureException);
                }
                node2 = (Element)XMLUtil.getNextSiblingByName(node2, "CustomizedSections");
            }
            node = XMLUtil.getNextSiblingByName(node, "Resources");
        }
        termsAndConditionsRequest.checkRequestPostCondition();
    }

    public void notifyAboutChanges() {
        this.setChanged();
        this.notifyObservers();
    }

    public static TermsAndConditionsResponseHandler get() {
        return SINGLETON;
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

