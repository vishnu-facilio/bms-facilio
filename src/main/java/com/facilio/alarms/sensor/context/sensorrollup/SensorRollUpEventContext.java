package com.facilio.alarms.sensor.context.sensorrollup;

import com.facilio.alarms.sensor.FacilioDataProcessing;
import com.facilio.alarms.sensor.context.SensorAlarmDetailsContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;


@Setter
@Getter
@Log4j
public class SensorRollUpEventContext extends BaseEventContext {

    private static final long serialVersionUID = 1L;

    Boolean isMeterRollUpEvent;
    private SensorRuleContext sensorRule;
    private long readingFieldId = -1;
    private FacilioField readingField;

    public void setIsMeterRollUpEvent(Boolean isMeterRollUpEvent) {
        this.isMeterRollUpEvent = isMeterRollUpEvent;
    }

    public boolean isMeterRollUpEvent() {
        if (isMeterRollUpEvent != null) {
            return isMeterRollUpEvent.booleanValue();
        }
        return false;
    }

    public String constructMessageKey() {
        if (isMeterRollUpEvent()) {
            if (getResource() != null) {
                return "SensorMeterRollUp_" + getResource().getId();
            }
        } else {
            if (getResource() != null && getReadingField() != null) {
                return "SensorRollUp_" + getReadingFieldId() + "_" + getResource().getId();
            }
        }
        return null;
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new SensorRollUpAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        SensorRollUpAlarmContext sensorRollUpAlarm = (SensorRollUpAlarmContext) baseAlarm;
        sensorRollUpAlarm.setSensorRule(sensorRule);
        if (readingFieldId != -1) {
            sensorRollUpAlarm.setReadingFieldId(readingFieldId);
        }
        return baseAlarm;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new SensorRollUpAlarmOccurrenceContext();
        }
        SensorRollUpAlarmOccurrenceContext sensorRollUpAlarmOccurrence = (SensorRollUpAlarmOccurrenceContext) alarmOccurrence;
        sensorRollUpAlarmOccurrence.setSensorRule(sensorRule);
        if (readingFieldId != -1) {
            sensorRollUpAlarmOccurrence.setReadingFieldId(readingFieldId);
        }
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    public FacilioField getReadingField() {
        try {
            if (readingField == null && readingFieldId > 0) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                readingField = modBean.getField(readingFieldId);
            }
        } catch (Exception e) {
            LOGGER.error("Error in SensorRollUpEventContext while fetching reading fieldid : " + readingFieldId, e);
        }
        return readingField;
    }

    @JsonSerialize
    public Type getEventTypeEnum() {
        return Type.SENSOR_ROLLUP_ALARM;
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setRuleId(long ruleId) {
        if (ruleId > 0) {
            SensorRuleContext sensorRuleContext = new SensorRuleContext();
            sensorRuleContext.setId(ruleId);
            setSensorRule(sensorRuleContext);
        }
    }

    @FacilioDataProcessing
    public SensorRollUpEventContext constructRollUpEvent(Long resourceId, Long ttime, boolean isMeterRollUpEvent, SensorRuleContext sensorRule) throws Exception {

        SensorRollUpEventContext sensorRollUpEvent = new SensorRollUpEventContext();
        ResourceContext resource = ResourceAPI.getResource(resourceId);

        SensorAlarmDetailsContext sensorAlarmDetails = sensorRule.getSensorAlarmDetails();

        sensorRollUpEvent.setSeverityString(sensorAlarmDetails.getSeverity());
        sensorRollUpEvent.setResource(resource);
        sensorRollUpEvent.setSiteId(resource.getSiteId());

        sensorRollUpEvent.setIsMeterRollUpEvent(isMeterRollUpEvent);
        sensorRollUpEvent.setCreatedTime(ttime);
        if (!isMeterRollUpEvent) {
            sensorRollUpEvent.setReadingFieldId(NewReadingRuleAPI.getPrimaryFieldId(sensorRule.getNs()));
        } else {
            sensorRollUpEvent.setReadingFieldId(NewReadingRuleAPI.getPrimaryFieldId(sensorRule.getNs()));
        }
        sensorRollUpEvent.setEventMessage(sensorAlarmDetails.getMessage());
        sensorRollUpEvent.setSensorRule(sensorRule);

        return sensorRollUpEvent;
    }

    @FacilioDataProcessing
    public SensorRollUpEventContext constructRollUpClearEvent(Long resourceId, Long ttime, boolean isMeterRollUpEvent, SensorRuleContext sensorRule) throws Exception {

        SensorRollUpEventContext sensorRollUpEvent = new SensorRollUpEventContext();
        ResourceContext resource = ResourceAPI.getResource(resourceId);

        SensorAlarmDetailsContext alarmDetailsContext = sensorRule.getSensorAlarmDetails();

        sensorRollUpEvent.setSeverityString(FacilioConstants.Alarm.CLEAR_SEVERITY);
        sensorRollUpEvent.setResource(resource);
        sensorRollUpEvent.setSiteId(resource.getSiteId());

        sensorRollUpEvent.setIsMeterRollUpEvent(isMeterRollUpEvent);
        sensorRollUpEvent.setCreatedTime(ttime);

        if (!isMeterRollUpEvent) {
            sensorRollUpEvent.setReadingFieldId(NewReadingRuleAPI.getPrimaryFieldId(sensorRule.getNs()));
        } else {
            sensorRollUpEvent.setReadingFieldId(NewReadingRuleAPI.getPrimaryFieldId(sensorRule.getNs()));
        }
        sensorRollUpEvent.setComment("Sensor alarm auto cleared because associated rule executed clear condition for the associated asset.");
        sensorRollUpEvent.setEventMessage(alarmDetailsContext.getMessage());
        sensorRollUpEvent.setSensorRule(sensorRule);

        return sensorRollUpEvent;
    }

}
