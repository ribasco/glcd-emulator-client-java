package com.ibasco.glcdemu.client.net;

public class TcpTransporOptions {
    private TcpTransporOptions() {}

    public static final TransportOption<String> IP_ADDRESS = new BasicTcpTransportOption<>("IP_ADDRESS", String.class);

    public static final TransportOption<Integer> PORT_NUMBER = new BasicTcpTransportOption<>("PORT_NUMBER", Integer.class);

    private static class BasicTcpTransportOption<T> implements TransportOption<T> {

        private String name;

        private Class<T> type;

        private BasicTcpTransportOption(String name, Class<T> type) {
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
