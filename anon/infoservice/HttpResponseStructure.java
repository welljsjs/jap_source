/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.util.MyStringBuilder;
import anon.util.XMLUtil;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.w3c.dom.Document;

public final class HttpResponseStructure {
    public static final int HTTP_RETURN_OK = 200;
    public static final int HTTP_RETURN_ACCEPTED = 202;
    public static final int HTTP_RETURN_BAD_REQUEST = 400;
    public static final int HTTP_RETURN_NOT_FOUND = 404;
    public static final int HTTP_RETURN_INTERNAL_SERVER_ERROR = 500;
    public static final int HTTP_TYPE_TEXT_PLAIN = 0;
    public static final int HTTP_TYPE_TEXT_HTML = 1;
    public static final int HTTP_TYPE_TEXT_XML = 2;
    public static final int HTTP_TYPE_APPLICATION_JNLP = 10;
    public static final int HTTP_ENCODING_PLAIN = 0;
    public static final int HTTP_ENCODING_ZLIB = 1;
    public static final int HTTP_ENCODING_GZIP = 2;
    public static final int HTTP_TYPE_NO_TYPE = -1;
    public static final String HTTP_11_STRING = "HTTP/1.1 ";
    public static final String HTTP_CRLF_STRING = "\r\n";
    public static final String HTTP_RETURN_OK_STRING = "200 OK";
    public static final String HTTP_RETURN_ACCEPTED_STRING = "202 Accepted";
    public static final String HTTP_RETURN_BAD_REQUEST_STRING = "400 Bad Request";
    public static final String HTTP_RETURN_NOT_FOUND_STRING = "404 Not Found";
    public static final String HTTP_RETURN_INTERNAL_SERVER_ERROR_STRING = "500 Internal Server Error";
    public static final String HTTP_HEADER_TYPE_STRING = "Content-type: ";
    public static final String HTTP_HEADER_ENCODING_STRING = "Content-Encoding: ";
    public static final String HTTP_HEADER_LENGTH_STRING = "Content-length: ";
    public static final String HTTP_HEADER_DATE_STRING = "Date: ";
    public static final String HTTP_HEADER_EXPIRES_STRING = "Expires: ";
    public static final String HTTP_HEADER_CACHE_CONTROL_STRING = "Cache-Control: ";
    public static final String HTTP_HEADER_PRAGMA_STRING = "Pragma: ";
    public static final String HTTP_HEADER_CACHE_CONTROL_STRINGS = "Cache-Control: no-cache\r\nPragma: no-cache\r\n";
    public static final String HTTP_ENCODING_ZLIB_STRING = "deflate";
    public static final String HTTP_ENCODING_GZIP_STRING = "gzip";
    public static final String HTTP_TYPE_APPLICATION_JNLP_STRING = "application/x-java-jnlp-file";
    public static final String HTTP_TYPE_TEXT_PLAIN_STRING = "text/plain";
    public static final String HTTP_TYPE_TEXT_HTML_STRING = "text/html";
    public static final String HTTP_TYPE_TEXT_XML_STRING = "text/xml";
    public static final String HTML_NOT_FOUND = "<HTML><TITLE>404 File Not Found</TITLE><H1>404 File Not Found</H1><P>File not found on this server.</P></HTML>";
    public static final String HTML_BAD_REQUEST = "<HTML><TITLE>400 Bad Request</TITLE><H1>400 Bad Request</H1><P>Your request has been rejected by the server.</P></HTML>";
    public static final String HTML_INTERNAL_SERVER_ERROR = "<HTML><TITLE>500 Internal Server Error</TITLE><H1>500 Internal Server Error</H1><P>Error while processing the request on the server.</P></HTML>";
    private byte[] m_httpReturnData;

    public HttpResponseStructure(int n) {
        this.m_httpReturnData = n == 200 ? this.createHttpMessage(200, -1, 0, null, false) : (n == 202 ? this.createHttpMessage(202, -1, 0, null, false) : (n == 400 ? this.createHttpMessage(400, 1, 0, HTML_BAD_REQUEST.getBytes(), false) : (n == 404 ? this.createHttpMessage(404, 1, 0, HTML_NOT_FOUND.getBytes(), false) : this.createHttpMessage(500, 1, 0, HTML_INTERNAL_SERVER_ERROR.getBytes(), false))));
    }

    public HttpResponseStructure(Document document) {
        this(document, 0);
    }

    public HttpResponseStructure(Document document, int n) {
        String string = XMLUtil.toString(document);
        if (string == null) {
            this.m_httpReturnData = this.createHttpMessage(500, 1, 0, HTML_INTERNAL_SERVER_ERROR.getBytes(), false);
        } else {
            try {
                this.m_httpReturnData = this.createHttpMessage(200, 2, n, string.getBytes("UTF8"), false);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                this.m_httpReturnData = this.createHttpMessage(200, 2, n, string.getBytes(), false);
            }
        }
    }

