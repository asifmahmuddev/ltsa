package gnu.jel;

import java.io.ByteArrayOutputStream;

class PatchableByteArrayOutputStream extends ByteArrayOutputStream {
    public void patchAddress(int paramInt1, int paramInt2) {
        this.buf[paramInt1] = (byte) (paramInt2 >>> 8 & 0xFF);
        this.buf[paramInt1 + 1] = (byte) (paramInt2 & 0xFF);
    }
}
