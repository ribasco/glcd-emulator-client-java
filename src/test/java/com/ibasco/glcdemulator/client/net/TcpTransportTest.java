/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator Client
 * Filename: TcpTransportTest.java
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@ExtendWith(MockitoExtension.class)
class TcpTransportTest {

    @Spy
    private TcpTransport transport;

    @Mock
    private SocketChannel socketChannel;

    @Mock
    private ByteBuffer mockBuffer;

    @DisplayName("Test Open. No options specified")
    @Test
    void testOpen_NoOptions() {
        assertThrows(IOException.class, () -> transport.open());
    }

    @DisplayName("Test Open. With options, blank ip address")
    @Test
    void testOpen_WithOptions_NoIp() {
        transport.setOption(TcpTransporOptions.IP_ADDRESS, " ");
        assertThrows(IOException.class, () -> transport.open());
    }

    @DisplayName("Test Open. With options, null port")
    @Test
    void testOpen_WithOptions_NullPort() {
        transport.setOption(TcpTransporOptions.IP_ADDRESS, "192.168.1.10");
        transport.setOption(TcpTransporOptions.PORT_NUMBER, null);
        assertThrows(NullPointerException.class, () -> transport.open());
    }

    @DisplayName("Test Open. With options, invalid port")
    @Test
    void testOpen_WithOptions_InvalidPort() {
        transport.setOption(TcpTransporOptions.IP_ADDRESS, "192.168.1.10");
        transport.setOption(TcpTransporOptions.PORT_NUMBER, -1);
        assertThrows(IllegalArgumentException.class, () -> transport.open());
    }

    @DisplayName("Test Open. With options, all options valid")
    @Test
    void testOpen_WithOptions_ValidOpts() {
        updateValidOptions(transport);

        assertDoesNotThrow(() -> doReturn(socketChannel).when(transport).createChannel(any(InetSocketAddress.class)));
        assertDoesNotThrow(() -> transport.open());
        assertDoesNotThrow(() -> verify(transport).createChannel(any(InetSocketAddress.class)));
    }

    @DisplayName("Test Send. Null arguments")
    @Test
    void testSend_Buffer_NullArg() {
        updateValidOptions(transport);
        assertThrows(IllegalArgumentException.class, () -> transport.send(null));
    }

    @DisplayName("Test Send. Null arguments")
    @Test
    void testSend_Byte() {
        updateValidOptions(transport);
        assertDoesNotThrow(() -> doReturn(socketChannel).when(transport).createChannel(any(InetSocketAddress.class)));
        assertDoesNotThrow(() -> transport.open());
        assertDoesNotThrow(() -> transport.send((byte) 10));
        assertDoesNotThrow(() -> verify(socketChannel).write(any(ByteBuffer.class)));
    }

    @DisplayName("Test Receive. Null arg")
    @Test
    void testReceive_NullArg() {
        updateValidOptions(transport);
        assertDoesNotThrow(() -> doReturn(socketChannel).when(transport).createChannel(any(InetSocketAddress.class)));
        assertDoesNotThrow(() -> transport.open());
        assertThrows(IllegalArgumentException.class, () -> transport.receive(null));
    }

    @DisplayName("Test Receive. Valid arg")
    @Test
    void testReceive_ValidArg() {
        updateValidOptions(transport);
        assertDoesNotThrow(() -> doReturn(socketChannel).when(transport).createChannel(any(InetSocketAddress.class)));
        assertDoesNotThrow(() -> transport.open());
        assertDoesNotThrow(() -> transport.receive(mockBuffer));
        assertDoesNotThrow(() -> verify(socketChannel).read(mockBuffer));
    }

    private void updateValidOptions(Transport transport) {
        transport.setOption(TcpTransporOptions.IP_ADDRESS, "192.168.1.10");
        transport.setOption(TcpTransporOptions.PORT_NUMBER, 3580);
    }
}
