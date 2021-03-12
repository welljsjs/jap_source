/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.FixedRatioChannelsDescription;
import anon.client.ITrustModel;
import anon.client.IllegalTCRequestPostConditionException;
import anon.client.MixPacket;
import anon.client.MixParameters;
import anon.client.crypto.ASymMixCipherPlainRSA;
import anon.client.crypto.ASymMixCipherRSAOAEP;
import anon.client.crypto.ControlChannelCipher;
import anon.client.crypto.IASymMixCipher;
import anon.client.crypto.KeyPool;
import anon.client.crypto.SymCipher;
import anon.crypto.SignatureVerifier;
import anon.crypto.XMLEncryption;
import anon.crypto.XMLSignature;
import anon.error.ServiceSignatureException;
import anon.error.TrustException;
import anon.error.UnknownProtocolVersionException;
import anon.infoservice.Database;
import anon.infoservice.MixCascade;
import anon.infoservice.MixInfo;
import anon.infoservice.ServiceOperator;
import anon.terms.TermsAndConditions;
import anon.terms.TermsAndConditionsMixInfo;
import anon.terms.TermsAndConditionsReadException;
import anon.terms.TermsAndConditionsRequest;
import anon.terms.TermsAndConditionsResponseHandler;
import anon.terms.template.TermsAndConditionsTemplate;
import anon.util.Base64;
import anon.util.JAPMessages;
import anon.util.XMLParseException;
import anon.util.XMLUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.Locale;
import logging.LogHolder;
import logging.LogType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KeyExchangeManager {
    private int m_mixCascadeCertificateLock;
    private Object m_internalSynchronization;
    private boolean m_protocolWithTimestamp;
    private boolean m_protocolWithReplay;
    private boolean m_paymentRequired;
    private boolean m_bEnhancedChannelEncryption;
    private boolean m_bWithIntegrityCheck;
    private SymCipher m_firstMixSymmetricCipher;
    private ControlChannelCipher m_controlchannelCipher;
    private boolean m_chainProtocolWithFlowControl;
    private boolean m_chainProtocolWithUpstreamFlowControl;
    private int m_upstreamSendMe;
    private int m_downstreamSendMe;
    private boolean m_bDebug;
    private FixedRatioChannelsDescription m_fixedRatioChannelsDescription;
    private MixParameters[] m_mixParameters;
    private SymCipher m_multiplexerInputStreamCipher;
    private SymCipher m_multiplexerOutputStreamCipher;
    private MixCascade m_cascade;
    private TermsAndConditionsRequest m_tnCRequest;
    private TermsAndConditionsReadException tcrException;
    static /* synthetic */ Class class$anon$infoservice$MixCascade;
    static /* synthetic */ Class class$anon$infoservice$MixInfo;

    public KeyExchangeManager(InputStream inputStream, OutputStream outputStream, MixCascade mixCascade, ITrustModel iTrustModel, boolean bl) throws XMLParseException, ServiceSignatureException, IOException, UnknownProtocolVersionException, TrustException, TermsAndConditionsReadException, IllegalTCRequestPostConditionException {
        block94: {
            this.m_bDebug = false;
            this.tcrException = null;
            this.m_bDebug = bl;
            try {
                Object object;
                Element element;
                Object object2;
                Object object3;
                Object object4;
                Object object5;
                Object object6;
                Object object7;
                Object object8;
                Object object9;
                Object object10;
                Object object11;
                int n;
                int n2;
                this.m_mixCascadeCertificateLock = -1;
                this.m_internalSynchronization = new Object();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                byte[] arrby = new byte[n2];
                for (n2 = dataInputStream.readUnsignedShort(); n2 > 0; n2 -= n) {
                    n = inputStream.read(arrby, arrby.length - n2, n2);
                    if (n != -1) continue;
                    throw new EOFException("EOF detected while reading initial XML structure.");
                }
                Element element2 = XMLUtil.toXMLDocument(arrby).getDocumentElement();
                this.m_cascade = new MixCascade(element2, Long.MAX_VALUE, mixCascade.getId());
                boolean bl2 = false;
                boolean bl3 = false;
                boolean bl4 = false;
                TrustException trustException = null;
                ServiceSignatureException serviceSignatureException = null;
                if (mixCascade.isUserDefined()) {
                    this.m_cascade.setUserDefined(true, mixCascade);
                    bl2 = true;
                    try {
                        iTrustModel.checkTrust(this.m_cascade, true);
                    }
                    catch (TrustException trustException2) {
                        trustException = trustException2;
                    }
                    catch (ServiceSignatureException serviceSignatureException2) {
                        serviceSignatureException = serviceSignatureException2;
                    }
                } else {
                    MixCascade mixCascade2 = (MixCascade)Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = KeyExchangeManager.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).getEntryById(this.m_cascade.getId());
                    if (mixCascade2 != null) {
                        if (!this.m_cascade.compareMixIDs(mixCascade2)) {
                            bl2 = true;
                        }
                        try {
                            iTrustModel.checkTrust(this.m_cascade, true);
                            bl3 = true;
                        }
                        catch (TrustException trustException3) {
                            trustException = trustException3;
                        }
                        catch (ServiceSignatureException serviceSignatureException3) {
                            serviceSignatureException = serviceSignatureException3;
                        }
                        try {
                            iTrustModel.checkTrust(mixCascade2, true);
                            bl4 = true;
                        }
                        catch (TrustException trustException4) {
                        }
                        catch (ServiceSignatureException serviceSignatureException4) {
                            // empty catch block
                        }
                    }
                }
                if (serviceSignatureException != null) {
                    throw serviceSignatureException;
                }
                if (bl2 || bl3 != bl4) {
                    Database.getInstance(class$anon$infoservice$MixCascade == null ? (class$anon$infoservice$MixCascade = KeyExchangeManager.class$("anon.infoservice.MixCascade")) : class$anon$infoservice$MixCascade).update(this.m_cascade);
                    if (this.m_cascade.isUserDefined()) {
                        Database.getInstance(class$anon$infoservice$MixInfo == null ? (class$anon$infoservice$MixInfo = KeyExchangeManager.class$("anon.infoservice.MixInfo")) : class$anon$infoservice$MixInfo).update(new MixInfo(this.m_cascade.getCertPath()));
                    }
                }
                if (trustException != null) {
                    throw trustException;
                }
                if (this.m_cascade.getMixProtocolVersion() == null) {
                    throw new XMLParseException("##__null__##", "MixProtocolVersion (channel) node expected in received XML structure.");
                }
                this.m_protocolWithTimestamp = false;
                this.m_protocolWithReplay = false;
                this.m_bEnhancedChannelEncryption = false;
                this.m_bWithIntegrityCheck = false;
                this.m_paymentRequired = this.m_cascade.isPayment();
                if (!this.m_cascade.isPaymentProtocolSupported()) {
                    throw new UnknownProtocolVersionException(this.m_cascade, "Payment", this.m_cascade.getPaymentProtocolVersion(), 0);
                }
                this.m_firstMixSymmetricCipher = null;
                this.m_controlchannelCipher = null;
                LogHolder.log(7, LogType.NET, "Cascade is using channel-protocol version '" + this.m_cascade.getMixProtocolVersion() + "'.");
                if (!this.m_cascade.getMixProtocolVersion().equals("0.2")) {
                    if (this.m_cascade.getMixProtocolVersion().equals("0.4")) {
                        this.m_firstMixSymmetricCipher = new SymCipher();
                    } else if (this.m_cascade.getMixProtocolVersion().equals("0.81")) {
                        this.m_protocolWithTimestamp = false;
                        this.m_protocolWithReplay = true;
                        this.m_firstMixSymmetricCipher = new SymCipher();
                    } else if (this.m_cascade.getMixProtocolVersion().equalsIgnoreCase("0.9")) {
                        this.m_firstMixSymmetricCipher = new SymCipher();
                    } else if (this.m_cascade.getMixProtocolVersion().equalsIgnoreCase("0.10")) {
                        this.m_firstMixSymmetricCipher = new SymCipher();
                        this.m_bEnhancedChannelEncryption = true;
                    } else if (this.m_cascade.getMixProtocolVersion().equalsIgnoreCase("0.11")) {
                        this.m_firstMixSymmetricCipher = new SymCipher();
                        this.m_bEnhancedChannelEncryption = true;
                        this.m_bWithIntegrityCheck = true;
                    } else if (this.m_cascade.getMixProtocolVersion().equalsIgnoreCase("0.12")) {
                        this.m_firstMixSymmetricCipher = new SymCipher();
                        this.m_bEnhancedChannelEncryption = true;
                        this.m_bWithIntegrityCheck = true;
                    } else {
                        throw new UnknownProtocolVersionException(this.m_cascade, "Mix", this.m_cascade.getMixProtocolVersion(), 0);
                    }
                }
                this.m_mixParameters = new MixParameters[this.m_cascade.getNumberOfMixes()];
                this.m_tnCRequest = new TermsAndConditionsRequest();
                for (int i = 0; i < this.m_cascade.getNumberOfMixes(); ++i) {
                    Object object12;
                    object11 = this.m_cascade.getMixInfo(i);
                    if (object11 == null) {
                        throw new XMLParseException("Could not get MixInfo object for Mix " + i + "!");
                    }
                    if (i > 0 && SignatureVerifier.getInstance().isCheckSignatures() && !((MixInfo)object11).isVerified()) {
                        throw new ServiceSignatureException(this.m_cascade, "Received XML structure has an invalid signature for Mix " + Integer.toString(i + 1) + ".", i);
                    }
                    object10 = ((MixInfo)object11).getXmlStructure();
                    object9 = null;
                    object9 = this.m_bEnhancedChannelEncryption ? new ASymMixCipherRSAOAEP() : new ASymMixCipherPlainRSA();
                    this.m_mixParameters[i] = new MixParameters(((MixInfo)object11).getId(), (IASymMixCipher)object9);
                    if (this.m_mixParameters[i].getMixCipher().setPublicKey((Element)object10) != 0) {
                        throw new XMLParseException("Received XML structure contains an invalid public key for Mix " + Integer.toString(i) + ".");
                    }
                    if (this.m_cascade.isTermsAndConditionsConfirmationRequired()) {
                        object8 = ((MixInfo)object11).getServiceOperator();
                        object7 = ((MixInfo)object11).getTermsAndConditionMixInfo();
                        if (object7 != null) {
                            try {
                                object6 = TermsAndConditions.getTermsAndConditions((ServiceOperator)object8);
                                if (object6 == null || !((TermsAndConditions)object6).isMostRecent(((TermsAndConditionsMixInfo)object7).getDate()) || ((TermsAndConditions)object6).isSignatureObsolete()) {
                                    boolean bl5 = false;
                                    if (object6 != null) {
                                        TermsAndConditions.removeTermsAndConditions((TermsAndConditions)object6);
                                        bl5 = ((TermsAndConditions)object6).isSignatureObsolete() ? ((TermsAndConditions)object6).isAccepted() : false;
                                    }
                                    object6 = new TermsAndConditions((ServiceOperator)object8, ((TermsAndConditionsMixInfo)object7).getDate());
                                    if (!bl5) {
                                        if (this.tcrException == null) {
                                            this.tcrException = new TermsAndConditionsReadException();
                                        }
                                        this.tcrException.addTermsAndConditonsToRead((TermsAndConditions)object6);
                                    } else {
                                        ((TermsAndConditions)object6).setAccepted(true);
                                    }
                                    TermsAndConditions.storeTermsAndConditions((TermsAndConditions)object6);
                                } else if (!((TermsAndConditions)object6).isAccepted()) {
                                    if (this.tcrException == null) {
                                        this.tcrException = new TermsAndConditionsReadException();
                                    }
                                    this.tcrException.addTermsAndConditonsToRead((TermsAndConditions)object6);
                                }
                                object5 = JAPMessages.getLocale();
                                Object object13 = object4 = ((TermsAndConditionsMixInfo)object7).hasTranslation((Locale)object5) ? ((Locale)object5).getLanguage().trim().toLowerCase() : ((TermsAndConditionsMixInfo)object7).getDefaultLanguage();
                                if (!((String)object4).equals(((TermsAndConditionsMixInfo)object7).getDefaultLanguage()) && !((TermsAndConditions)object6).hasDefaultTranslation()) {
                                    this.m_tnCRequest.addCustomizedSectionsRequest((ServiceOperator)object8, ((TermsAndConditionsMixInfo)object7).getDefaultLanguage());
                                    if (TermsAndConditionsTemplate.getById(((TermsAndConditionsMixInfo)object7).getDefaultTemplateRefId(), false) == null) {
                                        this.m_tnCRequest.addTemplateRequest((ServiceOperator)object8, ((TermsAndConditionsMixInfo)object7).getDefaultLanguage(), ((TermsAndConditionsMixInfo)object7).getDefaultTemplateRefId());
                                    }
                                }
                                if (TermsAndConditionsTemplate.getById((String)(object12 = ((TermsAndConditionsMixInfo)object7).getTemplateRefId((String)object4)), false) == null) {
                                    this.m_tnCRequest.addTemplateRequest((ServiceOperator)object8, (String)object4, (String)object12);
                                }
                                if (!((TermsAndConditions)object6).hasTranslation((String)object4)) {
                                    this.m_tnCRequest.addCustomizedSectionsRequest((ServiceOperator)object8, (String)object4);
                                }
                            }
                            catch (ParseException parseException) {
                                LogHolder.log(3, LogType.NET, "tc mix info " + ((TermsAndConditionsMixInfo)object7).getId() + " has an invalid date format: " + ((TermsAndConditionsMixInfo)object7).getDate());
                            }
                        } else {
                            LogHolder.log(4, LogType.NET, "Cascade requires Terms And Conditions confirmation but Mix " + ((MixInfo)object11).getName() + " does not send any TC Infos!");
                        }
                    }
                    if (i == this.m_cascade.getNumberOfMixes() - 1) {
                        object8 = object10.getElementsByTagName("MixProtocolVersion");
                        if (object8.getLength() == 0) {
                            throw new XMLParseException("##__null__##", "MixProtocolVersion (chain) node expected in received XML structure.");
                        }
                        object7 = (Element)object8.item(0);
                        object6 = XMLUtil.parseValue((Node)object7, (String)null);
                        if (object6 == null) {
                            throw new XMLParseException("##__null__##", "MixProtocolVersion (chain) node has no value.");
                        }
                        object6 = ((String)object6).trim();
                        this.m_chainProtocolWithFlowControl = false;
                        this.m_chainProtocolWithUpstreamFlowControl = false;
                        this.m_fixedRatioChannelsDescription = null;
                        LogHolder.log(7, LogType.NET, "Cascade is using chain-protocol version '" + (String)object6 + "'.");
                        if (((String)object6).equals("0.3")) continue;
                        if (((String)object6).equals("0.6")) {
                            this.m_chainProtocolWithFlowControl = true;
                            object5 = XMLUtil.getFirstChildByName((Node)object10, "FlowControl");
                            if (object5 == null) {
                                throw new XMLParseException("##__null__##", "FlowControl node expected in received XML structure.");
                            }
                            object4 = XMLUtil.getFirstChildByName((Node)object5, "UpstreamSendMe");
                            if (object4 == null) {
                                throw new XMLParseException("##__null__##", "UpstreamSendMe node expected in received XML structure.");
                            }
                            object12 = XMLUtil.getFirstChildByName((Node)object5, "DownstreamSendMe");
                            if (object12 == null) {
                                throw new XMLParseException("##__null__##", "DownstreamSendMe node expected in received XML structure.");
                            }
                            this.m_upstreamSendMe = XMLUtil.parseValue((Node)object4, -1);
                            if (this.m_upstreamSendMe <= 0) {
                                throw new XMLParseException("##__null__##", "Got wrong value for UpstreamSendMe in received XML structure.");
                            }
                            this.m_downstreamSendMe = XMLUtil.parseValue((Node)object12, -1);
                            if (this.m_downstreamSendMe <= 0) {
                                throw new XMLParseException("##__null__##", "Got wrong value for DownstreamSendMe in received XML structure.");
                            }
                            this.m_chainProtocolWithUpstreamFlowControl = XMLUtil.parseAttribute((Node)object5, "withUpstreamFlowControl", false);
                            continue;
                        }
                        if (((String)object6).equals("0.5")) {
                            object5 = object10.getElementsByTagName("DownstreamPackets");
                            if (object5.getLength() == 0) {
                                throw new XMLParseException("##__null__##", "DownstreamPackets node expected in received XML structure.");
                            }
                            object4 = (Element)object5.item(0);
                            int n3 = XMLUtil.parseValue((Node)object4, -1);
                            if (n3 < 1) {
                                throw new XMLParseException("DownstreamPackets", "Node has an invalid value.");
                            }
                            object3 = object10.getElementsByTagName("ChannelTimeout");
                            if (object3.getLength() == 0) {
                                throw new XMLParseException("##__null__##", "ChannelTimeout node expected in received XML structure.");
                            }
                            object2 = (Element)object3.item(0);
                            long l = XMLUtil.parseValue((Node)object2, -1);
                            if (l < 1L) {
                                throw new XMLParseException("ChannelTimeout node has an invalid value.");
                            }
                            l = 1000L * l;
                            NodeList nodeList = object10.getElementsByTagName("ChainTimeout");
                            if (nodeList.getLength() == 0) {
                                throw new XMLParseException("##__null__##", "ChainTimeout node expected in received XML structure.");
                            }
                            element = (Element)nodeList.item(0);
                            long l2 = XMLUtil.parseValue((Node)element, -1);
                            if (l2 < 1L) {
                                throw new XMLParseException("ChainTimeout", "Node has an invalid value.");
                            }
                            l2 = 1000L * l2;
                            this.m_fixedRatioChannelsDescription = new FixedRatioChannelsDescription(n3, l, l2);
                            continue;
                        }
                        throw new UnknownProtocolVersionException(this.m_cascade, "Chain", (String)object6, i);
                    }
                    if (i != 0 || XMLUtil.getFirstChildByName((Node)object10, "SupportsEncrypedControlChannels") == null) continue;
                    this.m_controlchannelCipher = new ControlChannelCipher();
                }
                this.m_multiplexerInputStreamCipher = new SymCipher();
                this.m_multiplexerOutputStreamCipher = new SymCipher();
                KeyPool.start(bl);
                LogHolder.log(7, LogType.NET, "Starting key exchange...");
                if (this.m_firstMixSymmetricCipher == null) {
                    object = new MixPacket(0);
                    object11 = "KEYPACKET".getBytes();
                    System.arraycopy(object11, 0, ((MixPacket)object).getPayloadData(), 0, ((Object)object11).length);
                    object10 = new byte[32];
                    KeyPool.getKey((byte[])object10, 0);
                    KeyPool.getKey((byte[])object10, 16);
                    System.arraycopy(object10, 0, ((MixPacket)object).getPayloadData(), ((Object)object11).length, ((Object)object10).length);
                    this.m_mixParameters[0].getMixCipher().encrypt(((MixPacket)object).getPayloadData(), 0, ((MixPacket)object).getPayloadData(), 0);
                    outputStream.write(((MixPacket)object).getRawPacket());
                    this.m_multiplexerInputStreamCipher.setEncryptionKeyAES((byte[])object10, 0, 16);
                    this.m_multiplexerOutputStreamCipher.setEncryptionKeyAES((byte[])object10, 16, 16);
                } else {
                    int n4;
                    int n5;
                    object = XMLUtil.createDocument();
                    if (object == null) {
                        throw new XMLParseException("Cannot create XML document for key exchange.");
                    }
                    object11 = object.createElement("JAPKeyExchange");
                    object11.setAttribute("version", "0.1");
                    object10 = object.createElement("LinkEncryption");
                    object9 = new byte[64];
                    KeyPool.getKey((byte[])object9, 0);
                    KeyPool.getKey((byte[])object9, 16);
                    KeyPool.getKey((byte[])object9, 32);
                    KeyPool.getKey((byte[])object9, 48);
                    this.m_multiplexerOutputStreamCipher.setEncryptionKeyAES((byte[])object9, 0, 32);
                    this.m_multiplexerInputStreamCipher.setEncryptionKeyAES((byte[])object9, 32, 32);
                    XMLUtil.setValue((Node)object10, Base64.encode((byte[])object9, true));
                    object11.appendChild((Node)object10);
                    object8 = object.createElement("MixEncryption");
                    object7 = new byte[32];
                    KeyPool.getKey((byte[])object7, 0);
                    KeyPool.getKey((byte[])object7, 16);
                    this.m_firstMixSymmetricCipher.setEncryptionKeyAES((byte[])object7, 0, 32);
                    XMLUtil.setValue((Node)object8, Base64.encode((byte[])object7, true));
                    object11.appendChild((Node)object8);
                    if (this.m_controlchannelCipher != null) {
                        object6 = object.createElement("ControlChannelEncryption");
                        object5 = new byte[32];
                        KeyPool.getKey((byte[])object5, 0);
                        KeyPool.getKey((byte[])object5, 16);
                        this.m_controlchannelCipher.setSentKey((byte[])object5, 0, 16);
                        this.m_controlchannelCipher.setRecvKey((byte[])object5, 16, 16);
                        XMLUtil.setValue((Node)object6, Base64.encode((byte[])object5, true));
                        object11.appendChild((Node)object6);
                    }
                    object6 = object.createElement("ReplayDetection");
                    if (this.m_protocolWithReplay) {
                        XMLUtil.setValue((Node)object6, "true");
                    } else {
                        XMLUtil.setValue((Node)object6, "false");
                    }
                    object11.appendChild((Node)object6);
                    object5 = XMLEncryption.getEncryptedElement((Element)object11, this.m_mixParameters[0].getMixCipher().getPublicKey());
                    object.appendChild((Node)object5);
                    object4 = new ByteArrayOutputStream();
                    byte[] arrby2 = XMLUtil.toByteArray((Node)object);
                    object3 = new DataOutputStream((OutputStream)object4);
                    ((DataOutputStream)object3).writeShort(arrby2.length);
                    ((DataOutputStream)object3).flush();
                    ((OutputStream)object4).write(arrby2);
                    ((OutputStream)object4).flush();
                    object2 = ((ByteArrayOutputStream)object4).toByteArray();
                    outputStream.write((byte[])object2);
                    outputStream.flush();
                    byte[] arrby3 = new byte[n5];
                    for (n5 = dataInputStream.readUnsignedShort(); n5 > 0; n5 -= n4) {
                        n4 = inputStream.read(arrby3, arrby3.length - n5, n5);
                        if (n4 != -1) continue;
                        throw new EOFException("EOF detected while reading symmetric key signature XML structure.");
                    }
                    Document document = XMLUtil.toXMLDocument(arrby3);
                    element = null;
                    if (this.m_protocolWithReplay) {
                        Element element3 = document.getDocumentElement();
                        Element element4 = (Element)element3.getFirstChild();
                        Element element5 = (Element)element4.getFirstChild();
                        for (int i = 0; i < this.m_cascade.getNumberOfMixes(); ++i) {
                            for (int j = 0; j < this.m_cascade.getNumberOfMixes(); ++j) {
                                if (!element5.getAttribute("id").equals(this.m_mixParameters[j].getMixId())) continue;
                                this.m_mixParameters[j].setReplayOffset(Integer.parseInt(element5.getFirstChild().getFirstChild().getNodeValue()));
                            }
                            element5 = (Element)XMLUtil.getNextSibling(element5);
                        }
                        MixParameters.m_referenceTime = System.currentTimeMillis() / 1000L;
                        element = (Element)element3.getLastChild();
                    } else {
                        element = document.getDocumentElement();
                    }
                    if (element == null) {
                        throw new XMLParseException("##__root__##", "No document element in received symmetric key signature XML structure.");
                    }
                    object.getDocumentElement().appendChild(XMLUtil.importNode((Document)object, element, true));
                    if (!XMLSignature.verifyFast((Node)object, this.m_cascade.getCertPath().getEndEntityKeys())) {
                        throw new ServiceSignatureException(this.m_cascade, "Invalid symmetric keys signature received.", 0);
                    }
                }
                if (!this.m_cascade.isTermsAndConditionsConfirmationRequired()) break block94;
                if (this.m_tnCRequest.hasResourceRequests()) {
                    object = XMLUtil.createDocument();
                    object11 = this.m_tnCRequest.toXmlElement((Document)object);
                    object10 = XMLUtil.toString((Node)object);
                    if (object10 != null) {
                        object9 = new ByteArrayOutputStream();
                        object8 = new DataOutputStream((OutputStream)object9);
                        ((DataOutputStream)object8).writeShort(((String)object10).length());
                        ((DataOutputStream)object8).writeBytes((String)object10);
                        outputStream.write(((ByteArrayOutputStream)object9).toByteArray());
                        outputStream.flush();
                        int n6 = dataInputStream.readInt();
                        object6 = new byte[n6];
                        inputStream.read((byte[])object6, 0, n6);
                        object5 = XMLUtil.toXMLDocument((byte[])object6);
                        if (object5 != null) {
                            try {
                                TermsAndConditionsResponseHandler.get().handleXMLResourceResponse((Document)object5, this.m_tnCRequest);
                            }
                            catch (SignatureException signatureException) {
                                throw new ServiceSignatureException(this.m_cascade, "Could not verify terms and conditions signature: " + signatureException.getMessage());
                            }
                        }
                    }
                }
                object = XMLUtil.createDocument();
                object11 = null;
                if (this.tcrException != null) {
                    object11 = object.createElement("TermsAndConditionsInterrupt");
                } else {
                    object11 = object.createElement("TermsAndConditionsConfirm");
                    XMLUtil.setAttribute((Element)object11, "accepted", true);
                }
                object.appendChild((Node)object11);
                object10 = XMLUtil.toString((Node)object);
                object9 = new ByteArrayOutputStream();
                object8 = new DataOutputStream((OutputStream)object9);
                ((DataOutputStream)object8).writeShort(((String)object10).length());
                ((DataOutputStream)object8).writeBytes((String)object10);
                outputStream.write(((ByteArrayOutputStream)object9).toByteArray());
                outputStream.flush();
                if (this.tcrException != null) {
                    throw this.tcrException;
                }
            }
            catch (ServiceSignatureException serviceSignatureException) {
                this.removeCertificateLock();
                throw serviceSignatureException;
            }
        }
    }

    public boolean isProtocolWithTimestamp() {
        return this.m_protocolWithTimestamp;
    }

    public boolean isPaymentRequired() {
        return this.m_paymentRequired;
    }

    public boolean isChainProtocolWithFlowControl() {
        return this.m_chainProtocolWithFlowControl;
    }

    public boolean isChainProtocolWithUpstreamFlowControl() {
        return this.m_chainProtocolWithUpstreamFlowControl;
    }

    public int getUpstreamSendMe() {
        return this.m_upstreamSendMe;
    }

    public int getDownstreamSendMe() {
        return this.m_downstreamSendMe;
    }

    public FixedRatioChannelsDescription getFixedRatioChannelsDescription() {
        return this.m_fixedRatioChannelsDescription;
    }

    public SymCipher getFirstMixSymmetricCipher() {
        return this.m_firstMixSymmetricCipher;
    }

    public SymCipher getMultiplexerInputStreamCipher() {
        return this.m_multiplexerInputStreamCipher;
    }

    public SymCipher getMultiplexerOutputStreamCipher() {
        return this.m_multiplexerOutputStreamCipher;
    }

    public MixParameters[] getMixParameters() {
        return this.m_mixParameters;
    }

    public MixCascade getConnectedCascade() {
        return this.m_cascade;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeCertificateLock() {
        Object object = this.m_internalSynchronization;
        synchronized (object) {
            if (this.m_mixCascadeCertificateLock != -1) {
                SignatureVerifier.getInstance().getVerificationCertificateStore().removeCertificateLock(this.m_mixCascadeCertificateLock);
                this.m_mixCascadeCertificateLock = -1;
            }
        }
    }

    public boolean isProtocolWithEnhancedChannelEncryption() {
        return this.m_bEnhancedChannelEncryption;
    }

    public boolean isProtocolWithIntegrityCheck() {
        return this.m_bWithIntegrityCheck;
    }

    public ControlChannelCipher getControlChannelCipher() {
        return this.m_controlchannelCipher;
    }

    public boolean isDebug() {
        return this.m_bDebug;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

