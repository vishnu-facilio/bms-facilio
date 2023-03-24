package com.facilio.bmsconsoleV3.context.attendance;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.shift.Break;
import com.facilio.modules.FacilioStringEnum;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class AttendanceTransaction extends V3Context {

    AttendanceTransaction(){

    }
    public AttendanceTransaction(Long time, Type type){
        this.transactionTime = time;
        this.transactionType = type;
    };

    public AttendanceTransaction(Long time, Type type, Break br) throws Exception {
        if (type != Type.BREAK){
            throw new Exception("type has to be break for this constructor usage");
        }
        this.transactionTime = time;
        this.transactionType = type;
        this.shiftBreak = br;
    };


    private Break shiftBreak;

    private V3PeopleContext people;

    private Type transactionType;

    private Source sourceType;

    private Long transactionTime;

    private String notes;

    public enum Type implements FacilioStringEnum {
        CHECK_IN,
        CHECK_OUT,
        BREAK,
        RESUME_WORK
    }

    public enum Source implements FacilioStringEnum {
        WEB,
        MOBILE,
        SYSTEM,
    }
}
