package com.facilio.mv.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class MVProjectContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	String description;
	EMC_Options emcOptions;
	long startTime;
	long endTime;
	FacilioFrequency frequency;
	boolean isAutoGenVmMeter;
	AssetContext meter;
	User owner;
	int saveGoal;
	FormulaFieldContext saveGoalFormulaField;
	long reportingPeriodStartTime;
	long reportingPeriodEndTime;
	Boolean status;
	Boolean isLocked;
	

	public FormulaFieldContext getSaveGoalFormulaField() {
		return saveGoalFormulaField;
	}
	public void setSaveGoalFormulaField(FormulaFieldContext saveGoalFormulaField) {
		this.saveGoalFormulaField = saveGoalFormulaField;
	}
	
	public Boolean getIsLocked() {
		return isLocked;
	}
	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}
	public boolean isLocked() {
		if(this.isLocked != null) {
			return this.isLocked.booleanValue();
		}
		return false;
	}
	public Boolean isStatus() {
		return status;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}

	public int getSaveGoal() {
		return saveGoal;
	}

	public void setSaveGoal(int saveGoal) {
		this.saveGoal = saveGoal;
	}

	public long getReportingPeriodStartTime() {
		return reportingPeriodStartTime;
	}

	public void setReportingPeriodStartTime(long reportingPeriodStartTime) {
		this.reportingPeriodStartTime = reportingPeriodStartTime;
	}

	public long getReportingPeriodEndTime() {
		return reportingPeriodEndTime;
	}

	public void setReportingPeriodEndTime(long reportingPeriodEndTime) {
		this.reportingPeriodEndTime = reportingPeriodEndTime;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	

	public AssetContext getMeter() {
		return meter;
	}

	public void setMeter(AssetContext meter) {
		this.meter = meter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getEmcOptions() {
		if (emcOptions != null) {
			return emcOptions.getIntVal();
		}
		return -1;
	}

	public void setEmcOptions(int emcOptions) {
		this.emcOptions = EMC_Options.getAllOptions().get(new Integer(emcOptions));
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getFrequency() {
		if (frequency != null) {
			return frequency.getValue();
		}
		return -1;
	}

	public void setFrequency(int frequency) {
		this.frequency = FacilioFrequency.valueOf(frequency);
	}

	public boolean isAutoGenVmMeter() {
		return isAutoGenVmMeter;
	}

	public void setAutoGenVmMeter(boolean isAutoGenVmMeter) {
		this.isAutoGenVmMeter = isAutoGenVmMeter;
	}

	public enum EMC_Options {
		
		RETROFIT_ISOLATION_KEY_PARM_MESUREMENT(1, "Retrofit Isolation (Key Parameter Measurement)"),
		RETROFIT_ISOLATION_ALL_PARAM_MESUREMENT(2, "Retrofit Isolation: All Parameter Measurement"),
		WHOLE_FACILITY(3, "Whole Facility"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private EMC_Options(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, EMC_Options> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, EMC_Options> initTypeMap() {
			Map<Integer, EMC_Options> typeMap = new HashMap<>();

			for (EMC_Options type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, EMC_Options> getAllOptions() {
			return optionMap;
		}
	}

}
