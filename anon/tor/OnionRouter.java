/*
 * Decompiled with CFR 0.150.
 */
package anon.tor;

import anon.crypto.MyAES;
import anon.crypto.MyRSA;
import anon.crypto.MyRSAPublicKey;
import anon.crypto.tinytls.util.hash;
import anon.tor.cells.Cell;
import anon.tor.cells.CreateCell;
import anon.tor.cells.RelayCell;
import anon.tor.ordescription.ORDescriptor;
import anon.util.ByteArrayUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import logging.LogHolder;
import logging.LogType;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class OnionRouter {
    private static final BigInteger SAFEPRIME = new BigInteger("00FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381FFFFFFFFFFFFFFFF", 16);
    private static final BigInteger MINKEY = new BigInteger(new byte[]{1, 0, 0, 0});
    private static final BigInteger MAXKEY = SAFEPRIME.subtract(MINKEY);
    private static final DHParameters DH_PARAMS = new DHParameters(SAFEPRIME, new BigInteger("2"));
    private ORDescriptor m_description;
    private DHBasicAgreement m_dhe;
    private MyAES m_encryptionEngine;
    private MyAES m_decryptionEngine;
    private OnionRouter m_nextOR;
    private int m_circID;
    private SHA1Digest m_digestDf;
    private SHA1Digest m_digestDb;
    private boolean m_extended;

    public OnionRouter(int n, ORDescriptor oRDescriptor) throws IOException {
        this.m_description = oRDescriptor;
        this.m_circID = n;
        this.m_nextOR = null;
        this.m_extended = false;
    }

    public ORDescriptor getDescription() {
        return this.m_description;
    }

    public synchronized RelayCell encryptCell(RelayCell relayCell) throws Exception {
        if (this.m_nextOR != null) {
            relayCell = this.m_nextOR.encryptCell(relayCell);
        } else {
            relayCell.generateDigest(this.m_digestDf);
        }
        relayCell.doCryptography(this.m_encryptionEngine);
        return relayCell;
    }

    public synchronized RelayCell decryptCell(RelayCell relayCell) throws Exception {
        RelayCell relayCell2 = relayCell;
        relayCell2.doCryptography(this.m_decryptionEngine);
        if (this.m_nextOR != null && this.m_extended) {
            relayCell2 = this.m_nextOR.decryptCell(relayCell2);
        } else {
            relayCell2.checkDigest(this.m_digestDb);
        }
        return relayCell2;
    }

    public CreateCell createConnection() throws Exception {
        CreateCell createCell = new CreateCell(this.m_circID);
        createCell.setPayload(this.createExtendOnionSkin(), 0);
        return createCell;
    }

    public boolean checkCreatedCell(Cell cell) {
        try {
            this.checkExtendParameters(cell.getPayload(), 0, 148);
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private RelayCell extendConnection(String string, int n) throws IOException, InvalidCipherTextException, Exception {
        byte[] arrby = ByteArrayUtil.conc(InetAddress.getByName(string).getAddress(), ByteArrayUtil.inttobyte(n, 2), this.createExtendOnionSkin());
        MyRSAPublicKey myRSAPublicKey = this.m_description.getSigningKey();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DEROutputStream dEROutputStream = new DEROutputStream(byteArrayOutputStream);
        dEROutputStream.writeObject(myRSAPublicKey.getAsSubjectPublicKeyInfo().getPublicKey());
        dEROutputStream.flush();
        byte[] arrby2 = byteArrayOutputStream.toByteArray();
        byte[] arrby3 = hash.sha(arrby2);
        arrby = ByteArrayUtil.conc(arrby, arrby3);
        RelayCell relayCell = new RelayCell(this.m_circID, 6, 0, arrby);
        return relayCell;
    }

    public RelayCell extendConnection(ORDescriptor oRDescriptor) throws IOException, InvalidCipherTextException, Exception {
        RelayCell relayCell;
        if (this.m_nextOR == null) {
            this.m_nextOR = new OnionRouter(this.m_circID, oRDescriptor);
            relayCell = this.m_nextOR.extendConnection(oRDescriptor.getAddress(), oRDescriptor.getPort());
            relayCell.generateDigest(this.m_digestDf);
        } else {
            relayCell = this.m_nextOR.extendConnection(oRDescriptor);
        }
        relayCell.doCryptography(this.m_encryptionEngine);
        return relayCell;
    }

    public boolean checkExtendedCell(RelayCell relayCell) {
        try {
            if (this.m_nextOR == null) {
                this.checkExtendParameters(relayCell.getPayload(), 11, 148);
                LogHolder.log(7, LogType.MISC, "[TOR] Circuit '" + this.m_circID + "' Extended");
                return true;
            }
            relayCell.doCryptography(this.m_decryptionEngine);
            if (!this.m_extended) {
                relayCell.checkDigest(this.m_digestDb);
                this.m_extended = this.m_nextOR.checkExtendedCell(relayCell);
                if (!this.m_extended) {
                    this.m_nextOR = null;
                }
                return this.m_extended;
            }
            return this.m_nextOR.checkExtendedCell(relayCell);
        }
        catch (Exception exception) {
            return false;
        }
    }

    private byte[] createExtendOnionSkin() throws IOException, InvalidCipherTextException, Exception {
        byte[] arrby = new byte[86];
        byte[] arrby2 = new byte[16];
        MyAES myAES = new MyAES();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(arrby2);
        myAES.init(true, arrby2);
        DHKeyGenerationParameters dHKeyGenerationParameters = new DHKeyGenerationParameters(new SecureRandom(), DH_PARAMS);
        DHKeyPairGenerator dHKeyPairGenerator = new DHKeyPairGenerator();
        dHKeyPairGenerator.init(dHKeyGenerationParameters);
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = dHKeyPairGenerator.generateKeyPair();
        DHPublicKeyParameters dHPublicKeyParameters = (DHPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        DHPrivateKeyParameters dHPrivateKeyParameters = (DHPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        this.m_dhe = new DHBasicAgreement();
        this.m_dhe.init(dHPrivateKeyParameters);
        byte[] arrby3 = dHPublicKeyParameters.getY().toByteArray();
        int n = 0;
        if (arrby3[0] == 0) {
            n = 1;
        }
        System.arraycopy(arrby2, 0, arrby, 0, 16);
        System.arraycopy(arrby3, n, arrby, 16, 70);
        MyRSA myRSA = new MyRSA();
        myRSA.init(this.m_description.getOnionKey());
        arrby = myRSA.processBlockOAEP(arrby, 0, arrby.length);
        byte[] arrby4 = new byte[186];
        System.arraycopy(arrby, 0, arrby4, 0, 128);
        myAES.processBytesCTR(arrby3, 70 + n, arrby4, 128, 58);
        return arrby4;
    }

    private void checkExtendParameters(byte[] arrby, int n, int n2) throws Exception {
        byte[] arrby2 = new byte[128];
        System.arraycopy(arrby, n, arrby2, 0, 128);
        DHPublicKeyParameters dHPublicKeyParameters = new DHPublicKeyParameters(new BigInteger(1, arrby2), DH_PARAMS);
        BigInteger bigInteger = this.m_dhe.calculateAgreement(dHPublicKeyParameters);
        byte[] arrby3 = bigInteger.toByteArray();
        byte[] arrby4 = new byte[129];
        if (arrby3[0] == 0) {
            System.arraycopy(arrby3, 1, arrby4, 0, 128);
        } else {
            System.arraycopy(arrby3, 0, arrby4, 0, 128);
        }
        byte[] arrby5 = hash.sha(arrby4);
        for (int i = 0; i < arrby5.length; ++i) {
            if (arrby5[i] == arrby[i + n + 128]) continue;
            throw new Exception("wrong derivative key");
        }
        if (bigInteger.compareTo(MINKEY) == -1 || bigInteger.compareTo(MAXKEY) == 1) {
            throw new CryptoException("Calculated DH-Key is not in allowed range (KEY:" + bigInteger.doubleValue() + ")");
        }
        if (bigInteger.bitCount() < 16 || 1024 - bigInteger.bitCount() < 16) {
            throw new CryptoException("Calculated DH-Key is not valid. Not enough zeros ore ones");
        }
        arrby4[128] = 1;
        this.m_digestDf = new SHA1Digest();
        byte[] arrby6 = hash.sha(arrby4);
        this.m_digestDf.update(arrby6, 0, 20);
        arrby4[128] = 2;
        this.m_digestDb = new SHA1Digest();
        arrby6 = hash.sha(arrby4);
        this.m_digestDb.update(arrby6, 0, 20);
        arrby4[128] = 3;
        arrby6 = hash.sha(arrby4);
        this.m_encryptionEngine = new MyAES();
        this.m_encryptionEngine.init(true, arrby6, 0, 16);
        byte[] arrby7 = new byte[16];
        System.arraycopy(arrby6, 16, arrby7, 0, 4);
        arrby4[128] = 4;
        arrby6 = hash.sha(arrby4);
        System.arraycopy(arrby6, 0, arrby7, 4, 12);
        this.m_decryptionEngine = new MyAES();
        this.m_decryptionEngine.init(true, arrby7);
    }
}

