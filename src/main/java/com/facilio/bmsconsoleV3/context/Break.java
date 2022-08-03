package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;
import lombok.Data;

import java.util.List;

@Data
public class Break extends V3Context {

    private String name;
    private Long BreakTime;
    private List<Shift> shifts;

    public enum Type {
        PAID,
        UNPAID;

        public int getValue() {
            return ordinal() + 1;
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Break.Type breakType;

    public int getBreakType() {
        return breakType != null ? breakType.getValue() : -1;
    }

    public void setBreakType(int breakType) {
        this.breakType = Break.Type.valueOf(breakType);
    }

    private Break.Mode breakMode;

    public int getBreakMode() {
        return breakMode != null ? breakMode.getValue() : -1;
    }

    public void setBreakMode(int breakMode) {
        this.breakMode = Break.Mode.valueOf(breakMode);
    }

    public enum Mode {
        MANUAL,
        AUTOMATIC;

        public int getValue() {
            return ordinal() + 1;
        }

        public static Mode valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
