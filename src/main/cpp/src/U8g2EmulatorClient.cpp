//
// Created by raffy on 9/12/2018.
//

#include "U8g2EmulatorClient.h"

U8g2EmulatorClient::U8g2EmulatorClient(const u8g2_cb_t *rotation) : U8G2() {
    //constructor: const u8g2_cb_t *rotation, uint8_t cs, uint8_t reset = U8X8_PIN_NONE
    //u8x8_byte_arduino_hw_spi
    //u8x8_gpio_and_delay_arduino
    //u8x8_SetPin_ST7920_HW_SPI(getU8x8(), cs, reset);
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
        Serial.flush();
    }
    return 1;
}

uint8_t U8g2EmulatorClient::gpio_cb(u8x8_t *u8x8, uint8_t msg, uint8_t arg_int, void *arg_ptr) {
    return 1;
}

void U8g2EmulatorClient::sendBuffer(void) {
    Serial.write(MSG_START);
    U8G2::sendBuffer();
    Serial.write(MSG_START);
}


