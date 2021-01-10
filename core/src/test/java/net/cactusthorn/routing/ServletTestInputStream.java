package net.cactusthorn.routing;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class ServletTestInputStream extends ServletInputStream {

    private ByteArrayInputStream is;

    public ServletTestInputStream(String value) {
        this.is = new ByteArrayInputStream(value.getBytes());
    }

    public ServletTestInputStream(ByteArrayInputStream is) {
        this.is = is;
    }

    @Override public boolean isFinished() {
        return false;
    }

    @Override public boolean isReady() {
        return true;
    }

    @Override public void setReadListener(ReadListener readListener) {
    }

    @Override public int read() throws IOException {
        return is.read();
    }

    @Override public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    @Override public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    @Override public long skip(long n) throws IOException {
        return is.skip(n);
    }

    @Override public int available() throws IOException {
        return is.available();
    }

    @Override public void close() throws IOException {
        is.close();
    }

    @Override public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    @Override public synchronized void reset() throws IOException {
        is.reset();
    }

    @Override public boolean markSupported() {
        return is.markSupported();
    }
}
