//
// Created by raffy on 9/12/2018.
//

#include "U8g2EmulatorClient.h"

u8g2_bytesend_cb U8g2EmulatorClient::bytecb = nullptr;

U8g2EmulatorClient::U8g2EmulatorClient(const u8g2_cb_t *rotation, u8g2_setup_cb setup_cb, u8g2_bytesend_cb bytesend_cb) {
    U8g2EmulatorClient::bytecb = bytesend_cb;
    setup_cb(&u8g2, rotation, byte_cb, gpio_cb);
}

uint8_t U8g2EmulatorClient::byte_cb(u8x8_t *u8x8, uint8_t msg, uint8_t arg_int, void *arg_ptr) {
    if (msg == U8X8_MSG_BYTE_SEND) {
        U8g2EmulatorClient::bytecb(arg_int, arg_ptr);
        return 1;
    }
    return 0;
}

uint8_t U8g2EmulatorClient::gpio_cb(u8x8_t *u8x8, uint8_t msg, uint8_t arg_int, void *arg_ptr) {
    return 1;
}

void U8g2EmulatorClient::sendBuffer() {
    uint8_t startByte = MSG_START;
    U8g2EmulatorClient::bytecb(1, &startByte);
    U8G2::sendBuffer();
}


