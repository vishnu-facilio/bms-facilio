package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceOrderTicketStatusContext extends V3Context {
    private String status;
    private String displayName;
    private String color;
    private String textColor;
    private boolean recordLocked;
    private boolean deleteLocked;


    public ServiceOrderTicketStatusContext.StatusType getTypeCodeEnum() {
        return typeCode;
    }

    public void setTypeCode(ServiceOrderTicketStatusContext.StatusType typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode(){
        if(typeCode != null){
            return typeCode.getIndex();
        }
        return -1;
    }
    public void setTypeCode(int typeCode){
        this.typeCode = ServiceOrderTicketStatusContext.StatusType.valueOf(typeCode);
    }
    private ServiceOrderTicketStatusContext.StatusType typeCode;



    public static enum StatusType implements FacilioIntEnum {
        UPCOMING("Upcoming"),
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

        public static ServiceOrderTicketStatusContext.StatusType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
