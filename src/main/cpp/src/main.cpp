//
// Created by raffy on 9/12/2018.
//

#include "main.h"

#include <Arduino.h>

#include "U8g2EmulatorClient.h"

U8g2EmulatorClient u8g2(U8G2_R0);

void setup() {
    Serial.begin(115200);
    u8g2.begin();
    u8g2.setFont(u8g2_font_4x6_mf);
}

u8g2_uint_t x = 0;

void loop() {
    u8g2.clearBuffer();
    u8g2.drawStr(x++, 10, "hello");
    u8g2.sendBuffer();
    if (x >= 128)
        x = 0;
}