/*
 * Decompiled with CFR 0.150.
 */
package anon.mixminion.mmrdescription;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;

public class ServerStats {
    private String m_Server = "privacy.outel.org";
    private int m_Port = 80;

    public Vector getWhoIsDown() throws IOException {
        Object object;
        Object object2;
        String string = null;
        Vector<String> vector = new Vector<String>();
        try {
            object2 = new HTTPConnection(this.m_Server, this.m_Port);
            object = ((HTTPConnection)object2).Get("/minion/nlist.txt");
            if (((HTTPResponse)object).getStatusCode() != 200) {
                LogHolder.log(7, LogType.MISC, "There was a problem with fetching the Statistics of the Mixminion-network. ");
                return vector;
            }
            string = ((HTTPResponse)object).getText();
        }
        catch (Throwable throwable) {
            return vector;
        }
        object2 = new LineNumberReader(new StringReader(string));
        object = ((LineNumberReader)object2).readLine();
        ((LineNumberReader)object2).readLine();
        ((LineNumberReader)object2).readLine();
        ((LineNumberReader)object2).readLine();
        ((LineNumberReader)object2).readLine();
        object = ((LineNumberReader)object2).readLine();
        while (((String)object).length() > 5) {
            char c = ((String)object).charAt(26);
            if (c == ' ' || c == '.' || c == '_' || c == '-') {
                String string2 = ((String)object).substring(0, 15);
                vector.addElement(string2.trim());
            }
            object = ((LineNumberReader)object2).readLine();
        }
        return vector;
    }
}

