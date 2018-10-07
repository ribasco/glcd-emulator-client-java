package com.ibasco.glcdemu.client;

import com.ibasco.glcdemu.client.net.GeneralOptions;
import com.ibasco.glcdemu.client.net.Transport;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.pidisplay.core.util.ByteUtils;
import com.ibasco.pidisplay.drivers.glcd.GlcdBaseDriver;
import com.ibasco.pidisplay.drivers.glcd.GlcdConfig;
import com.ibasco.pidisplay.drivers.glcd.exceptions.GlcdDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * <p>A special graphics display driver which is primarily used for communicating with the graphics display emulator.
 * A data {@link Transport} is used for transmitting the instructions to the emulator.</p>
 *
 * <p>
 * <h2>Protocol specifications:</h2>
 *
 * <pre>
 * | Field     | Value                      | Type  | Size (Bytes) | Description                                                      |
 * |-----------|----------------------------|-------|--------------|------------------------------------------------------------------|
 * | Header    | 0xFE                       | byte  | 1            | Marks the start of the frame                                     |
 * | Mode      | Command = 0xF8 Data = 0xFA | byte  | 1            | Command/Data mode. Applies only for controllers using the DC pin |
 * | Data Size |                            | short | 2            | Size of the data. Applies only for controllers using DC          |
 * </pre>
 *
 * @author Rafael Ibasco
 */
public class GlcdEmulatorClient extends GlcdBaseDriver implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(GlcdEmulatorClient.class);

    private ByteBuffer buffer;

    private Transport transport;

    private static final int MSG_START = 0xFE;

    private static final int MSG_DC_0 = 0xE0;

    private static final int MSG_DC_1 = 0xE8;

    private static final int MSG_BYTE_SEND = 0xEC;

    private boolean debug;

    /**
     * Creates a new emulator client with the given transport
     *
     * @param config
     *         The {@link GlcdConfig} associated with this instance
     * @param transport
     *         The data {@link Transport} that is responsible for sending/receiving data to/from the emulator
     */
    public GlcdEmulatorClient(GlcdConfig config, Transport transport) {
        super(config, true);
        this.debug = transport.getOption(GeneralOptions.DEBUG_OUTPUT, false);
        this.transport = Objects.requireNonNull(transport, "Transport cannot be null");
        try {
            if (!debug)
                transport.open();
            buffer = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
            initialize();
        } catch (GlcdDriverException | IOException e) {
            throw new RuntimeException("Error during emulator client initialization", e);
        }
    }

    @Override
    public void sendBuffer() {
        try {
            if (!debug)
                transport.send((byte) MSG_START);
            super.sendBuffer();
        } catch (IOException e) {
            log.error("Problem occured while transmitting message headers over network", e);
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
        try {
            switch (event.getMessage()) {
                case U8X8_MSG_BYTE_START_TRANSFER:
                    reset();
                    break;
                case U8X8_MSG_BYTE_END_TRANSFER:
                    buffer.flip();
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
            log.error("Error occured while trying to send u8g2 message", e);
            reset();
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
