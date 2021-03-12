/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

import HTTPClient.FilenameMangler;
import HTTPClient.HttpHeaderElement;
import HTTPClient.NVPair;
import HTTPClient.ParseException;
import HTTPClient.Util;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.BitSet;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

public class Codecs {
    private static BitSet BoundChar;
    private static BitSet EBCDICUnsafeChar;
    private static byte[] Base64EncMap;
    private static byte[] Base64DecMap;
    private static char[] UUEncMap;
    private static byte[] UUDecMap;
    private static final String ContDisp = "\r\nContent-Disposition: form-data; name=\"";
    private static final String FileName = "\"; filename=\"";
    private static final String Boundary = "\r\n-----ieoau._._-2_8_GoodLuck8.3-dskdfJwSJKlrWLr0234324jfLdsjfdAuaoei-----";
    private static NVPair[] dummy;

    private Codecs() {
    }

    public static final String base64Encode(String string) {
        if (string == null) {
            return null;
        }
        byte[] arrby = string.getBytes();
        return new String(Codecs.base64Encode(arrby));
    }

    public static final byte[] base64Encode(byte[] arrby) {
        int n;
        if (arrby == null) {
            return null;
        }
        byte[] arrby2 = new byte[(arrby.length + 2) / 3 * 4];
        int n2 = 0;
        for (n = 0; n < arrby.length - 2; n += 3) {
            arrby2[n2++] = Base64EncMap[arrby[n] >>> 2 & 0x3F];
            arrby2[n2++] = Base64EncMap[arrby[n + 1] >>> 4 & 0xF | arrby[n] << 4 & 0x3F];
            arrby2[n2++] = Base64EncMap[arrby[n + 2] >>> 6 & 3 | arrby[n + 1] << 2 & 0x3F];
            arrby2[n2++] = Base64EncMap[arrby[n + 2] & 0x3F];
        }
        if (n < arrby.length) {
            arrby2[n2++] = Base64EncMap[arrby[n] >>> 2 & 0x3F];
            if (n < arrby.length - 1) {
                arrby2[n2++] = Base64EncMap[arrby[n + 1] >>> 4 & 0xF | arrby[n] << 4 & 0x3F];
                arrby2[n2++] = Base64EncMap[arrby[n + 1] << 2 & 0x3F];
            } else {
                arrby2[n2++] = Base64EncMap[arrby[n] << 4 & 0x3F];
            }
        }
        while (n2 < arrby2.length) {
            arrby2[n2] = 61;
            ++n2;
        }
        return arrby2;
    }

    public static final String base64Decode(String string) {
        if (string == null) {
            return null;
        }
        byte[] arrby = string.getBytes();
        return new String(Codecs.base64Decode(arrby));
    }

    public static final byte[] base64Decode(byte[] arrby) {
        int n;
        int n2;
        if (arrby == null) {
            return null;
        }
        int n3 = arrby.length;
        while (arrby[n3 - 1] == 61) {
            --n3;
        }
        byte[] arrby2 = new byte[n3 - arrby.length / 4];
        for (n2 = 0; n2 < arrby.length; ++n2) {
            arrby[n2] = Base64DecMap[arrby[n2]];
        }
        n2 = 0;
        for (n = 0; n < arrby2.length - 2; n += 3) {
            arrby2[n] = (byte)(arrby[n2] << 2 & 0xFF | arrby[n2 + 1] >>> 4 & 3);
            arrby2[n + 1] = (byte)(arrby[n2 + 1] << 4 & 0xFF | arrby[n2 + 2] >>> 2 & 0xF);
            arrby2[n + 2] = (byte)(arrby[n2 + 2] << 6 & 0xFF | arrby[n2 + 3] & 0x3F);
            n2 += 4;
        }
        if (n < arrby2.length) {
            arrby2[n] = (byte)(arrby[n2] << 2 & 0xFF | arrby[n2 + 1] >>> 4 & 3);
        }
        if (++n < arrby2.length) {
            arrby2[n] = (byte)(arrby[n2 + 1] << 4 & 0xFF | arrby[n2 + 2] >>> 2 & 0xF);
        }
        return arrby2;
    }

