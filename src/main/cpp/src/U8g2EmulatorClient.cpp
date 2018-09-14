//
// Created by raffy on 9/12/2018.
//

#include "U8g2EmulatorClient.h"

U8g2EmulatorClient::U8g2EmulatorClient(const u8g2_cb_t *rotation) : U8G2() {
    u8g2_Setup_st7920_s_128x64_f(&u8g2, rotation, byte_cb, gpio_cb);
}

uint8_t U8g2EmulatorClient::byte_cb(u8x8_t *u8x8, uint8_t msg, uint8_t arg_int, void *arg_ptr) {
    if (msg == U8X8_MSG_BYTE_SEND) {
        uint8_t value;
        uint8_t size = arg_int;
        auto *data = (uint8_t *)arg_ptr;
        while( size > 0 ) {
            value = *data;
            data++;
            size--;
            Serial.write(value);
        }
    }
    return 1;
}

uint8_t U8g2EmulatorClient::gpio_cb(u8x8_t *u8x8, uint8_t msg, uint8_t arg_int, void *arg_ptr) {
    return 1;
}

void U8g2EmulatorClient::sendBuffer() {
    Serial.write(MSG_START);
    U8G2::sendBuffer();
}


