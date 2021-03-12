/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Request;
import HTTPClient.Response;
import java.io.IOException;

class RetryException
extends IOException {
    Request request = null;
    Response response = null;
    RetryException first = null;
    RetryException next = null;
    IOException exception = null;
    boolean conn_reset = true;
    boolean restart = false;

    public RetryException() {
    }

    public RetryException(String string) {
        super(string);
    }

    void addToListAfter(RetryException retryException) {
        if (retryException == null) {
            return;
        }
        if (retryException.next != null) {
            this.next = retryException.next;
        }
        retryException.next = this;
    }
}

