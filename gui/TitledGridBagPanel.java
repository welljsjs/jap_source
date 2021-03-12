/*
 * Decompiled with CFR 0.150.
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public final class TitledGridBagPanel
extends JPanel {
    private GridBagConstraints m_constraints;
    private Vector m_rows;

    public TitledGridBagPanel() {
        this((String)null);
    }

    public TitledGridBagPanel(String string) {
        this(string, null);
    }

    public TitledGridBagPanel(String string, Insets insets) {
        super(new GridBagLayout());
        if (string != null) {
            this.setBorder(new TitledBorder(string));
        }
        this.m_constraints = new GridBagConstraints();
        this.m_constraints.anchor = 17;
        this.setInsets(insets);
        this.m_rows = new Vector();
    }

    public void setInsets(Insets insets) {
        if (insets == null) {
            insets = this.getDefaultInsets();
        }
        this.m_constraints.insets = insets;
    }

    public Insets getDefaultInsets() {
        return new Insets(5, 5, 5, 5);
    }

    public void removeInsets() {
        this.m_constraints.insets = new Insets(0, 0, 0, 0);
    }

    public void setEnabled(boolean bl) {
        if (this.getBorder() instanceof TitledBorder) {
            TitledBorder titledBorder = new TitledBorder(((TitledBorder)this.getBorder()).getTitle());
            if (!bl) {
                titledBorder.setTitleColor(Color.gray);
            }
            this.setBorder(titledBorder);
        }
        super.setEnabled(bl);
        Component[] arrcomponent = this.getComponents();
        for (int i = 0; i < arrcomponent.length; ++i) {
            arrcomponent[i].setEnabled(bl);
        }
    }

    public void setLayout(LayoutManager layoutManager) {
        if (!(layoutManager instanceof GridBagLayout)) {
            throw new IllegalStateException("Layout is fixed to GridBagLayout!");
        }
        super.setLayout(layoutManager);
    }

    public int addRow(Component component) {
        return this.addRow(component, null);
    }

    public int addRow(Component component, Component component2) {
        return this.addRow(component, component2, 2);
    }

    public int addRow(Component component, Component component2, int n) {
        return this.replaceRow(component, component2, this.getNextRow(), n);
    }

    public int addRow(Component component, Component component2, Component component3, Component component4) {
        return this.replaceRow(component, component2, component3, component4, this.getNextRow());
    }

    public int addRow(Component component, Component component2, Component component3, Component component4, int n) {
        return this.replaceRow(component, component2, component3, component4, this.getNextRow(), n);
    }

    public int addRow(Component component, Component component2, Component component3) {
        return this.replaceRow(component, component2, component3, this.getNextRow());
    }

    public int addDummyRow() {
        this.m_rows.addElement(new JLabel());
        return this.m_rows.size() - 1;
    }

    public void addDummyRows(int n) {
        while (n > 0) {
            this.m_rows.addElement(new JLabel());
            --n;
        }
    }

    public int addRow(Component[] arrcomponent, int[] arrn) {
        return this.replaceRow(arrcomponent, arrn, this.getNextRow());
    }

    public int replaceRow(Component component, Component component2, int n) {
        return this.replaceRow(component, component2, n, 2);
    }

    public int replaceRow(Component component, Component component2, int n, int n2) {
        Component[] arrcomponent = new Component[]{component, component2};
        return this.replaceRow(arrcomponent, null, n, n2);
    }

    public int replaceRow(Component component, Component component2, Component component3, int n) {
        Component[] arrcomponent = new Component[]{component, component2, component3};
        return this.replaceRow(arrcomponent, null, n);
    }

    public int replaceRow(Component component, Component component2, Component component3, Component component4, int n, int n2) {
        Component[] arrcomponent = new Component[]{component, component2, component3, component4};
        return this.replaceRow(arrcomponent, null, n, n2);
    }

    public int replaceRow(Component component, Component component2, Component component3, Component component4, int n) {
        return this.replaceRow(component, component2, component3, component4, n, 2);
    }

    public int getNextRow() {
        return this.m_rows.size();
    }

    public int replaceRow(Component[] arrcomponent, int[] arrn, int n) {
        return this.replaceRow(arrcomponent, arrn, n, 2);
    }

    public int replaceRow(Component[] arrcomponent, int[] arrn, int n, int n2) {
        if (arrcomponent != null && arrcomponent.length > 0) {
            int[] arrn2;
            int n3;
            Vector<Component> vector = new Vector<Component>();
            for (n3 = 0; n3 < arrcomponent.length; ++n3) {
                vector.addElement(arrcomponent[n3]);
            }
            while (this.m_rows.size() < n - 1) {
                this.m_rows.addElement(new Vector());
            }
            if (this.m_rows.size() > n) {
                Vector vector2 = (Vector)this.m_rows.elementAt(n);
                for (n3 = 0; n3 < vector2.size(); ++n3) {
                    this.remove((Component)vector2.elementAt(n3));
                }
                this.m_rows.removeElementAt(n);
            }
            this.m_rows.insertElementAt(vector, n);
            if (arrn != null) {
                arrn2 = arrn;
            } else {
                arrn2 = new int[arrcomponent.length];
                for (n3 = 0; n3 < arrn2.length; ++n3) {
                    arrn2[n3] = 1;
                    int n4 = n3;
                    while (n4 + 1 < arrcomponent.length && arrcomponent[n4 + 1] == null) {
                        ++n4;
                        int n5 = n3;
                        arrn2[n5] = arrn2[n5] + 1;
                    }
                }
            }
            for (n3 = 0; n3 < arrcomponent.length; ++n3) {
                if (arrcomponent[n3] == null) continue;
                this.m_constraints.gridx = n3;
                this.m_constraints.gridy = n;
                this.m_constraints.weightx = 1.0;
                this.m_constraints.gridwidth = arrn2[n3];
                this.m_constraints.weighty = n3 == arrcomponent.length - 1 ? 10.0 : 0.0;
                this.m_constraints.fill = n2;
                this.add(arrcomponent[n3], this.m_constraints);
            }
        }
        return n;
    }
}

