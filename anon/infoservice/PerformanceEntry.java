/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.Constants;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PerformanceEntry
extends AbstractDatabaseEntry
implements IXMLEncodable {
    public static final long WEEK_SEVEN_DAYS_TIMEOUT = 604800000L;
    public static final long ONE_DAY_TIMEOUT = 86400000L;
    private static final double BOUND_ROUNDING = 0.1;
    public static final String XML_ELEMENT_CONTAINER_NAME = "PerformanceInfo";
    public static final String XML_ELEMENT_NAME = "PerformanceEntry";
    private static final String XML_ELEMENT_DATA = "Data";
    private static final String XML_ATTR_ID = "id";
    public static final long LAST_TEST_DATA_TTL = 1200000L;
    private static final int PERFORMANCE_ENTRY_TTL = 3600000;
    public static final int SPEED = 0;
    public static final int DELAY = 1;
    public static final int USERS = 2;
    public static final int PACKETS = 3;
    private static final String[] ATTRIBUTES = new String[]{"Speed", "Delay", "Users", "Packets"};
    public static final int[][] BOUNDARIES = new int[][]{{0, 30, 40, 50, 100, 200, 300, 400, 500, 600, 700, 800}, {500, 750, 1000, 2000, 2500, 3000, 4000, 8000, Integer.MAX_VALUE}, {0}, {0}};
    private String m_strCascadeId;
    private Calendar m_current = Calendar.getInstance();
    private long m_lastUpdate;
    private long m_serial;
    private boolean m_bPassive;
    private long m_lastTestTime;
    private StabilityAttributes m_stabilityAttributes;
    private PerformanceAttributeEntry[][][] m_entries = new PerformanceAttributeEntry[ATTRIBUTES.length][8][24];
    private PerformanceAttributeFloatingTimeEntry[] m_floatingTimeEntries;
    private int[] m_lastTestAverage = new int[4];
    static /* synthetic */ Class class$anon$infoservice$MixCascade;

    public PerformanceEntry(String string) {
        this(string, false);
    }

    public PerformanceEntry(String string, boolean bl) {
        super(Long.MAX_VALUE);
        this.m_strCascadeId = string;
        this.m_lastUpdate = System.currentTimeMillis();
        this.m_serial = System.currentTimeMillis();
        this.m_bPassive = bl;
        this.m_floatingTimeEntries = new PerformanceAttributeFloatingTimeEntry[]{new PerformanceAttributeFloatingTimeEntry(0, !bl), new PerformanceAttributeFloatingTimeEntry(1, !bl), new PerformanceAttributeFloatingTimeEntry(2, !bl), new PerformanceAttributeFloatingTimeEntry(3, !bl)};
    }

    public PerformanceEntry(Element element) throws XMLParseException {
        super(System.currentTimeMillis() + 3600000L);
        this.m_floatingTimeEntries = new PerformanceAttributeFloatingTimeEntry[ATTRIBUTES.length];
        XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
        this.m_strCascadeId = XMLUtil.parseAttribute((Node)element, XML_ATTR_ID, "");
        if (this.m_strCascadeId == "") {
            throw new XMLParseException("PerformanceEntry: invalid id");
        }
        Node node = XMLUtil.getFirstChildByName(element, XML_ELEMENT_DATA);
        if (node == null) {
            throw new XMLParseException("PerformanceEntry: Could not find node Data");
        }
        this.m_current.setTime(new Date(System.currentTimeMillis()));
        Node node2 = XMLUtil.getFirstChildByName(node, ATTRIBUTES[1]);
        this.m_floatingTimeEntries[1] = new PerformanceAttributeFloatingTimeEntry(1, node2);
        Node node3 = XMLUtil.getFirstChildByName(node, ATTRIBUTES[0]);
        this.m_floatingTimeEntries[0] = new PerformanceAttributeFloatingTimeEntry(0, node3);
        Node node4 = XMLUtil.getFirstChildByName(node, "Stability");
        this.m_stabilityAttributes = node4 != null ? new StabilityAttributes((Element)node4) : new StabilityAttributes(0, 0, 0, 0);
        this.m_lastUpdate = System.currentTimeMillis();
        this.m_serial = System.currentTimeMillis();
    }

    public String getId() {
        return this.m_strCascadeId;
    }

    public long getLastUpdate() {
        return this.m_lastUpdate;
    }

    public long getVersionNumber() {
        return this.m_serial;
    }

    public long getLastTestTime() {
        return this.m_lastTestTime;
    }

    public PerformanceAttributeEntry importValue(int n, long l, int n2) {
        return this.addPerformanceAttributeEntry(n, l, n2, true);
    }

    public PerformanceEntry update(PerformanceEntry performanceEntry) {
        boolean bl = false;
        if (!this.m_bPassive) {
            return null;
        }
        for (int i = 0; i < ATTRIBUTES.length; ++i) {
            this.setBound(i, performanceEntry.getBound(i));
            this.setBestBound(i, performanceEntry.getBestBound(i));
            if ((i != 1 || this.getBound(i).getBound() <= 0) && (i == 1 || this.getBound(i).getBound() < 0)) continue;
            bl = true;
        }
        this.setStabilityAttributes(performanceEntry.getStabilityAttributes());
        if (bl) {
            this.m_lastUpdate = System.currentTimeMillis();
        }
        return this;
    }

    public Vector updateHourlyPerformanceAttributeEntries(long l) {
        if (!this.m_bPassive) {
            return null;
        }
        Vector<PerformanceAttributeEntry> vector = new Vector<PerformanceAttributeEntry>();
        for (int i = 0; i < ATTRIBUTES.length; ++i) {
            int n = this.getBound(i).getNotRecoveredBound();
            if (i == 0 && n == Integer.MAX_VALUE) {
                n = -1;
            } else if (i == 1 && n == 0) {
                n = -1;
            }
            PerformanceAttributeEntry performanceAttributeEntry = this.addPerformanceAttributeEntry(i, l, n, false);
            if (performanceAttributeEntry == null) continue;
            vector.addElement(performanceAttributeEntry);
        }
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PerformanceAttributeEntry addPerformanceAttributeEntry(int n, long l, int n2, boolean bl) {
        PerformanceAttributeEntry[][][] arrperformanceAttributeEntry = this.m_entries;
        synchronized (this.m_entries) {
            PerformanceAttributeEntry performanceAttributeEntry = null;
            if (System.currentTimeMillis() - l >= 604800000L) {
                // ** MonitorExit[var6_5] (shouldn't be in output)
                return null;
            }
            if (l > System.currentTimeMillis()) {
                LogHolder.log(4, LogType.MISC, "Performance timestamp has future value and is ignored: " + l + " , current: " + System.currentTimeMillis());
                // ** MonitorExit[var6_5] (shouldn't be in output)
                return null;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(l));
            int n3 = calendar.get(7);
            int n4 = calendar.get(11);
            if (this.m_bPassive) {
                if (n4 > 0) {
                    --n4;
                } else if (n3 == 1) {
                    n3 = 7;
                    n4 = 23;
                } else {
                    --n3;
                    n4 = 23;
                }
                calendar.set(11, n4);
                calendar.set(7, n3);
                l = calendar.getTime().getTime();
            }
            for (int i = n4; i < 24; ++i) {
                if (this.m_entries[n][n3][i] == null || System.currentTimeMillis() - this.m_entries[n][n3][i].getDayTimestamp() <= 86400000L) continue;
                this.m_entries[n][n3][i] = null;
            }
            performanceAttributeEntry = this.m_entries[n][n3][n4];
            if (performanceAttributeEntry == null) {
                this.m_entries[n][n3][n4] = performanceAttributeEntry = new PerformanceAttributeEntry(n, this.m_bPassive);
            } else if (this.m_bPassive) {
                // ** MonitorExit[var6_5] (shouldn't be in output)
                return null;
            }
            PerformanceAttributeEntry performanceAttributeEntry2 = n4 > 0 ? this.m_entries[n][n3][n4 - 1] : (n3 == 1 ? this.m_entries[n][7][23] : this.m_entries[n][n3 - 1][23]);
            performanceAttributeEntry.addValue(l, n2, performanceAttributeEntry2);
            if (bl && !this.m_bPassive) {
                this.m_floatingTimeEntries[n].addValue(l, n2);
            }
            // ** MonitorExit[var6_5] (shouldn't be in output)
            return performanceAttributeEntry;
        }
    }

    public int addData(int n, Hashtable hashtable) {
        if (hashtable.isEmpty()) {
            LogHolder.log(1, LogType.MISC, "Empty performance data!");
            return -1;
        }
        int n2 = 0;
        long l = -1L;
        long l2 = -1L;
        Enumeration<Object> enumeration = hashtable.keys();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
        Util.sort(vector, new Util.LongSortAsc());
        enumeration = vector.elements();
        int n3 = 0;
        while (enumeration.hasMoreElements()) {
            int n4;
            long l3 = (Long)enumeration.nextElement();
            if (this.addPerformanceAttributeEntry(n, l3, n4 = ((Integer)hashtable.get(new Long(l3))).intValue(), false) == null) continue;
            l2 = l3;
            this.m_floatingTimeEntries[n].addValue(l3, n4);
            if (n4 > 0) {
                if (n4 >= Integer.MAX_VALUE) continue;
                if (n2 < 0) {
                    n2 = 0;
                }
                n2 += n4;
                ++n3;
                l = l3;
                continue;
            }
            if (n3 != 0) continue;
            if (n2 == 0) {
                n2 = -1;
            }
            l = l3;
        }
        if (n3 > 0) {
            n2 /= n3;
        }
        if (l >= 0L) {
            this.m_lastTestTime = l;
            this.m_lastTestAverage[n] = n2;
        }
        if (l2 >= 0L) {
            this.m_lastUpdate = l2;
        }
        return n2;
    }

    public int getLastTestAverage(int n) {
        return this.m_lastTestAverage[n];
    }

    public void setStabilityAttributes(StabilityAttributes stabilityAttributes) {
        this.m_stabilityAttributes = stabilityAttributes;
    }

    public void setBound(int n, Bound bound) {
        this.m_floatingTimeEntries[n].setBound(bound);
    }

    public void setBestBound(int n, int n2) {
        this.m_floatingTimeEntries[n].setBestBound(n2);
    }

    public Bound getBound(int n) {
        return this.m_floatingTimeEntries[n].getBound();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public StabilityAttributes getStabilityAttributes() {
        StabilityAttributes stabilityAttributes;
        if (this.m_stabilityAttributes != null) {
            return this.m_stabilityAttributes;
        }
        Hashtable hashtable = this.m_floatingTimeEntries[3].m_Values;
        synchronized (hashtable) {
            Hashtable hashtable2 = this.m_floatingTimeEntries[0].m_Values;
            synchronized (hashtable2) {
                stabilityAttributes = new StabilityAttributes(this.m_floatingTimeEntries[3].m_Values.size(), this.m_floatingTimeEntries[0].m_iUnknown, this.m_floatingTimeEntries[0].m_iErrors, this.m_floatingTimeEntries[3].m_iResets);
            }
        }
        return stabilityAttributes;
    }

    public int getBestBound(int n) {
        return this.m_floatingTimeEntries[n].getBestBound();
    }

    public int getAverage(int n) {
        return this.m_floatingTimeEntries[n].getAverage();
    }

    public String delayToHTML(int n) {
        return this.toHTML(1, "ms", n);
    }

    public String speedToHTML(int n) {
        return this.toHTML(0, "kbit/s", n);
    }

    public String usersToHTML(int n) {
        return this.toHTML(2, "", n);
    }

    private long getDayTimestamp(int n, int n2) {
        long l = -1L;
        for (int i = 0; i < 24 && (this.m_entries[n][n2][i] == null || (l = this.m_entries[n][n2][i].getDayTimestamp()) == -1L); ++i) {
        }
        return l;
    }

    private String toHTML(int n, String string, int n2) {
        Object object;
        int n3;
        MixCascade mixCascade = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = PerformanceEntry.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.m_strCascadeId);
        String string2 = (mixCascade != null ? mixCascade.getName() : "") + "<h2>" + this.m_strCascadeId + "</h2>";
        this.m_current.setTime(new Date(System.currentTimeMillis()));
        int n4 = this.m_current.get(7);
        Calendar calendar = Calendar.getInstance();
        calendar.add(7, -6);
        for (n3 = 1; n3 <= 7; ++n3) {
            object = new SimpleDateFormat("E yyyy-MM-dd");
            String string3 = ((DateFormat)object).format(calendar.getTime());
            string2 = calendar.get(7) == n2 ? string2 + "<b> " + (calendar.get(7) == n4 ? "Today</b> " : string3 + "</b> | ") : string2 + "<a href=\"/values/" + ATTRIBUTES[n].toLowerCase() + "/" + this.m_strCascadeId + "/" + calendar.get(7) + "\">" + (calendar.get(7) == n4 ? "Today</a> " : string3 + "</a> | ");
            calendar.add(7, 1);
        }
        string2 = string2 + "<br /><br /><table width=\"100%\"><tr><th width=\"16%\">Hour</th><th>Average</th><th>Min</th><th>Max</th><th>Bound</th><th>% Std. Deviation</th><th>Err/Try/Total</th><th>Resets</th></tr>";
        for (n3 = 0; n3 < 24; ++n3) {
            string2 = string2 + "<tr><td CLASS=\"name\">" + n3 + ":00 - " + (n3 + 1) % 24 + ":00</td>";
            object = this.m_entries[n][n2][n3];
            long l = 0L;
            if (object != null) {
                l = ((PerformanceAttributeEntry)object).getDayTimestamp();
            }
            if (object == null || System.currentTimeMillis() - l >= 604800000L) {
                string2 = string2 + "<td colspan=\"7\" align=\"center\">No data available</td>";
            } else {
                int n5;
                NumberFormat numberFormat = NumberFormat.getInstance(Constants.LOCAL_FORMAT);
                numberFormat.setMaximumFractionDigits(2);
                numberFormat.setMinimumFractionDigits(2);
                string2 = string2 + "<td>" + ((PerformanceAttributeEntry)object).getAverageValue() + " " + string + "</td>" + "<td>" + ((PerformanceAttributeEntry)object).getMinValue() + " " + string + "</td>" + "<td>" + ((PerformanceAttributeEntry)object).getMaxValue() + " " + string + "</td>" + "<td>";
                int n6 = n5 = object == null ? -1 : ((PerformanceAttributeEntry)object).getBound();
                if (n == 1) {
                    string2 = n5 == Integer.MAX_VALUE ? string2 + "> " + BOUNDARIES[1][BOUNDARIES[1].length - 2] : (n5 <= 0 ? string2 + "?" : string2 + n5);
                } else if (n == 0) {
                    string2 = n5 == 0 ? string2 + "< " + BOUNDARIES[0][1] : (n5 < 0 || n5 == Integer.MAX_VALUE ? string2 + "?" : string2 + n5);
                }
                string2 = string2 + " " + string + "</td>";
                string2 = object == null || ((PerformanceAttributeEntry)object).getStdDeviation() == -1.0 || ((PerformanceAttributeEntry)object).getAverageValue() == 0 ? string2 + "<td></td>" : string2 + "<td>" + numberFormat.format(100.0 * ((PerformanceAttributeEntry)object).getStdDeviation() / (double)((PerformanceAttributeEntry)object).getAverageValue()) + " %</td>";
                double d = 0.0;
                double d2 = 0.0;
                if (object != null && ((PerformanceAttributeEntry)object).getValueSize() != 0) {
                    d = (double)((PerformanceAttributeEntry)object).getErrors() / (double)((PerformanceAttributeEntry)object).getValueSize() * 100.0;
                    d2 = (double)((PerformanceAttributeEntry)object).getUnknown() / (double)((PerformanceAttributeEntry)object).getValueSize() * 100.0;
                }
                string2 = string2 + "<td>" + ((PerformanceAttributeEntry)object).getErrors() + " / " + ((PerformanceAttributeEntry)object).getUnknown() + " / " + ((PerformanceAttributeEntry)object).getValueSize() + " (" + NumberFormat.getInstance(Constants.LOCAL_FORMAT).format(d) + " % / " + NumberFormat.getInstance(Constants.LOCAL_FORMAT).format(d2) + " %)</td>";
                object = this.m_entries[3][n2][n3];
                string2 = object != null && ((PerformanceAttributeEntry)object).getResets() > 0 ? string2 + "<td>" + ((PerformanceAttributeEntry)object).getResets() + "</td>" : string2 + "<td></td>";
            }
            string2 = string2 + "</tr>";
        }
        string2 = string2 + "</table>";
        return string2;
    }

    public Element toXmlElement(Document document) {
        Element element = document.createElement(XML_ELEMENT_NAME);
        XMLUtil.setAttribute(element, XML_ATTR_ID, this.getId());
        Element element2 = document.createElement(XML_ELEMENT_DATA);
        Element element3 = this.m_floatingTimeEntries[1].toXmlElement(document);
        element2.appendChild(element3);
        Element element4 = this.m_floatingTimeEntries[0].toXmlElement(document);
        element2.appendChild(element4);
        Element element5 = this.getStabilityAttributes().toXmlElement(document);
        element2.appendChild(element5);
        element.appendChild(element2);
        return element;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    public static class Bound {
        private int m_bound;
        private int m_nonRecoveredBound;

        public Bound(int n, int n2) {
            this.m_bound = n;
            this.m_nonRecoveredBound = n2;
        }

        public int getBound() {
            return this.m_bound;
        }

        public int getNotRecoveredBound() {
            return this.m_nonRecoveredBound;
        }
    }

    public static class StabilityAttributes {
        public static final String XML_ELEMENT_NAME = "Stability";
        private static final String XML_ATTR_TOTAL = "total";
        private static final String XML_ATTR_UNKNOWN = "unknown";
        private static final String XML_ATTR_ERRORS = "errors";
        private static final String XML_ATTR_RESETS = "resets";
        private static final String XML_ATTR_BOUND_UNKNOWN = "boundUnknown";
        private static final String XML_ATTR_BOUND_ERRORS = "boundErrors";
        private static final String XML_ATTR_BOUND_RESETS = "boundResets";
        private static final double BOUND = 5.0;
        private int m_iSize;
        private int m_iErrors;
        private int m_iResets;
        private int m_iUnknown;
        private int m_boundUnknown;
        private int m_boundErrors;
        private int m_boundResets;

        private StabilityAttributes(Element element) throws XMLParseException {
            XMLUtil.assertNodeName(element, XML_ELEMENT_NAME);
            this.m_iSize = XMLUtil.parseAttribute((Node)element, XML_ATTR_TOTAL, 0);
            this.m_iUnknown = XMLUtil.parseAttribute((Node)element, XML_ATTR_UNKNOWN, 0);
            this.m_iErrors = XMLUtil.parseAttribute((Node)element, XML_ATTR_ERRORS, 0);
            this.m_iResets = XMLUtil.parseAttribute((Node)element, XML_ATTR_RESETS, 0);
            this.m_boundUnknown = XMLUtil.parseAttribute((Node)element, XML_ATTR_BOUND_UNKNOWN, 0);
            this.m_boundErrors = XMLUtil.parseAttribute((Node)element, XML_ATTR_BOUND_ERRORS, 0);
            this.m_iResets = XMLUtil.parseAttribute((Node)element, XML_ATTR_BOUND_RESETS, 0);
        }

        public StabilityAttributes(int n, int n2, int n3, int n4) {
            this.m_iSize = n;
            this.m_iUnknown = n2;
            this.m_iErrors = n3;
            this.m_iResets = n4;
            if (n == 0) {
                this.m_boundUnknown = 0;
                this.m_boundErrors = 0;
                this.m_boundResets = 0;
                return;
            }
            double d = 100.0 * (double)this.m_iUnknown / (double)this.m_iSize;
            double d2 = 100.0 * (double)n3 / (double)this.m_iSize;
            this.m_boundUnknown = (int)Math.ceil(d / 5.0) * 5;
            this.m_boundErrors = (int)Math.ceil(d2 / 5.0) * 5;
            this.m_boundResets = (int)Math.ceil(100.0 * (double)n4 / (double)n / 5.0) * 5;
        }

        public int getBoundErrors() {
            return this.m_boundErrors;
        }

        public int getBoundResets() {
            return this.m_boundResets;
        }

        public int getBoundUnknown() {
            return this.m_boundUnknown;
        }

        public int getValueSize() {
            return this.m_iSize;
        }

        public Element toXmlElement(Document document) {
            Element element = document.createElement(XML_ELEMENT_NAME);
            XMLUtil.setAttribute(element, XML_ATTR_TOTAL, this.m_iSize);
            XMLUtil.setAttribute(element, XML_ATTR_UNKNOWN, this.m_iUnknown);
            XMLUtil.setAttribute(element, XML_ATTR_ERRORS, this.m_iErrors);
            XMLUtil.setAttribute(element, XML_ATTR_RESETS, this.m_iResets);
            XMLUtil.setAttribute(element, XML_ATTR_BOUND_UNKNOWN, this.m_boundUnknown);
            XMLUtil.setAttribute(element, XML_ATTR_BOUND_ERRORS, this.m_boundErrors);
            XMLUtil.setAttribute(element, XML_ATTR_BOUND_RESETS, this.m_boundResets);
            return element;
        }
    }

    public static class PerformanceAttributeEntry {
        private int m_iLastValue = -1;
        private long m_iLastTimestamp = -1L;
        private int m_lMaxValue = -1;
        private int m_iMinValue = -1;
        private int m_lAverageValue = -1;
        private int m_lBound = -1;
        private double m_lStdDeviation = 0.0;
        private long m_lastUpdate = -1L;
        private Hashtable m_Values = new Hashtable();
        private int m_iErrors = 0;
        private int m_iResets = 0;
        private int m_iUnknown = 0;
        private int m_iSuccess = 0;
        private int m_attribute;
        private boolean m_bPassive;

        private PerformanceAttributeEntry(int n, boolean bl) {
            this.m_attribute = n;
            this.m_bPassive = bl;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void addValue(long l, int n, PerformanceAttributeEntry performanceAttributeEntry) {
            int n2;
            Enumeration enumeration;
            if (System.currentTimeMillis() - l >= 604800000L) {
                return;
            }
            this.m_lastUpdate = l;
            if (n < 0 || !this.m_bPassive && n == Integer.MAX_VALUE) {
                if (n < 0) {
                    ++this.m_iErrors;
                    if (n < -1) {
                        LogHolder.log(4, LogType.MISC, "Got negative performance value (" + n + ") for timestamp " + l + ".");
                    }
                } else if (n == Integer.MAX_VALUE) {
                    ++this.m_iUnknown;
                }
                if (this.m_Values.size() == 0) {
                    this.m_lAverageValue = -1;
                    this.m_iMinValue = -1;
                    this.m_lMaxValue = -1;
                    this.m_lStdDeviation = -1.0;
                    this.m_lBound = -1;
                }
                return;
            }
            this.m_Values.put(new Long(l), new Integer(n));
            ++this.m_iSuccess;
            if (this.m_attribute == 3) {
                if (this.m_iLastTimestamp < 0L && performanceAttributeEntry != null) {
                    this.m_iLastTimestamp = performanceAttributeEntry.m_iLastTimestamp;
                    this.m_iLastValue = performanceAttributeEntry.m_iLastValue;
                }
                if (this.m_iLastTimestamp < l) {
                    if (n < this.m_iLastValue) {
                        ++this.m_iResets;
                    }
                    this.m_iLastValue = n;
                    this.m_iLastTimestamp = l;
                } else {
                    LogHolder.log(4, LogType.MISC, "Unordered timestamps for hourly attribute " + this.m_attribute + "." + "Timestamp new: " + l + " Timestamp old: " + this.m_iLastTimestamp + " Value: " + n);
                }
            }
            int n3 = 0;
            double d = 0.0;
            Cloneable cloneable = this.m_Values;
            synchronized (cloneable) {
                enumeration = this.m_Values.elements();
                while (enumeration.hasMoreElements()) {
                    n3 += ((Integer)enumeration.nextElement()).intValue();
                }
                this.m_lAverageValue = n3 / this.m_Values.size();
                enumeration = this.m_Values.elements();
                while (enumeration.hasMoreElements()) {
                    d += Math.pow((Integer)enumeration.nextElement() - this.m_lAverageValue, 2.0);
                }
            }
            this.m_lStdDeviation = Math.sqrt(d /= (double)this.m_Values.size());
            if (d < 0.0) {
                LogHolder.log(0, LogType.MISC, "Negative mean square error! " + d);
            }
            if (n < 0) {
                LogHolder.log(3, LogType.MISC, "Negative attribute value! " + n);
            }
            this.m_iMinValue = this.m_iMinValue == 0 || this.m_iMinValue == -1 ? n : Math.min(this.m_iMinValue, n);
            this.m_lMaxValue = Math.max(this.m_lMaxValue, n);
            cloneable = new Vector();
            Hashtable hashtable = this.m_Values;
            synchronized (hashtable) {
                enumeration = this.m_Values.elements();
                while (enumeration.hasMoreElements()) {
                    Integer n4 = (Integer)enumeration.nextElement();
                    if (n4 < 0) continue;
                    ((Vector)cloneable).addElement(n4);
                }
            }
            if (this.m_attribute == 0) {
                Util.sort((Vector)cloneable, new Util.IntegerSortAsc());
            } else {
                Util.sort((Vector)cloneable, new Util.IntegerSortDesc());
            }
            int n5 = (int)Math.floor((double)((Vector)cloneable).size() * 0.1);
            for (n2 = 0; n2 < n5 && ((Vector)cloneable).size() > 1; ++n2) {
                ((Vector)cloneable).removeElementAt(0);
            }
            if (((Vector)cloneable).size() > 0) {
                n2 = (Integer)((Vector)cloneable).elementAt(0);
                if (this.m_attribute == 0) {
                    for (int i = BOUNDARIES[this.m_attribute].length - 1; i >= 0; --i) {
                        if (n2 < BOUNDARIES[this.m_attribute][i]) continue;
                        this.m_lBound = BOUNDARIES[this.m_attribute][i];
                        return;
                    }
                } else {
                    for (int i = 0; i < BOUNDARIES[this.m_attribute].length; ++i) {
                        if (n2 > BOUNDARIES[this.m_attribute][i]) continue;
                        this.m_lBound = BOUNDARIES[this.m_attribute][i];
                        return;
                    }
                    this.m_lBound = BOUNDARIES[this.m_attribute][BOUNDARIES[this.m_attribute].length - 1];
                    return;
                }
                this.m_lBound = BOUNDARIES[this.m_attribute][0];
                return;
            }
            this.m_lBound = -1;
        }

        public int getAverageValue() {
            return this.m_lAverageValue;
        }

        public int getMinValue() {
            return this.m_iMinValue;
        }

        public int getMaxValue() {
            return this.m_lMaxValue;
        }

        public int getBound() {
            return this.m_lBound;
        }

        public double getStdDeviation() {
            return this.m_lStdDeviation;
        }

        public void setErrors(int n) {
            this.m_iErrors = n;
        }

        public int getErrors() {
            return this.m_iErrors;
        }

        public void setResets(int n) {
            this.m_iResets = n;
        }

        public int getResets() {
            return this.m_iResets;
        }

        public int getUnknown() {
            return this.m_iUnknown;
        }

        public void setUnknown(int n) {
            this.m_iUnknown = n;
        }

        public void setSuccess(int n) {
            this.m_iSuccess = n;
        }

        public int getSuccess() {
            return this.m_iSuccess;
        }

        public int getValueSize() {
            return this.getSuccess() + this.m_iErrors + this.m_iUnknown;
        }

        public long getDayTimestamp() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(this.m_lastUpdate));
            return this.m_lastUpdate - (long)(calendar.get(11) * 60 * 60 * 1000) - (long)(calendar.get(12) * 60 * 1000) - (long)(calendar.get(13) * 1000) - (long)calendar.get(14);
        }
    }

    private static class PerformanceAttributeFloatingTimeEntry
    implements IXMLEncodable {
        public static final long DEFAULT_TIMEFRAME = 3600000L;
        public static final String XML_ELEMENT_VALUES = "Values";
        public static final String XML_ELEMENT_VALUE = "Value";
        public static final String XML_ATTR_BEST = "best";
        public static final String XML_ATTR_BOUND = "bound";
        public static final String XML_ATTR_NOT_RECOVERED_BOUND = "notRecovered";
        private int m_iLastValue = -1;
        private long m_iLastTimestamp = -1L;
        public int m_attribute;
        public long m_lastUpdate;
        private Hashtable m_Values = new Hashtable();
        private Bound m_lBoundValue = new Bound(-1, -1);
        private int m_lBestBoundValue = -1;
        private int m_iResets = 0;
        private int m_iErrors = 0;
        private int m_iUnknown = 0;
        private boolean m_bInfoService;

        public PerformanceAttributeFloatingTimeEntry(int n, boolean bl) {
            this.m_attribute = n;
            this.m_bInfoService = bl;
        }

        public PerformanceAttributeFloatingTimeEntry(int n, Node node) {
            this.m_attribute = n;
            this.m_bInfoService = false;
            long l = XMLUtil.parseAttribute(node, XML_ATTR_BOUND, -1L);
            int n2 = l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
            l = XMLUtil.parseAttribute(node, XML_ATTR_NOT_RECOVERED_BOUND, -1L);
            int n3 = l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
            this.m_lBoundValue = new Bound(n2, n3);
            long l2 = XMLUtil.parseAttribute(node, XML_ATTR_BEST, -2L);
            this.m_lBestBoundValue = l2 == -2L ? (n == 0 ? Integer.MAX_VALUE : 0) : (l2 > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l2);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addValue(long l, int n) {
            if (System.currentTimeMillis() - l > 3600000L) {
                return;
            }
            Hashtable hashtable = this.m_Values;
            synchronized (hashtable) {
                Vector<Long> vector = new Vector<Long>();
                boolean bl = false;
                if (n < 0 || n == Integer.MAX_VALUE) {
                    if (n < 0) {
                        ++this.m_iErrors;
                    } else if (n == Integer.MAX_VALUE) {
                        ++this.m_iUnknown;
                    }
                } else if (this.m_attribute == 3) {
                    if (this.m_iLastTimestamp < l) {
                        if (n < this.m_iLastValue) {
                            ++this.m_iResets;
                            bl = true;
                        }
                        this.m_iLastValue = n;
                        this.m_iLastTimestamp = l;
                    } else {
                        LogHolder.log(4, LogType.MISC, "Unordered timestamps for floating PACKETS. Timestamp new: " + l + " Timestamp old: " + this.m_iLastTimestamp + " Value: " + n);
                    }
                    n = bl ? 1 : 0;
                }
                this.m_Values.put(new Long(l), new Integer(n));
                Enumeration enumeration = this.m_Values.keys();
                while (enumeration.hasMoreElements()) {
                    Long l2 = (Long)enumeration.nextElement();
                    if (System.currentTimeMillis() - l2 <= 3600000L) continue;
                    vector.addElement(l2);
                }
                for (int i = 0; i < vector.size(); ++i) {
                    n = (Integer)this.m_Values.get(vector.elementAt(i));
                    if (n < 0 || n == Integer.MAX_VALUE) {
                        if (n < 0) {
                            --this.m_iErrors;
                        } else if (n == Integer.MAX_VALUE) {
                            --this.m_iUnknown;
                        }
                    } else if (this.m_attribute == 3 && n == 1) {
                        --this.m_iResets;
                    }
                    this.m_Values.remove(vector.elementAt(i));
                }
                vector.removeAllElements();
            }
        }

        public void setBound(Bound bound) {
            if (!this.m_bInfoService) {
                this.m_lBoundValue = bound;
            }
        }

        public void setBestBound(int n) {
            if (!this.m_bInfoService) {
                this.m_lBestBoundValue = n;
            }
        }

        public Bound getBound() {
            if (!this.m_bInfoService) {
                return this.m_lBoundValue;
            }
            Vector vector = new Vector();
            Hashtable hashtable = (Hashtable)this.m_Values.clone();
            int n = this.calculateBound(hashtable, vector);
            int n2 = Math.max((int)Math.floor((double)vector.size() * 0.1), 2);
            int n3 = n;
            Util.sort(vector, new Util.LongSortDesc());
            for (int i = 0; i < vector.size(); ++i) {
                int n4;
                int n5 = (Integer)hashtable.get(vector.elementAt(i));
                if (n5 < 0 || n5 == Integer.MAX_VALUE) {
                    ++n2;
                    continue;
                }
                int n6 = n3;
                if (this.m_attribute == 1) {
                    for (n4 = BOUNDARIES[this.m_attribute].length - 1; n4 >= 0; --n4) {
                        if (BOUNDARIES[this.m_attribute][n4] != n3) continue;
                        if (n4 <= 0) break;
                        n6 = BOUNDARIES[this.m_attribute][n4 - 1];
                        break;
                    }
                } else {
                    for (n4 = 0; n4 < BOUNDARIES[this.m_attribute].length; ++n4) {
                        if (BOUNDARIES[this.m_attribute][n4] != n3) continue;
                        if (n4 + 1 >= BOUNDARIES[this.m_attribute].length) break;
                        n6 = BOUNDARIES[this.m_attribute][n4 + 1];
                        break;
                    }
                }
                if (n6 == n3 || (this.m_attribute != 0 || n5 >= n6) && (this.m_attribute != 1 || n5 <= n6)) continue;
                if (i <= n2) break;
                for (n4 = i; n4 < vector.size(); ++n4) {
                    hashtable.remove(vector.elementAt(n4));
                }
                vector.removeAllElements();
                n3 = this.calculateBound(hashtable, vector);
                break;
            }
            return new Bound(n3, n);
        }

        private int calculateBound(Hashtable hashtable, Vector vector) {
            int n;
            int n2 = 0;
            long l = 0L;
            Vector<Integer> vector2 = new Vector<Integer>();
            Enumeration enumeration = hashtable.keys();
            while (enumeration.hasMoreElements()) {
                Long l2 = (Long)enumeration.nextElement();
                if (System.currentTimeMillis() - l2 > 3600000L) continue;
                vector.addElement(l2);
                Integer n3 = (Integer)hashtable.get(l2);
                if (n3 < 0) {
                    ++l;
                    continue;
                }
                if (n3 == Integer.MAX_VALUE) continue;
                ++n2;
                vector2.addElement(n3);
            }
            if (n2 == 0) {
                if (l > 0L) {
                    return -1;
                }
                if (this.m_attribute == 1) {
                    return 0;
                }
                return Integer.MAX_VALUE;
            }
            if (this.m_attribute == 1) {
                Util.sort(vector2, new Util.IntegerSortDesc());
            } else {
                Util.sort(vector2, new Util.IntegerSortAsc());
            }
            int n4 = (int)Math.floor((double)vector2.size() * 0.1);
            for (n = 0; n < n4; ++n) {
                vector2.removeElementAt(0);
            }
            if (vector2.size() > 0) {
                n = (Integer)vector2.elementAt(0);
                return this.getBoundFromValue(n);
            }
            return -1;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int getBestBound() {
            if (!this.m_bInfoService) {
                return this.m_lBestBoundValue;
            }
            int n = 0;
            long l = 0L;
            int n2 = this.m_attribute == 1 ? Integer.MAX_VALUE : 0;
            Hashtable hashtable = this.m_Values;
            synchronized (hashtable) {
                Enumeration enumeration = this.m_Values.keys();
                while (enumeration.hasMoreElements()) {
                    Long l2 = (Long)enumeration.nextElement();
                    if (System.currentTimeMillis() - l2 > 3600000L) continue;
                    Integer n3 = (Integer)this.m_Values.get(l2);
                    if (n3 < 0) {
                        ++l;
                        continue;
                    }
                    if (n3 == Integer.MAX_VALUE) continue;
                    ++n;
                    if (this.m_attribute == 1) {
                        if (n3 >= n2) continue;
                        n2 = n3;
                        continue;
                    }
                    if (n3 <= n2) continue;
                    n2 = n3;
                }
            }
            if (n == 0) {
                if (l > 0L) {
                    return -1;
                }
                if (this.m_attribute == 0) {
                    return Integer.MAX_VALUE;
                }
                return 0;
            }
            return this.getBoundFromValue(n2);
        }

        private int getBoundFromValue(int n) {
            if (this.m_attribute == 1) {
                for (int i = 0; i < BOUNDARIES[this.m_attribute].length; ++i) {
                    if (n > BOUNDARIES[this.m_attribute][i]) continue;
                    return BOUNDARIES[this.m_attribute][i];
                }
                return BOUNDARIES[this.m_attribute][BOUNDARIES[this.m_attribute].length - 1];
            }
            for (int i = BOUNDARIES[this.m_attribute].length - 1; i >= 0; --i) {
                if (n < BOUNDARIES[this.m_attribute][i]) continue;
                return BOUNDARIES[this.m_attribute][i];
            }
            return BOUNDARIES[this.m_attribute][0];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int getAverage() {
            if (!this.m_bInfoService) {
                return -1;
            }
            int n = 0;
            int n2 = 0;
            int n3 = 0;
            long l = 0L;
            Hashtable hashtable = this.m_Values;
            synchronized (hashtable) {
                Enumeration enumeration = this.m_Values.keys();
                while (enumeration.hasMoreElements()) {
                    Long l2 = (Long)enumeration.nextElement();
                    if (System.currentTimeMillis() - l2 > 3600000L) continue;
                    n2 = (Integer)this.m_Values.get(l2);
                    if (n2 < 0) {
                        ++l;
                        continue;
                    }
                    if (n2 == Integer.MAX_VALUE) continue;
                    ++n;
                    n3 += n2;
                }
            }
            if (l > 0L && n == 0) {
                return -1;
            }
            if (n == 0) {
                return 0;
            }
            return n3 / n;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public double getStdDeviation() {
            if (!this.m_bInfoService) {
                return -1.0;
            }
            int n = 0;
            int n2 = 0;
            long l = 0L;
            long l2 = 0L;
            Hashtable hashtable = this.m_Values;
            synchronized (hashtable) {
                Enumeration enumeration = this.m_Values.keys();
                while (enumeration.hasMoreElements()) {
                    Long l3 = (Long)enumeration.nextElement();
                    if (System.currentTimeMillis() - l3 > 3600000L) continue;
                    n2 = (Integer)this.m_Values.get(l3);
                    if (n2 < 0) {
                        ++l;
                        continue;
                    }
                    if (n2 == Integer.MAX_VALUE) continue;
                    ++n;
                    l2 = (long)((double)l2 + Math.pow(n2 - this.getAverage(), 2.0));
                }
            }
            if (l > 0L && n == 0) {
                return -1.0;
            }
            if (n == 0) {
                return 0.0;
            }
            return Math.sqrt(l2 /= (long)n);
        }

        public Element toXmlElement(Document document) {
            Element element = document.createElement(ATTRIBUTES[this.m_attribute]);
            Bound bound = this.getBound();
            XMLUtil.setAttribute(element, XML_ATTR_BOUND, bound.getBound());
            XMLUtil.setAttribute(element, XML_ATTR_NOT_RECOVERED_BOUND, bound.getNotRecoveredBound());
            XMLUtil.setAttribute(element, XML_ATTR_BEST, this.getBestBound());
            return element;
        }
    }
}

