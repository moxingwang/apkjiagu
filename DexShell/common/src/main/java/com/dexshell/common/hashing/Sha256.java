package com.dexshell.common.hashing;

import java.nio.ByteBuffer;

import static java.lang.Integer.rotateRight;

public class Sha256 extends ShaAlgorithm {

    protected int h0 = 0x6a09e667;
    protected int h1 = 0xbb67ae85;
    protected int h2 = 0x3c6ef372;
    protected int h3 = 0xa54ff53a;
    protected int h4 = 0x510e527f;
    protected int h5 = 0x9b05688c;
    protected int h6 = 0x1f83d9ab;
    protected int h7 = 0x5be0cd19;

    protected static final int[] k = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2};

    public Sha256() {
        super(64);
    }


    @Override
    protected void updateInternal(byte[] chunks) {
        for (int chunk = 0; chunk < chunks.length / 64; chunk++) {
            final int[] w = new int[64];
            for (int i = 0; i < 16; i++) {
                w[i] = ByteBuffer.allocate(Integer.BYTES).put(chunks, chunk * 64 + i * 4, 4).getInt(0);
            }
            for (int i = 16; i < 64; i++) {
                final int w15 = w[i - 15];
                int s0 = rotateRight(w15, 7) ^ rotateRight(w15, 18) ^ (w15 >>> 3);
                final int w2 = w[i - 2];
                int s1 = rotateRight(w2, 17) ^ rotateRight(w2, 19) ^ (w2 >>> 10);
                w[i] = w[i - 16] + s0 + w[i - 7] + s1;
            }
            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;
            int f = h5;
            int g = h6;
            int h = h7;
            for (int i = 0; i < 64; i++) {
                int s1 = rotateRight(e, 6) ^ rotateRight(e, 11) ^ rotateRight(e, 25);
                int ch = (e & f) ^ ((~e) & g);
                int temp1 = h + s1 + ch + k[i] + w[i];
                int s0 = rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = s0 + maj;

                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }
            h0 += a;
            h1 += b;
            h2 += c;
            h3 += d;
            h4 += e;
            h5 += f;
            h6 += g;
            h7 += h;
        }
    }

    @Override
    protected byte[] digestInternal() {
        return ByteBuffer.allocate(Integer.BYTES * 8)
                .putInt(h0)
                .putInt(h1)
                .putInt(h2)
                .putInt(h3)
                .putInt(h4)
                .putInt(h5)
                .putInt(h6)
                .putInt(h7)
                .array();
    }

    public boolean test(byte[] input) {
        update(input);
        updateBuffer();
        return (h0 & 0xffff_0000) == 0;
    }

    @Override
    protected ShaAlgorithm clone() {
        return new Sha256();
    }

}
