/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.Request;
import HTTPClient.Util;
import java.util.Enumeration;
import java.util.Hashtable;

class IdempotentSequence {
    private static final int UNKNOWN = 0;
    private static final int HEAD = 1;
    private static final int GET = 2;
    private static final int POST = 3;
    private static final int PUT = 4;
    private static final int DELETE = 5;
    private static final int OPTIONS = 6;
    private static final int TRACE = 7;
    private int[] m_history;
    private String[] r_history;
    private int m_len = 0;
    private int r_len = 0;
    private boolean analysis_done = false;
    private Hashtable threads = new Hashtable();
    private static final Object INDET = new Object();

    public IdempotentSequence() {
        this.m_history = new int[10];
        this.r_history = new String[10];
    }

    public void add(Request request) {
        if (this.m_len >= this.m_history.length) {
            this.m_history = Util.resizeArray(this.m_history, this.m_history.length + 10);
        }
        this.m_history[this.m_len++] = IdempotentSequence.methodNum(request.getMethod());
        if (this.r_len >= this.r_history.length) {
            this.r_history = Util.resizeArray(this.r_history, this.r_history.length + 10);
        }
        this.r_history[this.r_len++] = request.getRequestURI();
    }

    public boolean isIdempotent(Request request) {
        if (!this.analysis_done) {
            this.do_analysis();
        }
        return (Boolean)this.threads.get(request.getRequestURI());
    }

    private void do_analysis() {
        Object object;
        for (int i = 0; i < this.r_len; ++i) {
            object = this.threads.get(this.r_history[i]);
            if (this.m_history[i] == 0) {
                this.threads.put(this.r_history[i], Boolean.FALSE);
                continue;
            }
            if (object == null) {
                if (IdempotentSequence.methodHasSideEffects(this.m_history[i]) && IdempotentSequence.methodIsComplete(this.m_history[i])) {
                    this.threads.put(this.r_history[i], Boolean.TRUE);
                    continue;
                }
                this.threads.put(this.r_history[i], INDET);
                continue;
            }
            if (object != INDET || !IdempotentSequence.methodHasSideEffects(this.m_history[i])) continue;
            this.threads.put(this.r_history[i], Boolean.FALSE);
        }
        Enumeration enumeration = this.threads.keys();
        while (enumeration.hasMoreElements()) {
            object = (String)enumeration.nextElement();
            if (this.threads.get(object) != INDET) continue;
            this.threads.put(object, Boolean.TRUE);
        }
    }

    public static boolean methodIsIdempotent(String string) {
        return IdempotentSequence.methodIsIdempotent(IdempotentSequence.methodNum(string));
    }

    private static boolean methodIsIdempotent(int n) {
        switch (n) {
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                return true;
            }
        }
        return false;
    }

    public static boolean methodIsComplete(String string) {
        return IdempotentSequence.methodIsComplete(IdempotentSequence.methodNum(string));
    }

    private static boolean methodIsComplete(int n) {
        switch (n) {
            case 1: 
            case 2: 
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                return true;
            }
        }
        return false;
    }

    public static boolean methodHasSideEffects(String string) {
        return IdempotentSequence.methodHasSideEffects(IdempotentSequence.methodNum(string));
    }

    private static boolean methodHasSideEffects(int n) {
        switch (n) {
            case 1: 
            case 2: 
            case 6: 
            case 7: {
                return false;
            }
        }
        return true;
    }

    private static int methodNum(String string) {
        if (string.equals("GET")) {
            return 2;
        }
        if (string.equals("POST")) {
            return 3;
        }
        if (string.equals("HEAD")) {
            return 1;
        }
        if (string.equals("PUT")) {
            return 4;
        }
        if (string.equals("DELETE")) {
            return 5;
        }
        if (string.equals("OPTIONS")) {
            return 6;
        }
        if (string.equals("TRACE")) {
            return 7;
        }
        return 0;
    }
}

