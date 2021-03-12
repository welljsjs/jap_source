/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.IMiscPasswordReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public abstract class AbstractMemorizingPasswordReader
implements IMiscPasswordReader {
    private Vector pwMatches = new Vector();
    private Enumeration currentPWIteration = null;
    private Object lastAccount = null;
    private boolean skipAll = false;
    private Hashtable m_completedObjects = new Hashtable();

    public final synchronized void reset() {
        this.m_completedObjects.clear();
    }

    public final synchronized int countCmpletedObjects() {
        return this.m_completedObjects.size();
    }

    public final synchronized char[] getPassword() {
        return ((String)this.m_completedObjects.elements().nextElement()).toCharArray();
    }

    protected abstract void initPasswordDialog(Object var1);

    protected abstract String readPassword();

    protected abstract boolean askForCancel();

    public final synchronized String readPassword(Object object) {
        String string;
        block9: {
            block8: {
                string = null;
                if (this.skipAll) {
                    return null;
                }
                if (this.lastAccount == null) {
                    this.lastAccount = object;
                }
                if (!object.equals(this.lastAccount)) {
                    this.pwMatches.addElement(this.m_completedObjects.get(this.lastAccount));
                    this.currentPWIteration = null;
                    this.currentPWIteration = this.pwMatches.elements();
                    this.lastAccount = object;
                }
                if (this.currentPWIteration != null) {
                    if (this.currentPWIteration.hasMoreElements()) {
                        return (String)this.currentPWIteration.nextElement();
                    }
                    this.currentPWIteration = null;
                }
                this.initPasswordDialog(object);
                do {
                    try {
                        string = this.readPassword();
                    }
                    catch (Exception exception) {
                        LogHolder.log(1, LogType.MISC, "Could not read password!", exception);
                        return null;
                    }
                    if (string != null) break block8;
                    this.m_completedObjects.remove(object);
                } while (!this.askForCancel());
                this.skipAll = true;
                break block9;
            }
            this.m_completedObjects.put(object, string);
        }
        return string;
    }
}

