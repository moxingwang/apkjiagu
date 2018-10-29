package com.dexshell.common.hashing;

import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class ShaAlgorithm implements Cloneable {//todo more efficient single byte[] hash, for blockchains

    protected byte[] buffer = new byte[0];
    /*
    Bit count; treated as 64-bit unsigned integer; we don't care about overflow because we only transform to bytes finally
     */
    protected long totalMessageBits = 0;
    protected final int chunkSize;

    public ShaAlgorithm(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    protected void updateMessageLengthBits(long bits) {
        totalMessageBits += bits;
    }

    public final void update(byte[] message) {
        updateMessageLengthBits(message.length * 8L);
        // strip remainder into a buffer
        final int l = buffer.length + message.length;

        if (l < chunkSize) {
            // if not enough bytes available to fill chunk, only update the buffer
            buffer = Arrays.copyOf(buffer, l);
            System.arraycopy(message, 0, buffer, l - message.length, message.length);
            return;
        }

        final int chunkCount = l / chunkSize;
        final byte[] chunks = new byte[chunkCount * chunkSize];
        // copy buffer to current chunks
        System.arraycopy(buffer, 0, chunks, 0, buffer.length);
        // copy part of message that fills up to a multiple of chunk size
        System.arraycopy(message, 0, chunks, buffer.length, chunks.length - buffer.length);
        buffer = new byte[l % chunkSize];
        // copy un-processable part of message to buffer
        System.arraycopy(message, message.length - buffer.length, buffer, 0, buffer.length);
        // update the chunks in the message
        updateInternal(chunks);

    }

    protected abstract void updateInternal(byte[] chunks);

    protected int messageLengthBytes = Long.BYTES;

    protected void updateBuffer() {//todo optimize
        // pad the message
        final int ml = buffer.length;
        byte[] chunk = Arrays.copyOf(buffer, chunkSize);
        chunk[ml] = (byte) 0x80;
        if (ml + 1 + messageLengthBytes > chunkSize) {
            updateInternal(chunk);
            chunk = new byte[chunkSize];
        }
        final byte[] mlBits = getMessageLengthBytes();
        System.arraycopy(mlBits, 0, chunk, chunkSize - messageLengthBytes, messageLengthBytes);
        updateInternal(chunk);
    }

    protected byte[] getMessageLengthBytes() {
        return ByteBuffer.allocate(Long.BYTES).putLong(totalMessageBits).array();
    }

    protected abstract byte[] digestInternal();

    public final byte[] digest() {
        updateBuffer();
        return digestInternal();
    }

    public final byte[] digest(byte[] message) {
        update(message);
        return digest();
    }

    public final byte[] hash(byte[] message) {
        try {
            return this.clone().digest(message);
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    protected abstract ShaAlgorithm clone() throws CloneNotSupportedException;

    //todo abstract reset

    public byte[] hashSingle(byte[] message) {
        // discarding buffer
        final int msgLen = message.length;
        int chunkCount  = (msgLen + 8) / chunkSize + 1;
        final int paddedLength = chunkCount * chunkSize;
        final byte[] padded = new byte[paddedLength];

        System.arraycopy(message, 0, padded, 0, msgLen);
        padded[msgLen] = (byte) 0x80;

        final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).putLong(msgLen * 8L);
        buffer.rewind();
        buffer.get(padded, paddedLength - Long.BYTES, Long.BYTES);

        updateInternal(padded);
        return digestInternal();
    }
}
