/*
 * Decompiled with CFR 0.150.
 */
package org.apache.log4j;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import org.apache.log4j.helpers.LogLog;

public class NDC {
    static Hashtable ht = new Hashtable();
    static int pushCounter = 0;
    static final int REAP_THRESHOLD = 5;

    private NDC() {
    }

    public static void clear() {
        Stack stack = (Stack)ht.get(Thread.currentThread());
        if (stack != null) {
            stack.setSize(0);
        }
    }

    public static Stack cloneStack() {
        Object v = ht.get(Thread.currentThread());
        if (v == null) {
            return null;
        }
        Stack stack = (Stack)v;
        return (Stack)stack.clone();
    }

    public static void inherit(Stack stack) {
        if (stack != null) {
            ht.put(Thread.currentThread(), stack);
        }
    }

    public static String get() {
        Stack stack = (Stack)ht.get(Thread.currentThread());
        if (stack != null && !stack.isEmpty()) {
            return ((DiagnosticContext)stack.peek()).fullMessage;
        }
        return null;
    }

    public static int getDepth() {
        Stack stack = (Stack)ht.get(Thread.currentThread());
        if (stack == null) {
            return 0;
        }
        return stack.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void lazyRemove() {
        Object object;
        Vector<Thread> vector;
        int n;
        Hashtable hashtable = ht;
        synchronized (hashtable) {
            if (++pushCounter <= 5) {
                return;
            }
            pushCounter = 0;
            n = 0;
            vector = new Vector<Thread>();
            object = ht.keys();
            while (object.hasMoreElements() && n <= 4) {
                Thread thread = (Thread)object.nextElement();
                if (thread.isAlive()) {
                    ++n;
                    continue;
                }
                n = 0;
                vector.addElement(thread);
            }
        }
        int n2 = vector.size();
        for (n = 0; n < n2; ++n) {
            object = (Thread)vector.elementAt(n);
            LogLog.debug("Lazy NDC removal for thread [" + ((Thread)object).getName() + "] (" + ht.size() + ").");
            ht.remove(object);
        }
    }

    public static String pop() {
        Thread thread = Thread.currentThread();
        Stack stack = (Stack)ht.get(thread);
        if (stack != null && !stack.isEmpty()) {
            return ((DiagnosticContext)stack.pop()).message;
        }
        return "";
    }

    public static String peek() {
        Thread thread = Thread.currentThread();
        Stack stack = (Stack)ht.get(thread);
        if (stack != null && !stack.isEmpty()) {
            return ((DiagnosticContext)stack.peek()).message;
        }
        return "";
    }

    public static void push(String string) {
        Thread thread = Thread.currentThread();
        Stack<DiagnosticContext> stack = (Stack<DiagnosticContext>)ht.get(thread);
        if (stack == null) {
            DiagnosticContext diagnosticContext = new DiagnosticContext(string, null);
            stack = new Stack<DiagnosticContext>();
            ht.put(thread, stack);
            stack.push(diagnosticContext);
        } else if (stack.isEmpty()) {
            DiagnosticContext diagnosticContext = new DiagnosticContext(string, null);
            stack.push(diagnosticContext);
        } else {
            DiagnosticContext diagnosticContext = (DiagnosticContext)stack.peek();
            stack.push(new DiagnosticContext(string, diagnosticContext));
        }
    }

    public static void remove() {
        ht.remove(Thread.currentThread());
        NDC.lazyRemove();
    }

    public static void setMaxDepth(int n) {
        Stack stack = (Stack)ht.get(Thread.currentThread());
        if (stack != null && n < stack.size()) {
            stack.setSize(n);
        }
    }

    private static class DiagnosticContext {
        String fullMessage;
        String message;

        DiagnosticContext(String string, DiagnosticContext diagnosticContext) {
            this.message = string;
            this.fullMessage = diagnosticContext != null ? diagnosticContext.fullMessage + ' ' + string : string;
        }
    }
}