    public static final char[] uuencode(byte[] arrby) {
        int n;
        if (arrby == null) {
            return null;
        }
        if (arrby.length == 0) {
            return new char[0];
        }
        int n2 = 45;
        char[] arrc = System.getProperty("line.separator", "\n").toCharArray();
        char[] arrc2 = new char[(arrby.length + 2) / 3 * 4 + (arrby.length + n2 - 1) / n2 * (arrc.length + 1)];
        int n3 = 0;
        int n4 = 0;
        while (n3 + n2 < arrby.length) {
            arrc2[n4++] = UUEncMap[n2];
            n = n3 + n2;
            while (n3 < n) {
                arrc2[n4++] = UUEncMap[arrby[n3] >>> 2 & 0x3F];
                arrc2[n4++] = UUEncMap[arrby[n3 + 1] >>> 4 & 0xF | arrby[n3] << 4 & 0x3F];
                arrc2[n4++] = UUEncMap[arrby[n3 + 2] >>> 6 & 3 | arrby[n3 + 1] << 2 & 0x3F];
                arrc2[n4++] = UUEncMap[arrby[n3 + 2] & 0x3F];
                n3 += 3;
            }
            for (n = 0; n < arrc.length; ++n) {
                arrc2[n4++] = arrc[n];
            }
        }
        arrc2[n4++] = UUEncMap[arrby.length - n3];
        while (n3 + 2 < arrby.length) {
            arrc2[n4++] = UUEncMap[arrby[n3] >>> 2 & 0x3F];
            arrc2[n4++] = UUEncMap[arrby[n3 + 1] >>> 4 & 0xF | arrby[n3] << 4 & 0x3F];
            arrc2[n4++] = UUEncMap[arrby[n3 + 2] >>> 6 & 3 | arrby[n3 + 1] << 2 & 0x3F];
            arrc2[n4++] = UUEncMap[arrby[n3 + 2] & 0x3F];
            n3 += 3;
        }
        if (n3 < arrby.length - 1) {
            arrc2[n4++] = UUEncMap[arrby[n3] >>> 2 & 0x3F];
            arrc2[n4++] = UUEncMap[arrby[n3 + 1] >>> 4 & 0xF | arrby[n3] << 4 & 0x3F];
            arrc2[n4++] = UUEncMap[arrby[n3 + 1] << 2 & 0x3F];
            arrc2[n4++] = UUEncMap[0];
        } else if (n3 < arrby.length) {
            arrc2[n4++] = UUEncMap[arrby[n3] >>> 2 & 0x3F];
            arrc2[n4++] = UUEncMap[arrby[n3] << 4 & 0x3F];
            arrc2[n4++] = UUEncMap[0];
            arrc2[n4++] = UUEncMap[0];
        }
        for (n = 0; n < arrc.length; ++n) {
            arrc2[n4++] = arrc[n];
        }
        if (n4 != arrc2.length) {
            throw new Error("Calculated " + arrc2.length + " chars but wrote " + n4 + " chars!");
        }
        return arrc2;
    }

