package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BMSEventContext extends BaseEventContext {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String condition;

    private String source;

    private long controller = -1;

    private long agentId = -1;
    private List<Map<String,Object>> sources;
    private String alarmClass;

	@Override
    public boolean shouldIgnore() {
        if (StringUtils.isEmpty(condition) || StringUtils.isEmpty(source)) {
            return true;
        }
        return super.shouldIgnore();
    }

    @Override
    public String constructMessageKey() {
        if (StringUtils.isEmpty(condition)) {
            condition = getEventMessage();
        }
        return "BMSEvent_" + condition + "_" + source + "_" + (controller == -1 ? "empty_controller" : controller);
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new BMSAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        BMSAlarmContext alarm = (BMSAlarmContext) baseAlarm;
        alarm.setCondition(getCondition());
        alarm.setSource(getSource());
        alarm.setController(getController());
        alarm.setAlarmClass(getAlarmClass());

        return baseAlarm;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new BMSAlarmOccurrenceContext();
        }

        BMSAlarmOccurrenceContext bmsOccurrence = (BMSAlarmOccurrenceContext) alarmOccurrence;
        bmsOccurrence.setCondition(getCondition());
        bmsOccurrence.setSource(getSource());
        bmsOccurrence.setController(getController());
        bmsOccurrence.setAlarmClass(getAlarmClass());
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    @Override
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.BMS_ALARM;
    }
}