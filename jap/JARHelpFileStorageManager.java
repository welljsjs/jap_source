/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.platform.AbstractOS;
import anon.util.ClassUtil;
import anon.util.RecursiveFileTool;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import anon.util.ZipArchiver;
import gui.help.AbstractHelpFileStorageManager;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.zip.ZipFile;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class JARHelpFileStorageManager
extends AbstractHelpFileStorageManager {
    public static final String HELP_VERSION_NODE = "jondohelp";
    public static final String HELP_VERSION_ATTRIBUTE = "version";
    public static final String HELP_VERSION_FILE = "jondohelp.xml";
    private String m_helpPath;
    private ZipArchiver m_archiver;

    public JARHelpFileStorageManager() {
        ZipFile zipFile = ClassUtil.getJarFile();
        if (zipFile == null) {
            // empty if block
        }
        this.m_archiver = new ZipArchiver(zipFile);
    }

    private void setHelpPath(String string) {
        this.m_helpPath = string;
    }

    public boolean helpVersionMismatch() {
        String string = this.getHelpVersion(this.m_helpPath + File.separator + "help");
        if (string == null) {
            return true;
        }
        return !"00.20.001".equals(string);
    }

    public boolean handleHelpPathChanged(String string, String string2, boolean bl) {
        boolean bl2 = true;
        this.setHelpPath(string2);
        if (string != null) {
            this.removeOldHelp(string, false);
        }
        if (string2 != null) {
            bl2 = this.installHelp(bl);
        }
        return bl2;
    }

    public String helpPathValidityCheck(String string, boolean bl) {
        if (string != null) {
            File file = new File(string);
            if (string.indexOf("JonDo") >= 0) {
                bl = true;
            }
            if (file.exists()) {
                if (file.isDirectory()) {
                    int n;
                    String string2 = string;
                    while ((n = string2.toLowerCase().lastIndexOf("help".toLowerCase())) >= 0) {
                        if (!new File(string2.substring(0, n + "help".length())).exists()) {
                            LogHolder.log(0, LogType.MISC, "Existing help directory was not found!");
                        }
                        if (this.getHelpVersion(string2.substring(0, n + "help".length())) != null) {
                            return "helpNested";
                        }
                        string2 = string2.substring(0, n);
                    }
                    string2 = AbstractOS.getInstance().getenv("ALLUSERSPROFILE");
                    if (string2 != null && file.getPath().indexOf(string2) >= 0) {
                        return "helpVirtual";
                    }
                    string2 = AbstractOS.getInstance().getenv("PROGRAMFILES");
                    if (string2 != null && file.getPath().indexOf(string2) >= 0) {
                        return "helpVirtual";
                    }
                    string2 = AbstractOS.getInstance().getenv("SYSTEMROOT");
                    if (string2 != null && file.getPath().indexOf(string2) >= 0) {
                        return "helpVirtual";
                    }
                    string2 = AbstractOS.getInstance().getenv("PROGRAMDATA");
                    if (string2 != null && file.getPath().indexOf(string2) >= 0) {
                        return "helpVirtual";
                    }
                    File file2 = new File(file.getPath() + File.separator + "help" + File.separator);
                    if (!file2.exists()) {
                        try {
                            if (!file2.mkdir()) {
                                return "invalidHelpPathNoWrite";
                            }
                            file2.delete();
                        }
                        catch (SecurityException securityException) {
                            return "invalidHelpPathNoWrite";
                        }
                        return "HELP_IS_VALID";
                    }
                    File file3 = new File(file2.getPath() + File.separator + HELP_VERSION_FILE);
                    if (bl || file3.exists()) {
                        try {
                            if (!file2.canWrite()) {
                                return "invalidHelpPathNoWrite";
                            }
                        }
                        catch (SecurityException securityException) {
                            LogHolder.log(2, LogType.MISC, securityException);
                            return "invalidHelpPathNoWrite";
                        }
                        try {
                            if (!file2.canRead() || file2.list() == null) {
                                return "invalidHelpPathNoRead";
                            }
                        }
                        catch (SecurityException securityException) {
                            LogHolder.log(2, LogType.MISC, securityException);
                            return "invalidHelpPathNoRead";
                        }
                        return "helpJonDoExists";
                    }
                    LogHolder.log(4, LogType.GUI, "Found help directory without this version file: " + file3);
                    return "helpDirExists";
                }
                return "helpNoDir";
            }
            return "invalidHelpPathNotExists";
        }
        return "invalidHelpPathNull";
    }

    public Observable getStorageObservable() {
        return this.m_archiver;
    }

    public boolean extractHelpFiles(String string) {
        return this.extractHelpFiles(string, true);
    }

    private boolean extractHelpFiles(String string, boolean bl) {
        if (string == null) {
            LogHolder.log(3, LogType.MISC, "Invalid directory for help extraction: " + string);
            return false;
        }
        boolean bl2 = this.m_archiver.extractArchive("help/", string);
        if (bl2) {
            JARHelpFileStorageManager.createHelpVersionDoc(string);
            return true;
        }
        LogHolder.log(3, LogType.MISC, "Extracting help files was not succesful.");
        return false;
    }

    private boolean installHelp(boolean bl) {
        File file = this.getHelpFolder();
        if (file == null) {
            LogHolder.log(0, LogType.MISC, "Destination folder is null: Aborting help installation");
            return false;
        }
        if (this.m_archiver == null) {
            LogHolder.log(0, LogType.MISC, "JARStorageManager does only work when started from a Jar file");
            return false;
        }
        if (file.exists()) {
            if (this.helpVersionMismatch()) {
                this.removeOldHelp(this.m_helpPath, bl);
                if (file.exists()) {
                    LogHolder.log(0, LogType.MISC, "Could not delete old help directory!");
                    return false;
                }
            } else {
                LogHolder.log(5, LogType.MISC, "Previous help installation restored.");
                return true;
            }
        }
        return this.extractHelpFiles(this.m_helpPath, false);
    }

    private static void createHelpVersionDoc(String string) {
        Document document = XMLUtil.createDocument();
        Element element = document.createElement(HELP_VERSION_NODE);
        XMLUtil.setAttribute(element, HELP_VERSION_ATTRIBUTE, "00.20.001");
        document.appendChild(element);
        File file = new File(string + File.separator + "help" + File.separator + HELP_VERSION_FILE);
        try {
            XMLUtil.write(document, file);
        }
        catch (IOException iOException) {
            LogHolder.log(4, LogType.MISC, "Could not write help version due to an I/O error: ", iOException);
        }
    }

    private boolean removeOldHelp(String string, boolean bl) {
        if (string == null) {
            return true;
        }
        File file = new File(string + File.separator + "help" + File.separator);
        File file2 = new File(string + File.separator + "help" + File.separator + HELP_VERSION_FILE);
        try {
            if (!file.exists() || !bl && !file2.exists() && file.list().length > 0) {
                LogHolder.log(6, LogType.MISC, "No old help found in " + file.getPath());
                return true;
            }
        }
        catch (SecurityException securityException) {
            LogHolder.log(6, LogType.MISC, "No old help found in " + file.getPath(), securityException);
            return false;
        }
        if (!RecursiveFileTool.deleteRecursion(file)) {
            LogHolder.log(4, LogType.MISC, "Failed to delete old help at first try - try again!");
            RecursiveFileTool.deleteRecursion(file);
        }
        if (!file.exists()) {
            LogHolder.log(7, LogType.MISC, "removed old help from " + string);
            return true;
        }
        return false;
    }

    private boolean isHelpInstalled() {
        File file = this.getHelpFolder();
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return true;
        }
        LogHolder.log(4, LogType.GUI, "Checked for help folder " + file + " but it did not exist");
        return false;
    }

    private String getHelpVersion(String string) {
        try {
            File file = new File(string + File.separator + HELP_VERSION_FILE);
            if (!file.exists()) {
                return null;
            }
            Document document = XMLUtil.readXMLDocument(file);
            Node node = XMLUtil.getFirstChildByName(document, HELP_VERSION_NODE);
            String string2 = XMLUtil.parseAttribute(node, HELP_VERSION_ATTRIBUTE, null);
            return string2;
        }
        catch (IOException iOException) {
            LogHolder.log(3, LogType.MISC, "Error: an I/O error occured while parsing help version file: ", iOException);
        }
        catch (XMLParseException xMLParseException) {
            LogHolder.log(3, LogType.MISC, "Error: help version file cannot be parsed: ", xMLParseException);
        }
        return null;
    }

    private File getHelpFolder() {
        if (this.m_helpPath == null) {
            return null;
        }
        return new File(this.m_helpPath + File.separator + "help" + File.separator);
    }

    public boolean ensureMostRecentVersion(String string) {
        this.setHelpPath(string);
        if (this.helpVersionMismatch() || !this.isHelpInstalled()) {
            if (this.m_helpPath != null && this.m_helpPath.indexOf("JonDo") >= 0) {
                return this.installHelp(true);
            }
            return this.installHelp(false);
        }
        return true;
    }

    public boolean helpInstallationExists(String string) {
        this.setHelpPath(string);
        return this.isHelpInstalled();
    }
}

