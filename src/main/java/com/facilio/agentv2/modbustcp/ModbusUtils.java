package com.facilio.agentv2.modbustcp;

public class ModbusUtils {

    public static enum RegisterType {
        COIL_STATUS(false),
        INPUT_STATUS(true),
        HOLDING_REGISTER(true),
        INPUT_REGISTER(false);

        private boolean writable;

        RegisterType() {};

        RegisterType(boolean isWritable) {
            this.writable = isWritable;
        }

        public int getValue() {
            return ordinal()+1;
        }

        public boolean isWritable() {
            return writable;
        }

        public static ModbusUtils.RegisterType valueOf (int value) {
            if (value >= 0 && value < values().length) {
                return values() [value - 1];
            }
            return null;
        }
    }
}
