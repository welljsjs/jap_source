/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

import anon.terms.TCComponent;
import java.util.Vector;

public class TCComposite
extends TCComponent {
    protected Vector tcComponents = new Vector();

    public TCComposite() {
    }

    public TCComposite(double d, Object object) {
        super(d, object);
    }

    public void addTCComponent(TCComponent tCComponent) {
        TCComponent tCComponent2 = null;
        for (int i = 0; i < this.tcComponents.size(); ++i) {
            tCComponent2 = (TCComponent)this.tcComponents.elementAt(i);
            if (tCComponent2.getId() == tCComponent.getId()) {
                this.tcComponents.removeElementAt(i);
                this.tcComponents.insertElementAt(tCComponent, i);
                return;
            }
            if (!(tCComponent2.getId() > tCComponent.getId())) continue;
            this.tcComponents.insertElementAt(tCComponent, i);
            return;
        }
        this.tcComponents.addElement(tCComponent);
    }

    public void removeTCComponent(double d) {
        TCComponent tCComponent = null;
        for (int i = 0; i < this.tcComponents.size(); ++i) {
            tCComponent = (TCComponent)this.tcComponents.elementAt(i);
            if (tCComponent.getId() != d) continue;
            this.tcComponents.removeElementAt(i);
        }
    }

    public int getTCComponentCount() {
        return this.tcComponents.size();
    }

    public TCComponent[] getTCComponents() {
        TCComponent[] arrtCComponent = new TCComponent[this.tcComponents.size()];
        for (int i = 0; i < this.tcComponents.size(); ++i) {
            arrtCComponent[i] = (TCComponent)this.tcComponents.elementAt(i);
        }
        return arrtCComponent;
    }

    public TCComponent getTCComponent(double d) {
        TCComponent tCComponent = null;
        for (int i = 0; i < this.tcComponents.size(); ++i) {
            tCComponent = (TCComponent)this.tcComponents.elementAt(i);
            if (tCComponent.getId() != d) continue;
            return tCComponent;
        }
        return null;
    }

    public boolean hasContent() {
        return super.hasContent() || this.getTCComponentCount() > 0;
    }

    public String toString() {
        return this.getClass() + "@" + this.id + ": " + this.tcComponents.toString();
    }

    public Object clone() {
        TCComposite tCComposite = null;
        try {
            tCComposite = (TCComposite)this.getClass().newInstance();
        }
        catch (InstantiationException instantiationException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        if (tCComposite != null) {
            tCComposite.id = this.id;
            tCComposite.content = this.content;
            TCComponent[] arrtCComponent = this.getTCComponents();
            for (int i = 0; i < arrtCComponent.length; ++i) {
                tCComposite.tcComponents.addElement(arrtCComponent[i].clone());
            }
        }
        return tCComposite;
    }
}

