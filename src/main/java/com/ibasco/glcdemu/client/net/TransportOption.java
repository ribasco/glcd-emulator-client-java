package com.ibasco.glcdemu.client.net;

public interface TransportOption<T> {
    String name();
    
    Class<T> type();
}
