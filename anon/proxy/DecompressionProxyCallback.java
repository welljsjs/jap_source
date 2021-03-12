/*
 * Decompiled with CFR 0.150.
 */
package anon.proxy;

import anon.proxy.AnonProxyRequest;
import anon.proxy.ProxyCallback;
import anon.proxy.ProxyCallbackBuffer;
import anon.proxy.ProxyCallbackNotProcessableException;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import logging.LogHolder;
import logging.LogType;

public class DecompressionProxyCallback
implements ProxyCallback {
    Hashtable decompressionKits = new Hashtable();
    private static final int FHCRC = 2;
    private static final int FEXTRA = 4;
    private static final int FNAME = 8;
    private static final int FCOMMENT = 16;
    public static final int MAX_DECOMPRESSION_OUTPUT = 10000;

    private int readGZIPHeader(byte[] arrby, int n, int n2) throws DataFormatException, HeaderSplitException {
        if (n2 < 10) {
            throw new HeaderSplitException();
        }
        try {
            int n3 = n;
            int n4 = this.toUnsignedShort(arrby[n3++], arrby[n3++]);
            if (n4 != 35615) {
                throw new DataFormatException("Not in GZIP format");
            }
            if (this.toUnsignedByte(arrby[n3++]) != 8) {
                throw new DataFormatException("Unsupported compression method");
            }
            int n5 = this.toUnsignedByte(arrby[n3++]);
            n3 += 6;
            if ((n5 & 4) == 4) {
                n3 = n3 + this.toUnsignedShort(arrby[n3++], arrby[n3++]);
            }
            if ((n5 & 8) == 8) {
                while (this.toUnsignedShort(arrby[n3++], arrby[n3++]) != 0) {
                }
            }
            if ((n5 & 0x10) == 16) {
                while (this.toUnsignedShort(arrby[n3++], arrby[n3++]) != 0) {
                }
            }
            if ((n5 & 2) == 2) {
                n3 += 2;
            }
            if (n3 > n + n2) {
                throw new HeaderSplitException();
            }
            return n3 - n;
        }
        catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new HeaderSplitException();
        }
    }

    public int toUnsignedShort(byte by, byte by2) {
        return this.toUnsignedByte(by2) << 8 | by;
    }

    public int toUnsignedByte(byte by) {
        return (by < 0 ? 128 : 0) + (0x7F & by);
    }

    public void closeRequest(AnonProxyRequest anonProxyRequest) {
        DecompressionKit decompressionKit = (DecompressionKit)this.decompressionKits.remove(anonProxyRequest);
        if (decompressionKit != null) {
            if (decompressionKit.getGzipInflater() != null) {
                decompressionKit.getGzipInflater().end();
            }
            if (decompressionKit.getZLibInflater() != null) {
                decompressionKit.getZLibInflater().end();
            }
        }
    }

    public int decompress(ProxyCallbackBuffer proxyCallbackBuffer, DecompressionKit decompressionKit, boolean bl, boolean bl2) throws DataFormatException, ArrayIndexOutOfBoundsException {
        Object object;
        Inflater inflater;
        boolean bl3 = bl2;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        Inflater inflater2 = inflater = bl ? decompressionKit.getGzipInflater() : decompressionKit.getZLibInflater();
        if (decompressionKit.headerBytesNotComplete()) {
            object = decompressionKit.completeUnfinishedHeaderBytes();
            proxyCallbackBuffer.injectModificationData((byte[])object);
            bl3 = true;
        }
        if (bl3) {
            try {
                n5 = bl ? this.readGZIPHeader(proxyCallbackBuffer.getChunk(), proxyCallbackBuffer.getModificationStartOffset(), proxyCallbackBuffer.getModificationDataLength()) : 0;
            }
            catch (HeaderSplitException headerSplitException) {
                byte[] arrby = proxyCallbackBuffer.extractModificationData();
                decompressionKit.addHeaderBytes(arrby);
                LogHolder.log(7, LogType.NET, "gzip splitted up between two chunks.");
                return 0;
            }
        }
        Inflater inflater3 = inflater = bl ? decompressionKit.getGzipInflater() : decompressionKit.getZLibInflater();
        if (inflater.needsInput()) {
            n2 = proxyCallbackBuffer.getLeadingDataLength();
            n3 = proxyCallbackBuffer.getTrailingDataLength();
            n4 = decompressionKit.getResult().length - n3 - n2;
            inflater.setInput(proxyCallbackBuffer.getChunk(), proxyCallbackBuffer.getModificationStartOffset() + n5, proxyCallbackBuffer.getModificationDataLength() - n5);
            object = null;
            n = inflater.inflate(decompressionKit.getResult(), n2, n4);
            while (n == n4 && !inflater.needsInput()) {
                if (object == null) {
                    object = new ByteArrayOutputStream();
                    proxyCallbackBuffer.copyLeadingData((ByteArrayOutputStream)object);
                    ((ByteArrayOutputStream)object).write(decompressionKit.getResult(), n2, n4);
                }
                n4 = decompressionKit.getResult().length;
                n = inflater.inflate(decompressionKit.getResult());
                ((ByteArrayOutputStream)object).write(decompressionKit.getResult(), 0, n);
            }
            if (object == null) {
                proxyCallbackBuffer.copyLeadingData(decompressionKit.getResult());
                proxyCallbackBuffer.copyTrailingData(decompressionKit.getResult(), n2 + n);
                proxyCallbackBuffer.setChunk(decompressionKit.getResult());
                proxyCallbackBuffer.setModificationStartOffset(n2 + n);
                proxyCallbackBuffer.setModificationEndOffset(proxyCallbackBuffer.getModificationStartOffset());
                proxyCallbackBuffer.setPayloadLength(proxyCallbackBuffer.getModificationStartOffset() + n3);
            } else {
                proxyCallbackBuffer.copyTrailingData((ByteArrayOutputStream)object);
                byte[] arrby = ((ByteArrayOutputStream)object).toByteArray();
                proxyCallbackBuffer.setChunk(arrby);
                proxyCallbackBuffer.setModificationStartOffset(arrby.length - n3);
                proxyCallbackBuffer.setModificationEndOffset(proxyCallbackBuffer.getModificationStartOffset());
            }
            if (inflater.finished()) {
                LogHolder.log(6, LogType.NET, "finish connection after decompressing.");
                inflater.reset();
                return 2;
            }
        }
        return 2;
    }

    public synchronized int handleDownstreamChunk(AnonProxyRequest anonProxyRequest, ProxyCallbackBuffer proxyCallbackBuffer) throws ProxyCallbackNotProcessableException {
        String[] arrstring;
        if (proxyCallbackBuffer.getModificationStartOffset() < proxyCallbackBuffer.getPayloadLength() && (arrstring = anonProxyRequest.getContentEncodings()) != null) {
            Object object;
            Vector<String> vector = new Vector<String>();
            StringTokenizer stringTokenizer = null;
            for (int i = 0; i < arrstring.length; ++i) {
                stringTokenizer = new StringTokenizer(arrstring[i], "");
                object = null;
                while (stringTokenizer.hasMoreTokens()) {
                    object = stringTokenizer.nextToken();
                    if (((String)object).trim().equals("gzip") || ((String)object).trim().equals("deflate")) {
                        vector.addElement((String)object);
                        continue;
                    }
                    LogHolder.log(4, LogType.NET, "The Content-Encoding " + (String)object + " is not supported.");
                }
            }
            if (vector.size() > 0) {
                String string = null;
                object = null;
                boolean bl = false;
                boolean bl2 = true;
                try {
                    for (int i = 0; i < vector.size(); ++i) {
                        string = (String)vector.elementAt(i);
                        bl2 = string.equals("gzip");
                        object = (DecompressionKit)this.decompressionKits.get(anonProxyRequest);
                        if (object == null) {
                            object = new DecompressionKit();
                            ((DecompressionKit)object).setNewInflater(bl2);
                            ((DecompressionKit)object).setResult(new byte[10000]);
                            this.decompressionKits.put(anonProxyRequest, object);
                            bl = true;
                        }
                        if (this.decompress(proxyCallbackBuffer, (DecompressionKit)object, bl2, bl) != 0) continue;
                        return 0;
                    }
                }
                catch (DataFormatException dataFormatException) {
                    Inflater inflater;
                    if (object != null && (inflater = ((DecompressionKit)object).getInflater(bl2)) != null) {
                        inflater.reset();
                    }
                    LogHolder.log(4, LogType.NET, "compressed data has invalid format.", dataFormatException);
                }
                catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                    Inflater inflater;
                    if (object != null && (inflater = ((DecompressionKit)object).getInflater(bl2)) != null) {
                        inflater.reset();
                    }
                    LogHolder.log(3, LogType.NET, "indexing error occured while decompressing data. Maybe the result buffer is too small?", arrayIndexOutOfBoundsException);
                }
            }
        }
        return 2;
    }

    public int handleUpstreamChunk(AnonProxyRequest anonProxyRequest, ProxyCallbackBuffer proxyCallbackBuffer) throws ProxyCallbackNotProcessableException {
        return 2;
    }

    private class HeaderSplitException
    extends Exception {
        private HeaderSplitException() {
        }
    }

    public static class DecompressionKit {
        private Inflater gzipInflater = null;
        private Inflater zLibInflater = null;
        private ByteArrayOutputStream unfinishedGzipHeader;
        private byte[] result = null;

        public byte[] getResult() {
            return this.result;
        }

        public void setResult(byte[] arrby) {
            this.result = arrby;
        }

        public Inflater getGzipInflater() {
            return this.gzipInflater;
        }

        public void setGzipInflater(Inflater inflater) {
            this.gzipInflater = inflater;
        }

        public Inflater getZLibInflater() {
            return this.zLibInflater;
        }

        public void setZLibInflater(Inflater inflater) {
            this.zLibInflater = inflater;
        }

        public Inflater getInflater(boolean bl) {
            return bl ? this.gzipInflater : this.zLibInflater;
        }

        public void setNewInflater(boolean bl) {
            if (bl) {
                this.setGzipInflater(new Inflater(true));
            } else {
                this.setZLibInflater(new Inflater());
            }
        }

        private void addHeaderBytes(byte[] arrby) {
            this.addHeaderBytes(arrby, 0, arrby.length);
        }

        private void addHeaderBytes(byte[] arrby, int n, int n2) {
            if (this.unfinishedGzipHeader == null) {
                this.unfinishedGzipHeader = new ByteArrayOutputStream();
            }
            this.unfinishedGzipHeader.write(arrby, n, n2);
        }

        private byte[] completeUnfinishedHeaderBytes() {
            byte[] arrby = this.unfinishedGzipHeader.toByteArray();
            this.unfinishedGzipHeader = null;
            return arrby;
        }

        public boolean headerBytesNotComplete() {
            return this.unfinishedGzipHeader != null;
        }
    }
}

