/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator Client
 * Filename: SerialTransport.java
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
package com.ibasco.glcdemu.client.net;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SerialTransport implements Transport {

    private SerialPort port;

    @Override
    public void open() throws IOException {

    }

    @Override
    public int send(ByteBuffer buffer) throws IOException {
        return 0;
    }

    @Override
    public void send(byte data) throws IOException {

    }

    @Override
    public int receive(ByteBuffer buffer) throws IOException {
        return 0;
    }

    @Override
    public <T> void setOption(TransportOption<T> option, T value) {

    }

    @Override
    public <T> T getOption(TransportOption<T> option) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
