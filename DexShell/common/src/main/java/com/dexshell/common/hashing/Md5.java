package com.dexshell.common.hashing;

import com.google.common.io.BaseEncoding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.Integer.rotateLeft;

public class Md5 extends ShaAlgorithm {//todo fix buggy

    private int a0 = 0x67452301;
    private int b0 = 0xefcdab89;
    private int c0 = 0x98badcfe;
    private int d0 = 0x10325476;

    private static final int[] s = {
            7, 12, 17, 22,
            5, 9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
    };

    private static final int[] K = {
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
            0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
            0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
            0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
            0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
            0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
            0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
            0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
            0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
            0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
            0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
            0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
            0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
            0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
            0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
            0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    };

    public Md5() {
        super(64);
    }

    @Override
    protected void updateInternal(byte[] chunks) {
        for (int chunk = 0; chunk < chunks.length / chunkSize; chunk++) {
            // break chunk into 16 31-bit words
            final int[] M = new int[16];
            for (int i = 0; i < 16; i++) {
                M[i] = ByteBuffer.allocate(Integer.BYTES).put(chunks, chunk * chunkSize + i * 4, 4).getInt(0);
            }
            int a = a0;
            int b = b0;
            int c = c0;
            int d = d0;
            for (int i = 0; i < 64; i++) {
                int f;
                final int g;
                if (i < 16) {
                    f = (b & c) | (~b & d);
                    g = i;
                } else if (i < 32) {
                    f = (d & b) | (~d & c);
                    g = (5 * i + 1) % 16;
                } else if (i < 48) {
                    f = b ^ c ^ d;
                    g = (3 * i + 5) % 16;
                } else {
                    f = c ^ (b | ~d);
                    g = (7 * i) % 16;
                }
                f += a + K[i] + M[g];
                a = d;
                d = c;
                c = b;
                b += rotateLeft(f, s[((i >>> 4) << 2) | (i & 3)]);
            }
            a0 += a;
            b0 += b;
            c0 += c;
            d0 += d;
        }
    }

    @Override
    protected byte[] digestInternal() {
        return ByteBuffer.allocate(Integer.BYTES * 4)
                .putInt(a0)
                .putInt(b0)
                .putInt(c0)
                .putInt(d0)
                .array();
    }

    @Override
    protected ShaAlgorithm clone() throws CloneNotSupportedException {
        return new Md5();
    }

    static class MD5 {

        private static final int INIT_A = 0x67452301;
        private static final int INIT_B = (int) 0xEFCDAB89L;
        private static final int INIT_C = (int) 0x98BADCFEL;
        private static final int INIT_D = 0x10325476;

        private static final int[] SHIFT_AMTS = {
                7, 12, 17, 22,
                5, 9, 14, 20,
                4, 11, 16, 23,
                6, 10, 15, 21
        };

        private static final int[] TABLE_T = new int[64];

        static {
            for (int i = 0; i < 64; i++)
                TABLE_T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
        }

        public static byte[] computeMD5(byte[] message) {
            ByteBuffer padded = ByteBuffer.allocate((((message.length + 8) / 64) + 1) * 64).order(ByteOrder.LITTLE_ENDIAN);
            padded.put(message);
            padded.put((byte) 0x80);
            long messageLenBits = (long) message.length * 8;
            padded.putLong(padded.capacity() - 8, messageLenBits);

            padded.rewind();

            int a = INIT_A;
            int b = INIT_B;
            int c = INIT_C;
            int d = INIT_D;
            while (padded.hasRemaining()) {
                // obtain a slice of the buffer from the current position,
                // and view it as an array of 32-bit ints
                IntBuffer chunk = padded.slice().order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
                int originalA = a;
                int originalB = b;
                int originalC = c;
                int originalD = d;
                for (int j = 0; j < 64; j++) {
                    int div16 = j >>> 4;
                    int f = 0;
                    int bufferIndex = j;
                    switch (div16) {
                        case 0:
                            f = (b & c) | (~b & d);
                            break;

                        case 1:
                            f = (b & d) | (c & ~d);
                            bufferIndex = (bufferIndex * 5 + 1) & 0x0F;
                            break;

                        case 2:
                            f = b ^ c ^ d;
                            bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                            break;

                        case 3:
                            f = c ^ (b | ~d);
                            bufferIndex = (bufferIndex * 7) & 0x0F;
                            break;
                    }
                    int temp = b + Integer.rotateLeft(a + f + chunk.get(bufferIndex) + TABLE_T[j], SHIFT_AMTS[(div16 << 2) | (j & 3)]);
                    a = d;
                    d = c;
                    c = b;
                    b = temp;
                }

                a += originalA;
                b += originalB;
                c += originalC;
                d += originalD;
                padded.position(padded.position() + 64);
            }

            ByteBuffer md5 = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
            for (int n : new int[]{a, b, c, d}) {
                md5.putInt(n);
            }
            return md5.array();
        }

        public static String toHexString(byte[] b) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                sb.append(String.format("%02X", b[i] & 0xFF));
            }
            return sb.toString();
        }

    }

}


