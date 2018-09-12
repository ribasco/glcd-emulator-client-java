//
// Created by raffy on 9/12/2018.
//

#ifndef CPP_U8G2EMULATORCLIENT_H
#define CPP_U8G2EMULATORCLIENT_H

#include <U8g2lib.h>

class U8g2EmulatorClient : public U8G2 {
public:
    explicit U8g2EmulatorClient(const u8g2_cb_t *rotation);
private:
    static uint8_t byte_cb(u8x8_t *u8x8, uint8_t msg, uint8_t arg_int, void *arg_ptr);
    static uint8_t gpio_cb(u8x8_t *u8x8, uint8_t msg, uint8_t arg_int, void *arg_ptr);
};

#endif //CPP_U8G2EMULATORCLIENT_H
