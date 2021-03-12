/*
 * Decompiled with CFR 0.150.
 */
package gui.help;

import anon.util.JAPMessages;
import anon.util.LanguageMapper;
import gui.help.JAPHelp;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Observable;

public abstract class AbstractHelpFileStorageManager {
    public static final String HELP_INVALID_NULL = "invalidHelpPathNull";
    public static final String HELP_INVALID_PATH_NOT_EXISTS = "invalidHelpPathNotExists";
    public static final String HELP_INVALID_NOWRITE = "invalidHelpPathNoWrite";
    public static final String HELP_INVALID_NOREAD = "invalidHelpPathNoRead";
    public static final String HELP_NO_DIR = "helpNoDir";
    public static final String HELP_DIR_EXISTS = "helpDirExists";
    public static final String HELP_JONDO_EXISTS = "helpJonDoExists";
    public static final String HELP_NESTED = "helpNested";
    public static final String HELP_VIRTUAL = "helpVirtual";
    public static final String HELP_VALID = "HELP_IS_VALID";
    private Hashtable m_hashLocalisedHelpDirs = new Hashtable();
    public static final String HELP_FOLDER = "help";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final String getLocalisedHelpDir() {
        Hashtable hashtable = this.m_hashLocalisedHelpDirs;
        synchronized (hashtable) {
            String string;
            if (this.m_hashLocalisedHelpDirs.size() == 0) {
                int n = 1;
                while (true) {
                    try {
                        String string2 = JAPMessages.getString(JAPHelp.MSG_LANGUAGE_CODE + String.valueOf(n));
                        LanguageMapper languageMapper = new LanguageMapper(string2, new Locale(string2, ""));
                        this.m_hashLocalisedHelpDirs.put(languageMapper.getISOCode(), "help/" + languageMapper.getISOCode() + "/" + HELP_FOLDER);
                    }
                    catch (Exception exception) {
                        break;
                    }
                    ++n;
                }
            }
            if ((string = (String)this.m_hashLocalisedHelpDirs.get(JAPMessages.getLocale().getLanguage())) == null) {
                string = (String)this.m_hashLocalisedHelpDirs.get(new LanguageMapper("EN").getISOCode());
            }
            return string;
        }
    }

    public boolean extractHelpFiles(String string) {
        return false;
    }

    public abstract boolean handleHelpPathChanged(String var1, String var2, boolean var3);

    public abstract String helpPathValidityCheck(String var1, boolean var2);

    public abstract Observable getStorageObservable();

    public abstract boolean ensureMostRecentVersion(String var1);

    public abstract boolean helpInstallationExists(String var1);

    public String getInitPath() {
        return null;
    }
}

