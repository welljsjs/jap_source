/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.crypto.JAPCertificate;
import anon.platform.AbstractOS;
import anon.util.JAPMessages;
import gui.GUIUtils;
import gui.SimpleFileFilter;
import gui.dialog.JAPDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public final class JAPUtil {
    private static final String MSG_DATE_UNIT = (class$jap$JAPUtil == null ? (class$jap$JAPUtil = JAPUtil.class$("jap.JAPUtil")) : class$jap$JAPUtil).getName() + "_";
    static /* synthetic */ Class class$jap$JAPUtil;

    public static JAPDialog.ILinkedInformation createDialogBrowserLink(String string) {
        URL uRL;
        try {
            uRL = new URL(string);
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
        final URL uRL2 = uRL;
        return new JAPDialog.LinkedInformationAdapter(){

            public String getMessage() {
                return uRL2.toString();
            }

            public int getType() {
                return 2;
            }

            public void clicked(boolean bl) {
                AbstractOS.getInstance().openURL(uRL2);
            }
        };
    }

    public static String formatEuroCentValue(long l, boolean bl) {
        long l2 = l / 100L;
        long l3 = l - l2 * 100L;
        String string = new Long(l2).toString();
        String string2 = new Long(l3).toString();
        String string3 = l3 < 10L ? "0" : "";
        String string4 = JAPMessages.getLocale().getLanguage();
        return string + JAPUtil.getCurrencyDelimiter(string4) + string3 + string2 + (bl ? " Euro" : "");
    }

    public static String getCurrencyDelimiter(String string) {
        if (string.equalsIgnoreCase("en")) {
            return new String(".");
        }
        return new String(",");
    }

    public static void setMnemonic(AbstractButton abstractButton, String string) {
        if (abstractButton == null || string == null || string.equals("")) {
            return;
        }
        abstractButton.setMnemonic(string.charAt(0));
    }

    public static void setPerfectTableSize(JTable jTable, Dimension dimension) {
        TableModel tableModel = jTable.getModel();
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < tableModel.getColumnCount(); ++i) {
            int n4;
            Object object;
            TableColumn tableColumn = jTable.getColumnModel().getColumn(i);
            TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
            int n5 = tableColumn.getPreferredWidth();
            int n6 = 0;
            if (tableCellRenderer != null) {
                object = tableCellRenderer.getTableCellRendererComponent(null, tableColumn.getHeaderValue(), false, false, 0, 0);
                n5 = object.getPreferredSize().width;
                n6 = object.getPreferredSize().height;
            }
            if (tableModel.getRowCount() <= 0) continue;
            object = jTable.getDefaultRenderer(tableModel.getColumnClass(i));
            int n7 = 0;
            for (n4 = 0; n4 < tableModel.getRowCount(); ++n4) {
                Object object2 = tableModel.getValueAt(n4, i);
                Component component = object.getTableCellRendererComponent(jTable, object2, false, false, n4, i);
                n7 = Math.max(n7, component.getPreferredSize().width);
                n6 += component.getPreferredSize().height;
            }
            n4 = Math.max(n5, n7);
            tableColumn.setPreferredWidth(n4);
            n += n4;
            n3 = n3 == 0 ? n6 : Math.min(n3, n6);
        }
        n = Math.min(dimension.width, n + 30);
        n2 = Math.min(dimension.height, n3);
        jTable.setPreferredScrollableViewportSize(new Dimension(n, n2));
    }

    public static JFileChooser showFileDialog(Window window) {
        SimpleFileFilter simpleFileFilter = null;
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(0);
        simpleFileFilter = new SimpleFileFilter();
        jFileChooser.addChoosableFileFilter(simpleFileFilter);
        if (simpleFileFilter != null) {
            jFileChooser.setFileFilter(simpleFileFilter);
        }
        jFileChooser.setFileHidingEnabled(false);
        GUIUtils.showMonitoredFileChooser(jFileChooser, window, "__FILE_CHOOSER_OPEN");
        return jFileChooser;
    }

    public static JAPCertificate openCertificate(Window window) throws IOException {
        File file = JAPUtil.showFileDialog(window).getSelectedFile();
        JAPCertificate jAPCertificate = null;
        if (file != null && (jAPCertificate = JAPCertificate.getInstance(file)) == null) {
            throw new IOException("Could not create certificate!");
        }
        return jAPCertificate;
    }

    public static String getDuration(int n, String string) {
        String string2;
        if (string.equals("days") || string.equals("day")) {
            string2 = n == 1 ? "day" : "days";
        } else if (string.equalsIgnoreCase("weeks") || string.equalsIgnoreCase("week")) {
            string2 = n == 1 ? "week" : "weeks";
        } else if (string.equalsIgnoreCase("months") || string.equalsIgnoreCase("month")) {
            string2 = n == 1 ? "month" : "months";
        } else if (string.equalsIgnoreCase("years") || string.equalsIgnoreCase("year")) {
            string2 = n == 1 ? "year" : "years";
        } else {
            return n + "";
        }
        return n + " " + JAPMessages.getString(MSG_DATE_UNIT + string2);
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

