/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.keyexchange;

import anon.crypto.IMyPrivateKey;
import anon.crypto.JAPCertificate;
import anon.crypto.MyDSAPrivateKey;
import anon.crypto.MyDSAPublicKey;
import anon.crypto.MyDSASignature;
import anon.crypto.tinytls.TLSException;
import anon.crypto.tinytls.keyexchange.Key_Exchange;
import anon.crypto.tinytls.util.PRF;
import anon.crypto.tinytls.util.hash;
import anon.util.ByteArrayUtil;
import java.math.BigInteger;
import java.security.SecureRandom;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class DHE_DSS_Key_Exchange
extends Key_Exchange {
    private static final int MAXKEYMATERIALLENGTH = 104;
    private static final byte[] CLIENTFINISHEDLABEL = "client finished".getBytes();
    private static final byte[] SERVERFINISHEDLABEL = "server finished".getBytes();
    private static final byte[] KEYEXPANSION = "key expansion".getBytes();
    private static final byte[] MASTERSECRET = "master secret".getBytes();
    private static final BigInteger SAFEPRIME = new BigInteger("00FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381FFFFFFFFFFFFFFFF", 16);
    private static final DHParameters DH_PARAMS = new DHParameters(SAFEPRIME, new BigInteger("2"));
    private DHParameters m_dhparams;
    private DHPublicKeyParameters m_dhserverpub;
    private byte[] m_premastersecret;
    private byte[] m_mastersecret;
    private byte[] m_clientrandom;
    private byte[] m_serverrandom;
    private DHBasicAgreement m_dhe = null;

    public byte[] generateServerKeyExchange(IMyPrivateKey iMyPrivateKey, byte[] arrby, byte[] arrby2) throws TLSException {
        if (!(iMyPrivateKey instanceof MyDSAPrivateKey)) {
            throw new TLSException("wrong key type (cannot cast to MyDSAPrivateKey)");
        }
        MyDSAPrivateKey myDSAPrivateKey = (MyDSAPrivateKey)iMyPrivateKey;
        this.m_clientrandom = arrby;
        this.m_serverrandom = arrby2;
        DHKeyGenerationParameters dHKeyGenerationParameters = new DHKeyGenerationParameters(new SecureRandom(), DH_PARAMS);
        DHKeyPairGenerator dHKeyPairGenerator = new DHKeyPairGenerator();
        dHKeyPairGenerator.init(dHKeyGenerationParameters);
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = dHKeyPairGenerator.generateKeyPair();
        DHPublicKeyParameters dHPublicKeyParameters = (DHPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        DHPrivateKeyParameters dHPrivateKeyParameters = (DHPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        this.m_dhe = new DHBasicAgreement();
        this.m_dhe.init(dHPrivateKeyParameters);
        byte[] arrby3 = dHPublicKeyParameters.getParameters().getP().toByteArray();
        arrby3 = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby3.length, 2), arrby3);
        byte[] arrby4 = dHPublicKeyParameters.getParameters().getG().toByteArray();
        arrby4 = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby4.length, 2), arrby4);
        byte[] arrby5 = dHPublicKeyParameters.getY().toByteArray();
        arrby5 = ByteArrayUtil.conc(ByteArrayUtil.inttobyte(arrby5.length, 2), arrby5);
        byte[] arrby6 = ByteArrayUtil.conc(arrby3, arrby4, arrby5);
        byte[] arrby7 = ByteArrayUtil.conc(arrby, arrby2, arrby6);
        MyDSASignature myDSASignature = new MyDSASignature();
        try {
            myDSASignature.initSign(myDSAPrivateKey);
        }
        catch (Exception exception) {
            throw new TLSException("wrong key type (cannot init signature algorithm (" + exception.getMessage() + "))");
        }
        byte[] arrby8 = myDSASignature.sign(arrby7);
        arrby6 = ByteArrayUtil.conc(arrby6, ByteArrayUtil.inttobyte(arrby8.length, 2), arrby8);
        return arrby6;
    }

    public void processServerKeyExchange(byte[] arrby, int n, int n2, byte[] arrby2, byte[] arrby3, JAPCertificate jAPCertificate) throws TLSException {
        this.m_clientrandom = arrby2;
        this.m_serverrandom = arrby3;
        BigInteger bigInteger = null;
        BigInteger bigInteger2 = null;
        BigInteger bigInteger3 = null;
        int n3 = n;
        byte[] arrby4 = null;
        int n4 = (arrby[n] & 0xFF) << 8 | arrby[n + 1] & 0xFF;
        arrby4 = ByteArrayUtil.copy(arrby, n += 2, n4);
        n += n4;
        bigInteger = new BigInteger(1, arrby4);
        LogHolder.log(7, LogType.MISC, "[SERVER_KEY_EXCHANGE] DH_P = " + bigInteger.toString());
        n4 = (arrby[n] & 0xFF) << 8 | arrby[n + 1] & 0xFF;
        arrby4 = ByteArrayUtil.copy(arrby, n += 2, n4);
        n += n4;
        bigInteger2 = new BigInteger(1, arrby4);
        LogHolder.log(7, LogType.MISC, "[SERVER_KEY_EXCHANGE] DH_G = " + bigInteger2.toString());
        n4 = (arrby[n] & 0xFF) << 8 | arrby[n + 1] & 0xFF;
        arrby4 = ByteArrayUtil.copy(arrby, n += 2, n4);
        bigInteger3 = new BigInteger(1, arrby4);
        LogHolder.log(7, LogType.MISC, "[SERVER_KEY_EXCHANGE] DH_Ys = " + bigInteger3.toString());
        this.m_dhparams = new DHParameters(bigInteger, bigInteger2);
        this.m_dhserverpub = new DHPublicKeyParameters(bigInteger3, this.m_dhparams);
        byte[] arrby5 = ByteArrayUtil.copy(arrby, n3, (n += n4) - n3);
        byte[] arrby6 = ByteArrayUtil.conc(arrby2, arrby3, arrby5);
        n4 = (arrby[n] & 0xFF) << 8 | arrby[n + 1] & 0xFF;
        n += 2;
        MyDSASignature myDSASignature = new MyDSASignature();
        if (!(jAPCertificate.getPublicKey() instanceof MyDSAPublicKey)) {
            throw new TLSException("cannot decode certificate");
        }
        MyDSAPublicKey myDSAPublicKey = (MyDSAPublicKey)jAPCertificate.getPublicKey();
        try {
            myDSASignature.initVerify(myDSAPublicKey);
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (!myDSASignature.verify(arrby6, 0, arrby6.length, arrby, n, n4)) {
            LogHolder.log(7, LogType.MISC, "[SERVER_KEY_EXCHANGE] Signature wrong");
            throw new TLSException("wrong Signature", 2, 21);
        }
        LogHolder.log(7, LogType.MISC, "[SERVER_KEY_EXCHANGE] Signature ok");
    }

    public byte[] calculateServerFinished(byte[] arrby) {
        PRF pRF = new PRF(this.m_mastersecret, SERVERFINISHEDLABEL, ByteArrayUtil.conc(hash.md5(arrby), hash.sha(arrby)));
        return pRF.calculate(12);
    }

    public void processServerFinished(byte[] arrby, int n, byte[] arrby2) throws TLSException {
        PRF pRF = new PRF(this.m_mastersecret, SERVERFINISHEDLABEL, ByteArrayUtil.conc(hash.md5(arrby2), hash.sha(arrby2)));
        byte[] arrby3 = pRF.calculate(12);
        if (arrby[0] == 20 && arrby[1] == 0 && arrby[2] == 0 && arrby[3] == 12) {
            for (int i = 0; i < arrby3.length; ++i) {
                if (arrby3[i] == arrby[i + 4]) continue;
                throw new TLSException("wrong Server Finished message recieved", 2, 20);
            }
            return;
        }
        throw new TLSException("wrong Server Finished message recieved", 2, 10);
    }

    public void processClientKeyExchange(BigInteger bigInteger) {
        DHPublicKeyParameters dHPublicKeyParameters = new DHPublicKeyParameters(bigInteger, DH_PARAMS);
        this.m_premastersecret = this.m_dhe.calculateAgreement(dHPublicKeyParameters).toByteArray();
        if (this.m_premastersecret[0] == 0) {
            this.m_premastersecret = ByteArrayUtil.copy(this.m_premastersecret, 1, this.m_premastersecret.length - 1);
        }
        PRF pRF = new PRF(this.m_premastersecret, MASTERSECRET, ByteArrayUtil.conc(this.m_clientrandom, this.m_serverrandom));
        this.m_mastersecret = pRF.calculate(48);
        this.m_premastersecret = null;
    }

    public byte[] calculateClientKeyExchange() throws TLSException {
        DHKeyGenerationParameters dHKeyGenerationParameters = new DHKeyGenerationParameters(new SecureRandom(), this.m_dhparams);
        DHKeyPairGenerator dHKeyPairGenerator = new DHKeyPairGenerator();
        dHKeyPairGenerator.init(dHKeyGenerationParameters);
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = dHKeyPairGenerator.generateKeyPair();
        DHPublicKeyParameters dHPublicKeyParameters = (DHPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        DHPrivateKeyParameters dHPrivateKeyParameters = (DHPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        DHBasicAgreement dHBasicAgreement = new DHBasicAgreement();
        dHBasicAgreement.init(dHPrivateKeyParameters);
        this.m_premastersecret = dHBasicAgreement.calculateAgreement(this.m_dhserverpub).toByteArray();
        if (this.m_premastersecret[0] == 0) {
            this.m_premastersecret = ByteArrayUtil.copy(this.m_premastersecret, 1, this.m_premastersecret.length - 1);
        }
        PRF pRF = new PRF(this.m_premastersecret, MASTERSECRET, ByteArrayUtil.conc(this.m_clientrandom, this.m_serverrandom));
        this.m_mastersecret = pRF.calculate(48);
        this.m_premastersecret = null;
        return dHPublicKeyParameters.getY().toByteArray();
    }

    public void processClientFinished(byte[] arrby, byte[] arrby2) throws TLSException {
        PRF pRF = new PRF(this.m_mastersecret, CLIENTFINISHEDLABEL, ByteArrayUtil.conc(hash.md5(arrby2), hash.sha(arrby2)));
    }

    public byte[] calculateClientFinished(byte[] arrby) throws TLSException {
        PRF pRF = new PRF(this.m_mastersecret, CLIENTFINISHEDLABEL, ByteArrayUtil.conc(hash.md5(arrby), hash.sha(arrby)));
        return pRF.calculate(12);
    }

    public byte[] calculateKeys() {
        PRF pRF = new PRF(this.m_mastersecret, KEYEXPANSION, ByteArrayUtil.conc(this.m_serverrandom, this.m_clientrandom));
        return pRF.calculate(104);
    }
}

