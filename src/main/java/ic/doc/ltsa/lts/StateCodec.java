package ic.doc.ltsa.lts;

public class StateCodec {
    int[] bitSize;
    int NBIT;
    int NBYTE;
    static int[] masks = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431,
        67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE};
    int[] boundaries;

    public StateCodec(int[] paramArrayOfint) {
        this.bitSize = new int[paramArrayOfint.length];
        this.NBIT = 0;
        byte b1 = 1;
        for (int i = paramArrayOfint.length - 1; i >= 0; i--) {
            this.bitSize[i] = nbits(paramArrayOfint[i] - 1);
            if (this.NBIT + this.bitSize[i] > b1 * 64) {
                this.NBIT = b1 * 64;
                b1++;
            }
            this.NBIT += this.bitSize[i];
        }
        this.NBYTE = this.NBIT / 8;
        if (this.NBIT % 8 > 0)
            this.NBYTE++;
        this.boundaries = new int[b1];
        int j = 0;
        byte b2 = 0;
        for (int k = paramArrayOfint.length - 1; k >= 0; k--) {
            if (j + this.bitSize[k] <= 64) {
                j += this.bitSize[k];
            } else {
                this.boundaries[b2] = k + 1;
                j = this.bitSize[k];
                b2++;
            }
        }
        this.boundaries[b2] = 0;
    }

    private void longToBytes(byte[] paramArrayOfbyte, long paramLong, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt2; i++) {
            paramArrayOfbyte[i] = (byte) (paramArrayOfbyte[i] | (byte) (int) paramLong);
            paramLong >>>= 8L;
        }
    }

    private long bytesToLong(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
        long l = 0L;
        for (int i = paramInt2 - 1; i >= paramInt1; i--) {
            l |= paramArrayOfbyte[i] & 0xFFL;
            if (i > paramInt1)
                l <<= 8L;
        }
        return l;
    }

    public int bits() {
        int i = 0;
        for (byte b = 0; b < this.bitSize.length; b++)
            i += this.bitSize[b];
        return i;
    }

    public byte[] zero() {
        return new byte[this.NBYTE];
    }

    public byte[] encode(int[] paramArrayOfint) {
        byte[] arrayOfByte = new byte[this.NBYTE];
        int i = this.bitSize.length - 1;
        int j = this.NBYTE;
        for (byte b = 0; b < this.boundaries.length; b++) {
            long l = 0L;
            for (int k = i; k >= this.boundaries[b]; k--) {
                if (paramArrayOfint[k] < 0)
                    return null;
                l |= paramArrayOfint[k];
                if (k > this.boundaries[b])
                    l <<= this.bitSize[k - 1];
            }
            int m = j - 8;
            if (m < 0)
                m = 0;
            longToBytes(arrayOfByte, l, m, j);
            j = m;
            i = this.boundaries[b] - 1;
        }
        return arrayOfByte;
    }

    public int[] decode(byte[] paramArrayOfbyte) {
        int[] arrayOfInt = new int[this.bitSize.length + 1];
        int i = this.bitSize.length;
        int j = this.NBYTE;
        for (byte b = 0; b < this.boundaries.length; b++) {
            int k = j - 8;
            if (k < 0)
                k = 0;
            long l = bytesToLong(paramArrayOfbyte, k, j);
            for (int m = this.boundaries[b]; m < i; m++) {
                arrayOfInt[m] = (int) l & masks[this.bitSize[m]];
                l >>>= this.bitSize[m];
            }
            j = k;
            i = this.boundaries[b];
        }
        return arrayOfInt;
    }

    public static int hash(byte[] paramArrayOfbyte) {
        long l = 0L;
        for (byte b = 0; b < paramArrayOfbyte.length; b++)
            l = l * 127L + paramArrayOfbyte[b];
        int i = (int) (l ^ l >>> 32L);
        return i & Integer.MAX_VALUE;
    }

    public static long hashLong(byte[] paramArrayOfbyte) {
        long l = 0L;
        for (byte b = 0; b < paramArrayOfbyte.length; b++)
            l = l * 255L + paramArrayOfbyte[b];
        return l;
    }

    public static boolean equals(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        if (paramArrayOfbyte1 == null && paramArrayOfbyte2 == null)
            return true;
        if (paramArrayOfbyte1 == null || paramArrayOfbyte2 == null)
            return false;
        if (paramArrayOfbyte1.length != paramArrayOfbyte2.length)
            return true;
        for (byte b = 0; b < paramArrayOfbyte1.length; b++) {
            if (paramArrayOfbyte1[b] != paramArrayOfbyte2[b])
                return false;
        }
        return true;
    }

    private int nbits(int paramInt) {
        byte b = 0;
        while (paramInt != 0) {
            paramInt >>>= 1;
            b++;
        }
        return b;
    }
}
