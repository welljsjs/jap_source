/*
 * Decompiled with CFR 0.150.
 */
package jap;

import anon.infoservice.Database;
import anon.infoservice.ServiceOperator;
import anon.terms.TermsAndConditions;
import anon.util.JAPMessages;
import jap.OperatorsCellRenderer;
import jap.TermsAndCondtionsTableController;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import logging.LogHolder;
import logging.LogType;

public class TermsAndConditionsOperatorTable
extends JTable
implements MouseListener {
    private static final long serialVersionUID = 1L;
    private static final int OPERATOR_COL = 0;
    private static final int DATE_COL = 1;
    private static final int ACCEPTED_COL = 2;
    private static final String OPERATOR_COL_NAMEKEY = "mixOperator";
    private static final String DATE_COL_NAMEKEY = "validFrom";
    private static final String ACCEPTED_COL_NAMEKEY = (class$jap$JAPConfTC == null ? (class$jap$JAPConfTC = TermsAndConditionsOperatorTable.class$("jap.JAPConfTC")) : class$jap$JAPConfTC).getName() + "_tncAccepted";
    private static final int COLS = 3;
    private TermsAndCondtionsTableController controller;
    static /* synthetic */ Class class$jap$JAPConfTC;
    static /* synthetic */ Class class$anon$infoservice$ServiceOperator;
    static /* synthetic */ Class class$java$util$Date;
    static /* synthetic */ Class class$java$lang$Boolean;

    public TermsAndConditionsOperatorTable() {
        this((Vector)null);
    }

    public TermsAndConditionsOperatorTable(Vector vector) {
        OperatorTableModel operatorTableModel = vector != null ? new OperatorTableModel(vector) : new OperatorTableModel();
        this.setModel(operatorTableModel);
        this.setDefaultRenderer(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = TermsAndConditionsOperatorTable.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator, new OperatorsCellRenderer());
        this.setSelectionMode(0);
        this.getColumnModel().getColumn(0).setMinWidth(200);
        this.getColumnModel().getColumn(0).setPreferredWidth(200);
        this.getColumnModel().getColumn(1).setMinWidth(100);
        this.getColumnModel().getColumn(1).setPreferredWidth(100);
        int n = this.getOperators().size();
        this.setPreferredSize(new Dimension(450, Math.min(10 + n * 12, 100)));
        this.addMouseListener(this);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (this.controller != null) {
            ServiceOperator serviceOperator = (ServiceOperator)this.getModel().getValueAt(this.getSelectedRow(), 0);
            boolean bl = (Boolean)this.getModel().getValueAt(this.getSelectedRow(), 2);
            if ((this.getSelectedColumn() == 0 || this.getSelectedColumn() == 1) && mouseEvent.getClickCount() > 1) {
                this.getModel().setValueAt(new Boolean(this.controller.handleOperatorAction(serviceOperator, bl)), this.getSelectedRow(), 2);
            } else if (this.getSelectedColumn() != 2) {
                this.controller.handleSelectLineAction(serviceOperator);
            }
            this.repaint();
        }
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public TermsAndCondtionsTableController getController() {
        return this.controller;
    }

    public void setController(TermsAndCondtionsTableController termsAndCondtionsTableController) {
        this.controller = termsAndCondtionsTableController;
    }

    public void setOperators(Vector vector) {
        this.checkModel();
        ((OperatorTableModel)this.getModel()).setOperators(vector);
        this.repaint();
    }

    public Vector getOperators() {
        this.checkModel();
        return ((OperatorTableModel)this.getModel()).getOperators();
    }

    public Vector getTermsAccepted() {
        this.checkModel();
        return ((OperatorTableModel)this.getModel()).getTermsAccepted();
    }

    public Vector getTermsRejected() {
        this.checkModel();
        return ((OperatorTableModel)this.getModel()).getTermsRejected();
    }

    public boolean areTermsRejected() {
        this.checkModel();
        return ((OperatorTableModel)this.getModel()).areTermsRejected();
    }

    private void checkModel() {
        if (this.getModel() == null) {
            throw new IllegalStateException("Current model is null");
        }
        if (!(this.getModel() instanceof OperatorTableModel)) {
            throw new IllegalStateException("Wrong model set " + this.getModel().getClass());
        }
    }

    public void setAccepted(int n, boolean bl) {
        this.setValueAt(new Boolean(bl), n, 2);
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private class OperatorTableModel
    extends AbstractTableModel {
        private Vector m_vecOperators = new Vector();
        private String[] columnNames;
        private Class[] columnClasses;
        private BitSet accepted = new BitSet();

        public OperatorTableModel(Vector vector) {
            this.columnClasses = new Class[3];
            this.columnNames = new String[3];
            this.columnClasses[0] = class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = TermsAndConditionsOperatorTable.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator;
            this.columnClasses[1] = class$java$util$Date == null ? (class$java$util$Date = TermsAndConditionsOperatorTable.class$("java.util.Date")) : class$java$util$Date;
            this.columnClasses[2] = class$java$lang$Boolean == null ? (class$java$lang$Boolean = TermsAndConditionsOperatorTable.class$("java.lang.Boolean")) : class$java$lang$Boolean;
            this.columnNames[0] = JAPMessages.getString(TermsAndConditionsOperatorTable.OPERATOR_COL_NAMEKEY);
            this.columnNames[1] = JAPMessages.getString(TermsAndConditionsOperatorTable.DATE_COL_NAMEKEY);
            this.columnNames[2] = JAPMessages.getString(ACCEPTED_COL_NAMEKEY);
            this.setOperators(vector);
        }

        public OperatorTableModel() {
            this(Database.getInstance(class$anon$infoservice$ServiceOperator == null ? (class$anon$infoservice$ServiceOperator = TermsAndConditionsOperatorTable.class$("anon.infoservice.ServiceOperator")) : class$anon$infoservice$ServiceOperator).getEntryList());
        }

        public int getRowCount() {
            return this.m_vecOperators.size();
        }

        public int getColumnCount() {
            return this.columnNames.length;
        }

        public boolean isCellEditable(int n, int n2) {
            return n2 == 2 || n2 == 0;
        }

        public Class getColumnClass(int n) {
            return this.columnClasses[n];
        }

        public String getColumnName(int n) {
            return this.columnNames[n];
        }

        public Object getValueAt(int n, int n2) {
            try {
                switch (n2) {
                    case 0: {
                        return (ServiceOperator)this.m_vecOperators.elementAt(n);
                    }
                    case 1: {
                        ServiceOperator serviceOperator = (ServiceOperator)this.m_vecOperators.elementAt(n);
                        TermsAndConditions termsAndConditions = TermsAndConditions.getTermsAndConditions(serviceOperator);
                        return termsAndConditions != null ? termsAndConditions.getDate() : null;
                    }
                    case 2: {
                        return new Boolean(this.accepted.get(n));
                    }
                }
                throw new IndexOutOfBoundsException("No definition for column " + n2);
            }
            catch (Exception exception) {
                LogHolder.log(3, LogType.GUI, exception);
                return null;
            }
        }

        public void setValueAt(Object object, int n, int n2) {
            switch (n2) {
                case 0: {
                    break;
                }
                case 1: {
                    break;
                }
                case 2: {
                    boolean bl = (Boolean)object;
                    this.setAccepted(n, bl);
                    if (TermsAndConditionsOperatorTable.this.controller == null) break;
                    try {
                        TermsAndConditionsOperatorTable.this.controller.handleAcceptAction((ServiceOperator)this.getValueAt(n, 0), bl);
                    }
                    catch (IllegalStateException illegalStateException) {
                        this.setAccepted(n, !bl);
                    }
                    break;
                }
                default: {
                    throw new IndexOutOfBoundsException("No definition for column " + n2);
                }
            }
        }

        public void setOperators(Vector vector) {
            int n;
            Object e = null;
            this.m_vecOperators.removeAllElements();
            for (n = 0; n < this.accepted.size(); ++n) {
                this.accepted.clear(n);
            }
            if (vector != null) {
                n = 0;
                for (int i = 0; i < vector.size(); ++i) {
                    e = vector.elementAt(i);
                    if (!(e instanceof ServiceOperator) || !((ServiceOperator)e).hasTermsAndConditions()) continue;
                    this.m_vecOperators.addElement(e);
                    if (TermsAndConditions.getTermsAndConditions((ServiceOperator)e).isAccepted()) {
                        this.accepted.set(n);
                    }
                    ++n;
                }
            }
        }

        public Vector getOperators() {
            return this.m_vecOperators;
        }

        public ServiceOperator getOperator(int n) {
            return (ServiceOperator)this.getValueAt(n, 0);
        }

        public Vector getTermsAccepted() {
            return this.getTermsWithAcceptStatus(true);
        }

        public Vector getTermsRejected() {
            return this.getTermsWithAcceptStatus(false);
        }

        public boolean areTermsRejected() {
            for (int i = 0; i < this.m_vecOperators.size(); ++i) {
                if (this.accepted.get(i)) continue;
                return true;
            }
            return false;
        }

        public void setAccepted(int n, boolean bl) {
            if (bl) {
                this.accepted.set(n);
            } else {
                this.accepted.clear(n);
            }
        }

        private Vector getTermsWithAcceptStatus(boolean bl) {
            Vector<TermsAndConditions> vector = new Vector<TermsAndConditions>();
            ServiceOperator serviceOperator = null;
            for (int i = 0; i < this.m_vecOperators.size(); ++i) {
                serviceOperator = (ServiceOperator)this.m_vecOperators.elementAt(i);
                if (!serviceOperator.hasTermsAndConditions() || this.accepted.get(i) != bl) continue;
                vector.addElement(TermsAndConditions.getTermsAndConditions(serviceOperator));
            }
            return vector;
        }
    }
}

