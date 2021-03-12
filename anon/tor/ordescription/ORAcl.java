/*
 * Decompiled with CFR 0.150.
 */
package anon.tor.ordescription;

import anon.infoservice.ListenerInterface;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.Vector;

public class ORAcl {
    private Vector m_Constraints = new Vector();

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public void add(String var1_1) throws Exception {
        var2_2 = new StringTokenizer(var1_1);
        var3_3 = var2_2.nextToken();
        var4_4 = false;
        if (var3_3.equals("accept")) {
            var4_4 = true;
        }
        var3_3 = var2_2.nextToken();
        var2_2 = new StringTokenizer(var3_3, ":");
        var5_5 = var2_2.nextToken();
        var6_6 = var2_2.nextToken();
        var7_7 = 65535;
        var8_8 = 0;
        if (var6_6.equals("*")) {
            var7_7 = 0;
            var8_8 = 65535;
        } else {
            var2_2 = new StringTokenizer(var6_6, "-");
            var7_7 = Integer.parseInt(var2_2.nextToken());
            var8_8 = var2_2.hasMoreTokens() != false ? Integer.parseInt(var2_2.nextToken()) : var7_7;
        }
        var9_9 = null;
        var10_10 = null;
        if (var5_5.equals("*")) {
            var9_9 = "0.0.0.0";
            var10_10 = "0.0.0.0";
        } else {
            var2_2 = new StringTokenizer(var5_5, "/");
            var9_9 = var2_2.nextToken();
            if (var2_2.hasMoreElements()) {
                var10_10 = var2_2.nextToken();
                try {
                    var11_11 = Integer.parseInt(var10_10);
                    if (var11_11 < 0) ** GOTO lbl43
                    var10_10 = "";
                    for (var12_13 = 0; var12_13 < 4; ++var12_13) {
                        var10_10 = var11_11 >= 8 ? var10_10 + 255 : (var11_11 == 0 ? var10_10 + 0 : var10_10 + (255 - ((int)Math.pow(2.0, 8 - var11_11) - 1)));
                        var11_11 = Math.max(0, var11_11 - 8);
                        if (var12_13 == 3) continue;
                        var10_10 = var10_10 + ".";
                    }
                }
                catch (NumberFormatException var11_12) {}
            } else {
                var10_10 = "255.255.255.255";
            }
        }
lbl43:
        // 5 sources

        this.m_Constraints.addElement(new AclElement(var4_4, var9_9, var10_10, var7_7, var8_8));
    }

    public boolean isAllowed(String string, int n) {
        if (!ListenerInterface.isValidIP(string)) {
            return false;
        }
        try {
            for (int i = 0; i < this.m_Constraints.size(); ++i) {
                AclElement aclElement = (AclElement)this.m_Constraints.elementAt(i);
                if (!aclElement.isContained(string, n)) continue;
                return aclElement.isAccept();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    public boolean isAllowed(int n) {
        try {
            for (int i = 0; i < this.m_Constraints.size(); ++i) {
                AclElement aclElement = (AclElement)this.m_Constraints.elementAt(i);
                if (!aclElement.isContained(null, n)) continue;
                return aclElement.isAccept();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    private class AclElement {
        byte[] arAdrWithMask;
        byte[] arAdrMask;
        int portLow;
        int portHigh;
        boolean bIsAccept;

        public AclElement(boolean bl, String string, String string2, int n, int n2) throws Exception {
            InetAddress inetAddress = InetAddress.getByName(string);
            this.arAdrWithMask = inetAddress.getAddress();
            inetAddress = InetAddress.getByName(string2);
            this.arAdrMask = inetAddress.getAddress();
            for (int i = 0; i < 4; ++i) {
                int n3 = i;
                this.arAdrWithMask[n3] = (byte)(this.arAdrWithMask[n3] & this.arAdrMask[i]);
            }
            this.portLow = n;
            this.portHigh = n2;
            this.bIsAccept = bl;
        }

        public boolean isContained(String string, int n) throws Exception {
            if (n < this.portLow || n > this.portHigh) {
                return false;
            }
            if (string != null) {
                InetAddress inetAddress = InetAddress.getByName(string);
                byte[] arrby = inetAddress.getAddress();
                for (int i = 0; i < 4; ++i) {
                    if ((arrby[i] & this.arAdrMask[i]) == this.arAdrWithMask[i]) continue;
                    return false;
                }
            }
            return true;
        }

        public boolean isAccept() {
            return this.bIsAccept;
        }
    }
}

