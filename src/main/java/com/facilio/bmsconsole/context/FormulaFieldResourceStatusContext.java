package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.FormulaFieldContext.TriggerType;
import com.facilio.bmsconsole.context.LoggerContext.Status;
import com.facilio.bmsconsole.util.FacilioFrequency;

public class FormulaFieldResourceStatusContext {
	
	private long id;
	private long formulaFieldId;
	private long fieldId;
	private Long resourceId;
	private Status status;
	private boolean isLeaf;
	private ResourceContext resourceContext;
	private long resourceCount;
	private long actualStartTime;
	private long calculationStartTime;
	private long calculationEndTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getFormulaFieldId() {
		return formulaFieldId;
	}
	public void setFormulaFieldId(long formulaFieldId) {
		this.formulaFieldId = formulaFieldId;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	public boolean isLeaf() {
		return isLeaf;
	}
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	public Boolean getLeaf() {
		return isLeaf;
	}
	public ResourceContext getResourceContext() {
		return resourceContext;
	}
	public void setResourceContext(ResourceContext resourceContext) {
		this.resourceContext = resourceContext;
	}
	public long getResourceCount() {
		return resourceCount;
	}
	public void setResourceCount(long resourceCount) {
		this.resourceCount = resourceCount;
	}
	public long getActualStartTime() {
		return actualStartTime;
	}
	public void setActualStartTime(long actualStartTime) {
		this.actualStartTime = actualStartTime;
	}
	public long getCalculationStartTime() {
		return calculationStartTime;
	}
	public void setCalculationStartTime(long calculationStartTime) {
		this.calculationStartTime = calculationStartTime;
	}
	public long getCalculationEndTime() {
		return calculationEndTime;
	}
	public void setCalculationEndTime(long calculationEndTime) {
		this.calculationEndTime = calculationEndTime;
	}
	public int getStatus() {
		if (status != null) {
			return status.getIntVal();
		}
		return -1;
	}

	public void setStatus(int statusint) {
		this.status = Status.getAllOptions().get(new Integer(statusint));
	}
	
	public enum Status {
		
		IN_PROGRESS(1),
		RESOLVED(2),
		FAILED(3),
		IN_QUEUE(4),
		;

		int intVal;
		private Status(int intVal) {
			this.intVal = intVal;
		}
		
		public int getIntVal() {
			return intVal;
		}

		private static final Map<Integer, Status> optionMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Status> initTypeMap() {
			Map<Integer, Status> typeMap = new HashMap<>();

			for (Status status : values()) {
				typeMap.put(status.getIntVal(), status);
			}
			return typeMap;
		}

		public static Map<Integer, Status> getAllOptions() {
			return optionMap;
		}
	}
	
	public enum TriggerType {
		SCHEDULE,
		POST_LIVE_READING,
		PRE_LIVE_READING
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static TriggerType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private TriggerType triggerType;
	public TriggerType getTriggerTypeEnum() {
		return triggerType;
	}
	public void setTriggerType(TriggerType triggerType) {
		this.triggerType = triggerType;
	}
	public int getTriggerType() {
		if (triggerType != null) {
			return triggerType.getValue();
		}
		return -1;
	}
	public void setTriggerType(int triggerType) {
		this.triggerType = TriggerType.valueOf(triggerType);
	}

	private FacilioFrequency frequency;
	public int getFrequency() {
		if (frequency != null) {
			return frequency.getValue();
		}
		return -1;
	}
	public void setFrequency(int frequency) {
		this.frequency = FacilioFrequency.valueOf(frequency);
	}
	public void setFrequency(FacilioFrequency frequency) {
		this.frequency = frequency;
	}
	public FacilioFrequency getFrequencyEnum() {
		return frequency;
	}
	
	private int interval = -1; //In minutes
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}

}
