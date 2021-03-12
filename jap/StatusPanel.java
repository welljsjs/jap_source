/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.IStatusLine;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import logging.LogHolder;
import logging.LogType;

public class StatusPanel
extends JPanel
implements Runnable,
IStatusLine {
    private static final String MSG_CLICK_HERE = (class$jap$StatusPanel == null ? (class$jap$StatusPanel = StatusPanel.class$("jap.StatusPanel")) : class$jap$StatusPanel).getName() + "_clickHere";
    private final Object SYNC_MSG = new Object();
    private Random m_Random;
    private JLabel m_button;
    private static final int ICON_HEIGHT = 15;
    private static final int ICON_WIDTH = 16;
    private Image m_imageError;
    private Image m_imageInformation = GUIUtils.loadImageIcon("information.gif", true, false).getImage();
    private Image m_imageWarning;
    private MessagesListNode m_firstMessage;
    private volatile boolean m_bRun;
    private volatile int m_aktY;
    private Thread m_Thread;
    static /* synthetic */ Class class$jap$StatusPanel;

    public StatusPanel(JLabel jLabel) {
        this.m_imageError = GUIUtils.loadImageIcon("error.gif", true, false).getImage();
        this.m_imageWarning = GUIUtils.loadImageIcon("warning.gif", true, false).getImage();
        this.m_button = jLabel;
        if (this.m_button != null) {
            this.m_button.addMouseListener(new MouseAdapter(){
                boolean m_bClicked = false;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void mouseClicked(MouseEvent mouseEvent) {
                    if (mouseEvent.getClickCount() > 1 || SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isPopupTrigger()) {
                        return;
                    }
                    ButtonListener buttonListener = null;
                    if (this.m_bClicked) {
                        return;
                    }
                    this.m_bClicked = true;
                    Object object = StatusPanel.this.SYNC_MSG;
                    synchronized (object) {
                        MessagesListNode messagesListNode = StatusPanel.this.m_firstMessage;
                        if (messagesListNode != null && messagesListNode.buttonAction != null) {
                            buttonListener = messagesListNode.buttonAction;
                        }
                    }
                    if (buttonListener != null) {
                        buttonListener.actionPerformed(new ActionEvent(StatusPanel.this, mouseEvent.getID(), "mouseClicked"));
                        StatusPanel.this.repaint();
                    }
                    this.m_bClicked = false;
                }
            });
        }
        this.addMouseListener(new MouseAdapter(){
            boolean m_bClicked = false;

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1 || SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isPopupTrigger()) {
                    return;
                }
                ActionListener actionListener = null;
                if (this.m_bClicked) {
                    return;
                }
                this.m_bClicked = true;
                Object object = StatusPanel.this.SYNC_MSG;
                synchronized (object) {
                    MessagesListNode messagesListNode = StatusPanel.this.m_firstMessage;
                    if (messagesListNode != null) {
                        actionListener = messagesListNode.listener;
                    }
                }
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(StatusPanel.this, mouseEvent.getID(), "mouseClicked"));
                    StatusPanel.this.repaint();
                }
                this.m_bClicked = false;
            }
        });
        this.m_Random = new Random();
        this.setLayout(null);
        this.m_firstMessage = null;
        this.m_Thread = new Thread((Runnable)this, "StatusPanel");
        this.m_Thread.setDaemon(true);
        this.m_bRun = true;
        this.m_Thread.start();
    }

    public void finalize() {
        this.m_bRun = false;
        try {
            this.m_Thread.interrupt();
            this.m_Thread.join();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public int addStatusMsg(String string, int n, boolean bl) {
        return this.addStatusMsg(string, n, bl, null, null);
    }

    public int addStatusMsg(String string, int n, boolean bl, ActionListener actionListener) {
        return this.addStatusMsg(string, n, bl, actionListener, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int addStatusMsg(String string, int n, boolean bl, ActionListener actionListener, ButtonListener buttonListener) {
        MessagesListNode messagesListNode = null;
        Object object = this.SYNC_MSG;
        synchronized (object) {
            messagesListNode = new MessagesListNode();
            messagesListNode.listener = actionListener;
            messagesListNode.buttonAction = buttonListener;
            messagesListNode.m_Msg = string;
            messagesListNode.m_Id = Math.abs(this.m_Random.nextInt());
            if (bl) {
                messagesListNode.m_DisplayCount = 2;
            }
            if (n == 2) {
                messagesListNode.m_Icon = this.m_imageWarning;
            } else if (n == 1) {
                messagesListNode.m_Icon = this.m_imageInformation;
            } else if (n == 0) {
                messagesListNode.m_Icon = this.m_imageError;
            }
            if (this.m_firstMessage == null) {
                this.m_firstMessage = messagesListNode;
                messagesListNode.m_Next = messagesListNode;
                this.m_aktY = 15;
            } else {
                messagesListNode.m_Next = this.m_firstMessage.m_Next;
                this.m_firstMessage.m_Next = messagesListNode;
            }
            this.m_Thread.interrupt();
        }
        return messagesListNode.m_Id;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeStatusMsg(int n) {
        Object object = this.SYNC_MSG;
        synchronized (object) {
            if (this.m_firstMessage == null) {
                LogHolder.log(7, LogType.PAY, "Could not remove message with id of " + n + " since there are no messages at all");
                this.m_aktY = 15;
                return;
            }
            if (this.m_firstMessage.m_Id == n && this.m_firstMessage.m_Next == this.m_firstMessage) {
                this.m_firstMessage = null;
                this.m_aktY = 15;
                this.m_Thread.interrupt();
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                this.m_Thread.interrupt();
            } else {
                MessagesListNode messagesListNode = this.m_firstMessage;
                MessagesListNode messagesListNode2 = null;
                while (messagesListNode != null) {
                    if (messagesListNode.m_Next.m_Id == n) {
                        messagesListNode2 = messagesListNode;
                        messagesListNode = messagesListNode.m_Next;
                        break;
                    }
                    messagesListNode = messagesListNode.m_Next;
                    if (messagesListNode != this.m_firstMessage) continue;
                    return;
                }
                if (messagesListNode == this.m_firstMessage) {
                    this.m_firstMessage = messagesListNode.m_Next;
                    this.m_aktY = 15;
                    this.m_Thread.interrupt();
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    this.m_Thread.interrupt();
                }
                messagesListNode2.m_Next = messagesListNode.m_Next;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void paint(Graphics graphics) {
        if (graphics == null) {
            return;
        }
        super.paint(graphics);
        Object object = this.SYNC_MSG;
        synchronized (object) {
            if (this.m_firstMessage != null) {
                String string = this.m_firstMessage.m_Msg;
                if (this.m_firstMessage.buttonAction != null && !this.m_button.isVisible()) {
                    this.m_button.setVisible(this.m_firstMessage.buttonAction.isButtonShown());
                } else if (this.m_firstMessage.buttonAction == null && this.m_button.isVisible()) {
                    this.m_button.setVisible(false);
                }
                if (this.m_firstMessage.listener != null) {
                    this.setCursor(Cursor.getPredefinedCursor(12));
                    string = string + " (" + JAPMessages.getString(MSG_CLICK_HERE) + ")";
                    this.setToolTipText(JAPMessages.getString(MSG_CLICK_HERE));
                } else {
                    this.setToolTipText(null);
                    this.setCursor(Cursor.getDefaultCursor());
                }
                graphics.drawString(string, 18, graphics.getFont().getSize() - this.m_aktY);
                if (this.m_firstMessage.m_Icon != null) {
                    graphics.drawImage(this.m_firstMessage.m_Icon, 0, (this.getSize().height - this.m_firstMessage.m_Icon.getHeight(this)) / 2 - this.m_aktY, this);
                }
            } else {
                this.setToolTipText(null);
                this.setCursor(Cursor.getDefaultCursor());
                this.m_button.setVisible(false);
            }
        }
    }

    public Dimension getPreferredSize() {
        if (this.m_button != null) {
            return new Dimension(100, Math.max(18, this.m_button.getSize().height));
        }
        return new Dimension(100, 18);
    }

    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        try {
            while (this.m_bRun) {
                MessagesListNode messagesListNode;
                block21: {
                    messagesListNode = null;
                    try {
                        Thread.sleep(10000L);
                    }
                    catch (InterruptedException interruptedException) {
                        if (this.m_bRun) break block21;
                        return;
                    }
                }
                Object object = this.SYNC_MSG;
                synchronized (object) {
                    if (this.m_firstMessage != null && this.m_firstMessage.m_DisplayCount == 0) {
                        this.removeStatusMsg(this.m_firstMessage.m_Id);
                    }
                    if (this.m_firstMessage == null) {
                        this.repaint();
                        continue;
                    }
                    if (this.m_firstMessage.m_DisplayCount > 0) {
                        messagesListNode = this.m_firstMessage;
                        --this.m_firstMessage.m_DisplayCount;
                    }
                    if (this.m_firstMessage == null) {
                        this.m_aktY = 15;
                        this.repaint();
                        continue;
                    }
                    if (this.m_firstMessage.m_Next == this.m_firstMessage && this.m_firstMessage.listener != null && this.m_aktY == 0) {
                        this.repaint();
                        continue;
                    }
                    this.m_firstMessage = this.m_firstMessage.m_Next;
                    this.m_aktY = 15;
                }
                for (int i = 0; i < 15 && this.m_bRun; ++i) {
                    try {
                        Thread.sleep(100L);
                        --this.m_aktY;
                        this.repaint();
                        continue;
                    }
                    catch (InterruptedException interruptedException) {
                        Object object2 = this.SYNC_MSG;
                        synchronized (object2) {
                            if (this.m_firstMessage != null) {
                                if (this.m_firstMessage.m_DisplayCount >= 0 && this.m_firstMessage == messagesListNode) {
                                    ++this.m_firstMessage.m_DisplayCount;
                                }
                                this.m_aktY = 15;
                                i = -1;
                                this.m_firstMessage = this.m_firstMessage.m_Next;
                            }
                            continue;
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
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

    private final class MessagesListNode {
        ActionListener listener;
        ButtonListener buttonAction;
        String m_Msg;
        Image m_Icon;
        int m_Id;
        MessagesListNode m_Next;
        int m_DisplayCount = -1;

        private MessagesListNode() {
        }
    }

    public static abstract class ButtonListener
    implements ActionListener {
        public boolean isButtonShown() {
            return true;
        }

        public abstract /* synthetic */ void actionPerformed(ActionEvent var1);
    }
}

