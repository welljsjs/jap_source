/*
 * Decompiled with CFR 0.150.
 */
package anon.crypto.tinytls.ciphersuites;

import anon.crypto.tinytls.TLSException;
import anon.crypto.tinytls.ciphersuites.CipherSuite;
import anon.crypto.tinytls.keyexchange.DHE_RSA_Key_Exchange;
import anon.util.ByteArrayUtil;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class DHE_RSA_WITH_AES_128_CBC_SHA
extends CipherSuite {
    public DHE_RSA_WITH_AES_128_CBC_SHA() throws TLSException {
        super(new byte[]{0, 51});
        this.m_ciphersuitename = "TLS_DHE_RSA_WITH_AES_128_CBC_SHA";
        this.setKeyExchangeAlgorithm(new DHE_RSA_Key_Exchange());
    }

    protected void calculateKeys(byte[] arrby, boolean bl) {
        this.m_clientwritekey = ByteArrayUtil.copy(arrby, 40, 16);
        this.m_serverwritekey = ByteArrayUtil.copy(arrby, 56, 16);
        this.m_clientwriteIV = ByteArrayUtil.copy(arrby, 72, 16);
        this.m_serverwriteIV = ByteArrayUtil.copy(arrby, 88, 16);
        if (bl) {
            this.m_clientmacsecret = ByteArrayUtil.copy(arrby, 0, 20);
            this.m_servermacsecret = ByteArrayUtil.copy(arrby, 20, 20);
            this.m_encryptcipher = new CBCBlockCipher(new AESFastEngine());
            this.m_encryptcipher.init(true, new ParametersWithIV(new KeyParameter(this.m_clientwritekey), this.m_clientwriteIV));
            this.m_decryptcipher = new CBCBlockCipher(new AESFastEngine());
            this.m_decryptcipher.init(false, new ParametersWithIV(new KeyParameter(this.m_serverwritekey), this.m_serverwriteIV));
        } else {
            this.m_servermacsecret = ByteArrayUtil.copy(arrby, 0, 20);
            this.m_clientmacsecret = ByteArrayUtil.copy(arrby, 20, 20);
            this.m_encryptcipher = new CBCBlockCipher(new AESFastEngine());
            this.m_encryptcipher.init(true, new ParametersWithIV(new KeyParameter(this.m_serverwritekey), this.m_serverwriteIV));
            this.m_decryptcipher = new CBCBlockCipher(new AESFastEngine());
            this.m_decryptcipher.init(false, new ParametersWithIV(new KeyParameter(this.m_clientwritekey), this.m_clientwriteIV));
        }
    }
}

