/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.GlobalConstants;
import HTTPClient.HTTPClientModule;
import HTTPClient.ModuleException;
import HTTPClient.NVPair;
import HTTPClient.Request;
import HTTPClient.Response;
import HTTPClient.RoRequest;
import HTTPClient.Util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JunkbusterModule
implements HTTPClientModule,
GlobalConstants {
    private static String bl_file;
    private static String[] bl_lines;
    private static String[] bl_hosts;
    private static int[] bl_ports;
    private static String[] bl_paths;
    private static boolean[] bl_block;
    private static boolean remove_from;
    private static boolean remove_ua;
    private static boolean remove_referer;

    public int requestHandler(Request request, Response[] arrresponse) {
        String string = JunkbusterModule.isBlocked(request);
        if (string != null) {
            NVPair[] arrnVPair = new NVPair[]{new NVPair("Content-type", "text/plain")};
            byte[] arrby = ("JunkbusterModule: this url was blocked by the rule '" + string + "'").getBytes();
            arrresponse[0] = new Response("HTTP/1.1", 403, "Forbidden", arrnVPair, arrby, null, 0);
            return 4;
        }
        NVPair[] arrnVPair = request.getHeaders();
        if (remove_from) {
            arrnVPair = Util.removeAllValues(arrnVPair, "From");
        }
        if (remove_ua) {
            arrnVPair = Util.removeAllValues(arrnVPair, "User-Agent");
        }
        if (remove_referer) {
            arrnVPair = Util.removeAllValues(arrnVPair, "Referer");
        }
        request.setHeaders(arrnVPair);
        return 0;
    }

    public void responsePhase1Handler(Response response, RoRequest roRequest) throws IOException, ModuleException {
    }

    public int responsePhase2Handler(Response response, Request request) throws IOException {
        return 10;
    }

    public void responsePhase3Handler(Response response, RoRequest roRequest) {
    }

    public void trailerHandler(Response response, RoRequest roRequest) {
    }

    public static void removeFrom(boolean bl) {
        remove_from = bl;
    }

    public static void removeUserAgent(boolean bl) {
        remove_ua = bl;
    }

    public static void removeReferer(boolean bl) {
        remove_referer = bl;
    }

    private static String isBlocked(RoRequest roRequest) {
        String string = roRequest.getConnection().getHost();
        int n = roRequest.getConnection().getPort();
        String string2 = Util.getPath(roRequest.getRequestURI());
        boolean bl = false;
        String string3 = null;
        for (int i = 0; i < bl_hosts.length; ++i) {
            String string4 = bl_hosts[i];
            String string5 = bl_paths[i];
            int n2 = bl_ports[i];
            if (string4 != null && !string4.equals(string) && (string4.length() >= string.length() || !string.endsWith(string4) || string.charAt(string.length() - string4.length() - 1) != '.') || n2 != -1 && n2 != n || string5 != null && !string2.startsWith(string5)) continue;
            bl = bl_block[i];
            string3 = bl_lines[i];
        }
        return bl ? string3 : null;
    }

    private static synchronized void readBlocklist(String string) {
        if (string == null) {
            return;
        }
        try {
            String string2;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(string));
            bl_lines = new String[100];
            bl_hosts = new String[100];
            bl_ports = new int[100];
            bl_paths = new String[100];
            bl_block = new boolean[100];
            int n = 0;
            while ((string2 = bufferedReader.readLine()) != null) {
                int n2;
                if (n == bl_hosts.length) {
                    bl_lines = Util.resizeArray(bl_lines, n + 100);
                    bl_hosts = Util.resizeArray(bl_hosts, n + 100);
                    bl_ports = Util.resizeArray(bl_ports, n + 100);
                    bl_paths = Util.resizeArray(bl_paths, n + 100);
                    bl_block = Util.resizeArray(bl_block, n + 100);
                }
                if ((n2 = string2.indexOf(35)) != -1) {
                    string2 = string2.substring(0, n2);
                }
                if ((string2 = string2.trim()).length() == 0) continue;
                JunkbusterModule.bl_lines[n] = string2;
                n2 = 0;
                if (string2.charAt(0) == '~') {
                    JunkbusterModule.bl_block[n] = false;
                    n2 = 1;
                } else {
                    JunkbusterModule.bl_block[n] = true;
                }
                if (string2.charAt(n2) != '/') {
                    int n3;
                    int n4 = string2.indexOf(47);
                    if (n4 == -1) {
                        n4 = string2.length();
                    }
                    if ((n3 = string2.indexOf(58)) > n4) {
                        n3 = -1;
                    }
                    if (n3 != -1) {
                        if (n3 > n2) {
                            JunkbusterModule.bl_hosts[n] = string2.substring(n2, n3);
                        }
                        JunkbusterModule.bl_ports[n] = Integer.parseInt(string2.substring(n3 + 1, n4));
                    } else {
                        JunkbusterModule.bl_hosts[n] = string2.substring(n2, n4);
                        JunkbusterModule.bl_ports[n] = -1;
                    }
                    n2 = n4;
                } else {
                    JunkbusterModule.bl_ports[n] = -1;
                }
                if (n2 < string2.length()) {
                    JunkbusterModule.bl_paths[n] = string2.substring(n2);
                }
                ++n;
            }
            bl_lines = Util.resizeArray(bl_lines, n);
            bl_hosts = Util.resizeArray(bl_hosts, n);
            bl_ports = Util.resizeArray(bl_ports, n);
            bl_paths = Util.resizeArray(bl_paths, n);
            bl_block = Util.resizeArray(bl_block, n);
        }
        catch (Exception exception) {
            bl_lines = new String[0];
            bl_hosts = new String[0];
            bl_ports = new int[0];
            bl_paths = new String[0];
            bl_block = new boolean[0];
        }
    }

    static {
        try {
            remove_from = Boolean.getBoolean("HTTPClient.junkbuster.remove_from");
        }
        catch (Exception exception) {
            remove_from = false;
        }
        try {
            remove_ua = Boolean.getBoolean("HTTPClient.junkbuster.remove_useragent");
        }
        catch (Exception exception) {
            remove_ua = false;
        }
        try {
            remove_referer = Boolean.getBoolean("HTTPClient.junkbuster.remove_referer");
        }
        catch (Exception exception) {
            remove_referer = false;
        }
        try {
            bl_file = System.getProperty("HTTPClient.junkbuster.blockfile");
        }
        catch (Exception exception) {
            bl_file = null;
        }
        JunkbusterModule.readBlocklist(bl_file);
    }
}