    private static final byte[] uudecode(BufferedReader bufferedReader) throws ParseException, IOException {
        String string;
        while ((string = bufferedReader.readLine()) != null && !string.startsWith("begin ")) {
        }
        if (string == null) {
            throw new ParseException("'begin' line not found");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        stringTokenizer.nextToken();
        try {
            int n = Integer.parseInt(stringTokenizer.nextToken(), 8);
        }
        catch (Exception exception) {
            throw new ParseException("Invalid mode on line: " + string);
        }
        try {
            String string2 = stringTokenizer.nextToken();
        }
        catch (NoSuchElementException noSuchElementException) {
            throw new ParseException("No file name found on line: " + string);
        }
        byte[] arrby = new byte[1000];
        int n = 0;
        while ((string = bufferedReader.readLine()) != null && !string.equals("end")) {
            byte[] arrby2 = Codecs.uudecode(string.toCharArray());
            if (n + arrby2.length > arrby.length) {
                arrby = Util.resizeArray(arrby, n + 1000);
            }
            System.arraycopy(arrby2, 0, arrby, n, arrby2.length);
            n += arrby2.length;
        }
        if (string == null) {
            throw new ParseException("'end' line not found");
        }
        return Util.resizeArray(arrby, n);
    }

    public static final byte[] uudecode(char[] arrc) {
        if (arrc == null) {
            return null;
        }
        byte[] arrby = new byte[arrc.length / 4 * 3];
        int n = 0;
        int n2 = 0;
        while (n < arrc.length) {
            byte by;
            byte by2;
            byte by3 = UUDecMap[arrc[n++]];
            int n3 = n2 + by3;
            while (n2 < n3 - 2) {
                by2 = UUDecMap[arrc[n]];
                by = UUDecMap[arrc[n + 1]];
                byte by4 = UUDecMap[arrc[n + 2]];
                byte by5 = UUDecMap[arrc[n + 3]];
                arrby[n2++] = (byte)(by2 << 2 & 0xFF | by >>> 4 & 3);
                arrby[n2++] = (byte)(by << 4 & 0xFF | by4 >>> 2 & 0xF);
                arrby[n2++] = (byte)(by4 << 6 & 0xFF | by5 & 0x3F);
                n += 4;
            }
            if (n2 < n3) {
                by2 = UUDecMap[arrc[n]];
                by = UUDecMap[arrc[n + 1]];
                arrby[n2++] = (byte)(by2 << 2 & 0xFF | by >>> 4 & 3);
            }
            if (n2 < n3) {
                by2 = UUDecMap[arrc[n + 1]];
                by = UUDecMap[arrc[n + 2]];
                arrby[n2++] = (byte)(by2 << 4 & 0xFF | by >>> 2 & 0xF);
            }
            while (n < arrc.length && arrc[n] != '\n' && arrc[n] != '\r') {
                ++n;
            }
            while (n < arrc.length && (arrc[n] == '\n' || arrc[n] == '\r')) {
                ++n;
            }
        }
        return Util.resizeArray(arrby, n2);
    }

    public static final String quotedPrintableEncode(String string) {
        if (string == null) {
            return null;
        }
        char[] arrc = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] arrc2 = System.getProperty("line.separator", "\n").toCharArray();
        char[] arrc3 = new char[(int)((double)string.length() * 1.5)];
        char[] arrc4 = string.toCharArray();
        int n = 0;
        int n2 = 1;
        boolean bl = false;
        int n3 = string.length();
        for (int i = 0; i < n3; ++i) {
            char c = arrc4[i];
            if (c == arrc2[0] && Codecs.match(arrc4, i, arrc2)) {
                if (arrc3[n2 - 1] == ' ') {
                    arrc3[n2 - 1] = 61;
                    arrc3[n2++] = 50;
                    arrc3[n2++] = 48;
                } else if (arrc3[n2 - 1] == '\t') {
                    arrc3[n2 - 1] = 61;
                    arrc3[n2++] = 48;
                    arrc3[n2++] = 57;
                }
                arrc3[n2++] = 13;
                arrc3[n2++] = 10;
                i += arrc2.length - 1;
                n = n2;
            } else if (c > '~' || c < ' ' && c != '\t' || c == '=' || EBCDICUnsafeChar.get(c)) {
                arrc3[n2++] = 61;
                arrc3[n2++] = arrc[(c & 0xF0) >>> 4];
                arrc3[n2++] = arrc[c & 0xF];
            } else {
                arrc3[n2++] = c;
            }
            if (n2 > n + 70) {
                arrc3[n2++] = 61;
                arrc3[n2++] = 13;
                arrc3[n2++] = 10;
                n = n2;
            }
            if (n2 <= arrc3.length - 5) continue;
            arrc3 = Util.resizeArray(arrc3, arrc3.length + 500);
        }
        return String.valueOf(arrc3, 1, n2 - 1);
    }

    private static final boolean match(char[] arrc, int n, char[] arrc2) {
        if (arrc.length < n + arrc2.length) {
            return false;
        }
        for (int i = 1; i < arrc2.length; ++i) {
            if (arrc[n + i] == arrc2[i]) continue;
            return false;
        }
        return true;
    }

