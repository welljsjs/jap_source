/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.net.ftp;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPFile;

public interface FTPFileListParser {
    public FTPFile[] parseFileList(InputStream var1, String var2) throws IOException;

    public FTPFile[] parseFileList(InputStream var1) throws IOException;
}

