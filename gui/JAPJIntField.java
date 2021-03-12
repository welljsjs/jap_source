/*
 * Decompiled with CFR 0.150.
 */
package gui;

import anon.util.ClassUtil;
import anon.util.JAPMessages;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public final class JAPJIntField
extends JTextField {
    public static final int NO_MAXIMUM_BOUND = -1;
    public static final int ALLOW_ZEROS_NONE = 0;
    public static final int ALLOW_ZEROS_ONE = 1;
    public static final int ALLOW_ZEROS_UNTIL_BOUND = 2;
    private static final String MSG_NO_VALID_INTEGER = (class$gui$JAPJIntField == null ? (class$gui$JAPJIntField = JAPJIntField.class$("gui.JAPJIntField")) : class$gui$JAPJIntField).getName() + "_noValidInteger";
    private IIntFieldBounds m_bounds;
    private boolean b_bAutoTransferFocus;
    static /* synthetic */ Class class$gui$JAPJIntField;

    public JAPJIntField() {
        this(-1, false);
    }

    public JAPJIntField(int n) {
        this(n, false);
    }

    public JAPJIntField(int n, boolean bl) {
        this(new DefaultBounds(n), bl);
    }

    public JAPJIntField(IIntFieldBounds iIntFieldBounds) {
        this(iIntFieldBounds, false);
    }

    public JAPJIntField(IIntFieldBounds iIntFieldBounds, boolean bl) {
        super(JAPJIntField.parseNumberOfDigits(iIntFieldBounds.getMaximum()));
        if (iIntFieldBounds.getAllowZeros() < 0 || iIntFieldBounds.getAllowZeros() > 2) {
            throw new IllegalArgumentException("getAllowZeros() returned an illegal value: " + iIntFieldBounds.getAllowZeros());
        }
        this.m_bounds = iIntFieldBounds;
        this.b_bAutoTransferFocus = bl;
    }

    public void setInt(int n) {
        this.setText(Integer.toString(n));
    }

    public int getInt() throws NumberFormatException {
        Object[] arrobject = new Object[2];
        arrobject[1] = this.getName() == null || this.getName().trim().length() == 0 ? ClassUtil.getShortClassName(this.getClass()) : this.getName();
        try {
            int n = Integer.parseInt(this.getText());
            if (!(n < 0 || this.m_bounds.getAllowZeros() == 0 && n == 0 || this.m_bounds.getMaximum() >= 0 && n > this.m_bounds.getMaximum())) {
                return n;
            }
            arrobject[0] = new Integer(n);
        }
        catch (NumberFormatException numberFormatException) {
            arrobject[0] = this.getText();
        }
        throw new NumberFormatException(JAPMessages.getString(MSG_NO_VALID_INTEGER, arrobject));
    }

    public void updateBounds() {
        try {
            if (this.getInt() > this.m_bounds.getMaximum()) {
                this.setInt(this.m_bounds.getMaximum());
            }
            if (this.m_bounds.getAllowZeros() == 0 && this.getInt() == 0) {
                this.setInt(1);
            }
        }
        catch (NumberFormatException numberFormatException) {
            if (this.m_bounds.getAllowZeros() > 0) {
                this.setInt(0);
            }
            this.setInt(1);
        }
    }

    protected final Document createDefaultModel() {
        return new IntDocument();
    }

    private static int parseNumberOfDigits(int n) {
        int n2 = 0;
        while (n > 0) {
            ++n2;
            n /= 10;
        }
        if (n2 == 0) {
            n2 = 1;
        }
        return n2;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private final class IntDocument
    extends PlainDocument {
        private IntDocument() {
        }

        public void insertString(int n, String string, AttributeSet attributeSet) throws BadLocationException {
            int n2;
            if (string == null || string.trim().length() == 0) {
                return;
            }
            try {
                n2 = Integer.parseInt(this.getText(0, this.getLength()) + string);
            }
            catch (NumberFormatException numberFormatException) {
                return;
            }
            if (JAPJIntField.this.m_bounds.getAllowZeros() < 2 ? n2 < 10 && n > 0 : JAPJIntField.this.m_bounds.getMaximum() >= 0 && JAPJIntField.parseNumberOfDigits(JAPJIntField.this.m_bounds.getMaximum()) < n + 1) {
                return;
            }
            if ((JAPJIntField.this.m_bounds.getMaximum() < 0 || n2 <= JAPJIntField.this.m_bounds.getMaximum()) && (JAPJIntField.this.m_bounds.getAllowZeros() > 0 || JAPJIntField.this.m_bounds.getAllowZeros() == 0 && n2 > 0)) {
                super.insertString(n, string, attributeSet);
            }
            if (JAPJIntField.this.m_bounds.getMaximum() >= 0 && JAPJIntField.this.b_bAutoTransferFocus && this.getLength() > 0 && n + 1 == JAPJIntField.parseNumberOfDigits(JAPJIntField.this.m_bounds.getMaximum())) {
                JAPJIntField.this.transferFocus();
            }
        }
    }

    private static final class DefaultBounds
    extends AbstractIntFieldBounds {
        public DefaultBounds(int n) {
            super(n);
        }

        public int getAllowZeros() {
            return 1;
        }
    }

    public static final class IntFieldWithoutZeroBounds
    extends AbstractIntFieldBounds {
        public IntFieldWithoutZeroBounds(int n) {
            super(n);
        }

        public int getAllowZeros() {
            return 0;
        }
    }

    public static final class IntFieldUnlimitedZerosBounds
    extends AbstractIntFieldBounds {
        public IntFieldUnlimitedZerosBounds(int n) {
            super(n);
        }

        public int getAllowZeros() {
            return 2;
        }
    }

    public static abstract class AbstractIntFieldBounds
    implements IIntFieldBounds {
        private int m_maxValue;

        public AbstractIntFieldBounds(int n) {
            this.m_maxValue = n;
        }

        public final int getMaximum() {
            return this.m_maxValue;
        }

        public abstract /* synthetic */ int getAllowZeros();
    }

    public static interface IIntFieldBounds {
        public int getAllowZeros();

        public int getMaximum();
    }
}

