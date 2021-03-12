/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.pay.PayAccountsFile;
import anon.util.JAPMessages;
import gui.GUIUtils;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JapCouponField
extends JTextField {
    private static final long serialVersionUID = 1L;
    private static final int NR_OF_CHARACTERS = 4;
    private static final char[] ACCEPTED_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private JapCouponField m_nextCouponField;
    private boolean m_bFirstField = false;
    private static final String MSG_INSERT_FROM_CLIP = (class$gui$JapCouponField == null ? (class$gui$JapCouponField = JapCouponField.class$("gui.JapCouponField")) : class$gui$JapCouponField).getName() + "_insertFromClip";
    static /* synthetic */ Class class$gui$JapCouponField;

    public JapCouponField(boolean bl) {
        super(4);
        this.m_bFirstField = bl;
        final JPopupMenu jPopupMenu = new JPopupMenu();
        JMenuItem jMenuItem = new JMenuItem(JAPMessages.getString(MSG_INSERT_FROM_CLIP));
        MouseAdapter mouseAdapter = new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isPopupTrigger()) {
                    jPopupMenu.show(JapCouponField.this, mouseEvent.getX(), mouseEvent.getY());
                }
            }
        };
        this.addMouseListener(mouseAdapter);
        jMenuItem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent actionEvent) {
                Clipboard clipboard = GUIUtils.getSystemClipboard();
                Transferable transferable = clipboard.getContents(this);
                if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    try {
                        JapCouponField.this.setText((String)transferable.getTransferData(DataFlavor.stringFlavor));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        });
        jPopupMenu.add(jMenuItem);
    }

    public void setNextCouponField(JapCouponField japCouponField) {
        this.m_nextCouponField = japCouponField;
    }

    protected final Document createDefaultModel() {
        return new CouponDocument();
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private final class CouponDocument
    extends PlainDocument {
        private static final long serialVersionUID = 1L;

        private CouponDocument() {
        }

        public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
            string = string.toUpperCase();
            char[] arrc = string.toCharArray();
            char[] arrc2 = new char[arrc.length];
            int n2 = 0;
            for (int i = 0; i < arrc.length; ++i) {
                if ((!JapCouponField.this.m_bFirstField || n != 0 || i != 0 || !PayAccountsFile.isNewUserLetter(arrc[i]) && !PayAccountsFile.isPromotionLetter(arrc[i])) && !this.isCharacterAccepted(arrc[i])) continue;
                arrc2[n2] = arrc[i];
                ++n2;
            }
            string = new String(arrc2, 0, n2);
            if (string.length() + this.getLength() > 4) {
                if (4 <= string.length()) {
                    if (JapCouponField.this.m_nextCouponField != null && 4 < string.length()) {
                        JapCouponField.this.m_nextCouponField.setText(string.substring(4, string.length()));
                    }
                    string = string.substring(0, 4);
                    super.insertString(0, string, attributeSet);
                } else if (n + string.length() <= 4) {
                    super.writeLock();
                    super.remove(n, string.length());
                    super.insertString(n, string, attributeSet);
                    super.writeUnlock();
                } else if (n < 4) {
                    super.writeLock();
                    super.remove(n, 4 - n);
                    super.insertString(n, string.substring(0, 4 - n), attributeSet);
                    super.writeUnlock();
                }
            } else {
                super.insertString(n, string, attributeSet);
            }
            if (this.getLength() >= 4) {
                if (this.getLength() > 4) {
                    super.remove(4, this.getLength() - 4);
                }
                if (JapCouponField.this.getCaretPosition() >= 4) {
                    JapCouponField.this.setCaretPosition(0);
                    JapCouponField.this.transferFocus();
                }
            }
        }

        private boolean isCharacterAccepted(char c) {
            for (int i = 0; i < ACCEPTED_CHARS.length; ++i) {
                if (c != ACCEPTED_CHARS[i]) continue;
                return true;
            }
            return false;
        }
    }
}

