package com.ibasco.glcdemu.client;

import com.ibasco.glcdemu.client.net.Transport;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.pidisplay.core.u8g2.U8g2Message;
import com.ibasco.pidisplay.core.u8g2.U8g2MessageEvent;
import com.ibasco.pidisplay.drivers.glcd.GlcdBaseDriver;
import com.ibasco.pidisplay.drivers.glcd.GlcdConfig;
import com.ibasco.pidisplay.drivers.glcd.exceptions.GlcdConfigException;
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
 * A data {@link Transport} is used for sending the instructions to the emulator.</p>
 *
 * @author Rafael Ibasco
 */
public class GlcdEmulatorClient extends GlcdBaseDriver implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(GlcdEmulatorClient.class);

    private ByteBuffer buffer;

    private Transport transport;

    /**
     * Creates a new emulator client with the given transport
     *
     * @param config
     *         The {@link GlcdConfig} associated with this instance
     * @param transport
     *         The data {@link Transport} that is responsible for sending/receiving data to/from the emulator
     */
    public GlcdEmulatorClient(GlcdConfig config, Transport transport) {
        super(config);
        this.transport = Objects.requireNonNull(transport, "Transport cannot be null");
        try {
            transport.open();
            buffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            initialize();
        } catch (GlcdDriverException | IOException e) {
            throw new RuntimeException("Error during emulator client initialization", e);
        }
    }

    @Override
    protected void checkConfig(GlcdConfig config) throws GlcdConfigException {
        config.setEmulated(true);
        super.checkConfig(config);
    }

    @Override
    public void sendBuffer() {
        try {
            sendStart();
            super.sendBuffer();
            //sendEnd();
        } catch (IOException e) {
            throw new RuntimeException("Error occured while trying to send u8g2 message", e);
        }
    }

    private void sendStart() throws IOException {
        //writeToBuffer(new U8g2ByteEvent(U8g2Message.U8X8_MSG_START, 1));
        writeToBuffer(U8g2Message.U8X8_MSG_START.getCode());
        transport.send(buffer);
    }

    private void sendEnd() throws IOException {
        writeToBuffer(new U8g2ByteEvent(U8g2Message.U8X8_MSG_END, 1));
        transport.send(buffer);
    }

    private void writeToBuffer(U8g2MessageEvent event) {
        buffer.clear();
        buffer.put((byte) event.getMessage().getCode());
        buffer.put((byte) event.getValue());
        buffer.flip();
    }

    private void writeToBuffer(int value) {
        buffer.clear();
        buffer.put((byte) value);
        buffer.flip();
    }

    @Override
    protected void onByteEvent(U8g2ByteEvent event) {
        try {
            switch (event.getMessage()) {
                case U8X8_MSG_BYTE_SEND: {
                    writeToBuffer(event.getValue());
                    transport.send(buffer);
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error occured while trying to send u8g2 message", e);
        }
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
