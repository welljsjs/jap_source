/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JEditorPane;

public final class JAPAboutAutoScroller
extends Canvas
implements Runnable {
    private static final long serialVersionUID = 1L;
    private Image m_imgOffScreen;
    private Image m_imgBackground;
    private Image m_imgDoubleBuffer;
    private Image m_imgBackgroundPicture;
    private int m_iScrollAreaWidth;
    private int m_iScrollAreaHeight;
    private int m_iScrollAreaX;
    private int m_iScrollAreaY;
    private int m_iaktY;
    private int m_iTextHeight;
    private int m_iWidth;
    private int m_iHeight;
    private JEditorPane m_textArea;
    private Thread m_Thread;
    private int m_msSleep;
    private volatile boolean m_bRun;
    private Object oSync = new Object();
    private boolean isPainting = false;
    private JButton m_bttnOk;

    public JAPAboutAutoScroller(int n, int n2, Image image, int n3, int n4, int n5, int n6, String string) {
        this.m_iScrollAreaWidth = n5;
        this.m_iScrollAreaHeight = n6;
        this.m_iScrollAreaX = n3;
        this.m_iScrollAreaY = n4;
        this.m_iWidth = n;
        this.m_iHeight = n2;
        this.setSize(n, n2);
        this.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent mouseEvent) {
                if (JAPAboutAutoScroller.this.m_bttnOk.getBounds().contains(mouseEvent.getPoint())) {
                    JAPAboutAutoScroller.this.m_bttnOk.doClick();
                }
            }
        });
        this.m_imgBackgroundPicture = image;
        this.m_textArea = new JEditorPane();
        this.m_textArea.setEditable(false);
        this.m_textArea.setDoubleBuffered(false);
        this.m_textArea.setBackground(new Color(204, 204, 204));
        this.m_textArea.setSize(this.m_iScrollAreaWidth, 10000);
        this.m_textArea.setContentType("text/html");
        this.m_textArea.setText(string.trim());
        this.m_iTextHeight = this.m_textArea.getPreferredSize().height;
        this.m_bttnOk = new JButton("Ok");
        this.m_bttnOk.setMnemonic('O');
        this.m_bttnOk.setOpaque(false);
        this.m_bttnOk.setSelected(true);
        Dimension dimension = this.m_bttnOk.getPreferredSize();
        if (dimension.width > 76) {
            dimension.width = 76;
        }
        this.m_bttnOk.setSize(dimension);
        this.m_Thread = new Thread((Runnable)this, "JAP - AboutScroller");
        this.m_bRun = false;
    }

    public void addActionListener(ActionListener actionListener) {
        this.m_bttnOk.addActionListener(actionListener);
    }

    public synchronized void startScrolling(int n) {
        if (this.m_bRun) {
            return;
        }
        this.m_msSleep = n;
        this.m_Thread.start();
    }

    public synchronized void stopScrolling() {
        this.m_bRun = false;
        try {
            this.m_Thread.join();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void update(Graphics graphics) {
        this.paint(graphics);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void paint(Graphics graphics) {
        if (graphics == null) {
            return;
        }
        Object object = this.oSync;
        synchronized (object) {
            if (this.isPainting) {
                return;
            }
            this.isPainting = true;
        }
        if (this.m_imgOffScreen == null) {
            this.m_imgOffScreen = this.createImage(this.m_iScrollAreaWidth, this.m_iTextHeight + 2 * this.m_iScrollAreaHeight);
            object = this.m_imgOffScreen.getGraphics();
            try {
                this.m_textArea.paint((Graphics)object);
            }
            catch (Exception exception) {
                this.m_imgOffScreen = null;
            }
            if (object != null) {
                ((Graphics)object).dispose();
            }
        }
        if (this.m_imgBackground == null) {
            this.m_imgBackground = this.createImage(this.m_iWidth, this.m_iHeight);
            object = this.m_imgBackground.getGraphics();
            ((Graphics)object).drawImage(this.m_imgBackgroundPicture, 0, 0, null);
            int n = this.m_iWidth - 5 - this.m_bttnOk.getSize().width;
            int n2 = this.m_iHeight - 5 - this.m_bttnOk.getSize().height;
            this.m_bttnOk.setLocation(n, n2);
            Font font = new Font("Sans", 0, 9);
            ((Graphics)object).setFont(font);
            ((Graphics)object).setColor(Color.black);
            FontMetrics fontMetrics = ((Graphics)object).getFontMetrics();
            int n3 = fontMetrics.stringWidth("Version:");
            ((Graphics)object).drawString("Version", n - 5 - n3, n2);
            n3 = fontMetrics.stringWidth("00.20.001");
            ((Graphics)object).drawString("00.20.001", n - 5 - n3, this.m_iHeight - 5 - fontMetrics.getHeight());
            ((Graphics)object).translate(n, n2);
            this.m_bttnOk.paint((Graphics)object);
            ((Graphics)object).dispose();
            this.m_imgDoubleBuffer = this.createImage(this.m_iWidth, this.m_iHeight);
        }
        object = this.m_imgDoubleBuffer.getGraphics();
        ((Graphics)object).drawImage(this.m_imgBackground, 0, 0, null);
        if (this.m_imgOffScreen != null) {
            ++this.m_iaktY;
            if (this.m_iaktY <= this.m_iScrollAreaHeight) {
                ((Graphics)object).drawImage(this.m_imgOffScreen, this.m_iScrollAreaX, this.m_iScrollAreaY + this.m_iScrollAreaHeight - this.m_iaktY, this.m_iScrollAreaX + this.m_iScrollAreaWidth, this.m_iScrollAreaY + this.m_iScrollAreaHeight, 0, 0, this.m_iScrollAreaWidth, this.m_iaktY, null);
            } else {
                ((Graphics)object).drawImage(this.m_imgOffScreen, this.m_iScrollAreaX, this.m_iScrollAreaY, this.m_iScrollAreaWidth + this.m_iScrollAreaX, this.m_iScrollAreaHeight + this.m_iScrollAreaY, 0, this.m_iaktY - this.m_iScrollAreaHeight, this.m_iScrollAreaWidth, this.m_iaktY, null);
            }
        }
        ((Graphics)object).dispose();
        graphics.drawImage(this.m_imgDoubleBuffer, 0, 0, null);
        this.isPainting = false;
    }

    public void run() {
        this.m_iaktY = 0;
        this.m_bRun = true;
        while (this.m_bRun) {
            this.paint(this.getGraphics());
            try {
                Thread.sleep(this.m_msSleep);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (this.m_iaktY <= this.m_iTextHeight + this.m_iScrollAreaHeight) continue;
            this.m_iaktY = 0;
        }
    }
}