    public HttpResponseStructure(int n, int n2, String string) {
        try {
            this.m_httpReturnData = this.createHttpMessage(200, n, n2, string.getBytes("UTF8"), false);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            this.m_httpReturnData = this.createHttpMessage(200, n, n2, string.getBytes(), false);
        }
    }

    public HttpResponseStructure(int n, int n2, byte[] arrby) {
        this.m_httpReturnData = this.createHttpMessage(200, n, n2, arrby, false);
    }

    public HttpResponseStructure(int n, int n2, String string, boolean bl) {
        this.m_httpReturnData = this.createHttpMessage(200, n, n2, string.getBytes(), bl);
    }

    public HttpResponseStructure(int n, String string) {
        this.m_httpReturnData = this.createHttpMessage(n, 1, 0, string.getBytes(), false);
    }

    public byte[] getResponseData() {
        return this.m_httpReturnData;
    }

    private byte[] createHttpMessage(int n, int n2, int n3, byte[] arrby, boolean bl) {
        return this.createHttpMessage(n, n2, n3, arrby, bl, null);
    }

    private byte[] createHttpMessage(int n, int n2, int n3, byte[] arrby, boolean bl, DateFormat dateFormat) {
        MyStringBuilder myStringBuilder = new MyStringBuilder(2048);
        myStringBuilder.append(HTTP_11_STRING);
        if (n == 200) {
            myStringBuilder.append(HTTP_RETURN_OK_STRING);
        } else if (n == 202) {
            myStringBuilder.append(HTTP_RETURN_ACCEPTED_STRING);
        } else if (n == 400) {
            myStringBuilder.append(HTTP_RETURN_BAD_REQUEST_STRING);
        } else if (n == 404) {
            myStringBuilder.append(HTTP_RETURN_NOT_FOUND_STRING);
        } else if (n == 500) {
            myStringBuilder.append(HTTP_RETURN_INTERNAL_SERVER_ERROR_STRING);
        }
        myStringBuilder.append(HTTP_CRLF_STRING);
        if (arrby != null) {
            myStringBuilder.append(HTTP_HEADER_LENGTH_STRING);
            myStringBuilder.append(arrby.length);
            myStringBuilder.append(HTTP_CRLF_STRING);
        }
        if (n2 != -1) {
            myStringBuilder.append(HTTP_HEADER_TYPE_STRING);
            if (n2 == 0) {
                myStringBuilder.append(HTTP_TYPE_TEXT_PLAIN_STRING);
            } else if (n2 == 1) {
                myStringBuilder.append(HTTP_TYPE_TEXT_HTML_STRING);
            } else if (n2 == 2) {
                myStringBuilder.append(HTTP_TYPE_TEXT_XML_STRING);
            } else if (n2 == 10) {
                myStringBuilder.append(HTTP_TYPE_APPLICATION_JNLP_STRING);
            }
            myStringBuilder.append(HTTP_CRLF_STRING);
        }
        if (n3 != 0) {
            myStringBuilder.append(HTTP_HEADER_ENCODING_STRING);
            if (n3 == 1) {
                myStringBuilder.append(HTTP_ENCODING_ZLIB_STRING);
            } else if (n3 == 2) {
                myStringBuilder.append(HTTP_ENCODING_GZIP_STRING);
            }
            myStringBuilder.append(HTTP_CRLF_STRING);
        }
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        String string = dateFormat.format(new Date());
        myStringBuilder.append(HTTP_HEADER_EXPIRES_STRING);
        myStringBuilder.append(string);
        myStringBuilder.append(HTTP_CRLF_STRING);
        myStringBuilder.append(HTTP_HEADER_DATE_STRING);
        myStringBuilder.append(string);
        myStringBuilder.append(HTTP_CRLF_STRING);
        myStringBuilder.append(HTTP_HEADER_CACHE_CONTROL_STRINGS);
        myStringBuilder.append(HTTP_CRLF_STRING);
        byte[] arrby2 = null;
        if (arrby != null && !bl) {
            byte[] arrby3;
            try {
                arrby3 = myStringBuilder.toString().getBytes("UTF8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                arrby3 = myStringBuilder.toString().getBytes();
            }
            arrby2 = new byte[arrby3.length + arrby.length];
            System.arraycopy(arrby3, 0, arrby2, 0, arrby3.length);
            System.arraycopy(arrby, 0, arrby2, arrby3.length, arrby.length);
        } else {
            try {
                arrby2 = myStringBuilder.toString().getBytes("UTF8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                arrby2 = myStringBuilder.toString().getBytes();
            }
        }
        return arrby2;
    }
}

