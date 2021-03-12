/*
 * Decompiled with CFR 0.150.
 */
package anon.util;

import anon.crypto.SignatureCreator;
import anon.crypto.XMLSignature;
import anon.util.IXMLEncodable;
import anon.util.Util;
import anon.util.XMLParseException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class XMLUtil {
    public static final int STORAGE_MODE_NORMAL = 0;
    public static final int STORAGE_MODE_OPTIMIZED = 1;
    public static final int STORAGE_MODE_AGRESSIVE = 2;
    private static final String DEFAULT_FORMAT_SPACE = "    ";
    private static final String XML_STR_BOOLEAN_TRUE = "true";
    private static final String XML_STR_BOOLEAN_FALSE = "false";
    private static final String PACKAGE_TRANSFORMER = "javax.xml.transform.";
    private static final String HIERARCHY_REQUEST_ERR = "HIERARCHY_REQUEST_ERR: ";
    private static DocumentBuilderFactory ms_DocumentBuilderFactory;
    private static boolean m_bCheckedHumanReadableFormatting;
    private static boolean m_bNeedsHumanReadableFormatting;
    private static int ms_storageMode;
    public static final String[] SPECIAL_CHARS;
    public static final String[] ENTITIES;
    static /* synthetic */ Class class$java$io$Writer;
    static /* synthetic */ Class class$java$io$OutputStream;
    static /* synthetic */ Class class$org$w3c$dom$Node;
    static /* synthetic */ Class class$java$lang$String;

    public static int getStorageMode() {
        return ms_storageMode;
    }

    public static void setStorageMode(int n) {
        if (n == 0 || n == 1 || n == 2) {
            ms_storageMode = n;
        }
    }

    public static void assertNotNull(Node node) throws XMLParseException {
        if (node == null) {
            throw new XMLParseException("##__null__##");
        }
    }

    public static void assertNotNull(Node node, String string) throws XMLParseException {
        if (XMLUtil.parseAttribute(node, string, null) == null) {
            throw new XMLParseException("##__null__##");
        }
    }

    public static Node assertNodeName(Node node, String string) throws XMLParseException {
        if (node == null) {
            throw new XMLParseException("##__null__##", "Expected node '" + string + "' is NULL!");
        }
        if (!(node = XMLUtil.getDocumentElement(node)).getNodeName().equals(string)) {
            String string2 = node.getOwnerDocument().getDocumentElement() == node || node.getOwnerDocument() == node ? "##__root__##" : node.getNodeName();
            throw new XMLParseException(string2, "Node '" + node.getNodeName() + "' has not the expected name: '" + string + "'");
        }
        return node;
    }

    public static Node getDocumentElement(Node node) {
        if (node instanceof Document) {
            node = ((Document)node).getDocumentElement();
        }
        return node;
    }

    public static int parseValue(Node node, int n) {
        int n2 = n;
        String string = XMLUtil.parseValue(node, null);
        if (string != null) {
            try {
                n2 = Integer.parseInt(string);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return n2;
    }

    public static long parseValue(Node node, long l) {
        long l2 = l;
        String string = XMLUtil.parseValue(node, null);
        if (string != null) {
            try {
                l2 = Long.parseLong(string);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return l2;
    }

    public static double parseValue(Node node, double d) {
        double d2 = d;
        String string = XMLUtil.parseValue(node, null);
        if (string != null) {
            try {
                d2 = Util.parseDouble(string);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return d2;
    }

    public static String parseAttribute(Node node, String string, String string2) {
        try {
            if (node instanceof Document) {
                node = ((Document)node).getDocumentElement();
            }
            Attr attr = ((Element)node).getAttributeNode(string);
            return attr.getValue().trim();
        }
        catch (Exception exception) {
            return string2;
        }
    }

    public static boolean parseAttribute(Node node, String string, boolean bl) {
        boolean bl2 = bl;
        try {
            String string2 = XMLUtil.parseAttribute(node, string, null);
            if (string2.equalsIgnoreCase(XML_STR_BOOLEAN_TRUE)) {
                bl2 = true;
            } else if (string2.equalsIgnoreCase(XML_STR_BOOLEAN_FALSE)) {
                bl2 = false;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return bl2;
    }

    public static int parseAttribute(Node node, String string, int n) {
        int n2 = n;
        try {
            n2 = Integer.parseInt(XMLUtil.parseAttribute(node, string, null));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return n2;
    }

    public static double parseAttribute(Node node, String string, double d) {
        double d2 = d;
        try {
            d2 = Util.parseDouble(XMLUtil.parseAttribute(node, string, null));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return d2;
    }

    public static long parseAttribute(Node node, String string, long l) {
        long l2 = l;
        try {
            l2 = Long.parseLong(XMLUtil.parseAttribute(node, string, null));
        }
        catch (Exception exception) {
            // empty catch block
        }
        return l2;
    }

    public static boolean parseValue(Node node, boolean bl) {
        boolean bl2 = bl;
        try {
            String string = XMLUtil.parseValue(node, null);
            if (string == null) {
                return bl2;
            }
            if (string.equalsIgnoreCase(XML_STR_BOOLEAN_TRUE)) {
                bl2 = true;
            } else if (string.equalsIgnoreCase(XML_STR_BOOLEAN_FALSE)) {
                bl2 = false;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return bl2;
    }

    public static String parseValue(Node node, String string) {
        String string2 = string;
        if (node != null) {
            try {
                if (node.getNodeType() == 1) {
                    node = node.getFirstChild();
                }
                if (node.getNodeType() == 3 || node.getNodeType() == 5) {
                    string2 = "";
                    while (node != null && (node.getNodeType() == 5 || node.getNodeType() == 3)) {
                        string2 = node.getNodeType() == 5 ? string2 + node.getFirstChild().getNodeValue() : string2 + node.getNodeValue();
                        node = XMLUtil.getNextSibling(node);
                    }
                } else {
                    string2 = node.getNodeValue();
                }
            }
            catch (Exception exception) {
                return string;
            }
        }
        return string2;
    }

    public static String getXmlElementContainerName(Class class_) {
        return Util.getStaticFieldValue(class_, "XML_ELEMENT_CONTAINER_NAME");
    }

    public static String getXmlElementName(Class class_) {
        return Util.getStaticFieldValue(class_, "XML_ELEMENT_NAME");
    }

    public static Element[] readElementsByTagName(File file, String string) {
        int n;
        Vector<Element> vector = new Vector<Element>();
        if (file != null && string != null) {
            try {
                NodeList nodeList = XMLUtil.readXMLDocument(file).getDocumentElement().getElementsByTagName(string);
                for (n = 0; n < nodeList.getLength(); ++n) {
                    try {
                        vector.addElement((Element)nodeList.item(n));
                        continue;
                    }
                    catch (Exception exception) {
                        LogHolder.log(2, LogType.MISC, exception);
                    }
                }
            }
            catch (Exception exception) {
                LogHolder.log(2, LogType.MISC, exception);
            }
        }
        Element[] arrelement = new Element[vector.size()];
        for (n = 0; n < vector.size(); ++n) {
            arrelement[n] = (Element)vector.elementAt(n);
        }
        return arrelement;
    }

    public static NodeList getElementsByTagName(Node node, String string) {
        if (node == null || !(node instanceof Element) || string == null || string.trim().length() == 0) {
            return null;
        }
        return ((Element)node).getElementsByTagName(string);
    }

    public static Node getFirstChildByName(Node node, String string) {
        try {
            Node node2 = node.getFirstChild();
            while (node2 != null) {
                if (node2.getNodeName().equals(string)) {
                    return node2;
                }
                node2 = XMLUtil.getNextSibling(node2);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public static Node getFirstChildByName(Node node, String string, boolean bl) {
        if (bl) {
            return XMLUtil.getFirstChildByNameUsingDeepSearch(node, string);
        }
        return XMLUtil.getFirstChildByName(node, string);
    }

    public static Node getFirstChildByNameUsingDeepSearch(Node node, String string) {
        Node node2 = null;
        try {
            node = node.getFirstChild();
            while (node != null && (node2 = XMLUtil.getFirstChildByNameUsingDeepSearchInternal(node, string)) == null) {
                node = XMLUtil.getNextSibling(node);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return node2;
    }

    public static Node getLastChildByName(Node node, String string) {
        try {
            for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
                if (!node2.getNodeName().equals(string)) continue;
                return node2;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public static Node getNextSibling(Node node) {
        Node node2 = null;
        try {
            node2 = node.getNextSibling();
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
        return node2;
    }

    public static Node getNextSiblingByName(Node node, String string) {
        try {
            if (node == null) {
                return null;
            }
            Node node2 = XMLUtil.getNextSibling(node);
            while (node2 != null) {
                if (node2.getNodeName().equals(string)) {
                    return node2;
                }
                node2 = XMLUtil.getNextSibling(node2);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public static void setValue(Node node, String string) {
        if (node == null || string == null) {
            return;
        }
        node.appendChild(node.getOwnerDocument().createTextNode(string));
    }

    public static void setValue(Node node, int n) {
        node.appendChild(node.getOwnerDocument().createTextNode(Integer.toString(n)));
    }

    public static void setValue(Node node, long l) {
        node.appendChild(node.getOwnerDocument().createTextNode(Long.toString(l)));
    }

    public static void setValue(Node node, double d) {
        node.appendChild(node.getOwnerDocument().createTextNode(Double.toString(d)));
    }

    public static void setValue(Node node, boolean bl) {
        XMLUtil.setValue(node, bl ? XML_STR_BOOLEAN_TRUE : XML_STR_BOOLEAN_FALSE);
    }

    public static void setAttribute(Element element, String string, String string2) {
        if (string2 == null || string == null || element == null) {
            return;
        }
        element.setAttribute(string, string2);
    }

    public static void setAttribute(Element element, String string, boolean bl) {
        XMLUtil.setAttribute(element, string, bl ? XML_STR_BOOLEAN_TRUE : XML_STR_BOOLEAN_FALSE);
    }

    public static void setAttribute(Element element, String string, int n) {
        XMLUtil.setAttribute(element, string, Integer.toString(n));
    }

    public static void setAttribute(Element element, String string, double d) {
        XMLUtil.setAttribute(element, string, Double.toString(d));
    }

    public static void setAttribute(Element element, String string, long l) {
        XMLUtil.setAttribute(element, string, Long.toString(l));
    }

    public static Document createDocument() {
        try {
            if (ms_DocumentBuilderFactory == null) {
                ms_DocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            }
            return ms_DocumentBuilderFactory.newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException parserConfigurationException) {
            return null;
        }
    }

    public static Element createChildElementWithValue(Node node, String string, String string2) {
        Element element = node.getOwnerDocument().createElement(string);
        XMLUtil.setValue((Node)element, string2);
        node.appendChild(element);
        return element;
    }

    public static Element createChildElement(Node node, String string) {
        Element element = node.getOwnerDocument().createElement(string);
        node.appendChild(element);
        return element;
    }

    public static Node importNode(Document document, Node node, boolean bl) throws XMLParseException {
        Object object;
        Node node2;
        if (document == null || node == null) {
            return null;
        }
        Node node3 = null;
        short s = node.getNodeType();
        switch (s) {
            case 1: {
                node2 = document.createElement(node.getNodeName());
                object = node.getAttributes();
                if (object != null) {
                    for (int i = 0; i < object.getLength(); ++i) {
                        node2.setAttributeNode((Attr)XMLUtil.importNode(document, object.item(i), true));
                    }
                }
                node3 = node2;
                break;
            }
            case 2: {
                node3 = document.createAttribute(node.getNodeName());
                node3.setNodeValue(node.getNodeValue());
                break;
            }
            case 3: {
                node2 = node.getParentNode();
                if (node2 != null && node2.getNodeType() == 2) break;
                node3 = document.createTextNode(node.getNodeValue());
                break;
            }
            case 4: {
                node3 = document.createCDATASection(node.getNodeValue());
                break;
            }
            case 5: {
                node3 = document.createEntityReference(node.getNodeName());
                bl = false;
                break;
            }
            case 6: {
                throw new XMLParseException(node.getNodeName(), "HIERARCHY_REQUEST_ERR: Entity");
            }
            case 7: {
                node3 = document.createProcessingInstruction(node.getNodeName(), node.getNodeValue());
                break;
            }
            case 8: {
                node3 = document.createComment(node.getNodeValue());
                break;
            }
            case 10: {
                throw new XMLParseException(node.getNodeName(), "HIERARCHY_REQUEST_ERR: DocumentType");
            }
            case 11: {
                node3 = document.createDocumentFragment();
                break;
            }
            case 12: {
                throw new XMLParseException(node.getNodeName(), "HIERARCHY_REQUEST_ERR: Notation");
            }
            default: {
                throw new XMLParseException(node.getNodeName(), "HIERARCHY_REQUEST_ERR: Document");
            }
        }
        if (bl) {
            node2 = node.getFirstChild();
            while (node2 != null) {
                if (node3 != null && (object = XMLUtil.importNode(document, node2, true)) != null) {
                    node3.appendChild((Node)object);
                }
                node2 = XMLUtil.getNextSibling(node2);
            }
        }
        return node3;
    }

    public static byte[] toByteArray(Node node) {
        byte[] arrby = null;
        try {
            arrby = XMLSignature.toCanonical(node, true);
        }
        catch (Throwable throwable) {
            return null;
        }
        return arrby;
    }

    public static String toString(Node node) {
        String string;
        try {
            string = new String(XMLUtil.toByteArray(node), "UTF8");
        }
        catch (Exception exception) {
            return null;
        }
        return string;
    }

    public static String quoteXML(String string) {
        String string2 = string;
        if (string2.indexOf(38) >= 0 || string2.indexOf(60) >= 0 || string2.indexOf(62) >= 0) {
            StringBuffer stringBuffer = new StringBuffer(string);
            for (int i = 0; i < stringBuffer.length(); ++i) {
                char c = stringBuffer.charAt(i);
                if (c == '&') {
                    stringBuffer.insert(i, "amp;");
                    i += 4;
                    continue;
                }
                if (c == '<') {
                    stringBuffer.setCharAt(i, '&');
                    stringBuffer.insert(i + 1, "lt;");
                    i += 3;
                    continue;
                }
                if (c != '>') continue;
                stringBuffer.setCharAt(i, '&');
                stringBuffer.insert(i + 1, "gt;");
                i += 3;
            }
            return stringBuffer.toString();
        }
        return string2;
    }

    public static void removeComments(Node node) {
        if (node == null) {
            return;
        }
        if (node.getNodeType() != 8) {
            XMLUtil.removeCommentsInternal(node, node);
        }
    }

    public static Document formatHumanReadable(Document document) {
        XMLUtil.formatHumanReadable(document.getDocumentElement(), 0);
        return document;
    }

    public static Element formatHumanReadable(Element element) {
        XMLUtil.formatHumanReadable(element, 0);
        return element;
    }

    public static Document readXMLDocument(InputSource inputSource) throws IOException, XMLParseException {
        Document document = null;
        try {
            if (ms_DocumentBuilderFactory == null) {
                ms_DocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            }
            document = ms_DocumentBuilderFactory.newDocumentBuilder().parse(inputSource);
        }
        catch (IOException iOException) {
            throw iOException;
        }
        catch (Exception exception) {
            throw new XMLParseException("##__root__##", "Could not parse XML document: " + exception.getMessage());
        }
        return document;
    }

    public static Document readXMLDocument(InputStream inputStream) throws IOException, XMLParseException {
        return XMLUtil.readXMLDocument(new InputSource(inputStream));
    }

    public static Document readXMLDocument(Reader reader) throws IOException, XMLParseException {
        return XMLUtil.readXMLDocument(new InputSource(reader));
    }

    public static Document readXMLDocument(File file) throws IOException, XMLParseException {
        FileInputStream fileInputStream = new FileInputStream(file);
        IOException iOException = null;
        XMLParseException xMLParseException = null;
        Document document = null;
        try {
            document = XMLUtil.readXMLDocument(fileInputStream);
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        catch (XMLParseException xMLParseException2) {
            xMLParseException = xMLParseException2;
        }
        try {
            fileInputStream.close();
        }
        catch (IOException iOException3) {
            // empty catch block
        }
        if (iOException != null) {
            throw iOException;
        }
        if (xMLParseException != null) {
            throw xMLParseException;
        }
        return document;
    }

    public static void write(Document document, OutputStream outputStream) throws IOException {
        XMLUtil.formatHumanReadable(document);
        outputStream.write(XMLUtil.toString(document).getBytes("UTF8"));
        outputStream.flush();
    }

    public static void write(Document document, Writer writer) throws IOException {
        XMLUtil.formatHumanReadable(document);
        writer.write(XMLUtil.toString(document));
        writer.flush();
    }

    public static void write(Document document, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        XMLUtil.write(document, fileOutputStream);
        fileOutputStream.close();
    }

    public static Document toXMLDocument(String string) throws XMLParseException {
        if (string == null) {
            return XMLUtil.toXMLDocument((byte[])null);
        }
        InputSource inputSource = new InputSource(new StringReader(string));
        try {
            return XMLUtil.readXMLDocument(inputSource);
        }
        catch (XMLParseException xMLParseException) {
            throw xMLParseException;
        }
        catch (IOException iOException) {
            throw new XMLParseException("##__root__##", "Could not parse XML document: " + iOException.getMessage());
        }
    }

    public static Document toXMLDocument(char[] arrc) throws XMLParseException {
        return XMLUtil.toXMLDocument(new String(arrc));
    }

    public static Document toXMLDocument(byte[] arrby) throws XMLParseException {
        Document document;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrby);
        InputSource inputSource = new InputSource(byteArrayInputStream);
        try {
            document = XMLUtil.readXMLDocument(inputSource);
        }
        catch (XMLParseException xMLParseException) {
            throw xMLParseException;
        }
        catch (Exception exception) {
            throw new XMLParseException("##__root__##", "Could not parse XML document: " + exception.getMessage());
        }
        return document;
    }

    public static Document toXMLDocument(IXMLEncodable iXMLEncodable) {
        Document document = null;
        try {
            Element element = XMLUtil.toXMLElement(iXMLEncodable);
            document = element.getOwnerDocument();
            document.appendChild(element);
        }
        catch (Throwable throwable) {
            return null;
        }
        return document;
    }

    public static Document toSignedXMLDocument(IXMLEncodable iXMLEncodable, int n) {
        Document document = XMLUtil.toXMLDocument(iXMLEncodable);
        SignatureCreator.getInstance().signXml(n, document);
        return document;
    }

    public static Element toXMLElement(IXMLEncodable iXMLEncodable) {
        Document document = XMLUtil.createDocument();
        if (document == null) {
            return null;
        }
        Element element = iXMLEncodable.toXmlElement(document);
        return element;
    }

    public static final byte[] createDocumentStructure() {
        try {
            return XMLUtil.toByteArrayOutputStream(XMLUtil.createDocument()).toByteArray();
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private static ByteArrayOutputStream toByteArrayOutputStream(Node node) {
        Class<?> class_;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
        }
        catch (Throwable throwable) {
            return null;
        }
        try {
            class_ = Class.forName("com.sun.xml.tree.ParentNode");
            if (class_.isInstance(node)) {
                Document document = null;
                document = node instanceof Document ? (Document)node : node.getOwnerDocument();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)byteArrayOutputStream, "UTF8");
                Class<?> class_2 = Class.forName("com.sun.xml.tree.XmlDocument");
                Class[] arrclass = new Class[]{class$java$io$Writer == null ? (class$java$io$Writer = XMLUtil.class$("java.io.Writer")) : class$java$io$Writer, Integer.TYPE};
                Method method = class_2.getMethod("createWriteContext", arrclass);
                Object[] arrobject = new Object[]{outputStreamWriter, new Integer(2)};
                Object object = method.invoke(document, arrobject);
                arrclass = new Class[]{Class.forName("com.sun.xml.tree.XmlWriteContext")};
                Method method2 = node.getClass().getMethod("writeXml", arrclass);
                arrobject = new Object[]{object};
                method2.invoke(node, arrobject);
                ((Writer)outputStreamWriter).flush();
                return byteArrayOutputStream;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            Class<?>[] arrclass;
            class_ = Class.forName("javax.xml.transform.TransformerFactory");
            Object object = class_.getMethod("newInstance", null).invoke(class_, null);
            Object object2 = class_.getMethod("newTransformer", null).invoke(object, null);
            Class<?> class_3 = Class.forName("javax.xml.transform.stream.StreamResult");
            Object obj = class_3.getConstructor(class$java$io$OutputStream == null ? (class$java$io$OutputStream = XMLUtil.class$("java.io.OutputStream")) : class$java$io$OutputStream).newInstance(byteArrayOutputStream);
            Class<?> class_4 = Class.forName("javax.xml.transform.dom.DOMSource");
            Object obj2 = class_4.getConstructor(class$org$w3c$dom$Node == null ? (class$org$w3c$dom$Node = XMLUtil.class$("org.w3c.dom.Node")) : class$org$w3c$dom$Node).newInstance(node);
            Class<?> class_5 = Class.forName("javax.xml.transform.Transformer");
            Method method = null;
            Method[] arrmethod = class_5.getMethods();
            for (int i = 0; !(i >= arrmethod.length || arrmethod[i].getName().equals("transform") && (arrclass = (method = arrmethod[i]).getParameterTypes()).length == 2); ++i) {
            }
            Object[] arrobject = new Object[]{obj2, obj};
            method.invoke(object2, arrobject);
            return byteArrayOutputStream;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    private static Node getFirstChildByNameUsingDeepSearchInternal(Node node, String string) {
        block5: {
            try {
                if (node.getNodeName().equals(string)) {
                    return node;
                }
                if (node.hasChildNodes()) {
                    NodeList nodeList = node.getChildNodes();
                    for (int i = 0; i < nodeList.getLength(); ++i) {
                        Node node2 = XMLUtil.getFirstChildByNameUsingDeepSearchInternal(nodeList.item(i), string);
                        if (node2 == null) continue;
                        return node2;
                    }
                    break block5;
                }
                return null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    private static int formatHumanReadable(Node node, int n) {
        int n2;
        Object object;
        int n3 = 0;
        if (!m_bCheckedHumanReadableFormatting) {
            Document document = XMLUtil.createDocument();
            Element element = document.createElement("test1");
            document.appendChild(element);
            element.appendChild(document.createElement("test2"));
            element.appendChild(document.createElement("test3"));
            object = new StringTokenizer(XMLUtil.toString(element), "\n");
            n2 = 0;
            while (((StringTokenizer)object).hasMoreTokens()) {
                ++n2;
                ((StringTokenizer)object).nextToken();
            }
            if (n2 == 4) {
                m_bNeedsHumanReadableFormatting = false;
            }
            m_bCheckedHumanReadableFormatting = true;
        }
        if (!m_bNeedsHumanReadableFormatting) {
            return 0;
        }
        if (node.getNodeType() == 1 && XMLUtil.parseAttribute(node, "xml:space", "").equals("preserve")) {
            return 0;
        }
        if (node.hasChildNodes()) {
            object = node.getChildNodes();
            for (n2 = 0; n2 < object.getLength(); ++n2) {
                n2 += XMLUtil.formatHumanReadable(object.item(n2), n + 1);
            }
        }
        if (node.getNodeType() == 3) {
            object = node.getNodeValue();
            for (n2 = 0; n2 < SPECIAL_CHARS.length; ++n2) {
                object = Util.replaceAll((String)object, SPECIAL_CHARS[n2], ENTITIES[n2], (String[])(SPECIAL_CHARS[n2].equals("&") ? ENTITIES : null));
            }
            node.setNodeValue((String)object);
        }
        if (node.getNodeType() == 3 && (node.getNodeValue() == null || node.getNodeValue().trim().length() == 0 && node.getNodeValue().indexOf(10) == -1)) {
            if (XMLUtil.getNextSibling(node) == null && (node.getPreviousSibling() == null || node.getPreviousSibling().getNodeType() != 3 || node.getPreviousSibling().getNodeValue().indexOf(10) == -1)) {
                String string = new String();
                for (int i = 0; i < n - 1; ++i) {
                    string = string + DEFAULT_FORMAT_SPACE;
                }
                Text text = node.getOwnerDocument().createTextNode(string);
                node.getParentNode().appendChild(text);
                n3 = 0;
            } else {
                n3 = -1;
            }
            node.getParentNode().removeChild(node);
            return n3;
        }
        if (node.getOwnerDocument().getDocumentElement() != node && node.getNodeType() != 3) {
            Text text;
            Node node2 = XMLUtil.getNextSibling(node);
            object = new StringBuffer();
            for (n2 = 0; n2 < n; ++n2) {
                ((StringBuffer)object).append(DEFAULT_FORMAT_SPACE);
            }
            String string = ((StringBuffer)object).toString();
            if (node == node.getParentNode().getFirstChild()) {
                text = node.getOwnerDocument().createTextNode("\n" + string);
                node.getParentNode().insertBefore(text, node);
                ++n3;
            }
            if ((node2 = XMLUtil.getNextSibling(node)) != null && node2.getNodeType() != 3) {
                text = node.getOwnerDocument().createTextNode("\n" + string);
                node.getParentNode().insertBefore(text, node2);
                ++n3;
            } else if (node2 == null) {
                string = string.substring(0, string.length() - DEFAULT_FORMAT_SPACE.length());
                text = node.getOwnerDocument().createTextNode("\n" + string);
                node.getParentNode().appendChild(text);
                ++n3;
            }
        }
        return n3;
    }

    private static int removeCommentsInternal(Node node, Node node2) {
        if (node.getNodeType() == 1 && XMLUtil.parseAttribute(node, "xml:space", "").equals("preserve")) {
            return 0;
        }
        if (node.getNodeType() == 8) {
            node2.removeChild(node);
            return 1;
        }
        if (node.getNodeType() == 3 && node.getNodeValue().trim().length() == 0) {
            node2.removeChild(node);
            return 1;
        }
        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                i -= XMLUtil.removeCommentsInternal(nodeList.item(i), node);
            }
        }
        return 0;
    }

    public static String stripNewlineFromHash(String string) {
        String string2 = string.substring(28);
        if (string.length() == 29 && string2.equals("\n")) {
            string = string.substring(0, 28);
        }
        return string;
    }

    public static String toString(IXMLEncodable iXMLEncodable) {
        return XMLUtil.toString(XMLUtil.toXMLElement(iXMLEncodable));
    }

    public static void printXmlEncodable(IXMLEncodable iXMLEncodable) {
        System.out.println(XMLUtil.toString(iXMLEncodable));
    }

    public static BigInteger parseValue(Element element, BigInteger bigInteger) {
        try {
            String string = XMLUtil.parseValue((Node)element, (String)null);
            if (string == null) {
                return bigInteger;
            }
            return new BigInteger(string.trim());
        }
        catch (Exception exception) {
            return bigInteger;
        }
    }

    public static void setValue(Element element, BigInteger bigInteger) {
        try {
            XMLUtil.setValue((Node)element, bigInteger.toString());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static Document createDocumentFromElement(Element element) throws XMLParseException {
        Document document = XMLUtil.createDocument();
        Node node = XMLUtil.importNode(document, element, true);
        document.appendChild(node);
        return document;
    }

    public static String filterXMLChars(String string) {
        if (string == null) {
            return null;
        }
        String string2 = Util.replaceAll(string, "&", "&#38;");
        string2 = Util.replaceAll(string2, "<", "&#60;");
        string2 = Util.replaceAll(string2, ">", "&#62;");
        string2 = Util.replaceAll(string2, "\"", "&#34;");
        return string2;
    }

    public static void filterXMLCharsForAnObject(Object object) {
        if (object == null) {
            return;
        }
        Class<?> class_ = object.getClass();
        Method[] arrmethod = class_.getMethods();
        Method method = null;
        Method method2 = null;
        int n = 0;
        String string = null;
        String string2 = null;
        for (int i = 0; i < arrmethod.length; ++i) {
            if (arrmethod[i].getParameterTypes().length != 1) continue;
            n = arrmethod[i].getModifiers();
            if (!arrmethod[i].getParameterTypes()[0].equals(class$java$lang$String == null ? XMLUtil.class$("java.lang.String") : class$java$lang$String) || !arrmethod[i].getName().startsWith("set") || !Modifier.isPublic(n) || Modifier.isStatic(n)) continue;
            method2 = null;
            string2 = null;
            method = arrmethod[i];
            string = method.getName().substring(3);
            if (string == null || string.equals("")) continue;
            try {
                method2 = class_.getMethod("get" + string, null);
                if (method2 == null || !method2.getReturnType().equals(class$java$lang$String == null ? XMLUtil.class$("java.lang.String") : class$java$lang$String) || (string2 = (String)method2.invoke(object, null)) == null) continue;
                string2 = XMLUtil.filterXMLChars(string2);
                method.invoke(object, string2);
                continue;
            }
            catch (SecurityException securityException) {
                continue;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                continue;
            }
            catch (IllegalAccessException illegalAccessException) {
                continue;
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
    }

    public static String restoreFilteredXMLChars(String string) {
        if (string == null) {
            return null;
        }
        String string2 = Util.replaceAll(string, "&#38;", "&");
        string2 = Util.replaceAll(string2, "&#60;", "<");
        string2 = Util.replaceAll(string2, "&#62;", ">");
        string2 = Util.replaceAll(string2, "&#34;", "\"");
        return string2;
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
        m_bCheckedHumanReadableFormatting = false;
        m_bNeedsHumanReadableFormatting = true;
        ms_storageMode = 0;
        SPECIAL_CHARS = new String[]{"&", "<", ">"};
        ENTITIES = new String[]{"&amp;", "&lt;", "&gt;"};
        if (ms_DocumentBuilderFactory == null) {
            try {
                ms_DocumentBuilderFactory = DocumentBuilderFactory.newInstance();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

