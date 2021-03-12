/*
 * Decompiled with CFR 0.150.
 */
package gui;

import gui.dialog.JAPDialog;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.io.StringWriter;
import java.io.Writer;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.MinimalHTMLWriter;

public class JAPHtmlMultiLineLabel
extends JLabel {
    public static final int FONT_STYLE_PLAIN = 0;
    public static final int FONT_STYLE_ITALIC = 2;
    public static final int FONT_STYLE_BOLD = 1;
    public static final String TAG_BREAK = "<br>";
    public static final String TAG_A_OPEN = "<a href=\"\">";
    public static final String TAG_A_CLOSE = "</a>";
    public static final int UNLIMITED_LABEL_HEIGHT = 5000;
    private static final String TAG_HTML_OPEN = "<html>";
    private static final String TAG_HTML_CLOSE = "</html>";
    private static final String TAG_BODY_OPEN = "<body>";
    private static final String TAG_BODY_CLOSE = "</body>";
    private static final String TAG_HEAD_OPEN = "<head>";
    private static final String TAG_HEAD_CLOSE = "</head>";
    private static final String CLIENT_PROPERTY_HTML = "html";
    private static final String CURRENT_JAVA_VENDOR = System.getProperty("java.vendor");
    private static final String CURRENT_JAVA_VERSION = System.getProperty("java.version");
    private static final boolean HTML_COMPATIBILITY_MODE;
    private boolean m_bInitialised = false;
    private String m_rawText;

    public JAPHtmlMultiLineLabel(String string, Font font, int n) {
        super("", n);
        this.m_rawText = string;
        this.setFont(font);
    }

    public JAPHtmlMultiLineLabel(String string, Font font) {
        this(string, font, 2);
    }

    public JAPHtmlMultiLineLabel(String string, int n) {
        this(string, (Font)null, n);
    }

    public JAPHtmlMultiLineLabel(int n) {
        this((String)null, (Font)null, n);
    }

    public JAPHtmlMultiLineLabel(String string) {
        this(string, (Font)null, 2);
    }

    public JAPHtmlMultiLineLabel() {
        this("", (Font)null, 2);
    }

    public void setText(String string) {
        if (!this.m_bInitialised) {
            this.m_bInitialised = true;
            super.setText(JAPHtmlMultiLineLabel.formatTextAsHTML(string, this.getFont()));
        } else {
            this.m_rawText = string;
            this.setFont(this.getFont());
        }
    }

    public int getHTMLDocumentLength() {
        return ((View)this.getClientProperty(CLIENT_PROPERTY_HTML)).getDocument().getLength();
    }

    public String getHTMLDocumentText() {
        String string;
        HTMLDocument hTMLDocument = (HTMLDocument)((View)this.getClientProperty(CLIENT_PROPERTY_HTML)).getDocument();
        try {
            string = hTMLDocument.getText(0, hTMLDocument.getLength());
            if (string.charAt(string.length() - 1) == '\n') {
                string = string.substring(0, string.length() - 1);
            }
            if (string.charAt(0) == '\n') {
                string = string.substring(1, string.length());
            }
        }
        catch (BadLocationException badLocationException) {
            string = null;
        }
        return string;
    }

    public void cutHTMLDocument(int n) {
        StringWriter stringWriter = new StringWriter();
        HTMLDocument hTMLDocument = (HTMLDocument)((View)this.getClientProperty(CLIENT_PROPERTY_HTML)).getDocument();
        if (n > hTMLDocument.getLength()) {
            return;
        }
        if (n <= 0) {
            this.setText("");
            return;
        }
        try {
            hTMLDocument.remove(n, hTMLDocument.getLength() - n);
            MinimalHTMLWriter minimalHTMLWriter = new MinimalHTMLWriter((Writer)stringWriter, hTMLDocument);
            minimalHTMLWriter.write();
            this.setText(stringWriter.toString());
        }
        catch (Exception exception) {
            JAPDialog.showErrorDialog((Component)this, (Throwable)exception);
        }
    }

    public void setPreferredWidth(int n) {
        View view = (View)this.getClientProperty(CLIENT_PROPERTY_HTML);
        float f = view.getPreferredSpan(0);
        float f2 = view.getPreferredSpan(1);
        try {
            view.setSize(n, 5000.0f);
        }
        catch (NullPointerException nullPointerException) {
            PlainView plainView = new PlainView(view.getElement()){

                public Container getContainer() {
                    return null;
                }
            };
            view.getView(0).setParent(plainView);
            view.setSize(f, f2);
            view.setSize(n, 5000.0f);
            view.getView(0).setParent(view);
        }
        this.invalidate();
    }

    public void setFontStyle(int n) {
        this.setFont(new Font(this.getFont().getName(), n, this.getFont().getSize()));
    }

    public void setFont(Font font) {
        if (font == null) {
            font = new JLabel().getFont();
        }
        if (HTML_COMPATIBILITY_MODE && font.isBold() && font.getSize() >= 16 && font.getSize() <= 18) {
            Font font2 = new Font(font.getName(), 0, font.getSize());
            super.setFont(font2);
            super.setText(JAPHtmlMultiLineLabel.formatTextAsHTML(this.m_rawText, font2));
        } else {
            super.setFont(font);
            super.setText(JAPHtmlMultiLineLabel.formatTextAsHTML(this.m_rawText, font));
        }
    }

    public static String formatTextAsHTML(String string, Font font) {
        if (string == null) {
            return string;
        }
        if (string.trim().length() == 0) {
            return "";
        }
        if (font == null) {
            font = new JLabel().getFont();
        }
        int n = font.getSize();
        String string2 = "-1";
        string2 = n < 13 ? "-1" : (n < 16 ? "+0" : (n < 19 ? "+1" : (n < 26 ? "+2" : "+3")));
        String string3 = TAG_HTML_OPEN + TAG_BODY_OPEN.substring(0, TAG_BODY_OPEN.length() - 1) + (HTML_COMPATIBILITY_MODE ? "><font size=" + string2 + ">" : " style=\"font-size:" + n + "pt;" + "font-family:" + font.getFamily() + "\">");
        String string4 = "</body></html>";
        if (font.isBold()) {
            string3 = string3 + "<b>";
            string4 = "</b>" + string4;
        }
        return string3 + JAPHtmlMultiLineLabel.removeHTMLHEADAndBODYTags(string) + string4;
    }

    public static String removeTagsAndNewLines(String string) {
        if (string == null) {
            return null;
        }
        String string2 = string;
        while (true) {
            int n = string2.indexOf("<");
            int n2 = string2.indexOf(">");
            if (n < 0 && n2 < 0) break;
            if (n2 >= 0 && (n < 0 || n2 < n)) {
                n = n2;
            } else if (n2 < 0) {
                n2 = n;
            }
            if (++n2 >= string2.length()) {
                string2 = string2.substring(0, n);
                continue;
            }
            string2 = string2.substring(0, n) + string2.substring(n2, string2.length());
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string2, "\t\n\r\f");
        string2 = "";
        while (stringTokenizer.hasMoreTokens()) {
            String string3 = stringTokenizer.nextToken();
            string2 = string2 + string3;
        }
        return string2.trim();
    }

    public static String removeHTMLHEADAndBODYTags(String string) {
        if (string == null || string.trim().length() == 0) {
            return string;
        }
        String string2 = string.toLowerCase();
        int n = string2.indexOf(TAG_HEAD_OPEN.substring(0, TAG_HEAD_OPEN.length() - 1));
        int n2 = string2.indexOf(TAG_HEAD_CLOSE);
        if (n >= 0 || n2 >= 0) {
            if (n < 0 || n2 < 0 || n >= n2) {
                return "";
            }
            int n3 = n2 + TAG_HEAD_CLOSE.length();
            string = n == 0 ? string.substring(n3, string.length() - n3) : (n3 == string.length() ? string.substring(0, n) : string.substring(0, n) + string.substring(n3, string.length()));
        }
        return JAPHtmlMultiLineLabel.removeTAG(JAPHtmlMultiLineLabel.removeTAG(string, TAG_HTML_OPEN, TAG_HTML_CLOSE), TAG_BODY_OPEN, TAG_BODY_CLOSE);
    }

    private static String removeTAG(String string, String string2, String string3) {
        if (string == null || (string = string.trim()).length() == 0) {
            return string;
        }
        String string4 = string2.substring(0, string2.length() - 1);
        String string5 = string.toLowerCase();
        int n = 0;
        int n2 = string.length();
        if (string5.startsWith(string4)) {
            n = string5.indexOf(">") + 1;
        }
        if (string5.endsWith(string3)) {
            n2 -= string3.length();
        }
        if (n > 0 || n2 < string.length()) {
            string = n < 0 || n2 < 0 || n >= n2 ? "" : string.substring(n, n2).trim();
        }
        return string;
    }

    static {
        String string = System.getProperty("java.version");
        HTML_COMPATIBILITY_MODE = string != null && string.compareTo("1.3") < 0;
    }
}

