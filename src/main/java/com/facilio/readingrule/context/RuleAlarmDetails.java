package com.facilio.readingrule.context;

import com.facilio.bmsconsole.enums.FaultType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuleAlarmDetails {

    Long id;

    Long ruleId;

    String message;

    int faultType;

    public void setFaultType(int faultType) {
        this.faultType = faultType;
        this.faultTypeEnum = FaultType.valueOf(faultType);
    }

    FaultType faultTypeEnum;

    public void setFaultTypeEnum(FaultType faultTypeEnum) {
        this.faultTypeEnum = faultTypeEnum;
        this.faultType = faultTypeEnum.getIndex();
    }

    String severity;

    Long severityId;

    String problem;

    String possibleCauses;

    String recommendations;
}
