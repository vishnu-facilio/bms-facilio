package com.facilio.agentv2.modbustcp;

import com.facilio.modules.FacilioIntEnum;

public class ModbusUtils {
    public enum RegisterType implements FacilioIntEnum {
        COIL_STATUS("Coils status",false),
        INPUT_STATUS("Input status",true),
        HOLDING_REGISTER("Holding register",true),
        INPUT_REGISTER("Input register",false);

        private String name;
        private boolean writable;

        RegisterType(String name , boolean writable){
            this.name = name;
            this.writable = writable;
        }

        public static RegisterType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
        public boolean isWritable() {
            return writable;
        }
        @Override
        public Integer getIndex(){return ordinal()+1 ; }

        @Override
        public String getValue(){return name;}
    }
}
