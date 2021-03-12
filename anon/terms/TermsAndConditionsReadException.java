/*
 * Decompiled with CFR 0.150.
 */
package anon.terms;

import anon.infoservice.ServiceOperator;
import anon.terms.TermsAndConditions;
import java.util.Vector;

public class TermsAndConditionsReadException
extends Exception {
    Vector tcsTosShow = new Vector();

    public void addTermsAndConditonsToRead(TermsAndConditions termsAndConditions) {
        this.tcsTosShow.addElement(termsAndConditions);
    }

    public Vector getTermsTermsAndConditonsToRead() {
        return (Vector)this.tcsTosShow.clone();
    }

    public Vector getOperators() {
        Vector<ServiceOperator> vector = new Vector<ServiceOperator>();
        ServiceOperator serviceOperator = null;
        for (int i = 0; i < this.tcsTosShow.size(); ++i) {
            serviceOperator = ((TermsAndConditions)this.tcsTosShow.elementAt(i)).getOperator();
            if (vector.contains(serviceOperator)) continue;
            vector.addElement(serviceOperator);
        }
        return vector;
    }
}

