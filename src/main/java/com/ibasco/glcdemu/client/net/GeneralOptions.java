package com.ibasco.glcdemu.client.net;

public class GeneralOptions {
    private GeneralOptions() {

    }

    public static final TransportOption<Boolean> DEBUG_OUTPUT = new BasicGeneralOption<>("DEBUG_OUTPUT", Boolean.class);

    private static class BasicGeneralOption<T> implements TransportOption<T> {

        private String name;

        private Class<T> type;

        private BasicGeneralOption(String name, Class<T> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
