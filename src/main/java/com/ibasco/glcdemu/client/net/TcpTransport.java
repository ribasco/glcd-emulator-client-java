package com.ibasco.glcdemu.client.net;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class TcpTransport implements Transport {

    private SocketChannel channel;

    private TransportOptions options = new TransportOptions();

    @Override
    public void open() throws IOException {
        String ipAddress = options.get(TcpTransporOptions.IP_ADDRESS);
        if (StringUtils.isBlank(ipAddress))
            throw new IOException("IP address cannot be empty");
        int port = Objects.requireNonNull(options.get(TcpTransporOptions.PORT_NUMBER), "Port cannot be null");
        InetSocketAddress address = new InetSocketAddress(ipAddress, port);
        channel = SocketChannel.open(address);
    }

    @Override
    public int send(ByteBuffer buffer) throws IOException {
        return channel.write(buffer);
    }

    @Override
    public int receive(ByteBuffer buffer) throws IOException {
        return channel.read(buffer);
    }

    @Override
    public <T> void setOption(TransportOption<T> option, T value) {
        options.put(option, value);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
