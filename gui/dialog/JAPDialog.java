/*
 * Decompiled with CFR 0.150.
 */
package gui.dialog;

import anon.crypto.AbstractX509AlternativeName;
import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.ILocationSettings;
import gui.JAPHelpContext;
import gui.JAPHtmlMultiLineLabel;
import gui.dialog.DialogContentPane;
import gui.dialog.DialogContentPaneOptions;
import gui.dialog.IDialogOptions;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageObserver;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import logging.LogHolder;
import logging.LogType;

public class JAPDialog
implements Accessible,
WindowConstants,
RootPaneContainer,
MenuContainer,
ImageObserver,
IDialogOptions,
ILocationSettings {
    public static final String XML_ATTR_OPTIMIZED_FORMAT = "dialogOptFormat";
    public static final int FORMAT_GOLDEN_RATIO_PHI = 0;
    public static final int FORMAT_DEFAULT_SCREEN = 1;
    public static final int FORMAT_WIDE_SCREEN = 2;
    private static String ms_strGlobalTitle;
    private static final double[] FORMATS;
    public static final String MSG_ERROR_UNKNOWN;
    public static final String MSG_TITLE_INFO;
    public static final String MSG_TITLE_CONFIRMATION;
    public static final String MSG_TITLE_WARNING;
    public static final String MSG_TITLE_ERROR;
    public static final String MSG_ERROR_UNDISPLAYABLE;
    public static final String MSG_BTN_PROCEED;
    public static final String MSG_BTN_RETRY;
    private static final int NUMBER_OF_HEURISTIC_ITERATIONS = 6;
    private static int m_optimizedFormat;
    private static Hashtable ms_registeredDialogs;
    private static boolean ms_bConsoleOnly;
    private boolean m_bLocationSetManually = false;
    private boolean m_bModal;
    private boolean m_bBlockParentWindow = false;
    private int m_defaultCloseOperation;
    private Vector m_windowListeners = new Vector();
    private Vector m_componentListeners = new Vector();
    private DialogWindowAdapter m_dialogWindowAdapter;
    private boolean m_bForceApplicationModality;
    private boolean m_bDisposed = false;
    private GUIUtils.WindowDocker m_docker;
    private final Object SYNC_DOCK = new Object();
    private JDialog m_internalDialog;
    private Component m_parentComponent;
    private Window m_parentWindow;
    private boolean m_bOnTop = false;
    static /* synthetic */ Class class$gui$dialog$JAPDialog;
    static /* synthetic */ Class class$java$awt$event$WindowListener;
    static /* synthetic */ Class class$java$lang$Class;
    static /* synthetic */ Class class$javax$swing$JDialog;
    static /* synthetic */ Class class$gui$dialog$JAPDialog$ILinkedInformation;
    static /* synthetic */ Class class$gui$dialog$JAPDialog$LinkedCheckBox;
    static /* synthetic */ Class class$java$awt$Container;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setConsoleOnly(boolean bl) {
        ms_bConsoleOnly = bl;
        if (ms_bConsoleOnly) {
            Enumeration<Object> enumeration;
            Vector vector;
            Hashtable hashtable = ms_registeredDialogs;
            synchronized (hashtable) {
                vector = new Vector(ms_registeredDialogs.size());
                enumeration = ms_registeredDialogs.elements();
                while (enumeration.hasMoreElements()) {
                    vector.addElement(enumeration.nextElement());
                }
            }
            enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                ((JAPDialog)enumeration.nextElement()).dispose();
            }
            vector.removeAllElements();
        }
    }

    public static boolean isConsoleOnly() {
        return ms_bConsoleOnly;
    }

    public JAPDialog(Component component, String string, boolean bl) {
        this(component, string, bl, false);
    }

    public JAPDialog(Component component, String string) {
        this(component, string, true);
    }

    public JAPDialog(JAPDialog jAPDialog, String string, boolean bl) {
        this(JAPDialog.getInternalDialog(jAPDialog), string, bl);
    }

    public JAPDialog(JAPDialog jAPDialog, String string) {
        this(JAPDialog.getInternalDialog(jAPDialog), string);
    }

    private JAPDialog(Component component, String string, boolean bl, boolean bl2) {
        EventListener[] arreventListener;
        this.m_parentComponent = component;
        this.m_bForceApplicationModality = bl2;
        this.m_internalDialog = new JOptionPane().createDialog(component, JAPDialog.checkGlobalTitle(string));
        if (this.m_parentComponent == null) {
            this.m_parentComponent = this.m_internalDialog.getParent();
        }
        this.m_internalDialog.getContentPane().removeAll();
        this.m_internalDialog.setResizable(true);
        this.m_internalDialog.setModal(false);
        this.m_internalDialog.setDefaultCloseOperation(0);
        this.setDefaultCloseOperation(2);
        try {
            arreventListener = (EventListener[])(class$javax$swing$JDialog == null ? (class$javax$swing$JDialog = JAPDialog.class$("javax.swing.JDialog")) : class$javax$swing$JDialog).getMethod("getListeners", class$java$lang$Class == null ? (class$java$lang$Class = JAPDialog.class$("java.lang.Class")) : class$java$lang$Class).invoke(this.m_internalDialog, class$java$awt$event$WindowListener == null ? (class$java$awt$event$WindowListener = JAPDialog.class$("java.awt.event.WindowListener")) : class$java$awt$event$WindowListener);
        }
        catch (Exception exception) {
            arreventListener = null;
        }
        for (int i = 0; arreventListener != null && i < arreventListener.length; ++i) {
            this.m_internalDialog.removeWindowListener((WindowListener)arreventListener[i]);
        }
        this.m_dialogWindowAdapter = new DialogWindowAdapter();
        this.m_internalDialog.addWindowListener(this.m_dialogWindowAdapter);
        this.m_parentWindow = GUIUtils.getParentWindow(this.getParentComponent());
        this.setModal(bl);
        ms_registeredDialogs.put(this, this);
        final JAPDialog jAPDialog = this;
        this.addWindowListener(new WindowAdapter(){

            public void windowClosed(WindowEvent windowEvent) {
                ms_registeredDialogs.remove(jAPDialog);
                jAPDialog.removeWindowListener(this);
            }
        });
    }

    public static void setOptimizedFormat(int n) {
        if (n < 0 || n >= FORMATS.length) {
            n = FORMATS.length - 1;
        }
        m_optimizedFormat = n;
    }

    public static int getOptimizedFormat() {
        return m_optimizedFormat;
    }

    public static double getOptimizedFormatInternal(int n) {
        if (n < 0 || n >= FORMATS.length) {
            n = FORMATS.length - 1;
        }
        return FORMATS[n];
    }

    public static double getOptimizedFormatDelta(Window window) {
        return (double)window.getSize().height * JAPDialog.getOptimizedFormatInternal(m_optimizedFormat) - (double)window.getSize().width;
    }

    public static double getOptimizedFormatDelta(JAPDialog jAPDialog) {
        return (double)jAPDialog.getSize().height * JAPDialog.getOptimizedFormatInternal(m_optimizedFormat) - (double)jAPDialog.getSize().width;
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string) {
        JAPDialog.showMessageDialog(JAPDialog.getInternalDialog(jAPDialog), string);
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string, ILinkedInformation iLinkedInformation) {
        JAPDialog.showMessageDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, iLinkedInformation);
    }

    public static void showMessageDialog(Component component, String string) {
        JAPDialog.showMessageDialog(component, string, JAPMessages.getString(MSG_TITLE_INFO), (Icon)null);
    }

    public static void showMessageDialog(Component component, String string, ILinkedInformation iLinkedInformation) {
        JAPDialog.showMessageDialog(component, string, JAPMessages.getString(MSG_TITLE_INFO), (Icon)null, iLinkedInformation);
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string, String string2) {
        JAPDialog.showMessageDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2);
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string, String string2, ILinkedInformation iLinkedInformation) {
        JAPDialog.showMessageDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2, iLinkedInformation);
    }

    public static void showMessageDialog(Component component, String string, String string2) {
        JAPDialog.showMessageDialog(component, string, string2, (Icon)null);
    }

    public static void showMessageDialog(Component component, String string, String string2, ILinkedInformation iLinkedInformation) {
        JAPDialog.showMessageDialog(component, string, string2, null, iLinkedInformation);
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string, Icon icon) {
        JAPDialog.showMessageDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, icon);
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string, Icon icon, ILinkedInformation iLinkedInformation) {
        JAPDialog.showMessageDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, icon, iLinkedInformation);
    }

    public static void showMessageDialog(Component component, String string, Icon icon) {
        JAPDialog.showMessageDialog(component, string, JAPMessages.getString(MSG_TITLE_INFO), icon);
    }

    public static void showMessageDialog(Component component, String string, Icon icon, ILinkedInformation iLinkedInformation) {
        JAPDialog.showMessageDialog(component, string, JAPMessages.getString(MSG_TITLE_INFO), icon, iLinkedInformation);
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string, String string2, Icon icon) {
        JAPDialog.showMessageDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2, icon);
    }

    public static void showMessageDialog(JAPDialog jAPDialog, String string, String string2, Icon icon, ILinkedInformation iLinkedInformation) {
        JAPDialog.showMessageDialog(JAPDialog.getInternalDialog(jAPDialog), string, string2, icon, iLinkedInformation);
    }

    public static void showMessageDialog(Component component, String string, String string2, Icon icon) {
        JAPDialog.showMessageDialog(component, string, string2, icon, null);
    }

    public static void showMessageDialog(Component component, String string, String string2, Icon icon, ILinkedInformation iLinkedInformation) {
        if (string2 == null) {
            string2 = JAPMessages.getString(MSG_TITLE_CONFIRMATION);
        }
        JAPDialog.showConfirmDialog(component, string, JAPDialog.checkGlobalTitle(string2), -1, 1, icon, iLinkedInformation);
    }

    public static void showWarningDialog(JAPDialog jAPDialog, String string) {
        JAPDialog.showWarningDialog(jAPDialog, string, null, null);
    }

    public static void showWarningDialog(Component component, String string) {
        JAPDialog.showWarningDialog(component, string, null, null);
    }

    public static void showWarningDialog(JAPDialog jAPDialog, String string, String string2) {
        JAPDialog.showWarningDialog(jAPDialog, string, string2, null);
    }

    public static void showWarningDialog(Component component, String string, String string2) {
        JAPDialog.showWarningDialog(component, string, string2, null);
    }

    public static void showWarningDialog(JAPDialog jAPDialog, String string, String string2, ILinkedInformation iLinkedInformation) {
        JAPDialog.showWarningDialog(JAPDialog.getInternalDialog(jAPDialog), string, string2, iLinkedInformation);
    }

    public static void showWarningDialog(JAPDialog jAPDialog, String string, ILinkedInformation iLinkedInformation) {
        JAPDialog.showWarningDialog(JAPDialog.getInternalDialog(jAPDialog), string, null, iLinkedInformation);
    }

    public static void showWarningDialog(Component component, String string, ILinkedInformation iLinkedInformation) {
        JAPDialog.showWarningDialog(component, string, null, iLinkedInformation);
    }

    public static void showWarningDialog(Component component, String string, String string2, ILinkedInformation iLinkedInformation) {
        if (string2 == null) {
            string2 = JAPMessages.getString(MSG_TITLE_WARNING);
        }
        JAPDialog.showConfirmDialog(component, string, JAPDialog.checkGlobalTitle(string2), -1, 2, null, iLinkedInformation);
    }

    public static int showConfirmDialog(JAPDialog jAPDialog, String string, String string2, int n, int n2, Icon icon) {
        return JAPDialog.showConfirmDialog(jAPDialog, string, string2, n, n2, icon, null);
    }

    public static int showConfirmDialog(Component component, String string, String string2, int n, int n2, Icon icon) {
        return JAPDialog.showConfirmDialog(component, string, string2, n, n2, icon, null);
    }

    public static int showConfirmDialog(JAPDialog jAPDialog, String string, String string2, int n, int n2, Icon icon, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2, n, n2, icon, iLinkedInformation);
    }

    public static int showConfirmDialog(Component component, String string, String string2, int n, int n2, Icon icon, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog(component, string, string2, new Options(n), n2, icon, iLinkedInformation);
    }

    public static int showConfirmDialog(Component component, String string, Options options, int n) {
        return JAPDialog.showConfirmDialog(component, string, (String)null, options, n, (Icon)null, (ILinkedInformation)null);
    }

    public static int showConfirmDialog(Component component, String string, Options options, int n, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog(component, string, (String)null, options, n, (Icon)null, iLinkedInformation);
    }

    public static int showConfirmDialog(Component component, String string, String string2, Options options, int n) {
        return JAPDialog.showConfirmDialog(component, string, string2, options, n, (Icon)null, null);
    }

    public static int showConfirmDialog(Component component, String string, String string2, Options options, int n, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog(component, string, string2, options, n, null, iLinkedInformation);
    }

    public static int showConfirmDialog(Component component, String string, String string2, final Options options, int n, Icon icon, ILinkedInformation iLinkedInformation) {
        int n2;
        JComponent jComponent;
        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel;
        JAPDialog jAPDialog;
        DialogContentPane dialogContentPane;
        String string3;
        JAPHelpContext.IHelpContext iHelpContext = null;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = true;
        boolean bl4 = true;
        Vector<String> vector = new Vector<String>();
        String string4 = null;
        if (ms_bConsoleOnly) {
            LogHolder.log(1, LogType.GUI, string);
            return Integer.MIN_VALUE;
        }
        if (string == null) {
            string = "";
        }
        String string5 = string = JAPHtmlMultiLineLabel.removeHTMLHEADAndBODYTags(string);
        if (string2 == null) {
            string2 = JAPMessages.getString(MSG_TITLE_CONFIRMATION);
        }
        string2 = JAPDialog.checkGlobalTitle(string2);
        if (iLinkedInformation != null) {
            bl = iLinkedInformation.isApplicationModalityForced();
            bl2 = iLinkedInformation.isOnTop();
            bl3 = iLinkedInformation.isModal();
            bl4 = iLinkedInformation.isCloseWindowActive();
            string4 = iLinkedInformation.getTooltipText();
            if (iLinkedInformation instanceof JAPHelpContext.IHelpContext) {
                iHelpContext = (JAPHelpContext.IHelpContext)((Object)iLinkedInformation);
                if (iLinkedInformation.getType() == 0) {
                    iLinkedInformation = null;
                }
            }
        }
        if (iLinkedInformation != null && iLinkedInformation.getMessage() != null && iLinkedInformation.getMessage().trim().length() > 0) {
            string3 = JAPHtmlMultiLineLabel.removeTagsAndNewLines(iLinkedInformation.getMessage());
            if (iLinkedInformation.getType() != 3 && iLinkedInformation.getType() != 4) {
                string5 = string5 + "<br><a href=\"\">" + string3 + "</a>";
            }
        } else {
            string3 = null;
        }
        if ((dialogContentPane = new DialogContentPane(jAPDialog = new JAPDialog(component, string2, true, bl), new DialogContentPane.Layout(null, n, icon), new DialogContentPaneOptions(options.getOptionType(), iHelpContext, options.getUpdateCallbackHandler())){

            public String getButtonYesOKText() {
                if (options == null) {
                    return null;
                }
                try {
                    return options.getYesOKText();
                }
                catch (Throwable throwable) {
                    return null;
                }
            }

            public String getButtonNoText() {
                if (options == null) {
                    return null;
                }
                try {
                    return options.getNoText();
                }
                catch (Throwable throwable) {
                    return null;
                }
            }

            public String getButtonCancelText() {
                if (options == null) {
                    return null;
                }
                try {
                    return options.getCancelText();
                }
                catch (Throwable throwable) {
                    return null;
                }
            }
        }).getButtonHelp() != null) {
            vector.addElement(dialogContentPane.getButtonHelp().getText());
        }
        String string6 = dialogContentPane.getButtonYesOKText();
        if (dialogContentPane.getButtonYesOK() != null && string6 != null) {
            vector.addElement(string6);
        }
        string6 = dialogContentPane.getButtonCancelText();
        if (dialogContentPane.getButtonCancel() != null && string6 != null) {
            vector.addElement(string6);
        }
        string6 = dialogContentPane.getButtonNoText();
        if (dialogContentPane.getButtonNo() != null && string6 != null) {
            vector.addElement(string6);
        }
        if (!options.isDrawFocusEnabled()) {
            dialogContentPane.getButtonNo().setFocusPainted(false);
            dialogContentPane.getButtonYesOK().setFocusPainted(false);
            dialogContentPane.getButtonCancel().setFocusPainted(false);
        }
        dialogContentPane.setDefaultButtonOperation(2);
        dialogContentPane.updateDialog();
        jAPDialog.pack();
        try {
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeAndWait(new Runnable(){

                    public void run() {
                        JAPHtmlMultiLineLabel jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel("Text");
                        jAPHtmlMultiLineLabel.setText(jAPHtmlMultiLineLabel.getText());
                        jAPHtmlMultiLineLabel.revalidate();
                    }
                });
            }
        }
        catch (InterruptedException interruptedException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        try {
            jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel("Text");
        }
        catch (NullPointerException nullPointerException) {
            if (Thread.currentThread().isInterrupted()) {
                return Integer.MIN_VALUE;
            }
            throw nullPointerException;
        }
        if (jAPHtmlMultiLineLabel.getPreferredSize().width == 0 || jAPHtmlMultiLineLabel.getPreferredSize().height == 0) {
            LogHolder.log(0, LogType.GUI, "Dialog label size is invalid! This dialog might not show any label!");
        }
        try {
            jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel(string5);
            jAPHtmlMultiLineLabel.setFontStyle(0);
        }
        catch (NullPointerException nullPointerException) {
            if (Thread.currentThread().isInterrupted()) {
                return Integer.MIN_VALUE;
            }
            throw nullPointerException;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(jAPHtmlMultiLineLabel.getHTMLDocumentText());
        int n3 = 0;
        String string7 = null;
        while (stringTokenizer.hasMoreTokens()) {
            String string8 = stringTokenizer.nextToken();
            if (string8.length() <= n3) continue;
            n3 = string8.length();
            string7 = string8;
        }
        if (string3 != null && string3.length() > n3) {
            n3 = string3.length();
            string7 = string3;
        }
        PreferredWidthBoxPanel preferredWidthBoxPanel = new PreferredWidthBoxPanel();
        if (string3 != null && (iLinkedInformation.getType() == 3 || iLinkedInformation.getType() == 4)) {
            jComponent = new JCheckBox("Text");
            jComponent.setFont(jAPHtmlMultiLineLabel.getFont());
            preferredWidthBoxPanel.add(jComponent);
        }
        preferredWidthBoxPanel.add(jAPHtmlMultiLineLabel);
        dialogContentPane.setContentPane(preferredWidthBoxPanel);
        dialogContentPane.updateDialog();
        JComponent jComponent2 = (JComponent)jAPDialog.getContentPane();
        Dimension dimension = null;
        Icon icon2 = icon;
        if (icon2 == null) {
            icon2 = DialogContentPane.getMessageIcon(n);
        }
        Object[] arrobject = new String[vector.size()];
        for (int i = 0; i < arrobject.length; ++i) {
            arrobject[i] = vector.elementAt(i).toString();
        }
        JDialog jDialog = new JOptionPane(new JAPHtmlMultiLineLabel(string7), n, options.getOptionType(), icon2, arrobject).createDialog(component, string2);
        jDialog.pack();
        Dimension dimension2 = new Dimension(jDialog.getContentPane().getSize());
        jDialog.dispose();
        jDialog = null;
        dimension2.setSize(Math.max(dimension2.width, jAPDialog.getSize().width), dimension2.height);
        int n4 = 0;
        try {
            Window window = GUIUtils.getParentWindow(component);
            if (window == null) {
                return Integer.MIN_VALUE;
            }
            n4 = window.getSize().width;
        }
        catch (NullPointerException nullPointerException) {
            if (Thread.currentThread().isInterrupted()) {
                return Integer.MIN_VALUE;
            }
            throw nullPointerException;
        }
        if (n4 < dimension2.width * 4) {
            n4 = dimension2.width * 4;
        }
        n4 = Math.min(jComponent2.getWidth(), n4);
        double d = Double.MAX_VALUE;
        int n5 = n2 = Math.min(500, jComponent2.getWidth());
        int n6 = 0;
        int n7 = Math.max(jAPHtmlMultiLineLabel.getMinimumSize().width, new JAPHtmlMultiLineLabel((String)string7).getPreferredSize().width);
        boolean bl5 = true;
        for (int i = 0; i < 6; ++i) {
            preferredWidthBoxPanel = new PreferredWidthBoxPanel();
            preferredWidthBoxPanel.add(jComponent2);
            preferredWidthBoxPanel.setPreferredWidth(n2);
            jAPDialog.setContentPane(preferredWidthBoxPanel);
            jAPDialog.pack();
            jAPHtmlMultiLineLabel.setPreferredWidth(jAPHtmlMultiLineLabel.getWidth());
            jAPDialog.pack();
            if (preferredWidthBoxPanel.getHeight() < dimension2.height) {
                LogHolder.log(5, LogType.GUI, "Dialog height was too small.");
                preferredWidthBoxPanel.setPreferredHeigth(dimension2.height);
                jAPDialog.pack();
            }
            n2 = preferredWidthBoxPanel.getWidth();
            double d2 = JAPDialog.getOptimizedFormatDelta(jAPDialog);
            if (Math.abs(d2) < Math.abs(d) && (i == 0 || jAPHtmlMultiLineLabel.getSize().width >= n7)) {
                dimension = new Dimension(preferredWidthBoxPanel.getSize());
                d = d2;
                n5 = n2;
                n2 = (int)((double)n2 + d / 2.0);
                if (jAPHtmlMultiLineLabel.getSize().width < n7) {
                    ++n6;
                } else {
                    bl5 = false;
                    n6 = 0;
                }
            } else {
                n2 = jAPHtmlMultiLineLabel.getSize().width < n7 ? (int)((double)n2 + ((double)(n7 - jAPHtmlMultiLineLabel.getSize().width + n6) + 1.0)) : n5 + (int)(d / (3.0 * ((double)n6 + 1.0)));
                ++n6;
            }
            n2 = Math.max(n2, dimension2.width);
            if (n2 == n5) break;
        }
        if (bl5) {
            LogHolder.log(3, LogType.GUI, "Auto-formatting of dialog failed!");
            dimension = new Dimension(preferredWidthBoxPanel.getSize());
        }
        preferredWidthBoxPanel = new PreferredWidthBoxPanel();
        try {
            jAPHtmlMultiLineLabel = new JAPHtmlMultiLineLabel("<font color=#000000>" + string + "</font>");
            jAPHtmlMultiLineLabel.setFontStyle(0);
        }
        catch (NullPointerException nullPointerException) {
            if (Thread.currentThread().isInterrupted()) {
                return Integer.MIN_VALUE;
            }
            throw nullPointerException;
        }
        preferredWidthBoxPanel.add(jAPHtmlMultiLineLabel);
        jComponent = null;
        if (string3 != null) {
            if (iLinkedInformation.getType() == 2) {
                LogHolder.log(3, LogType.GUI, "The selectable link feature does not format properly on Java 7!!");
                JTextPane jTextPane = GUIUtils.createSelectableAndResizeableLabel(preferredWidthBoxPanel);
                jTextPane.setText(string3);
                jTextPane.setFont(jAPHtmlMultiLineLabel.getFont());
                jTextPane.setMargin(new Insets(0, 0, 0, 0));
                jTextPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
                jTextPane.setForeground(Color.blue);
                jTextPane.setCursor(Cursor.getPredefinedCursor(12));
                jComponent = jTextPane;
                jComponent.addMouseListener(new LinkedInformationClickListener(iLinkedInformation));
            } else if (iLinkedInformation.getType() == 3 || iLinkedInformation.getType() == 4) {
                jComponent = new JCheckBox(string3, iLinkedInformation.getType() == 3);
                jComponent.setFont(jAPHtmlMultiLineLabel.getFont());
                ((AbstractButton)jComponent).addItemListener(new LinkedInformationClickListener(iLinkedInformation));
            } else {
                jComponent = new JAPHtmlMultiLineLabel("<a href=\"\">" + string3 + "</a>");
                jComponent.addMouseListener(new LinkedInformationClickListener(iLinkedInformation));
                jComponent.setCursor(Cursor.getPredefinedCursor(12));
            }
            jComponent.setToolTipText(string4);
            preferredWidthBoxPanel.add(jComponent);
        }
        dialogContentPane.setContentPane(preferredWidthBoxPanel);
        dialogContentPane.setDefaultButton(options.getDefaultButton());
        dialogContentPane.updateDialog();
        ((JComponent)jAPDialog.getContentPane()).setPreferredSize(dimension);
        jAPDialog.pack();
        if (d != JAPDialog.getOptimizedFormatDelta(jAPDialog)) {
            LogHolder.log(3, LogType.GUI, "Calculated dialog size differs from real size!");
        }
        LogHolder.log(5, LogType.GUI, "Dialog golden ratio delta: " + JAPDialog.getOptimizedFormatDelta(jAPDialog));
        jAPDialog.setResizable(false);
        if (bl4) {
            jAPDialog.setDefaultCloseOperation(2);
        } else {
            jAPDialog.setDefaultCloseOperation(0);
        }
        jAPDialog.addWindowListener(new SimpleDialogButtonFocusWindowAdapter(dialogContentPane));
        jAPDialog.m_bOnTop = bl2;
        if (!bl3) {
            jAPDialog.setModal(false);
        }
        jAPDialog.setVisible(true);
        jAPDialog = null;
        return dialogContentPane.getButtonValue();
    }

    public static boolean showYesNoDialog(JAPDialog jAPDialog, String string) {
        return JAPDialog.showYesNoDialog(JAPDialog.getInternalDialog(jAPDialog), string);
    }

    public static boolean showYesNoDialog(JAPDialog jAPDialog, String string, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showYesNoDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, iLinkedInformation);
    }

    public static boolean showYesNoDialog(Component component, String string) {
        return JAPDialog.showYesNoDialog(component, string, (String)null);
    }

    public static boolean showYesNoDialog(Component component, String string, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showYesNoDialog(component, string, null, iLinkedInformation);
    }

    public static boolean showYesNoDialog(JAPDialog jAPDialog, String string, String string2) {
        return JAPDialog.showYesNoDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2);
    }

    public static boolean showYesNoDialog(JAPDialog jAPDialog, String string, String string2, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showYesNoDialog(JAPDialog.getInternalDialog(jAPDialog), string, string2, iLinkedInformation);
    }

    public static boolean showYesNoDialog(Component component, String string, String string2) {
        return JAPDialog.showYesNoDialog(component, string, string2, null);
    }

    public static boolean showYesNoDialog(Component component, String string, String string2, ILinkedInformation iLinkedInformation) {
        int n;
        if (string2 == null) {
            string2 = JAPMessages.getString(MSG_TITLE_CONFIRMATION);
        }
        return 0 == (n = JAPDialog.showConfirmDialog(component, string, JAPDialog.checkGlobalTitle(string2), 0, 3, null, iLinkedInformation));
    }

    public static int showConfirmDialog(JAPDialog jAPDialog, String string, int n, int n2, Icon icon) {
        return JAPDialog.showConfirmDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, null, n, n2, icon, null);
    }

    public static int showConfirmDialog(Component component, String string, int n, int n2, Icon icon) {
        return JAPDialog.showConfirmDialog(component, string, null, n, n2, icon, null);
    }

    public static int showConfirmDialog(JAPDialog jAPDialog, String string, int n, int n2) {
        return JAPDialog.showConfirmDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, null, n, n2, null, null);
    }

    public static int showConfirmDialog(Component component, String string, int n, int n2) {
        return JAPDialog.showConfirmDialog(component, string, null, n, n2, null, null);
    }

    public static int showConfirmDialog(JAPDialog jAPDialog, String string, int n, int n2, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, null, n, n2, null, iLinkedInformation);
    }

    public static int showConfirmDialog(Component component, String string, int n, int n2, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog(component, string, null, n, n2, null, iLinkedInformation);
    }

    public static int showConfirmDialog(JAPDialog jAPDialog, String string, String string2, int n, int n2) {
        return JAPDialog.showConfirmDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2, n, n2, null, null);
    }

    public static int showConfirmDialog(Component component, String string, String string2, int n, int n2) {
        return JAPDialog.showConfirmDialog(component, string, string2, n, n2, null, null);
    }

    public static int showConfirmDialog(JAPDialog jAPDialog, String string, String string2, int n, int n2, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2, n, n2, null, iLinkedInformation);
    }

    public static int showConfirmDialog(Component component, String string, String string2, int n, int n2, ILinkedInformation iLinkedInformation) {
        return JAPDialog.showConfirmDialog(component, string, string2, n, n2, null, iLinkedInformation);
    }

    public static void showErrorDialog(JAPDialog jAPDialog, String string) {
        JAPDialog.showErrorDialog(jAPDialog, string, (Throwable)null);
    }

    public static void showErrorDialog(Component component, String string, String string2, ILinkedInformation iLinkedInformation) {
        JAPDialog.showErrorDialog(component, string, string2, null, iLinkedInformation);
    }

    public static void showErrorDialog(Component component, String string, ILinkedInformation iLinkedInformation) {
        JAPDialog.showErrorDialog(component, string, null, null, iLinkedInformation);
    }

    public static void showErrorDialog(Component component, String string) {
        JAPDialog.showErrorDialog(component, string, (Throwable)null);
    }

    public static void showErrorDialog(Component component, String string, String string2) {
        JAPDialog.showErrorDialog(component, string, string2, null, null);
    }

    public static void showErrorDialog(JAPDialog jAPDialog, String string, String string2) {
        JAPDialog.showErrorDialog(JAPDialog.getInternalDialog(jAPDialog), string, string2, null, null);
    }

    public static void showErrorDialog(JAPDialog jAPDialog, String string, ILinkedInformation iLinkedInformation) {
        JAPDialog.showErrorDialog(JAPDialog.getInternalDialog(jAPDialog), string, null, null, null);
    }

    public static void showErrorDialog(JAPDialog jAPDialog, Throwable throwable) {
        JAPDialog.showErrorDialog((Component)JAPDialog.getInternalDialog(jAPDialog), null, null, throwable);
    }

    public static void showErrorDialog(Component component, Throwable throwable) {
        JAPDialog.showErrorDialog(component, null, null, throwable);
    }

    public static void showErrorDialog(JAPDialog jAPDialog, String string, Throwable throwable) {
        JAPDialog.showErrorDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, throwable);
    }

    public static void showErrorDialog(Component component, String string, Throwable throwable) {
        JAPDialog.showErrorDialog(component, string, null, throwable);
    }

    public static void showErrorDialog(JAPDialog jAPDialog, String string, String string2, Throwable throwable) {
        JAPDialog.showErrorDialog((Component)JAPDialog.getInternalDialog(jAPDialog), string, string2, throwable);
    }

    public static void showErrorDialog(Component component, String string, String string2, Throwable throwable) {
        JAPDialog.showErrorDialog(component, string, string2, throwable, null);
    }

    public static void showErrorDialog(Component component, String string, String string2, Throwable throwable, ILinkedInformation iLinkedInformation) {
        boolean bl = false;
        if ((string = JAPDialog.retrieveErrorMessage(string, throwable)) == null) {
            string = JAPMessages.getString(MSG_ERROR_UNKNOWN);
            bl = true;
        }
        int n = LogType.GUI;
        LogHolder.log(3, n, string, 1);
        if (throwable != null) {
            if (bl) {
                LogHolder.log(3, n, throwable);
            } else {
                LogHolder.log(6, n, throwable);
            }
        }
        try {
            if (string2 == null) {
                string2 = JAPMessages.getString(MSG_TITLE_ERROR);
            }
            JAPDialog.showConfirmDialog(component, string, JAPDialog.checkGlobalTitle(string2), -1, 0, null, iLinkedInformation);
        }
        catch (Exception exception) {
            LogHolder.log(2, LogType.GUI, JAPMessages.getString(MSG_ERROR_UNDISPLAYABLE));
            LogHolder.log(2, LogType.GUI, exception);
        }
    }

    public static String retrieveErrorMessage(String string, Throwable throwable) {
        if (string == null || string.trim().length() == 0) {
            if (throwable == null || throwable.getMessage() == null) {
                string = null;
            } else {
                string = throwable.getMessage();
                if (string == null || string.trim().length() == 0) {
                    string = null;
                }
            }
        }
        return string;
    }

    public Component getGlassPane() {
        return this.m_internalDialog.getGlassPane();
    }

    public JLayeredPane getLayeredPane() {
        return this.m_internalDialog.getLayeredPane();
    }

    public JRootPane getRootPane() {
        return this.m_internalDialog.getRootPane();
    }

    public final Container getContentPane() {
        return this.m_internalDialog.getContentPane();
    }

    public void setContentPane(Container container) {
        this.m_internalDialog.setContentPane(container);
    }

    public void setGlassPane(Component component) {
        this.m_internalDialog.setGlassPane(component);
    }

    public void setLayeredPane(JLayeredPane jLayeredPane) {
        this.m_internalDialog.setLayeredPane(jLayeredPane);
    }

    public final Component getParentComponent() {
        return this.m_parentComponent;
    }

    public final Window getOwner() {
        return this.m_parentWindow;
    }

    public void setName(String string) {
        this.m_internalDialog.setName(string);
    }

    public String getName() {
        return this.m_internalDialog.getName();
    }

    public void setEnabled(boolean bl) {
        this.m_internalDialog.setEnabled(bl);
    }

    public void setAlwaysOnTop(boolean bl) {
        if (!this.isVisible()) {
            this.m_bOnTop = bl;
        }
    }

    public boolean isVisible() {
        return this.m_internalDialog.isVisible();
    }

    public boolean isDisplayable() {
        return this.m_internalDialog.isDisplayable();
    }

    public void setVisible(boolean bl) {
        this.setVisible(bl, true);
    }

    public final void setVisible(boolean bl, boolean bl2) {
        if (this.isDisposed()) {
            return;
        }
        if (bl && !this.m_bLocationSetManually && !this.isVisible()) {
            if (bl2) {
                GUIUtils.setLocationRelativeTo(this.getParentComponent(), this.m_internalDialog, 0);
            } else {
                GUIUtils.setLocationRelativeTo(this.getOwner(), this.m_internalDialog, 5);
            }
        }
        String string = this.m_internalDialog.getName();
        String string2 = null;
        boolean bl3 = GUIUtils.isAlwaysOnTop(this.getOwner());
        if (this.m_bOnTop || bl3) {
            GUIUtils.setAlwaysOnTop(this.getOwner(), false);
            this.getOwner().toBack();
            string2 = this.getOwner().getName();
            this.m_internalDialog.setName(string2);
            GUIUtils.setAlwaysOnTop(this.m_internalDialog, true);
        }
        this.setVisibleInternal(bl);
        if (this.m_bOnTop || bl3) {
            String string3 = this.m_internalDialog.getName();
            this.m_internalDialog.setName("JAP " + Double.toString(Math.random()));
            GUIUtils.setAlwaysOnTop(this.getOwner(), bl3);
            GUIUtils.setAlwaysOnTop(this.m_internalDialog, false);
            if (string != null && string2 != null && string3 != null && string3.equals(string2)) {
                this.m_internalDialog.setName(string);
            }
        }
    }

    public static void setGlobalTitle(String string) {
        ms_strGlobalTitle = string;
    }

    private static String checkGlobalTitle(String string) {
        String string2 = ms_strGlobalTitle;
        if (string2 != null && string != null && string.indexOf(string2) < 0) {
            string = string + " - " + string2;
        }
        return string;
    }

    public void setTitle(String string) {
        this.m_internalDialog.setTitle(string);
    }

    public String getTitle() {
        return this.m_internalDialog.getTitle();
    }

    public void setJMenuBar(JMenuBar jMenuBar) {
        this.m_internalDialog.setJMenuBar(jMenuBar);
    }

    public JMenuBar getJMenuBar() {
        return this.m_internalDialog.getJMenuBar();
    }

    public void toFront() {
        this.m_internalDialog.toFront();
    }

    public void toBack() {
        this.m_internalDialog.toBack();
    }

    public final void setModal(boolean bl) {
        if (this.m_bForceApplicationModality) {
            this.m_bModal = false;
            this.m_internalDialog.setModal(bl);
        } else if (!this.isVisible()) {
            this.m_bModal = bl;
        }
    }

    public boolean isModal() {
        if (this.m_bForceApplicationModality) {
            return this.m_internalDialog.isModal();
        }
        return this.m_bModal;
    }

    public boolean isEnabled() {
        return this.m_internalDialog.isEnabled();
    }

    public boolean isResizable() {
        return this.m_internalDialog.isResizable();
    }

    public boolean isDisposed() {
        return this.m_bDisposed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void dispose() {
        this.m_bDisposed = true;
        if (this.m_bBlockParentWindow) {
            this.m_bBlockParentWindow = false;
            this.m_parentWindow.setEnabled(true);
            if (this.m_parentWindow.isVisible()) {
                this.m_parentWindow.setVisible(true);
            }
        }
        this.m_internalDialog.setVisible(false);
        this.m_internalDialog.dispose();
        Object object = this.SYNC_DOCK;
        synchronized (object) {
            if (this.m_docker != null) {
                this.m_docker.finalize();
                this.m_docker = null;
            }
        }
        object = this.m_internalDialog.getTreeLock();
        synchronized (object) {
            Enumeration enumeration = this.m_windowListeners.elements();
            while (enumeration.hasMoreElements()) {
                final WindowListener windowListener = (WindowListener)enumeration.nextElement();
                Thread thread = new Thread(new Runnable(){

                    public void run() {
                        SwingUtilities.invokeLater(new Runnable(){

                            public void run() {
                                windowListener.windowClosed(new WindowEvent(JAPDialog.this.m_internalDialog, 202));
                            }
                        });
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
            this.m_windowListeners.removeAllElements();
            enumeration = ((Vector)this.m_componentListeners.clone()).elements();
            while (enumeration.hasMoreElements()) {
                this.removeComponentListener((ComponentListener)enumeration.nextElement());
            }
            this.m_componentListeners.removeAllElements();
            this.m_internalDialog.removeWindowListener(this.m_dialogWindowAdapter);
            this.m_dialogWindowAdapter = null;
            this.m_internalDialog.getContentPane().removeAll();
            this.m_internalDialog.getRootPane().removeAll();
            this.m_internalDialog.getLayeredPane().removeAll();
            this.m_internalDialog.getTreeLock().notifyAll();
        }
    }

    public void validate() {
        this.m_internalDialog.validate();
    }

    public void requestFocus() {
        this.m_internalDialog.requestFocus();
    }

    public final Dimension getSize() {
        return this.m_internalDialog.getSize();
    }

    public final Dimension getPreferredSize() {
        return this.m_internalDialog.getPreferredSize();
    }

    public final void setSize(int n, int n2) {
        this.m_internalDialog.setSize(n, n2);
    }

    public final void setLocation(Point point) {
        this.m_bLocationSetManually = true;
        this.m_internalDialog.setLocation(point);
    }

    public final Rectangle getScreenBounds() {
        return GUIUtils.getCurrentScreen(this.m_internalDialog).getBounds();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDockable(boolean bl) {
        Object object = this.SYNC_DOCK;
        synchronized (object) {
            if (this.m_docker == null && bl) {
                this.m_docker = new GUIUtils.WindowDocker(this.m_internalDialog);
            } else if (this.m_docker != null && !bl) {
                this.m_docker.finalize();
                this.m_docker = null;
            }
        }
    }

    public void resetAutomaticLocation(boolean bl) {
        this.m_bLocationSetManually = bl;
    }

    public final void setLocationCenteredOnScreen() {
        this.m_bLocationSetManually = true;
        GUIUtils.centerOnScreen(this.m_internalDialog);
    }

    public final void setLocationRelativeTo(Component component, int n) {
        this.m_bLocationSetManually = true;
        GUIUtils.setLocationRelativeTo(component, this.m_internalDialog, n);
    }

    public void restoreLocation(Point point) {
        if (GUIUtils.restoreLocation(this.m_internalDialog, point)) {
            this.m_bLocationSetManually = true;
        }
    }

    public void restoreSize(Dimension dimension) {
        GUIUtils.restoreSize(this.m_internalDialog, dimension);
    }

    public void moveToUpRightCorner() {
        GUIUtils.moveToUpRightCorner(this.m_internalDialog);
    }

    public final void setLocation(int n, int n2) {
        this.m_bLocationSetManually = true;
        this.m_internalDialog.setLocation(n, n2);
    }

    public final void setSize(Dimension dimension) {
        this.m_internalDialog.setSize(dimension);
    }

    public void setResizable(boolean bl) {
        this.m_internalDialog.setResizable(bl);
    }

    public final Point getLocation() {
        return this.m_internalDialog.getLocation();
    }

    public boolean imageUpdate(Image image, int n, int n2, int n3, int n4, int n5) {
        return this.m_internalDialog.imageUpdate(image, n, n2, n3, n4, n5);
    }

    public final AccessibleContext getAccessibleContext() {
        return this.m_internalDialog.getAccessibleContext();
    }

    public Font getFont() {
        return this.m_internalDialog.getFont();
    }

    public void remove(MenuComponent menuComponent) {
        this.m_internalDialog.remove(menuComponent);
    }

    public boolean postEvent(Event event) {
        return this.m_internalDialog.postEvent(event);
    }

    public final void setDefaultCloseOperation(int n) {
        this.m_defaultCloseOperation = n;
    }

    public final int getDefaultCloseOperation() {
        return this.m_defaultCloseOperation;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void addWindowListener(WindowListener windowListener) {
        if (windowListener != null) {
            Object object = this.m_internalDialog.getTreeLock();
            synchronized (object) {
                this.m_windowListeners.addElement(windowListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void addComponentListener(ComponentListener componentListener) {
        Object object = this.m_internalDialog.getTreeLock();
        synchronized (object) {
            if (componentListener != null && !this.m_componentListeners.contains(componentListener)) {
                this.m_componentListeners.addElement(componentListener);
                this.m_internalDialog.addComponentListener(componentListener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void removeComponentListener(ComponentListener componentListener) {
        Object object = this.m_internalDialog.getTreeLock();
        synchronized (object) {
            this.m_componentListeners.removeElement(componentListener);
            this.m_internalDialog.removeComponentListener(componentListener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void removeWindowListener(WindowListener windowListener) {
        Object object = this.m_internalDialog.getTreeLock();
        synchronized (object) {
            this.m_windowListeners.removeElement(windowListener);
        }
    }

    public final void pack() {
        this.m_internalDialog.pack();
    }

    public Insets getInsets() {
        return this.m_internalDialog.getInsets();
    }

    void doWindowClosing() {
        this.m_dialogWindowAdapter.windowClosing(new WindowEvent(this.m_internalDialog, 201));
    }

    private static Window getInternalDialog(JAPDialog jAPDialog) {
        if (jAPDialog == null) {
            return null;
        }
        return jAPDialog.m_internalDialog;
    }

    private static boolean requestFocusForFirstFocusableComponent(Container container) {
        try {
            (class$java$awt$Container == null ? (class$java$awt$Container = JAPDialog.class$("java.awt.Container")) : class$java$awt$Container).getMethod("isFocusable", null).invoke(container, null);
            return true;
        }
        catch (Exception exception) {
            for (int i = 0; i < container.getComponentCount(); ++i) {
                if (container.getComponent(i) instanceof Container && JAPDialog.requestFocusForFirstFocusableComponent((Container)container.getComponent(i))) {
                    return true;
                }
                if (!container.getComponent(i).isFocusTraversable()) continue;
                container.getComponent(i).requestFocus();
                return true;
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setVisibleInternal(boolean bl) {
        if (this.isVisible() && this.m_bBlockParentWindow && !bl) {
            this.m_parentWindow.setEnabled(true);
            if (this.m_parentWindow.isVisible()) {
                this.m_parentWindow.setVisible(true);
            }
        }
        boolean bl2 = this.m_bBlockParentWindow = bl && this.m_bModal;
        if (this.m_bBlockParentWindow) {
            this.m_parentWindow.setEnabled(false);
        }
        if (this.m_bForceApplicationModality) {
            this.m_internalDialog.setVisible(bl);
            return;
        }
        Object object = this.m_internalDialog.getTreeLock();
        synchronized (object) {
            this.m_internalDialog.setVisible(bl);
            if (bl) {
                if (this.getContentPane() != null && this.getContentPane().isVisible()) {
                    this.getContentPane().setVisible(false);
                    this.getContentPane().setVisible(true);
                }
                this.m_internalDialog.toFront();
            }
            this.m_internalDialog.getTreeLock().notifyAll();
        }
        if (bl) {
            JAPDialog.requestFocusForFirstFocusableComponent(this.m_internalDialog.getContentPane());
            object = this.m_internalDialog.getRootPane().getDefaultButton();
            if (object != null) {
                ((JComponent)object).requestFocus();
            }
        }
        if (this.m_bBlockParentWindow) {
            object = new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void run() {
                    try {
                        BlockedWindowDeactivationAdapter blockedWindowDeactivationAdapter = new BlockedWindowDeactivationAdapter();
                        JAPDialog.this.m_parentWindow.addWindowListener(blockedWindowDeactivationAdapter);
                        JAPDialog.this.m_parentWindow.addFocusListener(blockedWindowDeactivationAdapter);
                        if (SwingUtilities.isEventDispatchThread()) {
                            EventQueue eventQueue = JAPDialog.this.m_internalDialog.getToolkit().getSystemEventQueue();
                            while (JAPDialog.this.isVisible()) {
                                Class<?> class_;
                                AWTEvent aWTEvent = eventQueue.getNextEvent();
                                if (JAPDialog.this.m_bBlockParentWindow && JAPDialog.this.m_parentWindow.isEnabled()) {
                                    JAPDialog.this.m_parentWindow.setEnabled(false);
                                }
                                try {
                                    class_ = Class.forName("java.awt.ActiveEvent");
                                }
                                catch (ClassNotFoundException classNotFoundException) {
                                    class_ = null;
                                }
                                Object object = aWTEvent.getSource();
                                if (object == JAPDialog.this.m_internalDialog) {
                                    if (aWTEvent instanceof WindowEvent) {
                                        if (((WindowEvent)aWTEvent).getID() == 201) {
                                            JAPDialog.this.m_dialogWindowAdapter.windowClosing((WindowEvent)aWTEvent);
                                            continue;
                                        }
                                    } else if (!(aWTEvent instanceof KeyEvent) || JAPDialog.this.getRootPane().getDefaultButton() != null) {
                                        // empty if block
                                    }
                                }
                                if (class_ != null && class_.isInstance(aWTEvent)) {
                                    class_.getMethod("dispatch", null).invoke(aWTEvent, null);
                                    continue;
                                }
                                if (object instanceof Component) {
                                    if (object == JAPDialog.this.getParentComponent() && aWTEvent instanceof WindowEvent && ((WindowEvent)aWTEvent).getID() == 201) continue;
                                    try {
                                        ((Component)object).dispatchEvent(aWTEvent);
                                    }
                                    catch (IllegalMonitorStateException illegalMonitorStateException) {
                                        LogHolder.log(5, LogType.GUI, illegalMonitorStateException);
                                    }
                                    continue;
                                }
                                if (!(object instanceof MenuComponent)) continue;
                                ((MenuComponent)object).dispatchEvent(aWTEvent);
                            }
                        } else {
                            Object object = JAPDialog.this.m_internalDialog.getTreeLock();
                            synchronized (object) {
                                while (JAPDialog.this.isVisible()) {
                                    try {
                                        JAPDialog.this.m_internalDialog.getTreeLock().wait();
                                    }
                                    catch (InterruptedException interruptedException) {
                                        // empty catch block
                                        break;
                                    }
                                }
                                JAPDialog.this.m_internalDialog.getTreeLock().notifyAll();
                            }
                        }
                        JAPDialog.this.m_parentWindow.removeWindowListener(blockedWindowDeactivationAdapter);
                        JAPDialog.this.m_parentWindow.removeFocusListener(blockedWindowDeactivationAdapter);
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.GUI, exception);
                    }
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                object.run();
            } else {
                try {
                    SwingUtilities.invokeAndWait((Runnable)object);
                }
                catch (InterruptedException interruptedException) {
                    this.setVisible(false);
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.GUI, exception);
                }
            }
            if (!this.m_parentWindow.isEnabled()) {
                this.m_bBlockParentWindow = false;
                this.m_parentWindow.setEnabled(!ms_bConsoleOnly);
                if (this.m_parentWindow.isVisible()) {
                    this.m_parentWindow.setVisible(true);
                }
            }
            if (ms_bConsoleOnly) {
                this.m_parentWindow.setEnabled(false);
            }
        }
        object = this.m_internalDialog.getTreeLock();
        synchronized (object) {
            this.m_internalDialog.getTreeLock().notifyAll();
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
        FORMATS = new double[]{(1.0 + Math.sqrt(5.0)) / 2.0, 1.3333333333333333, 1.7777777777777777};
        MSG_ERROR_UNKNOWN = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_errorUnknown";
        MSG_TITLE_INFO = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_titleInfo";
        MSG_TITLE_CONFIRMATION = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_titleConfirmation";
        MSG_TITLE_WARNING = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_titleWarning";
        MSG_TITLE_ERROR = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_titleError";
        MSG_ERROR_UNDISPLAYABLE = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_errorUndisplayable";
        MSG_BTN_PROCEED = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_proceed";
        MSG_BTN_RETRY = (class$gui$dialog$JAPDialog == null ? (class$gui$dialog$JAPDialog = JAPDialog.class$("gui.dialog.JAPDialog")) : class$gui$dialog$JAPDialog).getName() + "_retry";
        m_optimizedFormat = 2;
        ms_registeredDialogs = new Hashtable();
        ms_bConsoleOnly = false;
    }

    private class BlockedWindowDeactivationAdapter
    extends WindowAdapter
    implements FocusListener {
        private BlockedWindowDeactivationAdapter() {
        }

        public void windowActivated(WindowEvent windowEvent) {
            this.deactivate(windowEvent.getWindow());
        }

        public void focusGained(FocusEvent focusEvent) {
            this.deactivate((Window)focusEvent.getComponent());
        }

        public void focusLost(FocusEvent focusEvent) {
        }

        private void deactivate(Window window) {
            if (JAPDialog.this.m_bBlockParentWindow) {
                JAPDialog.this.toFront();
                if (window.isEnabled()) {
                    window.setEnabled(false);
                }
            }
        }
    }

    private static class PreferredWidthBoxPanel
    extends JPanel {
        private static final long serialVersionUID = 1L;
        private int m_preferredWidth = 0;
        private int m_preferredHeigth = 0;

        public PreferredWidthBoxPanel() {
            BoxLayout boxLayout = new BoxLayout(this, 1);
            this.setLayout(boxLayout);
        }

        public void setPreferredWidth(int n) {
            this.m_preferredHeigth = 0;
            this.m_preferredWidth = n;
        }

        public void setPreferredHeigth(int n) {
            this.m_preferredHeigth = n;
            this.m_preferredWidth = 0;
        }

        public Dimension getPreferredSize() {
            if (this.m_preferredWidth <= 0 && this.m_preferredHeigth <= 0) {
                return super.getPreferredSize();
            }
            if (this.m_preferredWidth > 0) {
                return new Dimension(this.m_preferredWidth, super.getPreferredSize().height);
            }
            return new Dimension(super.getPreferredSize().width, this.m_preferredHeigth);
        }
    }

    private static class LinkedInformationClickListener
    extends MouseAdapter
    implements ItemListener {
        private ILinkedInformation m_linkedInformation;

        public LinkedInformationClickListener(ILinkedInformation iLinkedInformation) {
            this.m_linkedInformation = iLinkedInformation;
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            this.m_linkedInformation.clicked(false);
        }

        public void itemStateChanged(ItemEvent itemEvent) {
            this.m_linkedInformation.clicked(((JCheckBox)itemEvent.getSource()).isSelected());
        }
    }

    private static class SimpleDialogButtonFocusWindowAdapter
    extends WindowAdapter {
        private DialogContentPane m_contentPane;

        public SimpleDialogButtonFocusWindowAdapter(DialogContentPane dialogContentPane) {
            this.m_contentPane = dialogContentPane;
        }

        public void windowOpened(WindowEvent windowEvent) {
            if (this.m_contentPane.getButtonCancel() != null) {
                this.m_contentPane.getButtonCancel().requestFocus();
            } else if (this.m_contentPane.getButtonNo() != null) {
                this.m_contentPane.getButtonNo().requestFocus();
            } else if (this.m_contentPane.getButtonYesOK() != null) {
                this.m_contentPane.getButtonYesOK().requestFocus();
            } else if (this.m_contentPane.getButtonHelp() != null) {
                this.m_contentPane.getButtonHelp().requestFocus();
            }
        }
    }

    private class DialogWindowAdapter
    implements WindowListener {
        private DialogWindowAdapter() {
        }

        public void windowOpened(WindowEvent windowEvent) {
            Vector vector = (Vector)JAPDialog.this.m_windowListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((WindowListener)vector.elementAt(i)).windowOpened(windowEvent);
            }
        }

        public void windowIconified(WindowEvent windowEvent) {
            Vector vector = (Vector)JAPDialog.this.m_windowListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((WindowListener)vector.elementAt(i)).windowIconified(windowEvent);
            }
        }

        public void windowDeiconified(WindowEvent windowEvent) {
            Vector vector = (Vector)JAPDialog.this.m_windowListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((WindowListener)vector.elementAt(i)).windowDeiconified(windowEvent);
            }
        }

        public void windowDeactivated(WindowEvent windowEvent) {
            Vector vector = (Vector)JAPDialog.this.m_windowListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((WindowListener)vector.elementAt(i)).windowDeactivated(windowEvent);
            }
        }

        public void windowActivated(WindowEvent windowEvent) {
            Vector vector = (Vector)JAPDialog.this.m_windowListeners.clone();
            for (int i = 0; i < vector.size(); ++i) {
                ((WindowListener)vector.elementAt(i)).windowActivated(windowEvent);
            }
        }

        public void windowClosed(WindowEvent windowEvent) {
        }

        public void windowClosing(WindowEvent windowEvent) {
            if (JAPDialog.this.isEnabled()) {
                if (JAPDialog.this.getDefaultCloseOperation() == 2) {
                    try {
                        JAPDialog.this.dispose();
                    }
                    catch (IllegalMonitorStateException illegalMonitorStateException) {
                        LogHolder.log(7, LogType.GUI, illegalMonitorStateException);
                    }
                } else if (JAPDialog.this.getDefaultCloseOperation() == 1) {
                    JAPDialog.this.setVisible(false);
                } else if (!JAPDialog.this.isVisible()) {
                    JAPDialog.this.m_internalDialog.setVisible(true);
                    LogHolder.log(6, LogType.GUI, "Fixed old JRE dialog closing bug.");
                }
                Vector vector = (Vector)JAPDialog.this.m_windowListeners.clone();
                for (int i = 0; i < vector.size(); ++i) {
                    ((WindowListener)vector.elementAt(i)).windowClosing(windowEvent);
                }
            }
        }
    }

    public static class Options {
        private int m_optionType;
        private DialogContentPaneOptions.OptionsUpdateCallback m_callbackOptions;
        private DialogContentPaneOptions.IOptionsUpdateCallbackHandler m_callbackHandlerOptions;

        public Options(int n) {
            this.m_optionType = n;
            this.m_callbackHandlerOptions = new DialogContentPaneOptions.IOptionsUpdateCallbackHandler(){

                public void setUpdateCallback(DialogContentPaneOptions.OptionsUpdateCallback optionsUpdateCallback) {
                    Options.this.m_callbackOptions = optionsUpdateCallback;
                }
            };
        }

        private DialogContentPaneOptions.IOptionsUpdateCallbackHandler getUpdateCallbackHandler() {
            return this.m_callbackHandlerOptions;
        }

        public void update() {
            if (this.m_callbackOptions != null) {
                this.m_callbackOptions.update();
            }
        }

        public final int getOptionType() {
            return this.m_optionType;
        }

        public int getDefaultButton() {
            if (this.m_optionType == -2147483647 || this.m_optionType == 2 || this.m_optionType == 1) {
                return 1;
            }
            if (this.m_optionType == 0) {
                return 3;
            }
            return -1;
        }

        public boolean isDrawFocusEnabled() {
            return true;
        }

        public String getYesOKText() {
            return null;
        }

        public String getNoText() {
            return null;
        }

        public String getCancelText() {
            return null;
        }
    }

    public static class LinkedHelpContext
    extends LinkedInformationAdapter
    implements JAPHelpContext.IHelpContext {
        private JAPHelpContext.IHelpContext m_helpContext;

        public LinkedHelpContext(JAPHelpContext.IHelpContext iHelpContext) {
            this.m_helpContext = iHelpContext;
        }

        public LinkedHelpContext(final String string) {
            this.m_helpContext = new JAPHelpContext.IHelpContext(){

                public String getHelpContext() {
                    return string;
                }

                public Component getHelpExtractionDisplayContext() {
                    return null;
                }
            };
        }

        public final String getHelpContext() {
            if (this.m_helpContext == null) {
                return null;
            }
            return this.m_helpContext.getHelpContext();
        }

        public Component getHelpExtractionDisplayContext() {
            if (this.m_helpContext == null) {
                return null;
            }
            return this.m_helpContext.getHelpExtractionDisplayContext();
        }

        public String getMessage() {
            return null;
        }

        public void clicked(boolean bl) {
        }

        public int getType() {
            return 0;
        }

        public final boolean isApplicationModalityForced() {
            return false;
        }
    }

    public static abstract class AbstractLinkedURLAdapter
    extends LinkedHelpContext {
        public static final String MAILTO = "mailto:";

        public AbstractLinkedURLAdapter(String string) {
            super(string);
        }

        public AbstractLinkedURLAdapter() {
            super((String)null);
        }

        public abstract URL getUrl();

        public String getTooltipText() {
            URL uRL = this.getUrl();
            if (uRL != null) {
                String string = this.getUrl().toString();
                if (string.toLowerCase().startsWith("mailto:") && string.length() > "mailto:".length()) {
                    string = string.substring("mailto:".length(), string.length());
                }
                return string;
            }
            return null;
        }

        public String getMessage() {
            return this.getTooltipText();
        }

        public void clicked(boolean bl) {
            if (this.getUrl() != null) {
                AbstractOS.getInstance().openURL(this.getUrl());
            }
        }

        public final int getType() {
            return 1;
        }
    }

    public static class LinkedInformationAdapter
    implements ILinkedInformation {
        public String getTooltipText() {
            return null;
        }

        public String getMessage() {
            return null;
        }

        public void clicked(boolean bl) {
        }

        public int getType() {
            return 0;
        }

        public boolean isApplicationModalityForced() {
            return false;
        }

        public boolean isOnTop() {
            return false;
        }

        public boolean isModal() {
            return true;
        }

        public boolean isCloseWindowActive() {
            return true;
        }
    }

    public static class LinkedInformation
    extends LinkedInformationAdapter {
        private String m_message;
        private String m_eMail;
        private URL m_url;

        public LinkedInformation(String string) {
            this(string, null);
        }

        public LinkedInformation(String string, String string2) {
            this.m_message = string2;
            if (AbstractX509AlternativeName.isValidEMail(string)) {
                this.m_eMail = string;
                if (this.m_message == null) {
                    this.m_message = this.m_eMail;
                }
            } else {
                try {
                    this.m_url = new URL(string);
                    if (this.m_message == null) {
                        this.m_message = this.m_url.toString();
                    }
                }
                catch (MalformedURLException malformedURLException) {
                    // empty catch block
                }
            }
        }

        public final int getType() {
            return 1;
        }

        public final void clicked(boolean bl) {
            if (this.m_eMail != null) {
                AbstractOS.getInstance().openEMail(this.m_eMail);
            } else if (this.m_url != null) {
                AbstractOS.getInstance().openURL(this.m_url);
            }
        }

        public final String getMessage() {
            return this.m_message;
        }
    }

    public static class LinkedCheckBox
    extends LinkedHelpContext {
        public static final String MSG_REMEMBER_ANSWER = (class$gui$dialog$JAPDialog$LinkedCheckBox == null ? (class$gui$dialog$JAPDialog$LinkedCheckBox = JAPDialog.class$("gui.dialog.JAPDialog$LinkedCheckBox")) : class$gui$dialog$JAPDialog$LinkedCheckBox).getName() + "_rememberAnswer";
        public static final String MSG_DO_NOT_SHOW_AGAIN = (class$gui$dialog$JAPDialog$LinkedCheckBox == null ? (class$gui$dialog$JAPDialog$LinkedCheckBox = JAPDialog.class$("gui.dialog.JAPDialog$LinkedCheckBox")) : class$gui$dialog$JAPDialog$LinkedCheckBox).getName() + "_doNotShowAgain";
        private String m_strMessage;
        private boolean m_bDefault;
        private boolean m_bState;

        public LinkedCheckBox(boolean bl) {
            this(bl, (JAPHelpContext.IHelpContext)null);
        }

        public LinkedCheckBox(boolean bl, JAPHelpContext.IHelpContext iHelpContext) {
            this(JAPMessages.getString(MSG_DO_NOT_SHOW_AGAIN), bl, iHelpContext);
        }

        public LinkedCheckBox(boolean bl, String string) {
            this(JAPMessages.getString(MSG_DO_NOT_SHOW_AGAIN), bl, string);
        }

        public LinkedCheckBox(String string, boolean bl) {
            this(string, bl, (JAPHelpContext.IHelpContext)null);
        }

        public LinkedCheckBox(String string, boolean bl, final String string2) {
            this(string, bl, new JAPHelpContext.IHelpContext(){

                public String getHelpContext() {
                    return string2;
                }

                public Component getHelpExtractionDisplayContext() {
                    return null;
                }
            });
        }

        public LinkedCheckBox(String string, boolean bl, JAPHelpContext.IHelpContext iHelpContext) {
            super(iHelpContext);
            this.m_strMessage = string;
            this.m_bState = this.m_bDefault = bl;
        }

        public String getMessage() {
            return this.m_strMessage;
        }

        public void clicked(boolean bl) {
            this.m_bState = bl;
        }

        public final boolean getState() {
            return this.m_bState;
        }

        public final int getType() {
            if (this.m_bDefault) {
                return 3;
            }
            return 4;
        }
    }

    public static class LinkedURLCheckBox
    extends LinkedCheckBox
    implements JAPHelpContext.IURLHelpContext {
        private URL m_url;
        private String m_message;

        public LinkedURLCheckBox(boolean bl, final URL uRL, final String string) {
            super(bl, new JAPHelpContext.IURLHelpContext(){

                public String getURLMessage() {
                    return string;
                }

                public URL getHelpURL() {
                    return uRL;
                }

                public String getHelpContext() {
                    return uRL.toString();
                }

                public Component getHelpExtractionDisplayContext() {
                    return null;
                }
            });
            if (uRL == null) {
                throw new NullPointerException("URL is null!");
            }
            if (string == null) {
                throw new NullPointerException("URL message is null!");
            }
            this.m_url = uRL;
            this.m_message = string;
        }

        public String getURLMessage() {
            return this.m_message;
        }

        public URL getHelpURL() {
            return this.m_url;
        }
    }

    public static interface ILinkedInformation {
        public static final String MSG_MORE_INFO = (class$gui$dialog$JAPDialog$ILinkedInformation == null ? (class$gui$dialog$JAPDialog$ILinkedInformation = JAPDialog.class$("gui.dialog.JAPDialog$ILinkedInformation")) : class$gui$dialog$JAPDialog$ILinkedInformation).getName() + "_moreInfo";
        public static final int TYPE_DEFAULT = 0;
        public static final int TYPE_LINK = 1;
        public static final int TYPE_SELECTABLE_LINK = 2;
        public static final int TYPE_CHECKBOX_TRUE = 3;
        public static final int TYPE_CHECKBOX_FALSE = 4;

        public String getMessage();

        public void clicked(boolean var1);

        public int getType();

        public boolean isApplicationModalityForced();

        public boolean isOnTop();

        public boolean isModal();

        public boolean isCloseWindowActive();

        public String getTooltipText();
    }
}