    public static final String quotedPrintableDecode(String string) throws ParseException {
        if (string == null) {
            return null;
        }
        char[] arrc = new char[(int)((double)string.length() * 1.1)];
        char[] arrc2 = string.toCharArray();
        char[] arrc3 = System.getProperty("line.separator", "\n").toCharArray();
        int n = 0;
        int n2 = 0;
        int n3 = string.length();
        int n4 = 0;
        while (n4 < n3) {
            int n5;
            char c;
            if ((c = arrc2[n4++]) == '=') {
                if (n4 >= n3 - 1) {
                    throw new ParseException("Premature end of input detected");
                }
                if (arrc2[n4] == '\n' || arrc2[n4] == '\r') {
                    if (arrc2[++n4 - 1] == '\r' && arrc2[n4] == '\n') {
                        ++n4;
                    }
                } else {
                    int n6;
                    int n7 = Character.digit(arrc2[n4], 16);
                    if ((n7 | (n6 = Character.digit(arrc2[n4 + 1], 16))) < 0) {
                        throw new ParseException(new String(arrc2, n4 - 1, 3) + " is an invalid code");
                    }
                    n5 = (char)(n7 << 4 | n6);
                    n4 += 2;
                    arrc[n2++] = n5;
                }
                n = n2;
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && n4 < n3 && arrc2[n4] == '\n') {
                    ++n4;
                }
                for (n5 = 0; n5 < arrc3.length; ++n5) {
                    arrc[n++] = arrc3[n5];
                }
                n2 = n;
            } else {
                arrc[n2++] = c;
                if (c != ' ' && c != '\t') {
                    n = n2;
                }
            }
            if (n2 <= arrc.length - arrc3.length - 2) continue;
            arrc = Util.resizeArray(arrc, arrc.length + 500);
        }
        return new String(arrc, 0, n2);
    }

    public static final String URLEncode(String string) {
        if (string == null) {
            return null;
        }
        return URLEncoder.encode(string);
    }

    public static final String URLDecode(String string) throws ParseException {
        if (string == null) {
            return null;
        }
        char[] arrc = new char[string.length()];
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c == '+') {
                arrc[n++] = 32;
                continue;
            }
            if (c == '%') {
                try {
                    arrc[n++] = (char)Integer.parseInt(string.substring(i + 1, i + 3), 16);
                    i += 2;
                    continue;
                }
                catch (NumberFormatException numberFormatException) {
                    throw new ParseException(string.substring(i, i + 3) + " is an invalid code");
                }
            }
            arrc[n++] = c;
        }
        return String.valueOf(arrc, 0, n);
    }

    public static final NVPair[] mpFormDataDecode(byte[] arrby, String string, String string2) throws IOException, ParseException {
        return Codecs.mpFormDataDecode(arrby, string, string2, null);
    }

    public static final NVPair[] mpFormDataDecode(byte[] arrby, String string, String string2, FilenameMangler filenameMangler) throws IOException, ParseException {
        String string3 = Util.getParameter("boundary", string);
        if (string3 == null) {
            throw new ParseException("'boundary' parameter not found in Content-type: " + string);
        }
        byte[] arrby2 = ("--" + string3 + "\r\n").getBytes();
        byte[] arrby3 = ("\r\n--" + string3 + "\r\n").getBytes();
        byte[] arrby4 = ("\r\n--" + string3 + "--").getBytes();
        int[] arrn = Util.compile_search(arrby2);
        int[] arrn2 = Util.compile_search(arrby3);
        int[] arrn3 = Util.compile_search(arrby4);
        int n = Util.findStr(arrby2, arrn, arrby, 0, arrby.length);
        if (n == -1) {
            throw new ParseException("Starting boundary not found: " + new String(arrby2));
        }
        n += arrby2.length;
        NVPair[] arrnVPair = new NVPair[10];
        boolean bl = false;
        int n2 = 0;
        while (!bl) {
            String string4;
            int n3;
            int n4 = Util.findStr(arrby3, arrn2, arrby, n, arrby.length);
            if (n4 == -1) {
                n4 = Util.findStr(arrby4, arrn3, arrby, n, arrby.length);
                if (n4 == -1) {
                    throw new ParseException("Ending boundary not found: " + new String(arrby4));
                }
                bl = true;
            }
            String string5 = null;
            String string6 = null;
            String string7 = null;
            while ((n3 = Codecs.findEOL(arrby, n) + 2) - 2 > n) {
                byte by;
                String string8 = new String(arrby, n, n3 - 2 - n);
                n = n3;
                while (n3 < arrby.length - 1 && ((by = arrby[n3]) == 32 || by == 9)) {
                    n3 = Codecs.findEOL(arrby, n) + 2;
                    string8 = string8 + new String(arrby, n, n3 - 2 - n);
                    n = n3;
                }
                if (!string8.regionMatches(true, 0, "Content-Disposition", 0, 19)) continue;
                Vector vector = Util.parseHeader(string8.substring(string8.indexOf(58) + 1));
                HttpHeaderElement httpHeaderElement = Util.getElement(vector, "form-data");
                if (httpHeaderElement == null) {
                    throw new ParseException("Expected 'Content-Disposition: form-data' in line: " + string8);
                }
                NVPair[] arrnVPair2 = httpHeaderElement.getParams();
                string6 = null;
                string5 = null;
                for (int i = 0; i < arrnVPair2.length; ++i) {
                    if (arrnVPair2[i].getName().equalsIgnoreCase("name")) {
                        string5 = arrnVPair2[i].getValue();
                    }
                    if (!arrnVPair2[i].getName().equalsIgnoreCase("filename")) continue;
                    string6 = arrnVPair2[i].getValue();
                }
                if (string5 == null) {
                    throw new ParseException("'name' parameter not found in header: " + string8);
                }
                string7 = string8;
            }
            if ((n += 2) > n4) {
                throw new ParseException("End of header not found at offset " + n4);
            }
            if (string7 == null) {
                throw new ParseException("Missing 'Content-Disposition' header at offset " + n);
            }
            if (string6 != null) {
                if (filenameMangler != null) {
                    string6 = filenameMangler.mangleFilename(string6, string5);
                }
                if (string6 != null) {
                    File file = new File(string2, string6);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(arrby, n, n4 - n);
                    fileOutputStream.close();
                }
                string4 = string6;
            } else {
                string4 = new String(arrby, n, n4 - n);
            }
            if (n2 >= arrnVPair.length) {
                arrnVPair = Util.resizeArray(arrnVPair, n2 + 10);
            }
            arrnVPair[n2] = new NVPair(string5, string4);
            n = n4 + arrby3.length;
            ++n2;
        }
        return Util.resizeArray(arrnVPair, n2);
    }

    private static final int findEOL(byte[] arrby, int n) {
        while (n < arrby.length - 1 && (arrby[n++] != 13 || arrby[n] != 10)) {
        }
        return n - 1;
    }

    public static final byte[] mpFormDataEncode(NVPair[] arrnVPair, NVPair[] arrnVPair2, NVPair[] arrnVPair3) throws IOException {
        return Codecs.mpFormDataEncode(arrnVPair, arrnVPair2, arrnVPair3, null);
    }

    public static final byte[] mpFormDataEncode(NVPair[] arrnVPair, NVPair[] arrnVPair2, NVPair[] arrnVPair3, FilenameMangler filenameMangler) throws IOException {
        int n;
        int n2 = 0;
        int n3 = 119;
        byte[] arrby = new byte[74];
        byte[] arrby2 = new byte[40];
        byte[] arrby3 = new byte[13];
        Util.getBytes(ContDisp, arrby2, 0);
        Util.getBytes(FileName, arrby3, 0);
        Util.getBytes(Boundary, arrby, 0);
        if (arrnVPair == null) {
            arrnVPair = dummy;
        }
        if (arrnVPair2 == null) {
            arrnVPair2 = dummy;
        }
        for (n = 0; n < arrnVPair.length; ++n) {
            n2 += n3 + arrnVPair[n].getName().length() + arrnVPair[n].getValue().length();
        }
        for (n = 0; n < arrnVPair2.length; ++n) {
            File file = new File(arrnVPair2[n].getValue());
            String string = file.getName();
            if (filenameMangler != null) {
                string = filenameMangler.mangleFilename(string, arrnVPair2[n].getName());
            }
            if (string == null) continue;
            n2 += n3 + arrnVPair2[n].getName().length() + 13;
            n2 = (int)((long)n2 + ((long)string.length() + file.length()));
        }
        n2 -= 2;
        byte[] arrby4 = new byte[n2 += 78];
        int n4 = 0;
        block2: for (int i = 0x30303030; i != 0x7A7A7A7A; ++i) {
            int n5;
            n4 = 0;
            while (!BoundChar.get(i & 0xFF)) {
                ++i;
            }
            while (!BoundChar.get(i >> 8 & 0xFF)) {
                i += 256;
            }
            while (!BoundChar.get(i >> 16 & 0xFF)) {
                i += 65536;
            }
            while (!BoundChar.get(i >> 24 & 0xFF)) {
                i += 0x1000000;
            }
            arrby[40] = (byte)(i & 0xFF);
            arrby[42] = (byte)(i >> 8 & 0xFF);
            arrby[44] = (byte)(i >> 16 & 0xFF);
            arrby[46] = (byte)(i >> 24 & 0xFF);
            int n6 = 2;
            int[] arrn = Util.compile_search(arrby);
            for (n5 = 0; n5 < arrnVPair.length; ++n5) {
                System.arraycopy(arrby, n6, arrby4, n4, arrby.length - n6);
                n6 = 0;
                System.arraycopy(arrby2, 0, arrby4, n4 += arrby.length - n6, arrby2.length);
                int n7 = arrnVPair[n5].getName().length();
                Util.getBytes(arrnVPair[n5].getName(), n7, arrby4, n4 += arrby2.length);
                if (n7 >= arrby.length && Util.findStr(arrby, arrn, arrby4, n4, n4 + n7) != -1) continue block2;
                n4 += n7;
                arrby4[n4++] = 34;
                arrby4[n4++] = 13;
                arrby4[n4++] = 10;
                arrby4[n4++] = 13;
                arrby4[n4++] = 10;
                int n8 = arrnVPair[n5].getValue().length();
                Util.getBytes(arrnVPair[n5].getValue(), n8, arrby4, n4);
                if (n8 >= arrby.length && Util.findStr(arrby, arrn, arrby4, n4, n4 + n8) != -1) continue block2;
                n4 += n8;
            }
            for (n5 = 0; n5 < arrnVPair2.length; ++n5) {
                File file = new File(arrnVPair2[n5].getValue());
                String string = file.getName();
                if (filenameMangler != null) {
                    string = filenameMangler.mangleFilename(string, arrnVPair2[n5].getName());
                }
                if (string == null) continue;
                System.arraycopy(arrby, n6, arrby4, n4, arrby.length - n6);
                n6 = 0;
                System.arraycopy(arrby2, 0, arrby4, n4 += arrby.length - n6, arrby2.length);
                int n9 = arrnVPair2[n5].getName().length();
                Util.getBytes(arrnVPair2[n5].getName(), arrby4, n4 += arrby2.length);
                if (n9 >= arrby.length && Util.findStr(arrby, arrn, arrby4, n4, n4 + n9) != -1) continue block2;
                System.arraycopy(arrby3, 0, arrby4, n4 += n9, arrby3.length);
                n9 = string.length();
                Util.getBytes(string, arrby4, n4 += arrby3.length);
                if (n9 >= arrby.length && Util.findStr(arrby, arrn, arrby4, n4, n4 + n9) != -1) continue block2;
                n4 += n9;
                arrby4[n4++] = 34;
                arrby4[n4++] = 13;
                arrby4[n4++] = 10;
                arrby4[n4++] = 13;
                arrby4[n4++] = 10;
                n9 = (int)file.length();
                int n10 = n4;
                FileInputStream fileInputStream = new FileInputStream(file);
                while (n9 > 0) {
                    int n11 = fileInputStream.read(arrby4, n4, n9);
                    n9 -= n11;
                    n4 += n11;
                }
                if (Util.findStr(arrby, arrn, arrby4, n10, n4) != -1) continue block2;
            }
        }
        System.arraycopy(arrby, 0, arrby4, n4, arrby.length);
        n4 += arrby.length;
        arrby4[n4++] = 45;
        arrby4[n4++] = 45;
        arrby4[n4++] = 13;
        arrby4[n4++] = 10;
        if (n4 != n2) {
            throw new Error("Calculated " + n2 + " bytes but wrote " + n4 + " bytes!");
        }
        arrnVPair3[0] = new NVPair("Content-Type", "multipart/form-data; boundary=" + new String(arrby, 4, 70));
        return arrby4;
    }

    public static final String nv2query(NVPair[] arrnVPair) {
        int n;
        if (arrnVPair == null) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (n = 0; n < arrnVPair.length; ++n) {
            stringBuffer.append(Codecs.URLEncode(arrnVPair[n].getName()) + "=" + Codecs.URLEncode(arrnVPair[n].getValue()) + "&");
        }
        if (n > 0) {
            stringBuffer.setLength(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }

    public static final NVPair[] query2nv(String string) throws ParseException {
        if (string == null) {
            return null;
        }
        int n = -1;
        int n2 = 1;
        while ((n = string.indexOf(38, n + 1)) != -1) {
            ++n2;
        }
        NVPair[] arrnVPair = new NVPair[n2];
        n = 0;
        for (n2 = 0; n2 < arrnVPair.length; ++n2) {
            int n3 = string.indexOf(61, n);
            int n4 = string.indexOf(38, n);
            if (n4 == -1) {
                n4 = string.length();
            }
            if (n3 == -1 || n3 >= n4) {
                throw new ParseException("'=' missing in " + string.substring(n, n4));
            }
            arrnVPair[n2] = new NVPair(Codecs.URLDecode(string.substring(n, n3)), Codecs.URLDecode(string.substring(n3 + 1, n4)));
            n = n4 + 1;
        }
        return arrnVPair;
    }

    public static final byte[] chunkedEncode(byte[] arrby, NVPair[] arrnVPair, boolean bl) {
        return Codecs.chunkedEncode(arrby, 0, arrby == null ? 0 : arrby.length, arrnVPair, bl);
    }

    public static final byte[] chunkedEncode(byte[] arrby, int n, int n2, NVPair[] arrnVPair, boolean bl) {
        if (arrby == null) {
            arrby = new byte[]{};
            n2 = 0;
        }
        if (bl && arrnVPair == null) {
            arrnVPair = new NVPair[]{};
        }
        String string = Integer.toString(n2, 16);
        int n3 = 0;
        if (n2 > 0) {
            n3 += string.length() + 2 + n2 + 2;
        }
        if (bl) {
            n3 += 3;
            for (int i = 0; i < arrnVPair.length; ++i) {
                n3 += arrnVPair[i].getName().length() + 2 + arrnVPair[i].getValue().length() + 2;
            }
            n3 += 2;
        }
        byte[] arrby2 = new byte[n3];
        int n4 = 0;
        if (n2 > 0) {
            Util.getBytes(string, arrby2, n4);
            n4 += string.length();
            arrby2[n4++] = 13;
            arrby2[n4++] = 10;
            System.arraycopy(arrby, n, arrby2, n4, n2);
            n4 += n2;
            arrby2[n4++] = 13;
            arrby2[n4++] = 10;
        }
        if (bl) {
            arrby2[n4++] = 48;
            arrby2[n4++] = 13;
            arrby2[n4++] = 10;
            for (int i = 0; i < arrnVPair.length; ++i) {
                Util.getBytes(arrnVPair[i].getName(), arrby2, n4);
                n4 += arrnVPair[i].getName().length();
                arrby2[n4++] = 58;
                arrby2[n4++] = 32;
                Util.getBytes(arrnVPair[i].getValue(), arrby2, n4);
                n4 += arrnVPair[i].getValue().length();
                arrby2[n4++] = 13;
                arrby2[n4++] = 10;
            }
            arrby2[n4++] = 13;
            arrby2[n4++] = 10;
        }
        if (n4 != arrby2.length) {
            throw new Error("Calculated " + arrby2.length + " bytes but wrote " + n4 + " bytes!");
        }
        return arrby2;
    }

    public static final Object chunkedDecode(InputStream inputStream) throws ParseException, IOException {
        String string;
        int n = Codecs.getChunkLength(inputStream);
        if (n > 0) {
            int n2;
            byte[] arrby = new byte[n];
            for (n2 = 0; n != -1 && n2 < arrby.length; n2 += n) {
                n = inputStream.read(arrby, n2, arrby.length - n2);
            }
            if (n == -1) {
                throw new ParseException("Premature EOF while reading chunk;Expected: " + arrby.length + " Bytes, " + "Received: " + (n2 + 1) + " Bytes");
            }
            inputStream.read();
            inputStream.read();
            return arrby;
        }
        NVPair[] arrnVPair = new NVPair[]{};
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        while ((string = dataInputStream.readLine()) != null && string.length() > 0) {
            int n3 = string.indexOf(58);
            if (n3 == -1) {
                throw new ParseException("Error in Footer format: no ':' found in '" + string + "'");
            }
            arrnVPair = Util.resizeArray(arrnVPair, arrnVPair.length + 1);
            arrnVPair[arrnVPair.length - 1] = new NVPair(string.substring(0, n3).trim(), string.substring(n3 + 1).trim());
        }
        return arrnVPair;
    }

    static final int getChunkLength(InputStream inputStream) throws ParseException, IOException {
        int n;
        int n2;
        byte[] arrby = new byte[8];
        int n3 = 0;
        while ((n2 = inputStream.read()) != 13 && n2 != 10 && n2 != 59 && n3 < arrby.length) {
            arrby[n3++] = (byte)n2;
        }
        if (n2 == 59) {
            while ((n2 = inputStream.read()) != 13 && n2 != 10) {
            }
        }
        if (n2 != 10 && (n2 != 13 || inputStream.read() != 10)) {
            throw new ParseException("Didn't find valid chunk length: " + new String(arrby, 0, n3));
        }
        try {
            n = Integer.parseInt(new String(arrby, 0, n3).trim(), 16);
        }
        catch (NumberFormatException numberFormatException) {
            throw new ParseException("Didn't find valid chunk length: " + new String(arrby, 0, n3));
        }
        return n;
    }

    static {
        int n;
        int n2;
        BoundChar = new BitSet(256);
        for (n2 = 48; n2 <= 57; ++n2) {
            BoundChar.set(n2);
        }
        for (n2 = 65; n2 <= 90; ++n2) {
            BoundChar.set(n2);
        }
        for (n2 = 97; n2 <= 122; ++n2) {
            BoundChar.set(n2);
        }
        BoundChar.set(39);
        BoundChar.set(43);
        BoundChar.set(95);
        BoundChar.set(45);
        BoundChar.set(46);
        EBCDICUnsafeChar = new BitSet(256);
        EBCDICUnsafeChar.set(33);
        EBCDICUnsafeChar.set(34);
        EBCDICUnsafeChar.set(35);
        EBCDICUnsafeChar.set(36);
        EBCDICUnsafeChar.set(64);
        EBCDICUnsafeChar.set(91);
        EBCDICUnsafeChar.set(92);
        EBCDICUnsafeChar.set(93);
        EBCDICUnsafeChar.set(94);
        EBCDICUnsafeChar.set(96);
        EBCDICUnsafeChar.set(123);
        EBCDICUnsafeChar.set(124);
        EBCDICUnsafeChar.set(125);
        EBCDICUnsafeChar.set(126);
        byte[] arrby = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        Base64EncMap = arrby;
        Base64DecMap = new byte[128];
        for (n = 0; n < Base64EncMap.length; ++n) {
            Codecs.Base64DecMap[Codecs.Base64EncMap[n]] = (byte)n;
        }
        UUEncMap = new char[64];
        for (n = 0; n < UUEncMap.length; ++n) {
            Codecs.UUEncMap[n] = (char)(n + 32);
        }
        UUDecMap = new byte[128];
        for (n = 0; n < UUEncMap.length; ++n) {
            Codecs.UUDecMap[Codecs.UUEncMap[n]] = (byte)n;
        }
        dummy = new NVPair[0];
    }
}

