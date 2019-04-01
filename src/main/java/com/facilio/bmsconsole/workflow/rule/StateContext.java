package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class StateContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

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
	
	private StageType stageType;
	public StageType getStageTypeEnum() {
		return stageType;
	}
	public int getStageType() {
		if (stageType != null) {
			stageType.getIntValue();
		}
		return -1;
	}
	public void setStageType(StageType stageType) {
		this.stageType = stageType;
	}
	public void setStageType(int stageType) {
		this.stageType = StageType.valueOf(stageType);
	}
	
	
	public static enum StageType {
		OPEN,
		CLOSED;
		
		public int getIntValue () {
			return ordinal() + 1;
		}
		
		public static StageType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
