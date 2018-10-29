package com.dexshell.common.hashing;

import java.nio.ByteBuffer;

public class Sha224 extends Sha256 {

    public static final int HASH_BYTES = 224 / 8;

    public Sha224() {
        h0 = 0xc1059ed8;
        h1 = 0x367cd507;
        h2 = 0x3070dd17;
        h3 = 0xf70e5939;
        h4 = 0xffc00b31;
        h5 = 0x68581511;
        h6 = 0x64f98fa7;
        h7 = 0xbefa4fa4;
    }

    /**
     * digesting omits the h7 value
     *
     * @return 224 bits hash value
     */
    @Override
    protected byte[] digestInternal() {
        final ByteBuffer hash = ByteBuffer.allocate(HASH_BYTES);
        hash.putInt(h0);
        hash.putInt(h1);
        hash.putInt(h2);
        hash.putInt(h3);
        hash.putInt(h4);
        hash.putInt(h5);
        hash.putInt(h6);
        return hash.array();
    }

    @Override
    protected ShaAlgorithm clone() {
        return new Sha224();
    }
}
