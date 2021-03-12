/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.ordescription;

import anon.crypto.MyRandom;
import anon.tor.ordescription.ORDescriptor;
import anon.tor.ordescription.ORListFetcher;
import anon.tor.util.Base16;
import anon.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public final class ORList {
    private Vector m_onionrouters = new Vector();
    private Vector m_exitnodes = new Vector();
    private Vector m_middlenodes = new Vector();
    private Hashtable m_onionroutersWithNames = new Hashtable();
    private MyRandom m_rand;
    private ORListFetcher m_orlistFetcher;
    private Date m_datePublished;
    private int m_countHibernate;
    private static final DateFormat ms_DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ORList(ORListFetcher oRListFetcher) {
        this.m_orlistFetcher = oRListFetcher;
        this.m_countHibernate = 0;
        this.m_rand = new MyRandom();
    }

    public synchronized int size() {
        return this.m_onionrouters.size();
    }

    public synchronized int active() {
        return this.size() - this.m_countHibernate;
    }

    public synchronized void setFetcher(ORListFetcher oRListFetcher) {
        this.m_orlistFetcher = oRListFetcher;
    }

    public synchronized boolean updateList() {
        try {
            byte[] arrby = null;
            if (this.size() == 0 || (arrby = this.m_orlistFetcher.getRouterStatus()) == null) {
                arrby = this.m_orlistFetcher.getAllDescriptors();
                if (arrby == null) {
                    return false;
                }
                return this.parseFirstDocument(arrby);
            }
            return this.parseStatus(arrby, true);
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.TOR, "There was a problem with fetching the available ORRouters: " + throwable.getMessage());
            return false;
        }
    }

    public Vector getList() {
        return (Vector)this.m_onionrouters.clone();
    }

    public Date getPublished() {
        return this.m_datePublished;
    }

    public synchronized ORDescriptor getByName(String string) {
        return (ORDescriptor)this.m_onionroutersWithNames.get(string);
    }

    public synchronized void remove(String string) {
        ORDescriptor oRDescriptor = this.getByName(string);
        if (oRDescriptor == null) {
            return;
        }
        this.m_onionrouters.removeElement(oRDescriptor);
        if (oRDescriptor.isExitNode()) {
            this.m_exitnodes.removeElement(oRDescriptor);
        } else {
            this.m_middlenodes.removeElement(oRDescriptor);
        }
        this.m_onionroutersWithNames.remove(string);
    }

    public synchronized void add(ORDescriptor oRDescriptor) {
        if (oRDescriptor.isExitNode()) {
            this.m_exitnodes.addElement(oRDescriptor);
        } else {
            this.m_middlenodes.addElement(oRDescriptor);
        }
        this.m_onionrouters.addElement(oRDescriptor);
        this.m_onionroutersWithNames.put(oRDescriptor.getName(), oRDescriptor);
        LogHolder.log(7, LogType.TOR, "Added: " + oRDescriptor);
    }

    public synchronized ORDescriptor getByRandom(Vector vector) {
        ORDescriptor oRDescriptor;
        if (this.active() == 0) {
            return null;
        }
        do {
            String string;
            if ((oRDescriptor = this.getByName(string = (String)vector.elementAt(this.m_rand.nextInt(vector.size())))) != null) continue;
            return null;
        } while (oRDescriptor.getHibernate());
        return oRDescriptor;
    }

    public synchronized ORDescriptor getByRandom() {
        ORDescriptor oRDescriptor;
        if (this.active() == 0) {
            return null;
        }
        while ((oRDescriptor = (ORDescriptor)this.m_onionrouters.elementAt(this.m_rand.nextInt(this.m_onionrouters.size()))).getHibernate()) {
        }
        return oRDescriptor;
    }

    public synchronized ORDescriptor getByRandom(int n) {
        ORDescriptor oRDescriptor;
        if (this.active() == 0) {
            return null;
        }
        int n2 = this.m_onionrouters.size();
        int n3 = n * this.m_exitnodes.size() - n2;
        int n4 = (n - 1) * n2;
        while ((oRDescriptor = this.m_rand.nextInt(n4 *= 2) > n3 ? (ORDescriptor)this.m_middlenodes.elementAt(this.m_rand.nextInt(this.m_middlenodes.size())) : (ORDescriptor)this.m_exitnodes.elementAt(this.m_rand.nextInt(this.m_exitnodes.size()))).getHibernate()) {
        }
        return oRDescriptor;
    }

    public synchronized ORDescriptor getORDescriptor(String string) {
        if (this.m_onionroutersWithNames.containsKey(string)) {
            return (ORDescriptor)this.m_onionroutersWithNames.get(string);
        }
        return null;
    }

    private boolean parseStatus(byte[] arrby, boolean bl) throws Exception {
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(arrby)));
        Date date = null;
        String string = lineNumberReader.readLine();
        boolean bl2 = false;
        if (string == null || !string.startsWith("network-status-version")) {
            return false;
        }
        while (true) {
            byte[] arrby2;
            String string2;
            String string3;
            StringTokenizer stringTokenizer;
            lineNumberReader.mark(200);
            string = lineNumberReader.readLine();
            if (string == null) break;
            if (string.startsWith("published")) {
                stringTokenizer = new StringTokenizer(string, " ");
                stringTokenizer.nextToken();
                string3 = stringTokenizer.nextToken();
                string3 = string3 + " " + stringTokenizer.nextToken();
                date = ms_DateFormat.parse(string3);
                continue;
            }
            if (!string.startsWith("r ")) continue;
            stringTokenizer = new StringTokenizer(string, " ");
            stringTokenizer.nextToken();
            string3 = stringTokenizer.nextToken();
            String string4 = stringTokenizer.nextToken() + "=";
            String string5 = stringTokenizer.nextToken() + "=";
            String string6 = stringTokenizer.nextToken();
            string6 = string6 + " " + stringTokenizer.nextToken();
            String string7 = stringTokenizer.nextToken();
            Vector<String> vector = new Vector<String>();
            int n = Integer.parseInt(stringTokenizer.nextToken());
            lineNumberReader.mark(200);
            string = lineNumberReader.readLine();
            if (!string.startsWith("s ")) {
                lineNumberReader.reset();
            } else {
                stringTokenizer = new StringTokenizer(string);
                stringTokenizer.nextToken();
                while (stringTokenizer.hasMoreTokens()) {
                    vector.addElement(stringTokenizer.nextToken());
                }
            }
            string = lineNumberReader.readLine();
            if (string.startsWith("v ")) {
                string2 = string.substring(2);
            } else if (string.startsWith("opt v ")) {
                string2 = string.substring(6);
            } else {
                lineNumberReader.reset();
            }
            ORDescriptor oRDescriptor = this.getORDescriptor(string3);
            String string8 = Base16.encode(Base64.decode(string5));
            if (oRDescriptor != null && oRDescriptor.getHash() != null && string8.equals(oRDescriptor.getHash()) || (arrby2 = this.m_orlistFetcher.getDescriptor(string8)) == null) continue;
            if (oRDescriptor != null && oRDescriptor.getHibernate()) {
                bl2 = true;
            }
            this.remove(string3);
            LineNumberReader lineNumberReader2 = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(arrby2)));
            oRDescriptor = ORDescriptor.parse(lineNumberReader2);
            oRDescriptor.setHash(string8);
            if (bl2 && !oRDescriptor.getHibernate()) {
                --this.m_countHibernate;
            }
            this.add(oRDescriptor);
        }
        return true;
    }

    private synchronized boolean parseFirstDocument(byte[] arrby) throws Exception {
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(arrby)));
        Date date = new Date();
        lineNumberReader.mark(200);
        String string = lineNumberReader.readLine();
        if (string == null) {
            return false;
        }
        this.m_countHibernate = 0;
        this.m_onionrouters = new Vector();
        this.m_exitnodes = new Vector();
        this.m_middlenodes = new Vector();
        this.m_onionroutersWithNames = new Hashtable();
        do {
            if (string.startsWith("router ")) {
                lineNumberReader.reset();
                ORDescriptor oRDescriptor = ORDescriptor.parse(lineNumberReader);
                if (oRDescriptor != null) {
                    if (oRDescriptor.getHibernate()) {
                        ++this.m_countHibernate;
                    }
                    this.add(oRDescriptor);
                }
            }
            lineNumberReader.mark(200);
        } while ((string = lineNumberReader.readLine()) != null && string != null);
        LogHolder.log(7, LogType.TOR, "Exit Nodes : " + this.m_exitnodes.size() + " Non-Exit Nodes : " + this.m_middlenodes.size());
        this.m_datePublished = date;
        return true;
    }

    static {
        ms_DateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
}

