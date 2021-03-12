/*
 * Decompiled with CFR 0.150.
 */
package gui;

import gui.ClipboardCopier;
import gui.dialog.JAPDialog;
import java.awt.Cursor;
import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

public class JapHtmlPane
extends JScrollPane
implements HyperlinkListener {
    private JEditorPane html;
    private URL url;
    private Cursor cursor;
    private static final String MAILTO = "mailto";

    public JapHtmlPane(String string, JViewport jViewport) {
        this.html = new JEditorPane("text/html", string);
        new ClipboardCopier(true).register(this.html);
        this.html.setEditable(false);
        this.html.addHyperlinkListener(this);
        if (jViewport != null) {
            this.setViewport(jViewport);
        }
        this.getViewport().add(this.html);
        this.cursor = this.html.getCursor();
    }

    public JEditorPane getPane() {
        return this.html;
    }

    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            this.linkActivated(hyperlinkEvent.getURL());
        } else if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            this.html.setCursor(Cursor.getPredefinedCursor(12));
        } else if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.EXITED) {
            this.html.setCursor(this.cursor);
        }
    }

    private void linkActivated(URL uRL) {
        this.html.setCursor(Cursor.getPredefinedCursor(3));
        SwingUtilities.invokeLater(new PageLoader(uRL));
    }

    private void loadURL(URL uRL) {
        this.html.setCursor(Cursor.getPredefinedCursor(3));
        SwingUtilities.invokeLater(new PageLoader(uRL));
    }

    private final class PageLoader
    implements Runnable {
        PageLoader(URL uRL) {
            JapHtmlPane.this.url = uRL;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            if (JapHtmlPane.this.url == null) {
                JapHtmlPane.this.html.setCursor(JapHtmlPane.this.cursor);
                JapHtmlPane.this.html.getParent().repaint();
            } else if (JapHtmlPane.this.url.getProtocol().startsWith("file") || JapHtmlPane.this.url.getProtocol().startsWith("zip") || JapHtmlPane.this.url.getProtocol().startsWith("jar") || JapHtmlPane.this.url.getProtocol().startsWith("systemresource")) {
                Document document = JapHtmlPane.this.html.getDocument();
                try {
                    JapHtmlPane.this.html.setPage(JapHtmlPane.this.url);
                }
                catch (IOException iOException) {
                    JapHtmlPane.this.html.setDocument(document);
                    JapHtmlPane.this.getToolkit().beep();
                }
                finally {
                    JapHtmlPane.this.url = null;
                    SwingUtilities.invokeLater(this);
                }
            } else {
                boolean bl = true;
                if (!bl) {
                    JapHtmlPane.this.html.setCursor(JapHtmlPane.this.cursor);
                }
            }
        }

        private class ExternalLinkedInformation
        extends JAPDialog.LinkedInformationAdapter {
            private URL m_url;

            public ExternalLinkedInformation(URL uRL) {
                this.m_url = uRL;
            }

            public String getMessage() {
                return this.m_url.toString();
            }

            public int getType() {
                return 2;
            }

            public boolean isApplicationModalityForced() {
                return true;
            }
        }
    }
}

