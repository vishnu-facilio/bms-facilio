package com.facilio.bmsconsole.context;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
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
            return getResource().getId() + "_" + getEventType();
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
        operationAlarmOccurrenceContext.setReadingFieldId(getReadingFieldId());
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
        alarm.setReadingFieldId(getReadingFieldId());
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

    private FacilioField readingField;
    public FacilioField getReadingField(){
        try {
            if(readingField == null && readingFieldId > 0) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                readingField = modBean.getField(readingFieldId);
            }
        }
        catch(Exception e) {
//            LOGGER.error("Error while fetching reading fieldid : "+readingFieldId, e);
        }
        return readingField;
    }
    public void setReadingField(FacilioField readingField) {
        this.readingField = readingField;
    }

    private long readingFieldId = -1;
    public long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(long readingFieldId) {
        this.readingFieldId = readingFieldId;
    }


}
