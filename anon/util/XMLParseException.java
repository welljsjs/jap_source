/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

public class XMLParseException
extends Exception {
    private static final long serialVersionUID = 1L;
    public static final String ROOT_TAG = "##__root__##";
    public static final String NODE_NULL_TAG = "##__null__##";

    public XMLParseException(String string, String string2) {
        super(XMLParseException.parseTagName(string) + XMLParseException.getMessage(string2));
    }

    public XMLParseException(String string) {
        this(string, (String)null);
    }

    private static String getMessage(String string) {
        if (string == null) {
            return "";
        }
        return string;
    }

    private static String parseTagName(String string) {
        String string2 = "Error while parsing XML ";
        string2 = string == null ? "" : (string.equals(ROOT_TAG) ? string2 + "document root! " : (string.endsWith(NODE_NULL_TAG) ? string2 + "- node is null! " : string2 + "node '" + string + "'! "));
        return string2;
    }
}

