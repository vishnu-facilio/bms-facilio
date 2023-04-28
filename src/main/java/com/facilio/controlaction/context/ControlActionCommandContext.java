package com.facilio.controlaction.context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupRoutineContext;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;

import lombok.Getter;
import lombok.Setter;

public class ControlActionCommandContext extends V3Context {

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
	String actionName;
	User executedBy;
	ReadingDataMeta rdm;
	FacilioField field;
	
	ControlGroupContext group;
	ControlScheduleContext schedule;
	ControlScheduleExceptionContext exception;
	ControlGroupRoutineContext routine;
	Integer retriedCount;

	public Long getOverrideTimeInMillis() {
		return overrideTimeInMillis;
	}

	public void setOverrideTimeInMillis(Long overrideTimeInMillies) {
		this.overrideTimeInMillis = overrideTimeInMillies;
	}

	Long overrideTimeInMillis = 0L;

	public ControlActionCommandContext() {
		
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime,ControlGroupContext group,Status status,ReadingDataMeta rdm) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
		this.schedule = group.getControlSchedule();
		this.group = group;
		this.executedMode = Control_Action_Execute_Mode.SCHEDULE;
		this.status = status;
		this.executedBy = AccountUtil.getCurrentUser();
		this.rdm = rdm;
		ControlActionMode actinMode = rdm.getControlActionModeEnum() == null ? ControlActionMode.SANDBOX : rdm.getControlActionModeEnum();
		this.controlActionMode = actinMode;
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime,ControlGroupContext group,ControlScheduleExceptionContext exception,ReadingDataMeta rdm) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
		this.schedule = group.getControlSchedule();
		this.group = group;
		this.exception = exception;
		this.executedMode = Control_Action_Execute_Mode.SCHEDULE;
		this.executedBy = AccountUtil.getCurrentUser();
		this.rdm = rdm;
		ControlActionMode actinMode = rdm.getControlActionModeEnum() == null ? ControlActionMode.SANDBOX : rdm.getControlActionModeEnum();
		this.controlActionMode = actinMode;
	}
	
	public ControlActionCommandContext(ResourceContext resource,long fieldId,String value,long executedTime,ControlGroupContext group,ControlGroupRoutineContext routine,Status status,ReadingDataMeta rdm) {
		this.resource = resource;
		this.fieldId = fieldId;
		this.value = value;
		this.executedTime = executedTime;
		this.schedule = group.getControlSchedule();
		this.group = group;
		this.routine = routine;
		this.executedMode = Control_Action_Execute_Mode.SCHEDULE;
		this.status = status;
		this.executedBy = AccountUtil.getCurrentUser();
		this.rdm = rdm;
		ControlActionMode actinMode = rdm.getControlActionModeEnum() == null ? ControlActionMode.SANDBOX : rdm.getControlActionModeEnum();
		this.controlActionMode = actinMode;
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
	
	@Getter @Setter
	private String convertedValue;

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

	public void setActionName(String action) {
		this.actionName = action;
	}

	public String getActionName(){
		return this.actionName;
	}


	ControlActionMode controlActionMode;
	public int getControlActionMode() {
		if(controlActionMode != null) {
			return controlActionMode.getIntVal();
		}
		return -1;
	}

	public void setControlActionMode(int controlActionMode) {
		if(controlActionMode > 0) {
			this.controlActionMode = ControlActionMode.valueOf(controlActionMode);
		}
	}

	public int getExecutedMode() {
		if (executedMode != null) {
			return executedMode.getIntVal();
		}
		return -1;
	}

	public void setExecutedMode(int executedMode) {
		if (executedMode > 0) {
			this.executedMode = Control_Action_Execute_Mode.valueOf(executedMode);
		}
	}

	public int getStatus() {
		if (status != null){
			return status.getIntVal();
		}
		return -1;
	}

	public void setStatus(int status) {
		if (status > 0) {
			this.status = Status.valueOf(status);
		}
	}


	public enum Control_Action_Execute_Mode implements FacilioIntEnum{
		
		MANUAL(1, "Manual"),
		CARD(2, "Card"),
		SCHEDULE(3, "Schedule"),
		ALARM_CONDITION(4, "Alarm Condition"),
		RESERVATION_CONDITION(5, "Reservation Condition"),
		SCRIPT(6, "Script"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		Control_Action_Execute_Mode(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}
		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		public static Control_Action_Execute_Mode valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
		@Override
		public String getValue() {
			return getName();
		}

	}

	public enum Status implements FacilioIntEnum {
		SUCCESS( 1,"Success"),
		SENT(2,"Sent"),
		ERROR( 3,"Error"),
		SCHEDULED(4,"Scheduled"),
		SCHEDULED_WITH_NO_PERMISSION(5,"Scheduled without permission"),
		FAILED(6,"Failed"),
		RETRYING(7,"Retrying");

		private String name;
		private int intVal;

		Status(int intVal ,String name) {

			this.name = name;
			this.intVal=intVal;
		}

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		public String getName() {
			return name;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}

		public int getIntVal() {
			return intVal;
		}
		@Override
		public String getValue() {
			return getName();
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

	public Integer getRetriedCount() {return retriedCount;}

	public void setRetriedCount(Integer retriedCount) {this.retriedCount = retriedCount;}
}
