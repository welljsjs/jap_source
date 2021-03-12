/*
 * Decompiled with CFR 0.150.
 */
package com.skype;

import com.skype.Application;
import com.skype.ApplicationAdapter;
import com.skype.Friend;
import com.skype.SkypeException;
import com.skype.SkypeExceptionHandler;
import com.skype.SkypeObject;
import com.skype.StreamListener;
import com.skype.User;
import com.skype.Utils;
import com.skype.connector.Connector;
import com.skype.connector.ConnectorException;
import com.skype.connector.NotificationChecker;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class Stream
extends SkypeObject {
    private final Application application;
    private final String id;
    private Vector listeners = new Vector();
    private SkypeExceptionHandler exceptionHandler;

    Stream(Application application, String string) {
        this.application = application;
        this.id = string;
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof Stream) {
            Stream stream = (Stream)object;
            return this.getId().equals(stream.getId());
        }
        return false;
    }

    public String toString() {
        return this.getId();
    }

    public Application getApplication() {
        return this.application;
    }

    public String getId() {
        return this.id;
    }

    public Friend getFriend() {
        return User.getFriendInstance(this.getId().substring(0, this.getId().indexOf(58)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(String string) throws SkypeException {
        Utils.checkNotNull(string, "text");
        try {
            NotificationChecker notificationChecker = new NotificationChecker(){

                public boolean isTarget(String string) {
                    if (string.startsWith("APPLICATION " + Stream.this.getApplication().getName() + " SENDING ")) {
                        String string2 = string.substring(("APPLICATION " + Stream.this.getApplication().getName() + " SENDING ").length());
                        if ("".equals(string2)) {
                            return true;
                        }
                        String[] arrstring = string2.split(" ");
                        for (int i = 0; i < arrstring.length; ++i) {
                            String string3 = arrstring[i];
                            if (!(string3 = string3.substring(0, string3.indexOf(61))).equals(Stream.this.getId())) continue;
                            return false;
                        }
                        return true;
                    }
                    return false;
                }
            };
            String string2 = "ALTER APPLICATION " + this.getApplication().getName() + " WRITE " + this.getId();
            ApplicationAdapter applicationAdapter = null;
            try {
                final Future future = Connector.getInstance().waitForEndWithId(string2 + " " + string, string2, notificationChecker);
                applicationAdapter = new ApplicationAdapter(){

                    public void disconnected(Stream stream) throws SkypeException {
                        if (stream == Stream.this) {
                            future.cancel(true);
                        }
                    }
                };
                this.application.addApplicationListener(applicationAdapter);
                try {
                    Utils.checkError((String)future.get());
                }
                catch (CancellationException cancellationException) {
                    throw new SkypeException("The '" + this.getId() + "' stream is closed.", cancellationException);
                }
                catch (ExecutionException executionException) {
                    if (executionException.getCause() instanceof ConnectorException) {
                        throw (ConnectorException)executionException.getCause();
                    }
                    throw new SkypeException("The '" + string2 + " " + string + "' command failed.", executionException);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new SkypeException("The thread is interrupted.", interruptedException);
                }
                if (applicationAdapter != null) {
                    this.application.removeApplicationListener(applicationAdapter);
                }
            }
            catch (Throwable throwable) {
                if (applicationAdapter != null) {
                    this.application.removeApplicationListener(applicationAdapter);
                }
                throw throwable;
            }
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    public void send(String string) throws SkypeException {
        Utils.checkNotNull(string, "datagram");
        try {
            String string2 = "ALTER APPLICATION " + this.getApplication().getName() + " DATAGRAM " + this.getId();
            String string3 = string2 + " " + string;
            String string4 = Connector.getInstance().execute(string3, string2);
            Utils.checkError(string4);
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }

    public void addStreamListener(StreamListener streamListener) {
        Utils.checkNotNull("listener", streamListener);
        this.listeners.add(streamListener);
    }

    public void removeStreamListener(StreamListener streamListener) {
        Utils.checkNotNull("listener", streamListener);
        this.listeners.remove(streamListener);
    }

    void fireTextReceived(String string) {
        StreamListener[] arrstreamListener = this.listeners.toArray(new StreamListener[0]);
        for (int i = 0; i < arrstreamListener.length; ++i) {
            StreamListener streamListener = arrstreamListener[i];
            try {
                streamListener.textReceived(string);
                continue;
            }
            catch (Throwable throwable) {
                Utils.handleUncaughtException(throwable, this.exceptionHandler);
            }
        }
    }

    void fireDatagramReceived(String string) {
        StreamListener[] arrstreamListener = this.listeners.toArray(new StreamListener[0]);
        for (int i = 0; i < arrstreamListener.length; ++i) {
            StreamListener streamListener = arrstreamListener[i];
            try {
                streamListener.datagramReceived(string);
                continue;
            }
            catch (Throwable throwable) {
                Utils.handleUncaughtException(throwable, this.exceptionHandler);
            }
        }
    }

    public void disconnect() throws SkypeException {
        try {
            String string = Connector.getInstance().execute("ALTER APPLICATION " + this.application.getName() + " DISCONNECT " + this.getId());
            Utils.checkError(string);
        }
        catch (ConnectorException connectorException) {
            Utils.convertToSkypeException(connectorException);
        }
    }
}

