/*
 * Decompiled with CFR 0.150.
 */
package jap;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.DefaultListModel;

public class JAPInfoServiceListModel
extends DefaultListModel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setData(Vector vector) {
        JAPInfoServiceListModel jAPInfoServiceListModel = this;
        synchronized (jAPInfoServiceListModel) {
            int n = this.size();
            this.removeAllElements();
            if (n > 0) {
                this.fireIntervalRemoved(this, 0, n - 1);
            }
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                this.addElement(enumeration.nextElement());
            }
            if (this.size() > 0) {
                this.fireIntervalAdded(this, 0, this.size() - 1);
            }
        }
    }
}

