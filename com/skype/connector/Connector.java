/*
 * Decompiled with CFR 0.150.
 */
package com.skype.connector;

import com.skype.connector.AbstractConnectorListener;
import com.skype.connector.ConnectorException;
import com.skype.connector.ConnectorListener;
import com.skype.connector.ConnectorMessageEvent;
import com.skype.connector.ConnectorStatusEvent;
import com.skype.connector.ConnectorUtils;
import com.skype.connector.NotAttachedException;
import com.skype.connector.NotificationChecker;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Connector {
    private static Connector _instance;
    private volatile String _applicationName = "Skype4Java";
    private volatile int _status = 6;
    private volatile int _connectTimeout = 10000;
    private volatile int _commandTimeout = 10000;
    private final Object _isInitializedMutex = new Object();
    private boolean _isInitialized;
    private ExecutorService _asyncSender;
    private ExecutorService _syncSender;
    private final Vector _asyncListeners = new Vector();
    private final Vector _syncListeners = new Vector();
    private final AtomicInteger _commandCount = new AtomicInteger();
    private ExecutorService _commandExecutor;
    private final Hashtable properties = new Hashtable();

    public static synchronized Connector getInstance() {
        if (_instance == null) {
            String string = null;
            String string2 = System.getProperty("os.name");
            if (string2.startsWith("Windows")) {
                string = "com.skype.connector.win32.Win32Connector";
            } else if (string2.startsWith("Linux") || string2.startsWith("LINUX")) {
                string = "com.skype.connector.linux.LinuxConnector";
            } else if (string2.startsWith("Mac OS X")) {
                string = "com.skype.connector.osx.OSXConnector";
            }
            if (string == null) {
                throw new IllegalStateException("This platform is not supported by Skype4Java.");
            }
            try {
                Class<?> class_ = Class.forName(string);
                Method method = class_.getMethod("getInstance", null);
                _instance = (Connector)method.invoke(null, null);
            }
            catch (Exception exception) {
                throw new IllegalStateException("The connector couldn't be initialized.", exception);
            }
        }
        return _instance;
    }

    protected static synchronized void setInstance(Connector connector) throws ConnectorException {
        if (_instance != null) {
            _instance.dispose();
        }
        _instance = connector;
    }

    protected Connector() {
    }

    public String getInstalledPath() {
        return "skype";
    }

    public final void setApplicationName(String string) {
        ConnectorUtils.checkNotNull("applicationName", string);
        this._applicationName = string;
    }

    public final String getApplicationName() {
        return this._applicationName;
    }

    protected final void setStatus(int n) {
        this._status = n;
        this.fireStatusChanged(n);
    }

    private void fireStatusChanged(final int n) {
        this._syncSender.execute(new Runnable(){

            public void run() {
                Connector.this.fireStatusChanged(Connector.this.toConnectorListenerArray(Connector.this._syncListeners), n);
            }
        });
        this._asyncSender.execute(new Runnable(){

            public void run() {
                Connector.this.fireStatusChanged(Connector.this.toConnectorListenerArray(Connector.this._asyncListeners), n);
            }
        });
    }

    private ConnectorListener[] toConnectorListenerArray(Vector vector) {
        return vector.toArray(new ConnectorListener[0]);
    }

    private void fireStatusChanged(ConnectorListener[] arrconnectorListener, int n) {
        ConnectorStatusEvent connectorStatusEvent = new ConnectorStatusEvent(this, n);
        for (int i = arrconnectorListener.length - 1; 0 <= i; --i) {
            arrconnectorListener[i].statusChanged(connectorStatusEvent);
        }
    }

    public final int getStatus() {
        return this._status;
    }

    public final void setConnectTimeout(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("The connect timeout must be more than 0.");
        }
        this._connectTimeout = n;
    }

    public final int getConnectTimeout() {
        return this._connectTimeout;
    }

    public final void setCommandTimeout(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("The connect timeout must be more than 0.");
        }
        this._commandTimeout = n;
    }

    public final int getCommandTimeout() {
        return this._commandTimeout;
    }

    public final int connect() throws ConnectorException {
        this.initialize();
        int n = this.connect(this.getConnectTimeout());
        if (n == 2) {
            this.sendApplicationName(this.getApplicationName());
            this.sendProtocol();
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void initialize() throws ConnectorException {
        Object object = this._isInitializedMutex;
        synchronized (object) {
            if (!this._isInitialized) {
                this._asyncSender = Executors.newCachedThreadPool(new ThreadFactory(){
                    private final AtomicInteger threadNumber = new AtomicInteger();

                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable, "AsyncSkypeMessageSender-" + this.threadNumber.getAndIncrement());
                        thread.setDaemon(true);
                        return thread;
                    }
                });
                this._syncSender = Executors.newSingleThreadExecutor(new ThreadFactory(){

                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable, "SyncSkypeMessageSender");
                        thread.setDaemon(true);
                        return thread;
                    }
                });
                this._commandExecutor = Executors.newCachedThreadPool(new ThreadFactory(){
                    private final AtomicInteger threadNumber = new AtomicInteger();

                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread(runnable, "CommandExecutor-" + this.threadNumber.getAndIncrement());
                        thread.setDaemon(true);
                        return thread;
                    }
                });
                this.initializeImpl();
                this._isInitialized = true;
            }
        }
    }

    protected abstract void initializeImpl() throws ConnectorException;

    protected abstract int connect(int var1) throws ConnectorException;

    protected void sendApplicationName(String string) throws ConnectorException {
    }

    protected void sendProtocol() throws ConnectorException {
        this.execute("PROTOCOL 9999", new String[]{"PROTOCOL "}, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void dispose() throws ConnectorException {
        Object object = this._isInitializedMutex;
        synchronized (object) {
            if (!this._isInitialized) {
                return;
            }
            this.disposeImpl();
            this.setStatus(6);
            this._commandExecutor.shutdown();
            this._syncSender.shutdown();
            this._asyncSender.shutdown();
            this._syncListeners.clear();
            this._asyncListeners.clear();
            this._isInitialized = false;
        }
    }

    protected abstract void disposeImpl() throws ConnectorException;

    public boolean isRunning() throws ConnectorException {
        try {
            this.assureAttached();
            return true;
        }
        catch (ConnectorException connectorException) {
            return false;
        }
    }

    public final String execute(String string) throws ConnectorException {
        ConnectorUtils.checkNotNull("command", string);
        return this.execute(string, string);
    }

    public final String executeWithId(String string, String string2) throws ConnectorException {
        ConnectorUtils.checkNotNull("command", string);
        ConnectorUtils.checkNotNull("responseHeader", string2);
        String string3 = "#" + this._commandCount.getAndIncrement() + " ";
        String string4 = this.execute(string3 + string, new String[]{string3 + string2, string3 + "ERROR "}, true);
        return string4.substring(string3.length());
    }

    public final Future waitForEndWithId(String string, String string2, final NotificationChecker notificationChecker) throws ConnectorException {
        ConnectorUtils.checkNotNull("command", string);
        ConnectorUtils.checkNotNull("responseHeader", string2);
        ConnectorUtils.checkNotNull("responseHeader", notificationChecker);
        final String string3 = "#" + this._commandCount.getAndIncrement() + " ";
        NotificationChecker notificationChecker2 = new NotificationChecker(){

            public boolean isTarget(String string) {
                if (notificationChecker.isTarget(string)) {
                    return true;
                }
                return string.startsWith(string3 + "ERROR ");
            }
        };
        final Future future = this.execute(string3 + string, notificationChecker2, true, false);
        return new Future(){

            public boolean isDone() {
                return future.isDone();
            }

            public boolean isCancelled() {
                return future.isCancelled();
            }

            public Object get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return this.removeId((String)future.get(l, timeUnit));
            }

            public Object get() throws InterruptedException, ExecutionException {
                return this.removeId((String)future.get());
            }

            private String removeId(String string) {
                if (string.startsWith(string3)) {
                    return string.substring(string3.length());
                }
                return string;
            }

            public boolean cancel(boolean bl) {
                return future.cancel(bl);
            }
        };
    }

    public final String executeWithoutTimeout(String string, String string2) throws ConnectorException {
        ConnectorUtils.checkNotNull("command", string);
        ConnectorUtils.checkNotNull("responseHeader", string2);
        return this.execute(string, new String[]{string2, "ERROR "}, true, true);
    }

    public final String execute(String string, String string2) throws ConnectorException {
        ConnectorUtils.checkNotNull("command", string);
        ConnectorUtils.checkNotNull("responseHeader", string2);
        return this.execute(string, new String[]{string2, "ERROR "}, true);
    }

    public final String execute(String string, String[] arrstring) throws ConnectorException {
        ConnectorUtils.checkNotNull("command", string);
        ConnectorUtils.checkNotNull("responseHeaders", arrstring);
        return this.execute(string, arrstring, true);
    }

    protected final String execute(String string, String[] arrstring, boolean bl) throws ConnectorException {
        return this.execute(string, arrstring, bl, false);
    }

    private String execute(String string, final String[] arrstring, boolean bl, boolean bl2) throws ConnectorException {
        NotificationChecker notificationChecker = new NotificationChecker(){

            public boolean isTarget(String string) {
                for (int i = 0; i < arrstring.length; ++i) {
                    String string2 = arrstring[i];
                    if (!string.startsWith(string2)) continue;
                    return true;
                }
                return false;
            }
        };
        try {
            return (String)this.execute(string, notificationChecker, bl, bl2).get();
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new ConnectorException("The '" + string + "' command was interrupted.", interruptedException);
        }
        catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof NotAttachedException) {
                NotAttachedException notAttachedException = (NotAttachedException)executionException.getCause();
                throw new NotAttachedException(notAttachedException.getStatus(), (Throwable)notAttachedException);
            }
            if (executionException.getCause() instanceof ConnectorException) {
                ConnectorException connectorException = (ConnectorException)executionException.getCause();
                throw new ConnectorException(connectorException.getMessage(), connectorException);
            }
            throw new ConnectorException("The '" + string + "' command execution failed.", executionException);
        }
    }

    private Future execute(final String string, final NotificationChecker notificationChecker, boolean bl, boolean bl2) throws ConnectorException {
        ConnectorUtils.checkNotNull("command", string);
        ConnectorUtils.checkNotNull("responseChecker", notificationChecker);
        if (bl) {
            this.assureAttached();
        }
        return this._commandExecutor.submit(new Callable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public Object call() throws Exception {
                final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
                AbstractConnectorListener abstractConnectorListener = new AbstractConnectorListener(){

                    public void messageReceived(ConnectorMessageEvent connectorMessageEvent) {
                        String string = connectorMessageEvent.getMessage();
                        if (notificationChecker.isTarget(string) || string.startsWith("PONG")) {
                            linkedBlockingQueue.add(string);
                        }
                    }
                };
                Connector.this.addConnectorListener(abstractConnectorListener, false);
                Connector.this.fireMessageSent(string);
                Connector.this.sendCommand(string);
                try {
                    String string3;
                    boolean bl = false;
                    while (true) {
                        if ((string3 = (String)linkedBlockingQueue.poll(Connector.this.getCommandTimeout(), TimeUnit.MILLISECONDS)) == null) {
                            if (bl) {
                                Connector.this.setStatus(6);
                                throw new NotAttachedException(6);
                            }
                            Connector.this.fireMessageSent("PING");
                            Connector.this.sendCommand("PING");
                            bl = true;
                            continue;
                        }
                        if (!string3.startsWith("PONG")) break;
                        bl = false;
                    }
                    String string2 = string3;
                    return string2;
                }
                finally {
                    Connector.this.removeConnectorListener(abstractConnectorListener);
                }
            }
        });
    }

    private void fireMessageSent(String string) {
        this.fireMessageEvent(string, false);
    }

    protected abstract void sendCommand(String var1);

    private void assureAttached() throws ConnectorException {
        int n = this.getStatus();
        if (n != 2 && (n = this.connect()) != 2) {
            throw new NotAttachedException(n);
        }
    }

    public final void addConnectorListener(ConnectorListener connectorListener) throws ConnectorException {
        this.addConnectorListener(connectorListener, true);
    }

    public final void addConnectorListener(ConnectorListener connectorListener, boolean bl) throws ConnectorException {
        this.addConnectorListener(connectorListener, bl, false);
    }

    public final void addConnectorListener(ConnectorListener connectorListener, boolean bl, boolean bl2) throws ConnectorException {
        ConnectorUtils.checkNotNull("listener", connectorListener);
        if (bl2) {
            this._syncListeners.add(connectorListener);
        } else {
            this._asyncListeners.add(connectorListener);
        }
        if (bl) {
            this.assureAttached();
        }
    }

    public final void removeConnectorListener(ConnectorListener connectorListener) {
        ConnectorUtils.checkNotNull("listener", connectorListener);
        this._syncListeners.remove(connectorListener);
        this._asyncListeners.remove(connectorListener);
    }

    protected final void fireMessageReceived(String string) {
        this.fireMessageEvent(string, true);
    }

    private void fireMessageEvent(final String string, final boolean bl) {
        ConnectorUtils.checkNotNull("message", string);
        this._syncSender.execute(new Runnable(){

            public void run() {
                Connector.this.fireMessageEvent(Connector.this.toConnectorListenerArray(Connector.this._syncListeners), string, bl);
            }
        });
        this._asyncSender.execute(new Runnable(){

            public void run() {
                Connector.this.fireMessageEvent(Connector.this.toConnectorListenerArray(Connector.this._asyncListeners), string, bl);
            }
        });
    }

    private void fireMessageEvent(ConnectorListener[] arrconnectorListener, String string, boolean bl) {
        ConnectorMessageEvent connectorMessageEvent = new ConnectorMessageEvent(this, string);
        for (int i = arrconnectorListener.length - 1; 0 <= i; --i) {
            if (bl) {
                arrconnectorListener[i].messageReceived(connectorMessageEvent);
                continue;
            }
            arrconnectorListener[i].messageSent(connectorMessageEvent);
        }
    }

    public final void setStringProperty(String string, String string2) {
        ConnectorUtils.checkNotNull("name", string);
        if (string2 != null) {
            this.properties.put(string, string2);
        } else {
            this.properties.remove(string);
        }
    }

    public final String getStringProperty(String string) {
        ConnectorUtils.checkNotNull("name", string);
        return (String)this.properties.get(string);
    }

    public class Status {
        public static final int PENDING_AUTHORIZATION = 1;
        public static final int ATTACHED = 2;
        public static final int REFUSED = 3;
        public static final int NOT_AVAILABLE = 4;
        public static final int API_AVAILABLE = 5;
        public static final int NOT_RUNNING = 6;
    }
}

