package com.ibasco.glcdemu.client.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface Transport extends Closeable {
    void open() throws IOException;

    int send(ByteBuffer buffer) throws IOException;

    int receive(ByteBuffer buffer) throws IOException;

    <T> void setOption(TransportOption<T> option, T value);
}
