/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import org.w3c.dom.Node;

public class TermsAndConditionsMixInfo {
    public static final String TNC_MIX_INFO_ROOT = "TermsAndConditionsInfos";
    public static final String TNC_MIX_INFO = "TermsAndConditionsInfo";
    public static final String TNC_MIX_INFO_ID = "id";
    public static final String TNC_MIX_INFO_DATE = "date";
    public static final String TNC_MIX_INFO_LOCALE = "locale";
    public static final String TNC_MIX_INFO_DEFAULT_LANG = "defaultLang";
    public static final String TNC_MIX_INFO_TEMPLATE_REFID = "referenceId";
    private String id = "";
    private String date = "";
    private String defaultLang = "";
    private Hashtable templates = new Hashtable();

    public TermsAndConditionsMixInfo(Node node) throws XMLParseException {
        if (node == null) {
            throw new XMLParseException("T&C Info Node is null");
        }
        this.id = XMLUtil.parseAttribute(node, TNC_MIX_INFO_ID, "");
        if (this.id.equals("")) {
            throw new XMLParseException("T&C Info Node does not contain an ID");
        }
        this.date = XMLUtil.parseAttribute(node, TNC_MIX_INFO_DATE, "");
        if (this.date.equals("")) {
            throw new XMLParseException("T&C Info Node " + this.id + " does not contain a valid date");
        }
        this.defaultLang = XMLUtil.parseAttribute(node, TNC_MIX_INFO_DEFAULT_LANG, "").trim().toLowerCase();
        if (this.defaultLang.equals("")) {
            throw new XMLParseException("T&C Info Node " + this.id + " does not define a default language");
        }
        Node node2 = XMLUtil.getFirstChildByName(node, TNC_MIX_INFO);
        String string = "";
        String string2 = "";
        while (node2 != null) {
            string = XMLUtil.parseAttribute(node2, TNC_MIX_INFO_LOCALE, "");
            string2 = XMLUtil.parseAttribute(node2, TNC_MIX_INFO_TEMPLATE_REFID, "");
            if (!string.equals("") && !string2.equals("")) {
                this.templates.put(string.trim().toLowerCase(), string2);
            }
            node2 = XMLUtil.getNextSiblingByName(node2, TNC_MIX_INFO);
        }
        Enumeration enumeration = this.getLanguages();
    }

    public String getId() {
        return this.id;
    }

    public String getDate() {
        return this.date;
    }

    public String getTemplateRefId(Locale locale) {
        return this.getTemplateRefId(locale.getLanguage());
    }

    public String getTemplateRefId(String string) {
        return (String)this.templates.get(string.trim().toLowerCase());
    }

    public String getDefaultTemplateRefId() {
        return (String)this.templates.get(this.getDefaultLanguage());
    }

    public boolean hasTranslation(String string) {
        return this.templates.get(string.trim().toLowerCase()) != null;
    }

    public boolean hasTranslation(Locale locale) {
        return this.hasTranslation(locale.getLanguage());
    }

    public String getDefaultLanguage() {
        return this.defaultLang;
    }

    public Enumeration getLanguages() {
        return this.templates.keys();
    }
}

