/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;
import org.apache.commons.net.ftp.FTPFile;

public interface FTPFileEntryParser {
    public FTPFile parseFTPEntry(String var1);

    public String readNextEntry(BufferedReader var1) throws IOException;

    public Vector preParse(Vector var1);
}

