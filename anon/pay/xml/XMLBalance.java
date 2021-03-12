/*
 * Decompiled with CFR 0.150.
 */
package anon.pay.xml;

import anon.crypto.IMyPrivateKey;
import anon.crypto.XMLSignature;
import anon.pay.PayMessage;
import anon.pay.Transaction;
import anon.util.Base64;
import anon.util.IXMLEncodable;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLBalance
implements IXMLEncodable {
    private static final String DEFAULT_RATE_ENDDATE = "3000-01-01 00:00:00.00000000";
    private static final String DEFAULT_RATE_STARTDATE = "1970-01-01 00:00:00.00000000";
    private long m_lAccountNumber;
    private Timestamp m_Timestamp;
    private Timestamp m_tStartDate;
    private Timestamp m_ValidTime;
    private long m_lDeposit;
    private boolean m_bBlocked = false;
    private Transaction m_transaction;
    private long m_lSpent;
    private long m_lVolumeBytesMonthly;
    private Timestamp m_flatEnddate;
    private long m_volumeKBytesleft;
    private long m_volumeBytesleft;
    private long m_lOverusageBytes = 0L;
    private Timestamp m_tOverusageDate = null;
    private double m_dFactorOverusageGeneral = 0.0;
    private double m_dFactorOverusageUser = 0.0;
    private String m_message;
    private String m_messageText;
    private URL m_messageLink;
    private Timestamp m_tMonthlyBytesUpdatedOn = new Timestamp(0L);
    private long m_lLastMonthRemainingTraffic;
    private Document m_docTheBalance = null;
    private String m_strAffiliate;

    public XMLBalance(long l, long l2, long l3, Timestamp timestamp, Timestamp timestamp2, long l4, Timestamp timestamp3, IMyPrivateKey iMyPrivateKey, long l5, Timestamp timestamp4, Timestamp timestamp5, long l6, Timestamp timestamp6, double d, double d2, long l7, boolean bl, String string, Transaction transaction) {
        this.m_transaction = transaction;
        this.m_bBlocked = bl;
        this.m_strAffiliate = string;
        this.m_lLastMonthRemainingTraffic = l7;
        this.m_dFactorOverusageGeneral = d;
        this.m_dFactorOverusageUser = d2;
        this.m_lOverusageBytes = l6;
        this.m_tOverusageDate = timestamp6;
        this.m_lDeposit = l2;
        this.m_lSpent = l3;
        this.m_Timestamp = timestamp;
        this.m_tMonthlyBytesUpdatedOn = timestamp4;
        this.m_lVolumeBytesMonthly = l5;
        if (this.m_Timestamp == null) {
            this.m_Timestamp = new Timestamp(System.currentTimeMillis());
        }
        this.m_ValidTime = timestamp2;
        if (this.m_ValidTime == null) {
            this.m_ValidTime = Timestamp.valueOf(DEFAULT_RATE_ENDDATE);
        }
        this.m_lAccountNumber = l;
        this.m_volumeKBytesleft = l4 / 1000L;
        this.m_volumeBytesleft = l4;
        this.m_flatEnddate = timestamp3;
        if (this.m_flatEnddate == null) {
            this.m_flatEnddate = Timestamp.valueOf(DEFAULT_RATE_ENDDATE);
        }
        this.m_tStartDate = timestamp5;
        if (this.m_tStartDate == null) {
            this.m_tStartDate = Timestamp.valueOf(DEFAULT_RATE_STARTDATE);
        }
        this.m_docTheBalance = XMLUtil.createDocument();
        this.m_docTheBalance.appendChild(this.internal_toXmlElement(this.m_docTheBalance));
        if (iMyPrivateKey != null) {
            this.sign(iMyPrivateKey);
        }
    }

    public void sign(IMyPrivateKey iMyPrivateKey) {
        try {
            XMLSignature.sign((Node)this.m_docTheBalance, iMyPrivateKey, 0);
        }
        catch (XMLParseException xMLParseException) {
            LogHolder.log(7, LogType.PAY, "Could not sign XMLBalance");
        }
    }

    public void setMessage(PayMessage payMessage) {
        if (payMessage == null) {
            this.m_message = null;
            this.m_messageLink = null;
            this.m_messageText = null;
        } else {
            this.m_message = payMessage.getShortMessage();
            this.m_messageLink = payMessage.getMessageLink();
            this.m_messageText = payMessage.getMessageText();
        }
        this.m_docTheBalance = XMLUtil.createDocument();
        this.m_docTheBalance.appendChild(this.internal_toXmlElement(this.m_docTheBalance));
    }

    public XMLBalance(Document document) throws Exception {
        this.setValues(document.getDocumentElement());
        this.m_docTheBalance = document;
    }

    public XMLBalance(String string) throws Exception {
        Document document = XMLUtil.toXMLDocument(string);
        this.setValues(document.getDocumentElement());
        this.m_docTheBalance = document;
    }

    public XMLBalance(Element element) throws Exception {
        this.setValues(element);
        this.m_docTheBalance = XMLUtil.createDocument();
        this.m_docTheBalance.appendChild(XMLUtil.importNode(this.m_docTheBalance, element, true));
    }

    private void setValues(Element element) throws Exception {
        block21: {
            boolean bl;
            String string;
            Element element2;
            block20: {
                if (!element.getTagName().equals("Balance") || !element.getAttribute("version").equals("1.0")) {
                    throw new Exception("Balance wrong XML format");
                }
                element2 = (Element)XMLUtil.getFirstChildByName(element, "AccountNumber");
                string = XMLUtil.parseValue((Node)element2, (String)null);
                this.m_lAccountNumber = Long.parseLong(string);
                Node node = XMLUtil.getFirstChildByName(element, "Transaction");
                if (node instanceof Element && node != null) {
                    this.m_transaction = new Transaction((Element)node);
                }
                element2 = (Element)XMLUtil.getFirstChildByName(element, "Blocked");
                this.m_bBlocked = XMLUtil.parseValue((Node)element2, false);
                this.m_strAffiliate = XMLUtil.parseValue(XMLUtil.getFirstChildByName(element, "Affiliate"), null);
                if (this.m_strAffiliate != null && this.m_strAffiliate.endsWith("_null")) {
                    this.m_strAffiliate = null;
                }
                element2 = (Element)XMLUtil.getFirstChildByName(element, "Deposit");
                string = XMLUtil.parseValue((Node)element2, (String)null);
                this.m_lDeposit = Long.parseLong(string);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "Spent");
                string = XMLUtil.parseValue((Node)element2, (String)null);
                this.m_lSpent = Long.parseLong(string);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "BalanceInCent");
                string = XMLUtil.parseValue((Node)element2, "0");
                element2 = (Element)XMLUtil.getFirstChildByName(element, "FlatrateEnddate");
                string = XMLUtil.parseValue((Node)element2, DEFAULT_RATE_ENDDATE);
                this.m_flatEnddate = Timestamp.valueOf(string);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "OverusageBytes");
                if (XMLUtil.parseAttribute((Node)element2, "overusageDate", null) != null) {
                    this.m_tOverusageDate = Timestamp.valueOf(XMLUtil.parseAttribute((Node)element2, "overusageDate", null));
                }
                this.m_lLastMonthRemainingTraffic = XMLUtil.parseAttribute((Node)element2, "lastMonthRemainingTraffic", 0L);
                this.m_dFactorOverusageGeneral = XMLUtil.parseAttribute((Node)element2, "overusageFactorGeneral", 0.0);
                this.m_dFactorOverusageUser = XMLUtil.parseAttribute((Node)element2, "overusageFactorUser", 0.0);
                this.m_lOverusageBytes = XMLUtil.parseValue((Node)element2, 0L);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "StartDate");
                string = XMLUtil.parseValue((Node)element2, DEFAULT_RATE_STARTDATE);
                this.m_tStartDate = Timestamp.valueOf(string);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "VolumeBytesLeft");
                this.m_volumeKBytesleft = XMLUtil.parseValue((Node)element2, 0L);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "BytesLeft");
                this.m_volumeBytesleft = XMLUtil.parseValue((Node)element2, 0L);
                if (this.m_volumeBytesleft == 0L) {
                    this.m_volumeBytesleft = this.m_volumeKBytesleft * 1000L;
                }
                element2 = (Element)XMLUtil.getFirstChildByName(element, "VolumeBytesMonthly");
                this.m_lVolumeBytesMonthly = XMLUtil.parseValue((Node)element2, 0L);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "Timestamp");
                string = XMLUtil.parseValue((Node)element2, (String)null);
                this.m_Timestamp = string != null ? Timestamp.valueOf(string) : new Timestamp(System.currentTimeMillis());
                element2 = (Element)XMLUtil.getFirstChildByName(element, "Validtime");
                string = XMLUtil.parseValue((Node)element2, DEFAULT_RATE_ENDDATE);
                this.m_ValidTime = Timestamp.valueOf(string);
                element2 = (Element)XMLUtil.getFirstChildByName(element, "Message");
                if (element2 != null) {
                    bl = XMLUtil.parseAttribute((Node)element2, "encoded", false);
                    if (bl) {
                        try {
                            string = XMLUtil.parseValue((Node)element2, "");
                            if (!string.equals("")) {
                                this.m_message = Base64.decodeToString(string);
                                break block20;
                            }
                            this.m_message = "";
                        }
                        catch (Exception exception) {
                            LogHolder.log(7, LogType.PAY, "Error while reading message: " + exception + ", message (Base64) was" + string + "decoded message was" + this.m_message);
                        }
                    } else {
                        this.m_message = XMLUtil.parseValue((Node)element2, "");
                    }
                }
            }
            if ((element2 = (Element)XMLUtil.getFirstChildByName(element, "MessageLink")) != null && !(string = XMLUtil.parseValue((Node)element2, "")).equals("")) {
                try {
                    this.m_messageLink = new URL(string);
                }
                catch (MalformedURLException malformedURLException) {
                    LogHolder.log(7, LogType.PAY, "Could not get URL from messagelink string: " + string + ", reason: " + malformedURLException);
                }
            }
            if ((element2 = (Element)XMLUtil.getFirstChildByName(element, "MessageText")) != null) {
                bl = XMLUtil.parseAttribute((Node)element2, "encoded", false);
                if (bl) {
                    try {
                        string = XMLUtil.parseValue((Node)element2, "");
                        if (!string.equals("")) {
                            this.m_messageText = Base64.decodeToString(string);
                            break block21;
                        }
                        this.m_messageText = "";
                    }
                    catch (Exception exception) {
                        LogHolder.log(7, LogType.PAY, "Error while reading message: " + exception + ", message (Base64) was" + string + "decoded message was" + this.m_message);
                    }
                } else {
                    this.m_messageText = XMLUtil.parseValue((Node)element2, "");
                }
            }
        }
    }

    private Element internal_toXmlElement(Document document) {
        String string;
        Element element = document.createElement("Balance");
        element.setAttribute("version", "1.0");
        Element element2 = document.createElement("AccountNumber");
        XMLUtil.setValue((Node)element2, this.m_lAccountNumber);
        element.appendChild(element2);
        if (this.m_transaction != null) {
            element.appendChild(this.m_transaction.toXmlElement(document));
        }
        element2 = document.createElement("Blocked");
        XMLUtil.setValue((Node)element2, this.m_bBlocked);
        element.appendChild(element2);
        if (this.m_strAffiliate != null) {
            element2 = document.createElement("Affiliate");
            XMLUtil.setValue((Node)element2, this.m_strAffiliate);
            element.appendChild(element2);
        }
        element2 = document.createElement("Deposit");
        XMLUtil.setValue((Node)element2, this.m_lDeposit);
        element.appendChild(element2);
        element2 = document.createElement("Spent");
        XMLUtil.setValue((Node)element2, this.m_lSpent);
        element.appendChild(element2);
        element2 = document.createElement("FlatrateEnddate");
        XMLUtil.setValue((Node)element2, this.m_flatEnddate.toString());
        element.appendChild(element2);
        element2 = document.createElement("OverusageBytes");
        XMLUtil.setValue((Node)element2, this.m_lOverusageBytes);
        if (this.m_tOverusageDate != null) {
            XMLUtil.setAttribute(element2, "overusageDate", this.m_tOverusageDate.toString());
        }
        XMLUtil.setAttribute(element2, "lastMonthRemainingTraffic", this.m_lLastMonthRemainingTraffic);
        XMLUtil.setAttribute(element2, "overusageFactorGeneral", this.m_dFactorOverusageGeneral);
        XMLUtil.setAttribute(element2, "overusageFactorUser", this.m_dFactorOverusageUser);
        element.appendChild(element2);
        if (this.m_tStartDate != null) {
            element2 = document.createElement("StartDate");
            XMLUtil.setValue((Node)element2, this.m_tStartDate.toString());
            element.appendChild(element2);
        }
        element2 = document.createElement("VolumeBytesLeft");
        XMLUtil.setValue((Node)element2, this.m_volumeKBytesleft);
        element.appendChild(element2);
        element2 = document.createElement("BytesLeft");
        XMLUtil.setValue((Node)element2, this.m_volumeBytesleft);
        element.appendChild(element2);
        element2 = document.createElement("VolumeBytesMonthly");
        XMLUtil.setValue((Node)element2, this.m_lVolumeBytesMonthly);
        element.appendChild(element2);
        element2 = document.createElement("Timestamp");
        XMLUtil.setValue((Node)element2, this.m_Timestamp.toString());
        element.appendChild(element2);
        element2 = document.createElement("Validtime");
        XMLUtil.setValue((Node)element2, this.m_ValidTime.toString());
        element.appendChild(element2);
        element2 = document.createElement("Message");
        if (this.m_message != null) {
            string = Base64.encodeString(this.m_message);
            XMLUtil.setValue((Node)element2, string);
            XMLUtil.setAttribute(element2, "encoded", true);
        }
        element.appendChild(element2);
        element2 = document.createElement("MessageText");
        if (this.m_messageText != null) {
            string = Base64.encodeString(this.m_messageText);
            XMLUtil.setAttribute(element2, "encoded", true);
            XMLUtil.setValue((Node)element2, string);
        }
        element.appendChild(element2);
        element2 = document.createElement("MessageLink");
        if (this.m_messageLink != null) {
            XMLUtil.setValue((Node)element2, this.m_messageLink.toString());
        }
        element.appendChild(element2);
        return element;
    }

    public long getAccountNumber() {
        return this.m_lAccountNumber;
    }

    public double getOverusageFactorGeneral() {
        return this.m_dFactorOverusageGeneral;
    }

    public double getOverusageFactor() {
        return this.m_dFactorOverusageUser;
    }

    public String getAffiliate() {
        return this.m_strAffiliate;
    }

    public boolean isBlocked() {
        return this.m_bBlocked;
    }

    public long getDeposit() {
        return this.m_lDeposit;
    }

    public Transaction getTransaction() {
        return this.m_transaction;
    }

    public long getSpent() {
        return this.m_lSpent;
    }

    public long getOverusageBytes() {
        return this.m_lOverusageBytes;
    }

    public Timestamp getOverusageDate() {
        return this.m_tOverusageDate;
    }

    public Timestamp getMonthlyBytesUpdatedOn() {
        return this.m_tMonthlyBytesUpdatedOn;
    }

    public long getVolumeBytesMonthly() {
        return this.m_lVolumeBytesMonthly;
    }

    public long getLastMonthRemainingTraffic() {
        return this.m_lLastMonthRemainingTraffic;
    }

    public long getVolumeBytesLeft() {
        return this.m_volumeBytesleft;
    }

    public Timestamp getStartDate() {
        return this.m_tStartDate;
    }

    public Timestamp getFlatEnddate() {
        return this.m_flatEnddate;
    }

    public Timestamp getTimestamp() {
        return this.m_Timestamp;
    }

    public Timestamp getValidTime() {
        return this.m_ValidTime;
    }

    public PayMessage getMessage() {
        if (this.m_message == null || this.m_message.equals("")) {
            return null;
        }
        return new PayMessage(this.m_message, this.m_messageText, this.m_messageLink);
    }

    public MonthlyOverusage calculateMonthlyOverusage(Timestamp timestamp) {
        MonthlyOverusage monthlyOverusage = null;
        if (this.canDoMonthlyOverusage(timestamp)) {
            monthlyOverusage = new MonthlyOverusage();
            monthlyOverusage.m_tEndOfCurrentPeriod = XMLBalance.calculateEndOfCurrentMonthlyPeriod(this.getFlatEnddate(), timestamp);
            monthlyOverusage.m_dFactor = this.getOverusageFactorGeneral();
            monthlyOverusage.m_lAdditionalTraffic = this.getVolumeBytesMonthly() * (long)XMLBalance.calculateRemainingRateMonths(this.getFlatEnddate(), timestamp);
            if (monthlyOverusage.m_lAdditionalTraffic > 0L && this.getLastMonthRemainingTraffic() > 0L) {
                monthlyOverusage.m_lAdditionalTraffic = monthlyOverusage.m_lAdditionalTraffic - this.getVolumeBytesMonthly() + this.getLastMonthRemainingTraffic();
            }
            monthlyOverusage.m_lAdditionalTraffic = (long)((double)monthlyOverusage.m_lAdditionalTraffic / this.getOverusageFactorGeneral());
        } else if (this.isCurrentlyInOverusage(timestamp)) {
            monthlyOverusage = new MonthlyOverusage();
            monthlyOverusage.m_dFactor = this.getOverusageFactor();
            monthlyOverusage.m_lAdditionalTraffic = this.getOverusageBytes();
            monthlyOverusage.m_tEndOfCurrentPeriod = XMLBalance.calculateEndOfCurrentMonthlyPeriod(this.getFlatEnddate(), timestamp);
        }
        return monthlyOverusage;
    }

    public Element toXmlElement(Document document) {
        try {
            return (Element)XMLUtil.importNode(document, this.m_docTheBalance.getDocumentElement(), true);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public boolean isCurrentlyInOverusage(Timestamp timestamp) {
        return this.getOverusageDate() != null && XMLBalance.isSameMonthlyPeriod(this.getOverusageDate(), timestamp, this.getStartDate(), true);
    }

    public boolean canDoMonthlyOverusage(Timestamp timestamp) {
        return this.getVolumeBytesMonthly() > 0L && this.getStartDate() != null && !this.getStartDate().after(timestamp) && XMLBalance.calculateRemainingRateMonths(this.getFlatEnddate(), timestamp) > 0 && (this.getOverusageDate() == null || !XMLBalance.isSameMonthlyPeriod(this.getOverusageDate(), timestamp, this.getStartDate(), true)) && this.getOverusageFactorGeneral() > 1.0 && this.getLastMonthRemainingTraffic() > 0L;
    }

    public static boolean isSameMonthlyPeriod(Timestamp timestamp, Timestamp timestamp2, Timestamp timestamp3, boolean bl) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        GregorianCalendar gregorianCalendar3 = new GregorianCalendar();
        if (timestamp3 == null) {
            return true;
        }
        if (timestamp == null || timestamp2 == null) {
            return false;
        }
        gregorianCalendar.setTime(timestamp);
        gregorianCalendar2.setTime(timestamp2);
        gregorianCalendar3.setTime(timestamp3);
        return XMLBalance.isSameMonthlyPeriod(gregorianCalendar, gregorianCalendar2, gregorianCalendar3, bl);
    }

    private static boolean isSameMonthlyPeriod(Calendar calendar, Calendar calendar2, Calendar calendar3, boolean bl) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(calendar2.getTime());
        ((Calendar)gregorianCalendar).add(2, -1);
        if (bl && calendar.getTime().getTime() > calendar2.getTime().getTime()) {
            return true;
        }
        calendar3.set(1, calendar.get(1));
        calendar3.set(2, calendar.get(2));
        if (calendar.get(2) == calendar2.get(2)) {
            if (calendar.get(1) != calendar2.get(1)) {
                return false;
            }
            if (calendar.get(5) >= calendar3.get(5) && calendar2.get(5) >= calendar3.get(5) || calendar.get(5) < calendar3.get(5) && calendar2.get(5) < calendar3.get(5)) {
                return true;
            }
        } else if (calendar.get(2) == gregorianCalendar.get(2)) {
            if (calendar.get(1) != gregorianCalendar.get(1)) {
                return false;
            }
            if (calendar.get(5) >= calendar3.get(5) && gregorianCalendar.get(5) < calendar3.get(5)) {
                return true;
            }
        }
        return false;
    }

    public static int calculateRemainingRateMonths(Calendar calendar, Calendar calendar2) {
        return XMLBalance.calculateRemainingRateMonths(new Timestamp(calendar.getTime().getTime()), new Timestamp(calendar2.getTime().getTime()));
    }

    public static Timestamp calculateEndOfCurrentMonthlyPeriod(Timestamp timestamp, Timestamp timestamp2) {
        if (timestamp == null || timestamp2 == null) {
            return null;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(timestamp);
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar2.setTime(timestamp2);
        return new Timestamp(XMLBalance.calculateEndOfCurrentMonthlyPeriod(gregorianCalendar, gregorianCalendar2).getTime().getTime());
    }

    public static Calendar calculateEndOfCurrentMonthlyPeriod(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            return null;
        }
        Calendar calendar3 = (Calendar)calendar.clone();
        calendar3.set(1, calendar2.get(1));
        calendar3.set(2, calendar2.get(2));
        if (calendar2.get(5) > calendar3.get(5)) {
            calendar3.add(2, 1);
        }
        return calendar3;
    }

    public static int calculateRemainingRateMonths(Timestamp timestamp, Timestamp timestamp2) {
        if (timestamp == null || timestamp2 == null || timestamp.getTime() < timestamp2.getTime()) {
            return 0;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        GregorianCalendar gregorianCalendar2 = new GregorianCalendar();
        gregorianCalendar.setTime(timestamp);
        gregorianCalendar2.setTime(timestamp2);
        int n = 0;
        if (gregorianCalendar.get(1) != gregorianCalendar2.get(1)) {
            n += 12 * (gregorianCalendar.get(1) - gregorianCalendar2.get(1));
            gregorianCalendar2.set(1, gregorianCalendar.get(1));
        }
        if (gregorianCalendar.get(2) != gregorianCalendar2.get(2)) {
            n += gregorianCalendar.get(2) - gregorianCalendar2.get(2);
            gregorianCalendar2.set(2, gregorianCalendar.get(2));
        }
        if (n > 0 && gregorianCalendar2.get(5) > gregorianCalendar.get(5)) {
            --n;
        }
        return n;
    }

    public static Calendar calculateEndDate(Calendar calendar, int n, int n2) {
        Calendar calendar2 = (Calendar)calendar.clone();
        if (n2 == 5 || n2 == 5) {
            calendar2.add(5, n);
        } else if (n2 == 3) {
            calendar2.add(3, n);
            calendar2.add(5, -1);
        } else if (n2 == 2) {
            calendar2.add(2, n);
            calendar2.add(5, -1);
        } else if (n2 == 1) {
            calendar2.add(1, n);
            calendar2.add(5, -1);
        } else {
            return null;
        }
        calendar2.set(11, 23);
        calendar2.set(12, 59);
        calendar2.set(13, 59);
        calendar2.set(14, 0);
        return calendar2;
    }

    public boolean isLastMonthOfRate(Timestamp timestamp) {
        return XMLBalance.isLastMonthOfRate(this.getFlatEnddate(), timestamp, this.getStartDate());
    }

    public static boolean isLastMonthOfRate(Timestamp timestamp, Timestamp timestamp2, Timestamp timestamp3) {
        if (timestamp.getTime() < timestamp2.getTime()) {
            return false;
        }
        return XMLBalance.isSameMonthlyPeriod(timestamp2, timestamp, timestamp3, false);
    }

    public static boolean isLastMonthOfRate(Calendar calendar, Calendar calendar2, Calendar calendar3) {
        if (calendar.getTime().getTime() < calendar2.getTime().getTime()) {
            return false;
        }
        Calendar calendar4 = (Calendar)calendar.clone();
        Calendar calendar5 = (Calendar)calendar2.clone();
        Calendar calendar6 = (Calendar)calendar3.clone();
        return XMLBalance.isSameMonthlyPeriod(calendar5, calendar4, calendar6, false);
    }

    public class MonthlyOverusage {
        public double m_dFactor;
        public Timestamp m_tEndOfCurrentPeriod;
        public long m_lAdditionalTraffic;
    }
}

