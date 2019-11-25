/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator Client
 * Filename: GlcdEmulatorClienIT.java
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

import com.ibasco.glcdemulator.client.GlcdRemoteClient;
import com.ibasco.glcdemulator.client.net.GeneralOptions;
import com.ibasco.glcdemulator.client.net.TcpTransporOptions;
import com.ibasco.glcdemulator.client.net.TcpTransport;
import com.ibasco.glcdemulator.client.net.Transport;
import com.ibasco.ucgdisplay.drivers.glcd.Glcd;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdConfig;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdConfigBuilder;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplayDriver;
import com.ibasco.ucgdisplay.drivers.glcd.enums.*;
import com.ibasco.ucgdisplay.drivers.glcd.utils.XBMData;
import com.ibasco.ucgdisplay.drivers.glcd.utils.XBMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlcdClientTcpExample {
    private static final Logger log = LoggerFactory.getLogger(GlcdClientTcpExample.class);

    private int count = 0;

    private int xpos = 0, ypos = 0;

    private XBMData raspberryPiLogo;

    private GlcdClientTcpExample() throws Exception {
        URI resRaspberryPiLogo = this.getClass().getResource("/raspberrypi-small.xbm").toURI();
        raspberryPiLogo = XBMUtils.decodeXbmFile(new File(resRaspberryPiLogo));
    }

    public static void main(String[] args) throws Exception {
        new GlcdClientTcpExample().run();
    }

    private void run() throws Exception {
        //Configure GLCD
        GlcdConfigBuilder configBuilder = GlcdConfigBuilder.create(Glcd.SSD1306.D_128x64_NONAME, GlcdBusInterface.PARALLEL_8080);

        Transport dataTransport = new TcpTransport();
        dataTransport.setOption(TcpTransporOptions.IP_ADDRESS, "192.168.1.24");
        dataTransport.setOption(TcpTransporOptions.PORT_NUMBER, 3580);
        dataTransport.setOption(GeneralOptions.DEBUG_OUTPUT, false);

        AtomicBoolean shutdown = new AtomicBoolean(false);

        try (GlcdRemoteClient driver = new GlcdRemoteClient(configBuilder.build(), dataTransport)) {
            boolean show = true;
            long prevMillis = 0;
            int xpos = 0;
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
                /*driver.setFont(GlcdFont.FONT_4X6_MR);
                driver.drawString(xpos++,6, "HELLO WORLD");*/
                driver.sendBuffer();

                if (xpos > 64)
                    xpos = 0;
                Thread.sleep(10);
            }
        } catch (IOException e) {
            shutdown.set(true);
            throw new RuntimeException(e);
        }
    }

    private void drawText(GlcdDisplayDriver driver) {
        if (count > 100)
            count = 0;
        driver.setFont(GlcdFont.FONT_ASTRAGAL_NBP_TR);
        driver.drawString(xpos++, 40, "Hello from Java!");
        if (xpos == 192)
            xpos = 0;
    }

    private void drawRpiLogo(GlcdDisplayDriver driver) {
        driver.setBitmapMode(GlcdBitmapMode.TRANSPARENT);
        if (raspberryPiLogo != null)
            driver.drawXBM(40, -5, 95, 74, raspberryPiLogo.getData());
    }

    private void drawU8G2Logo(GlcdDisplayDriver driver) {
        driver.setFontMode(GlcdFontMode.TRANSPARENT);

        driver.setFontDirection(GlcdFontDirection.LEFT_TO_RIGHT);
        driver.setFont(GlcdFont.FONT_INB16_MF);
        driver.drawString(0, 22, "U");

        driver.setFontDirection(GlcdFontDirection.TOP_TO_DOWN);
        driver.setFont(GlcdFont.FONT_INB19_MN);
        driver.drawString(14, 8, "8");

        driver.setFontDirection(GlcdFontDirection.LEFT_TO_RIGHT);
        driver.setFont(GlcdFont.FONT_INB16_MF);
        driver.drawString(36, 22, "g");
        driver.drawString(48, 22, "2");

        driver.drawHLine(2, 25, 34);
        driver.drawHLine(3, 26, 34);
        driver.drawVLine(32, 22, 12);
        driver.drawVLine(33, 23, 12);
    }
}
