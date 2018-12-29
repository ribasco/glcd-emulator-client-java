/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator Client
 * Filename: GlcdRemoteClientTest.java
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
package com.ibasco.glcdemulator.client;

import com.ibasco.glcdemulator.client.net.Transport;
import com.ibasco.ucgdisplay.drivers.glcd.Glcd;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdConfig;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriverAdapter;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdPinMapConfig;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdPin;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdRotation;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;

@ExtendWith(MockitoExtension.class)
class GlcdRemoteClientTest {

    @Mock
    private Transport mockTransport;

    @Mock
    private GlcdDriverAdapter mockAdapter;

    @Test
    void testDrawOperation() {
        GlcdConfig config = new GlcdConfig();
        updateValidConfig(config);
        GlcdRemoteClient client = createClient(config);

        /*doAnswer(invocation -> {
            System.out.println("Answer: " + invocation);
            U8g2ByteEvent event = new U8g2ByteEvent(U8X8_MSG_BYTE_START_TRANSFER, 0);
            client.onByteEvent(event);
            return null;
        }).when(mockAdapter).sendBuffer();*/

        byte[] data = new byte[1024];

        when(mockAdapter.getBuffer()).thenReturn(data);

        client.drawBox(0, 10, 20, 30);
        client.sendBuffer();

        ArgumentCaptor<ByteBuffer> byteArg = ArgumentCaptor.forClass(ByteBuffer.class);

        verify(mockAdapter, times(1)).getBuffer();
        assertDoesNotThrow(() -> verify(mockTransport, times(1)).send(byteArg.capture()));

        assertFalse(client.isEmulated());
        assertNotNull(byteArg.getValue());
        assertArrayEquals(data, byteArg.getValue().array());
    }

    private GlcdRemoteClient createClient(GlcdConfig config) {
        return new GlcdRemoteClient(config, mockTransport, mockAdapter);
    }

    private void updateValidConfig(GlcdConfig config) {
        config.setDisplay(Glcd.ST7920.D_128x64);
        config.setBusInterface(GlcdBusInterface.SPI_HW_4WIRE_ST7920);
        config.setRotation(GlcdRotation.ROTATION_NONE);
        config.setPinMapConfig(new GlcdPinMapConfig().map(GlcdPin.SPI_MOSI, 10).map(GlcdPin.SPI_CLOCK, 11));
    }
}
