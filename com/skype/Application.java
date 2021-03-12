/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.ApplicationListener;
import com.skype.Friend;
import com.skype.SkypeException;
import com.skype.SkypeExceptionHandler;
import com.skype.SkypeObject;
import com.skype.Stream;
import com.skype.Utils;
import com.skype.connector.AbstractConnectorListener;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import java.util.Hashtable;
import java.util.Vector;

public final class Application
extends SkypeObject {
    private static final Hashtable applications = new Hashtable();
    private final String name;
    private boolean isInitialized;
    private final Object isInitializedFieldMutex = new Object();
    private Thread shutdownHookForFinish = new ShutdownHookForFinish();
    private final ConnectorListener dataListener = new DataListener();
    private final Object connectMutex = new Object();
    private final Vector listeners = new Vector();
    private final Hashtable streams = new Hashtable();
    private SkypeExceptionHandler exceptionHandler;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Application getInstance(String string) throws SkypeException {
        Application application = new Application(string);
        Application application2 = null;
        Hashtable hashtable = applications;
        synchronized (hashtable) {
            application2 = !applications.containsKey(string) ? applications.put(string, application) : (Application)applications.get(string);
        }
        if (application2 == null) {
            application2 = application;
        }
        application2.initialize();
        return application2;
    }

    private Application(String string) throws SkypeException {
        this.name = string;
    }

    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this.name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void initialize() throws SkypeException {
        try {
            Object object = this.isInitializedFieldMutex;
            synchronized (object) {
                String string = Connector.getInstance().execute("CREATE APPLICATION " + this.name);
                this.getAllStreams();
                if (string.startsWith("ERROR ") && !string.startsWith("ERROR 541 ")) {
                    Utils.checkError(string);
                }
                if (!this.isInitialized) {
                    Connector.getInstance().addConnectorListener(this.dataListener, false, true);
                    Runtime.getRuntime().addShutdownHook(this.shutdownHookForFinish);
                    this.isInitialized = true;
                }
            }
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void finish() throws SkypeException {
        try {
            Object object = this.isInitializedFieldMutex;
            synchronized (object) {
                if (this.isInitialized) {
                    Connector.getInstance().removeConnectorListener(this.dataListener);
                    Runtime.getRuntime().removeShutdownHook(this.shutdownHookForFinish);
                    String string = Connector.getInstance().execute("DELETE APPLICATION " + this.getName());
                    Utils.checkError(string);
                    Connector.getInstance().dispose();
                    this.isInitialized = false;
                }
            }
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    public Stream[] connect(Friend friend) throws SkypeException {
        Utils.checkNotNull("friends", friend);
        return this.connect(friend.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Stream[] connect(final String string) throws SkypeException {
        Utils.checkNotNull("ids", string);
        Object object = this.connectMutex;
        synchronized (object) {
            try {
                final Stream[] arrstream = new Object();
                AbstractConnectorListener abstractConnectorListener = new AbstractConnectorListener(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    public void messageReceived(ConnectorMessageEvent connectorMessageEvent) {
                        String string4 = connectorMessageEvent.getMessage();
                        if (string4.equals("APPLICATION " + Application.this.getName() + " CONNECTING ")) {
                            Object object = arrstream;
                            synchronized (object) {
                                arrstream.notify();
                            }
                        } else if (string4.startsWith("APPLICATION " + Application.this.getName() + " STREAMS ")) {
                            String string2 = string4.substring(("APPLICATION " + Application.this.getName() + " STREAMS ").length());
                            if ("".equals(string2)) {
                                return;
                            }
                            String[] arrstring = string2.split(" ");
                            for (int i = 0; i < arrstring.length; ++i) {
                                String string3 = arrstring[i];
                                if (!string3.startsWith(string + ":")) continue;
                                Object object = arrstream;
                                synchronized (object) {
                                    arrstream.notify();
                                    return;
                                }
                            }
                        }
                    }
                };
                try {
                    Connector.getInstance().addConnectorListener(abstractConnectorListener);
                    Stream[] arrstream2 = arrstream;
                    synchronized (arrstream) {
                        if (string != null) {
                            String string2 = "ALTER APPLICATION " + this.getName() + " CONNECT " + string;
                            String string3 = Connector.getInstance().execute(string2, new String[]{string2, "APPLICATION " + this.getName() + " CONNECTING ", "ERROR "});
                            Utils.checkError(string3);
                        }
                        try {
                            arrstream.wait();
                        }
                        catch (InterruptedException interruptedException) {
                            throw new SkypeException("The connecting was interrupted.", interruptedException);
                        }
                        arrstream2 = this.getAllStreams(string);
                        return arrstream2;
                    }
                }
                catch (ConnectorException connectorException) {
                    Utils.convertToSkypeException(connectorException);
                    Stream[] arrstream3 = null;
                    return arrstream3;
                }
                finally {
                    Connector.getInstance().removeConnectorListener(abstractConnectorListener);
                }
            }
            catch (SkypeException skypeException) {
                Stream[] arrstream = this.getAllStreams(string);
                int n = 0;
                while (true) {
                    if (n >= arrstream.length) {
                        throw skypeException;
                    }
                    Stream stream = arrstream[n];
                    try {
                        stream.disconnect();
                    }
                    catch (SkypeException skypeException2) {
                        // empty catch block
                    }
                    ++n;
                }
            }
        }
    }

    public Stream[] getAllStreams(String string) throws SkypeException {
        Vector<Stream> vector = new Vector<Stream>();
        Stream[] arrstream = this.getAllStreams();
        for (int i = 0; i < arrstream.length; ++i) {
            Stream stream = arrstream[i];
            String string2 = stream.getFriend().getId();
            if (!string2.equals(string)) continue;
            vector.add(stream);
        }
        return vector.toArray(new Stream[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Stream[] getAllStreams() throws SkypeException {
        String string = Utils.getPropertyWithCommandId("APPLICATION", this.getName(), "STREAMS");
        Hashtable hashtable = this.streams;
        synchronized (hashtable) {
            this.fireStreamEvents(string);
            if ("".equals(string)) {
                return new Stream[0];
            }
            String[] arrstring = string.split(" ");
            Stream[] arrstream = new Stream[arrstring.length];
            for (int i = 0; i < arrstring.length; ++i) {
                arrstream[i] = (Stream)this.streams.get(arrstring[i]);
            }
            return arrstream;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireStreamEvents(String string) {
        Hashtable hashtable = this.streams;
        synchronized (hashtable) {
            Object object;
            String[] arrstring = "".equals(string) ? new String[0] : string.split(" ");
            for (int i = 0; i < arrstring.length; ++i) {
                String string2 = arrstring[i];
                if (this.streams.containsKey(string2)) continue;
                object = new Stream(this, string2);
                this.streams.put(string2, object);
                this.fireConnected((Stream)object);
            }
            String[] arrstring2 = this.streams.keySet().toArray(new String[0]);
            block4: for (int i = 0; i < arrstring2.length; ++i) {
                object = arrstring2[i];
                for (int j = 0; j < arrstring.length; ++j) {
                    String string3 = arrstring[j];
                    if (((String)object).equals(string3)) continue block4;
                }
                Stream stream = (Stream)this.streams.remove(object);
                this.fireDisconnected(stream);
            }
        }
    }

    private void fireConnected(Stream stream) {
        ApplicationListener[] arrapplicationListener = this.listeners.toArray(new ApplicationListener[0]);
        for (int i = 0; i < arrapplicationListener.length; ++i) {
            ApplicationListener applicationListener = arrapplicationListener[i];
            try {
                applicationListener.connected(stream);
                continue;
            }
            catch (Throwable throwable) {
                Utils.handleUncaughtException(throwable, this.exceptionHandler);
            }
        }
    }

    private void fireDisconnected(Stream stream) {
        ApplicationListener[] arrapplicationListener = this.listeners.toArray(new ApplicationListener[0]);
        for (int i = 0; i < arrapplicationListener.length; ++i) {
            ApplicationListener applicationListener = arrapplicationListener[i];
            try {
                applicationListener.disconnected(stream);
                continue;
            }
            catch (Throwable throwable) {
                Utils.handleUncaughtException(throwable, this.exceptionHandler);
            }
        }
    }

    public void addApplicationListener(ApplicationListener applicationListener) {
        Utils.checkNotNull("listener", applicationListener);
        this.listeners.add(applicationListener);
    }

    public void removeApplicationListener(ApplicationListener applicationListener) {
        Utils.checkNotNull("listener", applicationListener);
        this.listeners.remove(applicationListener);
    }

    private class ShutdownHookForFinish
    extends Thread {
        private ShutdownHookForFinish() {
        }

        public void run() {
            try {
                Connector.getInstance().execute("DELETE APPLICATION " + Application.this.getName());
                Connector.getInstance().dispose();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private class DataListener
    extends AbstractConnectorListener {
        private DataListener() {
        }

        public void messageReceived(ConnectorMessageEvent connectorMessageEvent) {
            String string;
            String string2;
            String string3 = connectorMessageEvent.getMessage();
            if (string3.startsWith(string2 = "APPLICATION " + Application.this.getName() + " STREAMS ")) {
                string = string3.substring(string2.length());
                Application.this.fireStreamEvents(string);
            }
            if (string3.startsWith(string = "APPLICATION " + Application.this.getName() + " ")) {
                this.handleData(string3.substring(string.length()));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void handleData(String string) {
            block11: {
                try {
                    if (this.isReceivedText(string)) {
                        String string2 = string.substring("RECEIVED ".length());
                        String string3 = string2.substring(0, string2.indexOf(61));
                        String string4 = "ALTER APPLICATION " + Application.this.getName() + " READ " + string3;
                        String string5 = Connector.getInstance().executeWithId(string4, string4);
                        Utils.checkError(string5);
                        String string6 = string5.substring(string4.length() + 1);
                        Hashtable hashtable = Application.this.streams;
                        synchronized (hashtable) {
                            if (Application.this.streams.containsKey(string3)) {
                                ((Stream)Application.this.streams.get(string3)).fireTextReceived(string6);
                            }
                            break block11;
                        }
                    }
                    if (!this.isReceivedDatagram(string)) break block11;
                    String string7 = string.substring("DATAGRAM ".length());
                    String string8 = string7.substring(0, string7.indexOf(32));
                    String string9 = string7.substring(string7.indexOf(32) + 1);
                    Hashtable hashtable = Application.this.streams;
                    synchronized (hashtable) {
                        if (Application.this.streams.containsKey(string8)) {
                            ((Stream)Application.this.streams.get(string8)).fireDatagramReceived(string9);
                        }
                    }
                }
                catch (Exception exception) {
                    Utils.handleUncaughtException(exception, Application.this.exceptionHandler);
                }
            }
        }

        private boolean isReceivedText(String string) {
            return string.startsWith("RECEIVED ") && "RECEIVED ".length() < string.length();
        }

        private boolean isReceivedDatagram(String string) {
            return string.startsWith("DATAGRAM ");
        }
    }
}

