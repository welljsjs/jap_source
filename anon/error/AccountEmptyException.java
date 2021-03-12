/*
 * Decompiled with CFR 0.150.
 */
package anon.error;

import anon.error.AnonServiceException;
import anon.infoservice.MixCascade;
import anon.pay.PayAccount;

public class AccountEmptyException
extends AnonServiceException {
    private static final long serialVersionUID = 1L;
    private PayAccount m_account;

    public AccountEmptyException(MixCascade mixCascade) {
        this(mixCascade, null);
    }

    public AccountEmptyException(MixCascade mixCascade, PayAccount payAccount) {
        super(mixCascade, "Connection to service " + mixCascade + " was closed because " + (payAccount == null ? "no usable account is available." : "the account " + payAccount.getAccountNumber() + " is empty."), -32);
        this.m_account = payAccount;
    }

    public PayAccount getAccount() {
        return this.m_account;
    }
}

