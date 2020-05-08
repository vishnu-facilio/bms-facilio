package com.facilio.bmsconsole.context;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.chain.Context;

public class AssetRollUpEvent extends BaseEventContext {
    private static final long serialVersionUID = 1L;

    @Override
    public String constructMessageKey() {
        if (getResource() != null) {
            return "AssetRollUp_" + getResource().getId() + "_" + getEventType();
        }
        return null;
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new AssetRollUpAlarm();
        }
        super.updateAlarmContext(baseAlarm, add);

        return baseAlarm;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new ReadingAlarmOccurrenceContext();
        }

        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    @Override
    @JsonSerialize
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.ASSET_ROLLUP_ALARM;
    }
}
