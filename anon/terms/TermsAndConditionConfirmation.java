/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

import java.util.Vector;

public interface TermsAndConditionConfirmation {
    public boolean confirmTermsAndConditions(Vector var1, Vector var2);

    public static final class AlwaysAccept
    implements TermsAndConditionConfirmation {
        public boolean confirmTermsAndConditions(Vector vector, Vector vector2) {
            return true;
        }
    }
}

