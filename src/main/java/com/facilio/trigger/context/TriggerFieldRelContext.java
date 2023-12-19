package com.facilio.trigger.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
public class TriggerFieldRelContext extends BaseTriggerContext{

    long id = -1;
    long fieldId = -1;
    int scheduleType;
    long timeInterval = -1;
    String timeValue;

    @Override
    public int getType() {
        return super.getType();
    }

    @Override
    public TriggerType getTypeEnum() {
        return super.getTypeEnum();
    }

    public String getTime() {
        return timeValue == null? null : timeValue.toString();
    }
    String oldValue;
    String newValue;

    ScheduledRuleType scheduleTypeEnum;

    private long triggerEndTime = -1;
    public static enum ScheduledRuleType {
        BEFORE,
        ON,
        AFTER
        ;

        public int getValue() {
            return ordinal() + 1;
        }

        public static ScheduledRuleType valueOf (int value) {
            if (value > 0 && value <= values().length) {
                return values() [value - 1];
            }
            return null;
        }
    }


    public Long validatedExecutionTime(Long executionTime){
        if(executionTime == null){
            return null;
        }

        long currentTime = (System.currentTimeMillis() / 1000);
        if(executionTime < currentTime) {
            executionTime = currentTime + 30;
        }
        return executionTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof TriggerFieldRelContext) {
            TriggerFieldRelContext fieldRel = (TriggerFieldRelContext) obj;
            if (this.getEventTypeEnum().isPresent(EventType.FIELD_CHANGE.getValue())) {
                return this.fieldId == fieldRel.fieldId;
            }
            if (this.getEventTypeEnum().isPresent(EventType.SCHEDULED.getValue())) {
                return ((this.fieldId == fieldRel.fieldId) && (this.scheduleType == fieldRel.scheduleType));
            }
            return this.getEventTypeEnum() == fieldRel.getEventTypeEnum();
        }
        return true;
    }

    @Override
    public void validateTrigger() throws Exception {
        if (getScheduleTypeEnum() != null && getTimeValue() == null && getTimeInterval() < 0){//date
            throw new IllegalArgumentException("Scheduled type and Time is mandatory for date field");
        }else if (getScheduleTypeEnum() != null && getTimeInterval() < 0){ //datetime
            throw new IllegalArgumentException("Scheduled type and Interval is mandatory for datetime field");
        }

        if (getFieldId() > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField field = modBean.getField(getFieldId(), getModuleId());
            if (field == null) {
                throw new IllegalArgumentException("Invalid Field");
            }
        }
        super.validateTrigger();
    }
}
