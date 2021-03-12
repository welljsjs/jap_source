/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.infoservice.ServiceLocation;
import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.CountryMapper;
import anon.util.IReturnRunnable;
import anon.util.JAPMessages;
import anon.util.JobQueue;
import anon.util.ResourceLoader;
import gui.ClipFrame;
import gui.ILocationSettings;
import gui.JAPAWTMsgBox;
import gui.JAPHtmlMultiLineLabel;
import gui.dialog.JAPDialog;
import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import logging.LogHolder;
import logging.LogType;

public final class GUIUtils
implements ILocationSettings {
    public static final String aktVersion = "00.00.213";
    public static final String MSG_DEFAULT_IMGAGE_PATH = (class$gui$GUIUtils == null ? (class$gui$GUIUtils = GUIUtils.class$("gui.GUIUtils")) : class$gui$GUIUtils).getName() + "_imagePath";
    public static final String MSG_DEFAULT_IMGAGE_PATH_LOWCOLOR = (class$gui$GUIUtils == null ? (class$gui$GUIUtils = GUIUtils.class$("gui.GUIUtils")) : class$gui$GUIUtils).getName() + "_imagePathLowColor";
    public static final String FILE_CHOOSER_OPEN = "__FILE_CHOOSER_OPEN";
    public static final String FILE_CHOOSER_SAVE = "__FILE_CHOOSER_SAVE";
    private static final String MSG_PASTE_FILE = (class$gui$GUIUtils == null ? (class$gui$GUIUtils = GUIUtils.class$("gui.GUIUtils")) : class$gui$GUIUtils).getName() + "_pasteFile";
    private static final String MSG_COPY_FROM_CLIP = (class$gui$GUIUtils == null ? (class$gui$GUIUtils = GUIUtils.class$("gui.GUIUtils")) : class$gui$GUIUtils).getName() + "_copyFromClip";
    private static final String MSG_SAVED_TO_CLIP = (class$gui$GUIUtils == null ? (class$gui$GUIUtils = GUIUtils.class$("gui.GUIUtils")) : class$gui$GUIUtils).getName() + "_savedToClip";
    private static final int MAXIMUM_TEXT_LENGTH = 60;
    private static boolean ms_loadImages = true;
    private static boolean ms_bCapturingAWTEvents = false;
    private static Point ms_mousePosition;
    private static final Object SYNC_MOUSE_POSITION;
    private static AWTEventListener ms_mouseListener;
    private static final Vector AWT_EVENT_LISTENERS;
    private static final IIconResizer DEFAULT_RESIZER;
    private static IIconResizer ms_resizer;
    private static final NativeGUILibrary DUMMY_GUI_LIBRARY;
    private static NativeGUILibrary ms_nativeGUILibrary;
    private static final IIconResizer RESIZER;
    private static Hashtable ms_iconCache;
    static /* synthetic */ Class class$gui$GUIUtils;
    static /* synthetic */ Class class$java$awt$Window;
    static /* synthetic */ Class class$javax$swing$JComponent;
    static /* synthetic */ Class class$javax$swing$KeyStroke;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$java$awt$Frame;
    static /* synthetic */ Class class$java$awt$Toolkit;
    static /* synthetic */ Class class$javax$swing$LookAndFeel;

    public static final Point getEventLocation(Component component, MouseEvent mouseEvent) {
        Component component2 = null;
        if (mouseEvent.getSource() instanceof Component) {
            component2 = (Component)mouseEvent.getSource();
        }
        Window window = GUIUtils.getParentWindow(component2);
        int n = component.getLocation().x;
        int n2 = component.getLocation().y;
        while (component2 != window && component2 != null) {
            n += component2.getLocation().x;
            n2 += component2.getLocation().y;
            component2 = component2.getParent();
        }
        return new Point(mouseEvent.getX() + n, mouseEvent.getY() + n2);
    }

    public static final IIconResizer getIconResizer() {
        return RESIZER;
    }

    public static void setLoadImages(boolean bl) {
        if (ms_loadImages && !bl) {
            LogHolder.log(5, LogType.GUI, "Loading of images has been stopped!");
        }
        ms_loadImages = bl;
    }

    public static boolean isLoadingImagesStopped() {
        return !ms_loadImages;
    }

    public static final void setIconResizer(IIconResizer iIconResizer) {
        ms_resizer = iIconResizer != null ? iIconResizer : DEFAULT_RESIZER;
    }

    public static ImageIcon loadImageIcon(String string) {
        return GUIUtils.loadImageIcon(string, true, true);
    }

    public static ImageIcon loadImageIcon(String string, boolean bl) {
        return GUIUtils.loadImageIcon(string, bl, true);
    }

    public static ImageIcon loadImageIcon(String string, boolean bl, boolean bl2) {
        Object object;
        ImageIcon imageIcon = null;
        boolean bl3 = false;
        String string2 = null;
        if (string == null) {
            return null;
        }
        if (bl2 && ms_resizer.getResizeFactor() != 1.0) {
            string2 = (int)(100.0 * ms_resizer.getResizeFactor()) + "/" + string;
        }
        if (string2 != null && ms_iconCache.containsKey(string2)) {
            imageIcon = new ImageIcon((Image)ms_iconCache.get(string2));
            if (imageIcon != null) {
                bl3 = true;
            }
        } else if (ms_iconCache.containsKey(string)) {
            imageIcon = new ImageIcon((Image)ms_iconCache.get(string));
        }
        if (imageIcon == null && ms_loadImages) {
            int n;
            if (string2 != null && (imageIcon = GUIUtils.loadImageIconInternal(ResourceLoader.getResourceURL(string2))) != null) {
                bl3 = true;
            }
            if (imageIcon == null) {
                imageIcon = GUIUtils.loadImageIconInternal(ResourceLoader.getResourceURL(string));
            }
            object = null;
            try {
                object = Toolkit.getDefaultToolkit().getColorModel();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (imageIcon == null && (object == null || ((ColorModel)object).getPixelSize() <= 16)) {
                if (string2 != null && (imageIcon = GUIUtils.loadImageIconInternal(ResourceLoader.getResourceURL(JAPMessages.getString(MSG_DEFAULT_IMGAGE_PATH_LOWCOLOR) + string2))) != null) {
                    bl3 = true;
                }
                if (imageIcon == null) {
                    imageIcon = GUIUtils.loadImageIconInternal(ResourceLoader.getResourceURL(JAPMessages.getString(MSG_DEFAULT_IMGAGE_PATH_LOWCOLOR) + string));
                }
            }
            if (imageIcon == null || imageIcon.getImageLoadStatus() == 4) {
                if (string2 != null && (imageIcon = GUIUtils.loadImageIconInternal(ResourceLoader.getResourceURL(JAPMessages.getString(MSG_DEFAULT_IMGAGE_PATH) + string2))) != null) {
                    bl3 = true;
                }
                if (imageIcon == null) {
                    imageIcon = GUIUtils.loadImageIconInternal(ResourceLoader.getResourceURL(JAPMessages.getString(MSG_DEFAULT_IMGAGE_PATH) + string));
                }
            }
            if (imageIcon != null) {
                if (bl) {
                    n = 14;
                    while ((imageIcon.getImageLoadStatus() & n) == 0) {
                        Thread.yield();
                    }
                }
                if (string2 != null && bl3) {
                    ms_iconCache.put(string2, imageIcon.getImage());
                } else {
                    ms_iconCache.put(string, imageIcon.getImage());
                }
            }
            n = 6;
            if (imageIcon == null || (imageIcon.getImageLoadStatus() & n) != 0) {
                LogHolder.log(6, LogType.GUI, "Could not load requested image '" + string + "'!");
            }
        }
        if (bl2 && !bl3 && ms_loadImages && ms_resizer.getResizeFactor() != 1.0) {
            object = imageIcon;
            IReturnRunnable iReturnRunnable = new IReturnRunnable((ImageIcon)object){
                private ImageIcon m_icon;
                private final /* synthetic */ ImageIcon val$image;
                {
                    this.val$image = imageIcon;
                }

                public void run() {
                    this.m_icon = GUIUtils.createScaledImageIcon(this.val$image, ms_resizer);
                }

                public Object getValue() {
                    return this.m_icon;
                }
            };
            Thread thread = new Thread(iReturnRunnable);
            thread.setDaemon(true);
            thread.start();
            try {
                thread.join(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            while (thread.isAlive()) {
                thread.interrupt();
                try {
                    thread.join();
                }
                catch (InterruptedException interruptedException) {}
            }
            if (iReturnRunnable.getValue() != null) {
                return (ImageIcon)iReturnRunnable.getValue();
            }
            if (imageIcon != null && iReturnRunnable.getValue() == null) {
                LogHolder.log(3, LogType.GUI, "Interrupted while scaling image icon!");
            }
        }
        return imageIcon;
    }

    private static ImageIcon loadImageIconInternal(URL uRL) {
        try {
            return new ImageIcon(uRL);
        }
        catch (NullPointerException nullPointerException) {
            return null;
        }
    }

    public static ImageIcon combine(ImageIcon imageIcon, ImageIcon imageIcon2) {
        if (imageIcon == null) {
            return imageIcon2;
        }
        if (imageIcon2 == null) {
            return imageIcon;
        }
        int n = imageIcon.getIconWidth() + imageIcon2.getIconWidth();
        int n2 = Math.max(imageIcon.getIconHeight(), imageIcon2.getIconHeight());
        try {
            Class<?> class_ = Class.forName("java.awt.image.BufferedImage");
            Field field = class_.getField("TYPE_INT_ARGB");
            Constructor<?> constructor = class_.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
            Image image = (Image)constructor.newInstance(new Integer(n), new Integer(n2), new Integer(field.getInt(class_)));
            Graphics graphics = (Graphics)class_.getMethod("createGraphics", null).invoke(image, null);
            graphics.drawImage(imageIcon.getImage(), 0, 0, null);
            graphics.drawImage(imageIcon2.getImage(), imageIcon.getIconWidth(), 0, null);
            graphics.dispose();
            return new ImageIcon(image);
        }
        catch (Exception exception) {
            return imageIcon;
        }
    }

    public static void setLocationRelativeTo(Component component, Window window, int n) {
        Serializable serializable;
        if (component == null && window == null) {
            return;
        }
        if (n < 0 || n > 13) {
            n = 0;
        }
        Serializable serializable2 = null;
        if (component != null) {
            if (component instanceof Window || component instanceof Applet) {
                serializable2 = (Container)component;
            } else {
                for (serializable = component.getParent(); serializable != null; serializable = ((Component)serializable).getParent()) {
                    if (!(serializable instanceof Window) && !(serializable instanceof Applet)) continue;
                    serializable2 = serializable;
                    break;
                }
            }
        }
        if (component != null && !component.isShowing() || serializable2 == null || !serializable2.isShowing()) {
            serializable = window.getSize();
            Dimension dimension = window.getToolkit().getScreenSize();
            window.setLocation((dimension.width - ((Dimension)serializable).width) / 2, (dimension.height - ((Dimension)serializable).height) / 2);
        } else {
            Serializable serializable3;
            Point point;
            serializable = component.getSize();
            if (serializable2 instanceof Applet) {
                point = component.getLocationOnScreen();
            } else {
                point = new Point(0, 0);
                for (serializable3 = component; serializable3 != null; serializable3 = ((Component)serializable3).getParent()) {
                    Point point2 = ((Component)serializable3).getLocation();
                    point.x += point2.x;
                    point.y += point2.y;
                    if (serializable3 == serializable2) break;
                }
            }
            serializable3 = window.getBounds();
            int n2 = point.x + (((Dimension)serializable).width - ((Rectangle)serializable3).width >> 1);
            int n3 = point.y + (((Dimension)serializable).height - ((Rectangle)serializable3).height >> 1);
            switch (n) {
                case 1: {
                    n2 -= ((Dimension)serializable).width / 2;
                    break;
                }
                case 2: {
                    n2 += ((Dimension)serializable).width / 2;
                    break;
                }
                case 3: {
                    n3 -= ((Dimension)serializable).height / 2;
                    break;
                }
                case 4: {
                    n3 += ((Dimension)serializable).height / 2;
                    break;
                }
                case 5: {
                    n3 = point.y + 40;
                    break;
                }
                case 6: {
                    n3 = point.y - ((Rectangle)serializable3).height;
                    break;
                }
                case 7: {
                    n3 = point.y + ((Dimension)serializable).height;
                    break;
                }
                case 9: {
                    n2 = point.x - ((Rectangle)serializable3).width;
                    break;
                }
                case 8: {
                    n2 = point.x + ((Dimension)serializable).width;
                    break;
                }
                case 10: {
                    n2 -= ((Dimension)serializable).width / 2;
                    n3 += ((Dimension)serializable).height / 2;
                    break;
                }
                case 12: {
                    n2 += ((Dimension)serializable).width / 2;
                    n3 += ((Dimension)serializable).height / 2;
                    break;
                }
                case 13: {
                    n2 += ((Dimension)serializable).width / 2;
                    n3 -= ((Dimension)serializable).height / 2;
                    break;
                }
                case 11: {
                    n2 -= ((Dimension)serializable).width / 2;
                    n3 -= ((Dimension)serializable).height / 2;
                }
            }
            Dimension dimension = window.getToolkit().getScreenSize();
            if (n3 + ((Rectangle)serializable3).height > dimension.height) {
                n3 = dimension.height - ((Rectangle)serializable3).height;
                int n4 = n2 = point.x < dimension.width >> 1 ? point.x + ((Dimension)serializable).width : point.x - ((Rectangle)serializable3).width;
            }
            if (n2 + ((Rectangle)serializable3).width > dimension.width) {
                n2 = dimension.width - ((Rectangle)serializable3).width;
            }
            if (n2 < 0) {
                n2 = 0;
            }
            if (n3 < 0) {
                n3 = 0;
            }
            window.setLocation(n2, n3);
        }
    }

    public static Window getParentWindow(Component component) {
        Component component2 = component;
        if (component2 == null) {
            component2 = new JOptionPane().createDialog(component2, "").getParent();
        }
        while (component2 != null && !(component2 instanceof Window)) {
            component2 = component2.getParent();
        }
        return (Window)component2;
    }

    public static void moveToUpRightCorner(Window window) {
        Screen screen = GUIUtils.getCurrentScreen(window);
        Dimension dimension = window.getSize();
        window.setLocation(screen.getX() + (screen.getWidth() - dimension.width), screen.getY());
    }

    public static void setNativeGUILibrary(NativeGUILibrary nativeGUILibrary) {
        if (nativeGUILibrary != null) {
            ms_nativeGUILibrary = nativeGUILibrary;
        }
    }

    public static boolean isAlwaysOnTop(Component component) {
        return GUIUtils.isAlwaysOnTop(GUIUtils.getParentWindow(component));
    }

    public static boolean isAlwaysOnTop(Window window) {
        if (window == null) {
            return false;
        }
        try {
            Method method = (class$java$awt$Window == null ? (class$java$awt$Window = GUIUtils.class$("java.awt.Window")) : class$java$awt$Window).getMethod("isAlwaysOnTop", new Class[0]);
            return (Boolean)method.invoke(window, new Object[0]);
        }
        catch (Throwable throwable) {
            return ms_nativeGUILibrary.isAlwaysOnTop(window);
        }
    }

    public static void setFontStyle(Component component, int n) {
        if (component == null) {
            return;
        }
        component.setFont(new Font(component.getFont().getName(), n, component.getFont().getSize()));
    }

    public static boolean setAlwaysOnTop(Component component, boolean bl) {
        return GUIUtils.setAlwaysOnTop(GUIUtils.getParentWindow(component), bl);
    }

    public static boolean hasJavaOnTop() {
        try {
            Class[] arrclass = new Class[]{Boolean.TYPE};
            (class$java$awt$Window == null ? (class$java$awt$Window = GUIUtils.class$("java.awt.Window")) : class$java$awt$Window).getMethod("setAlwaysOnTop", arrclass);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            return false;
        }
        return true;
    }

    public static boolean setAlwaysOnTop(Window window, boolean bl) {
        if (window == null) {
            return false;
        }
        try {
            Class[] arrclass = new Class[]{Boolean.TYPE};
            Method method = (class$java$awt$Window == null ? (class$java$awt$Window = GUIUtils.class$("java.awt.Window")) : class$java$awt$Window).getMethod("setAlwaysOnTop", arrclass);
            Object[] arrobject = new Object[]{new Boolean(bl)};
            method.invoke(window, arrobject);
            return true;
        }
        catch (Throwable throwable) {
            return ms_nativeGUILibrary.setAlwaysOnTop(window, bl);
        }
    }

    public static boolean restoreSize(Window window, Dimension dimension) {
        if (window == null || dimension == null) {
            return false;
        }
        window.setSize(dimension);
        Screen screen = GUIUtils.getCurrentScreen(window);
        int n = window.getSize().width;
        int n2 = window.getSize().height;
        if (window.getLocation().x + n > screen.getX() + screen.getWidth()) {
            n = screen.getX() + screen.getWidth() - window.getLocation().x;
        }
        if (window.getLocation().y + n2 > screen.getY() + screen.getHeight()) {
            n2 = screen.getY() + screen.getHeight() - window.getLocation().y;
        }
        if (n == 0) {
            n = window.getSize().width;
        }
        if (n2 == 0) {
            n2 = window.getSize().height;
        }
        window.setSize(n, n2);
        return true;
    }

    public static Point getMiddlePoint(Window window) {
        if (window == null) {
            return new Point(0, 0);
        }
        return new Point(window.getLocation().x + window.getSize().width / 2, window.getLocation().y + window.getSize().height / 2);
    }

    public static boolean restoreLocation(Window window, Point point) {
        if (window == null || point == null) {
            return false;
        }
        double d = -1.0;
        double d2 = Double.MAX_VALUE;
        Screen screen = null;
        window.setLocation(point);
        Point point2 = GUIUtils.getMiddlePoint(window);
        Screen[] arrscreen = GUIUtils.getScreens(window);
        int n = point.x;
        int n2 = window.getSize().width;
        int n3 = point.y;
        int n4 = window.getSize().height;
        if (arrscreen.length == 0) {
            return false;
        }
        for (int i = 0; i < arrscreen.length; ++i) {
            boolean bl;
            if (point2.x >= arrscreen[i].getX() && point2.y >= arrscreen[i].getY() && point2.x <= arrscreen[i].getX() + arrscreen[i].getWidth() && point2.y <= arrscreen[i].getY() + arrscreen[i].getHeight()) {
                screen = arrscreen[i];
                break;
            }
            boolean bl2 = n >= arrscreen[i].getX() && n <= arrscreen[i].getX() + arrscreen[i].getWidth() && n3 >= arrscreen[i].getY() && n3 <= arrscreen[i].getY() + arrscreen[i].getHeight();
            boolean bl3 = n + n2 >= arrscreen[i].getX() && n + n2 <= arrscreen[i].getX() + arrscreen[i].getWidth() && n3 >= arrscreen[i].getY() && n3 <= arrscreen[i].getY() + arrscreen[i].getHeight();
            boolean bl4 = n3 + n4 >= arrscreen[i].getY() && n3 + n4 <= arrscreen[i].getY() + arrscreen[i].getHeight() && n >= arrscreen[i].getX() && n <= arrscreen[i].getX() + arrscreen[i].getWidth();
            boolean bl5 = bl = n3 + n4 >= arrscreen[i].getY() && n + n2 >= arrscreen[i].getX() && n3 + n4 <= arrscreen[i].getY() + arrscreen[i].getHeight() && n + n2 <= arrscreen[i].getX() + arrscreen[i].getWidth();
            if (!bl2 && !bl3 && !bl4 && !bl) continue;
            int n5 = bl2 || bl4 ? n : arrscreen[i].getX();
            int n6 = bl3 || bl ? n + n2 : arrscreen[i].getX() + arrscreen[i].getWidth();
            int n7 = bl2 || bl3 ? n3 : arrscreen[i].getY();
            int n8 = bl4 || bl ? n3 + n4 : arrscreen[i].getY() + arrscreen[i].getHeight();
            int n9 = (n6 - n5) * (n8 - n7);
            LogHolder.log(6, LogType.GUI, "Calculated partial overlapping area for restoring window location: " + n9);
            if (!((double)n9 >= d)) continue;
            d = n9;
            screen = arrscreen[i];
        }
        if (screen == null) {
            for (int i = 0; i < arrscreen.length; ++i) {
                Point point3 = new Point(arrscreen[i].getX() + arrscreen[i].getWidth() / 2, arrscreen[i].getY() + arrscreen[i].getHeight() / 2);
                double d3 = Math.sqrt(Math.pow(point2.x - point3.x, 2.0) + Math.pow(point2.y - point3.y, 2.0));
                LogHolder.log(6, LogType.GUI, "Calculated distance vector for restoring window location: " + d3);
                if (!(d3 < d2)) continue;
                screen = arrscreen[i];
                d2 = d3;
            }
        }
        LogHolder.log(5, LogType.GUI, "The following screen was chosen for restoring a window location:\n" + screen);
        if (n + window.getSize().width > screen.getX() + screen.getWidth()) {
            n = screen.getX() + screen.getWidth() - window.getSize().width;
        }
        if (n3 + window.getSize().height > screen.getY() + screen.getHeight()) {
            n3 = screen.getY() + screen.getHeight() - window.getSize().height;
        }
        if (n < screen.getX()) {
            n = screen.getX();
        }
        if (n3 < screen.getY()) {
            n3 = screen.getY();
        }
        window.setLocation(n, n3);
        return true;
    }

    public static MouseListener addTimedTooltipListener(JComponent jComponent) {
        try {
            Class<?> class_ = Class.forName("javax.swing.InputMap");
            Object object = (class$javax$swing$JComponent == null ? (class$javax$swing$JComponent = GUIUtils.class$("javax.swing.JComponent")) : class$javax$swing$JComponent).getMethod("getInputMap", new Class[0]).invoke(jComponent, new Object[0]);
            (class$javax$swing$JComponent == null ? (class$javax$swing$JComponent = GUIUtils.class$("javax.swing.JComponent")) : class$javax$swing$JComponent).getMethod("getActionMap", new Class[0]).invoke(jComponent, new Object[0]);
            boolean bl = false;
            KeyStroke[] arrkeyStroke = (KeyStroke[])class_.getMethod("keys", new Class[0]).invoke(object, new Object[0]);
            if (arrkeyStroke == null || arrkeyStroke.length == 0) {
                class_.getMethod("put", class$javax$swing$KeyStroke == null ? (class$javax$swing$KeyStroke = GUIUtils.class$("javax.swing.KeyStroke")) : class$javax$swing$KeyStroke, class$java$lang$Object == null ? (class$java$lang$Object = GUIUtils.class$("java.lang.Object")) : class$java$lang$Object).invoke(object, KeyStroke.getKeyStroke(92, 0), "backSlash");
                bl = true;
            }
            ToolTipManager.sharedInstance().registerComponent(jComponent);
            if (bl) {
                class_.getMethod("remove", class$javax$swing$KeyStroke == null ? (class$javax$swing$KeyStroke = GUIUtils.class$("javax.swing.KeyStroke")) : class$javax$swing$KeyStroke).invoke(object, KeyStroke.getKeyStroke(92, 0));
            }
            ToolTipMouseListener toolTipMouseListener = new ToolTipMouseListener();
            jComponent.addMouseListener(toolTipMouseListener);
            return toolTipMouseListener;
        }
        catch (Exception exception) {
            LogHolder.log(5, LogType.GUI, "Could not register component for timed tooltip!", exception);
            return null;
        }
    }

    public static Screen[] getScreens(Window window) {
        try {
            Object object = Class.forName("java.awt.GraphicsEnvironment").getMethod("getLocalGraphicsEnvironment", null).invoke(null, null);
            Object[] arrobject = (Object[])object.getClass().getMethod("getScreenDevices", null).invoke(object, null);
            Screen[] arrscreen = new Screen[arrobject.length];
            for (int i = 0; i < arrobject.length; ++i) {
                Object object2 = arrobject[i].getClass().getMethod("getDefaultConfiguration", null).invoke(arrobject[i], null);
                Frame frame = (Frame)(class$java$awt$Frame == null ? GUIUtils.class$("java.awt.Frame") : class$java$awt$Frame).getConstructor(Class.forName("java.awt.GraphicsConfiguration")).newInstance(object2);
                arrscreen[i] = new Screen(frame.getLocation(), (Rectangle)object2.getClass().getMethod("getBounds", null).invoke(object2, null));
            }
            return arrscreen;
        }
        catch (Exception exception) {
            return new Screen[]{new Screen(new Point(0, 0), GUIUtils.getDefaultScreenBounds(window))};
        }
    }

    public static Screen getCurrentScreen(Component component) {
        return GUIUtils.getCurrentScreen(GUIUtils.getParentWindow(component));
    }

    public static Screen getCurrentScreen(Window window) {
        if (window == null) {
            return null;
        }
        try {
            Object object = Class.forName("java.awt.GraphicsEnvironment").getMethod("getLocalGraphicsEnvironment", null).invoke(null, null);
            Object[] arrobject = (Object[])object.getClass().getMethod("getScreenDevices", null).invoke(object, null);
            Point point = GUIUtils.getMiddlePoint(window);
            for (int i = 0; i < arrobject.length; ++i) {
                Object object2 = arrobject[i].getClass().getMethod("getDefaultConfiguration", null).invoke(arrobject[i], null);
                Frame frame = (Frame)(class$java$awt$Frame == null ? GUIUtils.class$("java.awt.Frame") : class$java$awt$Frame).getConstructor(Class.forName("java.awt.GraphicsConfiguration")).newInstance(object2);
                Point point2 = frame.getLocation();
                Rectangle rectangle = (Rectangle)object2.getClass().getMethod("getBounds", null).invoke(object2, null);
                if (point.x < point2.x || point.x > point2.x + rectangle.width || point.y < point2.y || point.y > point2.y + rectangle.height) continue;
                return GUIUtils.getOverlappingScreen(new Screen(point2, rectangle), window);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return new Screen(new Point(0, 0), GUIUtils.getDefaultScreenBounds(window));
    }

    public static void centerOnScreen(Window window) {
        Rectangle rectangle = GUIUtils.getDefaultScreenBounds(window);
        Dimension dimension = window.getSize();
        window.setLocation(rectangle.x + (rectangle.width - dimension.width) / 2, rectangle.y + (rectangle.height - dimension.height) / 2);
    }

    public static void centerOnWindow(Window window, Window window2) {
        if (window == null || window2 == null) {
            return;
        }
        Dimension dimension = window2.getSize();
        Dimension dimension2 = window.getSize();
        Point point = window2.getLocationOnScreen();
        window.setLocation(point.x + dimension.width / 2 - dimension2.width / 2, point.y + dimension.height / 2 - dimension2.height / 2);
    }

    public static JTextPane createSelectableAndResizeableLabel(Component component) {
        JTextPane jTextPane = new JTextPane();
        jTextPane.setBackground(component.getBackground());
        jTextPane.setEditable(false);
        jTextPane.setDisabledTextColor(jTextPane.getCaretColor());
        Font font = new JLabel().getFont();
        jTextPane.setFont(new Font(font.getName(), 1, font.getSize()));
        return jTextPane;
    }

    public static JLabel createMultiLineLabel(String string, int n) {
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel();
        jAPHtmlMultiLineLabel.setText(JAPMessages.getString(string));
        jAPHtmlMultiLineLabel.setPreferredWidth(n);
        return jAPHtmlMultiLineLabel;
    }

    public static JLabel createLabel(String string) {
        return GUIUtils.createLabel(new String[]{string});
    }

    public static JLabel createLabel(String string, String string2) {
        return GUIUtils.createLabel(new String[]{string, string2});
    }

    public static JButton createButton(String string) {
        return new JButton(JAPMessages.getString(string));
    }

    public static JLabel createLabel(String[] arrstring) {
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < arrstring.length; ++i) {
            stringBuffer.append(JAPMessages.getString(arrstring[i]) + (i < arrstring.length - 1 ? "/" : ""));
        }
        return new JLabel(stringBuffer.toString());
    }

    public static boolean isMouseButton(MouseEvent mouseEvent, int n) {
        return (mouseEvent.getModifiers() & n) == n;
    }

    public static Clipboard getSystemClipboard() {
        Clipboard clipboard = null;
        try {
            Method method = (class$java$awt$Toolkit == null ? (class$java$awt$Toolkit = GUIUtils.class$("java.awt.Toolkit")) : class$java$awt$Toolkit).getMethod("getSystemSelection", new Class[0]);
            clipboard = (Clipboard)method.invoke(Toolkit.getDefaultToolkit(), new Object[0]);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        return clipboard;
    }

    public static Vector registerLookAndFeelClasses(File file) throws IllegalAccessException {
        Vector<File> vector;
        int n;
        File file2;
        if (file == null) {
            return new Vector();
        }
        UIManager.LookAndFeelInfo[] arrlookAndFeelInfo = UIManager.getInstalledLookAndFeels();
        Vector<File> vector2 = new Vector<File>(arrlookAndFeelInfo.length);
        for (int i = 0; i < arrlookAndFeelInfo.length; ++i) {
            file2 = ClassUtil.getClassDirectory(arrlookAndFeelInfo[i].getClassName());
            if (file2 == null) continue;
            vector2.addElement(file2);
        }
        ClassUtil.addFileToClasspath(file);
        ClassUtil.loadClasses(file);
        Vector vector3 = ClassUtil.findSubclasses(class$javax$swing$LookAndFeel == null ? (class$javax$swing$LookAndFeel = GUIUtils.class$("javax.swing.LookAndFeel")) : class$javax$swing$LookAndFeel);
        for (n = 0; n < vector3.size(); ++n) {
            LookAndFeel lookAndFeel;
            try {
                lookAndFeel = (LookAndFeel)((Class)vector3.elementAt(n)).newInstance();
            }
            catch (IllegalAccessException illegalAccessException) {
                continue;
            }
            catch (InstantiationException instantiationException) {
                continue;
            }
            catch (ClassCastException classCastException) {
                continue;
            }
            try {
                if (!lookAndFeel.isSupportedLookAndFeel()) continue;
                UIManager.LookAndFeelInfo[] arrlookAndFeelInfo2 = UIManager.getInstalledLookAndFeels();
                boolean bl = false;
                for (int i = 0; i < arrlookAndFeelInfo2.length; ++i) {
                    if (!arrlookAndFeelInfo2[i].getClassName().equals(lookAndFeel.getClass().getName())) continue;
                    bl = true;
                }
                if (bl) continue;
                UIManager.installLookAndFeel(lookAndFeel.getName(), lookAndFeel.getClass().getName());
                continue;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        UIManager.LookAndFeelInfo[] arrlookAndFeelInfo3 = UIManager.getInstalledLookAndFeels();
        if (arrlookAndFeelInfo3.length > arrlookAndFeelInfo.length) {
            vector = new Vector(arrlookAndFeelInfo3.length - arrlookAndFeelInfo.length);
            for (n = 0; n < arrlookAndFeelInfo3.length; ++n) {
                file2 = ClassUtil.getClassDirectory(arrlookAndFeelInfo3[n].getClassName());
                if (vector2.contains(file2)) continue;
                vector.addElement(file2);
            }
        } else {
            vector = new Vector<File>();
        }
        return vector;
    }

    public static void resizeAllFonts(float f) {
        Enumeration enumeration = UIManager.getDefaults().keys();
        while (enumeration.hasMoreElements()) {
            Object k = enumeration.nextElement();
            Object object = UIManager.get(k);
            if (!(object instanceof FontUIResource)) continue;
            GUIUtils.adjustFontSize(k.toString(), f);
        }
    }

    public static String getTextFromClipboard(Component component) {
        return GUIUtils.getTextFromClipboard(component, true);
    }

    public static void saveTextToClipboard(String string, Component component) {
        Object object;
        try {
            object = GUIUtils.getSystemClipboard();
            ((Clipboard)object).setContents(new StringSelection(string), new ClipboardOwner(){

                public void lostOwnership(Clipboard clipboard, Transferable transferable) {
                }
            });
            if (string.equals(GUIUtils.getTextFromClipboard(component, false))) {
                JAPDialog.showMessageDialog(component, JAPMessages.getString(MSG_SAVED_TO_CLIP));
                return;
            }
        }
        catch (Exception exception) {
            LogHolder.log(5, LogType.GUI, exception);
        }
        object = new ClipFrame(component, JAPMessages.getString(MSG_COPY_FROM_CLIP), false);
        ((ClipFrame)object).setText(string);
        ((JAPDialog)object).setVisible(true, false);
    }

    public static ImageIcon createScaledImageIcon(ImageIcon imageIcon, IIconResizer iIconResizer) {
        if (imageIcon == null) {
            return null;
        }
        if (iIconResizer == null) {
            return imageIcon;
        }
        return new ImageIcon(imageIcon.getImage().getScaledInstance((int)((double)imageIcon.getIconWidth() * iIconResizer.getResizeFactor()), -1, 8));
    }

    public static Icon createScaledIcon(Icon icon, IIconResizer iIconResizer) {
        if (icon == null) {
            return icon;
        }
        return new IconScaler(icon, iIconResizer.getResizeFactor());
    }

    public static String trim(String string, int n) {
        if (string == null || n < 4) {
            return null;
        }
        if ((string = JAPHtmlMultiLineLabel.removeTagsAndNewLines(string)).length() > n) {
            string = string.substring(0, n - 2) + "...";
        }
        return string;
    }

    public static String trim(String string) {
        return GUIUtils.trim(string, 60);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addAWTEventListener(AWTEventListener aWTEventListener) {
        Vector vector = AWT_EVENT_LISTENERS;
        synchronized (vector) {
            if (!ms_bCapturingAWTEvents) {
                Runnable runnable = new Runnable(){

                    public void run() {
                        SwingUtilities.invokeLater(new Runnable(){

                            /*
                             * WARNING - Removed try catching itself - possible behaviour change.
                             */
                            public void run() {
                                EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
                                while (!JAPDialog.isConsoleOnly()) {
                                    try {
                                        Class<?> class_;
                                        AWTEvent aWTEvent = eventQueue.getNextEvent();
                                        try {
                                            class_ = Class.forName("java.awt.ActiveEvent");
                                        }
                                        catch (ClassNotFoundException classNotFoundException) {
                                            class_ = null;
                                        }
                                        if (class_ != null && class_.isInstance(aWTEvent)) {
                                            class_.getMethod("dispatch", null).invoke(aWTEvent, null);
                                        } else if (aWTEvent.getSource() instanceof Component) {
                                            try {
                                                ((Component)aWTEvent.getSource()).dispatchEvent(aWTEvent);
                                            }
                                            catch (IllegalMonitorStateException illegalMonitorStateException) {
                                                LogHolder.log(5, LogType.GUI, illegalMonitorStateException);
                                            }
                                        } else if (aWTEvent.getSource() instanceof MenuComponent) {
                                            ((MenuComponent)aWTEvent.getSource()).dispatchEvent(aWTEvent);
                                        }
                                        Vector vector = AWT_EVENT_LISTENERS;
                                        synchronized (vector) {
                                            for (int i = 0; i < AWT_EVENT_LISTENERS.size(); ++i) {
                                                ((AWTEventListener)AWT_EVENT_LISTENERS.elementAt(i)).eventDispatched(aWTEvent);
                                            }
                                        }
                                        Thread.yield();
                                    }
                                    catch (Exception exception) {
                                        LogHolder.log(2, LogType.GUI, exception);
                                    }
                                }
                            }
                        });
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    new Thread(runnable).start();
                } else {
                    runnable.run();
                }
                ms_bCapturingAWTEvents = true;
            }
            if (aWTEventListener == null) {
                return;
            }
            if (!AWT_EVENT_LISTENERS.contains(aWTEventListener)) {
                AWT_EVENT_LISTENERS.addElement(aWTEventListener);
            }
        }
    }

    public static void removeAWTEventListener(AWTEventListener aWTEventListener) {
        AWT_EVENT_LISTENERS.removeElement(aWTEventListener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Point getMousePosition() {
        Object object = SYNC_MOUSE_POSITION;
        synchronized (object) {
            if (ms_mouseListener == null) {
                ms_mouseListener = new AWTEventListener(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void eventDispatched(AWTEvent aWTEvent) {
                        if (aWTEvent instanceof MouseEvent) {
                            MouseEvent mouseEvent = (MouseEvent)aWTEvent;
                            if (aWTEvent.getSource() != null && aWTEvent.getSource() instanceof Component) {
                                Component component = (Component)aWTEvent.getSource();
                                try {
                                    Object object = SYNC_MOUSE_POSITION;
                                    synchronized (object) {
                                        ms_mousePosition = component.getLocationOnScreen();
                                        ms_mousePosition.x += mouseEvent.getX();
                                        ms_mousePosition.y += mouseEvent.getY();
                                    }
                                }
                                catch (IllegalComponentStateException illegalComponentStateException) {
                                    // empty catch block
                                }
                            }
                        }
                    }
                };
                GUIUtils.addAWTEventListener(ms_mouseListener);
            }
        }
        if (ms_mousePosition == null) {
            return null;
        }
        return new Point(GUIUtils.ms_mousePosition.x, GUIUtils.ms_mousePosition.y);
    }

    public static Point getRelativePosition(Point point, Component component) {
        Point point2;
        if (point == null || component == null) {
            return null;
        }
        Point point3 = point;
        if (point3 == null) {
            return null;
        }
        try {
            point2 = component.getLocationOnScreen();
        }
        catch (IllegalComponentStateException illegalComponentStateException) {
            point2 = component.getLocation();
        }
        if (point3.x < point2.x - 1 || point3.x > point2.x + component.getSize().width + 1 || point3.y < point2.y - 1 || point3.y > point2.y + component.getSize().height + 1) {
            return null;
        }
        point3.x -= point2.x;
        point3.y -= point2.y;
        return point3;
    }

    public static Point getMousePosition(Component component) {
        return GUIUtils.getRelativePosition(GUIUtils.getMousePosition(), component);
    }

    private static Screen getOverlappingScreen(Screen screen, Window window) {
        if (screen == null) {
            return null;
        }
        Screen screen2 = new Screen(new Point(0, 0), GUIUtils.getDefaultScreenBounds(window));
        if (screen2.getX() == screen.getX() && screen2.getY() == screen.getY() && screen2.getWidth() == screen.getWidth() && screen2.getHeight() == screen.getHeight()) {
            return screen;
        }
        int n = screen.getX();
        int n2 = screen.getY();
        int n3 = screen.getWidth();
        int n4 = screen.getHeight();
        boolean bl = false;
        if (screen.getY() < screen2.getY() && screen.getY() + screen.getHeight() > screen2.getY() || screen2.getY() < screen.getY() && screen2.getY() + screen2.getHeight() > screen.getY()) {
            bl = true;
            LogHolder.log(5, LogType.GUI, "Found overlapping screen.");
            n2 = Math.max(screen.getY(), screen2.getY());
            n4 = Math.min(screen.getY() + screen.getHeight(), screen2.getY() + screen2.getHeight() - Math.abs(screen.getY() - screen2.getY()));
        }
        if (screen.getX() < screen2.getX() && screen.getX() + screen.getWidth() > screen2.getX() || screen2.getX() < screen.getX() && screen2.getX() + screen2.getWidth() > screen.getX()) {
            bl = true;
            n = Math.max(screen.getX(), screen2.getX());
            n3 = Math.min(screen.getX() + screen.getWidth(), screen2.getX() + screen2.getWidth() - Math.abs(screen.getX() - screen2.getX()));
        }
        if (bl) {
            screen = new Screen(new Point(n, n2), new Rectangle(n3, n4));
        }
        return screen;
    }

    private static String getTextFromClipboard(Component component, boolean bl) {
        Clipboard clipboard = GUIUtils.getSystemClipboard();
        String string = null;
        Transferable transferable = clipboard.getContents(component);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                string = (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
            catch (Exception exception) {
                LogHolder.log(5, LogType.GUI, exception);
            }
        }
        if (bl && string == null) {
            ClipFrame clipFrame = new ClipFrame(component, JAPMessages.getString(MSG_PASTE_FILE), true);
            clipFrame.setVisible(true, false);
            string = clipFrame.getText();
        }
        return string;
    }

    private static Rectangle getDefaultScreenBounds(Window window) {
        Rectangle rectangle;
        if (window == null) {
            return null;
        }
        try {
            Object object = Class.forName("java.awt.GraphicsEnvironment").getMethod("getLocalGraphicsEnvironment", null).invoke(null, null);
            Object object2 = object.getClass().getMethod("getDefaultScreenDevice", null).invoke(object, null);
            Object object3 = object2.getClass().getMethod("getDefaultConfiguration", null).invoke(object2, null);
            rectangle = (Rectangle)object3.getClass().getMethod("getBounds", null).invoke(object3, null);
        }
        catch (Exception exception) {
            rectangle = new Rectangle(new Point(0, 0), window.getToolkit().getScreenSize());
        }
        return rectangle;
    }

    private static void adjustFontSize(Object object, float f) {
        try {
            UIDefaults uIDefaults = UIManager.getDefaults();
            Font font = uIDefaults.getFont(object);
            uIDefaults.put(object, new FontUIResource(font.getName(), font.getStyle(), Math.round((float)font.getSize() * f)));
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.GUI, exception);
        }
    }

    public static Dimension getMaxSize(Vector vector) {
        Dimension dimension = new Dimension(0, 0);
        int n = 0;
        int n2 = 0;
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            JComponent jComponent = (JComponent)enumeration.nextElement();
            n = Math.max(dimension.width, jComponent.getPreferredSize().width);
            n2 = Math.max(dimension.height, jComponent.getPreferredSize().height);
            dimension.setSize(n, n2);
        }
        dimension.setSize(n, n2);
        return dimension;
    }

    public static Dimension getTotalSize(Vector vector) {
        int n = 0;
        int n2 = 0;
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            JComponent jComponent = (JComponent)enumeration.nextElement();
            n += jComponent.getPreferredSize().width;
            n2 += jComponent.getPreferredSize().height;
        }
        return new Dimension(n, n2);
    }

    public static int showMonitoredFileChooser(JFileChooser jFileChooser, Component component, String string) {
        int n;
        if (jFileChooser == null) {
            throw new NullPointerException("No file chooser given!");
        }
        LogHolder.log(4, LogType.GUI, "Showing monitored file chooser...");
        final class Class_showMonitoredFileChooser_Runnable
        implements Runnable {
            public volatile boolean m_bFinished;
            private final /* synthetic */ JFileChooser val$a_chooser;

            Class_showMonitoredFileChooser_Runnable(boolean bl, JFileChooser jFileChooser) {
                this.val$a_chooser = jFileChooser;
                this.m_bFinished = bl;
            }

            public void run() {
                try {
                    Thread.sleep(2000L);
                    LogHolder.log(4, LogType.GUI, "Waiting in timeout thread of monitored file chooser...");
                    if (!(this.val$a_chooser.isVisible() && this.val$a_chooser.isShowing() || this.m_bFinished)) {
                        LogHolder.log(1, LogType.GUI, "File chooser dialog blocked and is now interrupted!");
                        GUIUtils.interruptAWTEventThread();
                    }
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
        Class_showMonitoredFileChooser_Runnable class_showMonitoredFileChooser_Runnable = new Class_showMonitoredFileChooser_Runnable(false, jFileChooser);
        Thread thread = new Thread(class_showMonitoredFileChooser_Runnable);
        thread.start();
        try {
            n = string == FILE_CHOOSER_OPEN ? jFileChooser.showOpenDialog(component) : (string == FILE_CHOOSER_SAVE ? jFileChooser.showSaveDialog(component) : jFileChooser.showDialog(component, string));
        }
        catch (Exception exception) {
            LogHolder.log(1, LogType.GUI, exception);
            n = -1;
        }
        LogHolder.log(4, LogType.GUI, "Finished monitored file chooser. Stopping thread.");
        class_showMonitoredFileChooser_Runnable.m_bFinished = true;
        thread.interrupt();
        LogHolder.log(4, LogType.GUI, "Stopped monitored file chooser thread.");
        return n;
    }

    public static void setSizes(Vector vector, Dimension dimension) {
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            JComponent jComponent = (JComponent)enumeration.nextElement();
            jComponent.setPreferredSize(new Dimension(dimension.width, dimension.height));
            jComponent.setMaximumSize(new Dimension(dimension.width, dimension.height));
        }
    }

    public static void setEqualWidths(Vector vector, Dimension dimension) {
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            JComponent jComponent = (JComponent)enumeration.nextElement();
            double d = jComponent.getPreferredSize().height;
            jComponent.setPreferredSize(new Dimension(dimension.width, (int)d));
            jComponent.setMaximumSize(new Dimension(dimension.width, (int)d));
        }
    }

    public static JAPDialog.ILinkedInformation createURLLink(URL uRL, String string) {
        return GUIUtils.createURLLink(uRL, string, null);
    }

    public static JAPDialog.ILinkedInformation createURLLink(final URL uRL, final String string, String string2) {
        if (uRL == null) {
            return null;
        }
        JAPDialog.LinkedHelpContext linkedHelpContext = new JAPDialog.LinkedHelpContext(string2){

            public int getType() {
                return 1;
            }

            public void clicked(boolean bl) {
                AbstractOS.getInstance().openURL(uRL);
            }

            public String getMessage() {
                if (string == null || string.trim().length() == 0) {
                    return uRL.toString();
                }
                return string;
            }
        };
        return linkedHelpContext;
    }

    public static void exitWithNoMessagesError(String string) {
        JAPAWTMsgBox.MsgBox("File not found: " + string + "_en" + ".properties\nYour package of JAP may be corrupted.\n" + "Try again to download or install the package.", "Error");
        System.exit(1);
    }

    public static String getCountryFromServiceLocation(ServiceLocation serviceLocation) {
        if (serviceLocation == null) {
            return "";
        }
        String string = "";
        if (serviceLocation.getCity() != null && serviceLocation.getCity().trim().length() > 0) {
            string = serviceLocation.getCity().trim();
        }
        if (serviceLocation.getState() != null && serviceLocation.getState().trim().length() > 0 && !string.equals(serviceLocation.getState().trim())) {
            if (string.length() > 0) {
                string = string + ", ";
            }
            string = string + serviceLocation.getState().trim();
        }
        if (serviceLocation.getCountryCode() != null && serviceLocation.getCountryCode().trim().length() > 0) {
            if (string.length() > 0) {
                string = string + ", ";
            }
            try {
                string = string + new CountryMapper(serviceLocation.getCountryCode(), JAPMessages.getLocale()).toString();
            }
            catch (IllegalArgumentException illegalArgumentException) {
                string = string + serviceLocation.getCountryCode().trim();
            }
        }
        if (string.trim().length() == 0) {
            return "N/A";
        }
        return string;
    }

    private static void interruptAWTEventThread() {
        Thread[] arrthread = new Thread[Thread.activeCount()];
        Thread.enumerate(arrthread);
        for (int i = 0; i < arrthread.length; ++i) {
            if (!arrthread[i].getName().startsWith("AWT-EventQueue-")) continue;
            try {
                LogHolder.log(0, LogType.GUI, "Interrupting AWT event dispatch thread!");
                arrthread[i].interrupt();
                continue;
            }
            catch (Throwable throwable) {
                LogHolder.log(0, LogType.GUI, throwable);
            }
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

    static {
        SYNC_MOUSE_POSITION = new Object();
        AWT_EVENT_LISTENERS = new Vector();
        ms_resizer = DEFAULT_RESIZER = new IIconResizer(){

            public double getResizeFactor() {
                return 1.0;
            }
        };
        ms_nativeGUILibrary = DUMMY_GUI_LIBRARY = new NativeGUILibrary(){

            public boolean setAlwaysOnTop(Window window, boolean bl) {
                return false;
            }

            public boolean isAlwaysOnTop(Window window) {
                return false;
            }
        };
        RESIZER = new IIconResizer(){

            public double getResizeFactor() {
                return ms_resizer.getResizeFactor();
            }
        };
        ms_iconCache = new Hashtable();
    }

    private static class IconScaler
    implements Icon {
        private static Class GRAPHICS_2D;
        private Icon m_icon;
        private double m_scaleWidth;
        private double m_scaleHeight;

        public IconScaler(Icon icon, double d) {
            this(icon, d, d);
        }

        public IconScaler(Icon icon, double d, double d2) {
            this.m_icon = icon;
            if (GRAPHICS_2D != null) {
                this.m_scaleWidth = d;
                this.m_scaleHeight = d2;
            } else {
                this.m_scaleWidth = 1.0;
                this.m_scaleHeight = 1.0;
            }
        }

        public int getIconHeight() {
            return (int)((double)this.m_icon.getIconHeight() * this.m_scaleHeight);
        }

        public int getIconWidth() {
            return (int)((double)this.m_icon.getIconWidth() * this.m_scaleWidth);
        }

        public void paintIcon(Component component, Graphics graphics, int n, int n2) {
            IconScaler.scale(graphics, this.m_scaleWidth, this.m_scaleHeight);
            this.m_icon.paintIcon(component, graphics, n, n2);
            IconScaler.scale(graphics, 1.0 / this.m_scaleWidth, 1.0 / this.m_scaleHeight);
        }

        private static void scale(Graphics graphics, double d, double d2) {
            if (GRAPHICS_2D != null) {
                try {
                    GRAPHICS_2D.getMethod("scale", Double.TYPE, Double.TYPE).invoke(graphics, new Double(d), new Double(d2));
                }
                catch (Exception exception) {
                    LogHolder.log(3, LogType.GUI, exception);
                }
            }
        }

        static {
            try {
                GRAPHICS_2D = Class.forName("java.awt.Graphics2D");
            }
            catch (ClassNotFoundException classNotFoundException) {
                GRAPHICS_2D = null;
            }
        }
    }

    private static class ToolTipMouseListener
    extends MouseAdapter {
        private ToolTipMouseListener() {
        }

        public void mouseEntered(MouseEvent mouseEvent) {
            if (!(mouseEvent.getComponent() instanceof JComponent)) {
                return;
            }
            JComponent jComponent = (JComponent)mouseEvent.getComponent();
            ActionListener actionListener = null;
            try {
                Class<?> class_ = Class.forName("javax.swing.ActionMap");
                Object object = (class$javax$swing$JComponent == null ? (class$javax$swing$JComponent = GUIUtils.class$("javax.swing.JComponent")) : class$javax$swing$JComponent).getMethod("getActionMap", new Class[0]).invoke(jComponent, new Object[0]);
                actionListener = (Action)class_.getMethod("get", class$java$lang$Object == null ? (class$java$lang$Object = GUIUtils.class$("java.lang.Object")) : class$java$lang$Object).invoke(object, "postTip");
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.GUI, exception);
            }
            if (actionListener != null) {
                actionListener.actionPerformed(new ActionEvent(jComponent, 1001, "postTip"));
            }
        }
    }

    public static class Screen {
        private Point m_location;
        private Rectangle m_bounds;

        public Screen(Point point, Rectangle rectangle) {
            this.m_location = point;
            this.m_bounds = rectangle;
        }

        public int getX() {
            return this.m_location.x;
        }

        public int getY() {
            return this.m_location.y;
        }

        public int getWidth() {
            return this.m_bounds.width;
        }

        public int getHeight() {
            return this.m_bounds.height;
        }

        public Point getLocation() {
            return this.m_location;
        }

        public Rectangle getBounds() {
            return this.m_bounds;
        }

        public String toString() {
            return "x=" + this.getX() + " " + "y=" + this.getY() + " " + "width=" + this.getWidth() + " " + "height=" + this.getHeight();
        }
    }

    public static interface AWTEventListener {
        public void eventDispatched(AWTEvent var1);
    }

    public static class WindowDocker {
        private JobQueue m_queue;
        private Component m_component;
        private InternalListener m_listener;
        private Window m_parentWindow;

        public WindowDocker(Component component) {
            this.m_component = component;
            this.m_listener = new InternalListener();
            this.m_component.addMouseListener(this.m_listener);
            this.m_component.addMouseMotionListener(this.m_listener);
            this.m_parentWindow = GUIUtils.getParentWindow(component);
            this.m_queue = new JobQueue("Docking queue for window: " + component.getName());
        }

        public void finalize() {
            this.m_queue.stop();
            this.m_queue = null;
            this.m_component.removeMouseListener(this.m_listener);
            this.m_component.removeMouseMotionListener(this.m_listener);
            this.m_component.removeComponentListener(this.m_listener);
            this.m_listener = null;
        }

        private class InternalListener
        extends MouseAdapter
        implements MouseMotionListener,
        ComponentListener,
        IDockInterface {
            private boolean m_bIsDragging = false;
            private Point m_startPoint;
            private final Object SYNC = new Object();

            private InternalListener() {
            }

            public void componentHidden(ComponentEvent componentEvent) {
            }

            public void componentResized(ComponentEvent componentEvent) {
            }

            public void componentShown(ComponentEvent componentEvent) {
            }

            public void componentMoved(ComponentEvent componentEvent) {
                if (!this.m_bIsDragging) {
                    this.move(null);
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseReleased(MouseEvent mouseEvent) {
                Object object = this.SYNC;
                synchronized (object) {
                    this.m_bIsDragging = false;
                }
            }

            public void mouseMoved(MouseEvent mouseEvent) {
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void mouseDragged(MouseEvent mouseEvent) {
                Object object = this.SYNC;
                synchronized (object) {
                    if (!this.m_bIsDragging) {
                        this.m_bIsDragging = true;
                        this.m_startPoint = mouseEvent.getPoint();
                    } else {
                        Point point = mouseEvent.getPoint();
                        Point point2 = WindowDocker.this.m_parentWindow.getLocationOnScreen();
                        int n = point2.x + point.x - this.m_startPoint.x;
                        int n2 = point2.y + point.y - this.m_startPoint.y;
                        this.move(new Point(n, n2));
                    }
                }
            }

            private void move(final Point point) {
                WindowDocker.this.m_queue.addJob(new JobQueue.Job(){

                    public void runJob() {
                        Screen screen = GUIUtils.getCurrentScreen(WindowDocker.this.m_parentWindow);
                        boolean bl = point != null;
                        Point point2 = point;
                        if (point2 == null) {
                            point2 = WindowDocker.this.m_parentWindow.getLocationOnScreen();
                        }
                        int n = point2.x;
                        int n2 = point2.y;
                        int n3 = screen.getWidth() + screen.getX();
                        int n4 = screen.getHeight() + screen.getY();
                        if (n != screen.getX() && Math.abs(n - screen.getX()) < 10) {
                            bl = true;
                            n = screen.getX();
                        } else if (n + ((WindowDocker)((InternalListener)InternalListener.this).WindowDocker.this).m_parentWindow.getSize().width > n3 - 10 && n + ((WindowDocker)((InternalListener)InternalListener.this).WindowDocker.this).m_parentWindow.getSize().width <= n3 + 10) {
                            bl = true;
                            n = n3 - ((WindowDocker)((InternalListener)InternalListener.this).WindowDocker.this).m_parentWindow.getSize().width;
                        }
                        if (n2 != screen.getY() && Math.abs(n2 - screen.getY()) < 10) {
                            bl = true;
                            n2 = screen.getY();
                        } else if (n2 + ((WindowDocker)((InternalListener)InternalListener.this).WindowDocker.this).m_parentWindow.getSize().height > n4 - 10 && n2 + ((WindowDocker)((InternalListener)InternalListener.this).WindowDocker.this).m_parentWindow.getSize().height <= n4 + 10) {
                            bl = true;
                            n2 = n4 - ((WindowDocker)((InternalListener)InternalListener.this).WindowDocker.this).m_parentWindow.getSize().height;
                        }
                        if (bl) {
                            WindowDocker.this.m_parentWindow.setLocation(n, n2);
                        }
                    }
                });
            }
        }

        private static interface IDockInterface {
            public static final int DOCK_DISTANCE = 10;
        }
    }

    public static interface IIconResizer {
        public double getResizeFactor();
    }

    public static interface NativeGUILibrary {
        public boolean setAlwaysOnTop(Window var1, boolean var2);

        public boolean isAlwaysOnTop(Window var1);
    }
}

