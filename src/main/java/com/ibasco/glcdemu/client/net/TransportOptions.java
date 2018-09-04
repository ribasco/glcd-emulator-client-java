package com.ibasco.glcdemu.client.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class TransportOptions {
    private Map<TransportOption, Object> options = new HashMap<>();

    TransportOptions() {
    }

    <T> TransportOptions put(TransportOption<T> option, T value) {
        options.put(option, value);
        return this;
    }

    <T> T get(TransportOption<T> option) {
        return (T) options.get(option);
    }

    public Set<TransportOption> getOptions() {
        return options.keySet();
    }
}
