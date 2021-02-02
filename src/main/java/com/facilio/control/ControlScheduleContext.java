package com.facilio.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.v3.context.V3Context;

public class ControlScheduleContext extends V3Context {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private Long businessHour;
	BusinessHoursContext businessHoursContext;
	List<ControlScheduleExceptionContext> exceptions;
	Mode mode;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getBusinessHour() {
		return businessHour;
	}
	public void setBusinessHour(Long businessHour) {
		this.businessHour = businessHour;
	}
	public BusinessHoursContext getBusinessHoursContext() {
		return businessHoursContext;
	}
	public void setBusinessHoursContext(BusinessHoursContext businessHoursContext) {
		this.businessHoursContext = businessHoursContext;
	}

	public List<ControlScheduleExceptionContext> getExceptions() {
		return exceptions;
	}
	public void setExceptions(List<ControlScheduleExceptionContext> exceptions) {
		this.exceptions = exceptions;
	}
	
	public void addException(ControlScheduleExceptionContext exception) {
		this.exceptions = exceptions == null ? new ArrayList<ControlScheduleExceptionContext>() : exceptions;
		this.exceptions.add(exception);
	}
	
	public enum Mode {
		
		IGNORE_NORMAL_SCHEDULE(1, "Ignore Normal Schedule"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Mode(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}
		
		private static final Map<Integer, Mode> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Mode> initTypeMap() {
			Map<Integer, Mode> typeMap = new HashMap<>();

			for (Mode type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Mode> getAllOptions() {
			return optionMap;
		}
	}

	public Mode getModeEnum() {
		return mode;
	}
	public void setModeEnum(Mode mode) {
		this.mode = mode;
	}
	
	public int getMode() {
		if(mode != null) {
			return mode.getIntVal();
		}
		return -1;
	}
	public void setMode(int mode) {
		this.mode = Mode.getAllOptions().get(mode);
	}
}
