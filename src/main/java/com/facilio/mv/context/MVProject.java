package com.facilio.mv.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class MVProject extends ModuleBaseWithCustomFields {

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
	EnergyMeterContext energyMeter;
	User owner;
	List<MVBaseline> baselines;
	List<MVAdjustment> adjustments;
	int saveGoal;
	long reportingPeriodStartTime;
	long reportingPeriodEndTime;
	
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
	
	public EnergyMeterContext getEnergyMeter() {
		return energyMeter;
	}

	public void setEnergyMeter(EnergyMeterContext energyMeter) {
		this.energyMeter = energyMeter;
	}

	public List<MVBaseline> getBaselines() {
		return baselines;
	}

	public void setBaselines(List<MVBaseline> baselines) {
		this.baselines = baselines;
	}

	public List<MVAdjustment> getAdjustments() {
		return adjustments;
	}

	public void setAdjustments(List<MVAdjustment> adjustments) {
		this.adjustments = adjustments;
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

		RETROFIT_ISOLATION_ALL_PARAM_MESUREMENT(1, "Retrofit Isolation: All Parameter Measurement"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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
