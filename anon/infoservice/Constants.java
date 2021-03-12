/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import java.util.Locale;

public final class Constants {
    public static final String DEFAULT_RESSOURCE_FILENAME = "InfoService.properties";
    public static final String CERTSPATH = "certificates/";
    public static final String CERT_JAPINFOSERVICEMESSAGES = "japinfoservicemessages.cer";
    public static final int MAX_REQUEST_HEADER_SIZE = 10000;
    public static final int REQUEST_METHOD_UNKNOWN = -1;
    public static final int REQUEST_METHOD_GET = 1;
    public static final int REQUEST_METHOD_POST = 2;
    public static final int REQUEST_METHOD_HEAD = 3;
    public static final int MAX_NR_OF_CONCURRENT_CONNECTIONS = 50;
    public static final long TIMEOUT_INFOSERVICE = 900000L;
    public static final long TIMEOUT_MIX = 900000L;
    public static final long TIMEOUT_MIXCASCADE = 900000L;
    public static final long TIMEOUT_STATUS = 480000L;
    public static final long TIMEOUT_PAYMENT_INSTANCE = 900000L;
    public static final long TIMEOUT_TEMPORARY_CASCADE = 600000L;
    public static final int MAX_CASCADE_LENGTH = 3;
    public static final int MIN_CASCADE_LENGTH = 2;
    public static final long TIMEOUT_JAP_FORWARDERS = 900000L;
    public static final int FORWARDING_SERVER_VERIFY_TIMEOUT = 20;
    public static final int COMMUNICATION_TIMEOUT = 30000;
    public static final long ANNOUNCE_PERIOD = 300000L;
    public static final long UPDATE_INFORMATION_ANNOUNCE_PERIOD = 600000L;
    public static final Locale LOCAL_FORMAT = Locale.GERMAN;
}

