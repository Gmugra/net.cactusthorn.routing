package net.cactusthorn.routing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ServletTestOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Override public boolean isReady() {
        return true;
    }

    @Override public void setWriteListener(WriteListener writeListener) {
        throw new UnsupportedOperationException();
    }

    @Override public void write(int b) throws IOException {
        out.write(b);
    }

    public byte[] toByteArray() {
        return out.toByteArray();
    }

    @Override public String toString() {
        return new String(out.toByteArray());
    }

}
