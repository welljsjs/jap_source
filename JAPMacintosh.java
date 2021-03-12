/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.apple.eawt.Application
 *  com.apple.eawt.ApplicationAdapter
 *  com.apple.eawt.ApplicationEvent
 *  com.apple.eawt.ApplicationListener
 */
import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;
import jap.JAPController;

public class JAPMacintosh
extends JAP {
    JAPMacintosh(String[] arrstring) {
        super(arrstring);
    }

    protected void registerMRJHandlers() {
        try {
            Application application = Application.getApplication();
            application.addApplicationListener((ApplicationListener)new AppListener());
            application.addPreferencesMenuItem();
            application.setEnabledAboutMenu(true);
            application.setEnabledPreferencesMenu(true);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void main(String[] arrstring) {
        JAPMacintosh jAPMacintosh = new JAPMacintosh(arrstring);
        jAPMacintosh.registerMRJHandlers();
        jAPMacintosh.startJAP();
    }

    class AppListener
    extends ApplicationAdapter {
        AppListener() {
        }

        public void handleAbout(ApplicationEvent applicationEvent) {
            JAPController.aboutJAP();
            applicationEvent.setHandled(true);
        }

        public void handleQuit(ApplicationEvent applicationEvent) {
            applicationEvent.setHandled(true);
            JAPController.goodBye(true);
        }

        public void handlePreferences(ApplicationEvent applicationEvent) {
            applicationEvent.setHandled(true);
            JAPController.getInstance().showConfigDialog();
        }
    }
}

