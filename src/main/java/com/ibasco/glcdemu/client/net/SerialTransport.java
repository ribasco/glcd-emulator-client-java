package com.ibasco.glcdemu.client.net;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SerialTransport implements Transport {
    @Override
    public void close() throws IOException {

    }

    @Override
    public void open() throws IOException {

    }

    @Override
    public int send(ByteBuffer buffer) throws IOException {
        return 0;
    }

    @Override
    public int receive(ByteBuffer buffer) throws IOException {
        return 0;
    }

    @Override
    public <T> void setOption(TransportOption<T> option, T value) {

    }
}
