/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.client.AbstractDataChain;
import anon.client.AbstractDataChannel;
import anon.client.FixedRatioChannelsDescription;
import anon.client.IDataChannelFactory;
import anon.client.KeyExchangeManager;
import anon.client.Multiplexer;
import anon.client.SimulatedLimitedDataChannel;
import anon.client.UnlimitedDataChannel;
import anon.client.crypto.DataChannelCipher;
import anon.client.crypto.DefaultMixCipher;
import anon.client.crypto.FirstMixCipher;
import anon.client.crypto.IMixCipher;
import anon.client.crypto.KeyPool;
import anon.client.crypto.LastMixCipher;
import anon.client.crypto.MixCipherChain;
import anon.client.crypto.SymCipher;

public class DefaultDataChannelFactory
implements IDataChannelFactory {
    private static final int SYMMETRIC_CIPHER_KEY_LENGTH = 16;
    private static final int SYMMETRIC_CIPHER_BLOCK_LENGTH = 16;
    private KeyExchangeManager m_keyExchangeManager;
    private Multiplexer m_multiplexer;

    public DefaultDataChannelFactory(KeyExchangeManager keyExchangeManager, Multiplexer multiplexer) {
        this.m_keyExchangeManager = keyExchangeManager;
        this.m_multiplexer = multiplexer;
    }

    public AbstractDataChannel createDataChannel(int n, AbstractDataChain abstractDataChain) {
        IMixCipher[] arriMixCipher = new IMixCipher[this.m_keyExchangeManager.getMixParameters().length];
        for (int i = 0; i < arriMixCipher.length; ++i) {
            byte[] arrby;
            Object object;
            if (i == 0 && this.m_keyExchangeManager.getFirstMixSymmetricCipher() != null) {
                object = new SymCipher();
                if (this.m_keyExchangeManager.isProtocolWithEnhancedChannelEncryption()) {
                    arrby = new byte[32];
                    KeyPool.getKey(arrby);
                    KeyPool.getKey(arrby, 16);
                } else {
                    arrby = new byte[16];
                    KeyPool.getKey(arrby);
                }
                ((SymCipher)object).setEncryptionKeysAES(arrby);
                byte[] arrby2 = new byte[16];
                for (int j = 0; j < arrby2.length; ++j) {
                    arrby2[j] = -1;
                }
                ((SymCipher)object).setIV2(arrby2);
                arriMixCipher[i] = new FirstMixCipher(this.m_keyExchangeManager.getFirstMixSymmetricCipher(), (SymCipher)object);
                continue;
            }
            if (i == arriMixCipher.length - 1 && this.m_keyExchangeManager.isProtocolWithIntegrityCheck()) {
                object = new DataChannelCipher();
                if (this.m_keyExchangeManager.isProtocolWithEnhancedChannelEncryption()) {
                    arrby = new byte[32];
                    KeyPool.getKey(arrby);
                    KeyPool.getKey(arrby, 16);
                } else {
                    arrby = new byte[16];
                    KeyPool.getKey(arrby);
                }
                ((DataChannelCipher)object).setEncryptionKeysAES(arrby);
                arriMixCipher[i] = new LastMixCipher(this.m_keyExchangeManager.getMixParameters()[i], (DataChannelCipher)object, this.m_keyExchangeManager.isDebug());
                continue;
            }
            object = new SymCipher();
            if (this.m_keyExchangeManager.isProtocolWithEnhancedChannelEncryption()) {
                arrby = new byte[32];
                KeyPool.getKey(arrby);
                KeyPool.getKey(arrby, 16);
            } else {
                arrby = new byte[16];
                KeyPool.getKey(arrby);
            }
            ((SymCipher)object).setEncryptionKeysAES(arrby);
            arriMixCipher[i] = new DefaultMixCipher(this.m_keyExchangeManager.getMixParameters()[i], (SymCipher)object);
        }
        FixedRatioChannelsDescription fixedRatioChannelsDescription = this.m_keyExchangeManager.getFixedRatioChannelsDescription();
        if (fixedRatioChannelsDescription == null) {
            return new UnlimitedDataChannel(n, this.m_multiplexer, abstractDataChain, new MixCipherChain(arriMixCipher));
        }
        return new SimulatedLimitedDataChannel(n, this.m_multiplexer, abstractDataChain, new MixCipherChain(arriMixCipher), fixedRatioChannelsDescription.getChannelDownstreamPackets(), fixedRatioChannelsDescription.getChannelTimeout());
    }
}

