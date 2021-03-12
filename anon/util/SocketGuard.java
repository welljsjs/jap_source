/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.platform.AbstractOS;
import anon.util.IReturnRunnable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class SocketGuard {
    private Thread m_threadCloseGuard = null;
    private Object SYNC_CLOSE = new Object();
    private Socket m_Socket;
    private InputStream m_istream;
    private OutputStream m_ostream;
    private String m_strCaller;
    static /* synthetic */ Class class$java$net$Socket;

    public SocketGuard(Socket socket, final long l) throws IOException {
        this.m_Socket = socket;
        this.m_strCaller = LogHolder.getCallingMethod(true);
        if (l > 0L && SocketGuard.isClosedSupported()) {
            Runnable runnable = new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public void run() {
                    Object object = SocketGuard.this.SYNC_CLOSE;
                    synchronized (object) {
                        if (SocketGuard.this.m_threadCloseGuard != null) {
                            LogHolder.log(7, LogType.NET, "Waiting for socket for " + l + " milliseconds...");
                            try {
                                SocketGuard.this.SYNC_CLOSE.wait(l);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                            if (!SocketGuard.isClosed(SocketGuard.this.m_Socket)) {
                                LogHolder.log(1, LogType.NET, "Closing socket after " + l + " milliseconds! Called from: " + SocketGuard.this.m_strCaller);
                                try {
                                    SocketGuard.this.close();
                                }
                                catch (IOException iOException) {
                                    LogHolder.log(1, LogType.NET, iOException);
                                }
                                if (!SocketGuard.isClosed(SocketGuard.this.m_Socket)) {
                                    LogHolder.log(1, LogType.NET, "Unable to close socket!! This is a serious problem. Called from: " + SocketGuard.this.m_strCaller);
                                }
                            }
                        }
                    }
                }
            };
            this.m_threadCloseGuard = new Thread(runnable);
            this.m_threadCloseGuard.start();
        }
        this.m_istream = socket.getInputStream();
        this.m_ostream = socket.getOutputStream();
    }

    public Socket getSocket() {
        return this.m_Socket;
    }

    public InetAddress getInetAddress() {
        return this.m_Socket.getInetAddress();
    }

    public InputStream getInputStream() {
        return this.m_istream;
    }

    public OutputStream getOutputStream() {
        return this.m_ostream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        IOException iOException;
        block13: {
            block12: {
                iOException = null;
                try {
                    if (this.m_ostream != null) {
                        this.m_ostream.close();
                    }
                }
                catch (IOException iOException2) {
                    if (iOException != null) break block12;
                    iOException = iOException2;
                }
            }
            try {
                if (this.m_istream != null) {
                    this.m_istream.close();
                }
            }
            catch (IOException iOException3) {
                if (iOException != null) break block13;
                iOException = iOException3;
            }
        }
        if (this.m_Socket != null) {
            this.m_Socket.close();
        }
        if (this.m_threadCloseGuard != null) {
            Object object = this.SYNC_CLOSE;
            synchronized (object) {
                this.SYNC_CLOSE.notify();
                this.m_threadCloseGuard = null;
            }
        }
        if (iOException != null) {
            throw iOException;
        }
    }

    public static void close(SocketGuard socketGuard) {
        if (socketGuard != null) {
            try {
                socketGuard.close();
            }
            catch (IOException iOException) {
                LogHolder.log(3, LogType.NET, iOException);
            }
        }
    }

    public boolean isClosed() {
        return SocketGuard.isClosed(this.m_Socket);
    }

    public void setSoTimeout(int n) throws SocketException {
        if (this.m_Socket != null) {
            this.m_Socket.setSoTimeout(n);
        }
    }

    public static boolean isClosedSupported() {
        try {
            (class$java$net$Socket == null ? (class$java$net$Socket = SocketGuard.class$("java.net.Socket")) : class$java$net$Socket).getMethod("isClosed", new Class[0]);
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    public static void close(final Socket socket) throws IOException {
        if (socket == null) {
            return;
        }
        IReturnRunnable iReturnRunnable = new IReturnRunnable(){
            IOException ex = null;

            public void run() {
                do {
                    boolean bl = SocketGuard.isClosed(socket);
                    try {
                        socket.getInputStream().close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        socket.getOutputStream().close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        socket.close();
                        Thread.yield();
                    }
                    catch (IOException iOException) {
                        this.ex = iOException;
                        if (bl || SocketGuard.isClosedSupported()) continue;
                        LogHolder.log(4, LogType.NET, iOException);
                    }
                } while (SocketGuard.isClosedSupported() && !SocketGuard.isClosed(socket));
            }

            public Object getValue() {
                return this.ex;
            }
        };
        Thread thread = new Thread(iReturnRunnable);
        thread.start();
        do {
            try {
                thread.join();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        } while (thread.isAlive());
        if (iReturnRunnable.getValue() != null && !SocketGuard.isClosedSupported()) {
            throw (IOException)iReturnRunnable.getValue();
        }
    }

    public static boolean isClosed(Socket socket) {
        Socket socket2 = socket;
        if (socket2 != null) {
            try {
                return (Boolean)(class$java$net$Socket == null ? (class$java$net$Socket = SocketGuard.class$("java.net.Socket")) : class$java$net$Socket).getMethod("isClosed", new Class[0]).invoke(socket2, new Object[0]);
            }
            catch (Exception exception) {
                return false;
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Socket createSocket(final String string, final int n, long l) throws Exception {
        final Vector vector = new Vector();
        final Vector vector2 = new Vector();
        Thread thread = new Thread(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                try {
                    Socket socket = new Socket(string, n);
                    Vector vector3 = vector;
                    synchronized (vector3) {
                        if (Thread.interrupted()) {
                            socket.close();
                        } else {
                            vector.addElement(socket);
                        }
                    }
                }
                catch (Exception exception) {
                    Vector vector4 = vector2;
                    synchronized (vector4) {
                        vector2.addElement(exception);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        try {
            thread.join(l);
        }
        catch (Exception exception) {
            thread.interrupt();
            Vector vector3 = vector;
            synchronized (vector3) {
                if (vector.size() > 0) {
                    try {
                        ((Socket)vector.firstElement()).close();
                    }
                    catch (Exception exception2) {
                        // empty catch block
                    }
                }
            }
            throw exception;
        }
        Socket socket = null;
        Vector vector4 = vector;
        synchronized (vector4) {
            Vector vector5 = vector2;
            synchronized (vector5) {
                if (vector2.size() > 0) {
                    throw (Exception)vector2.firstElement();
                }
                if (vector.size() <= 0) {
                    thread.interrupt();
                    throw new Exception("Timeout occured while creating socket.");
                }
                socket = (Socket)vector.firstElement();
            }
        }
        return socket;
    }

    public static ServerSocket createVirtualBoxServerSocket(int n, InetAddress inetAddress) {
        InetAddress inetAddress2;
        Enumeration enumeration;
        Method method;
        Class<?> class_;
        Class<?> class_2;
        ServerSocket serverSocket = null;
        try {
            class_2 = Class.forName("java.net.NetworkInterface");
            class_ = Class.forName("java.net.Inet6Address");
            method = class_2.getMethod("getNetworkInterfaces", null);
            enumeration = (Enumeration)method.invoke(null, null);
        }
        catch (Exception exception) {
            LogHolder.log(1, LogType.NET, "Could not get any network interfaces!", exception);
            return null;
        }
        InetAddress inetAddress3 = null;
        block8: while (enumeration != null && enumeration.hasMoreElements() && serverSocket == null) {
            Enumeration enumeration2;
            Object e = enumeration.nextElement();
            if (!AbstractOS.getInstance().isVirtualBoxInterface(e)) continue;
            try {
                method = class_2.getMethod("getInetAddresses", null);
                enumeration2 = (Enumeration)method.invoke(e, null);
            }
            catch (Exception exception) {
                LogHolder.log(1, LogType.NET, "Could not analyze network interfaces!", exception);
                return null;
            }
            while (enumeration2.hasMoreElements()) {
                inetAddress2 = (InetAddress)enumeration2.nextElement();
                if (class_.isInstance(inetAddress2)) {
                    if (inetAddress3 != null) continue;
                    inetAddress3 = inetAddress2;
                    continue;
                }
                if (inetAddress.equals(inetAddress2)) {
                    enumeration = null;
                    continue block8;
                }
                LogHolder.log(5, LogType.NET, "Try binding Listener on host: " + inetAddress2);
                try {
                    serverSocket = new ServerSocket(n, 50, inetAddress2);
                    LogHolder.log(1, LogType.NET, "Listener was successfully bound to: " + inetAddress2.getHostAddress() + ":" + n);
                    continue block8;
                }
                catch (IOException iOException) {
                    LogHolder.log(4, LogType.NET, "Could not bind listener to host: " + inetAddress2, iOException);
                }
            }
        }
        if (serverSocket == null && inetAddress3 != null) {
            inetAddress2 = inetAddress3;
            try {
                serverSocket = new ServerSocket(n, 50, inetAddress2);
                LogHolder.log(1, LogType.NET, "Listener was successfully bound to: " + serverSocket);
            }
            catch (IOException iOException) {
                LogHolder.log(4, LogType.NET, "Could not bind listener to host: " + inetAddress2, iOException);
            }
        }
        return serverSocket;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

