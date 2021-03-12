/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.mmrdescription;

import anon.crypto.MyRandom;
import anon.mixminion.mmrdescription.MMRDescription;
import anon.mixminion.mmrdescription.MMRListFetcher;
import anon.mixminion.mmrdescription.ServerStats;
import anon.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class MMRList {
    private Vector m_mixminionrouters = new Vector();
    private Vector m_exitnodes;
    private Vector m_fragexitnodes = new Vector();
    private Hashtable m_mixminionroutersWithNames;
    private MyRandom m_rand;
    private MMRListFetcher m_mmrlistFetcher;

    public MMRList(MMRListFetcher mMRListFetcher) {
        this.m_exitnodes = new Vector();
        this.m_mixminionroutersWithNames = new Hashtable();
        this.m_mmrlistFetcher = mMRListFetcher;
        this.m_rand = new MyRandom();
    }

    public synchronized int size() {
        return this.m_mixminionrouters.size();
    }

    public synchronized void setFetcher(MMRListFetcher mMRListFetcher) {
        this.m_mmrlistFetcher = mMRListFetcher;
    }

    public synchronized boolean updateList() {
        try {
            byte[] arrby = this.m_mmrlistFetcher.getMMRList();
            if (arrby == null) {
                return false;
            }
            return this.parseDocument(arrby);
        }
        catch (Throwable throwable) {
            LogHolder.log(7, LogType.MISC, "There was a problem with fetching the available MMRouters: " + throwable.getMessage());
            return false;
        }
    }

    public Vector getList() {
        return this.m_mixminionrouters;
    }

    public synchronized MMRDescription getByName(String string) {
        return (MMRDescription)this.m_mixminionroutersWithNames.get(string);
    }

    public synchronized void remove(String string) {
        MMRDescription mMRDescription = this.getByName(string);
        this.m_mixminionrouters.removeElement(mMRDescription);
        this.m_exitnodes.removeElement(mMRDescription);
        this.m_mixminionroutersWithNames.remove(string);
    }

    public synchronized MMRDescription getByRandom(Vector vector) {
        return (MMRDescription)vector.elementAt(this.m_rand.nextInt(vector.size()));
    }

    public synchronized MMRDescription getByRandom() {
        return (MMRDescription)this.m_mixminionrouters.elementAt(this.m_rand.nextInt(this.m_mixminionrouters.size()));
    }

    public synchronized Vector getByRandomWithExit(int n) {
        int n2;
        Vector<MMRDescription> vector = new Vector<MMRDescription>();
        MMRDescription mMRDescription = null;
        boolean bl = true;
        for (n2 = 0; n2 < n - 1; ++n2) {
            bl = true;
            for (int i = 0; bl && i != 10; ++i) {
                mMRDescription = this.getByRandom();
                bl = vector.contains(mMRDescription);
            }
            vector.addElement(mMRDescription);
        }
        bl = true;
        for (n2 = 0; bl && n2 != 10; ++n2) {
            mMRDescription = this.getByRandom(this.m_exitnodes);
            bl = vector.contains(mMRDescription);
        }
        vector.addElement(mMRDescription);
        return vector;
    }

    public synchronized Vector getByRandomWithFrag(int n, int n2) {
        Vector vector = new Vector();
        Vector<MMRDescription> vector2 = null;
        MMRDescription mMRDescription = null;
        MMRDescription mMRDescription2 = null;
        boolean bl = true;
        mMRDescription2 = this.getByRandom(this.m_fragexitnodes);
        for (int i = 0; i < n2; ++i) {
            vector2 = new Vector<MMRDescription>();
            for (int j = 0; j < n - 1; ++j) {
                bl = true;
                while (bl) {
                    mMRDescription = this.getByRandom();
                    bl = vector2.contains(mMRDescription);
                }
                vector2.addElement(mMRDescription);
            }
            vector2.addElement(mMRDescription2);
            vector.addElement(vector2);
        }
        return vector;
    }

    public synchronized MMRDescription getMMRDescription(String string) {
        if (this.m_mixminionroutersWithNames.containsKey(string)) {
            return (MMRDescription)this.m_mixminionroutersWithNames.get(string);
        }
        return null;
    }

    private boolean parseDocument(byte[] arrby) throws Exception {
        Vector<MMRDescription> vector = new Vector<MMRDescription>();
        Vector<MMRDescription> vector2 = new Vector<MMRDescription>();
        Vector<MMRDescription> vector3 = new Vector<MMRDescription>();
        Hashtable<String, MMRDescription> hashtable = new Hashtable<String, MMRDescription>();
        LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(arrby)));
        String string = lineNumberReader.readLine();
        ServerStats serverStats = new ServerStats();
        Vector vector4 = serverStats.getWhoIsDown();
        if (string == null) {
            return false;
        }
        while ((string = lineNumberReader.readLine()) != null) {
            if (!string.startsWith("[Server]")) continue;
            MMRDescription mMRDescription = MMRDescription.parse(lineNumberReader);
            if (mMRDescription != null && !vector4.contains(mMRDescription.getName())) {
                boolean bl = true;
                if (hashtable.containsKey(mMRDescription.getName())) {
                    bl = false;
                }
                if (bl) {
                    if (mMRDescription.isExitNode()) {
                        if (mMRDescription.allowsFragmented()) {
                            vector3.addElement(mMRDescription);
                        } else {
                            vector2.addElement(mMRDescription);
                        }
                    }
                    vector.addElement(mMRDescription);
                    hashtable.put(mMRDescription.getName(), mMRDescription);
                }
            }
            LogHolder.log(7, LogType.MISC, "Added: " + mMRDescription);
        }
        this.m_exitnodes = vector2;
        this.m_fragexitnodes = vector3;
        LogHolder.log(7, LogType.MISC, "ExitNodes : " + vector2.size() + "Frag-Exit-Nodes:" + vector3.size());
        this.m_mixminionrouters = vector;
        this.m_mixminionroutersWithNames = hashtable;
        if (hashtable.isEmpty()) {
            System.out.println("Infoservice geht nicht!");
            return false;
        }
        return true;
    }

    public void vectortostring(Vector vector) {
        String string = "";
        for (int i = 0; i < vector.size(); ++i) {
            string = string + ((MMRDescription)vector.elementAt(i)).getName() + ",";
        }
        System.out.println(string);
    }

    public Vector mytesting() {
        MMRDescription mMRDescription = new MMRDescription("localhost", "rinos", 48099, Base64.decode("nLrOnRowaQV/U/1XCUlXicIAIKc="), Base64.decode("MK2+xQEe59Zfwd+7nQ17PCgVBlg="), true, true, "egal", null);
        mMRDescription.setIdentityKey(Base64.decode("MIIBCgKCAQEAs6lIEY4Vz2skNL8SHJKkO5hvfernaBkhO/RnowiyFD/TaHQ1kdxYryaIu3dQ3M03eh+k5VoPiU/sX9+OfmHu0hB4vIqm5c5UtOkigSZOhEBDnZ31OgmfrK0+TaQHqNoF9lgT95QC6KXUgdpbhz2Qklg6qNxPWAbKLlewr6g0RBO51pFM/KK4IF9DMu8jQ8dssmWddPWZcdmQuY77njVr83OcPkpP/T8K+heVdkw7/jmlPAJ+wC2iCgkOtM5NJhk6+8NqOA57P5xXkrcEJkA6qRG9pvYYKsN4lor3asETT+X8mMOEuAkkwBTkRkhovqhQ1WPR0MAHTXUKP1wYAjkB4QIDAQAB"));
        mMRDescription.setPacketKey(Base64.decode("MIIBCgKCAQEA0SiCjybZ/+YsuHG9pgAIFNN0j+xF5ZPu3YI1F9MtgGkYQ7xfSrUJksbXprfo+QjJS5izTLkXQfFlUzViy0DMC7JHufofCh1o3lqryGnmE0S0XVD5Cvvz2OLMyRhINLmytp+CXx3E355EVmDebJNtqVRoZaPdZRnvQ2wkB5I6dhiAmhhzIAQVho4DQFf7+2Riv++1VP097TxAww/2gzdq7Pmv3PDd+TI2djAOMDMZO9ZjeZrCX+B7WGZxIBX/hISi9ck1AYq9ss1F4mAOHStgUFoD/iwcONh9OiLyGUhWdmZDrH4HwTutm8thTgt7l3w6LEnvi3Fg8YqeyAp2ocCMOwIDAQAB"));
        Vector<MMRDescription> vector = new Vector<MMRDescription>();
        vector.addElement(mMRDescription);
        vector.addElement(mMRDescription);
        return vector;
    }
}

