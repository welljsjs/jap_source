/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j.helpers;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

public class AppenderAttachableImpl
implements AppenderAttachable {
    protected Vector appenderList;

    public void addAppender(Appender appender) {
        if (appender == null) {
            return;
        }
        if (this.appenderList == null) {
            this.appenderList = new Vector(1);
        }
        if (!this.appenderList.contains(appender)) {
            this.appenderList.addElement(appender);
        }
    }

    public int appendLoopOnAppenders(LoggingEvent loggingEvent) {
        int n = 0;
        if (this.appenderList != null) {
            n = this.appenderList.size();
            for (int i = 0; i < n; ++i) {
                Appender appender = (Appender)this.appenderList.elementAt(i);
                appender.doAppend(loggingEvent);
            }
        }
        return n;
    }

    public Enumeration getAllAppenders() {
        if (this.appenderList == null) {
            return null;
        }
        return this.appenderList.elements();
    }

    public Appender getAppender(String string) {
        if (this.appenderList == null || string == null) {
            return null;
        }
        int n = this.appenderList.size();
        for (int i = 0; i < n; ++i) {
            Appender appender = (Appender)this.appenderList.elementAt(i);
            if (!string.equals(appender.getName())) continue;
            return appender;
        }
        return null;
    }

    public boolean isAttached(Appender appender) {
        if (this.appenderList == null || appender == null) {
            return false;
        }
        int n = this.appenderList.size();
        for (int i = 0; i < n; ++i) {
            Appender appender2 = (Appender)this.appenderList.elementAt(i);
            if (appender2 != appender) continue;
            return true;
        }
        return false;
    }

    public void removeAllAppenders() {
        if (this.appenderList != null) {
            int n = this.appenderList.size();
            for (int i = 0; i < n; ++i) {
                Appender appender = (Appender)this.appenderList.elementAt(i);
                appender.close();
            }
            this.appenderList.removeAllElements();
            this.appenderList = null;
        }
    }

    public void removeAppender(Appender appender) {
        if (appender == null || this.appenderList == null) {
            return;
        }
        this.appenderList.removeElement(appender);
    }

    public void removeAppender(String string) {
        if (string == null || this.appenderList == null) {
            return;
        }
        int n = this.appenderList.size();
        for (int i = 0; i < n; ++i) {
            if (!string.equals(((Appender)this.appenderList.elementAt(i)).getName())) continue;
            this.appenderList.removeElementAt(i);
            break;
        }
    }
}

