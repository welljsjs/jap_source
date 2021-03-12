/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.util.JAPMessages;
import gui.GUIUtils;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class ClipboardCopier {
    private static final String MSG_COPY_TO_CLIP = (class$gui$ClipboardCopier == null ? (class$gui$ClipboardCopier = ClipboardCopier.class$("gui.ClipboardCopier")) : class$gui$ClipboardCopier).getName() + ".copyToClip";
    private static final String MSG_COPY_SELECTED_TO_CLIP = (class$gui$ClipboardCopier == null ? (class$gui$ClipboardCopier = ClipboardCopier.class$("gui.ClipboardCopier")) : class$gui$ClipboardCopier).getName() + ".copySelectedToClip";
    private JPopupMenu m_popup;
    private Object m_currentPopup;
    private MouseAdapter m_popupListener;
    private Vector m_vecRegistered = new Vector();
    static /* synthetic */ Class class$gui$ClipboardCopier;

    public ClipboardCopier(final boolean bl) {
        this.m_popup = new JPopupMenu();
        this.m_popupListener = new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isPopupTrigger()) {
                    ClipboardCopier.this.m_currentPopup = mouseEvent.getComponent();
                    ClipboardCopier.this.m_popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        };
        JMenuItem jMenuItem = bl ? new JMenuItem(JAPMessages.getString(MSG_COPY_SELECTED_TO_CLIP)) : new JMenuItem(JAPMessages.getString(MSG_COPY_TO_CLIP));
        jMenuItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = GUIUtils.getSystemClipboard();
                String string = ClipboardCopier.this.m_currentPopup instanceof JTextComponent ? (bl ? ((JTextComponent)ClipboardCopier.this.m_currentPopup).getSelectedText() : ((JTextComponent)ClipboardCopier.this.m_currentPopup).getText()) : (ClipboardCopier.this.m_currentPopup instanceof JLabel ? ((JLabel)ClipboardCopier.this.m_currentPopup).getToolTipText() : null);
                if (string != null) {
                    clipboard.setContents(new StringSelection(string), new ClipboardOwner(){

                        public void lostOwnership(Clipboard clipboard, Transferable transferable) {
                        }
                    });
                }
            }
        });
        this.m_popup.add(jMenuItem);
    }

    public synchronized void register(JTextComponent jTextComponent) {
        if (jTextComponent != null) {
            jTextComponent.addMouseListener(this.m_popupListener);
            this.m_vecRegistered.addElement(jTextComponent);
        }
    }

    public synchronized void register(JLabel jLabel) {
        if (jLabel != null) {
            jLabel.addMouseListener(this.m_popupListener);
            this.m_vecRegistered.addElement(jLabel);
        }
    }

    public synchronized void unregisterAll() {
        for (int i = 0; i < this.m_vecRegistered.size(); ++i) {
            ((JComponent)this.m_vecRegistered.elementAt(i)).removeMouseListener(this.m_popupListener);
        }
        this.m_vecRegistered.removeAllElements();
    }

    public synchronized void unregister(JLabel jLabel) {
        if (jLabel != null) {
            jLabel.removeMouseListener(this.m_popupListener);
            this.m_vecRegistered.removeElement(jLabel);
        }
    }

    public synchronized void unregister(JTextComponent jTextComponent) {
        if (jTextComponent != null) {
            jTextComponent.removeMouseListener(this.m_popupListener);
            this.m_vecRegistered.removeElement(jTextComponent);
        }
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

