/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator Client
 * Filename: TcpTransport.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
package com.ibasco.glcdemulator.client.net;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class TcpTransport implements Transport {

    private SocketChannel channel;

    private TransportOptions options = new TransportOptions();

    private ByteBuffer tmp = ByteBuffer.allocate(1);

    @Override
    public void open() throws IOException {
        String ipAddress = options.get(TcpTransporOptions.IP_ADDRESS);
        if (StringUtils.isBlank(ipAddress))
            throw new IOException("IP address cannot be empty");
        int port = Objects.requireNonNull(options.get(TcpTransporOptions.PORT_NUMBER), "Port cannot be null");
        InetSocketAddress address = new InetSocketAddress(ipAddress, port);
        channel = createChannel(address);
    }

    SocketChannel createChannel(InetSocketAddress address) throws IOException {
        return SocketChannel.open(address);
    }

    @Override
    public int send(ByteBuffer buffer) throws IOException {
        if (buffer == null)
            throw new IllegalArgumentException("Buffer cannot be null");
        return channel.write(buffer);
    }

    @Override
    public synchronized void send(byte data) throws IOException {
        ByteBuffer buf = (ByteBuffer)tmp;
        buf.clear();
        buf.put(data);
        buf.flip();
        channel.write(buf);
    }

    @Override
    public int receive(ByteBuffer buffer) throws IOException {
        if (buffer == null)
            throw new IllegalArgumentException("Buffer cannot be null");
        return channel.read(buffer);
    }

    @Override
    public <T> void setOption(TransportOption<T> option, T value) {
        options.put(option, value);
    }

    @Override
    public <T> T getOption(TransportOption<T> option) {
        return options.get(option);
    }

    @Override
    public void close() throws IOException {
        if (channel != null)
            channel.close();
    }
}
