/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.util.IProgressCapsule;
import anon.util.RecursiveFileTool;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import logging.LogHolder;
import logging.LogType;

public class ZipArchiver
extends Observable {
    private ZipFile m_archive;

    public ZipArchiver(ZipFile zipFile) {
        this.m_archive = zipFile;
    }

    public int applyDiff(File file, byte[] arrby) {
        try {
            Object object;
            Object object2;
            ZipFile zipFile = this.m_archive;
            ZipInputStream zipInputStream = null;
            ZipOutputStream zipOutputStream = null;
            ZipEntry zipEntry = null;
            long l = arrby.length;
            Hashtable<String, String> hashtable = new Hashtable<String, String>();
            Enumeration<ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                if (Thread.currentThread().isInterrupted()) {
                    return -1;
                }
                zipEntry = enumeration.nextElement();
                l += zipEntry.getSize();
                hashtable.put(zipEntry.getName(), zipEntry.getName());
            }
            zipInputStream = new ZipInputStream(new ByteArrayInputStream(arrby));
            zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
            zipOutputStream.setLevel(9);
            byte[] arrby2 = new byte[5000];
            long l2 = 0L;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (Thread.currentThread().isInterrupted()) {
                    return -1;
                }
                object2 = new ZipEntry(zipEntry.getName());
                if (!zipEntry.getName().equalsIgnoreCase("META-INF/INDEX.JD")) {
                    LogHolder.log(7, LogType.MISC, "JARDiff: " + zipEntry.getName());
                    hashtable.remove(zipEntry.getName());
                    int n = -1;
                    ((ZipEntry)object2).setTime(zipEntry.getTime());
                    ((ZipEntry)object2).setComment(zipEntry.getComment());
                    ((ZipEntry)object2).setExtra(zipEntry.getExtra());
                    ((ZipEntry)object2).setMethod(zipEntry.getMethod());
                    if (zipEntry.getSize() != -1L) {
                        ((ZipEntry)object2).setSize(zipEntry.getSize());
                    }
                    if (zipEntry.getCrc() != -1L) {
                        ((ZipEntry)object2).setCrc(zipEntry.getCrc());
                    }
                    zipOutputStream.putNextEntry((ZipEntry)object2);
                    while ((n = zipInputStream.read(arrby2, 0, 5000)) != -1) {
                        zipOutputStream.write(arrby2, 0, n);
                        this.notifyAboutChangesInterruptable(l2 += (long)n, l, 1);
                    }
                    zipOutputStream.closeEntry();
                } else {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
                    object = null;
                    while ((object = bufferedReader.readLine()) != null) {
                        if (Thread.currentThread().isInterrupted()) {
                            return -1;
                        }
                        StringTokenizer stringTokenizer = new StringTokenizer((String)object);
                        if (((String)(object = stringTokenizer.nextToken())).equalsIgnoreCase("remove")) {
                            object = stringTokenizer.nextToken();
                            LogHolder.log(7, LogType.MISC, "JARDiff: remove " + (String)object);
                            hashtable.remove(object);
                            continue;
                        }
                        if (((String)object).equalsIgnoreCase("move")) {
                            LogHolder.log(7, LogType.MISC, "JARDiff: move " + stringTokenizer.nextToken());
                            continue;
                        }
                        LogHolder.log(7, LogType.MISC, "JARDiff: unkown: " + (String)object);
                    }
                }
                zipInputStream.closeEntry();
            }
            enumeration = hashtable.elements();
            while (enumeration.hasMoreElements()) {
                object2 = (String)((Object)enumeration.nextElement());
                LogHolder.log(7, LogType.MISC, (String)object2);
                zipEntry = zipFile.getEntry((String)object2);
                ZipEntry zipEntry2 = new ZipEntry(zipEntry.getName());
                zipEntry2.setTime(zipEntry.getTime());
                zipEntry2.setComment(zipEntry.getComment());
                zipEntry2.setExtra(zipEntry.getExtra());
                zipEntry2.setMethod(zipEntry.getMethod());
                if (zipEntry.getSize() != -1L) {
                    zipEntry2.setSize(zipEntry.getSize());
                }
                if (zipEntry.getCrc() != -1L) {
                    zipEntry2.setCrc(zipEntry.getCrc());
                }
                zipOutputStream.putNextEntry(zipEntry2);
                LogHolder.log(7, LogType.MISC, "JARDiff: Getting in..");
                object = zipFile.getInputStream(zipEntry);
                int n = -1;
                LogHolder.log(7, LogType.MISC, "JARDiff: Reading..");
                try {
                    while ((n = ((InputStream)object).read(arrby2, 0, 5000)) != -1) {
                        zipOutputStream.write(arrby2, 0, n);
                        this.notifyAboutChangesInterruptable(l2 += (long)n, l, 1);
                    }
                }
                catch (Exception exception) {
                    LogHolder.log(2, LogType.MISC, exception);
                }
                ((InputStream)object).close();
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            zipOutputStream.flush();
            zipOutputStream.close();
            zipFile.close();
            zipInputStream.close();
        }
        catch (Throwable throwable) {
            LogHolder.log(2, LogType.MISC, throwable);
            return -1;
        }
        return 0;
    }

    public boolean extractSingleEntry(String string, String string2) {
        try {
            ZipEntry zipEntry = this.m_archive.getEntry(string);
            if (zipEntry == null) {
                LogHolder.log(3, LogType.MISC, "Entry " + string + " not found.");
                return false;
            }
            RecursiveFileTool.copySingleFile(this.m_archive.getInputStream(zipEntry), new File(string2));
            return true;
        }
        catch (IOException iOException) {
            LogHolder.log(3, LogType.MISC, "Extracting entry " + string + " failed", iOException);
            return false;
        }
    }

    public boolean extractArchive(String string, String string2) {
        String string3 = string2;
        Enumeration<? extends ZipEntry> enumeration = null;
        Vector<ZipEntry> vector = new Vector<ZipEntry>();
        Vector<String> vector2 = new Vector<String>();
        Vector<Object> vector3 = new Vector<Object>();
        ZipEntry zipEntry = null;
        String string4 = null;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        long l = 0L;
        long l2 = 0L;
        if (this.m_archive == null) {
            LogHolder.log(3, LogType.MISC, "Archive is null");
            return false;
        }
        if (string2 == null) {
            LogHolder.log(3, LogType.MISC, "Error while extracting archive " + this.m_archive.getName() + ": destination address is null");
            return false;
        }
        try {
            File file;
            Object object;
            enumeration = this.m_archive.entries();
            while (enumeration.hasMoreElements() && !Thread.currentThread().isInterrupted()) {
                zipEntry = enumeration.nextElement();
                string4 = zipEntry.getName();
                if (string != null && !string4.startsWith(string)) continue;
                l += zipEntry.getSize();
                if (zipEntry.isDirectory()) {
                    for (n = 0; n < vector2.size() && ((String)vector2.elementAt(n)).compareTo(string4) <= 0; ++n) {
                    }
                    vector2.insertElementAt(string4, n);
                    continue;
                }
                vector.addElement(zipEntry);
            }
            if (vector.size() == 0 && vector2.size() == 0) {
                LogHolder.log(3, LogType.MISC, "No matching files for " + string + " found in archive " + this.m_archive.getName());
                this.notifyAboutChanges(0L, 0L, 3);
                return false;
            }
            Enumeration enumeration2 = vector2.elements();
            while (enumeration2.hasMoreElements() && !Thread.currentThread().isInterrupted()) {
                object = (String)enumeration2.nextElement();
                file = new File(string3 + File.separator + (String)object);
                if (file != null) {
                    if (!file.exists() && !file.mkdir()) {
                        LogHolder.log(3, LogType.MISC, "Error while extracting archive " + this.m_archive.getName() + ": could not create directory " + file.getAbsolutePath());
                        ZipArchiver.extractErrorRollback(vector2, string2);
                        return false;
                    }
                    vector3.addElement(object);
                }
                ++n2;
            }
            this.notifyAboutChangesInterruptable(l2, l, 1);
            enumeration2 = vector.elements();
            while (enumeration2.hasMoreElements() && !Thread.currentThread().isInterrupted()) {
                object = (ZipEntry)enumeration2.nextElement();
                file = new File(string3 + File.separator + ((ZipEntry)object).getName());
                InputStream inputStream = this.m_archive.getInputStream((ZipEntry)object);
                RecursiveFileTool.copySingleFile(inputStream, file);
                vector3.addElement(((ZipEntry)object).getName());
                this.notifyAboutChangesInterruptable(l2 += ((ZipEntry)object).getSize(), l, 1);
                ++n3;
            }
        }
        catch (IllegalStateException illegalStateException) {
            LogHolder.log(3, LogType.MISC, "Cannot extract archive " + this.m_archive.getName() + ": file already closed");
            this.notifyAboutChanges(l2, l, 3);
            return false;
        }
        catch (InterruptedIOException interruptedIOException) {
            LogHolder.log(7, LogType.MISC, "Process of extracting " + this.m_archive.getName() + " cancelled");
            ZipArchiver.extractErrorRollback(vector3, string2);
            this.notifyAboutChanges(l2, l, 2);
            return false;
        }
        catch (InterruptedException interruptedException) {
            LogHolder.log(7, LogType.MISC, "Process of extracting " + this.m_archive.getName() + " cancelled");
            ZipArchiver.extractErrorRollback(vector3, string2);
            this.notifyAboutChanges(l2, l, 2);
            return false;
        }
        catch (Exception exception) {
            LogHolder.log(3, LogType.MISC, "Cannot extract archive " + this.m_archive.getName() + ": error occured: ", exception);
            ZipArchiver.extractErrorRollback(vector3, string2);
            this.notifyAboutChanges(l2, l, 3);
            return false;
        }
        this.notifyAboutChanges(l2, l, 0);
        return true;
    }

    private void notifyAboutChanges(long l, long l2, int n) {
        ZipEvent zipEvent = new ZipEvent(l, l2, n);
        this.setChanged();
        this.notifyObservers(zipEvent);
    }

    private void notifyAboutChangesInterruptable(long l, long l2, int n) throws InterruptedException {
        this.notifyAboutChanges(l, l2, n);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    private static void extractErrorRollback(Vector vector, String string) {
        for (int i = vector.size(); i > 0; --i) {
            File file = new File(string + File.separator + vector.elementAt(i - 1));
            if (!file.exists()) continue;
            String string2 = file.delete() ? " " : " not ";
            LogHolder.log(string2.trim().length() == 0 ? 7 : 3, LogType.MISC, "Rollback: file " + file.getAbsolutePath() + string2 + "successfully deleted");
        }
    }

    public class ZipEvent
    implements IProgressCapsule {
        private int value;
        private int maxValue;
        private int minValue = 0;
        private int status;

        public ZipEvent(long l, long l2, int n) {
            if (l2 > Integer.MAX_VALUE) {
                double d = l;
                double d2 = l2;
                double d3 = d / d2;
                double d4 = d3 * 2.147483647E9;
                this.value = (int)d4;
                this.maxValue = Integer.MAX_VALUE;
            } else {
                this.value = (int)l;
                this.maxValue = (int)l2;
            }
            this.status = n;
        }

        public void reset() {
        }

        public int getMaximum() {
            return this.maxValue;
        }

        public int getMinimum() {
            return this.minValue;
        }

        public int getValue() {
            return this.value;
        }

        public int getStatus() {
            return this.status;
        }

        public String getMessage() {
            return null;
        }
    }
}

