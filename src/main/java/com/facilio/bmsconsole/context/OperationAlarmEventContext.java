package com.facilio.bmsconsole.context;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.chain.Context;

import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agent.alarms.AgentAlarmOccurrenceContext;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.chain.Context;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class OperationAlarmEventContext extends BaseEventContext {

	private static final long serialVersionUID = 1L;

	@Override
    public String constructMessageKey() {
        if (getResource() != null && getCoverageTypeEnum() != null) {
            return getResource().getId() + "_" + getCoverageType()  + "_" + getEventType();
        }
        return null;
        
    }

    @Override
    @JsonSerialize
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.OPERATION_ALARM;
    }
    
	@Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new OperationAlarmOccurenceContext();
        }
        OperationAlarmOccurenceContext operationAlarmOccurrenceContext = (OperationAlarmOccurenceContext) alarmOccurrence;
        operationAlarmOccurrenceContext.setCoverageType(getCoverageType());
        operationAlarmOccurrenceContext.setSiteId(getSiteId());
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }
	
	@Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new OperationAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        OperationAlarmContext alarm = (OperationAlarmContext) baseAlarm;
        alarm.setCoverageType(getCoverageType());
        alarm.setSiteId(getSiteId());
        return baseAlarm;
    }
    private OperationAlarmContext.CoverageType coverageType;
    @JsonInclude
    public final int getCoverageType() {
        coverageType = getCoverageTypeEnum();
        if (coverageType != null) {
            return coverageType.getIndex();
        }
        return -1;
    }

    private OperationAlarmContext.CoverageType getCoverageTypeEnum() {
        return coverageType;
    }

    public final void setCoverageType(int coverageType) {
        this.coverageType = OperationAlarmContext.CoverageType.valueOf(coverageType);
    }
    public final void setCoverageType(OperationAlarmContext.CoverageType coverageType) {
        this.coverageType = coverageType;
    }
}
