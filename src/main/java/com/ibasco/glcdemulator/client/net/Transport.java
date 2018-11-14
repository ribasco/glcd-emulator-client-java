/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator Client
 * Filename: Transport.java
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

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface Transport extends Closeable {
    void open() throws IOException;

    int send(ByteBuffer buffer) throws IOException;

    void send(byte data) throws IOException;

    int receive(ByteBuffer buffer) throws IOException;

    <T> void setOption(TransportOption<T> option, T value);

    <T> T getOption(TransportOption<T> option);

    default <T> T getOption(TransportOption<T> option, T defaultVal) {
        T val = getOption(option);
        if (val == null) {
            return defaultVal;
        }
        return val;
    }
}
