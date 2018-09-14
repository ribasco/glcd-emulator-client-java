//
// Created by raffy on 9/12/2018.
//

#include "main.h"
#include "U8g2EmulatorClient.h"

U8g2EmulatorClient *u8g2;

u8g2_uint_t x = 0;

void drawU8G2Logo(U8G2 *u8g2, u8g2_uint_t offset = 0) {
    u8g2->clearBuffer();
    u8g2->setFontMode(1);

    u8g2->setFontDirection(0);
    u8g2->setFont(u8g2_font_inb16_mf);
    u8g2->drawStr(0 + offset, 22, "U");

    u8g2->setFontDirection(1);
    u8g2->setFont(u8g2_font_inb19_mn);
    u8g2->drawStr(14 + offset, 8, "8");

    u8g2->setFontDirection(0);
    u8g2->setFont(u8g2_font_inb16_mf);
    u8g2->drawStr(36 + offset, 22, "g");
    u8g2->drawStr(48 + offset, 22, "\xb2");

    u8g2->drawHLine(2 + offset, 25, 34);
    u8g2->drawHLine(3 + offset, 26, 34);
    u8g2->drawVLine(32 + offset, 22, 12);
    u8g2->drawVLine(33 + offset, 23, 12);
    u8g2->sendBuffer();
}

void setup() {
    Serial.begin(115200);
    u8g2 = new U8g2EmulatorClient(U8G2_R0);
    u8g2->begin();
    u8g2->setFont(u8g2_font_10x20_t_arabic);
    pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
    u8g2->clearBuffer();
    drawU8G2Logo(u8g2, x++);
    u8g2->sendBuffer();
    if (x > 128)
        x = 0;
}
