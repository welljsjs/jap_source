/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

public abstract class TCComponent {
    protected double id = -1.0;
    protected Object content = null;

    public TCComponent() {
    }

    public TCComponent(double d) {
        this.id = d;
    }

    public TCComponent(double d, Object object) {
        this.id = d;
        this.content = object;
    }

    public double getId() {
        return this.id;
    }

    public void setId(double d) {
        this.id = d;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(Object object) {
        this.content = object;
    }

    public boolean hasContent() {
        return this.content != null;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TCComponent)) {
            return false;
        }
        return ((TCComponent)object).getId() == this.id && this.getClass().equals(object.getClass());
    }

    public abstract Object clone();

    public String toString() {
        return this.content != null ? this.getClass() + "@" + this.id + ": " + this.content.toString() : null;
    }
}

