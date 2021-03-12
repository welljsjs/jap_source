/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.crypto.MyRandom;
import anon.infoservice.AbstractDatabaseEntry;
import anon.infoservice.DatabaseMessage;
import anon.infoservice.ICertifiedDatabaseEntry;
import anon.infoservice.IDistributable;
import anon.infoservice.IDistributor;
import anon.infoservice.externaldatabase.EDBException;
import anon.infoservice.externaldatabase.IEDBConfiguration;
import anon.infoservice.externaldatabase.IEDBDatabase;
import anon.util.Base64;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLUtil;
import anon.util.ZLibTools;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class Database
extends Observable
implements IXMLEncodable {
    private static String XML_ALL_DB_NAME = "InfoServiceDB";
    private static Hashtable ms_databases = new Hashtable();
    private static Thread ms_vacuumThread;
    private static IDistributor ms_distributor;
    private static boolean ms_bShutdown;
    private Class m_DatabaseEntryClass;
    private Thread m_dbThread;
    private final Object SYNC_THREAD = new Object();
    private Hashtable m_serviceDatabase;
    private MyRandom m_random = null;
    private long m_randomSeed;
    private Vector m_timeoutList;
    private volatile boolean m_bStopThread = false;
    private static final Object SYNC_EXTERNAL_DATABASE;
    private static IEDBDatabase ms_edbDatabase;
    private static boolean ms_bIsLoading;
    static /* synthetic */ Class class$anon$infoservice$Database;
    static /* synthetic */ Class class$anon$infoservice$AbstractDatabaseEntry;
    static /* synthetic */ Class class$org$w3c$dom$Element;
    static /* synthetic */ Class class$anon$util$IXMLEncodable;
    static /* synthetic */ Class class$anon$infoservice$Database$IWebInfo;

    public static void registerDistributor(IDistributor iDistributor) {
        ms_distributor = iDistributor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean registerExternalDatabase(IEDBConfiguration iEDBConfiguration) {
        Object object = SYNC_EXTERNAL_DATABASE;
        synchronized (object) {
            try {
                if (ms_vacuumThread != null) {
                    while (ms_vacuumThread.isAlive()) {
                        ms_vacuumThread.interrupt();
                        ms_vacuumThread.join(500L);
                    }
                }
                boolean bl = Database.testDB(iEDBConfiguration);
                ms_vacuumThread = new Thread("Database vacuum"){

                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                Database.doVacuum();
                                Thread.sleep(300000L);
                            }
                            catch (Exception exception) {
                                LogHolder.log(1, LogType.DB, "Could not vaccum database!", exception);
                            }
                        }
                    }
                };
                ms_vacuumThread.setDaemon(true);
                ms_vacuumThread.start();
                return bl;
            }
            catch (Exception exception) {
                return false;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean testDB(IEDBConfiguration iEDBConfiguration) throws Exception {
        Object object = SYNC_EXTERNAL_DATABASE;
        synchronized (object) {
            ms_edbDatabase = iEDBConfiguration.getEDBDatabaseInstance();
            ms_edbDatabase.testDB();
            return true;
        }
    }

    private static Database registerInstance(Database database) {
        Database database2 = (Database)ms_databases.get(database.getEntryClass());
        if (database2 == null && database != null) {
            ms_databases.put(database.getEntryClass(), database);
            database2 = database;
        }
        return database2;
    }

    private static Database unregisterInstance(Class class_) {
        return (Database)ms_databases.remove(class_);
    }

    private static void unregisterInstances() {
        ms_databases.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Database getInstance(Class class_) throws IllegalArgumentException {
        Database database = null;
        Class class_2 = class$anon$infoservice$Database == null ? (class$anon$infoservice$Database = Database.class$("anon.infoservice.Database")) : class$anon$infoservice$Database;
        synchronized (class_2) {
            database = (Database)ms_databases.get(class_);
            if (database == null) {
                database = new Database(class_);
                if (!ms_bShutdown) {
                    ms_databases.put(class_, database);
                }
            }
        }
        return database;
    }

    public static void restoreFromXML(Document document, Class[] arrclass) {
        if (document == null || arrclass == null) {
            return;
        }
        Element element = document.getDocumentElement();
        if (element == null) {
            return;
        }
        if (!element.getNodeName().equals(XML_ALL_DB_NAME)) {
            return;
        }
        Database database = null;
        for (int i = 0; i < arrclass.length; ++i) {
            database = Database.getInstance(arrclass[i]);
            if (database == null) continue;
            database.loadFromXml(element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Document dumpToXML(Class[] arrclass) {
        if (arrclass == null) {
            return null;
        }
        Document document = XMLUtil.createDocument();
        Element element = document.createElement(XML_ALL_DB_NAME);
        Database database = null;
        Element element2 = null;
        Class class_ = class$anon$infoservice$Database == null ? (class$anon$infoservice$Database = Database.class$("anon.infoservice.Database")) : class$anon$infoservice$Database;
        synchronized (class_) {
            for (int i = 0; i < arrclass.length; ++i) {
                database = Database.getInstance(arrclass[i]);
                element2 = database.toXmlElement(document);
                if (element2 == null) continue;
                element.appendChild(element2);
            }
        }
        document.appendChild(element);
        return document;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void shutdownDatabases() {
        Class class_ = class$anon$infoservice$Database == null ? (class$anon$infoservice$Database = Database.class$("anon.infoservice.Database")) : class$anon$infoservice$Database;
        synchronized (class_) {
            ms_bShutdown = true;
            Enumeration enumeration = ms_databases.elements();
            while (enumeration.hasMoreElements()) {
                Database database = (Database)enumeration.nextElement();
                Object object = database.SYNC_THREAD;
                synchronized (object) {
                    database.stopThread();
                }
            }
            ms_databases.clear();
        }
    }

    private Database(Class class_) throws IllegalArgumentException {
        if (class_ == null) {
            throw new NullPointerException("Invalid database class!");
        }
        if (!(class$anon$infoservice$AbstractDatabaseEntry == null ? (class$anon$infoservice$AbstractDatabaseEntry = Database.class$("anon.infoservice.AbstractDatabaseEntry")) : class$anon$infoservice$AbstractDatabaseEntry).isAssignableFrom(class_)) {
            throw new IllegalArgumentException("There is no Database that can store entries of type " + class_.getName() + "!");
        }
        this.m_DatabaseEntryClass = class_;
        this.m_serviceDatabase = new Hashtable();
        this.m_timeoutList = new Vector();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startThread() {
        Object object = this.SYNC_THREAD;
        synchronized (object) {
            if (ms_bShutdown || !this.m_bStopThread && this.m_dbThread != null && this.m_dbThread.isAlive()) {
                return;
            }
            while (this.m_dbThread != null && this.m_bStopThread && this.m_dbThread.isAlive()) {
                LogHolder.log(3, LogType.DB, "Shutting down old database thread before starting new one (" + this.m_DatabaseEntryClass.toString() + ")");
                this.m_dbThread.interrupt();
                Thread.yield();
            }
            this.m_bStopThread = false;
            this.m_dbThread = new Thread((Runnable)new TimeoutThread(), "Database Thread: " + this.m_DatabaseEntryClass.toString());
            this.m_dbThread.setDaemon(true);
            this.m_dbThread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stopThread() {
        Object object = this.SYNC_THREAD;
        synchronized (object) {
            this.m_bStopThread = true;
            while (this.m_dbThread != null && this.m_dbThread.isAlive()) {
                LogHolder.log(6, LogType.DB, "Shutting down db thread for class: " + this.m_DatabaseEntryClass.toString());
                this.m_dbThread.interrupt();
                Hashtable hashtable = this.m_serviceDatabase;
                synchronized (hashtable) {
                    this.m_serviceDatabase.notify();
                }
                Thread.yield();
            }
        }
    }

    public boolean update(AbstractDatabaseEntry abstractDatabaseEntry) throws IllegalArgumentException {
        return this.update(abstractDatabaseEntry, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean update(AbstractDatabaseEntry abstractDatabaseEntry, boolean bl) throws IllegalArgumentException {
        if (abstractDatabaseEntry == null) {
            return false;
        }
        if (!this.m_DatabaseEntryClass.isAssignableFrom(abstractDatabaseEntry.getClass())) {
            throw new IllegalArgumentException("Database cannot store entries of type " + abstractDatabaseEntry.getClass().getName() + "!");
        }
        boolean bl2 = false;
        AbstractDatabaseEntry abstractDatabaseEntry2 = null;
        boolean bl3 = false;
        boolean bl4 = false;
        Object object = this.SYNC_THREAD;
        synchronized (object) {
            Hashtable hashtable = this.m_serviceDatabase;
            synchronized (hashtable) {
                abstractDatabaseEntry2 = (AbstractDatabaseEntry)this.m_serviceDatabase.get(abstractDatabaseEntry.getId());
                bl2 = abstractDatabaseEntry.isNewerThan(abstractDatabaseEntry2);
                if (bl2) {
                    if (abstractDatabaseEntry.getExpireTime() <= System.currentTimeMillis()) {
                        if (abstractDatabaseEntry.isPersistanceDeletionAllowed()) {
                            abstractDatabaseEntry.deletePersistence();
                        }
                        LogHolder.log(6, LogType.NET, "Received an expired db entry: '" + abstractDatabaseEntry.getId() + "' (" + this.m_DatabaseEntryClass.toString() + "). It was dropped immediatly.");
                        return this.remove(abstractDatabaseEntry.getId());
                    }
                    while (this.m_timeoutList.removeElement(abstractDatabaseEntry.getId())) {
                    }
                    if (abstractDatabaseEntry.isPersistanceDeletionAllowed()) {
                        this.addExternal(abstractDatabaseEntry);
                        abstractDatabaseEntry.deletePersistence();
                    }
                    this.m_serviceDatabase.put(abstractDatabaseEntry.getId(), abstractDatabaseEntry);
                    boolean bl5 = false;
                    int n = 0;
                    while (!bl5) {
                        if (n < this.m_timeoutList.size()) {
                            Object e = this.m_timeoutList.elementAt(n);
                            AbstractDatabaseEntry abstractDatabaseEntry3 = (AbstractDatabaseEntry)this.m_serviceDatabase.get(e);
                            if (abstractDatabaseEntry3.getExpireTime() >= abstractDatabaseEntry.getExpireTime()) {
                                this.m_timeoutList.insertElementAt(abstractDatabaseEntry.getId(), n);
                                bl5 = true;
                            }
                        } else {
                            this.m_timeoutList.addElement(abstractDatabaseEntry.getId());
                            bl5 = true;
                        }
                        ++n;
                    }
                    if (n == 1) {
                        if (abstractDatabaseEntry.getExpireTime() == Long.MAX_VALUE) {
                            bl3 = true;
                        } else {
                            bl4 = true;
                            this.m_serviceDatabase.notify();
                        }
                    }
                    LogHolder.log(7, LogType.MISC, "Added / updated entry '" + abstractDatabaseEntry.getId() + "' in the " + this.m_DatabaseEntryClass.getName() + " database. Now there are " + Integer.toString(this.m_serviceDatabase.size()) + " entries stored in this database. The new entry has position " + Integer.toString(n) + "/" + Integer.toString(this.m_timeoutList.size()) + " in the database-timeout list.");
                    if (abstractDatabaseEntry instanceof IDistributable && bl) {
                        if (ms_distributor != null) {
                            ms_distributor.addJob((IDistributable)((Object)abstractDatabaseEntry));
                        } else {
                            LogHolder.log(4, LogType.MISC, "No distributor specified - cannot distribute database entries!");
                        }
                    }
                } else if (abstractDatabaseEntry.isPersistanceDeletionAllowed()) {
                    abstractDatabaseEntry.deletePersistence();
                }
            }
            if (bl3) {
                this.stopThread();
            } else if (bl4) {
                this.startThread();
            }
        }
        if (bl2) {
            this.setChanged();
            if (abstractDatabaseEntry2 == null) {
                this.notifyObservers(new DatabaseMessage(1, abstractDatabaseEntry));
            } else {
                this.notifyObservers(new DatabaseMessage(2, abstractDatabaseEntry));
            }
            return true;
        }
        return false;
    }

    public Class getEntryClass() {
        return this.m_DatabaseEntryClass;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(String string) {
        if (string != null) {
            AbstractDatabaseEntry abstractDatabaseEntry;
            boolean bl = false;
            boolean bl2 = false;
            Object object = this.SYNC_THREAD;
            synchronized (object) {
                Hashtable hashtable = this.m_serviceDatabase;
                synchronized (hashtable) {
                    abstractDatabaseEntry = (AbstractDatabaseEntry)this.m_serviceDatabase.remove(string);
                    if (abstractDatabaseEntry != null) {
                        if (abstractDatabaseEntry.isPersistanceDeletionAllowed()) {
                            this.removeExternal(abstractDatabaseEntry);
                        }
                        this.m_timeoutList.removeElement(string);
                        if (this.m_timeoutList.size() > 0 && ((AbstractDatabaseEntry)this.m_serviceDatabase.get(this.m_timeoutList.elementAt(0))).getExpireTime() == Long.MAX_VALUE) {
                            bl = true;
                        } else {
                            bl2 = true;
                        }
                    }
                }
                if (bl2) {
                    this.startThread();
                } else if (bl) {
                    this.stopThread();
                }
            }
            if (abstractDatabaseEntry != null) {
                this.setChanged();
                this.notifyObservers(new DatabaseMessage(3, abstractDatabaseEntry));
                return true;
            }
        }
        return false;
    }

    public boolean remove(AbstractDatabaseEntry abstractDatabaseEntry) {
        if (abstractDatabaseEntry != null && this.m_DatabaseEntryClass.isAssignableFrom(abstractDatabaseEntry.getClass())) {
            return this.remove(abstractDatabaseEntry.getId());
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeThis(AbstractDatabaseEntry abstractDatabaseEntry) {
        if (abstractDatabaseEntry != null && this.m_DatabaseEntryClass.isAssignableFrom(abstractDatabaseEntry.getClass())) {
            Hashtable hashtable = this.m_serviceDatabase;
            synchronized (hashtable) {
                if (this.getEntryById(abstractDatabaseEntry.getId()) == abstractDatabaseEntry) {
                    return this.remove(abstractDatabaseEntry.getId());
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAll() {
        Object object = this.SYNC_THREAD;
        synchronized (object) {
            Hashtable hashtable = this.m_serviceDatabase;
            synchronized (hashtable) {
                this.m_serviceDatabase.clear();
                this.m_timeoutList.removeAllElements();
            }
        }
        this.setChanged();
        this.notifyObservers(new DatabaseMessage(4));
    }

    public int loadFromXml(Element element) {
        return this.loadFromXml(element, false);
    }

    public int loadFromXml(Element element, boolean bl) {
        int n = 0;
        String string = XMLUtil.getXmlElementName(this.m_DatabaseEntryClass);
        if (element == null || string == null) {
            return n;
        }
        NodeList nodeList = element.getElementsByTagName(string);
        Constructor constructor = null;
        try {
            constructor = this.m_DatabaseEntryClass.getConstructor(class$org$w3c$dom$Element == null ? (class$org$w3c$dom$Element = Database.class$("org.w3c.dom.Element")) : class$org$w3c$dom$Element, Long.TYPE);
        }
        catch (Exception exception) {
            LogHolder.log(5, LogType.DB, "No timeout constructor for " + this.m_DatabaseEntryClass + " available.");
        }
        for (int i = 0; i < nodeList.getLength(); ++i) {
            try {
                AbstractDatabaseEntry abstractDatabaseEntry = constructor == null ? (AbstractDatabaseEntry)this.m_DatabaseEntryClass.getConstructor(class$org$w3c$dom$Element == null ? Database.class$("org.w3c.dom.Element") : class$org$w3c$dom$Element).newInstance(nodeList.item(i)) : (AbstractDatabaseEntry)constructor.newInstance(nodeList.item(i), new Long(Long.MAX_VALUE));
                if (bl && abstractDatabaseEntry instanceof ICertifiedDatabaseEntry && (!((ICertifiedDatabaseEntry)((Object)abstractDatabaseEntry)).isVerified() || !((ICertifiedDatabaseEntry)((Object)abstractDatabaseEntry)).isValid())) {
                    LogHolder.log(4, LogType.MISC, "XML entry " + nodeList.item(i).getNodeName() + " for ID " + abstractDatabaseEntry.getId() + " could not be verified while being loaded!");
                    continue;
                }
                this.update(abstractDatabaseEntry);
                ++n;
                continue;
            }
            catch (Exception exception) {
                LogHolder.log(4, LogType.MISC, "Could not load db entry from XML!", exception);
            }
        }
        return n;
    }

    public Element toXmlElement(Document document) {
        return this.toXmlElement(document, XMLUtil.getXmlElementContainerName(this.m_DatabaseEntryClass));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Element toXmlElement(Document document, String string) {
        if (document == null || !(class$anon$util$IXMLEncodable == null ? (class$anon$util$IXMLEncodable = Database.class$("anon.util.IXMLEncodable")) : class$anon$util$IXMLEncodable).isAssignableFrom(this.m_DatabaseEntryClass) || string == null || string.trim().length() == 0) {
            return null;
        }
        Element element = document.createElement(string);
        Hashtable hashtable = this.m_serviceDatabase;
        synchronized (hashtable) {
            Enumeration enumeration = this.m_serviceDatabase.elements();
            while (enumeration.hasMoreElements()) {
                element.appendChild(((IXMLEncodable)enumeration.nextElement()).toXmlElement(document));
            }
        }
        return element;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void randomize() {
        Hashtable hashtable = this.m_serviceDatabase;
        synchronized (hashtable) {
            if (this.m_random == null) {
                this.m_random = new MyRandom();
            }
            this.m_randomSeed = this.m_random.nextLong();
        }
    }

    public Hashtable getEntryHash() {
        return (Hashtable)this.m_serviceDatabase.clone();
    }

    public Vector getEntryList() {
        return this.getEntryList(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector getEntryList(boolean bl) {
        Vector vector = new Vector();
        MyRandom myRandom = null;
        Hashtable hashtable = this.m_serviceDatabase;
        synchronized (hashtable) {
            if (this.m_random != null) {
                this.m_random.setSeed(this.m_randomSeed, true);
            }
            myRandom = bl ? new MyRandom() : this.m_random;
            Enumeration enumeration = this.m_serviceDatabase.elements();
            while (enumeration.hasMoreElements()) {
                if (myRandom == null || vector.size() == 0) {
                    vector.addElement(enumeration.nextElement());
                    continue;
                }
                vector.insertElementAt(enumeration.nextElement(), Math.abs(myRandom.nextInt(vector.size() + 1)));
            }
        }
        return vector;
    }

    public Vector getSortedEntryList(Util.Comparable comparable) {
        Vector vector = this.getEntryList();
        Util.sort(vector, comparable);
        return vector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Enumeration getEntrySnapshotAsEnumeration() {
        Hashtable hashtable = this.m_serviceDatabase;
        synchronized (hashtable) {
            return this.getEntryList().elements();
        }
    }

    public int getNumberOfEntries() {
        return this.m_serviceDatabase.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractDatabaseEntry getEntryById(String string) {
        if (string == null) {
            return null;
        }
        AbstractDatabaseEntry abstractDatabaseEntry = null;
        Hashtable hashtable = this.m_serviceDatabase;
        synchronized (hashtable) {
            abstractDatabaseEntry = (AbstractDatabaseEntry)this.m_serviceDatabase.get(string);
        }
        return abstractDatabaseEntry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractDatabaseEntry getRandomEntry() {
        AbstractDatabaseEntry abstractDatabaseEntry = null;
        Hashtable hashtable = this.m_serviceDatabase;
        synchronized (hashtable) {
            if (this.m_timeoutList.size() > 0) {
                try {
                    String string = (String)this.m_timeoutList.elementAt(new MyRandom().nextInt(this.m_timeoutList.size()));
                    abstractDatabaseEntry = (AbstractDatabaseEntry)this.m_serviceDatabase.get(string);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return abstractDatabaseEntry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addObserver(Observer observer) {
        Hashtable hashtable = this.m_serviceDatabase;
        synchronized (hashtable) {
            super.addObserver(observer);
            observer.update(this, new DatabaseMessage(5, this.getEntryList()));
        }
    }

    public boolean isEntryIdInTimeoutList(String string) {
        return this.m_timeoutList.contains(string);
    }

    public int getTimeoutListSize() {
        return this.m_timeoutList.size();
    }

    public Document getWebInfos(String string) {
        return Database.getWebInfos(this.getEntryClass(), string);
    }

    public Document getWebInfos() {
        return Database.getWebInfos(this.getEntryClass());
    }

    private static Document getWebInfos(Class class_, String string) {
        Element element;
        if (!(class$anon$infoservice$Database$IWebInfo == null ? (class$anon$infoservice$Database$IWebInfo = Database.class$("anon.infoservice.Database$IWebInfo")) : class$anon$infoservice$Database$IWebInfo).isAssignableFrom(class_)) {
            LogHolder.log(0, LogType.DB, "Illegal class for web info: " + class_);
            return null;
        }
        Document document = XMLUtil.createDocument();
        IWebInfo iWebInfo = (IWebInfo)((Object)Database.getInstance(class_).getEntryById(string));
        Element element2 = element = iWebInfo == null ? null : iWebInfo.getWebInfo(document);
        if (element == null) {
            return null;
        }
        document.appendChild(element);
        return document;
    }

    private static Document getWebInfos(Class class_) {
        if (!(class$anon$infoservice$Database$IWebInfo == null ? (class$anon$infoservice$Database$IWebInfo = Database.class$("anon.infoservice.Database$IWebInfo")) : class$anon$infoservice$Database$IWebInfo).isAssignableFrom(class_)) {
            return null;
        }
        String string = Util.getStaticFieldValue(class_, "XML_ELEMENT_WEBINFO_CONTAINER");
        if (string == null) {
            return null;
        }
        Document document = XMLUtil.createDocument();
        Vector vector = Database.getInstance(class_).getEntryList();
        IWebInfo iWebInfo = null;
        Element element = document.createElement(string);
        Element element2 = null;
        document.appendChild(element);
        for (int i = 0; i < vector.size(); ++i) {
            iWebInfo = (IWebInfo)vector.elementAt(i);
            element2 = iWebInfo.getWebInfo(document);
            if (element2 == null) continue;
            element.appendChild(element2);
        }
        return document;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addExternal(AbstractDatabaseEntry abstractDatabaseEntry) {
        if (ms_edbDatabase == null || !(abstractDatabaseEntry instanceof IXMLEncodable) || ms_bIsLoading) {
            return;
        }
        Object object = SYNC_EXTERNAL_DATABASE;
        synchronized (object) {
            if (ms_edbDatabase == null) {
                return;
            }
            try {
                this.addExternal_int(abstractDatabaseEntry);
            }
            catch (EDBException eDBException) {
                LogHolder.log(2, LogType.DB, eDBException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void doVacuum() throws EDBException {
        if (ms_edbDatabase == null || ms_bIsLoading) {
            return;
        }
        Object object = SYNC_EXTERNAL_DATABASE;
        synchronized (object) {
            if (ms_edbDatabase == null) {
                return;
            }
            LogHolder.log(6, LogType.DB, "Vacuuming database...");
            ms_edbDatabase.doVacuum();
        }
    }

    private void addExternal_int(AbstractDatabaseEntry abstractDatabaseEntry) throws EDBException {
        String string = null;
        String string2 = null;
        String string3 = null;
        try {
            string2 = abstractDatabaseEntry.getId();
            string3 = Util.replaceAll(abstractDatabaseEntry.getClass().getName(), ".", "__");
            Document document = XMLUtil.toXMLDocument((IXMLEncodable)((Object)abstractDatabaseEntry));
            if (document == null) {
                throw new EDBException("Document is null!");
            }
            byte[] arrby = XMLUtil.toByteArray(document);
            string = Base64.encode(ZLibTools.compress(arrby), false);
        }
        catch (Throwable throwable) {
            throw new EDBException("Converting AbstractDatabaseEntry to external format failed!");
        }
        ms_edbDatabase.insert(string3, string2, string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeExternal(AbstractDatabaseEntry abstractDatabaseEntry) {
        if (ms_edbDatabase == null || !(abstractDatabaseEntry instanceof IXMLEncodable) || ms_bIsLoading) {
            return;
        }
        Object object = SYNC_EXTERNAL_DATABASE;
        synchronized (object) {
            if (ms_edbDatabase == null) {
                return;
            }
            try {
                this.removeExternal_int(abstractDatabaseEntry);
            }
            catch (EDBException eDBException) {
                LogHolder.log(2, LogType.DB, eDBException);
            }
        }
    }

    private void removeExternal_int(AbstractDatabaseEntry abstractDatabaseEntry) throws EDBException {
        String string = Util.replaceAll(abstractDatabaseEntry.getClass().getName(), ".", "__");
        ms_edbDatabase.remove(string, abstractDatabaseEntry.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadFromExternalDatabase() {
        if (ms_edbDatabase == null) {
            return;
        }
        Object object = SYNC_EXTERNAL_DATABASE;
        synchronized (object) {
            if (ms_edbDatabase == null) {
                return;
            }
            LogHolder.log(5, LogType.DB, "Reading cached objects from external database...");
            ms_bIsLoading = true;
            try {
                Database.loadFromExternalDatabase_int();
                LogHolder.log(5, LogType.DB, "Cached objects were read from external database.");
            }
            catch (EDBException eDBException) {
                LogHolder.log(2, LogType.DB, eDBException);
            }
            ms_bIsLoading = false;
        }
    }

    public static void loadFromExternalDatabase_int() throws EDBException {
        Vector vector = new Vector();
        Constructor<?> constructor = null;
        vector = ms_edbDatabase.getAllTypes();
        for (int i = 0; i < vector.size(); ++i) {
            String string = vector.elementAt(i).toString();
            String string2 = Util.replaceAll(string, "__", ".");
            boolean bl = false;
            try {
                Class<?> class_ = Class.forName(string2);
                Vector vector2 = ms_edbDatabase.getAllValuesOfType(string);
                Enumeration enumeration = vector2.elements();
                while (enumeration.hasMoreElements()) {
                    AbstractDatabaseEntry abstractDatabaseEntry;
                    Element element;
                    long l;
                    long l2;
                    String string3 = (String)enumeration.nextElement();
                    try {
                        l2 = System.currentTimeMillis();
                        byte[] arrby = Base64.decode(string3);
                        l = System.currentTimeMillis();
                        LogHolder.log(7, LogType.MISC, "Load from external Database - base64 decode needs [ms]: " + (l - l2));
                        l2 = System.currentTimeMillis();
                        byte[] arrby2 = ZLibTools.decompress(arrby);
                        l = System.currentTimeMillis();
                        LogHolder.log(7, LogType.MISC, "Load from external Database - decompress needs [ms]: " + (l - l2));
                        l2 = System.currentTimeMillis();
                        Document document = XMLUtil.toXMLDocument(arrby2);
                        l = System.currentTimeMillis();
                        LogHolder.log(7, LogType.MISC, "Load from external Database - document creation needs [ms]: " + (l - l2));
                        element = document.getDocumentElement();
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.DB, "Could not load cached DB entries for class " + string2 + ".", exception);
                        bl = true;
                        break;
                    }
                    try {
                        l2 = System.currentTimeMillis();
                        constructor = class_.getConstructor(class$org$w3c$dom$Element == null ? Database.class$("org.w3c.dom.Element") : class$org$w3c$dom$Element, Long.TYPE);
                        abstractDatabaseEntry = (AbstractDatabaseEntry)constructor.newInstance(element, new Long(Long.MAX_VALUE));
                        long l3 = System.currentTimeMillis();
                        LogHolder.log(7, LogType.MISC, "Load from external Database - construction Database entry needs [ms]: " + (l3 - l2));
                    }
                    catch (Exception exception) {
                        try {
                            long l4 = System.currentTimeMillis();
                            constructor = class_.getConstructor(class$org$w3c$dom$Element == null ? Database.class$("org.w3c.dom.Element") : class$org$w3c$dom$Element);
                            abstractDatabaseEntry = (AbstractDatabaseEntry)constructor.newInstance(element);
                            l = System.currentTimeMillis();
                            LogHolder.log(7, LogType.MISC, "Load from external Database - construction Database entry needs [ms]: " + (l - l4));
                        }
                        catch (Exception exception2) {
                            LogHolder.log(2, LogType.DB, "Could not load cached DB entries for class " + string2 + ".", exception2);
                            bl = true;
                            break;
                        }
                    }
                    LogHolder.log(6, LogType.DB, "Loading cached DB entry " + abstractDatabaseEntry.getClass().getName() + ":" + abstractDatabaseEntry.getId() + ".");
                    Database.getInstance(class_).update(abstractDatabaseEntry);
                }
            }
            catch (ClassNotFoundException classNotFoundException) {
                LogHolder.log(2, LogType.DB, "Could not load cached DB entries for class " + string2 + ".", classNotFoundException);
                bl = true;
            }
            if (!bl) continue;
            ms_edbDatabase.removeType(string);
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        ms_bShutdown = false;
        SYNC_EXTERNAL_DATABASE = new Object();
        ms_bIsLoading = false;
    }

    public static interface IWebInfo {
        public static final String FIELD_XML_ELEMENT_WEBINFO_CONTAINER = "XML_ELEMENT_WEBINFO_CONTAINER";

        public Element getWebInfo(Document var1);
    }

    private class TimeoutThread
    implements Runnable {
        private TimeoutThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void run() {
            LogHolder.log(6, LogType.DB, "Starting timeout database thread for class " + Database.this.m_DatabaseEntryClass.toString() + ".");
            while (!(Database.this.m_bStopThread || ms_bShutdown || Thread.currentThread().isInterrupted())) {
                boolean bl = true;
                Hashtable hashtable = Database.this.m_serviceDatabase;
                synchronized (hashtable) {
                    while (!Database.this.m_bStopThread && !ms_bShutdown && !Thread.currentThread().isInterrupted() && Database.this.m_timeoutList.size() > 0 && bl) {
                        AbstractDatabaseEntry abstractDatabaseEntry = (AbstractDatabaseEntry)Database.this.m_serviceDatabase.get(Database.this.m_timeoutList.firstElement());
                        if (System.currentTimeMillis() >= abstractDatabaseEntry.getExpireTime()) {
                            LogHolder.log(6, LogType.MISC, "DatabaseEntry (" + abstractDatabaseEntry.getClass().getName() + ")" + abstractDatabaseEntry.getId() + " has reached the expire time and is removed.");
                            AbstractDatabaseEntry abstractDatabaseEntry2 = (AbstractDatabaseEntry)Database.this.m_serviceDatabase.remove(abstractDatabaseEntry.getId());
                            if (abstractDatabaseEntry2 != null && abstractDatabaseEntry2.isPersistanceDeletionAllowed()) {
                                Database.this.removeExternal(abstractDatabaseEntry2);
                            }
                            Database.this.m_timeoutList.removeElementAt(0);
                            Database.this.setChanged();
                            Database.this.notifyObservers(new DatabaseMessage(3, abstractDatabaseEntry));
                            continue;
                        }
                        bl = false;
                    }
                    if (Database.this.m_bStopThread || ms_bShutdown || Thread.currentThread().isInterrupted()) {
                        return;
                    }
                }
                hashtable = Database.this.m_serviceDatabase;
                synchronized (hashtable) {
                    block19: {
                        block18: {
                            long l = 0L;
                            if (Database.this.m_timeoutList.size() > 0) {
                                l = ((AbstractDatabaseEntry)Database.this.m_serviceDatabase.get(Database.this.m_timeoutList.firstElement())).getExpireTime() - System.currentTimeMillis();
                            }
                            if (l > 0L) {
                                try {
                                    Database.this.m_serviceDatabase.wait(l);
                                    LogHolder.log(7, LogType.MISC, "One entry could be expired. Wake up...");
                                }
                                catch (InterruptedException interruptedException) {
                                    if (!Database.this.m_bStopThread && !ms_bShutdown && !Thread.currentThread().isInterrupted()) break block18;
                                    return;
                                }
                            }
                        }
                        if (Database.this.m_timeoutList.size() == 0) {
                            try {
                                Database.this.m_serviceDatabase.wait();
                                LogHolder.log(7, LogType.MISC, "First entry in the database. Look when it expires. Wake up...");
                            }
                            catch (InterruptedException interruptedException) {
                                if (!Database.this.m_bStopThread && !ms_bShutdown && !Thread.currentThread().isInterrupted()) break block19;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}

