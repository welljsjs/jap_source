/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.util.JAPMessages;
import anon.util.Util;
import gui.GUIUtils;
import gui.dialog.JAPDialog;
import jap.JAPModel;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import logging.FileLog;
import logging.ILog;
import logging.LogLevel;
import logging.LogType;

public final class JAPDebug
extends Observable
implements ActionListener,
ILog {
    public static boolean ms_bSystemErrorAllowed = true;
    private int m_debugType = LogType.ALL;
    private int m_debugLevel = 7;
    private static JTextArea m_textareaConsole;
    private static JAPDialog m_frameConsole;
    private static boolean m_bConsole;
    private static volatile boolean ms_bFile;
    private static String ms_strFileName;
    private static FileLog ms_FileLog;
    private static JAPDebug debug;
    private static SimpleDateFormat dateFormatter;
    private WindowAdapter m_winAdapter;
    static /* synthetic */ Class class$java$lang$Runtime;

    private JAPDebug() {
        m_bConsole = false;
        ms_bFile = false;
        ms_strFileName = null;
        this.m_winAdapter = new WindowAdapter(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void windowClosing(WindowEvent windowEvent) {
                JAPDebug jAPDebug = JAPDebug.this;
                synchronized (jAPDebug) {
                    m_bConsole = false;
                    JAPDebug.this.setChanged();
                    JAPDebug.this.notifyObservers();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void windowClosed(WindowEvent windowEvent) {
                JAPDebug jAPDebug = JAPDebug.this;
                synchronized (jAPDebug) {
                    m_bConsole = false;
                    JAPDebug.this.setChanged();
                    JAPDebug.this.notifyObservers();
                }
            }
        };
    }

    public void finalize() {
        ms_bFile = false;
        try {
            super.finalize();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static JAPDebug getInstance() {
        if (debug == null) {
            debug = new JAPDebug();
        }
        return debug;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void log(int n, int n2, String string) {
        block9: {
            try {
                if (n > this.m_debugLevel || (this.m_debugType & n2) == 0) break block9;
                JAPDebug jAPDebug = this;
                synchronized (jAPDebug) {
                    String string2 = "[" + dateFormatter.format(new Date()) + LogLevel.getLevelName(n) + "] " + string + "\n";
                    if (!m_bConsole) {
                        if (ms_bSystemErrorAllowed) {
                            System.err.print(string2);
                        }
                    } else {
                        m_textareaConsole.append(string2);
                        m_textareaConsole.setCaretPosition(m_textareaConsole.getText().length());
                    }
                    if (ms_bFile) {
                        ms_FileLog.log(n, n2, string);
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public void setLogType(int n) {
        this.m_debugType = n;
        if (ms_bFile) {
            ms_FileLog.setLogType(n);
        }
    }

    public int getLogType() {
        return this.m_debugType;
    }

    public void setLogLevel(int n) {
        if (n < 0 || n > 7) {
            return;
        }
        this.m_debugLevel = n;
        if (ms_bFile) {
            ms_FileLog.setLogLevel(n);
        }
    }

    public int getLogLevel() {
        if (debug == null) {
            JAPDebug.getInstance();
        }
        return JAPDebug.debug.m_debugLevel;
    }

    public static void showConsole(boolean bl, Component component) {
        debug.internal_showConsole(bl, component);
    }

    public static void setLogToFile(String string) throws IOException {
        if (string == null || string.trim().equals("")) {
            ms_bFile = false;
            ms_FileLog = null;
        } else {
            ms_FileLog = new FileLog(string, 10000000, 2);
            ms_FileLog.setLogLevel(JAPDebug.getInstance().m_debugLevel);
            ms_FileLog.setLogType(JAPDebug.getInstance().m_debugType);
            ms_bFile = true;
        }
        ms_strFileName = string;
    }

    public static boolean isShowConsole() {
        return m_bConsole;
    }

    public static boolean isLogToFile() {
        return ms_bFile;
    }

    public static String getLogFilename() {
        return ms_strFileName;
    }

    public void internal_showConsole(boolean bl, Component component) {
        if (!bl && m_bConsole) {
            m_frameConsole.dispose();
            m_frameConsole.removeWindowListener(this.m_winAdapter);
            m_textareaConsole = null;
            m_frameConsole = null;
            m_bConsole = false;
        } else if (bl && !m_bConsole) {
            m_frameConsole = new JAPDialog(component, "Debug-Console", false);
            m_textareaConsole = new JTextArea(null, 20, 30);
            m_textareaConsole.setEditable(false);
            Font font = Font.decode("Courier");
            if (font != null) {
                m_textareaConsole.setFont(font);
            }
            JPanel jPanel = new JPanel();
            JButton jButton = new JButton(JAPMessages.getString("bttnSaveAs") + "...", GUIUtils.loadImageIcon("saveicon.gif", true));
            jButton.setActionCommand("saveas");
            jButton.addActionListener(debug);
            JButton jButton2 = new JButton(JAPMessages.getString("bttnCopy"), GUIUtils.loadImageIcon("copyicon.gif", true));
            jButton2.setActionCommand("copy");
            jButton2.addActionListener(debug);
            JButton jButton3 = new JButton(JAPMessages.getString("bttnInsertConfig"), GUIUtils.loadImageIcon("copyintoicon.gif", true));
            jButton3.setActionCommand("insertConfig");
            jButton3.addActionListener(debug);
            JButton jButton4 = new JButton(JAPMessages.getString("bttnDelete"), GUIUtils.loadImageIcon("deleteicon.gif", true));
            jButton4.setActionCommand("delete");
            jButton4.addActionListener(debug);
            JButton jButton5 = new JButton(JAPMessages.getString("bttnClose"), GUIUtils.loadImageIcon("exiticon.gif", true));
            jButton5.setActionCommand("close");
            jButton5.addActionListener(debug);
            GridBagLayout gridBagLayout = new GridBagLayout();
            jPanel.setLayout(gridBagLayout);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridx = 1;
            gridBagConstraints.weightx = 0.0;
            gridBagLayout.setConstraints(jButton, gridBagConstraints);
            jPanel.add(jButton);
            gridBagConstraints.gridx = 2;
            gridBagLayout.setConstraints(jButton2, gridBagConstraints);
            jPanel.add(jButton2);
            gridBagConstraints.gridx = 3;
            gridBagLayout.setConstraints(jButton3, gridBagConstraints);
            jPanel.add(jButton3);
            gridBagConstraints.gridx = 4;
            gridBagLayout.setConstraints(jButton4, gridBagConstraints);
            jPanel.add(jButton4);
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.anchor = 13;
            gridBagConstraints.fill = 0;
            gridBagConstraints.gridx = 5;
            gridBagLayout.setConstraints(jButton5, gridBagConstraints);
            jPanel.add(jButton5);
            m_frameConsole.getContentPane().add("North", jPanel);
            m_frameConsole.getContentPane().add("Center", new JScrollPane(m_textareaConsole));
            m_frameConsole.addWindowListener(this.m_winAdapter);
            m_frameConsole.pack();
            m_frameConsole.moveToUpRightCorner();
            m_frameConsole.setVisible(true);
            m_bConsole = true;
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("saveas")) {
            this.saveLog();
        } else if (actionEvent.getActionCommand().equals("copy")) {
            m_textareaConsole.selectAll();
            m_textareaConsole.copy();
            m_textareaConsole.moveCaretPosition(m_textareaConsole.getCaretPosition());
        } else if (actionEvent.getActionCommand().equals("delete")) {
            m_textareaConsole.setText("");
        } else if (actionEvent.getActionCommand().equals("insertConfig")) {
            Serializable serializable;
            try {
                serializable = System.getProperties();
                Enumeration<?> enumeration = ((Properties)serializable).propertyNames();
                while (enumeration.hasMoreElements()) {
                    String string = (String)enumeration.nextElement();
                    String string2 = ((Properties)serializable).getProperty(string);
                    m_textareaConsole.append(string + ": " + string2 + "\n");
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            m_textareaConsole.append("TotalMemory: " + Util.formatBytesValueWithUnit(Runtime.getRuntime().totalMemory()) + "\n");
            try {
                serializable = (Long)(class$java$lang$Runtime == null ? (class$java$lang$Runtime = JAPDebug.class$("java.lang.Runtime")) : class$java$lang$Runtime).getMethod("maxMemory", new Class[0]).invoke(Runtime.getRuntime(), new Object[0]);
                m_textareaConsole.append("MaxMemory: " + Util.formatBytesValueWithUnit((Long)serializable) + "\n");
            }
            catch (Exception exception) {
                // empty catch block
            }
            m_textareaConsole.append("FreeMemory: " + Util.formatBytesValueWithUnit(Runtime.getRuntime().freeMemory()) + "\n");
            m_textareaConsole.append("\n");
            m_textareaConsole.append(JAPModel.getInstance().toString());
        } else {
            m_frameConsole.dispose();
            m_bConsole = false;
        }
    }

    private void saveLog() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogType(1);
        int n = jFileChooser.showDialog(m_frameConsole.getRootPane(), null);
        if (n == 0) {
            File file = jFileChooser.getSelectedFile();
            try {
                FileWriter fileWriter = new FileWriter(file);
                m_textareaConsole.write(fileWriter);
                fileWriter.flush();
                fileWriter.close();
            }
            catch (Exception exception) {
                JAPDialog.showErrorDialog(m_frameConsole, JAPMessages.getString("errWritingLog"));
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
        m_bConsole = false;
        ms_bFile = false;
        ms_strFileName = null;
        ms_FileLog = null;
        dateFormatter = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss, ");
    }
}

