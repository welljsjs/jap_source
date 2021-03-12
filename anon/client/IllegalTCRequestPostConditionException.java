/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

public class IllegalTCRequestPostConditionException
extends Exception {
    private StringBuffer errorMessages = new StringBuffer();
    private int errorMessageNrs = 0;

    public void addErrorMessage(String string) {
        this.errorMessages.append("\n");
        this.errorMessages.append(++this.errorMessageNrs);
        this.errorMessages.append(". ");
        this.errorMessages.append(string);
    }

    public boolean hasErrorMessages() {
        return this.errorMessageNrs > 0;
    }

    public String getMessage() {
        return this.hasErrorMessages() ? this.errorMessages.toString() : null;
    }
}

