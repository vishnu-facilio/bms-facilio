package com.facilio.bmsconsole.workflow.rule;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class StateContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	
	private long parentModuleId = -1;
	public long getParentModuleId() {
		return parentModuleId;
	}
	public void setParentModuleId(long parentModuleId) {
		this.parentModuleId = parentModuleId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String colorCode;
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	
	private StateType stateType;
	public StateType getStateTypeEnum() {
		return stateType;
	}
	public int getStateType() {
		if (stateType != null) {
			return stateType.getIntValue();
		}
		return -1;
	}
	public void setStateType(StateType stateType) {
		this.stateType = stateType;
	}
	public void setStateType(int stageType) {
		this.stateType = StateType.valueOf(stageType);
	}
	
	private Type type;
	public Type getTypeEnum() {
		return type;
	}
	public int getType() {
		if (type != null) {
			return type.getIntValue();
		}
		return -1;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public void setType(int type) {
		this.type = Type.valueOf(type);
	}
	
	
	public static enum StateType {
		OPEN,
		CLOSED;
		
		public int getIntValue () {
			return ordinal() + 1;
		}
		
		public static StateType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	public static enum Type {
		NORMAL;
		
		public int getIntValue () {
			return ordinal() + 1;
		}
		
		public static Type valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
