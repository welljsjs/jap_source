/*
 * Decompiled with CFR 0.150.
 */
package jap;

import jap.ISplashResponse;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.ColorModel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class JAPSplash
extends Window
implements ISplashResponse {
    private static final long serialVersionUID = 1L;
    private static final String IMGPATHHICOLOR = "images/";
    private static final String IMGPATHLOWCOLOR = "images/lowcolor/";
    private static final String SPLASH_FILE = "splash.jpg";
    private static final String BUSY_FILE = "busy.gif";
    private static final int SPLASH_WIDTH = 501;
    private static final int SPLASH_HEIGHT = 330;
    private static final int SPLASH_FILESIZE = 150000;
    private static final int BUSY_FILESIZE = 7000;
    private static final int VERSION_OFFSET_X = 10;
    private static final int VERSION_OFFSET_Y = 15;
    private static final int BUSY_POSITION_X = 15;
    private static final int BUSY_POSITION_Y = 312;
    private static final int MESSAGE_POSITION_X = 17;
    private static final int MESSAGE_POSITION_Y = 302;
    private Image m_imgSplash;
    private Image m_imgBusy;
    private Image m_imgOffScreen = null;
    private Font m_fntFont;
    private String m_strLoading;
    private String m_currentText;
    private String m_strVersion;
    private int m_iXVersion;
    private int m_iYVersion;

    public JAPSplash(Frame frame) {
        this(frame, null);
    }

    public JAPSplash(Frame frame, String string) {
        super(frame);
        this.setLayout(null);
        this.m_iYVersion = 100;
        this.m_iXVersion = 100;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        MediaTracker mediaTracker = new MediaTracker(this);
        this.loadImages(mediaTracker);
        if (string == null || string.trim().length() == 0) {
            this.setText("Busy");
        } else {
            this.setText(string);
        }
        this.m_strVersion = "Version: 00.20.001";
        this.m_fntFont = new Font("Sans", 0, 9);
        FontMetrics fontMetrics = toolkit.getFontMetrics(this.m_fntFont);
        this.m_iXVersion = 491 - fontMetrics.stringWidth(this.m_strVersion);
        this.m_iYVersion = 315;
        this.setSize(501, 330);
        try {
            mediaTracker.waitForAll();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.toFront();
    }

    private Image loadImage(String string, int n, MediaTracker mediaTracker) {
        InputStream inputStream = null;
        Class<?> class_ = null;
        try {
            class_ = Class.forName("JAP");
        }
        catch (Exception exception) {
            // empty catch block
        }
        inputStream = class_.getResourceAsStream(string);
        if (inputStream == null) {
            try {
                inputStream = new FileInputStream(string);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        Image image = null;
        if (inputStream != null) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            byte[] arrby = new byte[n];
            int n2 = 0;
            int n3 = 0;
            try {
                while ((n2 = inputStream.read(arrby, n3, arrby.length - n3)) > 0) {
                    n3 += n2;
                }
                image = toolkit.createImage(arrby, 0, n3);
                mediaTracker.addImage(this.m_imgSplash, 1);
                mediaTracker.checkID(1, true);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return image;
    }

    private boolean isHighColor() {
        ColorModel colorModel = null;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        try {
            colorModel = toolkit.getColorModel();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (colorModel == null) {
            return false;
        }
        return colorModel.getPixelSize() > 16;
    }

    private void loadImages(MediaTracker mediaTracker) {
        if (this.isHighColor()) {
            this.m_imgSplash = this.loadImage("images/splash.jpg", 150000, mediaTracker);
            this.m_imgBusy = this.loadImage("images/busy.gif", 7000, mediaTracker);
        } else {
            this.m_imgSplash = this.loadImage("images/lowcolor/splash.jpg", 150000, mediaTracker);
            this.m_imgBusy = this.loadImage("images/lowcolor/busy.gif", 7000, mediaTracker);
        }
    }

    public void setText(String string) {
        if (string != null && string.trim().length() > 0) {
            this.m_currentText = string;
            this.m_strLoading = string + "...";
        }
    }

    public String getText() {
        return this.m_currentText;
    }

    public void update(Graphics graphics) {
        this.paint(graphics);
    }

    public void paint(Graphics graphics) {
        if (this.m_imgOffScreen == null) {
            this.m_imgOffScreen = this.createImage(501, 330);
        }
        Graphics graphics2 = this.m_imgOffScreen.getGraphics();
        if (this.m_imgSplash != null) {
            graphics2.drawImage(this.m_imgSplash, 0, 0, this);
        }
        if (this.m_imgBusy != null) {
            graphics2.drawImage(this.m_imgBusy, 15, 312, this);
        }
        graphics2.setColor(Color.gray);
        graphics2.drawRect(0, 0, 500, 329);
        graphics2.setFont(this.m_fntFont);
        graphics2.setColor(Color.black);
        graphics2.drawString(this.m_strLoading, 17, 302);
        graphics2.drawString(this.m_strVersion, this.m_iXVersion, this.m_iYVersion);
        graphics.drawImage(this.m_imgOffScreen, 0, 0, this);
    }

    public void centerOnScreen() {
        JAPSplash.centerOnScreen(this);
    }

    private static void centerOnScreen(Window window) {
        Rectangle rectangle;
        Dimension dimension = window.getSize();
        try {
            Object object = Class.forName("java.awt.GraphicsEnvironment").getMethod("getLocalGraphicsEnvironment", null).invoke(null, null);
            Object object2 = object.getClass().getMethod("getDefaultScreenDevice", null).invoke(object, null);
            Object object3 = object2.getClass().getMethod("getDefaultConfiguration", null).invoke(object2, null);
            rectangle = (Rectangle)object3.getClass().getMethod("getBounds", null).invoke(object3, null);
        }
        catch (Exception exception) {
            rectangle = new Rectangle(new Point(0, 0), window.getToolkit().getScreenSize());
        }
        window.setLocation(rectangle.x + (rectangle.width - dimension.width) / 2, rectangle.y + (rectangle.height - dimension.height) / 2);
    }
}

