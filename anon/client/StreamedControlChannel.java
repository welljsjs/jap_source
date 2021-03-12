/*
 * Decompiled with CFR 0.150.
 */
package anon.client;

import anon.IServiceContainer;
import anon.client.AbstractControlChannel;
import anon.client.Multiplexer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import logging.LogHolder;
import logging.LogType;

public abstract class StreamedControlChannel
extends AbstractControlChannel {
    private byte[] m_messageBuffer = new byte[0];
    private int m_currentIndex = -2;
    private byte[] m_lengthBuffer = new byte[2];
    private boolean m_bIsEncrypted;

    public StreamedControlChannel(int n, Multiplexer multiplexer, IServiceContainer iServiceContainer, boolean bl) {
        super(n, multiplexer, iServiceContainer);
        this.m_bIsEncrypted = bl;
    }

    public int sendByteMessage(byte[] arrby) {
        if (arrby.length > 65535) {
            return -31;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeShort(arrby.length);
            dataOutputStream.flush();
            byte[] arrby2 = null;
            if (this.m_bIsEncrypted && this.m_parentMultiplexer.getControlChannelCipher() != null) {
                arrby2 = new byte[this.m_parentMultiplexer.getControlChannelCipher().getEncryptedOutputSize(arrby.length)];
                this.m_parentMultiplexer.getControlChannelCipher().encryptGCM1(arrby, 0, arrby2, 0, arrby.length);
            } else {
                arrby2 = arrby;
            }
            byteArrayOutputStream.write(arrby2);
            byteArrayOutputStream.flush();
        }
        catch (Exception exception) {
            LogHolder.log(1, LogType.NET, exception);
        }
        return this.sendRawMessage(byteArrayOutputStream.toByteArray());
    }

    protected void processPacketData(byte[] arrby) {
        int n = 0;
        while (n < arrby.length) {
            int n2;
            if (this.m_currentIndex < 0) {
                n2 = Math.min(-this.m_currentIndex, arrby.length - n);
                System.arraycopy(arrby, n, this.m_lengthBuffer, this.m_lengthBuffer.length + this.m_currentIndex, n2);
                this.m_currentIndex += n2;
                n += n2;
                if (this.m_currentIndex == 0) {
                    try {
                        int n3 = new DataInputStream(new ByteArrayInputStream(this.m_lengthBuffer)).readUnsignedShort();
                        if (this.m_bIsEncrypted && this.m_parentMultiplexer.getControlChannelCipher() != null) {
                            n3 = this.m_parentMultiplexer.getControlChannelCipher().getEncryptedOutputSize(n3);
                        }
                        this.m_messageBuffer = new byte[n3];
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }
            if (this.m_currentIndex >= 0 && this.m_currentIndex < this.m_messageBuffer.length) {
                n2 = Math.min(this.m_messageBuffer.length - this.m_currentIndex, arrby.length - n);
                System.arraycopy(arrby, n, this.m_messageBuffer, this.m_currentIndex, n2);
                this.m_currentIndex += n2;
                n += n2;
            }
            if (this.m_currentIndex != this.m_messageBuffer.length) continue;
            byte[] arrby2 = null;
            if (this.m_bIsEncrypted && this.m_parentMultiplexer.getControlChannelCipher() != null) {
                arrby2 = new byte[this.m_parentMultiplexer.getControlChannelCipher().getDecryptedOutputSize(this.m_messageBuffer.length)];
                try {
                    this.m_parentMultiplexer.getControlChannelCipher().decryptGCM2(this.m_messageBuffer, 0, arrby2, 0, this.m_messageBuffer.length);
                }
                catch (Exception exception) {
                    arrby2 = null;
                }
            } else {
                arrby2 = this.m_messageBuffer;
            }
            this.processMessage(arrby2);
            this.m_messageBuffer = new byte[0];
            this.m_currentIndex = -2;
        }
    }

    protected abstract void processMessage(byte[] var1);
}

