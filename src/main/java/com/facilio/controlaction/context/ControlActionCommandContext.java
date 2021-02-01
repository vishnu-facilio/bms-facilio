package com.facilio.controlaction.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
import com.facilio.control.ControlGroupRoutineContext;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

public class ControlActionCommandContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String command;
	long executedTime;
	Control_Action_Execute_Mode executedMode;
	Status status;
	ResourceContext resource;
	long fieldId;
	String value;
	User executedBy;
	ReadingDataMeta rdm;
	FacilioField field;
	
	ControlGroupContext group;
	ControlScheduleContext schedule;
	ControlScheduleExceptionContext exception;
	ControlGroupRoutineContext routine;
	
	public ControlActionCommandContext() {
		
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime,ControlScheduleContext schedule) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
		this.schedule = schedule;
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime,ControlScheduleContext schedule,ControlScheduleExceptionContext exception) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
		this.schedule = schedule;
		this.exception = exception;
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime,ControlScheduleContext schedule,ControlGroupRoutineContext routine) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
		this.schedule = schedule;
		this.routine = routine;
	}
	
	public FacilioField getField() {
		return field;
	}

	public void setField(FacilioField field) {
		this.field = field;
	}

	public ReadingDataMeta getRdm() {
		return rdm;
	}

	public void setRdm(ReadingDataMeta rdm) {
		this.rdm = rdm;
	}

	public ResourceContext getResource() {
		return resource;
	}

	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}

	public long getFieldId() {
		return fieldId;
	}

	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public User getExecutedBy() {
		return executedBy;
	}

	public void setExecutedBy(User executedBy) {
		this.executedBy = executedBy;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public long getExecutedTime() {
		return executedTime;
	}

	public void setExecutedTime(long executedTime) {
		this.executedTime = executedTime;
	}

	ControlActionMode controlActionMode;
	public int getControlActionMode() {
		if(controlActionMode != null) {
			return controlActionMode.getValue();
		}
		return -1;
	}

	public void setControlActionMode(int controlActionMode) {
		if(controlActionMode > 0) {
			this.controlActionMode = ControlActionMode.valueOf(controlActionMode);
		}
	}

	public int getExecutedMode() {		
		if(executedMode != null) {
			return executedMode.getIntVal();
		}
		return -1;
	}

	public void setExecutedMode(int executedMode) {
		if(executedMode > 0) {
			this.executedMode = Control_Action_Execute_Mode.getAllOptions().get(executedMode);
		}
	}
	
	public int getStatus() {
		if(status != null) {
			return status.getIntVal();
		}
		return -1;
	}

	public void setStatus(int status) {
		if(status > 0) {
			this.status = Status.getAllOptions().get(status);
		}
	}
	public enum Control_Action_Execute_Mode {
		
		MANUAL(1, "Manual"),
		CARD(2, "Card"),
		SCHEDULE(3, "Schedule"),
		ALARM_CONDITION(4, "Alarm Condition"),
		RESERVATION_CONDITION(5, "Reservation Condition"),
		SCRIPT(6, "SCRIPT"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Control_Action_Execute_Mode(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Control_Action_Execute_Mode> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Control_Action_Execute_Mode> initTypeMap() {
			Map<Integer, Control_Action_Execute_Mode> typeMap = new HashMap<>();

			for (Control_Action_Execute_Mode type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Control_Action_Execute_Mode> getAllOptions() {
			return optionMap;
		}
	}
	
	public enum Status {
		
		SUCCESS(1, "Success"),
		PENDING(2, "Pending"),
		ERROR(3, "Error"),
		SCHEDULED(4, "Scheduled"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Status(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Status> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Status> initTypeMap() {
			Map<Integer, Status> typeMap = new HashMap<>();

			for (Status type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Status> getAllOptions() {
			return optionMap;
		}
	}

	public ControlScheduleContext getSchedule() {
		return schedule;
	}

	public void setSchedule(ControlScheduleContext schedule) {
		this.schedule = schedule;
	}

	public ControlScheduleExceptionContext getException() {
		return exception;
	}

	public void setException(ControlScheduleExceptionContext exception) {
		this.exception = exception;
	}

	public ControlGroupRoutineContext getRoutine() {
		return routine;
	}

	public void setRoutine(ControlGroupRoutineContext routine) {
		this.routine = routine;
	}

	public ControlGroupContext getGroup() {
		return group;
	}

	public void setGroup(ControlGroupContext group) {
		this.group = group;
	}

}
