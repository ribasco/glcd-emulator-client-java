/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator Client
 * Filename: GlcdRemoteClient.java
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

import com.ibasco.glcdemulator.client.exceptions.GlcdEmulatorClientException;
import com.ibasco.glcdemulator.client.net.GeneralOptions;
import com.ibasco.glcdemulator.client.net.Transport;
import com.ibasco.ucgdisplay.common.utils.ByteUtils;
import com.ibasco.ucgdisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdBaseDriver;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdConfig;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriverAdapter;
import com.ibasco.ucgdisplay.drivers.glcd.exceptions.GlcdDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * <p>Graphics LCD emulator client</p>
 *
 * @author Rafael Ibasco
 */
public class GlcdRemoteClient extends GlcdBaseDriver implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(GlcdRemoteClient.class);

    private ByteBuffer buffer;

    private Transport transport;

    static final int MSG_START = 0xFE;

    private static final int MSG_DC_0 = 0xE0;

    private static final int MSG_DC_1 = 0xE8;

    private static final int MSG_BYTE_SEND = 0xEC;

    private boolean debug = false;

    private boolean emulated = false;

    public GlcdRemoteClient(GlcdConfig config, Transport transpor) {
        this(config, transpor, false);
    }

    /**
     * Creates a new emulator client with the given transport
     *
     * @param config
     *         The {@link GlcdConfig} associated with this instance
     * @param transport
     *         The data {@link Transport} that is responsible for sending/receiving data to/from the emulator
     * @param emulated
     *         Set to <code>true</code> if the instructions should be sent to the host for processing. If false, only the display data will be sent. Instructions will be processed internally by the native driver.
     */
    public GlcdRemoteClient(GlcdConfig config, Transport transport, boolean emulated) {
        super(config, true);
        this.debug = transport.getOption(GeneralOptions.DEBUG_OUTPUT, false);
        this.emulated = emulated;
        initClient(transport);
    }

    public boolean isEmulated() {
        return emulated;
    }

    private int calculateBufferSize() {
        if (emulated)
            return 1024;
        int size = (getConfig().getDisplaySize().getDisplayWidth() * getConfig().getDisplaySize().getDisplayHeight()) / 8;
        log.info("Calculated buffer size: {}", size);
        return size;
    }

    /**
     * <p>Initialize client with the given {@link Transport}</p>
     *
     * @param transport
     *         The data transport for this instance (e.g. Serial/TCP)
     */
    private void initClient(Transport transport) {
        this.transport = Objects.requireNonNull(transport, "Transport cannot be null");
        try {
            if (!debug)
                transport.open();
            buffer = ByteBuffer.allocate(calculateBufferSize()).order(ByteOrder.LITTLE_ENDIAN);
            initialize();
        } catch (GlcdDriverException | IOException e) {
            throw new GlcdEmulatorClientException("Exception thrown during client initialization", e);
        }
    }

    /** For unit-testing purposes only **/
    GlcdRemoteClient(GlcdConfig config, Transport transport, GlcdDriverAdapter adapter) {
        super(config, true, null, adapter);
        initClient(transport);
    }

    @Override
    public void sendBuffer() {
        try {
            if (!debug && emulated)
                transport.send((byte) MSG_START);
            super.sendBuffer();
            if (!emulated)
                transmitBuffer();
        } catch (IOException e) {
            throw new GlcdEmulatorClientException("Exception thrown during sendBuffer() operation", e);
        }
    }

    private void transmitBuffer() {
        try {
            byte[] data = getBuffer();
            if (data != null && data.length > 0) {
                ((Buffer)buffer).rewind();
                buffer.put(data);
                ((Buffer)buffer).flip();
                transport.send(buffer);
            }
        } catch (IOException e) {
            throw new GlcdEmulatorClientException("Exception thrown during transmitBuffer() operation", e);
        }
    }

    private void debugEvents(U8g2ByteEvent event) {
        switch (event.getMessage()) {
            case U8G2_BYTE_SEND_INIT:
                log.debug("Byte Send Init: Size = {}", event.getValue());
                break;
            case U8X8_MSG_BYTE_START_TRANSFER:
                log.debug("Start Transfer");
                break;
            case U8X8_MSG_BYTE_END_TRANSFER:
                log.debug("End Transfer");
                break;
            case U8X8_MSG_BYTE_SEND:
                log.debug("\tByte: {}", ByteUtils.toHexString(false, (byte) event.getValue()));
                break;
            case U8X8_MSG_BYTE_SET_DC: {
                if (event.getValue() == 0) {
                    log.debug("Set DC = 0");
                } else if (event.getValue() == 1) {
                    log.debug("Set DC = 1");
                } else {
                    log.debug("Unknown DC = {}", event.getValue());
                }
                break;
            }
        }
    }

    @Override
    protected void onByteEvent(U8g2ByteEvent event) {
        if (debug) {
            debugEvents(event);
            return;
        }
        if (!emulated)
            return;
        try {
            switch (event.getMessage()) {
                case U8X8_MSG_BYTE_START_TRANSFER:
                    reset();
                    break;
                case U8X8_MSG_BYTE_END_TRANSFER:
                    ((Buffer)buffer).flip();
                    transport.send(buffer);
                    break;
                case U8X8_MSG_BYTE_SET_DC:
                    if (event.getValue() == 0) {
                        buffer.put((byte) MSG_DC_0);
                    } else if (event.getValue() == 1) {
                        buffer.put((byte) MSG_DC_1);
                    }
                    break;
                case U8G2_BYTE_SEND_INIT:
                    buffer.put((byte) MSG_BYTE_SEND);
                    buffer.put((byte) event.getValue());
                    break;
                case U8X8_MSG_BYTE_SEND: {
                    buffer.put((byte) event.getValue());
                    break;
                }
            }
        } catch (IOException e) {
            reset();
            throw new GlcdEmulatorClientException("Exception thrown during byte event processing", e);
        }
    }

    private void reset() {
        buffer.clear();
    }

    @Override
    public void close() throws IOException {
        if (transport != null)
            transport.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
