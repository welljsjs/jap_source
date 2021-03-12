/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.ordescription;

import anon.crypto.MyRSAPublicKey;
import anon.crypto.MyRSASignature;
import anon.tor.ordescription.ORAcl;
import anon.tor.util.Base16;
import anon.util.Base64;
import java.io.LineNumberReader;
import java.util.StringTokenizer;
import java.util.Vector;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class ORDescriptor {
    private String m_address;
    private String m_name;
    private String m_fingerprint;
    private boolean m_hibernate;
    private int m_port;
    private int m_portDir;
    private int m_uptime;
    private String m_strSoftware;
    private String m_published;
    private String m_hash;
    private ORAcl m_acl;
    private boolean m_bIsExitNode;
    private MyRSAPublicKey m_onionkey;
    private MyRSAPublicKey m_signingkey;
    private Vector family;

    public ORDescriptor(String string, String string2, int n, String string3) {
        this.m_address = string;
        this.m_name = string2;
        this.m_port = n;
        this.m_portDir = -1;
        this.m_strSoftware = string3;
        this.m_acl = new ORAcl();
        this.m_bIsExitNode = false;
        this.m_uptime = 0;
        this.m_hibernate = false;
        this.family = null;
    }

    public void setPublished(String string) {
        this.m_published = string;
    }

    public String getPublished() {
        return this.m_published;
    }

    public void setFingerprint(String string) {
        this.m_fingerprint = string;
    }

    public String getFingerprint() {
        return this.m_fingerprint;
    }

    public void setHash(String string) {
        this.m_hash = string;
    }

    public String getHash() {
        return this.m_hash;
    }

    public void setUptime(int n) {
        this.m_uptime = n;
    }

    public int getUptime() {
        return this.m_uptime;
    }

    public Vector getFamily() {
        return this.family;
    }

    public void setHibernate(boolean bl) {
        this.m_hibernate = bl;
    }

    public boolean getHibernate() {
        return this.m_hibernate;
    }

    public void setExitNode(boolean bl) {
        this.m_bIsExitNode = bl;
    }

    public void setFamily(Vector vector) {
        this.family = vector;
    }

    public boolean isExitNode() {
        return this.m_bIsExitNode;
    }

    public void setAcl(ORAcl oRAcl) {
        this.m_acl = oRAcl;
    }

    public ORAcl getAcl() {
        return this.m_acl;
    }

    public boolean setOnionKey(byte[] arrby) {
        this.m_onionkey = MyRSAPublicKey.getInstance(arrby);
        return this.m_onionkey != null;
    }

    public MyRSAPublicKey getOnionKey() {
        return this.m_onionkey;
    }

    public boolean setSigningKey(byte[] arrby) {
        this.m_signingkey = MyRSAPublicKey.getInstance(arrby);
        return this.m_signingkey != null;
    }

    public MyRSAPublicKey getSigningKey() {
        return this.m_signingkey;
    }

    public String getAddress() {
        return this.m_address;
    }

    public String getName() {
        return this.m_name;
    }

    public void setDirPort(int n) {
        this.m_portDir = n;
    }

    public int getPort() {
        return this.m_port;
    }

    public int getDirPort() {
        return this.m_portDir;
    }

    public String getSoftware() {
        return this.m_strSoftware;
    }

    public boolean isSimilar(Object object) {
        if (object != null && object instanceof ORDescriptor) {
            ORDescriptor oRDescriptor = (ORDescriptor)object;
            if (this.m_address.equals(oRDescriptor.getAddress()) && this.m_name.equals(oRDescriptor.getName()) && this.m_port == oRDescriptor.getPort()) {
                return true;
            }
            if (oRDescriptor.family != null && this.family != null && oRDescriptor.family.contains(this.m_name) && this.family.contains(oRDescriptor.getName())) {
                return true;
            }
        }
        return false;
    }

    public static ORDescriptor parse(LineNumberReader lineNumberReader) {
        try {
            StringBuffer stringBuffer;
            StringBuffer stringBuffer2 = new StringBuffer();
            String string = lineNumberReader.readLine();
            stringBuffer2.append(string);
            stringBuffer2.append("\n");
            boolean bl = false;
            if (string == null || !string.startsWith("router")) {
                return null;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(string);
            stringTokenizer.nextToken();
            String string2 = stringTokenizer.nextToken();
            String string3 = stringTokenizer.nextToken();
            String string4 = stringTokenizer.nextToken();
            String string5 = stringTokenizer.nextToken();
            String string6 = stringTokenizer.nextToken();
            Vector<String> vector = null;
            byte[] arrby = null;
            byte[] arrby2 = null;
            ORAcl oRAcl = new ORAcl();
            String string7 = "";
            String string8 = "";
            String string9 = "";
            boolean bl2 = false;
            block6: while (true) {
                if ((string = lineNumberReader.readLine()) == null) {
                    return null;
                }
                stringBuffer2.append(string);
                stringBuffer2.append("\n");
                if (string == null) {
                    return null;
                }
                if (string.startsWith("opt ")) {
                    string = string.substring(4);
                }
                if (string.startsWith("platform")) {
                    string7 = string.substring(9);
                    continue;
                }
                if (string.startsWith("published")) {
                    string8 = string.substring(10);
                    continue;
                }
                if (string.startsWith("accept")) {
                    oRAcl.add(string);
                    bl = true;
                    continue;
                }
                if (string.startsWith("reject")) {
                    oRAcl.add(string);
                    continue;
                }
                if (string.startsWith("fingerprint")) {
                    stringBuffer = new StringBuffer();
                    stringTokenizer = new StringTokenizer(string);
                    stringTokenizer.nextToken();
                    while (stringTokenizer.hasMoreTokens()) {
                        stringBuffer.append(stringTokenizer.nextToken());
                    }
                    string9 = stringBuffer.toString();
                    continue;
                }
                if (string.startsWith("hibernate")) {
                    try {
                        if (Integer.parseInt(string.substring(10)) == 1) {
                            bl2 = true;
                            continue;
                        }
                        bl2 = false;
                    }
                    catch (Exception exception) {}
                    continue;
                }
                if (string.startsWith("onion-key")) {
                    stringBuffer = new StringBuffer();
                    string = lineNumberReader.readLine();
                    if (string == null) {
                        return null;
                    }
                    stringBuffer2.append(string);
                    stringBuffer2.append("\n");
                    while (true) {
                        if ((string = lineNumberReader.readLine()) == null) {
                            return null;
                        }
                        stringBuffer2.append(string);
                        stringBuffer2.append("\n");
                        if (string.startsWith("-----END")) {
                            arrby = Base64.decode(stringBuffer.toString());
                            continue block6;
                        }
                        stringBuffer.append(string);
                    }
                }
                if (string.startsWith("signing-key")) {
                    stringBuffer = new StringBuffer();
                    string = lineNumberReader.readLine();
                    if (string == null) {
                        return null;
                    }
                    stringBuffer2.append(string);
                    stringBuffer2.append("\n");
                    while (true) {
                        if ((string = lineNumberReader.readLine()) == null) {
                            return null;
                        }
                        stringBuffer2.append(string);
                        stringBuffer2.append("\n");
                        if (string.startsWith("-----END")) {
                            arrby2 = Base64.decode(stringBuffer.toString());
                            continue block6;
                        }
                        stringBuffer.append(string);
                    }
                }
                if (string.startsWith("family")) {
                    stringTokenizer = new StringTokenizer(string);
                    stringTokenizer.nextToken();
                    vector = new Vector<String>();
                    while (true) {
                        if (!stringTokenizer.hasMoreTokens()) continue block6;
                        vector.addElement(stringTokenizer.nextToken());
                    }
                }
                if (string.startsWith("router-signature")) break;
            }
            stringBuffer = new StringBuffer();
            string = lineNumberReader.readLine();
            if (string == null) {
                return null;
            }
            while (true) {
                if ((string = lineNumberReader.readLine()) == null) {
                    return null;
                }
                if (string.startsWith("-----END")) {
                    ORDescriptor oRDescriptor = new ORDescriptor(string3, string2, Integer.parseInt(string4), string7);
                    if (!oRDescriptor.setOnionKey(arrby) || !oRDescriptor.setSigningKey(arrby2)) {
                        return null;
                    }
                    oRDescriptor.setAcl(oRAcl);
                    oRDescriptor.setExitNode(bl);
                    oRDescriptor.setFamily(vector);
                    oRDescriptor.setPublished(string8);
                    oRDescriptor.setFingerprint(string9);
                    oRDescriptor.setHibernate(bl2);
                    oRDescriptor.setHash(ORDescriptor.calcHash(stringBuffer2.toString()));
                    try {
                        oRDescriptor.setDirPort(Integer.parseInt(string6));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    return oRDescriptor;
                }
                stringBuffer.append(string);
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return "ORRouter: " + this.m_name + " on " + this.m_address + ":" + this.m_port + " Software : " + this.m_strSoftware + " isExitNode:" + this.m_bIsExitNode;
    }

    private static String calcHash(String string) {
        SHA1Digest sHA1Digest = new SHA1Digest();
        byte[] arrby = string.getBytes();
        byte[] arrby2 = new byte[sHA1Digest.getDigestSize()];
        sHA1Digest.update(arrby, 0, arrby.length);
        sHA1Digest.doFinal(arrby2, 0);
        return Base16.encode(arrby2);
    }

    private static boolean checkSignature(byte[] arrby, byte[] arrby2, byte[] arrby3) {
        try {
            MyRSAPublicKey myRSAPublicKey = MyRSAPublicKey.getInstance(arrby3);
            MyRSASignature myRSASignature = new MyRSASignature();
            myRSASignature.initVerify(myRSAPublicKey);
            return myRSASignature.verify(arrby, arrby2);
        }
        catch (Throwable throwable) {
            return false;
        }
    }
}

