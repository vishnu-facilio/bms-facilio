package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class TripStatusContext extends V3Context {
    private String status;
    private String displayName;
    private String color;
    private String backgroundColor;
    private String textColor;
    private boolean recordLocked;
    private boolean deleteLocked;


    public StatusType getTypeCodeEnum() {
        return typeCode;
    }

    public void setTypeCode(StatusType typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode(){
        if(typeCode != null){
            return typeCode.getIndex();
        }
        return -1;
    }
    public void setTypeCode(int typeCode){
        this.typeCode = StatusType.valueOf(typeCode);
    }
    private StatusType typeCode;



    public static enum StatusType implements FacilioIntEnum {
        OPEN ("Open"),
        IN_PROGRESS ("In Progress"),
        CLOSED("Closed");

        String name;

        StatusType(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return this.name;
        }

        public static StatusType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

}
