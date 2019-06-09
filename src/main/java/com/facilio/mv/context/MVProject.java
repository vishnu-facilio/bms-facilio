package com.facilio.mv.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	int frequency;
	long recordingMeter;
	boolean isAutoGenVmMeter;
	
	List<MVBaseline> baselines;
	List<MVAdjustment> ajustments;
	List<MVAdjustmentVsBaseline> ajustmentVsBaseline;
	
	
	public List<MVBaseline> getBaselines() {
		return baselines;
	}


	public void setBaselines(List<MVBaseline> baselines) {
		this.baselines = baselines;
	}


	public List<MVAdjustment> getAjustments() {
		return ajustments;
	}


	public void setAjustments(List<MVAdjustment> ajustments) {
		this.ajustments = ajustments;
	}


	public List<MVAdjustmentVsBaseline> getAjustmentVsBaseline() {
		return ajustmentVsBaseline;
	}


	public void setAjustmentVsBaseline(List<MVAdjustmentVsBaseline> ajustmentVsBaseline) {
		this.ajustmentVsBaseline = ajustmentVsBaseline;
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
		if(emcOptions != null) {
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
		return frequency;
	}


	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}


	public long getRecordingMeter() {
		return recordingMeter;
	}


	public void setRecordingMeter(long recordingMeter) {
		this.recordingMeter = recordingMeter;
	}


	public boolean isAutoGenVmMeter() {
		return isAutoGenVmMeter;
	}


	public void setAutoGenVmMeter(boolean isAutoGenVmMeter) {
		this.isAutoGenVmMeter = isAutoGenVmMeter;
	}


	public enum EMC_Options {
		
		RETROFIT_ISOLATION_ALL_PARAM_MESUREMENT(1,"Retrofit Isolation: All Parameter Measurement"),
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
			
			for(EMC_Options type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		
		public static Map<Integer, EMC_Options> getAllOptions() {
			return optionMap;
		}
	}
	
}
