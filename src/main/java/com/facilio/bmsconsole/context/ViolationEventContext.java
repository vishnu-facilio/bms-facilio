package com.facilio.bmsconsole.context;

import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ViolationEventContext extends BaseEventContext {
	
	private static final long serialVersionUID = 1L;
	
	private FormulaFieldContext formulaField;
	public FormulaFieldContext getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(FormulaFieldContext formulaField) {
		this.formulaField = formulaField;
	}

	@Override
	@JsonSerialize
	public Type getEventTypeEnum() {
		return Type.VIOLATION_ALARM;
	}
	
	@Override
	public String constructMessageKey() {
		if (getResource() != null && getFormulaField() != null) {
			return getFormulaField().getId() + "_" + getResource().getId();
		}
		return null;
	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
		if (add && baseAlarm == null) {
			baseAlarm = new ViolationAlarmContext();
		}
		super.updateAlarmContext(baseAlarm, add);
		
		ViolationAlarmContext violationAlarm = (ViolationAlarmContext) baseAlarm;
		violationAlarm.setFormulaField(formulaField);

		return baseAlarm;
	}
	
	@Override
	public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
		if (add && alarmOccurrence == null) {
			alarmOccurrence = new ViolationAlarmOccurrenceContext();
		}

		ViolationAlarmOccurrenceContext violationOccurrence = (ViolationAlarmOccurrenceContext) alarmOccurrence;
		violationOccurrence.setFormulaField(formulaField);
		
		return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
	}

	@JsonIgnore
	@JSON(deserialize = false)
	public void setFormulaFieldId(long formulaId) {
		if (formulaId > 0) {
			FormulaFieldContext formula = new FormulaFieldContext();
			formula.setId(formulaId);
			setFormulaField(formula);
		}
	}

}
