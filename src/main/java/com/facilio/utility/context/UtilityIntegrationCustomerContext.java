package com.facilio.utility.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilityIntegrationCustomerContext extends V3Context {

    private static final long serialVersionUID = 1L;

    Long authorizationSubmittedTime;
    String customerEmail;
    String name;
    String customerId;
    String referrals;
    String formUid;
    String templateUid;
    Boolean isArchived;
    Boolean isRevoked;
    String status;
    String userEmail;
    String userUid;
    String userStatus;
    Long expires;
    Long revoked;
    Boolean isExpired;
    String utilityID;
    String meta;
    Long noOfConnections;
    String secretState;

    private Type customerType;

    public  Type getCustomerTypeEnum() {
        return customerType;
    }

    public Integer getCustomerType() {
        if(customerType != null) {
            return customerType.getIndex();
        }
        else {
            return null;
        }
    }
    public void setCustomerType(Integer customerType) {
        if (customerType != null) {
            this.customerType = Type.valueOf(customerType);
        }
    }
    public  enum Type implements FacilioIntEnum {
        MANUAL(1,"Manually Generated"),
        AUTO_GENERATED(2, "Auto Generated"),
        ;

        Integer intVal;
        String value;
        Type(Integer intVal,String value) {
            this.intVal = intVal;
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public Integer getIntVal() {
            return intVal;
        }
        public void setIntVal(Integer intVal) {
            this.intVal = intVal;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }


        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }





    }




}
