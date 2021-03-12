/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.platform.AbstractOS;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JLabel;
import logging.LogHolder;
import logging.LogType;

public class LinkMouseListener
extends MouseAdapter {
    private ILinkGenerator m_linkToOpen = null;
    private ILinkCallback m_callback;

    public LinkMouseListener() {
    }

    public LinkMouseListener(String string) {
        this(string, null);
    }

    public LinkMouseListener(String string, ILinkCallback iLinkCallback) {
        this.m_callback = iLinkCallback;
        this.m_linkToOpen = new ImmutableLinkGenerator(string);
    }

    public LinkMouseListener(ILinkGenerator iLinkGenerator) {
        this.m_linkToOpen = iLinkGenerator;
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        String string;
        if (this.m_linkToOpen != null) {
            string = this.m_linkToOpen.createLink();
        } else if (mouseEvent.getSource() instanceof JLabel) {
            string = ((JLabel)mouseEvent.getSource()).getText();
        } else {
            return;
        }
        try {
            URL uRL = new URL(string);
            if (this.m_callback != null) {
                this.m_callback.callback(uRL);
            }
            AbstractOS.getInstance().openURL(uRL);
        }
        catch (ClassCastException classCastException) {
            LogHolder.log(3, LogType.PAY, "opening a link failed, reason: called on non-JLabel component");
        }
        catch (MalformedURLException malformedURLException) {
            LogHolder.log(3, LogType.PAY, "opening a link failed, reason: malformed URL");
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        JComponent jComponent = (JComponent)mouseEvent.getSource();
        jComponent.setCursor(Cursor.getPredefinedCursor(12));
    }

    public void mouseExited(MouseEvent mouseEvent) {
        JComponent jComponent = (JComponent)mouseEvent.getSource();
        jComponent.setCursor(Cursor.getPredefinedCursor(0));
    }

    private class ImmutableLinkGenerator
    implements ILinkGenerator {
        private String m_LinkToOpen;

        public ImmutableLinkGenerator(String string) {
            this.m_LinkToOpen = string;
        }

        public String createLink() {
            return this.m_LinkToOpen;
        }
    }

    public static interface ILinkGenerator {
        public String createLink();
    }

    public static interface ILinkCallback {
        public void callback(URL var1);
    }
}

