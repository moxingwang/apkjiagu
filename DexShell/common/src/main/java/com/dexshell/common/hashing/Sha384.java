package com.dexshell.common.hashing;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class Sha384 extends Sha512 {

    public Sha384() {
        h0 = 0xcbbb9d5dc1059ed8L;
        h1 = 0x629a292a367cd507L;
        h2 = 0x9159015a3070dd17L;
        h3 = 0x152fecd8f70e5939L;
        h4 = 0x67332667ffc00b31L;
        h5 = 0x8eb44a8768581511L;
        h6 = 0xdb0c2e0d64f98fa7L;
        h7 = 0x47b5481dbefa4fa4L;
    }

    /**
     * omits the last two 64-bit hash values
     * @return SHA-384 hash
     */
    @Override
    protected byte[] digestInternal() {
        final ByteBuffer hash = ByteBuffer.allocate(6 * Long.BYTES);
        hash.putLong(h0);
        hash.putLong(h1);
        hash.putLong(h2);
        hash.putLong(h3);
        hash.putLong(h4);
        hash.putLong(h5);
        return hash.array();
    }

    @Override
    protected ShaAlgorithm clone() throws CloneNotSupportedException {
        return new Sha384();
    }

}
