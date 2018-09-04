package com.ibasco.glcdemu.client;

import com.ibasco.glcdemu.client.net.TcpTransporOptions;
import com.ibasco.glcdemu.client.net.TcpTransport;
import com.ibasco.glcdemu.client.net.Transport;
import com.ibasco.pidisplay.core.drivers.GraphicsDisplayDriver;
import com.ibasco.pidisplay.core.util.XBMUtils;
import com.ibasco.pidisplay.drivers.glcd.Glcd;
import com.ibasco.pidisplay.drivers.glcd.GlcdConfig;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdCommInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdFont;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdRotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlcdEmulatorClienIT {
    private static final Logger log = LoggerFactory.getLogger(GlcdEmulatorClienIT.class);

    private XBMUtils.XBMData raspberryPiLogo;

    private GlcdEmulatorClienIT() throws Exception {
        URI resRaspberryPiLogo = this.getClass().getClassLoader().getResource("images/raspberrypi-small.xbm").toURI();
        raspberryPiLogo = XBMUtils.decodeXbmFile(new File(resRaspberryPiLogo));
    }

    public static void main(String[] args) throws Exception {
        new GlcdEmulatorClienIT().run();
    }

    private void run() throws Exception {
        //Configure GLCD
        GlcdConfig config = new GlcdConfig();
        config.setDisplay(Glcd.ST7920.D_128x64);
        config.setCommInterface(GlcdCommInterface.SPI_HW_4WIRE_ST7920);
        config.setRotation(GlcdRotation.ROTATION_NONE);

        Transport dataTransport = new TcpTransport();
        dataTransport.setOption(TcpTransporOptions.IP_ADDRESS, "localhost");
        dataTransport.setOption(TcpTransporOptions.PORT_NUMBER, 3580);
        AtomicBoolean shutdown = new AtomicBoolean(false);

        try (GlcdEmulatorClient driver = new GlcdEmulatorClient(config, dataTransport))  {
            boolean show = true;
            long prevMillis = 0;
            while (!shutdown.get()) {
                driver.clearBuffer();
                drawU8G2Logo(driver);
                drawText(driver);

                long curMillis = System.currentTimeMillis();
                if ((curMillis - prevMillis) > 500) {
                    show = !show;
                    prevMillis = curMillis;
                }

                if (show) {
                    drawRpiLogo(driver);
                }
                driver.sendBuffer();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            shutdown.set(true);
        }
    }

    private int count = 0;

    private int xpos = 0, ypos = 0;

    private void drawText(GraphicsDisplayDriver driver) {
        if (count > 100)
            count = 0;
        driver.setFont(GlcdFont.FONT_ASTRAGAL_NBP_TR);
        driver.drawString(xpos++, 50, "Count: " + count++);
        xpos &= 0x7f;
    }

    private void drawRpiLogo(GraphicsDisplayDriver driver) {
        driver.setBitmapMode(1);
        if (raspberryPiLogo != null) {
            driver.drawXBM(40, -5, 95, 74, raspberryPiLogo.getData());
            //ypos &= 0x3f;
        }
    }

    private void drawU8G2Logo(GraphicsDisplayDriver driver) {
        driver.setFontMode(1);

        driver.setFontDirection(0);
        driver.setFont(GlcdFont.FONT_INB16_MF);
        driver.drawString(0, 22, "U");

        driver.setFontDirection(1);
        driver.setFont(GlcdFont.FONT_INB19_MN);
        driver.drawString(14, 8, "8");

        driver.setFontDirection(0);
        driver.setFont(GlcdFont.FONT_INB16_MF);
        driver.drawString(36, 22, "g");
        driver.drawString(48, 22, "2");

        driver.drawHLine(2, 25, 34);
        driver.drawHLine(3, 26, 34);
        driver.drawVLine(32, 22, 12);
        driver.drawVLine(33, 23, 12);
    }
}